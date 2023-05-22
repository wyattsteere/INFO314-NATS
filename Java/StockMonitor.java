import io.nats.client.*;

import java.util.concurrent.atomic.AtomicReference;

public class StockMonitor {
    public static void main(String[] args) throws Exception {
        AtomicReference<String> tempString = new AtomicReference<>("");
        String logFilePath = "test.txt"; //path to log file
        Connection nc = Nats.connect("nats://localhost:4222");
        NatsMessageLogger logger = new NatsMessageLogger(logFilePath);

        Dispatcher dispatchNAS = nc.createDispatcher((msg) -> {
            System.out.println("NASDAQ stock prices: " + new String(msg.getData()));
        });
        dispatchNAS.subscribe("NASDAQ"); //subscribing to entire stock market

        Dispatcher dispatchDOW = nc.createDispatcher((msg) -> {
            AtomicReference<String> tempAtomicReference = new AtomicReference<>("DOWJones stock prices: " + new String(msg.getData()));
            // Convert tempString to atomic
            tempString.set(tempAtomicReference.get());
        });
        dispatchDOW.subscribe("DOWJones"); //example names

        logger.logMessage(tempString.get());

        // Close the logger and connection
        logger.close();
        nc.close();
    }
}

