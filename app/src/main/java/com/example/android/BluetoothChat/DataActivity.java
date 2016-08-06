package com.example.android.BluetoothChat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.BluetoothChat.db.DBHelper;
import com.example.android.BluetoothChat.vo.Pose;

import java.util.List;

/**
 * Created by user on 2016-08-04.
 */
public class DataActivity extends Activity{

    //DB
    private Button btnCreateDatabase;
    private DBHelper dbHelper;
    private Button btnInsertData;
    private Button btnSelectAllDatas;
    private ListView IvPoses;

    public void onCreate(Bundle savedInstanceState) { //
        super.onCreate(savedInstanceState);

        setContentView(R.layout.data);



        // DB 생성
        btnCreateDatabase = (Button) findViewById(R.id.btnCreateDatabase);
        btnCreateDatabase.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                final EditText etDBName = new EditText(DataActivity.this);
                etDBName.setHint("DB명을 입력하세요");

                // Dialog로 database의 이름을 입력 받음
                AlertDialog.Builder dialog = new AlertDialog.Builder(DataActivity.this);
                dialog.setTitle("Database 이름 입력")
                        .setMessage("Database 이름 입력")
                        .setView(etDBName)
                        .setPositiveButton("생성", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (etDBName.getText().toString().length() > 0) {
                                    dbHelper = new DBHelper(DataActivity.this, etDBName.getText().toString(), null, 1);
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
        btnInsertData = (Button)findViewById(R.id.btnInsertData);
        btnInsertData.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                LinearLayout layout = new LinearLayout(DataActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText etAX = new EditText(DataActivity.this);
                etAX.setHint("AX 입력");
                final EditText etAY = new EditText(DataActivity.this);
                etAY.setHint("AY 입력");
                final EditText etAZ = new EditText(DataActivity.this);
                etAZ.setHint("AZ 입력");

                layout.addView(etAX);
                layout.addView(etAY);
                layout.addView(etAZ);


                AlertDialog.Builder dialog = new AlertDialog.Builder(DataActivity.this);
                dialog.setTitle("정보 입력")
                        .setView(layout)
                        .setPositiveButton("등록", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String ax = etAX.getText().toString();
                                String ay = etAY.getText().toString();
                                String az = etAZ.getText().toString();

                                Toast.makeText(DataActivity.this, ax + " " + ay + " " + az, Toast.LENGTH_LONG);

                                if(dbHelper == null){
                                    dbHelper = new DBHelper(DataActivity.this,"TEST",null,1);
                                }

                                Pose pose = new Pose();
                                pose.setAx(etAX.getText().toString());
                                pose.setAy(etAY.getText().toString());
                                pose.setAz(etAZ.getText().toString());

                                dbHelper.addPose(pose);


                            }
                        }).setNeutralButton("취소",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog,int which){

                    }
                }).create().show();
            }
        });

        IvPoses = (ListView)findViewById(R.id.IvPoses);
        btnSelectAllDatas = (Button)findViewById(R.id.btnSelectAllDatas);
        btnSelectAllDatas.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                IvPoses.setVisibility(View.VISIBLE);

                if (dbHelper == null) {
                    dbHelper = new DBHelper(DataActivity.this, "TEST", null, 1);
                }

                List<com.example.android.BluetoothChat.vo.Pose> poses = dbHelper.getAllPoses();

                IvPoses.setAdapter(new PoseListAdapter(poses, DataActivity.this));

            }
        });
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

                ((LinearLayout) convertView).addView(tvId);
                ((LinearLayout) convertView).addView(tvAx);

                holder = new Holder();
                holder.tvId = tvId;
                holder.tvAx = tvAx;

                convertView.setTag(holder);

            } else {
                holder = (Holder) convertView.getTag();
            }

            Pose pose = (Pose) getItem(position);
            holder.tvId.setText(pose.get_id() + "");
            holder.tvAx.setText(pose.getAx());

            return convertView;
        }
    }
    private class Holder{
        public TextView tvId;
        public TextView tvAx;
    }
}
