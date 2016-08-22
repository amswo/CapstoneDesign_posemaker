package com.example.android.BluetoothChat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.TextView;

/**
 * Created by user on 2016-08-22.
 */
public class SettingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);


//        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//        vibrator.vibrate(3000);

        TextView textView1 = (TextView) findViewById(R.id.setting_textView);
//        Typeface typeface1 = Typeface.createFromAsset(getAssets(), "fonts/HANYGO230.ttf");
//        textView1.setTypeface(typeface1);

        TextView textView2 = (TextView) findViewById(R.id.myInformation_textview);
//        Typeface typeface2 = Typeface.createFromAsset(getAssets(), "fonts/HANYGO230.ttf");
//        textView2.setTypeface(typeface2);

        TextView textView3 = (TextView) findViewById(R.id.id_textView);
//        Typeface typeface3 = Typeface.createFromAsset(getAssets(), "fonts/HANYGO230.ttf");
//        textView3.setTypeface(typeface3);

        TextView textView4 = (TextView) findViewById(R.id.sound_textView);
//        Typeface typeface4 = Typeface.createFromAsset(getAssets(), "fonts/HANYGO230.ttf");
//        textView4.setTypeface(typeface4);

        TextView textView5 = (TextView) findViewById(R.id.vibration_textView);
//        Typeface typeface5 = Typeface.createFromAsset(getAssets(), "fonts/HANYGO230.ttf");
//        textView5.setTypeface(typeface5);

        TextView textView6 = (TextView) findViewById(R.id.app_textView);
//        Typeface typeface6 = Typeface.createFromAsset(getAssets(), "fonts/HANYGO230.ttf");
//        textView6.setTypeface(typeface6);

        TextView textView7 = (TextView) findViewById(R.id.version_textView);
//        Typeface typeface7 = Typeface.createFromAsset(getAssets(), "fonts/HANYGO230.ttf");
//        textView7.setTypeface(typeface7);

        TextView textView8 = (TextView) findViewById(R.id.V_textView);
//        Typeface typeface8 = Typeface.createFromAsset(getAssets(), "fonts/HANYGO230.ttf");
//        textView8.setTypeface(typeface8);

        TextView textView9 = (TextView) findViewById(R.id.logout_button);
//        Typeface typeface9 = Typeface.createFromAsset(getAssets(), "fonts/HANYGO230.ttf");
//        textView9.setTypeface(typeface9);


    }


}
