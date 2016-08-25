package com.example.android.BluetoothChat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
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
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.BluetoothChat.db.DBHelper;
import com.example.android.BluetoothChat.vo.Pose;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by user on 2016-08-22.
 */
public class InitialPoseActivity extends Activity {

    TextView setting_textview;
    TextView initialpose_textview;
    TextView poseposition;
    TextView shoulder;
    TextView waist;

    Button btnInitialPose;
    Button btnPoseStart;

    public boolean btnStart = false;
    public int compareCnt = 0;

    // 알람
//    private Button btn2;
//    private TextView time_info;
//
//    private Chronometer chronometer;
//    private LinearLayout ll;

    //DB
    private DBHelper dbHelper;
//    private Button btnSelectAllDatas;
//    private ListView IvPoses;

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

    public int difAx = 0;
    public int difAy = 0;
    public int difAz = 0;


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
    private CRobot_BluetoothService mChatService2 = null;
    protected Object TAG;


    // 로딩
    Handler handler = new Handler();
    int status = 0;
    Button button;
    ProgressDialog progressDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up the window layout
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.initial_pose);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);

        // Set up the custom title
        mTitle = (TextView) findViewById(R.id.title_left_text);
        mTitle.setText(R.string.app_name);
        mTitle = (TextView) findViewById(R.id.title_right_text);

        setContentView(R.layout.initial_pose);

        setting_textview = (TextView) findViewById(R.id.setting_textView);
        setting_textview.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/HANYGO230.ttf"));

        initialpose_textview = (TextView) findViewById(R.id.initialpose_textView);
        initialpose_textview.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/HANYGO230.ttf"));

        poseposition = (TextView) findViewById(R.id.poseposition_view);
        poseposition.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/HANYGO230.ttf"));

        shoulder = (TextView) findViewById(R.id.shoulder);
        shoulder.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/HANYGO230.ttf"));

        waist = (TextView) findViewById(R.id.waist);
        waist.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/HANYGO230.ttf"));


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }


        dbCreate();

        // alarm
//        time_info = (TextView) findViewById(R.id.chronometer);
//        btn2 = (Button) findViewById(R.id.buttonreset);
//
//        chronometer = (Chronometer) findViewById(R.id.chronometer);

        // 1초 마다 실행되는 메소드
        // 실행되는데 약간의 텀이 필요하기 때문에 정확하게 1초마다 실행하는데에 대한 부분은 장담못함.
        // 그래도 거의 정확하게 실행.
//        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
//            public void onChronometerTick(Chronometer chronometer) {
//                long elapseMills = SystemClock.elapsedRealtime() - chronometer.getBase();
//                DecimalFormat numFormat = new DecimalFormat("###,###");
//                String num = numFormat.format(30000);
//                String output = numFormat.format(elapseMills);
//                time_info.setText("Total sec : " + output);
//
//                if (output.startsWith("10")) {
//                    AlertDialog.Builder dialog = new AlertDialog.Builder(InitialPoseActivity.this);
//                    dialog.setTitle("올바르지 않은 자세를 30초간 유지했습니다.");
//                    dialog.setMessage("다시한번 바르게 앉아주세요");
//                    // OK 버튼 이벤트
//                    dialog.setPositiveButton("예", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                        }
//                    });
//                    dialog.show();
//                }
//            }
//        });
//
//        btn2.setOnClickListener(new Button.OnClickListener() {
//            public void onClick(View v) {
//                chronometer.stop();
//                chronometer.setBase(SystemClock.elapsedRealtime());
//            }
//        });
//


//        button = (Button)findViewById(R.id.initposebutton);
//
//        button.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                CreateProgressDialog();
//                ShowProgressDialog();
//                //loading(v);
//            }
//        });

    }

    //로딩
    public void CreateProgressDialog() {
        progressDialog = new ProgressDialog(InitialPoseActivity.this);
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(true);
        progressDialog.setMax(100);
        progressDialog.show();
    }

    public void ShowProgressDialog() {
        status = 0;

        new Thread(new Runnable() {

            public void run() {
                while (status < 100) {
                    status += 3;

                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        //TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    handler.post(new Runnable() {
                        public void run() {
                            progressDialog.setProgress(status);

                            if (status >= 100) {
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            }
        }).start();
    }

//    public void alarmStart() {
//        chronometer.setBase(SystemClock.elapsedRealtime());
//        chronometer.start();
//    }

    // DB 생성을 위한 함수
    public void dbCreate() {
        final String etDBName = "datatest"; // DB 이름
        dbHelper = new DBHelper(InitialPoseActivity.this, etDBName, null, 1); //etDBName.getText().toString() + "/mnt/sdcard/"
        dbHelper.testDB();
    }

    // DB에 data 입력을 위한 함수
    public void dataCreate(){
        LinearLayout layout = new LinearLayout(InitialPoseActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        int ax = initAx;
        int ay = initAy;
        int az = initAz;

        if (dbHelper == null) {
            dbHelper = new DBHelper(InitialPoseActivity.this, "TEST", null, 1);
        }

        Pose pose = new Pose();
        // db
        pose.setAx(ax);
        pose.setAy(ay);
        pose.setAz(az);

        dbHelper.addPose(pose);

        Toast.makeText(getApplicationContext(), "ax: "+ ax +", ay: "+ ay +", az: "+ az, Toast.LENGTH_SHORT).show();

    }

    // DB안 초기값data 가져오기
//    public void getInitialData(){
//        if (dbHelper == null) {
//            dbHelper = new DBHelper(InitialPoseActivity.this, "datatest", null, 1);
//        }
//
//        List<com.example.android.BluetoothChat.vo.Pose> poses = dbHelper.getAllPoses();
//    }
//

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



//        // '초기자세설정' 버튼 실행
        btnInitialPose = (Button)findViewById(R.id.initposebutton);
        btnInitialPose.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                Log.d("TX", "start");
                sendMessage1((byte) 'a');
                CreateProgressDialog();
                ShowProgressDialog();
            }
        });

        // '자세교정시작' 버튼 실행
        btnPoseStart = (Button)findViewById(R.id.btnPoseStart);
        btnPoseStart.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                Log.d("TX", "자세교정시작 START");
                sendMessage1((byte) 'a');
                btnStart = true;
            }
        });
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
        if (mChatService2 != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService2.getState() == CRobot_BluetoothService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService2.start();
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
        if (mChatService2 != null) mChatService2.stop();
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
                case MESSAGE_STATE_CHANGE:
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

                                    if(btnStart){
                                        compareDif(num1,num2,num3);
                                    }

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


    // 비교
    public void compareDif(int num1, int num2, int num3){
        difAx = Math.abs(initAx - num1);
        difAy = Math.abs(initAy - num2);
        difAz = Math.abs(initAz - num3);

        Log.d("RX:", "difAx:" + difAx +", difAy:"+ difAy+", difAz:"+difAz);

        if(difAx>=50 && difAy>=25 && difAz>=230){
            Toast.makeText(getApplicationContext(), "자세를 바르게 앉으세요", Toast.LENGTH_SHORT).show();

        }
    }


    // 초기값을 위한 계산함수
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
            dataCreate();
            Log.d("RX:", "initAx: "+ initAx+ ", initAy: "+ initAy +", initAz: "+ initAz);

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