from .config import EnhancedStrategyConfig
from .indicators import (
    quantum_adaptive_ma,
    wave_pulse,
    quantum_channel,
    momentum_density,
    atr_trailing_stop_alpha_wave,
    t3_moving_average,
    atr,
)
from .ml_manager import MLModelManager

# --- Enhanced Alpha Wave Strategy Class ---
class EnhancedAlphaWaveStrategy:
    def __init__(self, params):
        self.params = params
        self.config = EnhancedStrategyConfig(params)
        self.ml_manager = MLModelManager(self.config) if self.config.use_ml_models else None
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
        if self.params.get('feature_engineering', False) and self.ml_manager:
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
