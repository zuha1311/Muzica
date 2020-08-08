package com.example.spotified.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.spotified.R;

public class Settings extends AppCompatActivity {

    Switch sSwitch;
    String SETTINGS_PREF = "Shake Feature";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sSwitch=findViewById(R.id.switch1);

        SharedPreferences sharedPreferences = getSharedPreferences(SETTINGS_PREF,Context.MODE_PRIVATE);
        Boolean isAllowed = sharedPreferences.getBoolean("feature",false);
        sSwitch.setChecked(isAllowed);

       if(isAllowed)
       {
           sSwitch.setChecked(true);
       }
       else
       {
           sSwitch.setChecked(false);
       }
        sSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(SETTINGS_PREF,Context.MODE_PRIVATE).edit();
                    editor.putBoolean("feature",true);
                    editor.apply();

                }else
                {
                    SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(SETTINGS_PREF,Context.MODE_PRIVATE).edit();
                    editor.putBoolean("feature",false);
                    editor.apply();
                }

            }
        });
    }
}