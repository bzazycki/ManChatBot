package com.manchester.chatbotapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * ChatDialog is used to allow the user to send themselves and email of the
 * entire chat history if they want to receive it. This makes use of the
 * Emailer class.
 */
public class ChatDialog extends Dialog {

    /**
     * The App Activity context. Required so that AppActivity
     * can be shut down and a new activity can start when needed.
     */
    private final AppActivity context;

    /**
     * The message being sent. This message is stored so that if the user
     * decides that they would like their chat log emailed to them, it can
     * be passed into the emailing service.
     */
    protected String message;

    /**
     * Creates the chat log. Stores the required fields. This by default
     * will call onCreate so that it pops up on the screen.
     * @param c the AppActivity context.
     * @param message the chatLog so that it can be sent to the emailer.
     */
    public ChatDialog(AppActivity c, String message) {
        super(c, R.style.DialogTheme);
        this.context = c;
        this.message = message;
    }

    /**
     * Overrides the onCreate so that it is customized. The entire dialog
     * creation can be found in this method.
     * @param savedInstanceState If this dialog is being reinitialized after a
     *     the hosting activity was previously shut down, holds the result from
     *     the most recent call to {@link #onSaveInstanceState}, or null if this
     *     is the first time.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_chat); // Use the dialog layout with rounded edges

        // Set background drawable for rounded edges (transparent window)
        if (getWindow() != null) {
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Initialize views. This uses resource information to format
        // the buttons so that it is generated easier and with more design.
        final EditText emailInput = findViewById(R.id.email_input);
        Button sendButton = findViewById(R.id.send_button);
        Button cancelButton = findViewById(R.id.back_button);
        Button quitButton = findViewById(R.id.quit_button);

        // Handle button clicks
        sendButton.setOnClickListener(new View.OnClickListener() {

            /*
             * When the sendButton is clicked, it will create a new thread and attempt to send
             * the email. After the email is sent it will close the AppActivity and start
             * the new activity.
             */
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();
                Toast.makeText(context, "Email sent to: " + email, Toast.LENGTH_SHORT).show();
                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    new Thread(() -> {
                        Emailer.sendEmail(email, message);
                        Intent intent = new Intent(context, MainActivity.class); // Replace 'NewActivity' with your target activity
                        context.startActivity(intent); // Use the context to start the activity
                        context.finish();
                        dismiss();

                    }).start();
                } else {
                    Toast.makeText(context, "Invalid email. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*
         * On cancel, simply removes the dialog.
         */
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        /*
         * On the quit button, will dismiss this, and start the main activity without
         * sending the email.
         */
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When the image is clicked, start a new Activity
                // Start a new activity
                Intent intent = new Intent(context, MainActivity.class); // Replace 'NewActivity' with your target activity
                context.startActivity(intent); // Use the context to start the activity
                dismiss();
            }
        });
    }
}
