package com.xiaopeng.networkmonitor.netprobe.model;

import com.google.gson.annotations.SerializedName;
/* loaded from: classes.dex */
public class NetInfo {
    @SerializedName("apn_alive_time")
    private long aliveTime;
    @SerializedName("cell_loc")
    private String cellId;
    @SerializedName("gsm_signal")
    private int gsmSignal;
    @SerializedName("is_lte_restarted")
    private boolean isLteRestarted;
    @SerializedName("lac")
    private String lac;
    @SerializedName("lte_signal")
    private int lteSignal;
    @SerializedName("mcc")
    private String mcc;
    @SerializedName("mnc")
    private String mnc;
    @SerializedName("network_type")
    private String netType;
    @SerializedName("traffic")
    private long traffic;

    public void setNetType(String netType) {
        this.netType = netType;
    }

    public String getNetType() {
        return this.netType;
    }

    public void setIsLteRestarted(boolean isLteRestarted) {
        this.isLteRestarted = isLteRestarted;
    }

    public boolean isLteRestarted() {
        return this.isLteRestarted;
    }

    public void setTraffic(long traffic) {
        this.traffic = traffic;
    }

    public long getTraffic() {
        return this.traffic;
    }

    public void setAliveTime(long aliveTime) {
        this.aliveTime = aliveTime;
    }

    public long getAliveTime() {
        return this.aliveTime;
    }

    public void setCellID(String cellId) {
        this.cellId = cellId;
    }

    public String getCellId() {
        return this.cellId;
    }

    public void setLac(String lac) {
        this.lac = lac;
    }

    public String getLac() {
        return this.lac;
    }

    public void setLteSignal(int signal) {
        this.lteSignal = signal;
    }

    public int getLteSignal() {
        return this.lteSignal;
    }

    public void setGsmSignal(int signal) {
        this.gsmSignal = signal;
    }

    public int getGsmSignal() {
        return this.gsmSignal;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    public String getMcc() {
        return this.mcc;
    }

    public void setMnc(String mnc) {
        this.mnc = mnc;
    }

    public String getMnc() {
        return this.mnc;
    }

    public String toString() {
        return "NetInfo {netType=" + this.netType + ",isLteRestarted=" + this.isLteRestarted + ", apnAliveTime=" + this.aliveTime + " }";
    }

    public boolean equals(Object object) {
        if (object instanceof NetInfo) {
            if (this == object) {
                return true;
            }
            NetInfo other = (NetInfo) object;
            return other.netType.equals(this.netType) && other.isLteRestarted == this.isLteRestarted && other.aliveTime == this.aliveTime && other.traffic == this.traffic && other.cellId.equals(this.cellId) && other.gsmSignal == this.gsmSignal && other.lteSignal == this.lteSignal && other.lac.equals(this.lac) && other.mcc.equals(this.mcc) && other.mnc.equals(this.mnc);
        }
        return false;
    }
}
