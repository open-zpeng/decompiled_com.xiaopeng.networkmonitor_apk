package com.xiaopeng.networkmonitor.netprobe;

import android.os.Handler;
import android.os.Looper;
import com.xiaopeng.networkmonitor.utils.NetUtils;
/* loaded from: classes.dex */
public abstract class ProbeTask extends Handler implements Runnable {
    public static final int EVENT_RETRY_PROBE = 1;

    @Override // java.lang.Runnable
    public abstract void run();

    public ProbeTask() {
    }

    public ProbeTask(Looper looper) {
        super(looper);
    }

    public void probe() {
        NetUtils.setTrafficStatsUidTag();
    }
}
