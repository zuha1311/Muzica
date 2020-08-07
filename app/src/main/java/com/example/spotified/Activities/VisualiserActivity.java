package com.example.spotified.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cleveroad.audiovisualization.AudioVisualization;
import com.cleveroad.audiovisualization.DbmHandler;
import com.cleveroad.audiovisualization.GLAudioVisualizationView;
import com.cleveroad.audiovisualization.VisualizerDbmHandler;
import com.example.spotified.R;

import static com.example.spotified.Activities.PlayerActivity.listSongs;

public class VisualiserActivity extends AppCompatActivity {
    AudioVisualization audioVisualization;
    GLAudioVisualizationView glView;
    TextView title,artist;
    String titleS,artistS;
    ImageView backBtn;

    @Override
    protected void onResume() {
        super.onResume();
        audioVisualization.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualiser);
        glView = findViewById(R.id.visualiser);
        title = findViewById(R.id.songTitleVis);
        artist = findViewById(R.id.songArtistVis);

        audioVisualization = (AudioVisualization) glView;
        Intent i = getIntent();
         titleS = i.getStringExtra("title");
         artistS = i.getStringExtra("artist");
         title.setText(titleS);
         artist.setText(artistS);

        VisualizerDbmHandler visualizerDbmHandler = DbmHandler.Factory.newVisualizerHandler(getApplicationContext(), 0);
        audioVisualization.linkTo(visualizerDbmHandler);




    }

    @Override
    protected void onDestroy() {
        audioVisualization.release();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        audioVisualization.onPause();
        super.onPause();
    }
}