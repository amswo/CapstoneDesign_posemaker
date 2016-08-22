package com.example.android.BluetoothChat.vo;

/**
 * Created by user on 2016-08-04.
 */
public class Pose {
    private int _id;
    private int ax;
    private int ay;
    private int az;

    public int get_id() {
        return _id;
    }

    public int getAx() {
        return ax;
    }

    public int getAy() {
        return ay;
    }

    public int getAz() {
        return az;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public void setAx(int ax) {
        this.ax = ax;
    }

    public void setAy(int ay) {
        this.ay = ay;
    }

    public void setAz(int az) {
        this.az = az;
    }
}
