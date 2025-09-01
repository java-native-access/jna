class EnhancedStrategyConfig:
    """
    A data class to hold strategy parameters, initialized from a dictionary.
    This decouples the strategy logic from the UI.
    """
    def __init__(self, params_dict):
        # Define default values for all possible parameters
        defaults = {
            'q_ama_adx_len': 2, 'q_ama_weight': 10.0, 'q_ama_ma_len': 6,
            'wp_smooth': 21, 'wp_const': 0.4,
            'qc_poles': 4, 'qc_period': 144, 'qc_mult': 1.414,
            'md_ma_len': 100, 'md_calc_len': 60, 'md_smooth_len': 3, 'md_threshold': 90,
            'atr_stop_len': 22, 'atr_stop_mult': 3.0,
            't3_fast_len': 12, 't3_slow_len': 25, 't3_factor': 0.7,
            'use_ml_models': True, 'model_type': 'ensemble',
            'lookback_period': 50, 'prediction_horizon': 5,
            'retrain_frequency': 100, 'confidence_threshold': 0.6,
            'feature_engineering': True, 'ensemble_voting': 'soft'
        }

        # Update defaults with provided params
        self.params = defaults
        self.params.update(params_dict)

        # For convenience, set parameters as attributes of the object
        for key, value in self.params.items():
            setattr(self, key, value)

        # For compatibility with code that expects ml_params dict
        self.ml_params = {
            'use_ml_models': self.use_ml_models,
            'model_type': self.model_type,
            'lookback_period': self.lookback_period,
            'prediction_horizon': self.prediction_horizon,
            'retrain_frequency': self.retrain_frequency,
            'confidence_threshold': self.confidence_threshold,
            'feature_engineering': self.feature_engineering,
            'ensemble_voting': self.ensemble_voting,
        }

    def validate(self):
        """Validates the strategy parameters."""
        if self.lookback_period < 10:
            raise ValueError("Lookback period must be at least 10")
        if not (0.5 <= self.confidence_threshold <= 1.0):
            raise ValueError("Confidence threshold must be between 0.5 and 1.0")

        # Add more validation for other params as needed
        return True
