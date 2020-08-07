package com.example.spotified.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationManager;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.spotified.R;

import java.util.ArrayList;

import Adapter.AlbumDetailsAdapter;
import Fragments.MusicFiles;

import static com.example.spotified.Activities.TabActivity.musicFiles;

public class AlbumDetails extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageView albumPhoto;
    String albumName;
    ArrayList<MusicFiles> albumsSongs = new ArrayList<>();
    public static AlbumDetailsAdapter albumDetailsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_details);
        recyclerView = findViewById(R.id.recyclerView2);
        albumPhoto = findViewById(R.id.albumPhoto);
        albumName = getIntent().getStringExtra("albumName");

        int  j =0;
        for(int i =0 ; i < musicFiles.size(); i++)
        {
            if(albumName.equals(musicFiles.get(i).getAlbum()))
            {
                albumsSongs.add(j,musicFiles.get(i));
                j++;
            }
        }
        byte [] image = getAlbumArt(albumsSongs.get(0).getPath());
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

        if(!(albumsSongs.size()<1))
        {
            albumDetailsAdapter = new AlbumDetailsAdapter(this,albumsSongs);
            recyclerView.setAdapter(albumDetailsAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false));

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