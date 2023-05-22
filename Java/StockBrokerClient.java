public class StockBrokerClient {

    //  StockBrokers are the components that will work with StockBrokerClients, where each client will have a por#olio of stocks and a "strategy" as to when to buy or sell par!cular stocks. StockBrokers are uniquely named (give each StockBroker a name constructor parameter that is used to iden!fy this StockBroker everywhere in the system), and clients choose which StockBroker they use. When the client wishes, they will send "buy" messages that look like the following:
    //  <order><buy symbol="(name)" amount="(number of shares)" /></order>
    //  ... and a "sell" message will look similarly, except with a tag name of sell replacing buy .
}
