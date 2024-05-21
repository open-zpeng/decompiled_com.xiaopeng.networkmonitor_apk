package com.xiaopeng.networkmonitor.netprobe.tcp;

import com.xiaopeng.networkmonitor.netprobe.ProbeTaskConfig;
/* loaded from: classes.dex */
public class TcpTaskConfig extends ProbeTaskConfig {
    private static final boolean DEFAULT_DATA_USAGE_LIMIT = true;
    private static final int DEFAULT_TCP_PORT = 80;
    private static final int TASK_PARAM_DATA_LIMIT = 4;
    private static final int TASK_PARAM_PORT = 0;
    private static final int TASK_PARAM_RESERVE = 1;

    public int getTcpPort() {
        try {
            int port = Integer.parseInt(getTaskParam(0));
            return port;
        } catch (Exception e) {
            return DEFAULT_TCP_PORT;
        }
    }

    public void setTcpPort(int port) {
        setTaskParam(0, String.valueOf(port));
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
}
