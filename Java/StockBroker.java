import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import io.nats.client.*;
public class StockBroker {
    private String name;
    private Connection nc;

    public StockBroker(String name) {
        this.name = name;
        this.nc = Nats.connect("nats://localhost:4222");
        Dispatcher d = nc.createDispatcher((msg) -> {
            String data = new String(msg.getData(), StandardCharsets.UTF_8);

            // wait 2 second before process
            Thread.sleep(waiter.nextInt(2) * 1000);

            String response = handleRequest(data);
            nc.publish(name, "replyto", response.getBytes());
        });
    }

    public static String handleRequest(String data){
        boolean buy = true;
        String[] decode = data.split("\"");
        String symbol = decode[1];
        int nShare = Integer.parseInt(decode[3]);
        if(data.contains("sell")){
            buy = false;
        }
        String filename = "/logs" + symbol + ".txt";
        File file = new File(filename);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        String lastLine;
        while ((line = br.readLine()) != null) {
            lastLine = line;
        }
        String[] stock = lastLine.split(" ");
        int price = Integer.parseInt(stock[2]);
        return "<orderRecept><" + symbol + " amount = " + nShare + " /><complete amount=\"" + Math.round(price * nShare * 0.9)/100.0 + "\" /></orderReceipt>";
    }
}