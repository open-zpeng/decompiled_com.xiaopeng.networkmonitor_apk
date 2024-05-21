package com.xiaopeng.networkmonitor.netprobe.model;

import android.support.v4.app.NotificationCompat;
import com.google.gson.annotations.SerializedName;
/* loaded from: classes.dex */
public class EncryptMessage {
    @SerializedName(NotificationCompat.CATEGORY_MESSAGE)
    private String encryptMessage;
    @SerializedName("time")
    private String time;

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return this.time;
    }

    public void setEncryptMessage(String encryptMessage) {
        this.encryptMessage = encryptMessage;
    }

    public String getEncryptMessage() {
        return this.encryptMessage;
    }

    public String toString() {
        return "EncryptMessage { time=" + this.time + ", message=" + this.encryptMessage + "}";
    }

    public boolean equals(Object object) {
        if (object instanceof EncryptMessage) {
            if (this == object) {
                return true;
            }
            EncryptMessage other = (EncryptMessage) object;
            return other.time.equals(this.time) && other.encryptMessage.equals(this.encryptMessage);
        }
        return false;
    }
}
