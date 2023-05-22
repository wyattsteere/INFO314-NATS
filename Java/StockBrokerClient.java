import io.nats.client.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import java.util.*;

public class StockBrokerClient {
    private String portfolio;
    private String strategy;
    private ArrayList<String> subs = new ArrayList<String>();
    private static Map<String, String> map;

    public StockBrokerClient(String pfile, String sfile){
        map = new HashMap<String, String>();
        try {
            String filename = "Clients/" + pfile;
            File file = new File(filename);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            String port = "";
            while ((line = br.readLine()) != null) {
                port += line;
            }

            String fs = "Clients/" + sfile;
            File f = new File(fs);
            BufferedReader br2 = new BufferedReader(new FileReader(f));
            String l;
            String strat = "";
            while ((l = br2.readLine()) != null) {
                strat += l;
            }
            this.portfolio = port;
            this.strategy = strat;

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        String pfile = "portfolio-1.xml";
        String sfile = "strategy-1.xml";
        StockBrokerClient sc = new StockBrokerClient(pfile, sfile);
        sc.run();
    }

    public void run() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document doc = builder.parse(new InputSource(new StringReader(this.portfolio)));
            Element root = doc.getDocumentElement();
            String[] find = portfolio.split("\"");
            String [] temp = new String[30];
            for(int i = 1; i < find.length; i+=2) {
                subs.add(find[i]);
                temp[(i-1)/2] = find[i];
            }
            String[] stock = new String[subs.size()];
            for(int i = 0; i < subs.size(); i++) {
                System.out.println(temp[i]);
                 map.put(temp[i], root.getElementsByTagName("stock").item(i).getTextContent());
            }
            System.out.println(map.get("MSFT"));

            Connection nc = Nats.connect("nats://localhost:4222");
            Dispatcher dis = nc.createDispatcher((msg)-> {
                try{ 
                    String response = new String(msg.getData());
                    Document di = builder.parse(new InputSource(new StringReader(response)));
                    Element ri = di.getDocumentElement();
                    String name = ri.getElementsByTagName("name").item(0).getTextContent();
                    String price = ri.getElementsByTagName("adjustedPrice").item(0).getTextContent();
                } catch (Exception e) {
                    System.out.println(e);
                }
               

            });

        } catch (Exception e) {
            System.out.println(e);
        }
        
    }
}