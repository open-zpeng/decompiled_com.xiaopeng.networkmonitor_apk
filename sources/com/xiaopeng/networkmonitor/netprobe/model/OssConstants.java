package com.xiaopeng.networkmonitor.netprobe.model;

import android.os.SystemProperties;
import android.text.TextUtils;
import com.xiaopeng.lib.utils.SystemPropertyUtil;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
/* loaded from: classes.dex */
public class OssConstants {
    public static final String OSS_BUCKET_CN = "hd1-xp-networktest";
    public static final String OSS_BUCKET_EU = "fr-xp-networktest";
    public static final String OSS_EP_CN = "http://oss-cn-hangzhou.aliyuncs.com";
    public static final String OSS_EP_EU = "http://oss-eu-central-1.aliyuncs.com";
    private static final String OSS_FILE_TYPE = ".txt";
    private static final String OSS_PROD = "prod";
    private static final String OSS_TEST = "test";
    private static final SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
    private static final SimpleDateFormat sTimeFormat = new SimpleDateFormat("HHmmss", Locale.CHINA);
    public static final HashMap<String, String> sBucketMaps = new HashMap<>();

    static {
        sBucketMaps.put("CN", OSS_BUCKET_CN);
        sBucketMaps.put("NO", OSS_BUCKET_EU);
        sBucketMaps.put("DK", OSS_BUCKET_EU);
        sBucketMaps.put("NL", OSS_BUCKET_EU);
        sBucketMaps.put("CH", OSS_BUCKET_EU);
    }

    public static String buildRemoteOssFileName() {
        StringBuilder stringBuilder = new StringBuilder();
        int binType = SystemProperties.getInt("ro.xiaopeng.special", 1);
        if (binType == 4) {
            stringBuilder.append(OSS_TEST);
        } else {
            stringBuilder.append(OSS_PROD);
        }
        long current = System.currentTimeMillis();
        String date = sDateFormat.format(Long.valueOf(current));
        String vin = SystemPropertyUtil.getVIN();
        stringBuilder.append(File.separator);
        stringBuilder.append(date);
        stringBuilder.append(File.separator);
        stringBuilder.append(sTimeFormat.format(Long.valueOf(current)));
        stringBuilder.append("_");
        stringBuilder.append(TextUtils.isEmpty(vin) ? SystemPropertyUtil.getIccid() : vin);
        stringBuilder.append(OSS_FILE_TYPE);
        return stringBuilder.toString();
    }
}
