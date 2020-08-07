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

import static com.example.spotified.Activities.TabActivity.albums;


public class Album1Fragment extends Fragment {



    RecyclerView recyclerView;
    public static AlbumAdapter albumAdapter;




    public Album1Fragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_album1, container , false);
        recyclerView = view.findViewById(R.id.recyclerView1);
        recyclerView.setHasFixedSize(true);
        if(!(albums.size()<1)){
            albumAdapter= new AlbumAdapter(getContext(),albums);
            recyclerView.setAdapter(albumAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        }
        return view;
    }

}