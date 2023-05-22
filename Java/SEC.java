import io.nats.client.*;

import java.io.*;
import java.sql.SQLOutput;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.io.BufferedWriter;

public class SEC {
    private static ArrayList<String> symbols = new ArrayList<String>();
    public SEC(String... args) {
        for(int i = 0; i < args.length; i++) {
            symbols.add(args[i]);
        }
    }
    public static void main(String[] args) throws Exception {
        StockMonitor SEC = new StockMonitor(args);
        SEC.start();
    }
    public static void start() {
        try {
            Connection nc = Nats.connect("nats://localhost:4222");
            Dispatcher d = nc.createDispatcher((msg) -> {
                String message = new String(msg.getData());
                suspiciousActivity(message);
            });

            d.subscribe(">");
            while (true) {
                Thread.sleep(500);
            }
        } catch(Exception e) {

        }
    }

    public static void suspiciousActivity(String message){
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document doc = builder.parse(new InputSource(new StringReader(message)));
            Element root = doc.getDocumentElement();
            String price = root.getElementsByTagName("adjustedPrice").item(0).getTextContent();
            String[] date = message.split("\"");
            String time = date[1];
            String filename = "logs/" + "suspicions" + ".txt";
            int intPrice = Integer.parseInt(price);

            File f = new File(filename);
            if(!f.exists()){
                f.createNewFile();
            }
            if (intPrice >= 5000) {
                FileWriter fw = new FileWriter(filename, true);
                BufferedWriter bw = new BufferedWriter(fw);
                String data = "SUSPICIOUS TRADE ALERT, Stock Broker: " + StockBroker.name + " completed trade for: " + StockBrokerClient.name +
                        "at " + time + " for a dollar amount of " + price + "\n";
                bw.write(data);
                bw.close();
            }
        }catch (Exception e) {
            System.out.println(e);
        }
    }
}


}
