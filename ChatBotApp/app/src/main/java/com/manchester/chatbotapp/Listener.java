package com.manchester.chatbotapp;

import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.content.Intent;
import android.util.Log;

import java.util.Locale;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

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
     * The Volume used by the listener.
     */
    protected float volume;

    /**
     * Stores if the Listener is allowed to speak. If it is not, then when
     * the speak method is called nothing will happen.
     */
    protected  boolean allowSpeech;

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
        this.volume = 1.0f;
        this.allowSpeech = true;

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
                    }
                    Log.i("TextToSpeech", "Initialization success!");
                } else {
                    Log.e("TextToSpeech", "Initialization failed!");
                }
            }
        });


    }

    /**
     * Speaks the words provided to it.
     * @param wordsToSpeak the words to speak.
     */
    public void speak(String wordsToSpeak) {

        if (!allowSpeech) {
            return;
        }

        Log.i("TextToSpeech", "Speaking!");
        Bundle params = new Bundle();
        params.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 100.0f); // Max volume
        tts.speak(wordsToSpeak, TextToSpeech.QUEUE_FLUSH, params, "UtteranceId");
    }

    /**
     * Waits for user input.
     * @return the user input that was read from the voice.
     */
    public String listen() {
        context.changeAnimation('l');
        // Create a CountDownLatch with 1 count to block until the result is available
        final CountDownLatch latch = new CountDownLatch(1);
        final String[] result = {""};

        // Initialize SpeechRecognizer
        this.sr = SpeechRecognizer.createSpeechRecognizer(context);

        // Set the RecognitionListener
        sr.setRecognitionListener(new RecognitionListener() {
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
            public void onError(int error) {
                // Destroy SpeechRecognizer in case of error
                sr.destroy();
                latch.countDown(); // Release the latch to prevent blocking forever
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    result[0] = matches.get(0); // Set the result
                }
                latch.countDown(); // Release the latch once results are received
            }

            @Override
            public void onPartialResults(Bundle partialResults) {}

            @Override
            public void onEvent(int eventType, Bundle params) {}
        });

        // Prepare the speech recognition intent
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.UK);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...");

        // Start listening
        sr.startListening(intent);

        try {
            // Wait for the result or error (this will block until latch.countDown() is called)
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Handle interruption properly
        }

        Log.e("TextToSpeech", "Message: " + result[0]);
        return result[0]; // Return the result after the latch is released
    }



}
