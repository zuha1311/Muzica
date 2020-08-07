package com.example.spotified.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.spotified.R;

public class MainActivity extends AppCompatActivity {

    /*private ListView listview;
    private String songNames[];*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}



      /*  listview=findViewById(R.id.songs_layout);
        ArrayList<File> songs = readSongs(Environment.getExternalStorageDirectory());

        songNames = new String[songs.size()];
        for(int i=0; i<songs.size(); ++i){
            songNames[i] = songs.get(i).getName().toString().replace(".mp4" ,"").replace(".mp3","");

        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.song_layout,R.id.textView,songNames);

        listview.setAdapter(adapter);


    }

    private ArrayList<File> readSongs(File root){
        ArrayList<File> arrayList = new ArrayList<File>();
        File files[]= root.listFiles();

        for (File file : files){
            if(file.isDirectory()){
                arrayList.addAll(readSongs(file));
            }else{
                if(file.getName().endsWith(".mp3")||file.getName().endsWith(".mp4")){
                    arrayList.add(file);
                }
            }
        }
        return arrayList;
    }*/
