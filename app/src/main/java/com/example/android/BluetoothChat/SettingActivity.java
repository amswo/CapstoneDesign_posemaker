package com.example.android.BluetoothChat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
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

    public static int sound=1;
    public static int vib=1;

    // fonts
    TextView setting_textview;
    TextView sound_textview;
    TextView vibration_textview;
    TextView app_textview;
    TextView version_textview;
    TextView v_textview;

    // sound
    SoundPool pool;
    int ddok;

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

        vide = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        final ToggleButton stb = (ToggleButton) this.findViewById(R.id.sound_toggleButton);
        final ToggleButton vtb = (ToggleButton) this.findViewById(R.id.vibration_toggleButton);


        //객체를 생성
        pool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        //ddok에 사운드를 로드함
        ddok = pool.load(this, R.raw.smallsound, 1);

        stb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(stb.isChecked()) {
                    pool.play(ddok, 1, 1, 0, 0, 1f);
                } else {
                    pool.stop(ddok);
                }
            }
        });

        vtb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (vtb.isChecked()) {
                    vib = 1;
                    vide.vibrate(500);
                } else {
                    vib = 0;
                }
            }
        });

    }

    public void Vibrator_basic(View v){
        vide.vibrate(1000);
    }

    public int getSound(){
        return sound;
    }

    public int getVib(){
        return vib;
    }
}

