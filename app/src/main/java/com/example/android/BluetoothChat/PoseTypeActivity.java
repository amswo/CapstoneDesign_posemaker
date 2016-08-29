package com.example.android.BluetoothChat;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by user on 2016-08-29.
 */
public class PoseTypeActivity extends Activity {
    InitialPoseActivity pose = new InitialPoseActivity();
    public int good = pose.getTotalgood();
    public int bad = pose.getTotalbad();
    public int cnt = good + bad;

    public float goodPose = 0;
    public float badPose = 0;

    private ChartView mChartView;



//    private ViewGroup layoutGraphView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.posetype);

//        layoutGraphView = (ViewGroup) findViewById(R.id.layoutGraphView);

        LinearLayout dynamicLayout = (LinearLayout) findViewById(R.id.layout);

        //차트를 출력하는 뷰객체(ChartView) 생성
        mChartView = new ChartView(this);

        //리니어 레이아웃에 차트뷰 추가( 폭, 높이 가득차게 )
        dynamicLayout.addView(mChartView, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        //차트 그리기
        mChartView.makeChart();




    }

    public float getGoodPose(){
        goodPose = (good/cnt)*100;
        return goodPose;
    }

    public float getBadPose(){
        badPose = (bad/cnt)*100;
        return badPose;
    }




}
