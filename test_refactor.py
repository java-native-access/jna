import sys
import os
import unittest

# Add the current directory to the path to allow importing the package
sys.path.insert(0, os.path.abspath(os.path.dirname(__file__)))

print("--- Starting Refactoring Verification Test ---")

class TestRefactoringImports(unittest.TestCase):

    def test_import_main(self):
        try:
            from trading_bot_app.main import main
            print("Successfully imported main function.")
            self.assertTrue(callable(main))
        except ImportError as e:
            self.fail(f"Failed to import main function: {e}")

    def test_import_ui(self):
        try:
            from trading_bot_app.ui import EnhancedTradingGUI, enhanced_trading_logic_thread
            print("Successfully imported UI components.")
            self.assertTrue(isinstance(EnhancedTradingGUI, type))
            self.assertTrue(callable(enhanced_trading_logic_thread))
        except ImportError as e:
            self.fail(f"Failed to import UI components: {e}")

    def test_import_ib_client(self):
        try:
            from trading_bot_app.ib_client import TradeApp
            print("Successfully imported IB Client.")
            self.assertTrue(isinstance(TradeApp, type))
        except ImportError as e:
            self.fail(f"Failed to import IB Client: {e}")

    def test_import_strategy(self):
        try:
            from trading_bot_app.strategy import EnhancedAlphaWaveStrategy
            print("Successfully imported Strategy.")
            self.assertTrue(isinstance(EnhancedAlphaWaveStrategy, type))
        except ImportError as e:
            self.fail(f"Failed to import Strategy: {e}")

    def test_import_ml_manager(self):
        try:
            from trading_bot_app.ml_manager import MLModelManager
            print("Successfully imported ML Manager.")
            self.assertTrue(isinstance(MLModelManager, type))
        except ImportError as e:
            self.fail(f"Failed to import ML Manager: {e}")

    def test_import_config(self):
        try:
            from trading_bot_app.config import EnhancedStrategyConfig
            print("Successfully imported Config.")
            self.assertTrue(isinstance(EnhancedStrategyConfig, type))
        except ImportError as e:
            self.fail(f"Failed to import Config: {e}")

    def test_import_backtesting(self):
        try:
            from trading_bot_app.backtesting import EnhancedAlphaWaveBacktestStrategy
            print("Successfully imported Backtesting strategy.")
            self.assertTrue(isinstance(EnhancedAlphaWaveBacktestStrategy, type))
        except ImportError as e:
            self.fail(f"Failed to import Backtesting strategy: {e}")

    def test_import_indicators(self):
        try:
            from trading_bot_app.indicators import atr
            print("Successfully imported an indicator function.")
            self.assertTrue(callable(atr))
        except ImportError as e:
            self.fail(f"Failed to import an indicator function: {e}")

    def test_import_utils(self):
        try:
            from trading_bot_app.utils import usTechStk
            print("Successfully imported a utility function.")
            self.assertTrue(callable(usTechStk))
        except ImportError as e:
            self.fail(f"Failed to import a utility function: {e}")

if __name__ == '__main__':
    print("\nRunning import tests...")
    suite = unittest.TestSuite()
    suite.addTest(unittest.makeSuite(TestRefactoringImports))
    runner = unittest.TextTestRunner()
    result = runner.run(suite)

    if result.wasSuccessful():
        print("\n--- All major components imported successfully. ---")
        print("--- Refactoring verification PASSED! ---")
        sys.exit(0)
    else:
        print("\n--- Refactoring verification FAILED! ---")
        sys.exit(1)
