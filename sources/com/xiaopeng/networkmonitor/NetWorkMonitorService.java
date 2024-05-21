package com.xiaopeng.networkmonitor;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import com.xiaopeng.lib.framework.moduleinterface.configurationmodule.ConfigurationChangeEvent;
import com.xiaopeng.lib.framework.moduleinterface.configurationmodule.IConfigurationData;
import com.xiaopeng.networkmonitor.netprobe.ConfigConstants;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
/* loaded from: classes.dex */
public class NetWorkMonitorService extends Service {
    private static final String TAG = "NetworkMonitorService";
    private NetworkStateChangeReceiver mBroadcastReceiver;
    private ConnectivityManager mConnectMgr;
    private Context mContext;
    private NetworkProbeHandler mProbeHandler;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class NetworkStateChangeReceiver extends BroadcastReceiver {
        private NetworkStateChangeReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(NetWorkMonitorService.TAG, "onReceive(): action = " + action);
            if ("android.net.conn.CONNECTIVITY_CHANGE".equals(action)) {
                NetWorkMonitorService.this.mProbeHandler.sendEmptyMessage(8);
            }
        }
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        this.mContext = getApplicationContext();
        this.mConnectMgr = (ConnectivityManager) this.mContext.getSystemService("connectivity");
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, "onStartCommand(): startId=" + startId);
        startProbeThread();
        requestNetworkState();
        String config = intent != null ? intent.getStringExtra(ConfigConstants.KEY_PROBE_CONFIG) : null;
        if (!TextUtils.isEmpty(config)) {
            Message msg = this.mProbeHandler.obtainMessage(2, config);
            msg.sendToTarget();
            return 1;
        }
        return 1;
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind()");
        return null;
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy()");
        this.mContext.unregisterReceiver(this.mBroadcastReceiver);
        this.mProbeHandler.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void requestNetworkState() {
        Log.d(TAG, "requestNetworkState()");
        this.mBroadcastReceiver = new NetworkStateChangeReceiver();
        IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        this.mContext.registerReceiver(this.mBroadcastReceiver, filter);
    }

    private void startProbeThread() {
        Log.d(TAG, "startProbeThread()");
        HandlerThread thread = new HandlerThread("NetworkProbe");
        thread.start();
        this.mProbeHandler = new NetworkProbeHandler(this.mContext, thread.getLooper());
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onConfigurationChanged(ConfigurationChangeEvent event) {
        List<IConfigurationData> data = event.getChangeList();
        if (data != null && data.size() > 0) {
            Log.d(TAG, "onConfigurationChanged()");
            for (IConfigurationData config : data) {
                if (ConfigConstants.KEY_PROBE_CONFIG.equals(config.getKey())) {
                    Message msg = this.mProbeHandler.obtainMessage(2);
                    msg.obj = config.getValue();
                    msg.sendToTarget();
                }
            }
        }
    }

    private boolean isNetworkConnected() {
        NetworkInfo ni = this.mConnectMgr.getActiveNetworkInfo();
        boolean ret = ni != null && ni.isConnected();
        Log.d(TAG, "isNetworkConnected(): ret = " + ret);
        return ret;
    }
}
