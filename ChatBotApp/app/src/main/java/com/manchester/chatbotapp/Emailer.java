package com.manchester.chatbotapp;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * This class handles all of the required emailing that is used for the chat history. This class
 * uses a simple http request to the server, where the server then handles the SMTP.
 */
public class Emailer  {

    /**
     * Makes a call to the server to send an email to the user. Will return
     * if the email successfully sent.
     * @return True - The message was sent to the server. False - Failed
     */
    public static boolean sendEmail(String emailAddress, String message) {
        String flaskURL = "http://34.236.100.216/sendEmail";
        try {
            URL url = new URL(flaskURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            //connection.setRequestProperty("Authorization", "Auth123");
            //connection.setRequestProperty("Location", "Manchester");
            connection.setDoOutput(true);

            String jsonInputString = String.format("{\"email\": \"%s\", \"message\": \"%s\"}", emailAddress, message);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            } catch (Exception e) {
                Log.e("Emailer", e.toString());
                return false;
            }

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                Log.e("Emailer", response.toString());
            }

            Log.i("Emailer", "Email Supposedly sent successfully sent to " + emailAddress);

        } catch (Exception e) {
            StackTraceElement[] stackTrace = e.getStackTrace();
            String stack = "";
            for (StackTraceElement s : stackTrace) {
                stack += s.toString() + "\n";
            }
            Log.e("Emailer", stack);
            return false;
        }

        return true;
    }



}
