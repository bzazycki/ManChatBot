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
 * This class handles all of the required emailing that is used for the chat history.
 */
public class Emailer  {

    /**
     * Makes a call to the server to send an email to the user. Will return
     * if the email successfully sent.
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
                return false;
            }

        } catch (Exception e) {
            StackTraceElement[] stackTrace = e.getStackTrace();
            String stack = "";
            for (StackTraceElement s : stackTrace) {
                stack += s.toString() + "\n";
            }
            return false;
        }

        return true;
    }



}
