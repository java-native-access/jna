import logging
from backtesting import Strategy
from .strategy import EnhancedAlphaWaveStrategy

logger = logging.getLogger(__name__)

# --- Enhanced Backtesting Strategy Class ---
class EnhancedAlphaWaveBacktestStrategy(Strategy):

    # Parameters that will be passed from the bt.run() call
    q_ama_adx_len = 2
    q_ama_weight = 10.0
    q_ama_ma_len = 6
    wp_smooth = 21
    wp_const = 0.4
    qc_poles = 4
    qc_period = 144
    qc_mult = 1.414
    md_ma_len = 100
    md_calc_len = 60
    md_smooth_len = 3
    md_threshold = 90
    atr_stop_len = 22
    atr_stop_mult = 3.0
    t3_fast_len = 12
    t3_slow_len = 25
    t3_factor = 0.7
    use_ml_models = True
    model_type = 'ensemble'
    lookback_period = 50
    prediction_horizon = 5
    retrain_frequency = 100
    confidence_threshold = 0.6
    feature_engineering = True
    ensemble_voting = 'soft'

    def init(self):
        """
        Initialize the strategy. This is called by the backtesting framework.
        """
        # Gather all parameters into a dictionary to pass to the core strategy logic
        param_keys = [
            'q_ama_adx_len', 'q_ama_weight', 'q_ama_ma_len', 'wp_smooth', 'wp_const',
            'qc_poles', 'qc_period', 'qc_mult', 'md_ma_len', 'md_calc_len',
            'md_smooth_len', 'md_threshold', 'atr_stop_len', 'atr_stop_mult',
            't3_fast_len', 't3_slow_len', 't3_factor', 'use_ml_models', 'model_type',
            'lookback_period', 'prediction_horizon', 'retrain_frequency',
            'confidence_threshold', 'feature_engineering', 'ensemble_voting'
        ]
        params = {key: getattr(self, key) for key in param_keys}

        # Instantiate the core strategy logic
        self.strategy = EnhancedAlphaWaveStrategy(params)
        self.ml_trained = False

        # Train ML models if enabled (CRITICAL: This still causes lookahead bias)
        # TODO: Refactor this to use a walk-forward training approach.
        if params.get('use_ml_models', False) and len(self.data.df) > 200:
            try:
                self.strategy.ml_manager.train_models(self.data.df)
                self.ml_trained = True
                logger.info("ML models trained for backtesting (WARNING: Potential lookahead bias)")
            except Exception as e:
                logger.error(f"ML training failed in backtest: {e}")

        # Compute indicators on the full dataset
        df_with_indicators = self.strategy.compute_indicators(self.data.df)

        # Create indicator series for the backtesting framework to use
        self.q_ama = self.I(lambda: df_with_indicators['q_ama'])
        self.wave_pulse = self.I(lambda: df_with_indicators['wave_pulse_val'])
        self.q_filter = self.I(lambda: df_with_indicators['q_filter'])
        self.momentum_density = self.I(lambda: df_with_indicators['momentum_density_val'])
        self.aw_stop_direction = self.I(lambda: df_with_indicators['aw_stop_direction'])
        self.aw_long_stop = self.I(lambda: df_with_indicators['aw_long_stop'])
        self.aw_short_stop = self.I(lambda: df_with_indicators['aw_short_stop'])

    def next(self):
        """
        Step through the data, generating signals and placing orders.
        """
        # Skip if insufficient data
        if len(self.data) < 100:
            return

        # Get current data slice. The backtesting framework handles the slicing.
        current_df = self.data.df.iloc[:len(self.data)]

        # Generate signals
        try:
            (long_entry, short_entry,
             long_exit, short_exit,
             ml_signal, ml_confidence) = self.strategy.generate_signals(current_df)

            # Entry logic
            if long_entry and not self.position:
                self.buy(size=0.95)
            elif short_entry and not self.position:
                self.sell(size=0.95)

            # Exit logic
            elif long_exit and self.position.is_long:
                self.position.close()
            elif short_exit and self.position.is_short:
                self.position.close()

        except Exception as e:
            logger.error(f"Error in backtesting strategy next(): {e}")
