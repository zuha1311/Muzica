package Adapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.spotified.Activities.PlayerActivity;
import com.example.spotified.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.ArrayList;

import Database.FavDB;
import Fragments.FavouriteFiles;
import Fragments.MusicFiles;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.MyViewHolder> {
    private Context fContext;
    public static ArrayList<FavouriteFiles> fFiles;
    private FavDB favDB;


    

    public FavouritesAdapter(Context fContext, ArrayList<FavouriteFiles> fFiles){
        this.fFiles = fFiles;
        this.fContext = fContext;
    }
    @NonNull
    @Override
    public FavouritesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(fContext).inflate(R.layout.fav_layout, parent, false);
        favDB = new FavDB(fContext);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull FavouritesAdapter.MyViewHolder holder, final int position) {

        holder.songtext.setText(fFiles.get(position).getTitle());
        byte[] image = getAlbumArt(fFiles.get(position).getPath());
        if(image != null){
            Glide.with(fContext).asBitmap()
                    .load(image)
                    .into(holder.album_art);
        }
        else
        {       Glide.with(fContext)
                .load(R.drawable.logo1)
                .into(holder.album_art);

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(fContext, PlayerActivity.class);
                intent.putExtra("sender" , "fav");
                intent.putExtra("position",position);
                fContext.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return fFiles.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView songtext;
        ImageView album_art;
        ImageView favBtn;

        public MyViewHolder(@NonNull View itemView) {

            super(itemView);
            songtext=itemView.findViewById(R.id.songText);
            album_art = itemView.findViewById(R.id.music_img);
            favBtn = itemView.findViewById(R.id.favItem);

            //remove from fav after click
            favBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    final FavouriteFiles favItem = fFiles.get(position);
                    favDB.remove_fav(favItem.getId());
                    removeItem(position);

                }
            });


        }

        }


    private static byte[] getAlbumArt (String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }
    private  void removeItem(int position)
    {
        fFiles.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,fFiles.size());

    }
}
