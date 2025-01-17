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

    public static String getChatResponse(String history, String userMessage) {
        String flaskURL = "http://34.236.100.216/chatbot";

        if (userMessage.toLowerCase().contains("miami university 2025")) {
            return "Yo yo yo what is up my slime!!!";
        }

        try {
            URL url = new URL(flaskURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            //connection.setRequestProperty("Authorization", "Auth123");
            //connection.setRequestProperty("Location", "Manchester");
            connection.setDoOutput(true);

            String messageToChat = "This is the history of the conversation that you have been " +
                    "having with the user: " + history + " -- This is what the user has said most "
                    + "recently: " + userMessage;

            String jsonInputString = "{\"user_message\": \"" + messageToChat + "\"}";

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
            StackTraceElement[] stackTrace = e.getStackTrace();
            String stack = "";
            for (StackTraceElement s : stackTrace) {
                stack += s.toString() + "\n";
            }
            return "I apologize for the inconvenience, but I am incapable of helping at this time";
            //return "An error occurred while getting the chat response.";
        }
    }

}
