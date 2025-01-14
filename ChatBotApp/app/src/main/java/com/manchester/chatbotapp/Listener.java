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

/**
 * The Listener Class. This intakes and processes speech into words, and given
 * words will speak them in the appropriate language and regional accent.
 */
public class Listener {

    public static String listen(Context c) {

        TextToSpeech tts = new TextToSpeech(c, status -> {
            if (status == TextToSpeech.ERROR) {
                return;
            }
        });

        Locale ukLocale = new Locale("en", "GB");
        tts.setLanguage(ukLocale);
        tts.setPitch(1.0f);
        tts.setSpeechRate(0.8f);

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
            public void onResults(Bundle results) {}

            @Override
            public void onPartialResults(Bundle partialResults) {}

            @Override
            public void onEvent(int eventType, Bundle params) {}
        });


        return "No Input";
    }

}
