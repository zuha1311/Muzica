package Adapter;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.spotified.Activities.PlayerActivity;
import com.example.spotified.R;

import java.util.ArrayList;

import Fragments.MusicFiles;
import Services.NotificationActionReceiver;

import static Adapter.AlbumDetailsAdapter.albumFiles;

public class ArtistDetailsAdapter extends RecyclerView.Adapter<ArtistDetailsAdapter.MyHolder> {
    private Context mContext;
    public static ArrayList<MusicFiles> artistFiles;
    public static Notification notification;
    public static final String ACTION_PREVIOUS = "actionPrevious";
    public static final String ACTION_PLAY = "actionPlay";
    public static final String ACTION_NEXT = "actionNext";
    public static final String CHANNEL_ID = "channel";

    public ArtistDetailsAdapter(Context mContext, ArrayList<MusicFiles> artistFiles) {
        this.mContext = mContext;
        this.artistFiles = artistFiles;
    }

    @NonNull
    @Override
    public ArtistDetailsAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.song_layout,parent , false);
        return new ArtistDetailsAdapter.MyHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ArtistDetailsAdapter.MyHolder holder, final int position) {
        holder.artist_name.setText(artistFiles.get(position).getTitle());
        byte[] image = getAlbumArt(artistFiles.get(position).getPath());

        if(image != null){
            Glide.with(mContext).asBitmap()
                    .load(image)
                    .into(holder.album_image);
        }
        else
        {       Glide.with(mContext)
                .load(R.drawable.logo1)
                .into(holder.album_image);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PlayerActivity.class);
                intent.putExtra("sender" , "artistDetails");
                intent.putExtra("position", position);
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return artistFiles.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        ImageView album_image;
        TextView artist_name;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            album_image = itemView.findViewById(R.id.music_img);
            artist_name = itemView.findViewById(R.id.songText);
        }
    }
    private static byte[] getAlbumArt(String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return  art;
    }
    /*public static void createNotification(Context mContext,MusicFiles aFiles, int playButton, int position,int size)
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(mContext);
            MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(mContext, "tag");
            byte[] image = getAlbumArt(artistFiles.get(position).getPath());
            Bitmap icon = BitmapFactory.decodeByteArray(image, 0, image.length);
            PendingIntent pendingIntentPrevious;
            int drw_previous;
            if (position == 0) {
                pendingIntentPrevious = null;
                drw_previous = 0;
            } else{
                Intent intentPrevious = new Intent(mContext, NotificationActionReceiver.class)
                        .setAction(ACTION_PREVIOUS);
                pendingIntentPrevious = PendingIntent.getBroadcast(mContext,0,intentPrevious,PendingIntent.FLAG_UPDATE_CURRENT);
                drw_previous = R.drawable.ic_baseline_skip_previous;

            }
            Intent intentPlay = new Intent(mContext, NotificationActionReceiver.class)
                    .setAction(ACTION_PLAY);
            PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(mContext,0,intentPlay,PendingIntent.FLAG_UPDATE_CURRENT);

            PendingIntent pendingIntentNext;
            int drw_next;

            if (position == size) {
                pendingIntentNext = null;
                drw_next = 0;
            } else{
                Intent intentPrevious = new Intent(mContext, NotificationActionReceiver.class)
                        .setAction(ACTION_NEXT);
                pendingIntentNext = PendingIntent.getBroadcast(mContext,0,intentPrevious,PendingIntent.FLAG_UPDATE_CURRENT);
                drw_next = R.drawable.ic_baseline_skip_next;

            }

            Intent resultIntent = new Intent(mContext,PlayerActivity.class);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext,1,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            notification = new NotificationCompat.Builder(mContext,CHANNEL_ID)
                    .setSmallIcon(R.drawable.bg)
                    .setContentTitle(artistFiles.get(position).getTitle())
                    .setContentText(artistFiles.get(position).getArtist())
                    .setLargeIcon(icon)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setShowWhen(false)
                    .setOnlyAlertOnce(true)
                    .setContentIntent(resultPendingIntent)
                    .addAction(drw_previous,"Previous",pendingIntentPrevious)
                    .addAction(playButton,"Play",pendingIntentPlay)
                    .addAction(drw_next,"Next",pendingIntentNext)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0,1,2)
                            .setMediaSession(mediaSessionCompat.getSessionToken()))
                    .build();

            notificationManagerCompat.notify(1,notification);

        }
    }*/

}

