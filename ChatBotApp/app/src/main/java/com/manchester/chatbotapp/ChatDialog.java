package com.manchester.chatbotapp;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.manchester.chatbotapp.AppActivity;
import com.manchester.chatbotapp.MainActivity;

public class ChatDialog extends Dialog {

    private Context c;

    public ChatDialog(Context c) {
        super(c);
        this.c = c;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Create EditText to input email
        final EditText emailInput = new EditText(c);
        emailInput.setHint("Enter your email");

        // Create the dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("Enter Email")
                .setView(emailInput) // Set the EditText view in the dialog
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = emailInput.getText().toString().trim();
                        if (!TextUtils.isEmpty(email) && email.contains("@")) {
                            // Handle email send logic here
                            Toast.makeText(c, "Email sent to: " + email, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(c, "Invalid email. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel(); // Simply dismiss the dialog
                    }
                })
                .setNeutralButton("Quit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss(); // Close the app
                    }
                });

        // Show the dialog
        builder.create().show();
    }

    private ChatDialog getThis() {
        return this;
    }
}
