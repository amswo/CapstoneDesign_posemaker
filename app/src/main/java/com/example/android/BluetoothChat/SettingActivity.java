package com.example.android.BluetoothChat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * Created by user on 2016-08-22.
 */
public class SettingActivity extends Activity {
    Vibrator vide;
    public int vib = 1;

    // fonts
    TextView setting_textview;
    TextView id_textview;
    TextView sound_textview;
    TextView vibration_textview;
    TextView app_textview;
    TextView version_textview;
    TextView v_textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        // fonts
        setting_textview = (TextView) findViewById(R.id.setting_textView);
        setting_textview.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/HANYGO230.ttf"));

        sound_textview = (TextView) findViewById(R.id.sound_textView);
        sound_textview.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/HANYGO230.ttf"));

        vibration_textview = (TextView) findViewById(R.id.vibration_textView);
        vibration_textview.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/HANYGO230.ttf"));

        app_textview = (TextView) findViewById(R.id.app_textView);
        app_textview.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/HANYGO230.ttf"));

        version_textview = (TextView) findViewById(R.id.version_textView);
        version_textview.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/HANYGO230.ttf"));

        v_textview = (TextView) findViewById(R.id.V_textView);
        v_textview.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/HANYGO230.ttf"));


        vide = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        final MediaPlayer mediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.smallsound);
        final ToggleButton vtb = (ToggleButton)this.findViewById(R.id.vibration_toggleButton);

        ToggleButton musikknapp = (ToggleButton) findViewById(R.id.sound_toggleButton);
        musikknapp.setOnCheckedChangeListener (new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!mediaPlayer.isPlaying())
                        mediaPlayer.start();
                } else {
                    mediaPlayer.pause();
                }
        }
    });

        vtb.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(vtb.isChecked()){
                    vib = 1;
                    vide.vibrate(1000);
                }else{
                    vib = 0;
                }
            }
        });

    }

    public void Vibrator_basic(View v){
        vide.vibrate(1000);
    }

}
