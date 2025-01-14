import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ChatDialog extends AppActivity {

    private void showEmailDialog() {
        // Create EditText to input email
        final EditText emailInput = new EditText(this);
        emailInput.setHint("Enter your email");

        // Create the dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Email")
                .setView(emailInput) // Set the EditText view in the dialog
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = emailInput.getText().toString().trim();
                        if (!TextUtils.isEmpty(email) && email.contains("@")) {
                            // Handle email send logic here
                            Toast.makeText(MainActivity.this, "Email sent to: " + email, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Invalid email. Please try again.", Toast.LENGTH_SHORT).show();
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
                        finish(); // Close the app
                    }
                });

        // Show the dialog
        builder.create().show();
    }
}
