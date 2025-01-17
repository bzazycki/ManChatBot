package com.manchester.chatbotapp;

import android.util.Log;

import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Backend_Functions contains a single static method that sends a request to the
 * server, and gets the string back. This string is parsed by the backend so that
 * it comes out how it is needed.
 */
public class Backend_Functions {

    /**
     * Sends a message to the backend API and gets the response back as a string.
     * @param history the history of the conversation being had by the user.
     * @param userMessage the message that the user would like to send to ChatGPT.
     * @return the response string, this is formatted by the backend.
     */
    public static String getChatResponse(String history, String userMessage) {
        String flaskURL = "http://34.236.100.216/chatbot";

        // A little easter egg
        if (userMessage.equalsIgnoreCase("miami university 2025")) {
            return "I was created by a team of computer scientists from Miami University in 2025!";
        }

        try {
            // Sets up the connection to the URL
            URL url = new URL(flaskURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setDoOutput(true);

            // The message that is to be sent to the backend.
            String messageToChat = "This is the history of the conversation that you have been " +
                    "having with the user: " + history + " -- This is what the user has said most "
                    + "recently: " + userMessage;

            // Formats this as a JSON.
            String jsonInputString = "{\"user_message\": \"" + messageToChat + "\"}";

            // Writes to the connection. Writing to the connection means that it is being
            // sent over the internet.
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Gets the input back from the connection.
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                return response.toString();
            }

            // If any error is thrown, log it for anyone after, and return that a message can
            // not be sent.
        } catch (Exception e) {
            Log.e("ChatGPT Request (Backend_Functions)", e.toString());
            return "I apologize for the inconvenience, but I am incapable of helping at this time";
        }
    }

}
