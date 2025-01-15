package com.manchester.chatbotapp;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * The main activity. This is the entry point for the entire application. The onCreate
 * function is called when it is created.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the app fullscreen by removing the title bar and status bar
        requestWindowFeature(Window.FEATURE_NO_TITLE); // Remove title bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); // Remove status bar

        // Create an ImageView programmatically
        ImageView imageView = new ImageView(this);

        // Set the image resource (ensure the image is added to res/drawable folder)
        imageView.setImageResource(R.drawable.startscreen); // Replace 'your_image_name' with the actual image name

        // Scale the image to fit the screen
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // Set an OnClickListener to the ImageView
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When the image is clicked, start a new Activity
                Intent intent = new Intent(MainActivity.this, AppActivity.class); // Replace NewActivity with the target Activity
                startActivity(intent);

                finish();
            }
        });

        // Set the ImageView as the content view
        setContentView(imageView);

    }
}