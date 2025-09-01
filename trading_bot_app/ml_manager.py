import logging
import numpy as np
import pandas as pd
import lightgbm as lgb
import xgboost as xgb
from sklearn.ensemble import RandomForestClassifier, RandomForestRegressor
from sklearn.model_selection import TimeSeriesSplit
from sklearn.preprocessing import RobustScaler
from sklearn.metrics import accuracy_score
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import LSTM, Dense, Dropout, BatchNormalization
from tensorflow.keras.optimizers import Adam
from tensorflow.keras.callbacks import EarlyStopping

from .config import EnhancedStrategyConfig

logger = logging.getLogger(__name__)

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
