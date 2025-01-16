package com.manchester.chatbotapp;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ChatDialog extends Dialog {

    private Context c;

    protected String message;

    public ChatDialog(AppActivity c, String message) {
        super(c, R.style.DialogTheme);
        this.c = c;
        this.message = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_chat); // Use the dialog layout with rounded edges

        // Set background drawable for rounded edges (transparent window)
        if (getWindow() != null) {
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Initialize views
        final EditText emailInput = findViewById(R.id.email_input);
        Button sendButton = findViewById(R.id.send_button);
        Button cancelButton = findViewById(R.id.back_button);
        Button quitButton = findViewById(R.id.quit_button);

        // Handle button clicks
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();
                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(c, "Email sent to: " + email, Toast.LENGTH_SHORT).show();
                    new Thread(() -> {
                        Emailer.sendEmail(email, message);
                    }).start();
                } else {
                    Toast.makeText(c, "Invalid email. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When the image is clicked, start a new Activity
                // Start a new activity
                Intent intent = new Intent(c, MainActivity.class); // Replace 'NewActivity' with your target activity
                c.startActivity(intent); // Use the context to start the activity
                dismiss();
            }
        });
    }

    // Utility to show this dialog
    public void showDialog() {
        this.show();
    }
}
