package com.xiaopeng.networkmonitor.netprobe.http;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@Retention(RetentionPolicy.SOURCE)
/* loaded from: classes.dex */
public @interface HttpMethod {
    public static final String HTTP_METHOD_GET = "GET";
    public static final String HTTP_METHOD_HEAD = "HEAD";
    public static final String HTTP_METHOD_POST = "POST";
}
