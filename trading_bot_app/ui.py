import tkinter as tk
from tkinter import ttk, scrolledtext, messagebox, Entry, Label, Button, Frame
import threading
import time
import queue
import json
import logging
import pandas as pd
import numpy as np
from backtesting import Backtest

# Local imports
from .ib_client import TradeApp
from .strategy import EnhancedAlphaWaveStrategy
from .backtesting import EnhancedAlphaWaveBacktestStrategy
from .config import EnhancedStrategyConfig
from .utils import (
    usTechStk,
    marketOrder,
    histData,
    dataDataframe,
    wait_for_data,
    validate_dataframe,
    wait_for_next_valid_order_id,
    update_indicators,
)

logger = logging.getLogger(__name__)

# --- Enhanced Trading Logic ---
def enhanced_trading_logic_thread(app, tickers, capital_per_ticker, ui_queue, strategy_params):
    """Enhanced trading logic with ML capabilities"""
    app.log_message("Enhanced trading logic started with ML capabilities.")

    initial_pos_at_start = {}
    prev_data = {ticker: None for ticker in tickers}
    strategy = EnhancedAlphaWaveStrategy(strategy_params)

    # Initialize ML models if enabled
    if strategy_params.get('use_ml_models', False):
        app.log_message("ML models enabled. Will train on sufficient data.")

    try:
        # Get initial positions
        app.log_message("Requesting initial positions at bot start...")
        app.reqPositions()
        time.sleep(2)

        temp_pos_df = app.pos_df.copy()
        if not temp_pos_df.empty:
            temp_pos_df.drop_duplicates(subset=['Symbol', 'Account'], keep='last', inplace=True)

        initial_pos_at_start = {ticker: 0 for ticker in tickers}
        stk_pos_df = temp_pos_df[temp_pos_df["SecType"]=="STK"]

        for _, row in stk_pos_df.iterrows():
            if row["Symbol"] in initial_pos_at_start:
                initial_pos_at_start[row["Symbol"] ] = int(row["Position"])

        app.log_message(f"Initial positions recorded: {initial_pos_at_start}")

        starttime = time.time()
        timeout = time.time() + 60*60*8  # 8 hours
        trade_count = 0

        while time.time() <= timeout:
            # Check for stop signal
            if not ui_queue.empty():
                msg = ui_queue.get_nowait()
                if msg == "STOP_TRADING":
                    app.log_message("Received stop signal. Exiting trading loop.")
                    break

            app.log_message("Starting enhanced strategy cycle...")
            app.data = {}
            req_map = {}

            # Request data for all tickers
            for i, ticker in enumerate(tickers):
                req_id = i
                req_map[req_id] = ticker
                contract = usTechStk(ticker)
                histData(app, req_id, contract, '30 D', '5 mins')  # More data for ML

            # Process each ticker
            for req_id, ticker in req_map.items():
                if not wait_for_data(app, req_id, timeout=15):
                    app.log_message(f"Timeout waiting for data for {ticker}")
                    continue

                app.log_message(f"----- Processing ticker: {ticker} -----")

                try:
                    # Get and validate data
                    df_hist = dataDataframe(app, req_id)
                    valid, msg = validate_dataframe(df_hist, ticker, min_rows=200)
                    if not valid:
                        app.log_message(msg)
                        continue

                    # Update indicators
                    df_hist = update_indicators(df_hist, prev_data[ticker], strategy)
                    prev_data[ticker] = df_hist

                    # Handle missing values
                    df_hist = df_hist.fillna(method='ffill').fillna(method='bfill')
                    df_hist.dropna(inplace=True)

                    if df_hist.empty or len(df_hist) < 100:
                        app.log_message(f"Insufficient data after processing for {ticker}")
                        continue

                    # Train ML models periodically
                    retrain_freq = strategy_params.get('retrain_frequency', 100)
                    if (strategy_params.get('use_ml_models', False) and
                        trade_count % retrain_freq == 0 and
                        len(df_hist) > 300):

                        app.log_message(f"Retraining ML models for {ticker}...")
                        try:
                            strategy.ml_manager.train_models(df_hist)
                            app.log_message(f"ML models retrained successfully for {ticker}")
                        except Exception as e:
                            app.log_message(f"ML retraining failed for {ticker}: {e}")

                    # Get current positions and orders
                    app.reqPositions()
                    time.sleep(1)
                    current_cycle_pos_df = app.pos_df.copy()
                    if not current_cycle_pos_df.empty:
                        current_cycle_pos_df.drop_duplicates(subset=['Symbol', 'Account'], keep='last', inplace=True)

                    app.reqOpenOrders()
                    time.sleep(1)
                    current_cycle_ord_df = app.order_df.copy()

                    # Generate enhanced signals
                    long_entry, short_entry, long_exit, short_exit, ml_signal, ml_confidence = strategy.generate_signals(df_hist)

                    # Get current market data
                    last_row = df_hist.iloc[-1]
                    last_close = last_row["Close"]
                    atr_val = last_row.get('ATR_val', last_row.get('atr_stop_len', 1))

                    # Position sizing with ML confidence
                    base_risk = capital_per_ticker * 0.01
                    if ml_confidence > 0.8:
                        risk_multiplier = 1.5  # Increase position size for high confidence
                    elif ml_confidence > 0.6:
                        risk_multiplier = 1.0
                    else:
                        risk_multiplier = 0.5  # Reduce size for low confidence

                    adjusted_risk = base_risk * risk_multiplier
                    quantity = int(adjusted_risk / (atr_val * strategy_params['atr_stop_mult'])) if atr_val > 0 else 0
                    quantity = min(quantity, int(capital_per_ticker / last_close)) if last_close > 0 else 0
                    quantity = max(quantity, 1) if quantity > 0 else 0

                    # Get current position
                    current_pos_series = current_cycle_pos_df[current_cycle_pos_df["Symbol"]==ticker]["Position"]
                    current_pos = current_pos_series.sum() if not current_pos_series.empty else 0

                    # Enhanced logging
                    app.log_message(f"{ticker} Enhanced Analysis:")
                    app.log_message(f"  Price: ${last_close:.2f} | Q_AMA: {last_row['q_ama']:.2f}")
                    app.log_message(f"  ML Signal: {ml_signal} | ML Confidence: {ml_confidence:.3f}")
                    app.log_message(f"  Alpha Wave Long: {long_entry} | Short: {short_entry}")
                    app.log_message(f"  Current Position: {current_pos} | Planned Quantity: {quantity}")

                    # Trading logic
                    orders_to_place = []

                    if current_pos == 0:  # No current position
                        if long_entry and quantity > 0:
                            if not wait_for_next_valid_order_id(app):
                                continue
                            order_id = app.nextValidOrderId
                            app.log_message(f"ENHANCED BUY SIGNAL for {ticker}. Qty: {quantity} (ML Conf: {ml_confidence:.3f})")
                            orders_to_place.append((order_id, ticker, marketOrder("BUY", quantity)))

                        elif short_entry and quantity > 0:
                            if not wait_for_next_valid_order_id(app):
                                continue
                            order_id = app.nextValidOrderId
                            app.log_message(f"ENHANCED SELL SIGNAL for {ticker}. Qty: {quantity} (ML Conf: {ml_confidence:.3f})")
                            orders_to_place.append((order_id, ticker, marketOrder("SELL", quantity)))

                    elif current_pos > 0:  # Long position
                        if long_exit:
                            if not wait_for_next_valid_order_id(app):
                                continue
                            order_id = app.nextValidOrderId
                            app.log_message(f"ENHANCED EXIT LONG for {ticker}. Pos: {current_pos}")
                            orders_to_place.append((order_id, ticker, marketOrder("SELL", abs(current_pos))))

                    elif current_pos < 0:  # Short position
                        if short_exit:
                            if not wait_for_next_valid_order_id(app):
                                continue
                            order_id = app.nextValidOrderId
                            app.log_message(f"ENHANCED EXIT SHORT for {ticker}. Pos: {current_pos}")
                            orders_to_place.append((order_id, ticker, marketOrder("BUY", abs(current_pos))))

                    # Place orders
                    for order_id, ticker_symbol, order in orders_to_place:
                        app.log_message(f"Placing {order.action} order for {order.totalQuantity} {ticker_symbol} (ID: {order_id})")
                        app.placeOrder(order_id, usTechStk(ticker_symbol), order)
                        trade_count += 1

                    app.log_message(f"----- Finished processing {ticker} -----")

                except Exception as e:
                    app.log_message(f"Error processing {ticker}: {e}")
                    import traceback
                    app.log_message(traceback.format_exc())

            app.log_message(f"Enhanced strategy cycle completed. Total trades: {trade_count}")

            # Wait for next cycle
            cycle_time = 300  # 5 minutes
            elapsed = (time.time() - starttime) % cycle_time
            time.sleep(max(0, cycle_time - elapsed))

        app.log_message("Enhanced trading timeout reached.")

    except Exception as e:
        app.log_message(f"Fatal error in enhanced trading thread: {e}")
        import traceback
        app.log_message(traceback.format_exc())

# --- Enhanced Trading GUI Class ---
class EnhancedTradingGUI:
    def __init__(self, master):
        self.master = master
        master.title("Enhanced IBKR Trading Bot with ML/DL")
        master.geometry("1000x900")

        self.ui_queue = queue.Queue()
        self.app = None
        self.connection_thread = None
        self.logic_thread = None
        self.is_connected = False
        self.is_running = False
        self.strategy_params_entries = {}

        self.create_gui()
        self.master.after(100, self.process_ui_queue)

    def create_gui(self):
        """Create the enhanced GUI with ML/DL options"""
        self.notebook = ttk.Notebook(self.master)

        # === Main Controls Tab ===
        self.main_tab = Frame(self.notebook)
        self.notebook.add(self.main_tab, text='Main Controls')

        # Connection frame
        conn_frame = ttk.LabelFrame(self.main_tab, text="Connection", padding=(10, 10))
        conn_frame.pack(fill=tk.X, padx=5, pady=5)

        Label(conn_frame, text="Host:").grid(row=0, column=0, sticky=tk.W, padx=2, pady=2)
        self.host_entry = Entry(conn_frame, width=15)
        self.host_entry.insert(0, "127.0.0.1")
        self.host_entry.grid(row=0, column=1, padx=2, pady=2)

        Label(conn_frame, text="Port:").grid(row=0, column=2, sticky=tk.W, padx=2, pady=2)
        self.port_entry = Entry(conn_frame, width=7)
        self.port_entry.insert(0, "7497")
        self.port_entry.grid(row=0, column=3, padx=2, pady=2)

        Label(conn_frame, text="Client ID:").grid(row=0, column=4, sticky=tk.W, padx=2, pady=2)
        self.clientid_entry = Entry(conn_frame, width=5)
        self.clientid_entry.insert(0, "26")
        self.clientid_entry.grid(row=0, column=5, padx=2, pady=2)

        self.connect_button = Button(conn_frame, text="Connect", command=self.toggle_connection)
        self.connect_button.grid(row=0, column=6, padx=5, pady=2)

        self.status_label = Label(conn_frame, text="Status: Disconnected", fg="red")
        self.status_label.grid(row=0, column=7, padx=5, pady=2, sticky=tk.W)

        # Strategy controls frame
        strategy_frame = ttk.LabelFrame(self.main_tab, text="Strategy Controls", padding=(10,10))
        strategy_frame.pack(fill=tk.X, padx=5, pady=5)

        Label(strategy_frame, text="Tickers (comma-sep):").grid(row=0, column=0, sticky=tk.W, padx=2, pady=2)
        self.ticker_entry = Entry(strategy_frame, width=40)
        self.ticker_entry.insert(0, "AAPL,MSFT,GOOG,NVDA,TSLA")
        self.ticker_entry.grid(row=0, column=1, columnspan=3, padx=2, pady=2)

        Label(strategy_frame, text="Capital/Ticker:").grid(row=1, column=0, sticky=tk.W, padx=2, pady=2)
        self.capital_entry = Entry(strategy_frame, width=10)
        self.capital_entry.insert(0, "10000")
        self.capital_entry.grid(row=1, column=1, padx=2, pady=2)

        # Control buttons
        button_frame = Frame(strategy_frame)
        button_frame.grid(row=2, column=0, columnspan=4, pady=10)

        self.start_button = Button(button_frame, text="Start Enhanced Strategy",
                                 command=self.start_strategy, state=tk.DISABLED, bg="green", fg="white")
        self.start_button.pack(side=tk.LEFT, padx=5)

        self.stop_button = Button(button_frame, text="Stop Strategy",
                                command=self.stop_strategy, state=tk.DISABLED, bg="red", fg="white")
        self.stop_button.pack(side=tk.LEFT, padx=5)

        Button(button_frame, text="Save Config", command=self.save_config).pack(side=tk.LEFT, padx=5)
        Button(button_frame, text="Load Config", command=self.load_config).pack(side=tk.LEFT, padx=5)
        Button(button_frame, text="Enhanced Backtest", command=self.backtest_strategy).pack(side=tk.LEFT, padx=5)
        Button(button_frame, text="ML Optimization", command=self.optimize_params).pack(side=tk.LEFT, padx=5)

        # === ML/DL Configuration Tab ===
        self.ml_tab = Frame(self.notebook)
        self.notebook.add(self.ml_tab, text='ML/DL Config')

        # ML Enable frame
        ml_enable_frame = ttk.LabelFrame(self.ml_tab, text="Machine Learning Settings", padding=(10,10))
        ml_enable_frame.pack(fill=tk.X, padx=5, pady=5)

        self.use_ml_var = tk.BooleanVar(value=True)
        ml_checkbox = tk.Checkbutton(ml_enable_frame, text="Enable ML/DL Models",
                                   variable=self.use_ml_var)
        ml_checkbox.grid(row=0, column=0, sticky=tk.W, padx=2, pady=2)

        Label(ml_enable_frame, text="Model Type:").grid(row=0, column=1, sticky=tk.W, padx=2, pady=2)
        self.model_type_var = tk.StringVar(value="ensemble")
        model_combo = ttk.Combobox(ml_enable_frame, textvariable=self.model_type_var,
                                 values=["ensemble", "random_forest", "lightgbm", "xgboost", "lstm"])
        model_combo.grid(row=0, column=2, padx=2, pady=2)

        # ML Parameters
        ml_params = [
            ("Lookback Period:", "lookback_period", "50", 8),
            ("Prediction Horizon:", "prediction_horizon", "5", 8),
            ("Retrain Frequency:", "retrain_frequency", "100", 8),
            ("Confidence Threshold:", "confidence_threshold", "0.6", 8),
        ]

        ml_param_frame = ttk.LabelFrame(self.ml_tab, text="ML Parameters", padding=(10,10))
        ml_param_frame.pack(fill=tk.X, padx=5, pady=5)

        for i, (text, key, default, width) in enumerate(ml_params):
            Label(ml_param_frame, text=text).grid(row=i//2, column=(i%2)*2, sticky=tk.W, padx=2, pady=2)
            entry = Entry(ml_param_frame, width=width)
            entry.insert(0, default)
            entry.grid(row=i//2, column=(i%2)*2+1, padx=2, pady=2)
            self.strategy_params_entries[key] = entry

        self.feature_eng_var = tk.BooleanVar(value=True)
        feature_checkbox = tk.Checkbutton(ml_param_frame, text="Advanced Feature Engineering",
                                        variable=self.feature_eng_var)
        feature_checkbox.grid(row=len(ml_params)//2, column=0, columnspan=2, sticky=tk.W, padx=2, pady=2)

        Label(ml_param_frame, text="Ensemble Voting:").grid(row=len(ml_params)//2, column=2, sticky=tk.W, padx=2, pady=2)
        self.ensemble_voting_var = tk.StringVar(value="soft")
        voting_combo = ttk.Combobox(ml_param_frame, textvariable=self.ensemble_voting_var,
                                  values=["soft", "hard"], width=8)
        voting_combo.grid(row=len(ml_params)//2, column=3, padx=2, pady=2)

        # === Alpha Wave Parameters Tab ===
        self.create_alpha_wave_tab()

        # === Performance Monitoring Tab ===
        self.create_performance_tab()

        # === Positions & Orders Tab ===
        self.create_positions_tab()

        self.notebook.pack(expand=True, fill='both', padx=5, pady=5)

        # Log frame
        log_frame = ttk.LabelFrame(self.master, text="Enhanced Log Output", padding=(10,10))
        log_frame.pack(fill=tk.BOTH, expand=True, padx=5, pady=5)

        self.log_text = scrolledtext.ScrolledText(log_frame, wrap=tk.WORD, height=12)
        self.log_text.pack(fill=tk.BOTH, expand=True, padx=5, pady=5)
        self.log_text.config(state=tk.DISABLED)

    def create_alpha_wave_tab(self):
        """Create Alpha Wave parameters tab"""
        self.aw_params_tab = Frame(self.notebook)
        self.notebook.add(self.aw_params_tab, text='Alpha Wave Params')

        # Scrollable frame setup
        aw_canvas = tk.Canvas(self.aw_params_tab)
        aw_scrollbar = ttk.Scrollbar(self.aw_params_tab, orient="vertical", command=aw_canvas.yview)
        aw_scrollable_frame = ttk.Frame(aw_canvas)

        aw_scrollable_frame.bind("<Configure>",
                               lambda e: aw_canvas.configure(scrollregion=aw_canvas.bbox("all")))
        aw_canvas.create_window((0, 0), window=aw_scrollable_frame, anchor="nw")
        aw_canvas.configure(yscrollcommand=aw_scrollbar.set)

        # Parameter groups
        param_groups = [
            ("Quantum AMA (Baseline)", [
                ("Q_AMA ADX Len:", "q_ama_adx_len", "2"),
                ("Q_AMA Weight:", "q_ama_weight", "10.0"),
                ("Q_AMA MA Len:", "q_ama_ma_len", "6")
            ]),
            ("WavePulse (Confirm #1)", [
                ("WP Smooth:", "wp_smooth", "21"),
                ("WP Constant:", "wp_const", "0.4")
            ]),
            ("Quantum Channel (Confirm #2)", [
                ("QC Poles:", "qc_poles", "4"),
                ("QC Period:", "qc_period", "144"),
                ("QC Multiplier:", "qc_mult", "1.414")
            ]),
            ("Momentum Density (Volatility)", [
                ("MD MA Len:", "md_ma_len", "100"),
                ("MD Calc Len:", "md_calc_len", "60"),
                ("MD Smooth Len:", "md_smooth_len", "3"),
                ("MD Threshold:", "md_threshold", "90")
            ]),
            ("ATR Trailing Stop", [
                ("ATR Stop Len:", "atr_stop_len", "22"),
                ("ATR Stop Mult:", "atr_stop_mult", "3.0")
            ]),
            ("T3 Moving Average", [
                ("T3 Fast Len:", "t3_fast_len", "12"),
                ("T3 Slow Len:", "t3_slow_len", "25"),
                ("T3 Factor:", "t3_factor", "0.7")
            ])
        ]

        for group_name, params in param_groups:
            frame = ttk.LabelFrame(aw_scrollable_frame, text=group_name, padding=(10,10))
            frame.pack(fill=tk.X, padx=5, pady=5)

            for i, (text, key, default) in enumerate(params):
                Label(frame, text=text).grid(row=i, column=0, sticky=tk.W, padx=2, pady=2)
                entry = Entry(frame, width=8)
                entry.insert(0, default)
                entry.grid(row=i, column=1, padx=2, pady=2)
                self.strategy_params_entries[key] = entry

        aw_canvas.pack(side="left", fill="both", expand=True)
        aw_scrollbar.pack(side="right", fill="y")

    def create_performance_tab(self):
        """Create performance monitoring tab"""
        self.perf_tab = Frame(self.notebook)
        self.notebook.add(self.perf_tab, text='Performance')

        # Performance metrics frame
        metrics_frame = ttk.LabelFrame(self.perf_tab, text="Real-time Metrics", padding=(10,10))
        metrics_frame.pack(fill=tk.X, padx=5, pady=5)

        # Metrics display
        self.metrics_text = scrolledtext.ScrolledText(metrics_frame, wrap=tk.WORD, height=8)
        self.metrics_text.pack(fill=tk.BOTH, expand=True, padx=5, pady=5)

        # Chart frame
        chart_frame = ttk.LabelFrame(self.perf_tab, text="Equity Curve", padding=(10,10))
        chart_frame.pack(fill=tk.BOTH, expand=True, padx=5, pady=5)

        # Placeholder for chart
        chart_label = Label(chart_frame, text="Equity curve will be displayed here during live trading")
        chart_label.pack(expand=True)

    def create_positions_tab(self):
        """Create positions and orders tab"""
        self.positions_tab = Frame(self.notebook)
        self.notebook.add(self.positions_tab, text='Positions & Orders')

        # Positions frame
        pos_frame = ttk.LabelFrame(self.positions_tab, text="Current Positions", padding=(10,10))
        pos_frame.pack(fill=tk.BOTH, expand=True, padx=5, pady=5)

        self.pos_tree = ttk.Treeview(pos_frame,
                                   columns=['Account', 'Symbol', 'SecType', 'Currency', 'Position', 'AvgCost'],
                                   show='headings')
        for col in self.pos_tree['columns']:
            self.pos_tree.heading(col, text=col)
            self.pos_tree.column(col, width=100)
        self.pos_tree.pack(fill=tk.BOTH, expand=True)

        # Orders frame
        ord_frame = ttk.LabelFrame(self.positions_tab, text="Open Orders", padding=(10,10))
        ord_frame.pack(fill=tk.BOTH, expand=True, padx=5, pady=5)

        self.ord_tree = ttk.Treeview(ord_frame,
                                   columns=['OrderId', 'Symbol', 'Action', 'TotalQty', 'OrderType', 'Status'],
                                   show='headings')
        for col in self.ord_tree['columns']:
            self.ord_tree.heading(col, text=col)
            self.ord_tree.column(col, width=100)
        self.ord_tree.pack(fill=tk.BOTH, expand=True)

    def get_strategy_params(self):
        """Collect all strategy parameters"""
        params = {}

        # Alpha Wave parameters
        for key, entry in self.strategy_params_entries.items():
            try:
                if key in ['lookback_period', 'prediction_horizon', 'retrain_frequency']:
                    params[key] = int(entry.get())
                elif key in ['confidence_threshold']:
                    params[key] = float(entry.get())
                elif key in ['q_ama_adx_len', 'q_ama_ma_len', 'wp_smooth', 'qc_poles', 'qc_period',
                           'md_ma_len', 'md_calc_len', 'md_smooth_len', 'md_threshold', 'atr_stop_len',
                           't3_fast_len', 't3_slow_len']:
                    params[key] = int(entry.get())
                else:
                    params[key] = float(entry.get())
            except ValueError:
                raise ValueError(f"Invalid value for parameter {key}")

        # ML/DL parameters
        params['use_ml_models'] = self.use_ml_var.get()
        params['model_type'] = self.model_type_var.get()
        params['feature_engineering'] = self.feature_eng_var.get()
        params['ensemble_voting'] = self.ensemble_voting_var.get()

        return params

    def start_strategy(self):
        """Start the enhanced trading strategy"""
        if not self.is_connected or not self.app:
            messagebox.showwarning("Warning", "Not connected to IBKR.")
            return

        if self.is_running:
            messagebox.showwarning("Warning", "Strategy is already running.")
            return

        # Validate inputs
        tickers_str = self.ticker_entry.get()
        capital_str = self.capital_entry.get()

        if not tickers_str:
            messagebox.showerror("Error", "Please enter at least one ticker.")
            return

        try:
            capital = int(capital_str)
            if capital <= 0:
                raise ValueError()
        except ValueError:
            messagebox.showerror("Error", "Capital must be a positive integer.")
            return

        tickers = [t.strip().upper() for t in tickers_str.split(',') if t.strip()]

        try:
            strategy_params = self.get_strategy_params()
        except ValueError as e:
            messagebox.showerror("Parameter Error", str(e))
            return

        # Start trading
        self.log_message_ui(f"Starting Enhanced Strategy with ML/DL capabilities")
        self.log_message_ui(f"Tickers: {tickers}")
        self.log_message_ui(f"Capital per ticker: ${capital:,}")
        self.log_message_ui(f"ML Models Enabled: {strategy_params['use_ml_models']}")

        self.is_running = True
        self.start_button.config(state=tk.DISABLED)
        self.stop_button.config(state=tk.NORMAL)

        # Start trading thread
        self.logic_thread = threading.Thread(
            target=enhanced_trading_logic_thread,
            args=(self.app, tickers, capital, self.ui_queue, strategy_params),
            daemon=True
        )
        self.logic_thread.start()

    def stop_strategy(self):
        """Stop the trading strategy"""
        if not self.is_running:
            self.log_message_ui("Stop command received, but strategy was not running.")
            return

        self.log_message_ui("Stopping enhanced trading strategy...")
        self.is_running = False

        if self.logic_thread:
            self.ui_queue.put("STOP_TRADING")

        if self.is_connected:
            self.start_button.config(state=tk.NORMAL)

        self.stop_button.config(state=tk.DISABLED)
        self.log_message_ui("Enhanced strategy stopped successfully.")

    def toggle_connection(self):
        """Toggle IBKR connection"""
        if not self.is_connected:
            host = self.host_entry.get()
            try:
                port = int(self.port_entry.get())
                client_id = int(self.clientid_entry.get())
            except ValueError:
                messagebox.showerror("Error", "Port and Client ID must be integers.")
                return

            self.log_message_ui(f"Connecting to IBKR at {host}:{port}...")

            # Import the original TradeApp class here
            self.app = TradeApp(self.ui_queue)
            self.app.connect(host, port, client_id)

            self.connection_thread = threading.Thread(target=self.app.run, daemon=True)
            self.connection_thread.start()

            # Wait for connection
            timeout = time.time() + 10
            while not self.app.isConnected() and time.time() < timeout:
                time.sleep(0.5)

            if self.app.isConnected():
                self.is_connected = True
                self.status_label.config(text="Status: Connected", fg="green")
                self.connect_button.config(text="Disconnect")
                self.start_button.config(state=tk.NORMAL)
                self.log_message_ui("Connected to IBKR successfully.")
                self.app.reqIds(-1)
            else:
                self.log_message_ui("Connection failed. Check TWS/Gateway.")
        else:
            if self.is_running:
                self.stop_strategy()

            if self.app and self.app.isConnected():
                self.app.disconnect()

            self.is_connected = False
            self.status_label.config(text="Status: Disconnected", fg="red")
            self.connect_button.config(text="Connect")
            self.start_button.config(state=tk.DISABLED)
            self.stop_button.config(state=tk.DISABLED)
            self.log_message_ui("Disconnected from IBKR.")

    def backtest_strategy(self):
        """Run enhanced backtesting with ML models"""
        if not self.is_connected or not self.app:
            messagebox.showwarning("Warning", "Not connected to IBKR.")
            return

        tickers = [t.strip().upper() for t in self.ticker_entry.get().split(',') if t.strip()]
        if not tickers:
            messagebox.showerror("Error", "No valid tickers provided.")
            return

        try:
            strategy_params = self.get_strategy_params()
            capital = int(self.capital_entry.get())
        except ValueError as e:
            messagebox.showerror("Parameter Error", str(e))
            return

        self.log_message_ui("Starting enhanced backtesting with ML models...")

        backtest_results = {}

        for ticker in tickers:
            try:
                self.log_message_ui(f"Backtesting {ticker}...")

                # Get historical data
                contract = usTechStk(ticker)
                self.app.reqHistoricalData(
                    reqId=999,
                    contract=contract,
                    endDateTime='',
                    durationStr='2 Y',  # More data for ML
                    barSizeSetting='1 hour',
                    whatToShow='ADJUSTED_LAST',
                    useRTH=1,
                    formatDate=1,
                    keepUpToDate=0,
                    chartOptions=[]
                )

                if not wait_for_data(self.app, 999, timeout=15):
                    self.log_message_ui(f"Timeout retrieving data for {ticker}")
                    continue

                df = dataDataframe(self.app, 999)
                valid, msg = validate_dataframe(df, ticker, min_rows=500)
                if not valid:
                    self.log_message_ui(msg)
                    continue

                # Run backtest
                bt = Backtest(
                    df,
                    EnhancedAlphaWaveBacktestStrategy,
                    cash=capital,
                    commission=0.002,
                    exclusive_orders=True
                )

                stats = bt.run(**strategy_params)

                # Store results
                backtest_results[ticker] = {
                    'Return [%]': stats['Return [%]'],
                    'Equity Final [$]': stats['Equity Final [$]'],
                    'Sharpe Ratio': stats.get('Sharpe Ratio', 0),
                    '# Trades': stats.get('# Trades', 0),
                    'Win Rate [%]': stats.get('Win Rate [%]', 0),
                    'Max. Drawdown [%]': stats.get('Max. Drawdown [%]', 0)
                }

                self.log_message_ui(f"{ticker} Backtest Results:")
                self.log_message_ui(f"  Return: {stats['Return [%]']:.2f}%")
                self.log_message_ui(f"  Final Equity: ${stats['Equity Final [$]']:,.2f}")
                self.log_message_ui(f"  Sharpe Ratio: {stats.get('Sharpe Ratio', 0):.2f}")
                self.log_message_ui(f"  Trades: {stats.get('# Trades', 0)}")
                self.log_message_ui(f"  Win Rate: {stats.get('Win Rate [%]', 0):.2f}%")

            except Exception as e:
                self.log_message_ui(f"Backtest failed for {ticker}: {e}")
                import traceback
                self.log_message_ui(traceback.format_exc())

        # Summary
        if backtest_results:
            avg_return = np.mean([r['Return [%]'] for r in backtest_results.values()])
            total_trades = sum([r['# Trades'] for r in backtest_results.values()])
            avg_sharpe = np.mean([r['Sharpe Ratio'] for r in backtest_results.values()])

            self.log_message_ui("=== BACKTEST SUMMARY ===")
            self.log_message_ui(f"Average Return: {avg_return:.2f}%")
            self.log_message_ui(f"Total Trades: {total_trades}")
            self.log_message_ui(f"Average Sharpe: {avg_sharpe:.2f}")

    def optimize_params(self):
        """Run ML-based parameter optimization"""
        if not self.is_connected or not self.app:
            messagebox.showwarning("Warning", "Not connected to IBKR.")
            return

        tickers = [t.strip().upper() for t in self.ticker_entry.get().split(',') if t.strip()]
        if not tickers:
            messagebox.showerror("Error", "No valid tickers provided.")
            return

        self.log_message_ui("Starting ML-based parameter optimization...")

        # Parameter grid for optimization
        param_grid = {
            'q_ama_adx_len': [2, 3, 4, 5],
            'q_ama_weight': [8.0, 10.0, 12.0, 15.0],
            'q_ama_ma_len': [5, 6, 7, 8],
            'wp_smooth': [18, 21, 24, 27],
            'md_threshold': [85, 90, 95],
            'atr_stop_mult': [2.5, 3.0, 3.5],
            'confidence_threshold': [0.55, 0.6, 0.65, 0.7]
        }

        from itertools import product

        best_params = None
        best_score = -float('inf')
        tested_combinations = 0
        max_combinations = 50  # Limit to prevent excessive computation

        for ticker in tickers[:2]:  # Optimize on first 2 tickers
            try:
                self.log_message_ui(f"Optimizing on {ticker}...")

                # Get data
                contract = usTechStk(ticker)
                self.app.reqHistoricalData(
                    reqId=999, contract=contract, endDateTime='',
                    durationStr='1 Y', barSizeSetting='1 hour',
                    whatToShow='ADJUSTED_LAST', useRTH=1,
                    formatDate=1, keepUpToDate=0, chartOptions=[]
                )

                if not wait_for_data(self.app, 999, timeout=10):
                    continue

                df = dataDataframe(self.app, 999)
                valid, msg = validate_dataframe(df, ticker, min_rows=300)
                if not valid:
                    continue

                # Sample parameter combinations
                param_combinations = list(product(*param_grid.values()))
                np.random.shuffle(param_combinations)

                for params in param_combinations[:max_combinations]:
                    try:
                        param_dict = dict(zip(param_grid.keys(), params))

                        # Add default values for missing parameters
                        base_params = self.get_strategy_params()
                        base_params.update(param_dict)

                        # Run backtest
                        bt = Backtest(
                            df,
                            EnhancedAlphaWaveBacktestStrategy,
                            cash=int(self.capital_entry.get()),
                            commission=0.002
                        )

                        stats = bt.run(**base_params)

                        # Score function (combine return and Sharpe ratio)
                        score = stats['Return [%]'] + (stats.get('Sharpe Ratio', 0) * 10)

                        if score > best_score:
                            best_score = score
                            best_params = param_dict

                        tested_combinations += 1

                        if tested_combinations % 10 == 0:
                            self.log_message_ui(f"Tested {tested_combinations} combinations...")

                    except Exception as e:
                        continue

                break  # Use first successful ticker

            except Exception as e:
                self.log_message_ui(f"Optimization failed for {ticker}: {e}")
                continue

        # Apply best parameters
        if best_params:
            self.log_message_ui(f"=== OPTIMIZATION RESULTS ===")
            self.log_message_ui(f"Best Score: {best_score:.2f}")
            self.log_message_ui("Best Parameters:")

            for key, value in best_params.items():
                if key in self.strategy_params_entries:
                    self.strategy_params_entries[key].delete(0, tk.END)
                    self.strategy_params_entries[key].insert(0, str(value))
                    self.log_message_ui(f"  {key}: {value}")

            self.log_message_ui("Parameters updated in GUI.")
        else:
            self.log_message_ui("Optimization failed to find better parameters.")

    def save_config(self):
        """Save configuration to file"""
        try:
            config = {
                'connection': {
                    'host': self.host_entry.get(),
                    'port': self.port_entry.get(),
                    'client_id': self.clientid_entry.get()
                },
                'trading': {
                    'tickers': self.ticker_entry.get(),
                    'capital': self.capital_entry.get()
                },
                'ml_settings': {
                    'use_ml_models': self.use_ml_var.get(),
                    'model_type': self.model_type_var.get(),
                    'feature_engineering': self.feature_eng_var.get(),
                    'ensemble_voting': self.ensemble_voting_var.get()
                },
                'strategy_params': {key: entry.get() for key, entry in self.strategy_params_entries.items()}
            }

            with open('enhanced_trading_config.json', 'w') as f:
                json.dump(config, f, indent=4)

            self.log_message_ui("Enhanced configuration saved successfully.")

        except Exception as e:
            self.log_message_ui(f"Failed to save configuration: {e}")

    def load_config(self):
        """Load configuration from file"""
        try:
            with open('enhanced_trading_config.json', 'r') as f:
                config = json.load(f)

            # Load connection settings
            if 'connection' in config:
                self.host_entry.delete(0, tk.END)
                self.host_entry.insert(0, config['connection']['host'])
                self.port_entry.delete(0, tk.END)
                self.port_entry.insert(0, config['connection']['port'])
                self.clientid_entry.delete(0, tk.END)
                self.clientid_entry.insert(0, config['connection']['client_id'])

            # Load trading settings
            if 'trading' in config:
                self.ticker_entry.delete(0, tk.END)
                self.ticker_entry.insert(0, config['trading']['tickers'])
                self.capital_entry.delete(0, tk.END)
                self.capital_entry.insert(0, config['trading']['capital'])

            # Load ML settings
            if 'ml_settings' in config:
                self.use_ml_var.set(config['ml_settings']['use_ml_models'])
                self.model_type_var.set(config['ml_settings']['model_type'])
                self.feature_eng_var.set(config['ml_settings']['feature_engineering'])
                self.ensemble_voting_var.set(config['ml_settings']['ensemble_voting'])

            # Load strategy parameters
            if 'strategy_params' in config:
                for key, value in config['strategy_params'].items():
                    if key in self.strategy_params_entries:
                        self.strategy_params_entries[key].delete(0, tk.END)
                        self.strategy_params_entries[key].insert(0, value)

            self.log_message_ui("Enhanced configuration loaded successfully.")

        except Exception as e:
            self.log_message_ui(f"Failed to load configuration: {e}")

    def log_message_ui(self, message):
        """Add message to UI log"""
        self.log_text.config(state=tk.NORMAL)
        timestamp = time.strftime('%Y-%m-%d %H:%M:%S')
        self.log_text.insert(tk.END, f"{timestamp} - {message}\n")
        self.log_text.see(tk.END)
        self.log_text.config(state=tk.DISABLED)

    def process_ui_queue(self):
        """Process messages from trading thread"""
        try:
            while True:
                msg = self.ui_queue.get_nowait()
                self.log_message_ui(msg)
        except queue.Empty:
            pass

        # Update positions and orders
        self.update_positions_orders()

        # Schedule next update
        self.master.after(100, self.process_ui_queue)

    def update_positions_orders(self):
        """Update positions and orders display"""
        if self.app:
            # Update positions
            for item in self.pos_tree.get_children():
                self.pos_tree.delete(item)

            for _, row in self.app.pos_df.iterrows():
                values = [row.get(col, '') for col in ['Account', 'Symbol', 'SecType', 'Currency', 'Position', 'Avg cost']]
                self.pos_tree.insert('', tk.END, values=values)

            # Update orders
            for item in self.ord_tree.get_children():
                self.ord_tree.delete(item)

            for _, row in self.app.order_df.iterrows():
                values = [row.get(col, '') for col in ['OrderId', 'Symbol', 'Action', 'TotalQty', 'OrderType', 'Status']]
                self.ord_tree.insert('', tk.END, values=values)
