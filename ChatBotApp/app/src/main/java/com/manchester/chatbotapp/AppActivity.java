package com.manchester.chatbotapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AppActivity extends AppCompatActivity {

    // === *** Attributes *** === //

    /**
     * The last system long second that the activity has been taken place on. This is monitored
     * so that it can me measured when inactivity has occurred.
     */
    protected long lastActivity;

    /**
     * The max time allowed before ending the session. 2 minutes (120,000 milliseconds)
     */
    protected static final long MAX_TIME = 120000;

    // === *** Constructors *** === //

    /**
     * Overrides the constructor so sets up the current time millis.
     */
    public AppActivity() {
        super();

        lastActivity = System.currentTimeMillis();

        setupThread();

    }

    // === *** Methods *** === //

    /**
     * Sets up the watcher thread. This will watch the application and if it is ever inactive
     * for more time than MAX_TIME allows it will switch back to the main screen.
     */
    public void setupThread() {

        Thread watcher = new Thread(() -> {

            try {

                while (true) {
                    Thread.sleep(1000);

                    long current = System.currentTimeMillis();

                    long timeSince = current - lastActivity;

                    System.out.println(timeSince);

                    if (timeSince > MAX_TIME) {

                        Intent intent = new Intent(AppActivity.this, MainActivity.class); // Replace NewActivity with the target Activity
                        startActivity(intent);

                    }
                }

            } catch (Exception e) {
                // On a failure, it should switch back the new intent.
                Intent intent = new Intent(AppActivity.this, MainActivity.class); // Replace NewActivity with the target Activity
                startActivity(intent);
            }

        });

        watcher.start();

    }

    /**
     * Overrides the create method to show the graphics as they appear.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // === ENTIRE FRAME === //

        // Make the app fullscreen by removing the title bar and status bar
        requestWindowFeature(Window.FEATURE_NO_TITLE); // Remove title bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); // Remove status bar

        // Create a LinearLayout with horizontal orientation to split the screen into left and right frames
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.HORIZONTAL); // Horizontal orientation

        // === LEFT FRAME === //

        // Left frame: Image
        LinearLayout leftLayout = new LinearLayout(this);
        leftLayout.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)); // 50% width
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.sprite); // Set your image resource
        leftLayout.addView(imageView); // Add the ImageView to the left layout

        // === RIGHT FRAME === //

        // Right frame: TextBox and Button
        RelativeLayout rightLayout = new RelativeLayout(this);
        rightLayout.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)); // 50% width

        // Panel above the input
        LinearLayout panel = new LinearLayout(this);
        panel.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, 600)); // Example height for the panel
        panel.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light)); // Set background for panel

        // EditText for text input
        EditText editText = new EditText(this);
        editText.setHint("Speak aloud or enter text..."); // Hint text for input field
        RelativeLayout.LayoutParams editTextParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        editTextParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM); // Align to bottom
        editText.setLayoutParams(editTextParams);

        // Button to submit text
        Button submitButton = new Button(this);
        submitButton.setText("Submit");
        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM); // Align button to bottom next to EditText
        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT); // Align button to the right of EditText
        submitButton.setLayoutParams(buttonParams);

        // Add panel, EditText, and Button to the rightLayout
        rightLayout.addView(panel); // Panel above the input
        rightLayout.addView(editText); // Add the EditText at the bottom
        rightLayout.addView(submitButton); // Add the Button next to the EditText

        // Set the left and right layouts as children of the main layout
        mainLayout.addView(leftLayout);
        mainLayout.addView(rightLayout);

        // Set the LinearLayout as the content view
        setContentView(mainLayout);
    }
}
