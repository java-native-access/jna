import pandas as pd
import numpy as np

# === Indicator Functions ===

def an_filter(series, alpha, poles):
    """
    Approximate anti-aliasing filter using chained EMAs for multi-pole low-pass filtering.
    """
    filtered = series.copy()
    for _ in range(poles):
        filtered = alpha * series + (1 - alpha) * filtered.shift(1).fillna(method='bfill')
        series = filtered  # Chain the filters
    return filtered.fillna(method='bfill')

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
