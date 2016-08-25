package com.example.android.BluetoothChat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.BluetoothChat.db.DBHelper;
import com.example.android.BluetoothChat.vo.Pose;


import java.util.List;

/**
 * Created by user on 2016-08-23.
 */
public class StartActivity extends Activity {
    //DB
    private DBHelper dbHelper;


    // 초기값을 위한 변수들
    public int cnt=0;
    public int SumAx=0;
    public int SumAy=0;
    public int SumAz=0;
    public int num1=0;
    public int num2=0;
    public int num3=0;
    public int initAx=0;
    public int initAy=0;
    public int initAz=0;


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
    private CRobot_BluetoothService2 mChatService = null;
    protected Object TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up the window layout
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.start);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);

        // Set up the custom title
        mTitle = (TextView) findViewById(R.id.title_left_text);
        mTitle.setText(R.string.app_name);
        mTitle = (TextView) findViewById(R.id.title_right_text);

        setContentView(R.layout.start);


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

//        dbCreate();


    }


    // DB 생성을 위한 함수
    public void dbCreate() {
        final String etDBName = "datatest"; // DB 이름
        dbHelper = new DBHelper(StartActivity.this, etDBName, null, 1); //etDBName.getText().toString() + "/mnt/sdcard/"
        dbHelper.testDB();
    }

    // DB에 data 입력을 위한 함수
    public void dataCreate(){
        LinearLayout layout = new LinearLayout(StartActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        int ax = initAx;
        int ay = initAy;
        int az = initAz;

        if (dbHelper == null) {
            dbHelper = new DBHelper(StartActivity.this, "TEST", null, 1);
        }

        Pose pose = new Pose();
        // db
        pose.setAx(ax);
        pose.setAy(ay);
        pose.setAz(az);

        dbHelper.addPose(pose);

        Toast.makeText(getApplicationContext(), "ax: "+ ax +", ay: "+ ay +", az: "+ az, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onStart() {
        super.onStart();
        // 블루투스 기능 잠시 꺼놓음
//         If BT is not on, request that it be enabled.
//         setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled())
        {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);


            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        }
        else {
            if (mChatService == null) setupChat();
        }
//
//
//        // 자세 '설정하기' 버튼
//        btnInitialPose = (Button)findViewById(R.id.initposebutton);
//        btnInitialPose.setOnClickListener(new Button.OnClickListener() {
//            public void onClick(View v) {
//                Log.d("TX", "start");
//                sendMessage1((byte) 'a');
//
//            }
//        });
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == CRobot_BluetoothService2.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

    private void setupChat() {
        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new CRobot_BluetoothService2(this, mHandler);

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
        if (mChatService.getState() != CRobot_BluetoothService2.STATE_CONNECTED) {
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
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case CRobot_BluetoothService2.STATE_CONNECTED:
                            mTitle.setText(R.string.title_connected_to);
                            mTitle.append(mConnectedDeviceName);
                            mConversationArrayAdapter.clear();
                            break;
                        case CRobot_BluetoothService2.STATE_CONNECTING:
                            mTitle.setText(R.string.title_connecting);
                            break;
                        case CRobot_BluetoothService2.STATE_LISTEN:
                        case CRobot_BluetoothService2.STATE_NONE:
                            mTitle.setText(R.string.title_not_connected);
                            break;
                    }
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);

                    strRxData += readMessage;
                    if (RxCount++ > 100)
                    {
                        String[] separated = strRxData.split("\n");

                        // 300 중에 한번만 처리
                        for (int i = 0; i < separated.length - 1; i++)
                        {

                            String[] sData = separated[i].split(",");
                            if (sData.length == 6) {
                                if ((sData[0] != "") && (sData[1] !="") && (sData[2] != "")) {
                                    Log.d("RX:", "AX:" + sData[0] + ", AY:" + sData[1] + ", AZ:" + sData[2] + ", GX:" + sData[3] + ", GY:" + sData[4] + ", GZ:" + sData[5]);

                                    num1 = Integer.parseInt(sData[0]);
                                    num2 = Integer.parseInt(sData[1]);
                                    num3 = Integer.parseInt(sData[2]);
                                    calculate(num1, num2, num3);

                                    strRxData = "";
                                    break;
                                }
                            }
                            else
                            {
                                continue;
                            }

                        }
                        RxCount = 0;
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


    //     초기값을 위한 계산함수
    public void calculate (int cnum1, int cnum2, int cnum3){
        SumAx += cnum1;
        SumAy += cnum2;
        SumAz += cnum3;

        cnt++;

        if(cnt==10){
            initAx = SumAx/cnt;
            initAy = SumAy/cnt;
            initAz = SumAz / cnt;

//            ((TextView) findViewById(R.id.tv_Data)).setText(String.format("%6d", cnt) + String.format("%6d", initAx) +
//                    String.format("%6d", initAy) + String.format("%6d", initAz));
//            dataCreate();

        }
        else{
            //((TextView) findViewById(R.id.tv_Data)).setText(String.format("%6d", cnt) + String.format("%6d", SumAx) +
            //String.format("%6d", SumAy) + String.format("%6d", SumAz));
        }
    }

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