 package com.example.android.BluetoothChat;

import android.annotation.SuppressLint;
import android.app.*;
import android.bluetooth.*;
import android.content.*;

import android.graphics.Typeface;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

import com.example.android.BluetoothChat.db.DBHelper;
import com.example.android.BluetoothChat.vo.Pose;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
    TextView btnInitialPose;
    Button btnPoseType;
    TextView btnSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        startActivity(new Intent(this, SplashActivity.class));

        // 초기자세설정으로 이동, 글꼴설정
        btnInitialPose = (TextView) findViewById(R.id.btnInitialPose);
        btnInitialPose.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/HANYGO230.ttf"));
        btnInitialPose.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), InitialPoseActivity.class);
                startActivity(intent);
            }
        });

        // 자세유형분석
//        btnPoseType = (TextView) findViewById(R.id.btnPoseType);
//        btnPoseType.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/HANYGO230.ttf"));
//        btnPoseType.setOnClickListener(new Button.OnClickListener() {
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), StartActivity.class);
//                startActivity(intent);
//            }
//        });

        btnPoseType = (Button)findViewById(R.id.btnPoseType);
        btnPoseType.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/HANYGO230.ttf"));
        btnPoseType.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Toast.makeText(getApplicationContext(), "콘텐츠를 준비중입니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 환경설정으로 이동, 글꼴설정
        btnSetting = (TextView) findViewById(R.id.btnSetting);
        btnSetting.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/HANYGO230.ttf"));
        btnSetting.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(intent);
            }
        });

    }
}