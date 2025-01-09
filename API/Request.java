package API;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Request {

    private static final String IP_ADDRESS = "http://34.236.100.216:80/chatbot";

    private static final String AUTH_CODE = "authcode";

    public static String makeRequest(String input) {
        try {
            URL url = new URL(IP_ADDRESS);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + AUTH_CODE);
            connection.setDoOutput(true);

            String jsonInputString = "{\"input\": \"" + input + "\"}";

            try (OutputStream os = connection.getOutputStream()) {
                byte[] inputBytes = jsonInputString.getBytes("utf-8");
                os.write(inputBytes, 0, inputBytes.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    return response.toString();
                }
            } else {
                return "ERROR: " + responseCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "FATAL ERROR";
        }
    }

    public static void main(String[] args) {
        System.out.println(makeRequest("Hello"));
    }
}