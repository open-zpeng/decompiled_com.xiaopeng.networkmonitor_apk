package com.xiaopeng.networkmonitor.netprobe.ping;

import com.xiaopeng.networkmonitor.netprobe.ProbeTaskConfig;
/* loaded from: classes.dex */
public class PingTaskConfig extends ProbeTaskConfig {
    private static final boolean DEFAULT_DATA_USAGE_LIMIT = true;
    private static final int DEFAULT_PING_COUNT = 4;
    private static final int TASK_PARAM_DATA_LIMIT = 4;
    private static final int TASK_PARAM_PING_COUNT = 0;
    private static final int TASK_PARAM_PING_SIZE = 1;
    private static final int TASK_PARAM_PRESERVE = 2;

    public void setPingCount(int count) {
        setTaskParam(0, String.valueOf(count));
    }

    public int getPingCount() {
        try {
            int count = Integer.parseInt(getTaskParam(0));
            return count;
        } catch (Exception e) {
            return 4;
        }
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
