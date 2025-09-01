import tkinter as tk
import logging
from .ui import EnhancedTradingGUI

def main():
    """
    Main function to initialize and run the trading bot application.
    """
    # Setup enhanced logging for the entire application
    logging.basicConfig(
        level=logging.INFO,
        format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
        handlers=[
            logging.FileHandler('enhanced_trading_bot.log'),
            logging.StreamHandler()
        ]
    )

    # Initialize the Tkinter root window and the GUI
    root = tk.Tk()
    gui = EnhancedTradingGUI(root)

    # Start the Tkinter event loop
    root.mainloop()

if __name__ == "__main__":
    main()
