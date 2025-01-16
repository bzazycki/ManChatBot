package com.manchester.chatbotapp;

import android.app.Dialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class InactivityDialog extends Dialog {

    private static final long COUNTDOWN_TIME = 60000; // 1 minute
    private static final long INTERVAL = 1000; // Update every second
    private CountDownTimer countdownTimer;
    private TextView messageTextView;
    private Button quitButton;
    private Button backButton;

    // Constructor for InactivityDialog
    public InactivityDialog(AppActivity activity) {
        super(activity);
    }

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

        // Set up the button listeners
        quitButton.setOnClickListener(v -> {
            if (getContext() instanceof InactivityDialogListener) {
                ((InactivityDialogListener) getContext()).onQuitClicked();
            }
            dismiss();
        });

        backButton.setOnClickListener(v -> {
            if (getContext() instanceof InactivityDialogListener) {
                ((InactivityDialogListener) getContext()).onBackClicked();
            }
            dismiss();
        });

        // Set the dialog layout
        setContentView(view);

        // Set the dialog properties (optional, to control cancelable behavior)
        setCancelable(false);

        // Start the countdown timer
        startCountdown();
    }

    private void startCountdown() {
        countdownTimer = new CountDownTimer(COUNTDOWN_TIME, INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update the message with the remaining time
                String timeLeft = String.format("You have been inactive for %d seconds. Resetting in %d seconds.",
                        (COUNTDOWN_TIME / 1000) - (millisUntilFinished / 1000),
                        millisUntilFinished / 1000);
                messageTextView.setText(timeLeft);
            }

            @Override
            public void onFinish() {
                // Trigger the reset action after countdown finishes
                if (getContext() instanceof InactivityDialogListener) {
                    ((InactivityDialogListener) getContext()).onInactivityTimeout();
                }
            }
        };
        countdownTimer.start();
    }

    @Override
    public void dismiss() {
        // Cancel the countdown when dialog is dismissed
        if (countdownTimer != null) {
            countdownTimer.cancel();
        }
        super.dismiss();
    }

    // Interface for communication with the parent activity
    public interface InactivityDialogListener {
        void onQuitClicked(); // For showing chat dialog when quit is clicked
        void onBackClicked(); // For resetting inactivity timer when back is clicked
        void onInactivityTimeout(); // For transitioning back to main screen after inactivity
    }
}
