package com.xiaopeng.lib.http.server;

import android.support.v4.app.NotificationCompat;
import com.google.gson.annotations.SerializedName;
import com.lzy.okgo.cache.CacheEntity;
/* loaded from: classes.dex */
public class ServerBean {
    @SerializedName("code")
    private int mCode;
    @SerializedName(CacheEntity.DATA)
    private String mData;
    @SerializedName(NotificationCompat.CATEGORY_MESSAGE)
    private String mMsg;

    public int getCode() {
        return this.mCode;
    }

    public void setCode(int code) {
        this.mCode = code;
    }

    public String getData() {
        return this.mData;
    }

    public void setData(String data) {
        this.mData = data;
    }

    public String getMsg() {
        return this.mMsg;
    }

    public void setMsg(String msg) {
        this.mMsg = msg;
    }
}
