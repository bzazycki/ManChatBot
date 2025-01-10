package com.manchester.chatbotapp;
/*
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.content.Intent;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class Listener {

    private TextToSpeech tts;
    private SpeechRecognizer speechRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Views
        inputText = findViewById(R.id.input_text);
        sendButton = findViewById(R.id.send_button);
        voiceInputButton = findViewById(R.id.voice_input_button);
        chatScrollView = findViewById(R.id.chat_scroll_view);
        chatLog = findViewById(R.id.chat_log);

        // Check and request permission for RECORD_AUDIO
        if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.RECORD_AUDIO}, 1);
        }

        // Initialize Text-to-Speech
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                Locale ukLocale = new Locale("en", "GB");
                tts.setLanguage(ukLocale);
                tts.setPitch(1.0f); // Normal pitch
                tts.setSpeechRate(0.8f); // Slower speech rate
            }
        });

        // Initialize Speech Recognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                Toast.makeText(MainActivity.this, "Listening...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBeginningOfSpeech() {
                // Optional: Add visual feedback to indicate speech has started
            }

            @Override
            public void onRmsChanged(float rmsdB) {
                // Optional: Add visual feedback for mic input level
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
            }

            @Override
            public void onEndOfSpeech() {
                Toast.makeText(MainActivity.this, "Processing speech...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int error) {
                Toast.makeText(MainActivity.this, "Speech recognition error: " + error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    inputText.setText(matches.get(0));
                } else {
                    Toast.makeText(MainActivity.this, "No speech recognized", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
            }
        });

        // Set up Send Button
        sendButton.setOnClickListener(v -> {
            String text = inputText.getText().toString();
            if (!text.isEmpty()) {
                addChatEntry(text);
                inputText.setText("");
            }
        });

        // Set up Voice Input Button
        voiceInputButton.setOnClickListener(v -> startVoiceInput());
    }

    private String startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.UK);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...");
        speechRecognizer.startListening(intent);
    }

    private void addChatEntry(String text) {
        // Create a new chat entry
        LinearLayout chatEntry = new LinearLayout(this);
        chatEntry.setOrientation(LinearLayout.HORIZONTAL);

        TextView chatTextView = new TextView(this);
        chatTextView.setText(text);
        chatTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        ImageButton ttsButton = new ImageButton(this);
        ttsButton.setImageResource(android.R.drawable.ic_media_play);
        ttsButton.setOnClickListener(v -> tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null));

        chatEntry.addView(chatTextView);
        chatEntry.addView(ttsButton);

        chatLog.addView(chatEntry);
        chatScrollView.post(() -> chatScrollView.fullScroll(ScrollView.FOCUS_DOWN));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tts != null) {
            tts.shutdown();
        }
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission for microphone not granted", Toast.LENGTH_SHORT).show();
        }
    }
}
*/