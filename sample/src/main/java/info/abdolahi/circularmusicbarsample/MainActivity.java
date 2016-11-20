package info.abdolahi.circularmusicbarsample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.Random;

import info.abdolahi.CircularMusicProgressBar;

public class MainActivity extends AppCompatActivity {

    CircularMusicProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (CircularMusicProgressBar) findViewById(R.id.album_art);


        // set progress to 40%
        progressBar.setValue(40);

        progressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random random = new Random();
                progressBar.setValue(random.nextFloat() * 100);
            }
        });

    }
}
