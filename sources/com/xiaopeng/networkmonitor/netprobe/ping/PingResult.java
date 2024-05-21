package com.xiaopeng.networkmonitor.netprobe.ping;

import java.text.DecimalFormat;
/* loaded from: classes.dex */
public class PingResult {
    private static final DecimalFormat sDf = new DecimalFormat("0.00");
    float averRtt;
    float maxRtt = -1.0f;
    float minRtt = 2.14748365E9f;

    public String toString() {
        return sDf.format(this.maxRtt) + "/" + sDf.format(this.minRtt) + "/" + sDf.format(this.averRtt);
    }
}
