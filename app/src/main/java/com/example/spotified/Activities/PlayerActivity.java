package com.example.spotified.Activities;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.spotified.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

import Adapter.MusicAdpater;

import Database.FavDB;
import Fragments.FavouriteFiles;
import Fragments.MusicFiles;
import Services.Playable;
import Services.onClearFromRecentService;

import static Adapter.AlbumDetailsAdapter.albumFiles;
import static Adapter.ArtistDetailsAdapter.artistFiles;


import static Adapter.FavouritesAdapter.fFiles;
import static Adapter.MusicAdpater.mFiles;
import static Fragments.SongsFragment.repeatBoolean;
import static Fragments.SongsFragment.shuffleBoolean;


public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    TextView song_name, artist_name,duration_played, duration_total;
    ImageView cover_art, nextBtn, prevBtn, backBtn, shuffleBtn, repeatBtn,menuBtn,favBtn,visBtn;
    FloatingActionButton playPauseButton;
    SeekBar seekbar;
    SensorManager sensorManager;
    SensorEventListener sensorEventListener;
    Float mAcceleration = 0f;
    Float mAccelerationCurrent = 0f;
    Float mAccelerationLast = 0f;
    String SETTINGS_PREF = "Shake Feature";
    int position = -1;
    public static ArrayList<MusicFiles> listSongs = new ArrayList<>();
    public static ArrayList<MusicFiles> listFavsongs = new ArrayList<>();
    static Uri uri;
    public static MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Thread playThread,prevThread,nextThread;
    public static NotificationManager notificationManager;
    public static final String ACTION_PREVIOUS = "actionPrevious";
    public static final String CHANNEL_PLAY = "actionPlay";
    public static final String CHANNEL_NEXT = "actionNext";
    public static final String CHANNEL_ID = "channel";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        mAcceleration = 0.0f;
        mAccelerationCurrent=SensorManager.GRAVITY_EARTH;
        mAccelerationLast = SensorManager.GRAVITY_EARTH;
        menuBtn = (ImageView) findViewById(R.id.menuBtn);
        backBtn = (ImageView) findViewById(R.id.backbutton);
        visBtn = findViewById(R.id.vis_btn);
        initViews();
        getIntentMethod();
        sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        bindShakeListener();
        song_name.setText(listSongs.get(position).getTitle());
        artist_name.setText(listSongs.get(position).getArtist());
        mediaPlayer.setOnCompletionListener(this);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(mediaPlayer != null && b)
                {
                    mediaPlayer.seekTo(i * 1000);

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer!=null){
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    seekbar.setProgress(mCurrentPosition);
                    duration_played.setText(formattedTime(mCurrentPosition));
                }

                handler.postDelayed(this,1000);
            }
        });
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            createMusicChannel();
            registerReceiver(broadcastReceiverSongs,new IntentFilter("TRACKS_TRACKS"));
            startService(new Intent(getBaseContext(),onClearFromRecentService.class));
        }

        shuffleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(shuffleBoolean)
                {
                    shuffleBoolean=false;
                    shuffleBtn.setImageResource(R.drawable.ic_baseline_shuffle_off);

                }
                else
                {
                    shuffleBoolean=true;
                    shuffleBtn.setImageResource(R.drawable.ic_baseline_shuffle_on);
                }

            }
        });

        repeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(repeatBoolean)
                {
                    repeatBoolean = false;
                    repeatBtn.setImageResource(R.drawable.ic_baseline_repeat_off);
                }
                else
                {
                    repeatBoolean = true;
                    repeatBtn.setImageResource(R.drawable.ic_baseline_repeat_on);
                }
            }
        });

        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MenuActivity.class);
                mediaPlayer.stop();
                startActivity(intent);
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (getApplicationContext(),TabActivity.class);
                intent.putExtra("sender", "musicFiles");
                startActivity(intent);
            }
        });
        visBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),VisualiserActivity.class);
                intent.putExtra("title", listSongs.get(position).getTitle().toString());
                intent.putExtra("artist",listSongs.get(position).getArtist().toString());
                startActivity(intent);
            }
        });

    }

    private void bindShakeListener() {
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];

                mAccelerationCurrent = mAccelerationLast;
                Double sq = Math.sqrt(x*x + y*y + z*z);
                mAccelerationCurrent = sq.floatValue();
                float delta = mAccelerationCurrent - mAccelerationLast;
                mAcceleration = mAcceleration * 0.9f + delta;

                if(mAcceleration > 10)
                {
                    SharedPreferences sharedPreferences = getSharedPreferences(SETTINGS_PREF,Context.MODE_PRIVATE);
                    Boolean isAllowed = sharedPreferences.getBoolean("feature",false);
                    if(isAllowed)
                    {
                        nextButtonClicked();
                    }
                }


            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createMusicChannel() {
        NotificationChannel channel = new NotificationChannel(MusicAdpater.CHANNEL_ID,"zuha", NotificationManager.IMPORTANCE_LOW);
        notificationManager = getSystemService(NotificationManager.class);
        if(notificationManager != null)
        {
            notificationManager.createNotificationChannel(channel);
        }
    }
    BroadcastReceiver broadcastReceiverSongs = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("actionName");
            switch (action)
            {
                case MusicAdpater.ACTION_PREVIOUS:
                    prevButtonClicked();
                    break;
                case MusicAdpater.ACTION_PLAY:
                    playPauseButtonClicked();
                    break;
                case MusicAdpater.ACTION_NEXT:
                   nextButtonClicked();

                    break;


            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        playThreadBtn();
        nextThreadBtn();
        prevThreadBtn();


         MusicAdpater.createNotification(PlayerActivity.this,mFiles.get(1),R.drawable.ic_baseline_pause_24,position,mFiles.size()-1);
         sensorManager.registerListener(sensorEventListener,sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);


    }

    private void prevThreadBtn() {
        prevThread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                prevBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        prevButtonClicked();
                    }
                });
            }
        };
        prevThread.start();
    }

    private void prevButtonClicked() {
        if(mediaPlayer.isPlaying())
        {

            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffleBoolean && !repeatBoolean)
            {
                position = getRandom(listSongs.size() - 1);
            } else if (!shuffleBoolean && !repeatBoolean)
            {
                position = ((position - 1) < 0 ? (listSongs.size() - 1) : (position - 1));
            }
            //position will be the same
            uri = Uri.parse(listSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());

            seekbar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekbar.setProgress(mCurrentPosition);

                    }

                    handler.postDelayed(this,1000);
                }
            });

            mediaPlayer.setOnCompletionListener(PlayerActivity.this);
            playPauseButton.setBackgroundResource(R.drawable.ic_baseline_pause_24);
            mediaPlayer.start();

            MusicAdpater.createNotification(PlayerActivity.this, mFiles.get(1),R.drawable.ic_baseline_pause_24,position,mFiles.size()-1);


        }
        else
        {
            mediaPlayer.stop();
            mediaPlayer.release();

            if(shuffleBoolean && !repeatBoolean)
            {
                position = getRandom(listSongs.size() - 1);
            } else if (!shuffleBoolean && !repeatBoolean)
            {
                position = ((position - 1) < 0 ? (listSongs.size() - 1) : (position - 1));
            }


            uri = Uri.parse(listSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());

            seekbar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekbar.setProgress(mCurrentPosition);
                    }

                    handler.postDelayed(this,1000);
                }
            });

            mediaPlayer.setOnCompletionListener(PlayerActivity.this);
            playPauseButton.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
            MusicAdpater.createNotification(PlayerActivity.this, mFiles.get(1),R.drawable.ic_baseline_play_arrow_24,position,mFiles.size()-1);

        }
    }

    private void nextThreadBtn() {
        nextThread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        nextButtonClicked();
                    }
                });
            }
        };
        nextThread.start();
    }

    private void nextButtonClicked() {
        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffleBoolean && !repeatBoolean)
            {
                position = getRandom(listSongs.size() - 1);
            } else if (!shuffleBoolean && !repeatBoolean)
            {
                position = ((position + 1) % listSongs.size());
            }

            uri = Uri.parse(listSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());

            seekbar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekbar.setProgress(mCurrentPosition);

                    }

                    handler.postDelayed(this,1000);
                }
            });

            mediaPlayer.setOnCompletionListener(this);

            playPauseButton.setBackgroundResource(R.drawable.ic_baseline_pause_24);
            MusicAdpater.createNotification(PlayerActivity.this, mFiles.get(1),R.drawable.ic_baseline_pause_24,position,mFiles.size()-1);
            mediaPlayer.start();

        }
        else
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffleBoolean && !repeatBoolean)
            {
                position = getRandom(listSongs.size() - 1);
            } else if (!shuffleBoolean && !repeatBoolean)
            {
                position = ((position + 1) % listSongs.size());
            }

            uri = Uri.parse(listSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());

            seekbar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekbar.setProgress(mCurrentPosition);

                    }

                    handler.postDelayed(this,1000);
                }
            });

            mediaPlayer.setOnCompletionListener(this);
            playPauseButton.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
            MusicAdpater.createNotification(PlayerActivity.this, mFiles.get(1),R.drawable.ic_baseline_play_arrow_24,position,mFiles.size()-1);

            }
    }

    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i+1);
    }

    private void playThreadBtn() {
        playThread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                playPauseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        playPauseButtonClicked();
                                         }
                });
            }
        };
        playThread.start();
    }

    private void playPauseButtonClicked() {
        if(mediaPlayer.isPlaying())
        {
            playPauseButton.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            mediaPlayer.pause();
            seekbar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekbar.setProgress(mCurrentPosition);

                    }

                    handler.postDelayed(this,1000);
                }
            });
            MusicAdpater.createNotification(PlayerActivity.this, mFiles.get(1),R.drawable.ic_baseline_play_arrow_24,position,mFiles.size()-1);


        }
        else
        {
            playPauseButton.setImageResource(R.drawable.ic_baseline_pause_24);
            mediaPlayer.start();
            seekbar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekbar.setProgress(mCurrentPosition);

                    }

                    handler.postDelayed(this,1000);
                }
            });
            MusicAdpater.createNotification(PlayerActivity.this, mFiles.get(1),R.drawable.ic_baseline_pause_24,position,mFiles.size()-1);
        }

        }


    private String formattedTime(int mCurrentPosition) {
        String totalOut = "" ;
        String totalNew = "" ;
        String seconds = String.valueOf(mCurrentPosition %  60);
        String minutes = String.valueOf(mCurrentPosition / 60);
        totalOut = minutes + ":" + seconds;
        totalNew = minutes + ":" + "0" + seconds;

        if(seconds.length() == 1)
        {
            return totalNew;
        }
        else{
            return totalOut;
        }
    }

    private void getIntentMethod() {
        position= getIntent().getIntExtra("position", -1);
        String sender = getIntent().getStringExtra("sender");
        if (sender!=null && sender.equals("albumDetails"))
        {
            listSongs = albumFiles ;
        }

        else if(sender!=null && sender.equals("artistDetails")){
            listSongs = artistFiles;
        }

        else
        {
            listSongs=mFiles;
        }

        if(listSongs!=null)
        {
            playPauseButton.setImageResource(R.drawable.ic_baseline_pause_24);
            uri = Uri.parse(listSongs.get(position).getPath());
        }
        if (mediaPlayer!=null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();
        }
        else
        {
            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();
        }

        seekbar.setMax(mediaPlayer.getDuration() / 1000);
        metaData(uri);
    }

    private void initViews(){
        song_name=findViewById(R.id.songName);
        artist_name=findViewById(R.id.songArtist);
        duration_played=findViewById(R.id.durationPlayed);
        duration_total=findViewById(R.id.durationTotal);
        cover_art=findViewById(R.id.cover_Art);
        nextBtn=findViewById(R.id.id_next);
        prevBtn=findViewById(R.id.id_prev);
        shuffleBtn=findViewById(R.id.id_shuffle);
        repeatBtn=findViewById(R.id.id_repeat);
        playPauseButton=findViewById(R.id.play_pause);
        seekbar=findViewById(R.id.seekbar);


    }

    private  void metaData(Uri uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int durationTotal = Integer.parseInt(listSongs.get(position).getDuration()) / 1000;
        duration_total.setText(formattedTime(durationTotal));
        byte[] art = retriever.getEmbeddedPicture();
        Bitmap bitmap;
        if(art!=null)
        {
            bitmap = BitmapFactory.decodeByteArray(art,0,art.length);
            ImageAnimation(this,cover_art,bitmap);

            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    Palette.Swatch swatch = palette.getDominantSwatch();
                    if(swatch!=null)
                    {
                        ImageView gradient = findViewById(R.id.imageViewGradient);
                        RelativeLayout mContainer = findViewById(R.id.mContainer);
                        gradient.setBackgroundResource(R.drawable.bg_gradient);
                        mContainer.setBackgroundResource(R.drawable.tab_bg);
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch.getRgb(),0x00000000});
                        gradient.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch.getRgb(),swatch.getRgb()});
                        mContainer.setBackground(gradientDrawableBg);

                        song_name.setTextColor(swatch.getTitleTextColor());
                        artist_name.setTextColor(swatch.getBodyTextColor());

                    }
                    else
                    {
                        ImageView gradient = findViewById(R.id.imageViewGradient);
                        RelativeLayout mContainer = findViewById(R.id.mContainer);
                        gradient.setBackgroundResource(R.drawable.bg_gradient);
                        mContainer.setBackgroundResource(R.drawable.tab_bg);
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{0xff000000,0x00000000});
                        gradient.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{0xff000000,0xff000000});
                        mContainer.setBackground(gradientDrawableBg);

                        song_name.setTextColor(Color.WHITE);
                        artist_name.setTextColor(Color.DKGRAY);
                    }
                }

            });
        }
        else
            {
                Glide.with(this)
                        .asBitmap()
                        .load(R.drawable.logo1)
                        .into(cover_art);
                ImageView gradient = findViewById(R.id.imageViewGradient);
                RelativeLayout mContainer = findViewById(R.id.mContainer);
                gradient.setBackgroundResource(R.drawable.bg_gradient);
                mContainer.setBackgroundResource(R.drawable.tab_bg);
                song_name.setTextColor(Color.WHITE);
                artist_name.setTextColor(Color.DKGRAY);
        }

    }
    public void ImageAnimation(final Context context , final ImageView imageView, final Bitmap bitmap)
    {
        Animation animOut = AnimationUtils.loadAnimation(context,android.R.anim.fade_out);
        final Animation animIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Glide.with(context).load(bitmap).into(imageView);
                animIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                imageView.startAnimation(animIn);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(animOut);

    }


    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
            onCompletionNext();

        if(mediaPlayer!=null)
        {
            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.setOnCompletionListener(this);


        }
    }

    private void onCompletionNext() {

            mediaPlayer.stop();
            mediaPlayer.release();
            if (shuffleBoolean && !repeatBoolean) {
                position = getRandom(listSongs.size() - 1);
            } else if (!shuffleBoolean && !repeatBoolean) {
                position = ((position + 1) % listSongs.size());
            }

            uri = Uri.parse(listSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());

            seekbar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekbar.setProgress(mCurrentPosition);
                    }

                    handler.postDelayed(this, 1000);
                }
            });
            mediaPlayer.setOnCompletionListener(PlayerActivity.this);
            playPauseButton.setImageResource(R.drawable.ic_baseline_pause_24);
            MusicAdpater.createNotification(PlayerActivity.this, mFiles.get(1), R.drawable.ic_baseline_pause_24, position, mFiles.size() - 1);
            mediaPlayer.start();
        }



    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.cancelAll();
        }
        unregisterReceiver(broadcastReceiverSongs);

    }

}