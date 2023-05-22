class StockBrokerClient:
	def __init__(self, client_name="", broker_name=""):
		self.broker_name = broker_name
		self.client_name = client_name
		self.stocks = []
		self.strategy = ""
		self.portfolio = ""

	def buy(self, symbol, quantity):
		pass

	def sell(self, symbol, quantity):
		pass
