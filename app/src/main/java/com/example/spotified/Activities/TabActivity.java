package com.example.spotified.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.example.spotified.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import Database.FavDB;
import Fragments.Album1Fragment;
import Fragments.ArtistsFragment;
import Fragments.FavourtitesFragment;
import Fragments.MusicFiles;
import Fragments.SongsFragment;

import static Adapter.ArtistsAdapter.artistFiles;

public class TabActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    public static ArrayList<MusicFiles> musicFiles;

    public static FavDB favDB;
    public static ArrayList<MusicFiles> albums = new ArrayList<>();
    public static ArrayList<MusicFiles> artists = new ArrayList<>();
    public static ArrayList<MusicFiles> fav= new ArrayList<>();
    private String MY_SORT_PREF = "SortOrder";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        execute();

    }

    private void initViewPager(){
        ViewPager viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        String sender = getIntent().getStringExtra("sender");
        if(sender!= null && sender.equals("musicFiles") )
        {
            viewPagerAdapter.addFragments(new SongsFragment(),"Songs");
            viewPagerAdapter.addFragments(new Album1Fragment(),"Albums");
            viewPagerAdapter.addFragments(new ArtistsFragment(),"Artists");
            viewPagerAdapter.addFragments(new FavourtitesFragment(),"Favourites");
        } else if(sender!=null && sender.equals("albumFiles"))
        {
            viewPagerAdapter.addFragments(new Album1Fragment(),"Albums");
            viewPagerAdapter.addFragments(new SongsFragment(),"Songs");
            viewPagerAdapter.addFragments(new ArtistsFragment(),"Artists");
            viewPagerAdapter.addFragments(new FavourtitesFragment(),"Favourites");

        }
        else if(sender!=null && sender.equals("artistFiles"))
        {
            viewPagerAdapter.addFragments(new ArtistsFragment(),"Artists");
            viewPagerAdapter.addFragments(new SongsFragment(),"Songs");
            viewPagerAdapter.addFragments(new Album1Fragment(),"Albums");
            viewPagerAdapter.addFragments(new FavourtitesFragment(),"Favourites");

        }
        else if(sender!=null && sender.equals("favFiles"))
        {
            viewPagerAdapter.addFragments(new FavourtitesFragment(),"Favourites");
            viewPagerAdapter.addFragments(new SongsFragment(),"Songs");
            viewPagerAdapter.addFragments(new Album1Fragment(),"Albums");
            viewPagerAdapter.addFragments(new ArtistsFragment(),"Artists");

       }


        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }
    private void execute(){

        musicFiles = getAllAudio(this);

        initViewPager();

    }



    public static class ViewPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles  = new ArrayList<>();
        }
        void addFragments (Fragment fragment, String title ){
            fragments.add(fragment);
            titles.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    public ArrayList<MusicFiles> getAllAudio(Context context)
    {
        SharedPreferences preferences  = getSharedPreferences(MY_SORT_PREF,MODE_PRIVATE);
        String sortOrder = preferences.getString("sorting","sortByName");
        ArrayList<String> duplicate = new ArrayList<>();
        albums.clear();
        artists.clear();
        fav.clear();

        ArrayList<MusicFiles> tempAudioList = new ArrayList<>();
        String order = null;
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        switch(sortOrder)
        {
            case "sortByName":
                order = MediaStore.MediaColumns.DISPLAY_NAME + " ASC";
                break;
            case "sortByDate":
                order = MediaStore.MediaColumns.DATE_ADDED+ " ASC";
                break;
            case "sortBySize":
                order = MediaStore.MediaColumns.SIZE + " DESC";
                break;


            }
        String[] projection = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media._ID,



        };
        Cursor cursor = context.getContentResolver().query(uri,projection,null,null,order);

        if(cursor!=null){
            while(cursor.moveToNext())
            {
                String album= cursor.getString(0);
                String title= cursor.getString(1);
                String duration= cursor.getString(2);
                String path= cursor.getString(3);
                String artist= cursor.getString(4);
                String id = cursor.getString(5);


                MusicFiles musicFiles = new MusicFiles(path,title,artist,album,duration,id,"0");
                Log.e("Path : " + path, "Album: " + album);
                tempAudioList.add(musicFiles);

                if(!duplicate.contains(album))
                {
                    albums.add(musicFiles);
                    duplicate.add(album);
                }
                if(!duplicate.contains(artist))
                {
                    artists.add(musicFiles);
                    duplicate.add(artist);
                }


            }
            cursor.close();
        }
        return tempAudioList;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem menuItem = menu.findItem(R.id.search_option);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        String userInput = s.toLowerCase();
        ArrayList<MusicFiles> myFiles = new ArrayList<>();
        for (MusicFiles songMusic : musicFiles){
            if(songMusic.getTitle().toLowerCase().contains(userInput))
            {
                myFiles.add(songMusic);
            }
        }
        for (MusicFiles songMusic : albums){
            if(songMusic.getAlbum().toLowerCase().contains(userInput))
            {
                myFiles.add(songMusic);
            }
        }
        for (MusicFiles songMusic :artistFiles)
        {
            if(songMusic.getArtist().toLowerCase().contains(userInput))
            {
                myFiles.add(songMusic);
            }
        }
        SongsFragment.musicAdpater.updateList(myFiles);
        Album1Fragment.albumAdapter.updateList(myFiles);
        ArtistsFragment.artistsAdapter.updateList(myFiles);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SharedPreferences.Editor editor = getSharedPreferences(MY_SORT_PREF,MODE_PRIVATE).edit();
        switch ((item.getItemId()))
        {
            case R.id.by_name:
                editor.putString("sorting","sortByName");
                editor.apply();
                this.recreate();
            case R.id.by_date:
                editor.putString("sorting","sortByDate");
                editor.apply();
                this.recreate();
            case R.id.by_size:
                editor.putString("sorting","sortBySize");
                editor.apply();
                this.recreate();
        }

        return super.onOptionsItemSelected(item);
    }
}