package com.xiaopeng.networkmonitor.netprobe.model;

import com.google.gson.annotations.SerializedName;
import com.xiaopeng.networkmonitor.netprobe.ConfigConstants;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class ProbeResult {
    @SerializedName("allow_restart_lte")
    private boolean allowRestartLte;
    @SerializedName("success")
    private boolean isSuccess;
    @SerializedName("probe_version")
    private String probeVersion;
    @SerializedName("report_info")
    private List<String> reportInfo = new ArrayList();
    @SerializedName("retry_times")
    private int retryTimes;
    @SerializedName("rtt")
    private int rtt;
    @SerializedName("status_code")
    private int statusCode;
    @SerializedName(ConfigConstants.KEY_TASK_ID)
    private int taskId;
    @SerializedName(ConfigConstants.KEY_TASK_NAME)
    private String taskName;
    @SerializedName("task_run_time")
    private long taskRunTime;
    @SerializedName(ConfigConstants.KEY_TARGET)
    private String url;

    public ProbeResult() {
        setProbeVersion(ConfigConstants.PROBE_CONFIG_VERSION);
    }

    public void setTaskRunTime(long taskRunTime) {
        this.taskRunTime = taskRunTime;
    }

    public long getTaskRunTime() {
        return this.taskRunTime;
    }

    public void setProbeVersion(String probeVersion) {
        this.probeVersion = probeVersion;
    }

    public String getProbeVersion() {
        return this.probeVersion;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getTaskId() {
        return this.taskId;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskName() {
        return this.taskName;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return this.url;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public boolean isSuccess() {
        return this.isSuccess;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    public int getRetryTimes() {
        return this.retryTimes;
    }

    public void setRtt(int rtt) {
        this.rtt = rtt;
    }

    public int getRtt() {
        return this.rtt;
    }

    public void setStatusCode(int code) {
        this.statusCode = code;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public void setAllowRestartLte(boolean allowRestartLte) {
        this.allowRestartLte = allowRestartLte;
    }

    public boolean isAllowRestartLte() {
        return this.allowRestartLte;
    }

    public List<String> getReportInfo() {
        return this.reportInfo;
    }

    public void addReportInfo(String reportInfo) {
        this.reportInfo.add(reportInfo);
    }

    public String toString() {
        return "ProbeResult {taskId=" + this.taskId + ",taskName=" + this.taskName + "retryTimes=" + this.retryTimes + ", rtt=" + this.rtt + "}";
    }

    public boolean equals(Object object) {
        if (object instanceof ProbeResult) {
            if (this == object) {
                return true;
            }
            ProbeResult other = (ProbeResult) object;
            return other.taskId == this.taskId && other.taskName.equals(this.taskName) && other.isSuccess == this.isSuccess && other.retryTimes == this.retryTimes && other.rtt == this.rtt && other.url.equals(this.url) && other.allowRestartLte == this.allowRestartLte;
        }
        return false;
    }
}
