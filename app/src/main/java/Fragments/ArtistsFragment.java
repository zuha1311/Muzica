package Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.spotified.R;

import Adapter.AlbumAdapter;
import Adapter.ArtistsAdapter;

import static com.example.spotified.Activities.TabActivity.albums;
import static com.example.spotified.Activities.TabActivity.artists;


public class ArtistsFragment extends Fragment {

    RecyclerView recyclerView;
    public static ArtistsAdapter artistsAdapter;


    public ArtistsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_artists, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewArtists);
        recyclerView.setHasFixedSize(true);
        if (!(artists.size() < 1)) {
            artistsAdapter = new ArtistsAdapter(getContext(), artists);
            recyclerView.setAdapter(artistsAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        }
        return view;
    }

   
}