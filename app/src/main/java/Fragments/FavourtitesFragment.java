package Fragments;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.spotified.R;

import java.util.ArrayList;

import Adapter.FavouritesAdapter;
import Database.FavDB;

import static Database.FavDB.COLUMN_ALBUM;
import static Database.FavDB.COLUMN_ARTIST;
import static Database.FavDB.COLUMN_DURATION;
import static Database.FavDB.COLUMN_ID;
import static Database.FavDB.COLUMN_PATH;
import static Database.FavDB.COLUMN_SONG_TITLE;



public class FavourtitesFragment extends Fragment {
    RecyclerView recyclerView;
    public static FavouritesAdapter favouritesAdapter;
    private FavDB favDB;
    ArrayList<FavouriteFiles> favSongsList = new ArrayList<>();


    public FavourtitesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourtites, container, false);
        recyclerView = view.findViewById(R.id.recyclerView4);
        favDB= new FavDB(getActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));


        loadData();

        return view;


    }

    private void loadData() {
        if (favSongsList != null) {
            favSongsList.clear();

        }
        SQLiteDatabase db = favDB.getReadableDatabase();
        Cursor cursor = favDB.selectAllList();
        try {
            while (cursor.moveToNext()) {
                String title = cursor.getString(cursor.getColumnIndex(COLUMN_SONG_TITLE));
                String id = cursor.getString(cursor.getColumnIndex(COLUMN_ID));
                String artist = cursor.getString(cursor.getColumnIndex(COLUMN_ARTIST));
                String album = cursor.getString(cursor.getColumnIndex(COLUMN_ALBUM));
                String duration = cursor.getString(cursor.getColumnIndex(COLUMN_DURATION));
                String path = cursor.getString(cursor.getColumnIndex(COLUMN_PATH));
                FavouriteFiles favItem = new FavouriteFiles(path,title,artist,album,duration,id);
                favSongsList.add(favItem);
            }
        } finally {
            if (cursor != null && cursor.isClosed())
                cursor.close();
            db.close();
        }

        favouritesAdapter = new FavouritesAdapter(getContext(), favSongsList);

        recyclerView.setAdapter(favouritesAdapter);
    }


}