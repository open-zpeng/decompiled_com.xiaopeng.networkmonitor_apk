package com.xiaopeng.networkmonitor.netprobe;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.List;
/* loaded from: classes.dex */
public class ProbeTaskConfig {
    @SerializedName(ConfigConstants.KEY_ALLOW_RESTART_LTE)
    private boolean mAllowDialog;
    @SerializedName(ConfigConstants.KEY_END_TIME)
    private Date mEndTime;
    @SerializedName(ConfigConstants.KEY_TASK_PARAM)
    private List<String> mParams;
    @SerializedName(ConfigConstants.KEY_INTERVAL)
    private long mProbeInterval;
    @Expose(deserialize = false, serialize = false)
    private int mProbeState;
    @SerializedName(ConfigConstants.KEY_TASK_TYPE)
    private String mProbeType;
    @SerializedName(ConfigConstants.KEY_TARGET)
    private String mProbeUrl;
    @Expose(deserialize = false, serialize = false)
    private int mRetryCount;
    @SerializedName(ConfigConstants.KEY_START_TIME)
    private Date mStartTime;
    @SerializedName(ConfigConstants.KEY_TASK_ID)
    private int mTaskId;
    @SerializedName(ConfigConstants.KEY_TASK_NAME)
    private String mTaskName;

    public ProbeTaskConfig() {
        this.mProbeInterval = ConfigConstants.DEFAULT_PROBE_INTERVAL;
    }

    private ProbeTaskConfig(int id) {
        this.mTaskId = id;
    }

    public void setTaskId(int id) {
        this.mTaskId = id;
    }

    public int getTaskId() {
        return this.mTaskId;
    }

    public void setProbeType(String type) {
        this.mProbeType = type;
    }

    public String getProbeType() {
        return this.mProbeType;
    }

    public void setTaskName(String name) {
        this.mTaskName = name;
    }

    public String getTaskName() {
        return this.mTaskName;
    }

    public void setStartTime(long startTime) {
        this.mStartTime = new Date(startTime);
    }

    public long getStartTime() {
        return this.mStartTime.getTime();
    }

    public void setEndTime(long endTime) {
        this.mEndTime = new Date(endTime);
    }

    public long getEndTime() {
        return this.mEndTime.getTime();
    }

    public void setProbeInterval(long probeInterval) {
        this.mProbeInterval = probeInterval;
    }

    public long getProbeInterval() {
        return this.mProbeInterval;
    }

    public void setProbeUrl(String url) {
        this.mProbeUrl = url;
    }

    public String getProbeUrl() {
        return this.mProbeUrl;
    }

    public void setTaskParams(List<String> params) {
        this.mParams = params;
    }

    public List<String> getTaskParams() {
        return this.mParams;
    }

    public void setTaskParam(int idx, String val) {
        if (idx >= 0 && idx < this.mParams.size()) {
            this.mParams.set(idx, val);
        }
    }

    public String getTaskParam(int idx) {
        if (idx >= 0 && idx < this.mParams.size()) {
            return this.mParams.get(idx);
        }
        return null;
    }

    public void setAllowDialog(boolean needRestart) {
        this.mAllowDialog = needRestart;
    }

    public boolean getAllowDialog() {
        return this.mAllowDialog;
    }

    public void incRetryCount(int count) {
        this.mRetryCount += count;
    }

    public int getRetryCount() {
        return this.mRetryCount;
    }

    public void setProbeState(int state) {
        synchronized (this) {
            this.mProbeState = state;
        }
    }

    public int getProbeState() {
        int i;
        synchronized (this) {
            i = this.mProbeState;
        }
        return i;
    }

    public boolean allowToRun() {
        int i = this.mProbeState;
        return (i == 3 || i == 1) ? false : true;
    }

    public boolean isValidTask() {
        return this.mEndTime.getTime() > System.currentTimeMillis() && this.mEndTime.after(this.mStartTime);
    }

    public String toString() {
        return "ProbeTaskConfig { id=" + this.mTaskId + ", probeType=" + this.mProbeType + ", startTime=" + this.mStartTime + ", interval=" + this.mProbeInterval + ", endTime=" + this.mEndTime + ", url=" + this.mProbeUrl + "}";
    }

    public boolean equals(Object object) {
        if ((object instanceof ProbeTaskConfig) && this != object) {
            ProbeTaskConfig other = (ProbeTaskConfig) object;
            return other.mTaskId == this.mTaskId && other.mTaskName.equals(this.mTaskName) && other.mProbeUrl.equals(this.mProbeUrl) && other.mProbeInterval == this.mProbeInterval && other.mStartTime == this.mStartTime && other.mEndTime == this.mEndTime && other.mAllowDialog == this.mAllowDialog && other.mParams.equals(this.mParams) && other.mProbeType.equals(this.mProbeType);
        }
        return false;
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private ProbeTaskConfig task;

        public Builder() {
            this.task = new ProbeTaskConfig();
        }

        public Builder(int id) {
            this.task = new ProbeTaskConfig(id);
        }

        public void setTaskId(int id) {
            this.task.setTaskId(id);
        }

        public void setProbeType(String type) {
            this.task.setProbeType(type);
        }

        public void setTaskName(String name) {
            this.task.setTaskName(name);
        }

        public void setStartTime(long startTime) {
            this.task.setStartTime(startTime);
        }

        public void setEndTime(long endTime) {
            this.task.setEndTime(endTime);
        }

        public void setProbeInterval(long interval) {
            this.task.setProbeInterval(interval);
        }

        public void setProbeUrl(String url) {
            this.task.setProbeUrl(url);
        }

        public void setAllowDialog(boolean needRestart) {
            this.task.setAllowDialog(needRestart);
        }

        public ProbeTaskConfig build() {
            return this.task;
        }
    }
}
