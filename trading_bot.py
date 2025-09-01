# -*- coding: utf-8 -*-
"""
Enhanced IBKR Trading Bot with Machine Learning Models
Integrates RandomForest, LightGBM, XGBoost, and Neural Networks

@author: Enhanced by AI Assistant
"""

import tkinter as tk
from tkinter import ttk, scrolledtext, messagebox, Entry, Label, Button, Frame
import threading
import time
import queue
import json
import logging
import pandas as pd
import numpy as np
from ibapi.client import EClient
from ibapi.wrapper import EWrapper
from ibapi.contract import Contract
from ibapi.order import Order
from backtesting import Backtest, Strategy

# ML/DL Imports
from sklearn.ensemble import RandomForestClassifier, RandomForestRegressor
from sklearn.model_selection import train_test_split, TimeSeriesSplit, GridSearchCV
from sklearn.preprocessing import StandardScaler, RobustScaler
from sklearn.metrics import classification_report, accuracy_score, precision_score, recall_score
import lightgbm as lgb
import xgboost as xgb
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import LSTM, Dense, Dropout, BatchNormalization
from tensorflow.keras.optimizers import Adam
from tensorflow.keras.callbacks import EarlyStopping
import warnings
warnings.filterwarnings('ignore')

# Setup enhanced logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('enhanced_trading_bot.log'),
        logging.StreamHandler()
    ]
)
logger = logging.getLogger()

# === Missing Helper Functions ===
def usTechStk(symbol):
    contract = Contract()
    contract.symbol = symbol
    contract.secType = "STK"
    contract.exchange = "SMART"
    contract.currency = "USD"
    return contract

def marketOrder(action, quantity):
    order = Order()
    order.action = action
    order.orderType = "MKT"
    order.totalQuantity = quantity
    return order

def histData(app, req_id, contract, duration, bar_size):
    app.reqHistoricalData(req_id, contract, "", duration, bar_size, "TRADES", 1, 1, False, [])

def dataDataframe(app, req_id):
    if req_id in app.data:
        return app.data[req_id]
    return pd.DataFrame()

def wait_for_data(app, req_id, timeout=10):
    start = time.time()
    while time.time() - start < timeout:
        if req_id in app.data and not app.data[req_id].empty:
            return True
        time.sleep(0.5)
    return False

def validate_dataframe(df, ticker, min_rows=100):
    if df.empty:
        return False, f"No data for {ticker}"
    if len(df) < min_rows:
        return False, f"Insufficient data for {ticker}: {len(df)} rows"
    return True, ""

def wait_for_next_valid_order_id(app, timeout=5):
    start = time.time()
    while time.time() - start < timeout:
        if app.nextValidOrderId is not None:
            return True
        time.sleep(0.1)
    return False

def update_indicators(df, prev_df, strategy):
    if prev_df is not None and not prev_df.empty:
        df = pd.concat([prev_df.iloc[-100:], df], ignore_index=True)
    df = strategy.compute_indicators(df)
    return df

# === Indicator Functions (Completed and Enhanced) ===
def atr(df, length=14):
    """
    Calculate Average True Range (ATR).
    """
    true_range = pd.DataFrame(index=df.index)
    true_range['hl'] = df['High'] - df['Low']
    true_range['hc'] = abs(df['High'] - df['Close'].shift())
    true_range['lc'] = abs(df['Low'] - df['Close'].shift())
    tr = true_range.max(axis=1)
    atr_val = tr.rolling(length).mean()
    return pd.DataFrame({'ATR': atr_val})

def t3_moving_average(series, length, factor=0.7):
    """
    Calculate Tilson T3 Moving Average.
    """
    e1 = series.ewm(span=length, adjust=False).mean()
    e2 = e1.ewm(span=length, adjust=False).mean()
    e3 = e2.ewm(span=length, adjust=False).mean()
    e4 = e3.ewm(span=length, adjust=False).mean()
    e5 = e4.ewm(span=length, adjust=False).mean()
    e6 = e5.ewm(span=length, adjust=False).mean()
    c1 = -factor**3
    c2 = 3 * factor**2 + 3 * factor**3
    c3 = -6 * factor**2 - 3 * factor - 3 * factor**3
    c4 = factor**3 + 3 * factor**2 + 3 * factor + 1
    t3 = c1 * e6 + c2 * e5 + c3 * e4 + c4 * e3
    return t3

def an_filter(series, alpha, poles):
    """
    Approximate anti-aliasing filter using chained EMAs for multi-pole low-pass filtering.
    """
    filtered = series.copy()
    for _ in range(poles):
        filtered = alpha * series + (1 - alpha) * filtered.shift(1).fillna(method='bfill')
        series = filtered  # Chain the filters
    return filtered.fillna(method='bfill')

def quantum_adaptive_ma(df, adx_length=2, weight=10.0, ma_length=6):
    df_copy = df.copy()
    df_copy['h'] = df_copy['High']
    df_copy['l'] = df_copy['Low']
    df_copy['c'] = df_copy['Close']
    df_copy['h1'] = df_copy['h'].shift(1)
    df_copy['l1'] = df_copy['l'].shift(1)
    df_copy['bulls1'] = 0.5 * (abs(df_copy['h'] - df_copy['h1']) + (df_copy['h'] - df_copy['h1']))
    df_copy['bears1'] = 0.5 * (abs(df_copy['l1'] - df_copy['l']) + (df_copy['l1'] - df_copy['l']))
    df_copy['bears'] = np.where(df_copy['bulls1'] > df_copy['bears1'], 0,
                               np.where(df_copy['bulls1'] == df_copy['bears1'], 0, df_copy['bears1']))
    df_copy['bulls'] = np.where(df_copy['bulls1'] < df_copy['bears1'], 0,
                               np.where(df_copy['bulls1'] == df_copy['bears1'], 0, df_copy['bulls1']))
    alpha_pb = 1 / (weight + 1)
    df_copy['power_bulls'] = df_copy['bulls'].ewm(alpha=alpha_pb, adjust=False, ignore_na=True).mean()
    df_copy['power_bears'] = df_copy['bears'].ewm(alpha=alpha_pb, adjust=False, ignore_na=True).mean()
    df_copy['true_range_temp'] = abs(df_copy['h'] - df_copy['l'])
    df_copy['str_range'] = df_copy['true_range_temp'].ewm(alpha=alpha_pb, adjust=False, ignore_na=True).mean()
    df_copy['pos_di'] = np.where(df_copy['str_range'] > 0, df_copy['power_bulls'] / df_copy['str_range'], 0)
    df_copy['neg_di'] = np.where(df_copy['str_range'] > 0, df_copy['power_bears'] / df_copy['str_range'], 0)
    di_sum = df_copy['pos_di'] + df_copy['neg_di']
    df_copy['di_diff_sum_ratio'] = np.where(di_sum > 0, abs(df_copy['pos_di'] - df_copy['neg_di']) / di_sum, 0)
    df_copy['diag_x'] = df_copy['di_diff_sum_ratio'].ewm(alpha=alpha_pb, adjust=False, ignore_na=True).mean()
    df_copy['adx_val'] = df_copy['diag_x']
    df_copy['adx_low'] = df_copy['adx_val'].rolling(window=adx_length, min_periods=1).min()
    df_copy['adx_high'] = df_copy['adx_val'].rolling(window=adx_length, min_periods=1).max()
    adx_min_series = df_copy['adx_low']
    adx_max_series = df_copy['adx_high']
    adx_diff_series = adx_max_series - adx_min_series
    df_copy['adx_constant'] = np.where(adx_diff_series > 0, (df_copy['adx_val'] - adx_min_series) / adx_diff_series, 0)
    var_ma_series = pd.Series(index=df_copy.index, dtype=float)
    if not df_copy.empty:
        first_close = df_copy['c'].bfill().iloc[0] if not df_copy['c'].bfill().empty else 0
        var_ma_series.iloc[0] = first_close
        for i in range(1, len(df_copy)):
            adx_const = df_copy['adx_constant'].iloc[i]
            prev_var_ma = var_ma_series.iloc[i-1]
            close_val = df_copy['c'].iloc[i]
            if pd.isna(adx_const): adx_const = 0
            if pd.isna(prev_var_ma): prev_var_ma = close_val if pd.notna(close_val) else first_close
            if pd.isna(close_val): close_val = prev_var_ma
            var_ma_series.iloc[i] = ((2.0 - adx_const) * prev_var_ma + adx_const * close_val) / 2.0
    df_copy['var_ma'] = var_ma_series
    df_copy['q_ama'] = df_copy['var_ma'].rolling(window=ma_length, min_periods=1).mean()
    return df_copy['q_ama']

def wave_pulse(df, smooth_period=21, constant_factor=0.4):
    df_copy = df.copy()
    close_prices = df_copy['Close']
    di_val = (smooth_period - 1.0) / 2.0 + 1.0
    c1_val = 2.0 / (di_val + 1.0)
    c3_val = 3.0 * (constant_factor**2 + constant_factor**3)
    c4_val = -3.0 * (2.0 * constant_factor**2 + constant_factor + constant_factor**3)
    c5_val = 3.0 * constant_factor + 1.0 + constant_factor**3 + 3.0 * constant_factor**2
    i1_val = close_prices.ewm(alpha=c1_val, adjust=False, ignore_na=True).mean()
    i2_val = i1_val.ewm(alpha=c1_val, adjust=False, ignore_na=True).mean()
    i3_val = i2_val.ewm(alpha=c1_val, adjust=False, ignore_na=True).mean()
    i4_val = i3_val.ewm(alpha=c1_val, adjust=False, ignore_na=True).mean()
    i5_val = i4_val.ewm(alpha=c1_val, adjust=False, ignore_na=True).mean()
    i6_val = i5_val.ewm(alpha=c1_val, adjust=False, ignore_na=True).mean()
    wave_val = (-constant_factor**3 * i6_val +
                c3_val * i5_val +
                c4_val * i4_val +
                c5_val * i3_val)
    return wave_val

def quantum_channel(df, source_col='hlc3', poles=4, period=144, multiplier=1.414):
    df_copy = df.copy()
    if source_col == 'hlc3':
        if 'hlc3' not in df_copy.columns:
            df_copy[source_col] = (df_copy['High'] + df_copy['Low'] + df_copy['Close']) / 3.0
    elif source_col not in df_copy.columns:
        raise ValueError(f"Source column '{source_col}' not found in DataFrame for Quantum Channel.")
    src = df_copy[source_col].fillna(method='bfill').fillna(method='ffill')
    quantum_beta = (1.0 - np.cos(2.0 * np.pi / period)) / (1.414**(2.0 / poles) - 1.0) if poles > 0 and period > 0 and (1.414**(2.0 / poles) - 1.0) != 0 else 1.0
    sqrt_val = quantum_beta**2 + 2.0 * quantum_beta
    quantum_alpha = -quantum_beta + np.sqrt(sqrt_val) if sqrt_val >= 0 else 0.01
    df_copy['tr0'] = abs(df_copy['High'] - df_copy['Low'])
    df_copy['tr1'] = abs(df_copy['High'] - df_copy['Close'].shift(1))
    df_copy['tr2'] = abs(df_copy['Low'] - df_copy['Close'].shift(1))
    df_copy['tr_qc'] = df_copy[['tr0', 'tr1', 'tr2']].max(axis=1)
    df_copy['tr_qc'] = df_copy['tr_qc'].fillna(method='bfill').fillna(method='ffill')
    quantum_filter_n = an_filter(src, quantum_alpha, poles)
    quantum_tr_n = an_filter(df_copy['tr_qc'], quantum_alpha, poles)
    df_copy['q_filter'] = quantum_filter_n
    df_copy['q_upper'] = quantum_filter_n + quantum_tr_n * multiplier
    df_copy['q_lower'] = quantum_filter_n - quantum_tr_n * multiplier
    return df_copy['q_filter'], df_copy['q_upper'], df_copy['q_lower']

def momentum_density(df, ma_length=100, calc_length=60, smooth_length=3):
    df_copy = df.copy()
    close_prices = df_copy['Close']
    bound = close_prices.rolling(window=ma_length, min_periods=1).mean() - 0.2 * close_prices.rolling(window=ma_length, min_periods=1).std()
    above_bound = (close_prices > bound).astype(int)
    sum_above = above_bound.rolling(window=calc_length, min_periods=1).sum()
    density_raw = sum_above * 100.0 / calc_length
    density = density_raw.ewm(span=smooth_length, adjust=False, ignore_na=True).mean()
    return density

def atr_trailing_stop_alpha_wave(df, atr_function_ref, atr_length=22, atr_mult=3.0, use_wicks=True):
    df_copy = df.copy()
    atr_output_df = atr_function_ref(df_copy, atr_length)
    if 'ATR' not in atr_output_df.columns:
        raise ValueError("The provided 'atr_function_ref' did not return a DataFrame with an 'ATR' column.")
    df_copy['ATR_val'] = atr_output_df['ATR']
    stop_atr_val = atr_mult * df_copy['ATR_val']
    src_price = (df_copy['High'] + df_copy['Low']) / 2.0
    stop_high_price = df_copy['High'] if use_wicks else df_copy['Close']
    stop_low_price = df_copy['Low'] if use_wicks else df_copy['Close']
    df_copy['calc_long_stop'] = src_price - stop_atr_val
    df_copy['calc_short_stop'] = src_price + stop_atr_val
    final_long_stop_series = pd.Series(index=df_copy.index, dtype=float)
    final_short_stop_series = pd.Series(index=df_copy.index, dtype=float)
    if not df_copy.empty:
        first_valid_long_stop = df_copy['calc_long_stop'].bfill().iloc[0] if not df_copy['calc_long_stop'].bfill().empty else 0
        first_valid_short_stop = df_copy['calc_short_stop'].bfill().iloc[0] if not df_copy['calc_short_stop'].bfill().empty else 0
        final_long_stop_series.iloc[0] = first_valid_long_stop
        final_short_stop_series.iloc[0] = first_valid_short_stop
        for i in range(1, len(df_copy)):
            current_bar_calc_long_stop = df_copy['calc_long_stop'].iloc[i]
            current_bar_calc_short_stop = df_copy['calc_short_stop'].iloc[i]
            prev_final_long_stop = final_long_stop_series.iloc[i-1]
            prev_final_short_stop = final_short_stop_series.iloc[i-1]
            prev_bar_low_price = stop_low_price.iloc[i-1]
            prev_bar_high_price = stop_high_price.iloc[i-1]
            if pd.notna(prev_bar_low_price) and pd.notna(prev_final_long_stop) and pd.notna(current_bar_calc_long_stop) and \
               prev_bar_low_price > prev_final_long_stop:
                final_long_stop_series.iloc[i] = max(current_bar_calc_long_stop, prev_final_long_stop)
            else:
                final_long_stop_series.iloc[i] = current_bar_calc_long_stop
            if pd.notna(prev_bar_high_price) and pd.notna(prev_final_short_stop) and pd.notna(current_bar_calc_short_stop) and \
               prev_bar_high_price < prev_final_short_stop:
                final_short_stop_series.iloc[i] = min(current_bar_calc_short_stop, prev_final_short_stop)
            else:
                final_short_stop_series.iloc[i] = current_bar_calc_short_stop
    df_copy['final_long_stop'] = final_long_stop_series.fillna(method='ffill').fillna(method='bfill')
    df_copy['final_short_stop'] = final_short_stop_series.fillna(method='ffill').fillna(method='bfill')
    df_copy['aw_stop_direction'] = 0
    if not df_copy.empty:
        # Initial direction assumed positive (long)
        df_copy['aw_stop_direction'].iloc[0] = 1
        for i in range(1, len(df_copy)):
            prev_dir = df_copy['aw_stop_direction'].iloc[i-1]
            current_low = stop_low_price.iloc[i]
            current_high = stop_high_price.iloc[i]
            prev_long_stop = df_copy['final_long_stop'].iloc[i-1]
            prev_short_stop = df_copy['final_short_stop'].iloc[i-1]
            if prev_dir == 1 and current_low <= prev_long_stop:
                df_copy['aw_stop_direction'].iloc[i] = -1
            elif prev_dir == -1 and current_high >= prev_short_stop:
                df_copy['aw_stop_direction'].iloc[i] = 1
            else:
                df_copy['aw_stop_direction'].iloc[i] = prev_dir
    df_copy['aw_long_stop'] = df_copy['final_long_stop']
    df_copy['aw_short_stop'] = df_copy['final_short_stop']
    return df_copy['aw_long_stop'], df_copy['aw_short_stop'], df_copy['aw_stop_direction']

# === TradeApp Class (Must be defined before GUI classes) ===
class TradeApp(EWrapper, EClient):
    def __init__(self, ui_queue):
        EClient.__init__(self, self)
        self.data = {}
        self.pos_df = pd.DataFrame(columns=['Account', 'Symbol', 'SecType', 'Currency', 'Position', 'Avg cost'])
        self.order_df = pd.DataFrame(columns=['PermId', 'ClientId', 'OrderId', 'Account', 'Symbol', 'SecType', 'Exchange', 'Action', 'OrderType', 'TotalQty', 'CashQty', 'LmtPrice', 'AuxPrice', 'Status'])
        self.nextValidOrderId = None
        self.ui_queue = ui_queue
        self.req_contracts = {}  # Maps reqId to contract for retry

        # Enhanced features
        self.connection_status = "Disconnected"
        self.last_data_time = {}
        self.error_count = 0

    def log_message(self, msg):
        """Enhanced logging with UI queue support"""
        timestamp = time.strftime('%H:%M:%S')
        formatted_msg = f"[{timestamp}] {msg}"

        if self.ui_queue:
            try:
                self.ui_queue.put(formatted_msg)
            except:
                pass  # Queue might be full or closed

        logger.info(msg)

    def historicalData(self, reqId, bar):
        """Enhanced historical data handler with validation"""
        try:
            bar_data = {
                "Date": bar.date,
                "Open": float(bar.open),
                "High": float(bar.high),
                "Low": float(bar.low),
                "Close": float(bar.close),
                "Volume": int(bar.volume)
            }

            if reqId not in self.data:
                self.data[reqId] = pd.DataFrame([bar_data])
                self.log_message(f"HistoricalData - ReqId: {reqId} - First bar: {bar.date}, Close: ${bar.close:.2f}")
            else:
                new_row = pd.DataFrame([bar_data])
                self.data[reqId] = pd.concat([self.data[reqId], new_row], ignore_index=True)

            # Track last data time
            self.last_data_time[reqId] = time.time()

        except Exception as e:
            self.log_message(f"Error processing historical data for reqId {reqId}: {e}")

    def historicalDataEnd(self, reqId, start, end):
        """Handle end of historical data"""
        if reqId in self.data:
            data_count = len(self.data[reqId])
            self.log_message(f"Historical data complete for reqId {reqId}: {data_count} bars from {start} to {end}")
        else:
            self.log_message(f"No historical data received for reqId {reqId}")

    def nextValidId(self, orderId):
        """Handle next valid order ID"""
        super().nextValidId(orderId)
        self.nextValidOrderId = orderId
        self.log_message(f"NextValidId received: {orderId}")

    def position(self, account, contract, position, avgCost):
        """Enhanced position handler"""
        super().position(account, contract, position, avgCost)

        try:
            position_data = {
                'Account': account,
                'Symbol': contract.symbol,
                'SecType': contract.secType,
                'Currency': contract.currency,
                'Position': float(position),
                'Avg cost': float(avgCost) if avgCost != 0 else 0.0
            }

            self.pos_df = pd.concat([self.pos_df, pd.DataFrame([position_data])], ignore_index=True)

            if position != 0:
                self.log_message(f"Position Update: {contract.symbol} - Qty: {position}, AvgCost: ${avgCost:.2f}")

        except Exception as e:
            self.log_message(f"Error processing position for {contract.symbol}: {e}")

    def positionEnd(self):
        """Handle end of position data"""
        super().positionEnd()
        position_count = len(self.pos_df)
        self.log_message(f"Position data complete: {position_count} positions received")

    def openOrder(self, orderId, contract, order, orderState):
        """Enhanced open order handler"""
        super().openOrder(orderId, contract, order, orderState)

        try:
            order_data = {
                'PermId': order.permId,
                'ClientId': order.clientId,
                'OrderId': orderId,
                'Account': order.account,
                'Symbol': contract.symbol,
                'SecType': contract.secType,
                'Exchange': contract.exchange,
                'Action': order.action,
                'OrderType': order.orderType,
                'TotalQty': float(order.totalQuantity),
                'CashQty': float(order.cashQty) if order.cashQty else 0.0,
                'LmtPrice': float(order.lmtPrice) if order.lmtPrice else 0.0,
                'AuxPrice': float(order.auxPrice) if order.auxPrice else 0.0,
                'Status': orderState.status
            }

            self.order_df = pd.concat([self.order_df, pd.DataFrame([order_data])], ignore_index=True)

            self.log_message(f"Open Order: {orderId} - {order.action} {order.totalQuantity} {contract.symbol} @ {order.orderType} - Status: {orderState.status}")

        except Exception as e:
            self.log_message(f"Error processing open order {orderId}: {e}")

    def orderStatus(self, orderId, status, filled, remaining, avgFillPrice, permId, parentId, lastFillPrice, clientId, whyHeld, mktCapPrice):
        """Enhanced order status handler"""
        super().orderStatus(orderId, status, filled, remaining, avgFillPrice, permId, parentId, lastFillPrice, clientId, whyHeld, mktCapPrice)

        try:
            # Log order status update
            status_msg = f"Order {orderId}: {status}"
            if filled > 0:
                status_msg += f" | Filled: {filled}"
            if remaining > 0:
                status_msg += f" | Remaining: {remaining}"
            if avgFillPrice > 0:
                status_msg += f" | AvgFill: ${avgFillPrice:.2f}"

            self.log_message(status_msg)

            # Remove completed orders from tracking
            if status in ["Filled", "Cancelled", "ApiCancelled"]:
                self.order_df = self.order_df[self.order_df['OrderId'] != orderId]
                self.log_message(f"Order {orderId} removed from tracking - Final Status: {status}")

        except Exception as e:
            self.log_message(f"Error processing order status for {orderId}: {e}")

    def error(self, reqId, errorCode, errorString, advancedOrderReject=""):
        """Enhanced error handler"""
        super().error(reqId, errorCode, errorString)

        self.error_count += 1

        # Format error message
        error_msg = f"Error {errorCode}: {errorString}"
        if reqId != -1:
            error_msg = f"ReqId {reqId} - {error_msg}"
        if advancedOrderReject:
            error_msg += f" | Advanced Reject: {advancedOrderReject}"

        # Categorize error severity
        if errorCode in [200, 202, 203, 300, 366, 2104, 2106, 2158]:
            # Information/Warning messages
            self.log_message(f"INFO: {error_msg}")
        elif errorCode in [502, 503, 504, 1100, 1101, 1102]:
            # Connection related errors
            self.log_message(f"CONNECTION: {error_msg}")
            self.connection_status = "Connection Issues"
        else:
            # Serious errors
            self.log_message(f"ERROR: {error_msg}")

    def connectionClosed(self):
        """Handle connection closed"""
        super().connectionClosed()
        self.connection_status = "Disconnected"
        self.log_message("Connection to TWS/Gateway closed")

    def connectAck(self):
        """Handle connection acknowledgment"""
        super().connectAck()
        self.connection_status = "Connected"
        self.log_message("Connection to TWS/Gateway established")

# --- Enhanced Strategy Configuration Class ---
class EnhancedStrategyConfig:
    def __init__(self, gui_entries):
        # Traditional Alpha Wave parameters
        self.alpha_wave_params = {
            'q_ama_adx_len': int(gui_entries['q_ama_adx_len'].get()),
            'q_ama_weight': float(gui_entries['q_ama_weight'].get()),
            'q_ama_ma_len': int(gui_entries['q_ama_ma_len'].get()),
            'wp_smooth': int(gui_entries['wp_smooth'].get()),
            'wp_const': float(gui_entries['wp_const'].get()),
            'qc_poles': int(gui_entries['qc_poles'].get()),
            'qc_period': int(gui_entries['qc_period'].get()),
            'qc_mult': float(gui_entries['qc_mult'].get()),
            'md_ma_len': int(gui_entries['md_ma_len'].get()),
            'md_calc_len': int(gui_entries['md_calc_len'].get()),
            'md_smooth_len': int(gui_entries['md_smooth_len'].get()),
            'md_threshold': int(gui_entries['md_threshold'].get()),
            'atr_stop_len': int(gui_entries['atr_stop_len'].get()),
            'atr_stop_mult': float(gui_entries['atr_stop_mult'].get()),
            't3_fast_len': int(gui_entries['t3_fast_len'].get()),
            't3_slow_len': int(gui_entries['t3_slow_len'].get()),
            't3_factor': float(gui_entries['t3_factor'].get())
        }

        # ML/DL parameters
        self.ml_params = {
            'use_ml_models': bool(gui_entries.get('use_ml_models', {}).get()),
            'model_type': gui_entries.get('model_type', {}).get() or 'ensemble',
            'lookback_period': int(gui_entries.get('lookback_period', {}).get() or 50),
            'prediction_horizon': int(gui_entries.get('prediction_horizon', {}).get() or 5),
            'retrain_frequency': int(gui_entries.get('retrain_frequency', {}).get() or 100),
            'confidence_threshold': float(gui_entries.get('confidence_threshold', {}).get() or 0.6),
            'feature_engineering': bool(gui_entries.get('feature_engineering', {}).get()),
            'ensemble_voting': gui_entries.get('ensemble_voting', {}).get() or 'soft'
        }

        self.params = {**self.alpha_wave_params, **self.ml_params}

    def validate(self):
        for key, value in self.alpha_wave_params.items():
            if isinstance(value, (int, float)) and value <= 0:
                raise ValueError(f"Parameter {key} must be positive")

        if self.ml_params['lookback_period'] < 10:
            raise ValueError("Lookback period must be at least 10")
        if self.ml_params['confidence_threshold'] < 0.5 or self.ml_params['confidence_threshold'] > 1.0:
            raise ValueError("Confidence threshold must be between 0.5 and 1.0")

        return True

# --- Advanced Feature Engineering Class ---
class FeatureEngineer:
    def __init__(self):
        self.scaler = RobustScaler()
        self.feature_names = []

    def create_technical_features(self, df):
        """Create comprehensive technical analysis features"""
        df = df.copy()

        # Price-based features
        df['returns'] = df['Close'].pct_change()
        df['log_returns'] = np.log(df['Close'] / df['Close'].shift(1))
        df['price_momentum'] = df['Close'] / df['Close'].shift(10) - 1
        df['price_acceleration'] = df['returns'] - df['returns'].shift(1)

        # Volatility features
        df['volatility_5'] = df['returns'].rolling(5).std()
        df['volatility_20'] = df['returns'].rolling(20).std()
        df['volatility_ratio'] = df['volatility_5'] / df['volatility_20']

        # Volume features
        df['volume_sma'] = df['Volume'].rolling(20).mean()
        df['volume_ratio'] = df['Volume'] / df['volume_sma']
        df['price_volume'] = df['Close'] * df['Volume']
        df['vwap'] = (df['price_volume'].rolling(20).sum() /
                     df['Volume'].rolling(20).sum())

        # High-Low features
        df['hl_ratio'] = (df['High'] - df['Low']) / df['Close']
        df['upper_shadow'] = (df['High'] - np.maximum(df['Open'], df['Close'])) / df['Close']
        df['lower_shadow'] = (np.minimum(df['Open'], df['Close']) - df['Low']) / df['Close']

        # Moving averages and crossovers
        for period in [5, 10, 20, 50]:
            df[f'sma_{period}'] = df['Close'].rolling(period).mean()
            df[f'ema_{period}'] = df['Close'].ewm(span=period).mean()
            df[f'price_sma_{period}_ratio'] = df['Close'] / df[f'sma_{period}']

        # RSI
        delta = df['Close'].diff()
        gain = (delta.where(delta > 0, 0)).rolling(window=14).mean()
        loss = (-delta.where(delta < 0, 0)).rolling(window=14).mean()
        rs = gain / loss
        df['rsi'] = 100 - (100 / (1 + rs))

        # Bollinger Bands
        bb_period = 20
        bb_std = 2
        df['bb_middle'] = df['Close'].rolling(bb_period).mean()
        bb_std_val = df['Close'].rolling(bb_period).std()
        df['bb_upper'] = df['bb_middle'] + (bb_std_val * bb_std)
        df['bb_lower'] = df['bb_middle'] - (bb_std_val * bb_std)
        df['bb_position'] = (df['Close'] - df['bb_lower']) / (df['bb_upper'] - df['bb_lower'])

        # MACD
        exp1 = df['Close'].ewm(span=12).mean()
        exp2 = df['Close'].ewm(span=26).mean()
        df['macd'] = exp1 - exp2
        df['macd_signal'] = df['macd'].ewm(span=9).mean()
        df['macd_histogram'] = df['macd'] - df['macd_signal']

        # Stochastic Oscillator
        low_min = df['Low'].rolling(window=14).min()
        high_max = df['High'].rolling(window=14).max()
        df['stoch_k'] = 100 * ((df['Close'] - low_min) / (high_max - low_min))
        df['stoch_d'] = df['stoch_k'].rolling(window=3).mean()

        # Williams %R
        df['williams_r'] = -100 * ((high_max - df['Close']) / (high_max - low_min))

        # Commodity Channel Index
        tp = (df['High'] + df['Low'] + df['Close']) / 3
        df['cci'] = (tp - tp.rolling(20).mean()) / (0.015 * tp.rolling(20).std())

        # Lag features
        for lag in [1, 2, 3, 5, 10]:
            df[f'close_lag_{lag}'] = df['Close'].shift(lag)
            df[f'volume_lag_{lag}'] = df['Volume'].shift(lag)
            df[f'returns_lag_{lag}'] = df['returns'].shift(lag)

        return df

    def create_target_variable(self, df, prediction_horizon=5, target_type='classification'):
        """Create target variable for ML models"""
        df = df.copy()

        if target_type == 'classification':
            # Binary classification: 1 if price goes up, 0 if down
            future_returns = df['Close'].shift(-prediction_horizon) / df['Close'] - 1
            df['target'] = (future_returns > 0).astype(int)
        elif target_type == 'regression':
            # Regression: predict future returns
            df['target'] = df['Close'].shift(-prediction_horizon) / df['Close'] - 1

        return df

    def prepare_ml_features(self, df, feature_columns=None):
        """Prepare features for ML models"""
        if feature_columns is None:
            # Auto-select numerical features
            feature_columns = df.select_dtypes(include=[np.number]).columns.tolist()
            exclude_cols = ['Open', 'High', 'Low', 'Close', 'Volume', 'target']
            feature_columns = [col for col in feature_columns if col not in exclude_cols]

        self.feature_names = feature_columns

        # Handle missing values
        X = df[feature_columns].fillna(method='ffill').fillna(method='bfill')

        # Scale features
        X_scaled = self.scaler.fit_transform(X)

        return pd.DataFrame(X_scaled, columns=feature_columns, index=df.index)

# --- ML Model Manager Class ---
class MLModelManager:
    def __init__(self, config):
        self.config = config
        self.models = {}
        self.feature_engineer = FeatureEngineer()
        self.is_trained = False
        self.prediction_cache = {}

    def build_random_forest(self, task_type='classification'):
        """Build Random Forest model"""
        if task_type == 'classification':
            model = RandomForestClassifier(
                n_estimators=100,
                max_depth=10,
                min_samples_split=5,
                min_samples_leaf=2,
                random_state=42,
                n_jobs=-1
            )
        else:
            model = RandomForestRegressor(
                n_estimators=100,
                max_depth=10,
                min_samples_split=5,
                min_samples_leaf=2,
                random_state=42,
                n_jobs=-1
            )
        return model

    def build_lightgbm(self, task_type='classification'):
        """Build LightGBM model"""
        if task_type == 'classification':
            model = lgb.LGBMClassifier(
                n_estimators=100,
                max_depth=8,
                learning_rate=0.1,
                num_leaves=31,
                subsample=0.8,
                colsample_bytree=0.8,
                random_state=42,
                verbose=-1
            )
        else:
            model = lgb.LGBMRegressor(
                n_estimators=100,
                max_depth=8,
                learning_rate=0.1,
                num_leaves=31,
                subsample=0.8,
                colsample_bytree=0.8,
                random_state=42,
                verbose=-1
            )
        return model

    def build_xgboost(self, task_type='classification'):
        """Build XGBoost model"""
        if task_type == 'classification':
            model = xgb.XGBClassifier(
                n_estimators=100,
                max_depth=6,
                learning_rate=0.1,
                subsample=0.8,
                colsample_bytree=0.8,
                random_state=42,
                eval_metric='logloss'
            )
        else:
            model = xgb.XGBRegressor(
                n_estimators=100,
                max_depth=6,
                learning_rate=0.1,
                subsample=0.8,
                colsample_bytree=0.8,
                random_state=42,
                eval_metric='rmse'
            )
        return model

    def build_lstm_model(self, input_shape, task_type='classification'):
        """Build LSTM neural network model"""
        model = Sequential([
            LSTM(50, return_sequences=True, input_shape=input_shape),
            Dropout(0.2),
            BatchNormalization(),
            LSTM(50, return_sequences=False),
            Dropout(0.2),
            BatchNormalization(),
            Dense(25, activation='relu'),
            Dropout(0.2),
            Dense(1, activation='sigmoid' if task_type == 'classification' else 'linear')
        ])

        optimizer = Adam(learning_rate=0.001)
        loss = 'binary_crossentropy' if task_type == 'classification' else 'mse'
        metrics = ['accuracy'] if task_type == 'classification' else ['mae']

        model.compile(optimizer=optimizer, loss=loss, metrics=metrics)
        return model

    def prepare_lstm_data(self, X, y, lookback_period):
        """Prepare data for LSTM model"""
        X_lstm, y_lstm = [], []

        for i in range(lookback_period, len(X)):
            X_lstm.append(X.iloc[i-lookback_period:i].values)
            y_lstm.append(y.iloc[i])

        return np.array(X_lstm), np.array(y_lstm)

    def train_models(self, df):
        """Train all ML models"""
        logger.info("Starting ML model training...")

        # Feature engineering
        df_features = self.feature_engineer.create_technical_features(df)
        df_features = self.feature_engineer.create_target_variable(
            df_features,
            self.config.ml_params['prediction_horizon']
        )

        # Prepare features
        X = self.feature_engineer.prepare_ml_features(df_features)
        y = df_features['target'].dropna()

        # Align X and y
        min_length = min(len(X), len(y))
        X = X.iloc[:min_length]
        y = y.iloc[:min_length]

        # Remove rows with NaN values
        valid_idx = ~(X.isna().any(axis=1) | y.isna())
        X = X[valid_idx]
        y = y[valid_idx]

        if len(X) < 100:
            logger.warning("Insufficient data for ML training")
            return False

        # Time series split
        tscv = TimeSeriesSplit(n_splits=3)
        train_idx, test_idx = list(tscv.split(X))[-1]

        X_train, X_test = X.iloc[train_idx], X.iloc[test_idx]
        y_train, y_test = y.iloc[train_idx], y.iloc[test_idx]

        # Train traditional ML models
        self.models['random_forest'] = self.build_random_forest()
        self.models['lightgbm'] = self.build_lightgbm()
        self.models['xgboost'] = self.build_xgboost()

        for name, model in self.models.items():
            if name != 'lstm':
                try:
                    model.fit(X_train, y_train)
                    y_pred = model.predict(X_test)
                    accuracy = accuracy_score(y_test, y_pred)
                    logger.info(f"{name} model accuracy: {accuracy:.4f}")
                except Exception as e:
                    logger.error(f"Error training {name}: {e}")

        # Train LSTM model
        try:
            lookback = self.config.ml_params['lookback_period']
            if len(X_train) > lookback:
                X_lstm, y_lstm = self.prepare_lstm_data(X_train, y_train, lookback)

                if len(X_lstm) > 50:  # Minimum samples for LSTM
                    lstm_model = self.build_lstm_model((lookback, X_train.shape[1]))

                    early_stopping = EarlyStopping(
                        monitor='val_loss',
                        patience=10,
                        restore_best_weights=True
                    )

                    lstm_model.fit(
                        X_lstm, y_lstm,
                        epochs=50,
                        batch_size=32,
                        validation_split=0.2,
                        callbacks=[early_stopping],
                        verbose=0
                    )

                    self.models['lstm'] = lstm_model
                    logger.info("LSTM model trained successfully")
        except Exception as e:
            logger.error(f"Error training LSTM: {e}")

        self.is_trained = True
        logger.info("ML model training completed")
        return True

    def predict(self, df_current):
        """Make predictions using ensemble of models"""
        if not self.is_trained or not self.models:
            return None, 0.0

        try:
            # Feature engineering
            df_features = self.feature_engineer.create_technical_features(df_current)
            X_current = self.feature_engineer.prepare_ml_features(df_features)

            if X_current.empty or X_current.iloc[-1].isna().any():
                return None, 0.0

            # Get latest features
            X_latest = X_current.iloc[-1:].values

            predictions = []
            confidences = []

            # Traditional ML model predictions
            for name, model in self.models.items():
                if name != 'lstm':
                    try:
                        if hasattr(model, 'predict_proba'):
                            pred_proba = model.predict_proba(X_latest)[0]
                            pred = 1 if pred_proba[1] > 0.5 else 0
                            confidence = max(pred_proba)
                        else:
                            pred = model.predict(X_latest)[0]
                            confidence = abs(pred)

                        predictions.append(pred)
                        confidences.append(confidence)
                    except Exception as e:
                        logger.error(f"Error predicting with {name}: {e}")

            # LSTM prediction
            if 'lstm' in self.models:
                try:
                    lookback = self.config.ml_params['lookback_period']
                    if len(X_current) >= lookback:
                        X_lstm = X_current.iloc[-lookback:].values.reshape(1, lookback, -1)
                        lstm_pred = self.models['lstm'].predict(X_lstm, verbose=0)[0][0]
                        predictions.append(1 if lstm_pred > 0.5 else 0)
                        confidences.append(lstm_pred if lstm_pred > 0.5 else 1 - lstm_pred)
                except Exception as e:
                    logger.error(f"Error predicting with LSTM: {e}")

            if not predictions:
                return None, 0.0

            # Ensemble prediction
            if self.config.ml_params['ensemble_voting'] == 'hard':
                final_prediction = 1 if sum(predictions) > len(predictions) / 2 else 0
            else:  # soft voting
                final_prediction = 1 if np.mean(predictions) > 0.5 else 0

            ensemble_confidence = np.mean(confidences)

            return final_prediction, ensemble_confidence

        except Exception as e:
            logger.error(f"Error in ensemble prediction: {e}")
            return None, 0.0

# --- Enhanced Alpha Wave Strategy Class ---
class EnhancedAlphaWaveStrategy:
    def __init__(self, params):
        self.params = params
        self.ml_manager = MLModelManager(EnhancedStrategyConfig({'use_ml_models': True})) if params.get('use_ml_models') else None
        self.trade_count = 0

    def compute_indicators(self, df):
        """Compute all technical indicators including original Alpha Wave indicators"""
        df = df.copy()

        # Original Alpha Wave indicators
        df['q_ama'] = quantum_adaptive_ma(df,
                                         adx_length=self.params['q_ama_adx_len'],
                                         weight=self.params['q_ama_weight'],
                                         ma_length=self.params['q_ama_ma_len'])

        df['wave_pulse_val'] = wave_pulse(df,
                                         smooth_period=self.params['wp_smooth'],
                                         constant_factor=self.params['wp_const'])

        if 'hlc3' not in df.columns:
            df['hlc3'] = (df['High'] + df['Low'] + df['Close']) / 3.0

        df['q_filter'], df['q_upper'], df['q_lower'] = quantum_channel(df,
                                                                     source_col='hlc3',
                                                                     poles=self.params['qc_poles'],
                                                                     period=self.params['qc_period'],
                                                                     multiplier=self.params['qc_mult'])

        df['momentum_density_val'] = momentum_density(df,
                                                     ma_length=self.params['md_ma_len'],
                                                     calc_length=self.params['md_calc_len'],
                                                     smooth_length=self.params['md_smooth_len'])

        df['aw_long_stop'], df['aw_short_stop'], df['aw_stop_direction'] = atr_trailing_stop_alpha_wave(df,
                                                                                                        atr_function_ref=atr,
                                                                                                        atr_length=self.params['atr_stop_len'],
                                                                                                        atr_mult=self.params['atr_stop_mult'],
                                                                                                        use_wicks=True)

        df['t3_fast'] = t3_moving_average(df['Close'],
                                         length=self.params['t3_fast_len'],
                                         factor=self.params['t3_factor'])

        df['t3_slow'] = t3_moving_average(df['Close'],
                                         length=self.params['t3_slow_len'],
                                         factor=self.params['t3_factor'])

        df['t3_diff'] = df['t3_fast'] - df['t3_slow']

        # Additional ML features if enabled
        if self.params.get('feature_engineering', False):
            df = self.ml_manager.feature_engineer.create_technical_features(df)

        return df

    def generate_signals(self, df):
        """Generate trading signals combining Alpha Wave and ML predictions"""
        # Original Alpha Wave signals
        alpha_wave_long, alpha_wave_short, long_exit, short_exit = self._alpha_wave_signals(df)

        # ML signals if enabled
        ml_signal, ml_confidence = None, 0.0
        if self.params.get('use_ml_models', False) and self.ml_manager and self.ml_manager.is_trained:
            ml_signal, ml_confidence = self.ml_manager.predict(df)

        # Combine signals
        final_long_entry = alpha_wave_long
        final_short_entry = alpha_wave_short

        if ml_signal is not None and ml_confidence > self.params.get('confidence_threshold', 0.6):
            # ML signal reinforcement
            if ml_signal == 1:  # ML predicts price increase
                final_long_entry = final_long_entry and True
                final_short_entry = final_short_entry and False
            else:  # ML predicts price decrease
                final_long_entry = final_long_entry and False
                final_short_entry = final_short_entry and True

        return final_long_entry, final_short_entry, long_exit, short_exit, ml_signal, ml_confidence

    def _alpha_wave_signals(self, df):
        """Original Alpha Wave signal generation logic"""
        last_row = df.iloc[-1]
        prev_row = df.iloc[-2] if len(df) > 1 else last_row

        last_close = last_row["Close"]
        last_q_ama = last_row["q_ama"]
        last_wave_pulse = last_row["wave_pulse_val"]
        prev_wave_pulse = prev_row["wave_pulse_val"]
        last_q_filter = last_row["q_filter"]
        prev_q_filter = prev_row["q_filter"]
        last_momentum_density = last_row["momentum_density_val"]
        vol_threshold_param = self.params['md_threshold']
        last_aw_stop_direction = last_row["aw_stop_direction"]
        prev_aw_stop_direction = prev_row["aw_stop_direction"]
        last_aw_long_stop = last_row["aw_long_stop"]
        last_aw_short_stop = last_row["aw_short_stop"]

        # Signal conditions
        baseline_trend_val = 1 if last_close > last_q_ama else -1 if last_close < last_q_ama else 0
        wave_pulse_direction = 1 if last_wave_pulse > prev_wave_pulse else -1
        wave_pulse_long_cond = wave_pulse_direction > 0 and last_close > last_wave_pulse
        wave_pulse_short_cond = wave_pulse_direction < 0 and last_close < last_wave_pulse
        quantum_filter_direction = 1 if last_q_filter > prev_q_filter else -1
        quantum_long_cond = quantum_filter_direction > 0 and last_close > last_q_filter
        quantum_short_cond = quantum_filter_direction < 0 and last_close < last_q_filter
        momentum_ok_sig = last_momentum_density > vol_threshold_param
        potential_long_entry_trigger = last_aw_stop_direction == 1 and prev_aw_stop_direction == -1
        potential_short_entry_trigger = last_aw_stop_direction == -1 and prev_aw_stop_direction == 1

        # Entry signals
        alpha_wave_long_entry = (potential_long_entry_trigger and
                               baseline_trend_val == 1 and
                               wave_pulse_long_cond and
                               quantum_long_cond and
                               momentum_ok_sig)

        alpha_wave_short_entry = (potential_short_entry_trigger and
                                baseline_trend_val == -1 and
                                wave_pulse_short_cond and
                                quantum_short_cond and
                                momentum_ok_sig)

        # Exit signals
        aw_sl_exit_long = last_aw_stop_direction == -1 and prev_aw_stop_direction == 1
        aw_sl_exit_short = last_aw_stop_direction == 1 and prev_aw_stop_direction == -1
        channel_exit_long_cond = last_row["High"] > last_aw_short_stop
        channel_exit_short_cond = last_row["Low"] < last_aw_long_stop
        final_long_exit_signal = aw_sl_exit_long or channel_exit_long_cond
        final_short_exit_signal = aw_sl_exit_short or channel_exit_short_cond

        return alpha_wave_long_entry, alpha_wave_short_entry, final_long_exit_signal, final_short_exit_signal

# --- Enhanced Backtesting Strategy Class ---
class EnhancedAlphaWaveBacktestStrategy(Strategy):
    def __init__(self, params):
        super().__init__()
        self.params = params
        self.strategy = EnhancedAlphaWaveStrategy(params)
        self.ml_trained = False

    def init(self):
        # Train ML models if enabled
        if self.params.get('use_ml_models', False) and len(self.data.df) > 200:
            try:
                self.strategy.ml_manager.train_models(self.data.df)
                self.ml_trained = True
                logger.info("ML models trained for backtesting")
            except Exception as e:
                logger.error(f"ML training failed in backtest: {e}")

        # Compute indicators
        df_with_indicators = self.strategy.compute_indicators(self.data.df)

        # Create indicator series for backtesting framework
        self.q_ama = self.I(lambda: df_with_indicators['q_ama'])
        self.wave_pulse = self.I(lambda: df_with_indicators['wave_pulse_val'])
        self.q_filter = self.I(lambda: df_with_indicators['q_filter'])
        self.momentum_density = self.I(lambda: df_with_indicators['momentum_density_val'])
        self.aw_stop_direction = self.I(lambda: df_with_indicators['aw_stop_direction'])
        self.aw_long_stop = self.I(lambda: df_with_indicators['aw_long_stop'])
        self.aw_short_stop = self.I(lambda: df_with_indicators['aw_short_stop'])

    def next(self):
        # Skip if insufficient data
        if len(self.data) < 100:
            return

        # Get current data slice
        current_df = self.data.df.iloc[:len(self.data)]

        # Generate signals
        try:
            long_entry, short_entry, long_exit, short_exit, ml_signal, ml_confidence = self.strategy.generate_signals(current_df)

            # Entry logic
            if long_entry and not self.position:
                self.buy(size=0.95)  # Use 95% of available capital

            elif short_entry and not self.position:
                self.sell(size=0.95)

            # Exit logic
            elif long_exit and self.position.is_long:
                self.position.close()

            elif short_exit and self.position.is_short:
                self.position.close()

        except Exception as e:
            logger.error(f"Error in backtesting strategy next(): {e}")

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

if __name__ == "__main__":
    # Package checking and setup
    root = tk.Tk()
    gui = EnhancedTradingGUI(root)
    root.mainloop()
