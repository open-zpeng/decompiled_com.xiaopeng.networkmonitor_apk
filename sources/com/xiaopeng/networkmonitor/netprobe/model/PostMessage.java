package com.xiaopeng.networkmonitor.netprobe.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.concurrent.atomic.AtomicBoolean;
/* loaded from: classes.dex */
public class PostMessage {
    @SerializedName("gps")
    private GpsInfo gpsInfo;
    @Expose(deserialize = false, serialize = false)
    private AtomicBoolean mUploadState = new AtomicBoolean(false);
    @SerializedName("network")
    private NetInfo netInfo;
    @SerializedName("probe")
    private ProbeResult probeResult;
    @SerializedName("vin")
    private String vin;

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getVin() {
        return this.vin;
    }

    public void setGpsInfo(GpsInfo gpsInfo) {
        this.gpsInfo = gpsInfo;
    }

    public GpsInfo getGpsInfo() {
        return this.gpsInfo;
    }

    public void setProbeResult(ProbeResult result) {
        this.probeResult = result;
    }

    public ProbeResult getProbeResult() {
        return this.probeResult;
    }

    public void setNetInfo(NetInfo netInfo) {
        this.netInfo = netInfo;
    }

    public NetInfo getNetInfo() {
        return this.netInfo;
    }

    public void setUploadState(boolean uploadState) {
        this.mUploadState.set(uploadState);
    }

    public boolean isUploaded() {
        return this.mUploadState.get();
    }

    public String toString() {
        return "PostMessage {vin=" + this.vin + ", gpsInfo=" + this.gpsInfo + ",netInfo=" + this.netInfo + ", probeResult=" + this.probeResult + "}";
    }

    public boolean equals(Object object) {
        if (object instanceof PostMessage) {
            if (this == object) {
                return true;
            }
            PostMessage other = (PostMessage) object;
            return other.vin.equals(this.vin) && other.gpsInfo.equals(this.gpsInfo) && other.netInfo.equals(this.netInfo) && other.probeResult.equals(this.probeResult);
        }
        return false;
    }
}
