package com.manchester.chatbotapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * This dialog manages the inactivity of the system. Whenever the system has not been
 * touched for two minutes, this dialog pops up. Then after 1 minute this dialog will
 * take actions of its own on the context. If the user selects quit it will popup the
 * ChatDialog panel, if they click cancel it will make this disappear, and if they
 * do nothing for the minute, it will close everything out.
 */
public class InactivityDialog extends Dialog {

    /**
     * The countdown timer. This is how long the inactivity timer will last before it force
     * closes the application.
     */
    private static final long COUNTDOWN_TIME = 60000; // 1 minute

    /**
     * How often the timer runs, which is every second. This is so that the timer will last
     * 60 seconds or 1 minute.
     */
    private static final long INTERVAL = 1000; // Update every second

    /**
     * The actual count down timer. Stored here so that it can be stopped and reset
     * when needed.
     */
    private CountDownTimer countdownTimer;

    /**
     * The message text view. This stores the text to tell the user how much time is left
     * on the countdown, and warns them that it is about to time out.
     */
    private TextView messageTextView;

    /**
     * Quit button. This allows the user to quit the app activity and return to the
     * main screen. Clicking this will pop up and allow the user to enter their
     * email before they leave
     */
    private Button quitButton;

    /**
     * The back button. Clicking this resets the inactivity timer, and then
     * dismisses this panel.
     */
    private Button backButton;

    /**
     * The AppActivity context. This context allows the dialog to interact with
     * the rest of the application, and close this context when needed.
     */
    private AppActivity context;

    /**
     * The chat log. This is only stored so that it can be passed to the
     * ChatDialog panel if the user would like to have it sent to them.
     */
    protected String chatLog;

    /**
     * The Constructor for InactivityDialog.
     * @param activity the activity
     * @param chatLog the chat log that the user has been using.
     */
    public InactivityDialog(AppActivity activity, String chatLog) {
        super(activity);
        this.context = activity;
        this.chatLog = chatLog;
    }

    /**
     * Overrides the onCreate to fill in the content of the dialog.
     * @param savedInstanceState If this dialog is being reinitialized after a
     *     the hosting activity was previously shut down, holds the result from
     *     the most recent call to {@link #onSaveInstanceState}, or null if this
     *     is the first time.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the custom layout for the dialog
        LayoutInflater inflater = LayoutInflater.from(getContext());  // Use LayoutInflater from the context
        View view = inflater.inflate(R.layout.dialog_inactivity, null);

        // Initialize the message text view and buttons
        messageTextView = view.findViewById(R.id.messageTextView);
        quitButton = view.findViewById(R.id.quit_button);
        backButton = view.findViewById(R.id.back_button);

        /*
         * Quit Button -> On the click of the button, start the chatDialog panel so that the
         * user can request the email sent to them, then dismisses this chat.
         */
        quitButton.setOnClickListener(v -> {
            ChatDialog chatDialog = new ChatDialog(context, chatLog); // Use the activity context to instantiate
            chatDialog.show(); // Show the chat dialog

            dismiss();
        });

        /*
         * The Back button simply dismisses the dialog panel.
         */
        backButton.setOnClickListener(v -> {
            dismiss();
        });

        // Set the dialog layout
        setContentView(view);

        // Set the dialog properties (optional, to control cancelable behavior)
        setCancelable(false);

        // Start the countdown timer
        startCountdown();
    }

    /**
     * Starts the countdown timer. This is so this panel is not always up. This panel eventually
     * needs to be shutdown and switch activity context. After the 1 minute timer, it will dismiss
     * itself, end the AppActivity context, and then start the MainActivity context.
     */
    private void startCountdown() {
        countdownTimer = new CountDownTimer(COUNTDOWN_TIME, INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsLeft = millisUntilFinished / 1000;
                // Update the message with the remaining time
                String timeLeft = String.format("Your session is about to expire! \n\n 00:%d",
                        secondsLeft);
                messageTextView.setText(timeLeft);

                // Timer has run out.
                if (secondsLeft <= 0) {
                    Intent intent = new Intent(context, MainActivity.class); // Replace 'NewActivity' with your target activity
                    context.startActivity(intent); // Use the context to start the activity
                    dismiss();
                    context.finish();
                    countdownTimer.cancel();
                }
            }

            /*
             *  The onfinish activity is handled in a different method.
             */
            @Override
            public void onFinish() {}
        };
        countdownTimer.start();
    }

    /**
     * Overrides the dismiss so that it can free up some resources, and
     * resets the timer so that it can pop back up later.
     */
    @Override
    public void dismiss() {
        // Cancel the countdown when dialog is dismissed
        if (countdownTimer != null) {
            countdownTimer.cancel();
            Log.i("Timer", "Countdown Stopped");
        }
        context.resetInactivityTimer();
        super.dismiss();
    }
}
