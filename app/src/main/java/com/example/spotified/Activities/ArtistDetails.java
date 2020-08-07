package com.example.spotified.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.spotified.R;

import java.util.ArrayList;

import Adapter.AlbumDetailsAdapter;
import Adapter.ArtistDetailsAdapter;
import Fragments.MusicFiles;

import static com.example.spotified.Activities.TabActivity.musicFiles;

public class ArtistDetails extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageView albumPhoto;
    String artistName;
    ArrayList<MusicFiles> artistSongs = new ArrayList<>();
    AlbumDetailsAdapter albumDetailsAdapter;
    ArtistDetailsAdapter artistDetailsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_details);
        recyclerView = findViewById(R.id.recyclerView3);
        albumPhoto = findViewById(R.id.albumPhoto);
        artistName = getIntent().getStringExtra("artistDetails");

        int  j =0;
        for(int i =0 ; i < musicFiles.size(); i++)
        {
            if(artistName.equals(musicFiles.get(i).getArtist()))
            {
                artistSongs.add(j,musicFiles.get(i));
                j++;
            }
        }
        byte [] image = getAlbumArt(artistSongs.get(0).getPath());
        if(image != null)
        {
            Glide.with(this)
                    .load(image)
                    .into(albumPhoto);
        }
        else
        {
            Glide.with(this)
                    .load(R.drawable.bg)
                    .into(albumPhoto);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!(artistSongs.size()<1))
        {
            artistDetailsAdapter = new ArtistDetailsAdapter(this,artistSongs);
            recyclerView.setAdapter(artistDetailsAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));

        }
    }

    private byte[] getAlbumArt (String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return  art;
    }
}