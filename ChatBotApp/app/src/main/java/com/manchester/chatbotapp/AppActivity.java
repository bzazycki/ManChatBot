package com.manchester.chatbotapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.content.pm.PackageManager;
import android.Manifest;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
     * The code to ensure that the application has access to the microphone.
     * If it does not have access to the microphone then it needs to ask for
     * it.
     */
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    /**
     * The Inactivity timeout. Lets the System timeout after this amount of time
     * before the end chat dialog appears.
     */
    private static final long INACTIVITY_TIMEOUT = 2 * 60 * 1000; // 2 minutes

    /**
     * The Inactivity handler. This works with the inactivity runnable to ensure
     * that the system can time out when it has not been used in a while.
     */
    private Handler inactivityHandler;
    private Runnable inactivityRunnable;

    private EditText userTextInput;


    // === *** Constructors *** === //

    /**
     * Overrides the constructor so sets up the current time millis.
     */
    public AppActivity() {
        super();

        lastActivity = System.currentTimeMillis();

    }

    // === *** Methods *** === //

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

        inactivityHandler = new Handler();
        inactivityRunnable = new Runnable() {
            public void run() {
                showInactivityDialog();
            }
        };

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
        buttonLayout.setWeightSum(2);  // Ensure buttons take equal space

        // Create the first button
        Button endChatButton = new Button(this);
        endChatButton.setText("END CHAT");
        endChatButton.setBackground(getDrawable(R.drawable.rounded_button));
        endChatButton.setTextColor(getResources().getColor(android.R.color.white));
        endChatButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

        // Set smaller size and margins
        LinearLayout.LayoutParams endChatParams = new LinearLayout.LayoutParams(
                dpToPx(120), dpToPx(48)); // Smaller size (width: 120dp, height: 48dp)
        endChatParams.setMargins(dpToPx(16), dpToPx(8), dpToPx(16), dpToPx(8)); // Add margins
        endChatButton.setLayoutParams(endChatParams);

        endChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatDialog chatDialog = new ChatDialog(getThis());
                chatDialog.show();
            }
        });

        // Create the ImageView for sound toggle
        final ImageView soundImageView = new ImageView(this);

        // Set the initial image for the sound icon
        soundImageView.setImageResource(R.drawable.volume_on);

        // Add space between buttons by adding padding or margins
        LinearLayout.LayoutParams soundButtonParams = new LinearLayout.LayoutParams(
                dpToPx(48), dpToPx(48)); // Adjust size
        soundButtonParams.setMargins(dpToPx(16), 0, 0, 0); // Add left margin for spacing
        soundImageView.setLayoutParams(soundButtonParams);

        // Set onClickListener to toggle sound state
        soundImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener.allowSpeech) {
                    listener.allowSpeech = false;
                    soundImageView.setImageResource(R.drawable.volume_off);
                    listener.stopSpeaking();
                } else {
                    listener.allowSpeech = true;
                    soundImageView.setImageResource(R.drawable.volume_on);
                }
            }
        });

        // Add the buttons to the horizontal button layout
        buttonLayout.addView(endChatButton);

        // Add the ImageView to the buttonLayout
        buttonLayout.addView(soundImageView);

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
        userTextInput = new EditText(this);
        userTextInput.setHint("Enter your thoughts..."); // Hint text for input field
        userTextInput.setBackgroundColor(getResources().getColor(android.R.color.white)); // White background
        userTextInput.setPadding(16, 16, 16, 16);
        userTextInput.setTextColor(getResources().getColor(android.R.color.black));
        userTextInput.setHintTextColor(getResources().getColor(android.R.color.darker_gray));
        userTextInput.setElevation(8); // Slight shadow for better visibility
        LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f); // Weight of 1 for proportional sizing
        userTextInput.setLayoutParams(editTextParams);

        // Button to submit text
        Button submitButton = new Button(this);
        submitButton.setText("➤");
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
                String input = userTextInput.getText().toString().trim();

                changeAnimation('t');

                if (input.isBlank() || input.isEmpty()) {
                    return;
                }

                userTextInput.setText("");

                hideKeyboard(view);

                // Log user input on the chat panel
                logUserInput(chatPanel, input);

                // Run the network call on a background thread
                new Thread(() -> {
                    // Get the chat response from the backend
                    String output = Backend_Functions.getChatResponse(chatLog, input);

                    // Update the chat panel and speak the output on the main thread
                    runOnUiThread(() -> {
                        logChatOutput(chatPanel, output);
                        listener.speak(output);
                    });

                }).start();

                lastActivity = System.currentTimeMillis();

                changeAnimation('l');

            }
        });

        Button listenButton = new Button(this);
        listenButton.setText("\uD83C\uDFA4");
        listenButton.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
        listenButton.setTextColor(getResources().getColor(android.R.color.white));
        listenButton.setElevation(8); // Slight shadow
        LinearLayout.LayoutParams lButtonParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonParams.setMargins(16, 0, 0, 0); // Add margin to separate from EditText
        listenButton.setLayoutParams(lButtonParam);
        listenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startListening();
            }

        });


        // Add panel, EditText, and Button to the rightLayout
        // Add the EditText at the bottom
        inputContainer.addView(userTextInput); // Add the Button next to the EditText
        inputContainer.addView(submitButton); // Panel above the input
        inputContainer.addView(listenButton);
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

        resetInactivityTimer();
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
     * Starts listening and then puts the text into the userInputText
     */
    private void startListening() {
        // Start listening for speech recognition asynchronously
        listener.listen(text -> {
            if (text != null) {
                Log.e("Listener", "Recognized text: " + text);

                // Update the UI with recognized text
                userTextInput.setText(text);
            } else {
                Log.e("Listener", "Failed to recognize speech.");
            }
        });
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
        chatLog += "User asked: " + text + "   ";
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
        chatLog += "Chat responded: " + text + "   ";
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

    // Resets timer on any user interaction
    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        resetInactivityTimer();
    }

    // Removes any pending callbacks and reschedule
    private void resetInactivityTimer() {
        inactivityHandler.removeCallbacks(inactivityRunnable);
        inactivityHandler.postDelayed(inactivityRunnable,INACTIVITY_TIMEOUT);
    }

    // Shows a dialog to alert user
    private void showInactivityDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Inactivity Alert")
                .setMessage("You have been inactive for 2 minutes.")
                .setPositiveButton("Okay", (dialog, which) -> {
                    // Reset the timer when dialog is dismissed
                    resetInactivityTimer();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    /**
     * On the destruction of this, remove all of the callbacks from the
     * inactivity handler so that it does not popup after the activity
     * has ended. This is an overridden method from AppCompatActivity.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up the handler to avoid memory leaks
        inactivityHandler.removeCallbacks(inactivityRunnable);
    }

    // Helper method to convert dp to pixels
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
