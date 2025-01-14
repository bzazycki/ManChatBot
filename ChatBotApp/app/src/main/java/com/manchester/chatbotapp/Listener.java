package com.manchester.chatbotapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.content.Intent;
import android.util.Log;
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

    /**
     * The Text-To-Speech object.
     */
    protected TextToSpeech tts;

    /**
     * The Speech Recognizer Object.
     */
    protected SpeechRecognizer sr;

    /**
     * Stores if the Listener is allowed to speak. If it is not, then when
     * the speak method is called nothing will happen.
     */
    protected boolean allowSpeech;

    /**
     * The Context for the object. Used in the Text To Speech and more.
     */
    protected AppActivity context;

    /**
     * The Locale that is being used for the speech. Can be changed to
     * change how the speech is rendered.
     */
    protected  Locale locale;

    /**
     * Constructor for the listener object. Will store much of the information
     * along with many of the contexts.
     */
    public Listener(AppActivity c) {
        this.context = c;
        this.locale = new Locale("en", "GB");

        // Initialize TextToSpeech
        this.tts = new TextToSpeech(c, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    // Set language and other properties after initialization
                    int result = tts.setLanguage(locale);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TextToSpeech", "The specified language is not supported!");
                    } else {
                        Log.i("TextToSpeech", "The specified language is supported");
                        tts.setPitch(1.0f);
                        tts.setSpeechRate(0.8f);
                    }
                    Log.i("TextToSpeech", "Initialization success!");
                } else {
                    Log.e("TextToSpeech", "Initialization failed!");
                }
            }
        });

        this.sr = SpeechRecognizer.createSpeechRecognizer(context);

    }

    public void speak(String wordsToSpeak) {
        Log.i("TextToSpeech", "Speaking!");
        Bundle params = new Bundle();
        params.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f); // Max volume
        tts.speak(wordsToSpeak, TextToSpeech.QUEUE_FLUSH, params, "UtteranceId");
    }

    public String listen() {

        final String[] result = {""};

        sr.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
            }

            @Override
            public void onBeginningOfSpeech() {
            }

            @Override
            public void onRmsChanged(float rmsdB) {
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
            }

            @Override
            public void onEndOfSpeech() {
            }

            @Override
            public void onError(int error) {
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && matches.isEmpty()) {
                    String foundResult = matches.get(0);
                    result[0] = foundResult;
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
            }
        });

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.UK);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...");
        sr.startListening(intent);

        return result[0];
    }


}
