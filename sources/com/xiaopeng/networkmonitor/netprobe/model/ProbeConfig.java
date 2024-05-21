package com.xiaopeng.networkmonitor.netprobe.model;

import com.google.gson.annotations.SerializedName;
import com.xiaopeng.networkmonitor.netprobe.ConfigConstants;
import com.xiaopeng.networkmonitor.netprobe.ProbeTaskConfig;
import java.util.List;
/* loaded from: classes.dex */
public class ProbeConfig {
    @SerializedName("network_traffic")
    private long mDataUsageLimit;
    @SerializedName(ConfigConstants.KEY_ALLOW_RESTART_NETWORK)
    private boolean mIsAllowRestartLte;
    @SerializedName(ConfigConstants.KEY_PROBE_RETRY_TIMES)
    private int mProbeRetryTimes;
    @SerializedName(ConfigConstants.KEY_RESTART_NETWORK_INTERVAL)
    private int mRestartInterval;
    @SerializedName(ConfigConstants.KEY_PROBE_TASK)
    private List<ProbeTaskConfig> mTaskConfigs;

    public void setIsAllowRestartLte(boolean isAllowRestartLte) {
        this.mIsAllowRestartLte = isAllowRestartLte;
    }

    public boolean isAllowRestartLte() {
        return this.mIsAllowRestartLte;
    }

    public void setRestartInterval(int interval) {
        this.mRestartInterval = interval;
    }

    public int getRestartInterval() {
        return this.mRestartInterval;
    }

    public void setProbeRetryTimes(int times) {
        this.mProbeRetryTimes = times;
    }

    public int getProbeRetryTimes() {
        return this.mProbeRetryTimes;
    }

    public void setTaskConfig(List<ProbeTaskConfig> configs) {
        this.mTaskConfigs = configs;
    }

    public long getDataUsageLimit() {
        return this.mDataUsageLimit;
    }

    public void setDataUsageLimit(long limit) {
        this.mDataUsageLimit = limit;
    }

    public List<ProbeTaskConfig> getTaskConfig() {
        return this.mTaskConfigs;
    }
}
