package com.example.spotified.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.spotified.R;

import java.util.ArrayList;

import Fragments.Album1Fragment;
import Fragments.ArtistsFragment;
import Fragments.FavourtitesFragment;
import Fragments.MusicFiles;

public class MenuActivity extends AppCompatActivity{
    GridLayout gridLayout;

    Button songs;
    public static ArrayList<MusicFiles> musicFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        gridLayout = findViewById(R.id.menuGrid);

        setSingleEvent(gridLayout);


    }

    private void setSingleEvent(GridLayout gridLayout) {
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            CardView cardView = (CardView) gridLayout.getChildAt(i);
            final int finalI = i;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (finalI == 0) { //songs
                        Intent intent = new Intent(MenuActivity.this, TabActivity.class);
                        intent.putExtra("sender","musicFiles");
                        startActivity(intent);

                    } else if (finalI == 1) { //albums

                        Intent intent = new Intent(MenuActivity.this, TabActivity.class);
                        intent.putExtra("sender","albumFiles");
                        startActivity(intent);
                    } else if (finalI == 2) { //artists

                        Intent intent = new Intent(MenuActivity.this, TabActivity.class);
                        intent.putExtra("sender","artistFiles");
                        startActivity(intent);

                    } else if (finalI == 3) { //favourites
                        Intent intent = new Intent(MenuActivity.this, TabActivity.class);
                        intent.putExtra("sender","favFiles");
                        startActivity(intent);

                    } else if (finalI == 4) { // playlists
                        Intent intent = new Intent(MenuActivity.this, Playlist.class);
                        startActivity(intent);


                    } else if (finalI == 5) { //genre
                        Intent intent = new Intent(MenuActivity.this, Genre.class);
                        startActivity(intent);

                    }
                    else if (finalI == 6) { //settings

                        Intent intent = new Intent(MenuActivity.this, Settings.class);
                        startActivity(intent);

                    } else if (finalI == 7) { //help

                        Intent intent = new Intent(MenuActivity.this, Help.class);
                        startActivity(intent);

                    } else { //about

                        Intent intent = new Intent(MenuActivity.this, About.class);
                        startActivity(intent);

                    }

                }
            });
        }
    }


}