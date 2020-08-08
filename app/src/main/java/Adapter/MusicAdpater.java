package Adapter;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.spotified.Activities.TabActivity;
import com.example.spotified.R;

import java.io.File;
import java.util.ArrayList;

import com.example.spotified.Activities.PlayerActivity;

import Database.FavDB;
import Fragments.MusicFiles;
import Services.NotificationActionReceiver;

import static com.example.spotified.Activities.TabActivity.musicFiles;

public class MusicAdpater extends RecyclerView.Adapter<MusicAdpater.MyViewHolder> {
    private Context mContext;
    public static ArrayList<MusicFiles> mFiles;
    public static Notification notification;
    public static final String ACTION_PREVIOUS = "actionPrevious";
    public static final String ACTION_PLAY = "actionPlay";
    public static final String ACTION_NEXT = "actionNext";
    public static final String CHANNEL_ID = "channel";
    public static final Bitmap noIcon = null;
    private FavDB favDB;


    public MusicAdpater(Context mContext, ArrayList<MusicFiles> mFiles) {
        this.mFiles = mFiles;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        favDB = new FavDB(mContext);
        SharedPreferences prefs = mContext.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean("firstStart", true);
        if (firstStart) {
            createTableOnFirstStart();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.song_layout, parent, false);
        return new MyViewHolder(view);
    }

    private void createTableOnFirstStart() {
        favDB.insertEmpty();
        SharedPreferences prefs = mContext.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        final MusicFiles musicFiles = mFiles.get(position);
        readCursordata(musicFiles, holder);
        holder.songtext.setText(mFiles.get(position).getTitle());
        byte[] image = getAlbumArt(mFiles.get(position).getPath());
        if (image != null) {
            Glide.with(mContext).asBitmap()
                    .load(image)
                    .into(holder.album_art);
        } else {
            Glide.with(mContext)
                    .load(R.drawable.logo1)
                    .into(holder.album_art);

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, PlayerActivity.class);
                intent.putExtra("position", position);
                mContext.startActivity(intent);

            }
        });
        holder.menu_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                PopupMenu popupMenu = new PopupMenu(mContext, view);
                popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener((new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.delete:
                                Toast.makeText(mContext, "Deleted", Toast.LENGTH_SHORT).show();
                                deleteFile(position, view);
                                break;
                        }

                        return true;
                    }
                }));
            }
        });


    }

    private void readCursordata(MusicFiles musicFiles, MyViewHolder holder) {
        Cursor cursor = favDB.read_all_data(musicFiles.getId());
        SQLiteDatabase db = favDB.getReadableDatabase();
        try {
            while (cursor.moveToNext()) {
                String item_fav_status = cursor.getString(cursor.getColumnIndex(FavDB.FAVORITE_STATUS));
                musicFiles.setFavStatus(item_fav_status);

                //check fav status
                if (item_fav_status != null && item_fav_status.equals("1")) {
                    holder.fav_btn.setBackgroundResource(R.drawable.ic_baseline_favorite_24_on);
                } else if (item_fav_status != null && item_fav_status.equals("0")) {
                    holder.fav_btn.setBackgroundResource(R.drawable.ic_baseline_favorite_border_24);
                }
            }
        } finally {
            if (cursor != null && cursor.isClosed())
                cursor.close();
            db.close();
        }


    }


    private void deleteFile(int position, View v) {
        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                Long.parseLong(mFiles.get(position).getId()));

        File file = new File(mFiles.get(position).getPath());
        boolean deleted = file.delete();
        if (deleted) {
            mContext.getContentResolver().delete(contentUri, null, null);
            mFiles.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mFiles.size());

        }


    }


    @Override
    public int getItemCount() {
        return mFiles.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView songtext;
        ImageView album_art, menu_more, fav_btn;
        RelativeLayout nowPlayingBottomBar;
        TextView songTitleMainScreen;
        ImageView playPauseButton, albumArtBottomBar;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            songtext = itemView.findViewById(R.id.songText);
            album_art = itemView.findViewById(R.id.music_img);
            menu_more = itemView.findViewById(R.id.menuMore);
            nowPlayingBottomBar = itemView.findViewById(R.id.hiddenBarMainScreen);

            playPauseButton = itemView.findViewById(R.id.playPauseButton);
            albumArtBottomBar = itemView.findViewById(R.id.albumArtBottomBar);
            fav_btn = itemView.findViewById(R.id.favItem);
            fav_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    MusicFiles mf = mFiles.get(position);

                    likeClick(mf, fav_btn);
                }
            });
            int position = getAdapterPosition();

        }
    }

    private void likeClick(MusicFiles mf, ImageView fav_btn) {
        if (mf.getFavStatus().equals("0")) {

            mf.setFavStatus("1");
            favDB.insertIntoDatabase(mf.getId(), mf.getTitle(), mf.getArtist(), mf.getAlbum(), mf.getDuration(), mf.getPath(), mf.getFavStatus());

            fav_btn.setBackgroundResource(R.drawable.ic_baseline_favorite_24_on);
            fav_btn.setSelected(true);
        } else if (mf.getFavStatus().equals("1")) {
            mf.setFavStatus("0");
            favDB.remove_fav(mf.getId());
            fav_btn.setBackgroundResource(R.drawable.ic_baseline_favorite_border_24);
            fav_btn.setSelected(false);
        }
    }

    private static byte[] getAlbumArt(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }

    public void updateList(ArrayList<MusicFiles> musicFilesArrayList) {
        mFiles = new ArrayList<>();
        mFiles.addAll(musicFilesArrayList);
        notifyDataSetChanged();

    }

    public static void createNotification(Context mContext, MusicFiles musicFiles, int playButton, int position, int size) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(mContext);
            MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(mContext, "tag");
            /*byte[] image = getAlbumArt(mFiles.get(position).getPath());

                Bitmap icon = BitmapFactory.decodeByteArray(image, 0, image.length);*/

            PendingIntent pendingIntentPrevious;
            int drw_previous;
            if (position == 0) {
                pendingIntentPrevious = null;
                drw_previous = 0;
            } else {
                Intent intentPrevious = new Intent(mContext, NotificationActionReceiver.class)
                        .setAction(ACTION_PREVIOUS);
                pendingIntentPrevious = PendingIntent.getBroadcast(mContext, 0, intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT);
                drw_previous = R.drawable.ic_baseline_skip_previous;

            }
            Intent intentPlay = new Intent(mContext, NotificationActionReceiver.class)
                    .setAction(ACTION_PLAY);
            PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(mContext, 0, intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);

            PendingIntent pendingIntentNext;
            int drw_next;

            if (position == size) {
                pendingIntentNext = null;
                drw_next = 0;
            } else {
                Intent intentNext = new Intent(mContext, NotificationActionReceiver.class)
                        .setAction(ACTION_NEXT);
                pendingIntentNext = PendingIntent.getBroadcast(mContext, 0, intentNext, PendingIntent.FLAG_UPDATE_CURRENT);
                drw_next = R.drawable.ic_baseline_skip_next;

            }

            Intent resultIntent = new Intent(mContext, PlayerActivity.class);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            notification = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                    .setSmallIcon(R.drawable.bg)
                    .setContentTitle(mFiles.get(position).getTitle())
                    .setContentText(mFiles.get(position).getArtist())
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setShowWhen(false)
                    .setOnlyAlertOnce(true)
                    .setContentIntent(resultPendingIntent)
                    .addAction(drw_previous, "Previous", pendingIntentPrevious)
                    .addAction(playButton, "Play", pendingIntentPlay)
                    .addAction(drw_next, "Next", pendingIntentNext)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0, 1, 2)
                            .setMediaSession(mediaSessionCompat.getSessionToken()))
                    .build();


            notificationManagerCompat.notify(1, notification);

        }
    }



}
