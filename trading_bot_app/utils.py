import time
import pandas as pd
from ibapi.contract import Contract
from ibapi.order import Order

# === Helper Functions ===
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
