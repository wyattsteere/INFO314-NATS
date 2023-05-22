/**
 * Take the NATS URL on the command-line.
 */
import io.nats.client.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StockPublisher {
    public static void main(String... args) throws IOException, InterruptedException {
        String natsURL = "nats://localhost:4222";
        if (args.length > 0) {
            natsURL = args[0];
        }

        System.console().writer().println("Starting stock publisher....");

        StockMarket sm1 = new StockMarket(StockPublisher::publishMessage, "AMZN", "MSFT", "GOOG", "NASDAQ", "DOWJones", "JSLM", "JNDJ", "SMSG", "LG", "HUH");
        new Thread(sm1).start();
        StockMarket sm2 = new StockMarket(StockPublisher::publishMessage, "ACTV", "BLIZ", "ROVIO", "PE", "GRE", "KEA", "QER", "23RD", "CBRM", "SHBz");
        new Thread(sm2).start();
        StockMarket sm3 = new StockMarket(StockPublisher::publishMessage, "GE", "GMC", "FORD", "ASMD", "BRS", "TYT", "MSTG", "KIA", "HNDI", "FA");
        new Thread(sm3).start();
    }

    public synchronized static void publishDebugOutput(String symbol, int adjustment, int price) {
        System.console().writer().printf("PUBLISHING %s: %d -> %f\n", symbol, adjustment, (price / 100.f));
    }
    // When you have the NATS code here to publish a message, put "publishMessage" in
    // the above where "publishDebugOutput" currently is
    public synchronized static void publishMessage(String symbol, int adjustment, int price){
        try {
            Connection nc = Nats.connect("nats://localhost:4222");
            String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss"));
            String xml = "<message sent=\"" + timeStamp + "\">\n" +
                    "   <stock>\n" +
                    "       <name>" + symbol + "</name>\n" +
                    "       <adjustment>" + adjustment + "</adjustment>\n" +
                    "       <adjustedPrice>" + price + "</adjustedPrice>\n" +
                    "   </stock>\n" +
                    "</message>";
            nc.publish(symbol, xml.getBytes());
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    } 
}