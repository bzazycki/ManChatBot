package com.manchester.chatbotapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class    AppActivity extends AppCompatActivity {

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

    /**
     * Stores the chat log so that it can be emailed if needed.
     */
    protected String chatLog = "";

    /**
     * The animation that is occurring. This will be changed depending
     * on methods that are called.
     */
    protected VideoView animation;

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

                        break;

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
        leftLayout.setOrientation(LinearLayout.VERTICAL);
        this.animation = new VideoView(this);

        // Set the video source from the raw resource folder
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.beewave);
        animation.setVideoURI(videoUri);

        animation.start();
        animation.setOnCompletionListener(mp -> animation.start());

        leftLayout.addView(animation); // Add the ImageView to the left layout

        // === RIGHT FRAME === //

        // Right frame: TextBox and Button
        RelativeLayout rightLayout = new RelativeLayout(this);
        rightLayout.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)); // 50% width

        // Panel above the input
        LinearLayout chatPanel = new LinearLayout(this);
        chatPanel.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, 600)); // Example height for the panel
        chatPanel.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light)); // Set background for pane
        chatPanel.setOrientation(LinearLayout.VERTICAL);

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
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get user input and clear the EditText
                String input = editText.getText().toString().trim();

                if (input.isBlank() || input.isEmpty()) {
                    return;
                }

                editText.setText("");

                hideKeyboard(view);

                // Log user input on the chat panel
                logUserInput(chatPanel, input);

                // Run the network call on a background thread
                new Thread(() -> {
                    // Get the chat response from the backend
                    String output = Backend_Functions.getChatResponse(input);
                    //final String trimmed = output.substring(17, output.length() - 2);
                    final String trimmed = output.trim();
                    // Update the chat panel on the main thread
                    runOnUiThread(() -> logChatOutput(chatPanel, trimmed));
                }).start();
            }
        });

        // Add panel, EditText, and Button to the rightLayout
        rightLayout.addView(editText); // Add the EditText at the bottom
        rightLayout.addView(submitButton); // Add the Button next to the EditText
        rightLayout.addView(chatPanel); // Panel above the input


        // Set the left and right layouts as children of the main layout
        mainLayout.addView(leftLayout);
        mainLayout.addView(rightLayout);

        // Set the LinearLayout as the content view
        setContentView(mainLayout);
    }

    /**
     * Changes the animation based on the character for the resource.
     * @param c the character that determines what resource to use.
     */
    public void changeAnimation(Character c) {
        switch (c) {
            case 'w':
                Uri waveUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.beewave);
                animation.setVideoURI(waveUri);
                break;
            case 't':
                Uri talkUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.beetalk);
                animation.setVideoURI(talkUri);
                break;
            case 'l':
                Uri listenUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.beetalk);
                animation.setVideoURI(listenUri);
                break;
        }

        animation.start();
        animation.setOnCompletionListener(mp -> animation.start());
    }

    /**
     * Adds the text box to the container.
     * @param container the container to add the user text to
     * @param text the text
     */
    private void logUserInput(LinearLayout container, String text) {
        TextView input = new EditText(this);
        input.setText(text);
        container.addView(input);
        chatLog += "You asked: " + text + "\n\n";
    }

    /**
     * Adds the text box to the container.
     * @param container the container to add the user text to
     * @param text the text
     */
    private void logChatOutput(LinearLayout container, String text) {
        TextView input = new EditText(this);
        input.setText(text);
        container.addView(input);
        chatLog += "Chatbot responded: " + text + "\n\n";
    }

    /**
     * Hides the keyboard so that it does not popup whenever text is added.
     * @param view the view that is having the keyboard hidden.
     */
    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


}
