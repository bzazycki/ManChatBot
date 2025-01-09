package API;

import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class Backend_Functions {

    public static void clearMemory() {
        try {
            FileWriter writer = new FileWriter("history.txt", false);
            writer.write("");
            writer.close();
            System.out.println("Memory cleared successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while clearing memory.");
            e.printStackTrace();
        }
    }


    public static String getChatResponse(String userMessage) {
        try {
            URL url = new URL("http://34.236.100.216:443");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            String jsonInputString = "{\"user_message\": \"" + userMessage + "\"}";

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                return response.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "An error occurred while getting the chat response.";
        }
    }

    public static void main(String[] args) {
        System.out.println(getChatResponse("Hello ChatGPT, tell me anything!"));
    }
}
