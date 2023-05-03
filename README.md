# INFO314-NATS

Welcome! This is a group project for INFO 314.

You are going to be experimenting with a "messaging system" called [NATS](https://nats.io/) (also long ago known as "gnatsd"). It has copious amounts of documentation on its website, including support for all mainstream programming languages (and quite a few that aren't considered mainstream).

## Goals

In this particular assignment, you are going to be building out a system of "stock market" principals: a "publisher" that will mimic the ups and downs of a given stock market (remember, there are many), a "monitor" that will watch the ups and downs and report on what it sees, and a "broker" and "clients" that will mimic the activities of a stock broker (buying and selling particular stocks) and their clients (who have particular strategies for when to buy and when to sell). Messages will be XML data, in order to maximize interoperability across languages/platforms.

## Steps

Ensure that you have a NATS server installed locally; it is a single binary, which you can either obtain from visiting the NATS website's [download](https://nats.io/download/) page, or use your platform's package manager to obtain it, or you can use the two binaries stored in this repository (if you are running macOS or Windows).

In a Terminal/console window, navigate to the directory the server is stored, and start it by typing `nats-server`. This will start the server, listening for connections on port 4222 (it's default port). 

> **NOTE:** Running NATS on its default port is sufficient for this homework, but if you wish you can run it with some additional command-line parameters, such as `--http_port` to specify a port on which you can get information about the running server in your web browser. Run `nats-server --help` to get a complete list of options.

At this point, the messaging server is up and running, and you shouldn't need to do anything further to it. Take note of the port the server is running on (4222 if you didn't spcify otherwise; look for the log message `[INF] Listening for client connections on 0.0.0.0:4222` in the console window running the server if you're not sure), as that will be part of the "NATS URL" you will need to pass to various clients in order to connect to your running NATS server.

> **NOTE:** A "NATS URL" looks very similar to an HTTP URL, but uses the "nats" scheme instead of "http"; thus, for most situations, connecting to a NATS server running on your local machine will require a URL of `nats://localhost:4222` or `"nats://127.0.0.1:4222"`.

Now you need to start building out the pieces that will make up your system. Your goal, over time, is to have all of these pieces running simultaneously, on different machines, with video evidence proving it.

## Stories/Rubric: 20 pts

For all of these components, you must be able to pass a NATS server URL on the command-line to specify where the NATS server is running.

### StockPublisher (5 pts)
The first component you will need to build is the StockPublisher. This component will connect to the NATS server and spin up three "stock markets": a StockMarketRunner that will create a set of stock symbols (the alphabetic short-codes for companies on a given stock market, like "MSFT" for Microsoft of "AMZN" for Amazon) and their starting prices, and then enter an infinite loop. While in that loop, it will wait for a short period of time. When it wakes up, it will pick a stock symbol at random, create a price "adjustment" anywhere from -$5 to +$5, and adjust the current price of that stock to that amount.

Your job will be spin up at least three stock markets (each represented by a StockMarketRunner) with roughly seven to ten stock symbols in each. You are free to have more, but you must have *at a minimum* three markets each with 7-10 stocks apiece. (Make sure the symbols are unique--each company can only be in one stock market inside your group.) Each of these StockMarketRunners must be started up inside their own Thread.

The StockPublisher will connect to the NATS server, and the static `publish` method on that class will do the work of publishing to NATS. Note that the method is marked `synchronized`: this is a simple way to ensure that your threads do not clobber each other while using it to publish messages.

Speaking of which, the messages your publisher sends out should use XML as the wire format and look like the following:

```
<message sent="(timestamp)">
  <stock>
    <name>(symbol)</name>
    <adjustment>(amount)</adjustment>
    <adjustedPrice>(price after adjustment)</adjustedPrice>
  </stock>
</message>
```

You are free to add additional XML elements to these messages if you wish, but the above must remain consistent in the face of any additions.

### StockMonitor (5 pts)

The StockMonitor is a message consumer that will listen to messages sent to the NATS server, and specifically it will either listen to all symbols published to the server, or it will listen for only those stock symbols passed on the command-line. That is, if the StockMonitor is run with command-line parameters of "MSFT", "AMZN" and "BLIZ", it will only listen for adjustments to those three stocks. Any stock symbols can be specified, regardless of which market they are in.

The StockMonitor must create "price log" files, one for each symbol it is tracking. A "price log" file is a text file, with each line consisting of the timestamp a message is received, the adjustment for that message, and the current price of the stock after adjustment. (In other words, the StockMonitor needs to log each message in a symbol-specific file.)

When you run StockMonitors in your video(s) (below), make sure to have one that watches all symbols, as well as a few that each watch some symbols (but not all).

### StockBroker (3 pts)

StockBrokers are the components that will work with StockBrokerClients, where each client will have a portfolio of stocks and a "strategy" as to when to buy or sell particular stocks. When the client wishes, they will send "buy" messages that look like the following:

```
<order><buy symbol="(name)" amount="(number of shares)" /></order>
```

... and a "sell" message will look similarly, except with a tag name of `sell` replacing `buy`.

The StockBroker must receive the message from one of their clients (and not intercept any client messages to other brokers!) and carry out the request. (In this particular implementation, "carry out the request" means "sleep for a few seconds".) Once the request is carried out, it must send a reponse to the client with the total amount in the order (either the cost of the buy, or the amount of the sell); in the response, the Broker must charge 10% of the total transaction, so a "buy" order response will need to include the 10% the Broker charges as its fee, and the "sell" order response will need to subtract the 10% the Broker charges as its fee. The responses look like the following:

```
<orderReceipt><(original order here)><complete amount="(amount after fee)" /></orderReceipt>
```

Thus, if the client sent an order of `<order><sell symbol="MSFT" amount="40" /></order>` and the current price at the time for MSFT stock is 5000 ($50), then the Broker sends back `<orderReceipt><sell symbol="MSFT" amount="40" /><complete amount="180000" /></orderReceipt>` (5000 * 40 = 200000, minus 200000 / 10 = 20000).

### StockBrokerClient (4 pts)

Lastly the StockBrokerClient is the other side of the broker/client relationship.

A client's portfolio file looks like the following:

```
<portfolio>
  <stock>(number of shares)</stock>
</portfolio>
```

and their strategy file looks like this:

```
```

* Create StockBrokerClient (4 pts)

    * StockBrokerClients take a NATS server URL and filename on the command-line
    * StockBrokerClients send subject:"trade:"+brokerName messages with buy/sell orders
    * filename is an XML file with current holdings and "buy/sell orders"
        * if price drops below symbol's "sell" order, send a SELL message
        * if price drops below symbol's "buy" order, send a BUY message
    * update/save portfolio after each receipt from StockBroker
    * stop and restart client (while keeping StockPublisher running)

### SEC (3 pts)

Meanwhile, the SEC (Securities and Exchange Commission) is always on the lookout for suspicious transactions, and transactions that are over $5000 are *always* suspicious. (Not really.) Write a `SEC` class that is able to see *all* of the client-broker orders, and write a line out to a file called "suspicions.log" that tracks the timestamp of the order, the client, the broker, the order sent, and the amount. (You don't need to stop the order, just log it--if the offline analysis determines there was any funny activity, the dedicated agents of the FBI will be happy to stop by either the client or the broker and have a chat.)

### Prove it!

For all of this, create a video or separate videos demonstrating that your implementations can achieve each of the following scenarios:

* one StockPublisher or multiple StockPublishers
* one StockMonitor or multiple StockMonitors
* one StockBroker with multiple StockBrokerClients using that one broker
* multiple StickBrokers each having multiple StockBrokerClients
* one SEC to keep a wary eye on everything

## Extra Credit

### Secure it! (3 pts)

Run NATS so that all communication is done over TLS and requires all message publishers and consumers to authenticate against the server.

### Format it! (3 pts)

Instead of using XML, pass all messages around using JSON instead. In your video, show your code sending XML messages and then JSON messages, and offer up your group's opinion as to which you prefer. Are there situations where one is better? 

### Interop it! (4 pts)

Work with another group. Have both of your StockMarketPublishers run against a single NATS server and see how your systems react. If your StockMarketPublishers each have the same symbol (such as each group is publishing "MSFT" changes), figure out ways to continue to work *without changing the stock symbol on either side*. (In other words, group A's StockBrokerClients with MSFT in their portfolio should only be seeing and reacting to group A's MSFT price changes even if group B also has a MSFT symbol.)

By the way, make sure that *either* group A's `SEC` *or* group B's `SEC` can see all the activity going on, regardless of which NATS server it is happening on--the SEC knows (and needs to know!) everything!
