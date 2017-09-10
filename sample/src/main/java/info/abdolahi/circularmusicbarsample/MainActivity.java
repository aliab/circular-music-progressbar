package info.abdolahi.circularmusicbarsample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.Random;

import info.abdolahi.CircularMusicProgressBar;
import info.abdolahi.OnCircularSeekBarChangeListener;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    CircularMusicProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.album_art);

        // set progress to 40%
        progressBar.setValue(40);

        // get user update
        progressBar.setOnCircularBarChangeListener(new OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularMusicProgressBar circularBar, int progress, boolean fromUser) {
                Log.d(TAG, "onProgressChanged: progress: " + progress + " / from user? " + fromUser);
            }

            @Override
            public void onClick(CircularMusicProgressBar circularBar) {
                Log.d(TAG, "onClick");
                updateRandomly();
            }

            @Override
            public void onLongPress(CircularMusicProgressBar circularBar) {
                Log.d(TAG, "onLongPress");
            }

        });

        // get onClick data
        progressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateRandomly();
            }
        });

    }

    private void updateRandomly() {
        Random random = new Random();
        final float percent = random.nextFloat() * 100;
        progressBar.setValue(percent);
    }
}
