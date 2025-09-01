import time
import logging
import pandas as pd
from ibapi.client import EClient
from ibapi.wrapper import EWrapper

logger = logging.getLogger(__name__)

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
