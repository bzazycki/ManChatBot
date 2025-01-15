package com.manchester.chatbotapp;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

/**
 * This class handles all of the required emailing that is used for the chat history.
 */
public class Emailer extends Activity {

    /**
     * The email address that is being sent to.
     */
    private String email;

    /**
     * The message that is sent with the email. This will be the chatlog history.
     */
    private String message;

    /**
     * The subject of the email that is being sent.
     */
    private static final String SUBJECT = "Chatbot Message Log";

    /**
     * The Emailer Constructor.
     * @param email the email address.
     * @param message the message to be sent.
     */
    public Emailer(String email, String message) {
        super();
        this.email = email;
        this.message = message;

    }

    /**
     * When This is created it will send the email then destroy itself.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sendEmail();

        finish();
    }

    /**
     * Does all of the required work to send the email using the native Android libraries.
     */
    private void sendEmail() {

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);

        emailIntent.setData(Uri.parse("mailto:"));

        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {email});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, SUBJECT);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        if (emailIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(emailIntent);
        } else {
            Log.e("Emailer", "No email apps installed on this devce");
        }

    }


}
