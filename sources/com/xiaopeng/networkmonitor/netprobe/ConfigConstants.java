package com.xiaopeng.networkmonitor.netprobe;

import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class ConfigConstants {
    public static final String KEY_ALLOW_RESTART_LTE = "trigger_restart";
    public static final String KEY_ALLOW_RESTART_NETWORK = "allow_restart_network";
    public static final String KEY_END_TIME = "end_time";
    public static final String KEY_INTERVAL = "interval";
    public static final String KEY_PROBE_CONFIG = "probe_config";
    public static final String KEY_PROBE_RETRY_TIMES = "probe_retry_times";
    public static final String KEY_PROBE_TASK = "tasks";
    public static final String KEY_RESTART_NETWORK_INTERVAL = "restart_network_interval_minutes";
    public static final String KEY_SIZE_LIMIT = "limit";
    public static final String KEY_START_TIME = "start_time";
    public static final String KEY_TARGET = "target";
    public static final String KEY_TASK_ID = "task_id";
    public static final String KEY_TASK_NAME = "task_name";
    public static final String KEY_TASK_PARAM = "task_params";
    public static final String KEY_TASK_TYPE = "task_type";
    public static final String PROBE_CONFIG_VERSION = "2.0";
    public static final long NETWORK_PROBE_RETRY_DELAY = TimeUnit.MINUTES.toMillis(1);
    public static final long DEFAULT_PROBE_INTERVAL = TimeUnit.MINUTES.toMillis(2);
    public static final long DEFAULT_POPUP_INTERVAL = TimeUnit.MINUTES.toMillis(30);
}
