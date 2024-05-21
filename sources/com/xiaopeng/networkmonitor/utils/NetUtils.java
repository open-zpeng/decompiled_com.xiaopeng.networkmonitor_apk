package com.xiaopeng.networkmonitor.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import com.xiaopeng.networkmonitor.App;
import com.xiaopeng.networkmonitor.NetworkProbeHandler;
import com.xiaopeng.networkmonitor.netprobe.model.StatsEntry;
import java.net.InetAddress;
import java.util.Collection;
/* loaded from: classes.dex */
public class NetUtils {
    private static final int NETWORK_SUBTYPE_TBOX = 3;

    public static boolean isTboxNetworkConnected() {
        Context context = App.getInstance().getApplicationContext();
        ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService("connectivity");
        NetworkInfo ni = connectMgr.getActiveNetworkInfo();
        return ni != null && ni.getSubtype() == 3 && ni.isConnected();
    }

    public static boolean isNetworkConnected() {
        Context context = App.getInstance().getApplicationContext();
        ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService("connectivity");
        NetworkInfo ni = connectMgr.getActiveNetworkInfo();
        return ni != null && ni.isConnected();
    }

    public static String getDnsServer() {
        Context context = App.getInstance().getApplicationContext();
        ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService("connectivity");
        LinkProperties lp = connectMgr.getActiveLinkProperties();
        Collection<InetAddress> dnsServers = lp.getDnsServers();
        for (InetAddress ia : dnsServers) {
            if (ia.getAddress() != null) {
                return ia.getHostAddress();
            }
        }
        return null;
    }

    public static String getNetworkTypeName() {
        Context context = App.getInstance().getApplicationContext();
        ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService("connectivity");
        NetworkInfo ni = connectMgr.getActiveNetworkInfo();
        if (ni != null) {
            if (ni.getType() == 9 && ni.getSubtype() == 3) {
                return "MOBILE";
            }
            return ni.getTypeName();
        }
        return "";
    }

    public static void setTrafficStatsUidTag() {
        StatsEntry entry = NetworkProbeHandler.getTrafficStatsEntry();
        if (entry != null) {
            TrafficStats.setThreadStatsTag(entry.tag);
            TrafficStats.setThreadStatsUid(entry.uid);
        }
    }
}
