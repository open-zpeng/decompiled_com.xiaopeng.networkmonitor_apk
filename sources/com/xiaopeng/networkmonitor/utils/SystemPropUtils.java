package com.xiaopeng.networkmonitor.utils;

import android.os.SystemProperties;
/* loaded from: classes.dex */
public class SystemPropUtils {
    public static String PROPERTY_CONFIG_RETRIVED = "sys.xp.prob_config";

    public static boolean isProbConfigRetrived() {
        return SystemProperties.getBoolean(PROPERTY_CONFIG_RETRIVED, false);
    }
}
