package com.xiaopeng.networkmonitor.netprobe;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@Retention(RetentionPolicy.SOURCE)
/* loaded from: classes.dex */
public @interface ProbeState {
    public static final int PROBE_STATE_DONE = 2;
    public static final int PROBE_STATE_IDLE = 0;
    public static final int PROBE_STATE_RUNNING = 1;
    public static final int PROBE_STATE_WAITING = 3;
}
