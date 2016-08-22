package com.example.android.BluetoothChat.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.android.BluetoothChat.vo.Pose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016-08-04.
 */
public class DBHelper extends SQLiteOpenHelper {
    private Context context;
    private Cursor cursor;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
        this.context = context;
    }

    public void onCreate(SQLiteDatabase db){
        StringBuffer sb = new StringBuffer();
        sb.append("CREATE TABLE TEST_TABLE(");
        sb.append("_ID INTEGER PRIMARY KEY AUTOINCREMENT,");
        sb.append("AX INTEGER NOT NULL,");
        sb.append("AY INTEGER NOT NULL,");
        sb.append("AZ INTEGER NOT NULL);");

        //SQL 실행
        db.execSQL(sb.toString());

//        Toast.makeText(context, "DB 생성 완료", Toast.LENGTH_SHORT).show();
}

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        Toast.makeText(context, "Version 올라감", Toast.LENGTH_SHORT).show();
    }

    public void testDB(){
        SQLiteDatabase db = getReadableDatabase();
    }


    public void addPose(Pose pose){
        SQLiteDatabase db = getWritableDatabase();

        StringBuffer sb = new StringBuffer();
        sb.append("INSERT INTO TEST_TABLE(");
        sb.append("AX,AY,AZ)");
        sb.append("VALUES (#AX#, #AY#, #AZ#)");

        String query = sb.toString();
        query = query.replace("#AX#", "'" + pose.getAx()+ "'");
        query = query.replace("#AY#", "'" + pose.getAy()+ "'");
        query = query.replace("#AZ#", "'" + pose.getAz()+ "'");

        db.execSQL(query);

//        Toast.makeText(context, pose.getAx(), Toast.LENGTH_LONG).show();
        Toast.makeText(context, "Insert 완료", Toast.LENGTH_SHORT).show();
    }


    // delete record 확실하지않다.
//    public void deletePose(Pose pose){
//        SQLiteDatabase db = getWritableDatabase();
//
//        db.delete("TEST_TABLE", "_id = ?", new String[]{String.valueOf(pose.get_id())});
//        db.close();
//    }

    public List<Pose> getAllPoses(){
               StringBuffer sb = new StringBuffer();
        sb.append("SELECT _ID, AX, AY, AZ FROM TEST_TABLE");

        SQLiteDatabase db= getReadableDatabase();

//         SELECT 실행
        cursor = db.rawQuery(sb.toString(), null);


        List<Pose> poses= new ArrayList<Pose>();

        Pose pose = null;

        while (cursor.moveToNext()){
            pose = new Pose();
            pose.set_id(cursor.getInt(0));
            pose.setAx(cursor.getInt(1));
            pose.setAy(cursor.getInt(2));
            pose.setAz(cursor.getInt(3));

            poses.add(pose);

        }

        return poses;
    }

}
