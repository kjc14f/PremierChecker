import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Year;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    private static final String HISTORY_URL = "https://fantasy.premierleague.com/api/element-summary/";
    private static final String YEAR = Year.now().toString();

    public static void main(String[] args) {
        new Main();
    }

    public Main() {
        initiateRequests();
    }

    private void initiateRequests() {

        AtomicInteger atomicInteger = new AtomicInteger(1);

        for (int i = 0; i < 6; i ++) {
            new Thread(() -> {
                while (true) {
                    int myI = atomicInteger.getAndIncrement();
                    System.out.println("Request no: " + myI);
                    try {
                        String content = makeRequest(myI);
                        writeToFile(content, myI);
                    } catch (FileNotFoundException e) {
                        System.out.println("Stopping thread");
                        break;
                    }
                }
            }).start();
        }

    }

    private String makeRequest(int i) throws FileNotFoundException {

        try {

            URL url = new URL(HISTORY_URL + i);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

            return content.toString();

        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    private void writeToFile(String buffer, int i) {

        File file = new File("src/main/resources/pre-season/" + YEAR + "/" + i + ".json");

        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(buffer);
            bufferedWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
