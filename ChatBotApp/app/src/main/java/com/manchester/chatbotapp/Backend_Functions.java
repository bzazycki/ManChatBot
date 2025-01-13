package com.manchester.chatbotapp;

import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class Backend_Functions {

    public static String getChatResponse(String userMessage) {
        String flaskURL = "http://34.236.100.216/chatbot";
        try {
            URL url = new URL(flaskURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            //connection.setRequestProperty("Authorization", "auth123");
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
            StackTraceElement[] stack = e.getStackTrace();
            String stackTrace = "";
            for (StackTraceElement elem : stack) {
                stackTrace += elem.toString() + "\n";
            }
            return stackTrace;
            //return "An error occurred while getting the chat response.";
        }
    }
}
