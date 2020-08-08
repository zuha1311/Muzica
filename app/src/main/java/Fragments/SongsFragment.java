package Fragments;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.spotified.Activities.PlayerActivity;
import com.example.spotified.R;

import Adapter.MusicAdpater;

import static Adapter.MusicAdpater.mFiles;
import static com.example.spotified.Activities.PlayerActivity.listSongs;
import static com.example.spotified.Activities.PlayerActivity.mediaPlayer;
import static com.example.spotified.Activities.TabActivity.musicFiles;


public class SongsFragment extends Fragment {
    RecyclerView recyclerView;
    public static MusicAdpater musicAdpater;
    public static boolean shuffleBoolean = false;
    public static boolean repeatBoolean = false;
    ImageView playPauseButton;
    RelativeLayout bottomBar;
    TextView songTitleMainScreen;
    public SongsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        recyclerView = view.findViewById(R.id.recyclerView1);
        playPauseButton = view.findViewById(R.id.playPauseButton);
        bottomBar = view.findViewById(R.id.hiddenBarMainScreen);


        recyclerView.setHasFixedSize(true);
        if(!(musicFiles.size()<1)){
            musicAdpater = new MusicAdpater(getContext(),musicFiles);
            recyclerView.setAdapter(musicAdpater);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
            bottomBarSetup();

        }
        return view;
    }

    private void bottomBarSetup() {

        try {
            bottomBarClickHandler();
            if(mediaPlayer.isPlaying())
            {
                bottomBar.setVisibility(View.VISIBLE);
            }
            else
                {
                bottomBar.setVisibility(View.INVISIBLE);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void bottomBarClickHandler() {


        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying())
                {
                    mediaPlayer.pause();
                    playPauseButton.setBackgroundResource(R.drawable.ic_baseline_play_circle_filled_24);
                }
                else
                {
                    mediaPlayer.start();
                    playPauseButton.setBackgroundResource(R.drawable.ic_baseline_pause_circle_filled_24);
                }
            }
        });
    }

    private static byte[] getAlbumArt (String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }
}
