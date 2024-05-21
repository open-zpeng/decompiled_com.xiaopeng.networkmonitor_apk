package com.xiaopeng.networkmonitor.netprobe.model;

import com.google.gson.annotations.SerializedName;
/* loaded from: classes.dex */
public class TboxApnInfo {
    public static final int NETWORK_2G = 0;
    public static final int NETWORK_3G = 1;
    public static final int NETWORK_4G = 2;
    private static final String NETWORK_TYPE_2G = "2G";
    private static final String NETWORK_TYPE_3G = "3G";
    private static final String NETWORK_TYPE_4G = "4G";
    private static final String NETWORK_TYPE_UNKNOWN = "unknown";
    public static final int NETWORK_UNKNOWN = -1;
    @SerializedName("apn1")
    private int apn1State;
    @SerializedName("apn2")
    private int apn2State;
    @SerializedName("apn3")
    private int apn3State;
    @SerializedName("imei")
    private String imei;
    @SerializedName("imsi")
    private String imsi;
    @SerializedName("network_type")
    private int networkType;
    @SerializedName("rsrp")
    private int rsrp;
    @SerializedName("rssi")
    private int rssi;

    public void setNetworkType(int type) {
        this.networkType = type;
    }

    public int getNetworkType() {
        return this.networkType;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getImei() {
        return this.imei;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getImsi() {
        return this.imsi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public int getRssi() {
        return this.rssi;
    }

    public void setRsrp(int rsrp) {
        this.rsrp = rsrp;
    }

    public int getRsrp() {
        return this.rsrp;
    }

    public String toString() {
        return "TboxApnInfo {networkType = " + this.networkType + ", imei = " + this.imei + ", imsi = " + this.imsi + ", rssi = " + this.rssi + ", rsrp = " + this.rsrp + "}";
    }

    public boolean equals(Object object) {
        if (object instanceof TboxApnInfo) {
            if (object == this) {
                return true;
            }
            TboxApnInfo other = (TboxApnInfo) object;
            return other.networkType == this.networkType && other.rssi == this.rssi && other.rsrp == this.rsrp && other.apn1State == this.apn1State;
        }
        return false;
    }

    public static String getNetworkType(int type) {
        if (type != 0) {
            if (type != 1) {
                if (type == 2) {
                    return NETWORK_TYPE_4G;
                }
                return "unknown";
            }
            return NETWORK_TYPE_3G;
        }
        return NETWORK_TYPE_2G;
    }
}
