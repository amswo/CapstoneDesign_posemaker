/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.BluetoothChat;

import android.annotation.SuppressLint;
import android.app.*;
import android.bluetooth.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;

import com.example.android.BluetoothChat.db.DBHelper;
import com.example.android.BluetoothChat.vo.Pose;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * This is the main Activity that displays the current chat session.
 */
public class MainActivity extends Activity {

    //DB
    private Button btnCreateDatabase;
    private DBHelper dbHelper;
    private Button btnInsertData;
    private Button btnSelectAllDatas;
    private ListView IvPoses;


    // Home 버튼
    private Button btnHome;


    public int RxCount = 0;
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    // Layout Views
    private TextView mTitle;

    byte[] g_buffer = new byte[1024];
    int g_bufferLength = 0;


    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Array adapter for the conversation thread
    private ArrayAdapter<String> mConversationArrayAdapter;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private CRobot_BluetoothService mChatService = null;
    protected Object TAG;


    @Override
    public void onCreate(Bundle savedInstanceState) { // 
        super.onCreate(savedInstanceState);

        // Set up the window layout
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.main);// setContentView
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);

        // Set up the custom title
        mTitle = (TextView) findViewById(R.id.title_left_text);
        mTitle.setText(R.string.app_name);
        mTitle = (TextView) findViewById(R.id.title_right_text);

        setContentView(R.layout.main);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }


        // DB 생성
        btnCreateDatabase = (Button) findViewById(R.id.btnCreateDatabase);
        btnCreateDatabase.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final EditText etDBName = new EditText(MainActivity.this);
                etDBName.setHint("DB명을 입력하세요");

                // Dialog로 database의 이름을 입력 받음
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("Database 이름 입력")
                        .setMessage("Database 이름 입력")
                        .setView(etDBName)
                        .setPositiveButton("생성", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (etDBName.getText().toString().length() > 0) {
                                    dbHelper = new DBHelper(MainActivity.this,  etDBName.getText().toString(), null, 1); //etDBName.getText().toString() + "/mnt/sdcard/"
                                    dbHelper.testDB();
                                }
                            }
                        }).setNeutralButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();

            }
        });

        // Data 입력
        btnInsertData = (Button) findViewById(R.id.btnInsertData);
        btnInsertData.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LinearLayout layout = new LinearLayout(MainActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText etAX = new EditText(MainActivity.this);
                etAX.setHint("AX 입력");
                final EditText etAY = new EditText(MainActivity.this);
                etAY.setHint("AY 입력");
                final EditText etAZ = new EditText(MainActivity.this);
                etAZ.setHint("AZ 입력");

                layout.addView(etAX);
                layout.addView(etAY);
                layout.addView(etAZ);


                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("정보 입력")
                        .setView(layout)
                        .setPositiveButton("등록", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String ax = etAX.getText().toString();
                                String ay = etAY.getText().toString();
                                String az = etAZ.getText().toString();

                                Toast.makeText(MainActivity.this, ax + " " + ay + " " + az, Toast.LENGTH_LONG);

                                if (dbHelper == null) {
                                    dbHelper = new DBHelper(MainActivity.this, "TEST", null, 1);
                                }

                                Pose pose = new Pose();
                                pose.setAx(etAX.getText().toString());
                                pose.setAy(etAY.getText().toString());
                                pose.setAz(etAZ.getText().toString());

                                dbHelper.addPose(pose);


                            }
                        }).setNeutralButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();
            }
        });


        // Data 가져오기
        IvPoses = (ListView) findViewById(R.id.IvPoses);
        btnSelectAllDatas = (Button) findViewById(R.id.btnSelectAllDatas);
        btnSelectAllDatas.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                IvPoses.setVisibility(View.VISIBLE);

                if (dbHelper == null) {
                    dbHelper = new DBHelper(MainActivity.this, "TEST", null, 1);
                }

                List<com.example.android.BluetoothChat.vo.Pose> poses = dbHelper.getAllPoses();

                IvPoses.setAdapter(new PoseListAdapter(poses, MainActivity.this));

            }
        });


        // Home Button
        btnHome = (Button)findViewById(R.id.btnHome);
        btnHome.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


    }



	@Override
    public void onStart() {
        super.onStart();

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) 
        {   
          Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            
     
          startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        // Otherwise, setup the chat session
        } 
        else {
            if (mChatService == null) setupChat();
        }

        // mode1 버튼이 눌렸을 때
        findViewById(R.id.btnStart).setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Log.d("TX", "start");
                        sendMessage1((byte) 'a');
                    }
                }
        );
    }

    @Override
    public synchronized void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == CRobot_BluetoothService.STATE_NONE) {
              // Start the Bluetooth chat services
              mChatService.start();
            }
        }

    }

    private void setupChat() {
        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);

        // Initialize the BluetoothChatService to perform bluetooth connections 
        mChatService = new CRobot_BluetoothService(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

	@Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null) mChatService.stop();
    }

    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

	private void sendMessage1(byte _data ) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != CRobot_BluetoothService.STATE_CONNECTED) {
            // Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        mChatService.write((byte)_data);
        mOutStringBuffer.setLength(0);
    }


 String strRxData ="";
    // The Handler that gets information back from the BluetoothChatService
 private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
          switch (msg.what) {
            case MESSAGE_STATE_CHANGE: // 占쏙옙占쏙옙占쏙옙占�占쏙옙占쏙옙
                switch (msg.arg1) {
                case CRobot_BluetoothService.STATE_CONNECTED: 
                    mTitle.setText(R.string.title_connected_to);
                    mTitle.append(mConnectedDeviceName); 
                    mConversationArrayAdapter.clear();
                    break;
                case CRobot_BluetoothService.STATE_CONNECTING: 
                    mTitle.setText(R.string.title_connecting);
                    break;
                case CRobot_BluetoothService.STATE_LISTEN:
                case CRobot_BluetoothService.STATE_NONE:
                    mTitle.setText(R.string.title_not_connected);
                    break;
                }
                break;
              case MESSAGE_READ:
                  byte[] readBuf = (byte[]) msg.obj;
                  // construct a string from the valid bytes in the buffer
                  String readMessage = new String(readBuf, 0, msg.arg1);

                  strRxData += readMessage;

                  String[] separated = strRxData.split("\n");
                  if (separated.length > 1) {

                      // 300 중에 한번만 처리
                      if(RxCount++ >=300) {

                          for (int i = 0; i < separated.length - 1; i++) {
                              //   Log.d("RX" + i, separated[i]);

                              String[] sData = separated[i].split(",");
                              if (sData.length >= 6) {
                                  Log.d("RX:", "AX:" + sData[0] + ", AY:" + sData[1] + ", AZ:" + sData[2] + ", GX:" + sData[3] + ", GY:" + sData[4] + ", GZ:" + sData[5]);

                                  try {
                                      ((TextView) findViewById(R.id.tv_Data)).setText(
                                              String.format("%6s", sData[0]) + String.format("%6s", sData[1]) + String.format("%6s", sData[2]) + String.format("%6s", sData[3]) + String.format("%6s", sData[4]) + String.format("%6s", sData[5]));
                                  } catch (Exception e) {
                                  }

                              }
                          }
                          RxCount = 0;
                      }
                    //  strRxData = separated[separated.length - 1];
                  }

                  break;
                
                //mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to "
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                               Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                // Get the device MAC address
                String address = data.getExtras()
                                     .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                // Get the BLuetoothDevice object
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                // Attempt to connect to the device
                mChatService.connect(device);
            }
            break;
        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, so set up a chat session
                setupChat();
            } else {
                // User did not enable Bluetooth or an error occured
                Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { 
        switch (item.getItemId()) {
        case R.id.scan:
            // Launch the DeviceListActivity to see devices and do scan
            Intent serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            return true;
        case R.id.discoverable:
            // Ensure this device is discoverable by others
            ensureDiscoverable();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause()
    {
    	super.onPause();
    }






    // Adapter와 Holder
    private class PoseListAdapter extends BaseAdapter {
        private List<Pose> poses;
        private Context context;

        public PoseListAdapter(List<Pose> poses, Context context) {
            this.poses = poses;
            this.context = context;
        }

        public int getCount() {
            return this.poses.size();
        }

        public Object getItem(int position) {
            return this.poses.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = null;

            if (convertView == null) {
                convertView = new LinearLayout(context);
                ((LinearLayout) convertView).setOrientation(LinearLayout.HORIZONTAL);

                TextView tvId = new TextView(context);
                TextView tvAx = new TextView(context);
                TextView tvAy = new TextView(context);
                TextView tvAz = new TextView(context);

                ((LinearLayout) convertView).addView(tvId);
                ((LinearLayout) convertView).addView(tvAx);
                ((LinearLayout) convertView).addView(tvAy);
                ((LinearLayout) convertView).addView(tvAz);

                holder = new Holder();
                holder.tvId = tvId;
                holder.tvAx = tvAx;
                holder.tvAy = tvAy;
                holder.tvAz = tvAz;

                convertView.setTag(holder);

            } else {
                holder = (Holder) convertView.getTag();
            }

            Pose pose = (Pose) getItem(position);
            holder.tvId.setText(pose.get_id() + " ");
            holder.tvAx.setText(pose.getAx()+" ");
            holder.tvAy.setText(pose.getAy()+" ");
            holder.tvAz.setText(pose.getAz()+" ");

            return convertView;
        }
    }
    private class Holder{
        public TextView tvId;
        public TextView tvAx;
        public TextView tvAy;
        public TextView tvAz;
    }

}


