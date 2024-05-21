package com.xiaopeng.networkmonitor.netprobe;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@Retention(RetentionPolicy.SOURCE)
/* loaded from: classes.dex */
public @interface ProbeType {
    public static final String PROBE_TYPE_DNS = "DNS";
    public static final String PROBE_TYPE_HTTP = "HTTP";
    public static final String PROBE_TYPE_PING = "PING";
    public static final String PROBE_TYPE_TCP = "TCP";
}
