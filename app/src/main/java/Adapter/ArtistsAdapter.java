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
import com.example.spotified.Activities.AlbumDetails;
import com.example.spotified.Activities.ArtistDetails;
import com.example.spotified.Activities.PlayerActivity;
import com.example.spotified.R;

import java.util.ArrayList;

import Fragments.MusicFiles;
import Services.NotificationActionReceiver;

public class ArtistsAdapter extends RecyclerView.Adapter<ArtistsAdapter.MyHolder> {
    private Context mContext;
    public static ArrayList<MusicFiles> artistFiles;


    public ArtistsAdapter(Context mContext, ArrayList<MusicFiles> artistFiles) {
        this.mContext = mContext;
        this.artistFiles = artistFiles;
    }

    @NonNull
    @Override
    public ArtistsAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.artist_item,parent , false);
        return new ArtistsAdapter.MyHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ArtistsAdapter.MyHolder holder, final int position) {
        holder.artist_name.setText(artistFiles.get(position).getArtist());
        byte[] image = getAlbumArt(artistFiles.get(position).getPath());
        if(image != null){
            Glide.with(mContext).asBitmap()
                    .load(image)
                    .into(holder.album_image);
        }
        else
        {       Glide.with(mContext)
                .load(R.drawable.bg)
                .into(holder.album_image);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (mContext, ArtistDetails.class);
                intent.putExtra("artistDetails",artistFiles.get(position).getArtist());
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
            album_image = itemView.findViewById(R.id.album_image);
            artist_name = itemView.findViewById(R.id.artist_name);
        }
    }
    private static byte[] getAlbumArt(String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return  art;
    }
    public void updateList(ArrayList<MusicFiles> musicFilesArrayList)
    {
        artistFiles = new ArrayList<>();
        artistFiles.addAll(musicFilesArrayList);
        notifyDataSetChanged();

    }





}
