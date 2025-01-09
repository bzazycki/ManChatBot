package Android.UI;

import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CustomFrameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create the parent FrameLayout
        FrameLayout parentLayout = new FrameLayout(this);
        parentLayout.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));
        parentLayout.setBackgroundColor(Color.parseColor("#DDDDDD")); // Set a light gray background

        // Create a border-like View
        View frameView = new View(this);
        FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(400, 400);
        frameParams.gravity = Gravity.CENTER;
        frameView.setLayoutParams(frameParams);

        // Create a ShapeDrawable to act as a border
        ShapeDrawable border = new ShapeDrawable(new RectShape());
        border.getPaint().setColor(Color.BLACK); // Border color
        border.getPaint().setStyle(android.graphics.Paint.Style.STROKE);
        border.getPaint().setStrokeWidth(8); // Border thickness
        frameView.setBackground(border);

        // Add a click listener to the frame
        frameView.setOnClickListener(v -> 
            Toast.makeText(CustomFrameActivity.this, "Frame clicked!", Toast.LENGTH_SHORT).show()
        );

        // Create a TextView to display inside the "frame"
        TextView textView = new TextView(this);
        textView.setText("Hello, Frame!");
        textView.setTextSize(24);
        textView.setTextColor(Color.BLACK);

        // Center the TextView within the parent FrameLayout
        FrameLayout.LayoutParams textParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        textParams.gravity = Gravity.CENTER;
        textView.setLayoutParams(textParams);

        // Add the frame and the TextView to the parent FrameLayout
        parentLayout.addView(frameView);
        parentLayout.addView(textView);

        // Set the parent FrameLayout as the content view
        setContentView(parentLayout);
    }
}

