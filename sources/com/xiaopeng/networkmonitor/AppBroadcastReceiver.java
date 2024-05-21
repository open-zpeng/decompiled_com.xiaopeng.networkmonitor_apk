package com.xiaopeng.networkmonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.Log;
/* loaded from: classes.dex */
public class AppBroadcastReceiver extends BroadcastReceiver {
    private static final String PRODUCT_NAME = "ro.product.name";
    private static final String TAG = "NetworkMonitorReceiver";

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.v(TAG, "onReceive(): action = " + action);
        if ("com.xiaopeng.xui.businessevent".equals(action) || (!isXuiStarted() && "android.intent.action.BOOT_COMPLETED".equals(action))) {
            Intent service = new Intent(context, NetWorkMonitorService.class);
            Bundle extras = intent.getExtras();
            if (extras != null) {
                service.putExtras(extras);
            }
            service.setAction(action);
            context.startService(service);
        }
    }

    private boolean isXuiStarted() {
        String prodName = SystemProperties.get(PRODUCT_NAME, "");
        return prodName.equalsIgnoreCase("e28");
    }
}
