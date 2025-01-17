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

public class InactivityDialog extends Dialog {

    private static final long COUNTDOWN_TIME = 60000; // 1 minute
    private static final long INTERVAL = 1000; // Update every second
    private CountDownTimer countdownTimer;
    private TextView messageTextView;
    private Button quitButton;
    private Button backButton;

    private AppActivity context;

    protected String chatLog;

    // Constructor for InactivityDialog
    public InactivityDialog(AppActivity activity, String chatLog) {
        super(activity);
        this.context = activity;
        this.chatLog = chatLog;
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

            ChatDialog chatDialog = new ChatDialog(context, chatLog); // Use the activity context to instantiate
            chatDialog.show(); // Show the chat dialog

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
                long secondsLeft = millisUntilFinished / 1000;
                // Update the message with the remaining time
                String timeLeft = String.format("Your session is about to expire! \n\n 00:%d",
                        secondsLeft);
                messageTextView.setText(timeLeft);

                if (secondsLeft <= 0) {
                    Intent intent = new Intent(context, MainActivity.class); // Replace 'NewActivity' with your target activity
                    context.startActivity(intent); // Use the context to start the activity
                    dismiss();
                    context.finish();
                    countdownTimer.cancel();
                }
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
            Log.i("Timer", "Countdown Stopped");
        }
        context.resetInactivityTimer();
        super.dismiss();
    }

    // Interface for communication with the parent activity
    public interface InactivityDialogListener {
        void onQuitClicked(); // For showing chat dialog when quit is clicked
        void onBackClicked(); // For resetting inactivity timer when back is clicked
        void onInactivityTimeout(); // For transitioning back to main screen after inactivity
    }
}
