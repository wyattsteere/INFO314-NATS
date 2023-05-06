import random
import threading
import time

class StockMarket:
    def __init__(self, publishFn, symbols) -> None:
        self.publish = publishFn
        self.symbols = symbols
        self.stocks = {}

        for symbol in symbols:
            # Price is always in pennies; floating-point for
            # money is evil and dangerous
            price = random.randrange(10000) + 1000 # min $10.00
            self.stocks[symbol] = price
            print(symbol + " " + str(price))

    def run(self):
        self.quit = False
        while (self.quit == False):
            time.sleep(random.randrange(5) + 1)            

            # Choose a stock to fluctuate at random, +/- $5.00
            symbol = self.symbols[random.randrange(len(self.symbols))]
            oldPrice = self.stocks[symbol]
            adjustment = random.randrange(1000) - 500
            newPrice = oldPrice + adjustment
            print(symbol + " " + str(adjustment) + " = " + str(newPrice))
            self.stocks[symbol] = newPrice

            self.publish(symbol, adjustment, newPrice)

def publish(symbol, adjustment, price):
    print("PUBLISH: " + str(symbol) + " " + str(adjustment) + " " + str(price))

if __name__ == "__main__":
    sm = StockMarket(publish, ["AMZN", "GOOG", "MSFT"])
    threading.Thread(target=sm.run).start()
    print("Press Ctrl-C to terminate")
