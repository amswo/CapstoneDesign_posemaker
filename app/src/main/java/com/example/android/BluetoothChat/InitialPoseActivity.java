package com.example.android.BluetoothChat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by user on 2016-08-22.
 */
public class InitialPoseActivity extends Activity {
    TextView poseposition;
    TextView shoulder;
    TextView waist;
    Button btnInitialPose;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initial_pose);

        poseposition = (TextView)findViewById(R.id.poseposition_view);
//        poseposition.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/HANYGO230.ttf"));

        shoulder = (TextView)findViewById((R.id.shoulder_view));
//        shoulder.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/HANYGO230.ttf"));

        waist = (TextView)findViewById(R.id.waist_view);
//        waist.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/HANYGO230.ttf"));

        TextView textView1 = (TextView) findViewById(R.id.setting_textView);
//        Typeface typeface1 = Typeface.createFromAsset(getAssets(), "HANYGO230.ttf");
//        textView1.setTypeface(typeface1);

        TextView textView2 = (TextView) findViewById(R.id.myInformation_textview);
//        Typeface typeface2 = Typeface.createFromAsset(getAssets(), "HANYGO230.ttf");
//        textView2.setTypeface(typeface2);

        btnInitialPose = (Button)findViewById(R.id.initposebutton);
        btnInitialPose.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent(getApplication(), HomeActivity.class);
                startActivity(intent);
            }
        });



    }
}