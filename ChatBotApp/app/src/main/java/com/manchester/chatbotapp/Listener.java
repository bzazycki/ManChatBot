package com.manchester.chatbotapp;

import android.content.Context;
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

import java.util.Locale;
import java.util.ArrayList;

/**
 * The Listener Class. This intakes and processes speech into words, and given
 * words will speak them in the appropriate language and regional accent.
 */
public class Listener {

    public static void speak(Context c, String wordsToSpeak) {

        TextToSpeech tts = new TextToSpeech(c, status -> {
            if (status == TextToSpeech.ERROR) {
                return;
            }
        });

        Locale ukLocale = new Locale("en", "GB");
        tts.setLanguage(ukLocale);
        tts.setPitch(1.0f);
        tts.setSpeechRate(0.8f);

        tts.speak(wordsToSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    public static String listen(Context c) {

        SpeechRecognizer speechRecognizer = SpeechRecognizer.createSpeechRecognizer(c);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {}

            @Override
            public void onBeginningOfSpeech() {}

            @Override
            public void onRmsChanged(float rmsdB) {}

            @Override
            public void onBufferReceived(byte[] buffer) {}

            @Override
            public void onEndOfSpeech() {}

            @Override
            public void onError(int error) {}

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && matches.isEmpty()) {

                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {}

            @Override
            public void onEvent(int eventType, Bundle params) {}
        });
    }


}
