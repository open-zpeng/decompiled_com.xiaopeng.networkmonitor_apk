package com.xiaopeng.networkmonitor;

import android.app.Application;
import android.util.Log;
import com.xiaopeng.lib.framework.configuration.ConfigurationModuleEntry;
import com.xiaopeng.lib.framework.ipcmodule.IpcModuleEntry;
import com.xiaopeng.lib.framework.module.Module;
import com.xiaopeng.lib.framework.moduleinterface.configurationmodule.IConfiguration;
import com.xiaopeng.lib.framework.moduleinterface.ipcmodule.IIpcService;
import com.xiaopeng.lib.framework.moduleinterface.netchannelmodule.http.IHttp;
import com.xiaopeng.lib.framework.netchannelmodule.NetworkChannelsEntry;
import com.xiaopeng.lib.http.HttpsUtils;
/* loaded from: classes.dex */
public class App extends Application {
    public static final String APPLICATION_ID = "com.xiaopeng.networkmonitor";
    private static final String TAG = "NetworkMonitorApp";
    private static App sInstance;

    public static App getInstance() {
        return sInstance;
    }

    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        Log.d(TAG, "onCreate()");
        initXmartIpc();
        initNetModule();
        initConfigurationModule();
    }

    @Override // android.app.Application
    public void onTerminate() {
        super.onTerminate();
    }

    public IConfiguration getConfigInterface() {
        return (IConfiguration) Module.get(ConfigurationModuleEntry.class).get(IConfiguration.class);
    }

    private void initXmartIpc() {
        Module.register(IpcModuleEntry.class, new IpcModuleEntry(this));
        ((IIpcService) Module.get(IpcModuleEntry.class).get(IIpcService.class)).init();
    }

    private void initNetModule() {
        Module.register(NetworkChannelsEntry.class, new NetworkChannelsEntry());
        IHttp http = (IHttp) Module.get(NetworkChannelsEntry.class).get(IHttp.class);
        http.config().applicationContext(this).enableTrafficStats().apply();
        HttpsUtils.init(this, false);
    }

    private void initConfigurationModule() {
        Module.register(ConfigurationModuleEntry.class, new ConfigurationModuleEntry());
        getConfigInterface().init(this, APPLICATION_ID);
    }
}
