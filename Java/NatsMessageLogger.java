import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

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
}