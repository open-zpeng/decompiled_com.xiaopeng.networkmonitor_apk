package com.xiaopeng.networkmonitor.netprobe.model;
/* loaded from: classes.dex */
public class GsmCellInfo {
    private String mCid;
    private boolean mIsConnected;
    private int mLac;
    private int mMcc;
    private int mMnc;
    private int mRssi;

    public GsmCellInfo() {
        this.mMcc = -1;
        this.mMnc = -1;
        this.mCid = "";
        this.mLac = -1;
        this.mRssi = -1;
        this.mIsConnected = false;
    }

    public GsmCellInfo(int mcc, int mnc, String cid, int lac) {
        this.mMcc = mcc;
        this.mMnc = mnc;
        this.mCid = cid;
        this.mLac = lac;
        this.mRssi = -1;
        this.mIsConnected = false;
    }

    public void update(int mcc, int mnc, String cid, int lac, int rssi, boolean isConnected) {
        this.mMcc = mcc;
        this.mMnc = mnc;
        this.mCid = cid;
        this.mLac = lac;
        this.mRssi = rssi;
        this.mIsConnected = isConnected;
    }

    public void setMcc(int mcc) {
        this.mMcc = mcc;
    }

    public int getMcc() {
        return this.mMcc;
    }

    public void setMnc(int mnc) {
        this.mMnc = mnc;
    }

    public int getMnc() {
        return this.mMnc;
    }

    public void setCid(String cid) {
        this.mCid = cid;
    }

    public String getCid() {
        return this.mCid;
    }

    public void setLac(int lac) {
        this.mLac = lac;
    }

    public int getLac() {
        return this.mLac;
    }

    public void setRssi(int rssi) {
        this.mRssi = rssi;
    }

    public int getRssi() {
        return this.mRssi;
    }

    public void setIsConnected(boolean isConnected) {
        this.mIsConnected = isConnected;
    }

    public boolean isConnected() {
        return this.mIsConnected;
    }

    public String toString() {
        return this.mMcc + "," + this.mMnc + "," + this.mLac + "," + this.mCid + "," + this.mRssi + "," + (this.mIsConnected ? 1 : 0);
    }

    public boolean equals(Object obj) {
        if (obj instanceof GsmCellInfo) {
            if (obj == this) {
                return true;
            }
            GsmCellInfo other = (GsmCellInfo) obj;
            return this.mMcc == other.mMcc && this.mMnc == other.mMnc && this.mLac == other.mLac && this.mCid == other.mCid && this.mIsConnected == other.mIsConnected;
        }
        return false;
    }
}
