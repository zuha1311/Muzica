package Fragments;

import android.content.Intent;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.spotified.Activities.PlayerActivity;
import com.example.spotified.R;

import Adapter.MusicAdpater;

import static com.example.spotified.Activities.TabActivity.musicFiles;


public class SongsFragment extends Fragment {
    RecyclerView recyclerView;
    public static MusicAdpater musicAdpater;
    public static boolean shuffleBoolean = false;
    public static boolean repeatBoolean = false;
    public static boolean favBoolean = false;



    public SongsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    /*private void bottomBarSetup() {

        songTitleMainScreen.setText(musicFiles.get(position).getTitle());
        if(PlayerActivity.mediaPlayer.isPlaying())
        {
            nowPlayingBottomBar.setVisibility(View.VISIBLE);
        }
        else
        {
            nowPlayingBottomBar.setVisibility(View.INVISIBLE);
        }

    }*/

    /*private void bottomBarClickHandler() {
        nowPlayingBottomBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),PlayerActivity.class);
                startActivity(intent);

            }
        });

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying())
                {
                    mediaPlayer.pause();
                    position = mediaPlayer.getCurrentPosition();
                    playPauseButton.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);

                }
                else {
                    mediaPlayer.seekTo(position);
                    mediaPlayer.start();
                    playPauseButton.setBackgroundResource(R.drawable.ic_baseline_pause_24);

                }
            }
        });


    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        recyclerView = view.findViewById(R.id.recyclerView1);

        recyclerView.setHasFixedSize(true);
        if(!(musicFiles.size()<1)){
            musicAdpater = new MusicAdpater(getContext(),musicFiles);
            recyclerView.setAdapter(musicAdpater);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));


        }
        return view;
    }
}