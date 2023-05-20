import io.nats.client.*;

import java.io.*;
import java.sql.SQLOutput;

public class StockMonitor {
    public static void main(String[] args) throws Exception {
        String asdf = "";
        String logFilePath = ""; //path to log file 
        Connection nc = Nats.connect("nats://localhost:4222");
        NatsMessageLogger logger = new NatsMessageLogger(logFilePath);

        Dispatcher dispatchNAS = nc.createDispatcher((msg) -> {
            System.out.println("NASDAQ stock prices: " + new String(msg.getData()));
        });
        dispatchNAS.subscribe("NASDAQ"); //subscribing to entire stock market

        Dispatcher dispatchDOW = nc.createDispatcher((msg) -> {
            asdf = ("DOWJones stock prices: " + new String(msg.getData())); //save as var
        });
        dispatchDOW.subscribe("DOWJones"); //example names

        logger.logMessage(asdf);

        // Close the logger and connection
        logger.close();
        connection.close();
    }
}

public class NatsMessageLogger {
    private BufferedWriter writer;

    public NatsMessageLogger(String logFilePath) throws IOException {
        writer = new BufferedWriter(new FileWriter(logFilePath, true));
    }

    public void logMessage(String message) throws IOException {
        writer.write(message);
        writer.newLine();
        writer.flush();
    }

    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
