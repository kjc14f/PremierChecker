import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main {

    public static void main(String[] args) {
        new Main();
    }

    public Main() {
        System.out.println(makeRequest(""));
    }

    public String makeRequest(String parameter) {
        try {
            URL url = new URL("https://fantasy.premierleague.com/drf/fixtures/");
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

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
