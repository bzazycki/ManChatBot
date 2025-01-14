package com.manchester.chatbotapp;



import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ChatDialog extends Dialog {

    private Context c;

    public ChatDialog(Context c) {
        super(c);
        this.c = c;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_chat); // Define a layout for your custom dialog

        // Initialize views
        final EditText emailInput = findViewById(R.id.email_input);
        Button sendButton = findViewById(R.id.send_button);
        Button cancelButton = findViewById(R.id.cancel_button);
        Button quitButton = findViewById(R.id.quit_button);

        // Handle button clicks
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();
                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(c, "Email sent to: " + email, Toast.LENGTH_SHORT).show();
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
                ((AppCompatActivity) c).finish();
                System.exit(0);
            }
        });
    }
}
