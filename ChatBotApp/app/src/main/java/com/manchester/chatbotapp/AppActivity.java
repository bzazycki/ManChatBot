package com.manchester.chatbotapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.Manifest;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * The AppActivity Class. This activity contains everything within the actual app.
 * While standard it would be in good practice to break up the many components and
 * panels, there are not many different components here to actually keep track of,
 * so keeping it all together is the design. Different components are stored
 * as attributes of the activity.
 */
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

    /**
     * Stores the chat log so that it can be emailed if needed.
     */
    protected String chatLog = "";

    /**
     * The animation that is occurring. This will be changed depending
     * on methods that are called.
     */
    protected VideoView animation;

    /**
     * The listener can both listen to words and speak.
     */
    protected Listener listener;

    /**
     *
     */
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;


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
     * for more time than MAX_TIME allows it will switch back to the main screen. That is all
     * this thread does. This thread can be safely ignored as it does not interact with any
     * views, ony activities. Whenever the user touches something on the screen or interacts
     * with something then the "lastActivity" field should be updated with the system time.
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

                        finish(); // Ends this activity.

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

        this.listener = new Listener(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO_PERMISSION);
        }

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
        RelativeLayout leftLayout = new RelativeLayout(this);
        leftLayout.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)); // 50% width

        // Create a vertical LinearLayout to hold the VideoView and the button row
        LinearLayout verticalLayout = new LinearLayout(this);
        verticalLayout.setOrientation(LinearLayout.VERTICAL);
        verticalLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        // Create a horizontal LinearLayout for the buttons
        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        buttonLayout.setWeightSum(3);  // Ensure buttons take equal space

        // Create the first button
        Button endChatButton = new Button(this);
        endChatButton.setText("End Chat");
        endChatButton.setBackgroundColor(Color.parseColor("#FFD700"));
        endChatButton.setTextColor(getResources().getColor(android.R.color.black));
        endChatButton.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        endChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatDialog chatDialog = new ChatDialog(getThis());
                chatDialog.show();
            }
        });

        // Create the second button
        Button soundButton = new Button(this);
        soundButton.setText("Sound");
        soundButton.setBackgroundColor(getResources().getColor(android.R.color.black));
        soundButton.setTextColor(getResources().getColor(android.R.color.white));
        soundButton.setTextColor(getResources().getColor(android.R.color.white));
        soundButton.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        soundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener.allowSpeech) {
                    listener.allowSpeech = false;
                    soundButton.setTextColor(Color.RED);
                    listener.stopSpeaking();
                } else {
                    listener.allowSpeech = true;
                    soundButton.setTextColor(Color.WHITE);
                }
            }
        });


        // Create the third button
        Button settingsButton = new Button(this);
        settingsButton.setText("Settings");
        settingsButton.setBackgroundColor(Color.parseColor("#FFD700"));
        settingsButton.setTextColor(getResources().getColor(android.R.color.black));
        settingsButton.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        // Add the buttons to the horizontal button layout
        buttonLayout.addView(endChatButton);
        buttonLayout.addView(soundButton);
        buttonLayout.addView(settingsButton);

        // Take 1 part of the available space
        verticalLayout.addView(buttonLayout);

        // Create the VideoView
        this.animation = new VideoView(this);

        // Set the video source from the raw resource folder
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.beewave);
        animation.setVideoURI(videoUri);
        animation.start();
        animation.setOnCompletionListener(mp -> animation.start());

        verticalLayout.addView(animation);

        // Add the vertical layout (with VideoView and buttons) to the leftLayout
        leftLayout.addView(verticalLayout);

        // === RIGHT FRAME === //

        // Right frame: TextBox and Button
        RelativeLayout rightLayout = new RelativeLayout(this);
        rightLayout.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)); // 50% width

        // Panel above the input
        LinearLayout chatPanel = new LinearLayout(this);
        chatPanel.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)); // Example height for the panel
        chatPanel.setOrientation(LinearLayout.VERTICAL);
        chatPanel.setPadding(16, 16, 16, 16);

        ScrollView scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, this.getDisplay().getHeight() - 50));

        scrollView.addView(chatPanel);

        // The input panel.
        LinearLayout inputContainer = new LinearLayout(this);
        inputContainer.setOrientation(LinearLayout.HORIZONTAL);
        RelativeLayout.LayoutParams inputContainerParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        inputContainerParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        inputContainer.setLayoutParams(inputContainerParams);


        // The Edit Text, the input
        EditText editText = new EditText(this);
        editText.setHint("Enter your thoughts..."); // Hint text for input field
        editText.setBackgroundColor(getResources().getColor(android.R.color.white)); // White background
        editText.setPadding(16, 16, 16, 16);
        editText.setTextColor(getResources().getColor(android.R.color.black));
        editText.setHintTextColor(getResources().getColor(android.R.color.darker_gray));
        editText.setElevation(8); // Slight shadow for better visibility
        LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f); // Weight of 1 for proportional sizing
        editText.setLayoutParams(editTextParams);

        // Button to submit text
        Button submitButton = new Button(this);
        submitButton.setText("Send");
        submitButton.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
        submitButton.setTextColor(getResources().getColor(android.R.color.white));
        submitButton.setElevation(8); // Slight shadow
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonParams.setMargins(16, 0, 0, 0); // Add margin to separate from EditText
        submitButton.setLayoutParams(buttonParams);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get user input and clear the EditText
                String input = editText.getText().toString().trim();

                changeAnimation('t');

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

                    // Update the chat panel and speak the output on the main thread
                    runOnUiThread(() -> {
                        logChatOutput(chatPanel, output);
                        listener.speak(output);
                    });

                }).start();

                lastActivity = System.currentTimeMillis();

                changeAnimation('l');

                // Start listening for speech recognition asynchronously
                listener.listen(text -> {
                    if (text != null) {
                        Log.e("Listener", "Recognized text: " + text);

                        // Update the UI with recognized text
                        runOnUiThread(() -> logUserInput(chatPanel, "Recognized: " + text));
                    } else {
                        Log.e("Listener", "Failed to recognize speech.");
                    }
                });
            }
        });

        // Add panel, EditText, and Button to the rightLayout
        // Add the EditText at the bottom
        inputContainer.addView(editText); // Add the Button next to the EditText
        inputContainer.addView(submitButton); // Panel above the input
        rightLayout.addView(scrollView);
        rightLayout.addView(inputContainer);

        // Set the left and right layouts as children of the main layout
        mainLayout.addView(leftLayout);
        mainLayout.addView(rightLayout);

        // Set the LinearLayout as the content view
        setContentView(mainLayout);

        new Thread(() -> {
            try {
                while (!listener.tts.isSpeaking()) {
                    Thread.sleep(500);
                    this.listener.speak("How can I help you today?");
                }
                //String output = this.listener.listen();
                Log.e("TextToSpeech", "Listened");
            } catch (InterruptedException e) {

            }
        }).start();

    }

    /**
     * Changes the animation based on the character for the resource.
     * @param c the character that determines what resource to use.
     */
    public void changeAnimation(Character c) {
        Uri videoUri = null;
        switch (c) {
            case 'w':
                videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.beewave);
                break;
            case 't':
                videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.beetalk);
                break;
            case 'l':
                videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.beelisten);
                break;
        }

        // Stop current video
        animation.stopPlayback();

        // Set new video URI
        animation.setVideoURI(videoUri);

        // Start video and ensure loop
        animation.start();
        animation.setOnCompletionListener(mp -> animation.start());
    }

    /**
     * Gets the App Activity Object.
     * @return the reference to the object.
     */
    private AppActivity getThis() {
        return this;
    }

    /**
     * Adds the text box to the container.
     * @param container the container to add the user text to
     * @param text the text
     */
    private void logUserInput(LinearLayout container, String text) {
        // Create a bubble for the user's input
        TextView bubble = new TextView(this);
        bubble.setText(text);
        bubble.setPadding(20, 20, 20, 20);
        bubble.setBackgroundResource(R.drawable.userbubble); // Custom drawable for user bubble
        bubble.setTextColor(getResources().getColor(android.R.color.white));
        bubble.setTextSize(16);
        bubble.setElevation(4); // Slight shadow

        // Set layout parameters for the bubble
        LinearLayout.LayoutParams bubbleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        bubbleParams.setMargins(8, 8, 8, 8);
        bubbleParams.gravity = Gravity.END; // Align to the right for user input
        bubble.setLayoutParams(bubbleParams);

        // Add bubble to the container
        container.addView(bubble);

        // Update chat log
        chatLog += "You asked: " + text + "\n\n";
    }

    /**
     * Adds the text box to the container.
     * @param container the container to add the user text to
     * @param text the text
     */
    private void logChatOutput(LinearLayout container, String text) {
        // Create a bubble for the chatbot's output
        TextView bubble = new TextView(this);
        bubble.setText(text);
        bubble.setPadding(20, 20, 20, 20);
        bubble.setBackgroundResource(R.drawable.chatbubble); // Custom drawable for chatbot bubble
        bubble.setTextColor(getResources().getColor(android.R.color.black));
        bubble.setTextSize(16);
        bubble.setElevation(4); // Slight shadow

        // Set layout parameters for the bubble
        LinearLayout.LayoutParams bubbleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        bubbleParams.setMargins(8, 8, 8, 8);
        bubbleParams.gravity = Gravity.START; // Align to the left for chatbot response
        bubble.setLayoutParams(bubbleParams);

        // Add bubble to the container
        container.addView(bubble);

        // Update chat log
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
