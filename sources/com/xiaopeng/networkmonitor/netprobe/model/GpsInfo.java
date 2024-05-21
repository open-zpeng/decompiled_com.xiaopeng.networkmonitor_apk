package com.xiaopeng.networkmonitor.netprobe.model;

import com.google.gson.annotations.SerializedName;
/* loaded from: classes.dex */
public class GpsInfo {
    @SerializedName("latitude")
    private float latitude;
    @SerializedName("longitude")
    private float longitude;
    @SerializedName("speed")
    private int speed;

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLongitude() {
        return this.longitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLatitude() {
        return this.latitude;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getSpeed() {
        return this.speed;
    }

    public String toString() {
        return "GpsInfo { longitude=" + this.longitude + ", latitude=" + this.latitude + "}";
    }

    public boolean equals(Object obj) {
        if (obj instanceof GpsInfo) {
            if (this == obj) {
                return true;
            }
            GpsInfo other = (GpsInfo) obj;
            return other.longitude == this.longitude && other.latitude == this.latitude && other.speed == this.speed;
        }
        return false;
    }
}
