package API;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

// 3.84.50.79 - IP Address

/**
 * Contains static methods that make calls to the backend to get a response from
 * the api.
 */
public class Request {

    /**
     * The IP address to make the request to.
     */
    private static final String IP_ADDRESS = "https://3.84.50.79";

    /**
     * Makes an HTTPS POST request to the backend server.
     * 
     * @param endpoint  the endpoint to call
     * @param jsonInput the JSON input string
     * @return the response from the server
     * @throws Exception if an error occurs during the request
     */
    public static String makePostRequest(String endpoint, String jsonInput) throws Exception {
        URL url = new URL(IP_ADDRESS + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInput.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int code = connection.getResponseCode();
        if (code != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + code);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString();
        }
    }
}
