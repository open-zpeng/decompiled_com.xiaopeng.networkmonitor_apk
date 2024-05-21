package com.xiaopeng.networkmonitor.netprobe.dns;

import android.text.TextUtils;
import com.xiaopeng.networkmonitor.netprobe.ProbeTaskConfig;
import com.xiaopeng.networkmonitor.utils.NetUtils;
/* loaded from: classes.dex */
public class DnsTaskConfig extends ProbeTaskConfig {
    private static final boolean DEFAULT_DATA_USAGE_LIMIT = true;
    public static final String LOCAL_NAME_SERVER = "LOCALNS";
    public static final String NONLOCAL_NAME_SERVER = "NS";
    private static final int TASK_PARAM_DATA_LIMIT = 4;
    private static final int TASK_PARAM_NAME_SERVER = 0;
    private static final int TASK_PARAM_PRESERVE = 2;
    private static final int TASK_PARAM_SERVER_IP = 1;

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

    public void setNameServer(String nameServer) {
        setTaskParam(0, nameServer);
    }

    public String getNameServer() {
        return getTaskParam(0);
    }

    public void setDnsServer(String serverIp) {
        setTaskParam(1, serverIp);
    }

    public String getDnsServer() {
        String server = getTaskParam(1);
        if (TextUtils.isEmpty(server)) {
            return NetUtils.getDnsServer();
        }
        return server;
    }
}
