package com.example.android.BluetoothChat;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.AsyncTask;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

/**
 * Created by user on 2016-08-11.
 */
public class AlarmActivity extends Activity {
    static final String TAG = "AlarmActivity";

    String rid;
    String senderId = "653402390386";

    Button btnregist,btnunregist;
    TextView tvmsgview;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm);

        btnActionListener btnListener = new btnActionListener();
        btnregist = (Button)findViewById(R.id.btn_regist);
        btnregist.setOnClickListener(btnListener);
        btnunregist = (Button)findViewById(R.id.btn_unregist);
        btnunregist.setOnClickListener(btnListener);
        tvmsgview = (TextView)findViewById(R.id.tv_msgview);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    class  btnActionListener implements View.OnClickListener{

        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.btn_regist:
                    regist();
                    break;
                case R.id.btn_unregist:
                    unregist();
                    break;
            }
        }

        void regist(){
            new AsyncTask<Void,Void,String>() {
                @Override
                protected String doInBackground(Void... params) {
                    String msg = "";

                    Log.d(TAG, msg);
                    try {
                        InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
                        rid = instanceID.getToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                        //instanceID.deleteToken(rid,GoogleCloudMessaging.INSTANCE_ID_SCOPE);
                        msg = "Device registered, registration ID=" + rid;

                        //발급받은 토큰을 server로 저장하는 함수 (미구현)
                        //RegistToServer();

                        //발급 받은 토큰을 Sharedpreference로 저장 (미구현)
                        //storeRegistrationId(context, regId);

                        Log.d(TAG, msg);
                    } catch (IOException ex) {
                        msg = "Error :" + ex.getMessage();
                    }
                    return msg;
                }
            }.execute(null, null, null);
        }

        void unregist(){
            new AsyncTask<Void,Void,String>() {
                @Override
                protected String doInBackground(Void... params) {
                    String msg = "";

                    Log.d(TAG, msg);
                    try {
                        InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
                        instanceID.deleteInstanceID();
                        msg = "Device unRegistered";

                        //server에 토큰 삭제 요청 (미구현)
                        //unRegistToServer();

                        //Sharedpreference로 저장된 토큰 제거 (미구현)
                        //deleteRegistrationId(context, regId);

                        Log.d(TAG, msg);
                    } catch (IOException ex) {
                        msg = "Error :" + ex.getMessage();
                    }
                    return msg;
                }
            }.execute(null, null, null);
        }
    }
}
