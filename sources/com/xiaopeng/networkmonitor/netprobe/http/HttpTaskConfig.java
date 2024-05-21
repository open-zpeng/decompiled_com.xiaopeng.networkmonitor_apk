package com.xiaopeng.networkmonitor.netprobe.http;

import com.xiaopeng.networkmonitor.netprobe.ProbeTaskConfig;
/* loaded from: classes.dex */
public class HttpTaskConfig extends ProbeTaskConfig {
    private static final int DEFAULT_CONNECT_TIMEOUT = 10000;
    private static final boolean DEFAULT_DATA_USAGE_LIMIT = true;
    private static final int DEFAULT_SIZE_LIMIT = 40960;
    private static final int TASK_PARAM_DATA_LIMIT = 4;
    private static final int TASK_PARAM_LIMIT = 1;
    private static final int TASK_PARAM_METHOD = 0;
    private static final int TASK_PARAM_POST_STRING = 2;
    private static final int TASK_PARAM_PRESERVE = 2;
    private static final int TASK_PARAM_TIMEOUT = 3;

    public String getHttpMethod() {
        return getTaskParam(0);
    }

    public void setHttpMethod(String method) {
        setTaskParam(0, method);
    }

    public int getSizeLimit() {
        try {
            int limit = Integer.parseInt(getTaskParam(1));
            return limit;
        } catch (Exception e) {
            return DEFAULT_SIZE_LIMIT;
        }
    }

    public void setSizeLimit(int limit) {
        setTaskParam(1, String.valueOf(limit));
    }

    public int getConnectTimeout() {
        try {
            int timeout = Integer.parseInt(getTaskParam(3));
            return timeout;
        } catch (Exception e) {
            return 10000;
        }
    }

    public void setConnectTimeout(int timeout) {
        setTaskParam(3, String.valueOf(timeout));
    }

    public boolean isDataUsageLimit() {
        try {
            boolean isLimit = !Boolean.parseBoolean(getTaskParam(4));
            return isLimit;
        } catch (Exception e) {
            return true;
        }
    }

    public void setDataUsageLimit(boolean isLimit) {
        setTaskParam(4, String.valueOf(isLimit));
    }

    public void setPostJsonString(String json) {
        setTaskParam(2, json);
    }

    public String getPostJsonString() {
        return getTaskParam(2);
    }
}
