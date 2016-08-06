package com.example.android.BluetoothChat.vo;

/**
 * Created by user on 2016-08-04.
 */
public class Pose {
    private int _id;
    private String ax;
    private String ay;
    private String az;

    public int get_id() {
        return _id;
    }

    public String getAx() {
        return ax;
    }

    public String getAy() {
        return ay;
    }

    public String getAz() {
        return az;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public void setAx(String ax) {
        this.ax = ax;
    }

    public void setAy(String ay) {
        this.ay = ay;
    }

    public void setAz(String az) {
        this.az = az;
    }
}
