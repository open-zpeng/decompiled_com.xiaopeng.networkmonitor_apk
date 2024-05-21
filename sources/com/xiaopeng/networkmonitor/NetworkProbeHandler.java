package com.xiaopeng.networkmonitor;

import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.car.Car;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.tbox.CarTboxManager;
import android.car.hardware.vcu.CarVcuManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import com.alibaba.sdk.android.oss.common.RequestParameters;
import com.google.gson.Gson;
import com.xiaopeng.lib.framework.module.Module;
import com.xiaopeng.lib.framework.moduleinterface.netchannelmodule.remotestorage.IRemoteStorage;
import com.xiaopeng.lib.framework.netchannelmodule.NetworkChannelsEntry;
import com.xiaopeng.lib.utils.SystemPropertyUtil;
import com.xiaopeng.lib.utils.ThreadUtils;
import com.xiaopeng.networkmonitor.netprobe.ConfigConstants;
import com.xiaopeng.networkmonitor.netprobe.ConfigParser;
import com.xiaopeng.networkmonitor.netprobe.ProbeTaskConfig;
import com.xiaopeng.networkmonitor.netprobe.ProbeType;
import com.xiaopeng.networkmonitor.netprobe.SaveMessageTask;
import com.xiaopeng.networkmonitor.netprobe.UploadMessageTask;
import com.xiaopeng.networkmonitor.netprobe.dns.DnsProbeTask;
import com.xiaopeng.networkmonitor.netprobe.http.HttpProbeTask;
import com.xiaopeng.networkmonitor.netprobe.model.GpsInfo;
import com.xiaopeng.networkmonitor.netprobe.model.GsmCellInfo;
import com.xiaopeng.networkmonitor.netprobe.model.NetInfo;
import com.xiaopeng.networkmonitor.netprobe.model.OssConstants;
import com.xiaopeng.networkmonitor.netprobe.model.PostMessage;
import com.xiaopeng.networkmonitor.netprobe.model.ProbeConfig;
import com.xiaopeng.networkmonitor.netprobe.model.ProbeResult;
import com.xiaopeng.networkmonitor.netprobe.model.StatsEntry;
import com.xiaopeng.networkmonitor.netprobe.model.TboxApnInfo;
import com.xiaopeng.networkmonitor.netprobe.ping.PingProbeTask;
import com.xiaopeng.networkmonitor.netprobe.tcp.TcpProbeTask;
import com.xiaopeng.networkmonitor.utils.FileUtil;
import com.xiaopeng.networkmonitor.utils.NetUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONObject;
/* loaded from: classes.dex */
public class NetworkProbeHandler extends Handler {
    private static final String CACHE_FILE_NAME = "post_messages.txt";
    private static final int DEFAULT_TRAFFIC_STATS_TAG = -14286849;
    private static final int DEFAULT_TRAFFIC_STATS_UID = 100024;
    public static final int EVENT_FETCH_MESSAGE_CACHE = 9;
    public static final int EVENT_GET_PROBE_CONFIG = 1;
    public static final int EVENT_MESSAGE_SAVE_RESULT = 16;
    public static final int EVENT_NETWORK_PROBE_DONE = 4;
    public static final int EVENT_NETWORK_STATE_CHANGE = 8;
    public static final int EVENT_POST_PROBE_RESULT = 6;
    public static final int EVENT_PROBE_CONFIG_CHANGE = 2;
    public static final int EVENT_RETRY_NETWORK_PROBE = 5;
    public static final int EVENT_START_NETWORK_PROBE = 3;
    public static final int EVENT_UPLOAD_MESSAGE_DONE = 7;
    private static final String LOCAL_JASON_CONFIG_FILE = "probe_config.txt";
    private static final int MAX_CELL_INFO_COUNT = 3;
    private static final int MAX_POST_MESSAGE_CACHE = 10;
    private static final String MODEM_STATUS_KEY_CID = "CID";
    private static final String MODEM_STATUS_KEY_LAC = "LAC";
    private static final String MODEM_STATUS_KEY_MCC = "MCC";
    private static final String MODEM_STATUS_KEY_MNC = "MNC";
    private static final String MODEM_STATUS_KEY_TAC = "TAC";
    private static final String POST_MESSAGE_CACHE_DIR = "/data/netprobe";
    private static final String PROP_EVENT_KEY_CONTENT = "content";
    private static final String PROP_EVENT_KEY_NEIBOUR = "neighbours";
    private static final String PROP_EVENT_KEY_TBOX = "tbox";
    private static final String SYSTEM_PROP_CELL_INFO = "sys.xiaopeng.cell_info";
    private static final String SYSTEM_PROP_DEVICE_IMEI = "persist.sys.xiaopeng.imei";
    private static final String SYSTEM_PROP_IMSI = "sys.xiaopeng.imsi";
    private static final String SYSTEM_PROP_NETWORK_TYPE = "sys.xiaopeng.network_type";
    private static final String TAG = "NetworkProbe";
    private static StatsEntry sTrafficEntry;
    private TboxApnInfo mApnInfo;
    private int mCacheInDisk;
    private Car mCarInstance;
    private final ServiceConnection mCarServiceConnection;
    private Context mContext;
    private String mCountryCode;
    private int mCurrentNetworkType;
    private ArrayList<GsmCellInfo> mGsmCellInfos;
    private LocationManager mLocationMgr;
    private NetworkStatsManager mNetStatsMgr;
    private Set<PostMessage> mPostMessageCache;
    private ProbeConfig mProbeConfig;
    private ExecutorService mProbeExecutors;
    private IRemoteStorage mRemoteStorage;
    private SparseArray<ProbeTaskConfig> mTaskConfig;
    private long mTboxConnectionTime;
    private final CarTboxManager.CarTboxEventCallback mTboxEventCallback;
    private CarTboxManager mTboxManager;
    private CarVcuManager mVcuManager;
    private final WifiManager mWifiMgr;
    private static final long DEFAULT_POST_DELAY = TimeUnit.SECONDS.toMillis(10);
    private static final List<String> sCountryCodeList = new ArrayList();
    private static final Gson sGson = new Gson();

    static {
        sCountryCodeList.add("NO");
        sCountryCodeList.add("NL");
        sCountryCodeList.add("DK");
        sCountryCodeList.add("CH");
    }

    public NetworkProbeHandler(Context context, Looper looper) {
        super(looper);
        this.mGsmCellInfos = new ArrayList<>(3);
        this.mCurrentNetworkType = -1;
        this.mCarServiceConnection = new ServiceConnection() { // from class: com.xiaopeng.networkmonitor.NetworkProbeHandler.1
            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(NetworkProbeHandler.TAG, "onServiceConnected()");
                try {
                    NetworkProbeHandler.this.mTboxManager = (CarTboxManager) NetworkProbeHandler.this.mCarInstance.getCarManager("xp_tbox");
                    NetworkProbeHandler.this.mVcuManager = (CarVcuManager) NetworkProbeHandler.this.mCarInstance.getCarManager("xp_vcu");
                    NetworkProbeHandler.this.registerCarEvent();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName name) {
                Log.d(NetworkProbeHandler.TAG, "onServiceDisconnected()");
            }
        };
        this.mTboxEventCallback = new CarTboxManager.CarTboxEventCallback() { // from class: com.xiaopeng.networkmonitor.NetworkProbeHandler.2
            @Keep
            public void onChangeEvent(CarPropertyValue carPropertyValue) {
                int lac;
                int mcc;
                int mnc;
                int i;
                int propertyId = carPropertyValue.getPropertyId();
                if (propertyId != 554700817) {
                    try {
                        if (propertyId == 554700819) {
                            try {
                                JSONObject jsonObject = new JSONObject((String) carPropertyValue.getValue());
                                JSONObject currentCell = jsonObject.getJSONObject(NetworkProbeHandler.PROP_EVENT_KEY_TBOX);
                                String cellId = currentCell.optString(NetworkProbeHandler.MODEM_STATUS_KEY_CID);
                                String cellLac = currentCell.optString(NetworkProbeHandler.MODEM_STATUS_KEY_LAC);
                                String cellMcc = currentCell.optString(NetworkProbeHandler.MODEM_STATUS_KEY_MCC);
                                String cellMnc = currentCell.optString(NetworkProbeHandler.MODEM_STATUS_KEY_MNC);
                                if (TextUtils.isEmpty(cellLac)) {
                                    lac = 0;
                                } else {
                                    int lac2 = Integer.parseInt(cellLac);
                                    lac = lac2;
                                }
                                if (TextUtils.isEmpty(cellMcc)) {
                                    mcc = 0;
                                } else {
                                    int mcc2 = Integer.parseInt(cellMcc);
                                    mcc = mcc2;
                                }
                                if (TextUtils.isEmpty(cellMnc)) {
                                    mnc = 0;
                                } else {
                                    int mnc2 = Integer.parseInt(cellMnc);
                                    mnc = mnc2;
                                }
                                int rssi = NetworkProbeHandler.this.mApnInfo.getRssi();
                                if (NetworkProbeHandler.this.mGsmCellInfos.size() >= 1) {
                                    i = 0;
                                    GsmCellInfo cellInfo = (GsmCellInfo) NetworkProbeHandler.this.mGsmCellInfos.get(0);
                                    cellInfo.update(mcc, mnc, cellId, lac, rssi, true);
                                } else {
                                    GsmCellInfo cellInfo2 = new GsmCellInfo();
                                    i = 0;
                                    cellInfo2.update(mcc, mnc, cellId, lac, rssi, true);
                                    NetworkProbeHandler.this.mGsmCellInfos.add(0, cellInfo2);
                                }
                                Log.d(NetworkProbeHandler.TAG, "onChangeEvent(); current cell = " + NetworkProbeHandler.this.mGsmCellInfos.get(i));
                                JSONArray neighbourCell = jsonObject.getJSONArray(NetworkProbeHandler.PROP_EVENT_KEY_NEIBOUR);
                                for (int i2 = i; i2 < neighbourCell.length() && NetworkProbeHandler.this.mGsmCellInfos.size() < 3; i2++) {
                                    JSONObject jSONObject = (JSONObject) neighbourCell.get(i2);
                                    String cellId2 = currentCell.optString(NetworkProbeHandler.MODEM_STATUS_KEY_CID);
                                    String cellLac2 = currentCell.optString(NetworkProbeHandler.MODEM_STATUS_KEY_LAC);
                                    String cellMcc2 = currentCell.optString(NetworkProbeHandler.MODEM_STATUS_KEY_MCC);
                                    String cellMnc2 = currentCell.optString(NetworkProbeHandler.MODEM_STATUS_KEY_MNC);
                                    if (!TextUtils.isEmpty(cellLac2)) {
                                        lac = Integer.parseInt(cellLac2);
                                    }
                                    if (!TextUtils.isEmpty(cellMcc2)) {
                                        mcc = Integer.parseInt(cellMcc2);
                                    }
                                    if (!TextUtils.isEmpty(cellMnc2)) {
                                        mnc = Integer.parseInt(cellMnc2);
                                    }
                                    if (NetworkProbeHandler.this.mGsmCellInfos.size() >= i2 + 2) {
                                        GsmCellInfo cellInfo3 = (GsmCellInfo) NetworkProbeHandler.this.mGsmCellInfos.get(i2 + 1);
                                        cellInfo3.update(mcc, mnc, cellId2, lac, rssi, false);
                                    } else {
                                        GsmCellInfo cellInfo4 = new GsmCellInfo();
                                        cellInfo4.update(mcc, mnc, cellId2, lac, rssi, false);
                                        NetworkProbeHandler.this.mGsmCellInfos.add(i2 + 1, cellInfo4);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return;
                        }
                        return;
                    } finally {
                        NetworkProbeHandler.this.saveGsmCellInfo();
                    }
                }
                try {
                    NetworkProbeHandler.this.mApnInfo = (TboxApnInfo) NetworkProbeHandler.sGson.fromJson((String) carPropertyValue.getValue(), (Class<Object>) TboxApnInfo.class);
                    int netType = NetworkProbeHandler.this.mApnInfo.getNetworkType();
                    if (NetworkProbeHandler.this.mCurrentNetworkType != netType) {
                        NetworkProbeHandler.this.mCurrentNetworkType = netType;
                        NetworkProbeHandler.this.requestMobileNetworkStatus();
                    }
                    String imei = SystemProperties.get(NetworkProbeHandler.SYSTEM_PROP_DEVICE_IMEI, "");
                    if (!imei.equals(NetworkProbeHandler.this.mApnInfo.getImei())) {
                        SystemProperties.set(NetworkProbeHandler.SYSTEM_PROP_DEVICE_IMEI, NetworkProbeHandler.this.mApnInfo.getImei());
                    }
                    String imsi = SystemProperties.get(NetworkProbeHandler.SYSTEM_PROP_IMSI, "");
                    if (!imsi.equals(NetworkProbeHandler.this.mApnInfo.getImsi())) {
                        SystemProperties.set(NetworkProbeHandler.SYSTEM_PROP_IMSI, NetworkProbeHandler.this.mApnInfo.getImsi());
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }

            @Keep
            public void onErrorEvent(int i, int i1) {
                Log.d(NetworkProbeHandler.TAG, "onErrorEvent()");
            }
        };
        this.mContext = context;
        this.mLocationMgr = (LocationManager) this.mContext.getSystemService(RequestParameters.SUBRESOURCE_LOCATION);
        this.mNetStatsMgr = (NetworkStatsManager) this.mContext.getSystemService("netstats");
        this.mWifiMgr = (WifiManager) this.mContext.getSystemService("wifi");
        int[] uidTagInfo = this.mNetStatsMgr.getTrafficStatsInfo(App.APPLICATION_ID);
        if (uidTagInfo != null) {
            sTrafficEntry = new StatsEntry(uidTagInfo[0], uidTagInfo[1]);
        } else {
            sTrafficEntry = new StatsEntry(DEFAULT_TRAFFIC_STATS_UID, -14286849);
        }
        Log.d(TAG, "traffic stats info: " + sTrafficEntry);
        this.mTaskConfig = new SparseArray<>();
        this.mRemoteStorage = (IRemoteStorage) Module.get(NetworkChannelsEntry.class).get(IRemoteStorage.class);
        try {
            this.mRemoteStorage.initWithContext(App.getInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.mPostMessageCache = Collections.synchronizedSet(new HashSet());
        initCarControl();
        this.mCountryCode = "CN";
        sendEmptyMessage(9);
    }

    private String getCountryCode() {
        String country = "CN";
        try {
            country = this.mWifiMgr.getCountryCode();
        } catch (Exception e) {
        }
        Log.i(TAG, "getCountryCode(): country=" + country);
        return country.toUpperCase();
    }

    public static StatsEntry getTrafficStatsEntry() {
        return sTrafficEntry;
    }

    @Override // android.os.Handler
    public void handleMessage(Message msg) {
        Log.d(TAG, "handleMessage(): what=" + toEventString(msg.what));
        int i = msg.what;
        if (i == 1) {
            getProbeConfig();
        } else if (i == 2) {
            handleProbeConfigChange((String) msg.obj);
        } else if (i == 3) {
            handleNetworkProbe((ProbeTaskConfig) msg.obj);
        } else if (i == 4) {
            handleNetworkProbeDone((ProbeResult) msg.obj);
        } else if (i != 16) {
            switch (i) {
                case 6:
                    handlePostProbeResult((PostMessage) msg.obj);
                    return;
                case 7:
                    handleUploadMessageDone((PostMessage) msg.obj);
                    return;
                case 8:
                    handleNetworkStateChange();
                    return;
                case 9:
                    fetchMessageFromFile();
                    return;
                default:
                    return;
            }
        } else {
            handleMessageSaveResult((List) msg.obj);
        }
    }

    public void onDestroy() {
        Log.d(TAG, "onDestory()");
        unregisterCarEvent();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveGsmCellInfo() {
        Log.d(TAG, "saveGsmCellInfo()");
        int sz = this.mGsmCellInfos.size();
        if (sz > 3) {
            this.mGsmCellInfos.trimToSize();
        }
        if (sz > 0) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < sz - 1; i++) {
                builder.append(this.mGsmCellInfos.get(i).toString());
                builder.append(";");
            }
            builder.append(this.mGsmCellInfos.get(sz - 1));
            String cellStr = builder.toString();
            if (cellStr.length() >= 91) {
                do {
                    int idx = cellStr.lastIndexOf(59);
                    if (idx != -1) {
                        cellStr = cellStr.substring(0, idx);
                    } else {
                        cellStr = cellStr.substring(0, 91);
                    }
                } while (cellStr.length() >= 91);
                SystemProperties.set(SYSTEM_PROP_CELL_INFO, cellStr);
            }
            SystemProperties.set(SYSTEM_PROP_CELL_INFO, cellStr);
        }
    }

    private boolean isDataUsageOverLimit() {
        boolean ret = this.mProbeConfig.getDataUsageLimit() < getDataUsageCurrentMonth();
        Log.d(TAG, "isDataUsageOverLimit(): " + ret);
        return ret;
    }

    private long getDataUsageCurrentMonth() {
        GregorianCalendar current = new GregorianCalendar();
        current.setTime(new Date());
        GregorianCalendar firstDayOfMonth = new GregorianCalendar(current.get(1), current.get(2), 1, 0, 0, 0);
        long total = 0 + getDataUsage(firstDayOfMonth.get(14), current.get(14));
        Log.d(TAG, "getDataUsageCurrentMonth(): " + total);
        return total;
    }

    private long getDataUsage(long startTime, long endTime) {
        long total = 0;
        try {
            NetworkStats netStats = this.mNetStatsMgr.queryDetailsForUidTag(9, null, startTime, endTime, sTrafficEntry.uid, sTrafficEntry.tag);
            NetworkStats.Bucket bucket = new NetworkStats.Bucket();
            while (netStats.hasNextBucket()) {
                if (netStats.getNextBucket(bucket)) {
                    total += bucket.getRxBytes() + bucket.getTxBytes();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "fail to query data usage: " + e);
        }
        return total;
    }

    private void initCarControl() {
        this.mCarInstance = Car.createCar(this.mContext, this.mCarServiceConnection, this);
        this.mCarInstance.connect();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void registerCarEvent() {
        Collection<Integer> tboxPropId = new ArrayList<>();
        tboxPropId.add(554700817);
        tboxPropId.add(554700819);
        try {
            this.mTboxManager.registerPropCallback(tboxPropId, this.mTboxEventCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unregisterCarEvent() {
        try {
            this.mTboxManager.unregisterCallback(this.mTboxEventCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void requestMobileNetworkStatus() {
        Log.d(TAG, "requestMobileNetworkStatus()");
        CarTboxManager carTboxManager = this.mTboxManager;
        if (carTboxManager == null) {
            Log.w(TAG, "fail to get TboxManager");
            return;
        }
        try {
            carTboxManager.requestTboxModemStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getProbeConfig() {
        Log.d(TAG, "getProbeConfig()");
        String config = App.getInstance().getConfigInterface().getConfiguration(ConfigConstants.KEY_PROBE_CONFIG, "");
        handleProbeConfigChange(config);
    }

    private void fetchMessageFromFile() {
        Log.d(TAG, "fetchMessageFromFile()");
        File file = new File(POST_MESSAGE_CACHE_DIR, CACHE_FILE_NAME);
        if (file.exists()) {
            try {
                FileReader reader = new FileReader(file);
                BufferedReader bufReader = new BufferedReader(reader);
                while (true) {
                    String json = bufReader.readLine();
                    if (json == null) {
                        break;
                    }
                    PostMessage postMessage = (PostMessage) sGson.fromJson(json, (Class<Object>) PostMessage.class);
                    this.mPostMessageCache.add(postMessage);
                }
                file.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.mCacheInDisk = 0;
        tryUploadAllPostMessages();
    }

    private void handleNetworkStateChange() {
        Log.d(TAG, "handleNetworkStateChange()");
        sendEmptyMessage(1);
        tryUploadAllPostMessages();
        Log.d(TAG, "handleNetworkStateChange(): " + this.mTboxManager);
        if (this.mTboxManager == null) {
            requestMobileNetworkStatus();
        }
        if (NetUtils.isTboxNetworkConnected()) {
            this.mTboxConnectionTime = SystemClock.uptimeMillis();
            SystemProperties.set(SYSTEM_PROP_NETWORK_TYPE, TboxApnInfo.getNetworkType(this.mCurrentNetworkType));
            return;
        }
        this.mTboxConnectionTime = SystemClock.uptimeMillis() - this.mTboxConnectionTime;
        String netTypeName = NetUtils.getNetworkTypeName();
        if (!TextUtils.isEmpty(netTypeName)) {
            SystemProperties.set(SYSTEM_PROP_NETWORK_TYPE, netTypeName.toUpperCase());
        } else {
            SystemProperties.set(SYSTEM_PROP_NETWORK_TYPE, "unknown");
        }
    }

    private void tryUploadAllPostMessages() {
        Log.d(TAG, "tryUploadAllPostMessages()");
        if (NetUtils.isNetworkConnected()) {
            long postDelay = DEFAULT_POST_DELAY;
            synchronized (this.mPostMessageCache) {
                for (PostMessage postMessage : this.mPostMessageCache) {
                    Message msg = obtainMessage(6, postMessage);
                    postDelay += 0 * TimeUnit.SECONDS.toMillis(10L);
                    sendMessageDelayed(msg, postDelay);
                }
            }
        }
    }

    private void handleUploadMessageDone(PostMessage postMessage) {
        Log.d(TAG, "handlePostMessageError()");
        if (postMessage.isUploaded()) {
            this.mPostMessageCache.remove(postMessage);
        } else if (this.mPostMessageCache.size() > 10) {
            List<PostMessage> postMessages = new ArrayList<>();
            synchronized (this.mPostMessageCache) {
                for (PostMessage pm : this.mPostMessageCache) {
                    if (this.mCacheInDisk < 10) {
                        postMessages.add(this.mCacheInDisk, pm);
                        this.mCacheInDisk++;
                    }
                }
            }
            ThreadUtils.postNormal(new SaveMessageTask(POST_MESSAGE_CACHE_DIR, CACHE_FILE_NAME, sGson, postMessages, this));
        } else {
            this.mPostMessageCache.add(postMessage);
        }
    }

    private boolean shouldProbe() {
        boolean ret = NetUtils.isNetworkConnected() && !isDataUsageOverLimit();
        Log.d(TAG, "shouldProbe(): " + ret);
        return ret;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private void handleNetworkProbe(ProbeTaskConfig config) {
        boolean z;
        Log.d(TAG, "handleNetworkProbe(): id = " + config.getTaskId());
        if (shouldProbe()) {
            if (config.getProbeState() != 1 && (this.mCacheInDisk <= 10 || this.mPostMessageCache.size() <= 10)) {
                config.setProbeState(1);
                String probeType = config.getProbeType();
                Log.d(TAG, "handleNetworkProbe: type=" + probeType);
                if (!TextUtils.isEmpty(probeType)) {
                    String upperCase = probeType.toUpperCase();
                    switch (upperCase.hashCode()) {
                        case 67849:
                            if (upperCase.equals(ProbeType.PROBE_TYPE_DNS)) {
                                z = true;
                                break;
                            }
                            z = true;
                            break;
                        case 82881:
                            if (upperCase.equals(ProbeType.PROBE_TYPE_TCP)) {
                                z = true;
                                break;
                            }
                            z = true;
                            break;
                        case 2228360:
                            if (upperCase.equals(ProbeType.PROBE_TYPE_HTTP)) {
                                z = false;
                                break;
                            }
                            z = true;
                            break;
                        case 2455922:
                            if (upperCase.equals(ProbeType.PROBE_TYPE_PING)) {
                                z = true;
                                break;
                            }
                            z = true;
                            break;
                        default:
                            z = true;
                            break;
                    }
                    if (!z) {
                        this.mProbeExecutors.execute(new HttpProbeTask(config, this, this.mProbeConfig.getProbeRetryTimes()));
                        return;
                    } else if (z) {
                        this.mProbeExecutors.execute(new PingProbeTask(config, this));
                        Log.d(TAG, "handleNetworkProbe(): ping");
                        return;
                    } else if (z) {
                        this.mProbeExecutors.execute(new TcpProbeTask(config, this, this.mProbeConfig.getProbeRetryTimes()));
                        return;
                    } else if (z) {
                        this.mProbeExecutors.execute(new DnsProbeTask(config, this, this.mProbeConfig.getProbeRetryTimes()));
                        return;
                    } else {
                        Log.w(TAG, "unknown probe type");
                        return;
                    }
                }
                return;
            }
            sendEmptyMessage(9);
            return;
        }
        Log.w(TAG, "handleNetworkProbe(): network is not connected");
        Message msg = obtainMessage(3, config);
        config.setProbeState(3);
        sendMessageDelayed(msg, config.getProbeInterval());
    }

    private void handleNetworkProbeDone(ProbeResult probeResult) {
        Log.d(TAG, "handleNetworkProbeDone(): task id = " + probeResult.getTaskId());
        PostMessage postMessage = new PostMessage();
        postMessage.setProbeResult(probeResult);
        postMessage.setVin(SystemPropertyUtil.getVIN());
        GpsInfo gpsInfo = new GpsInfo();
        gpsInfo.setSpeed(getCarSpeed());
        Location loc = getLocation();
        if (loc != null) {
            try {
                DecimalFormat df = new DecimalFormat("0.000000");
                gpsInfo.setLongitude(Float.parseFloat(df.format(loc.getLongitude())));
                gpsInfo.setLatitude(Float.parseFloat(df.format(loc.getLatitude())));
            } catch (Exception e) {
                Log.d(TAG, "fail to parse number: " + e);
            }
        }
        this.mCountryCode = getCountryCode();
        if (!sCountryCodeList.contains(this.mCountryCode)) {
            postMessage.setGpsInfo(gpsInfo);
        }
        postMessage.setNetInfo(getNetworkInfo());
        postProbeResultToOss(postMessage);
        ProbeTaskConfig config = this.mTaskConfig.get(probeResult.getTaskId());
        if (config != null && config.isValidTask() && config.allowToRun()) {
            config.setProbeState(3);
            Message msg = obtainMessage(3);
            msg.obj = config;
            sendMessageDelayed(msg, config.getProbeInterval());
        } else if (config != null && !config.isValidTask()) {
            this.mTaskConfig.remove(probeResult.getTaskId());
        }
    }

    private void handleMessageSaveResult(List<PostMessage> savedMessages) {
        Log.d(TAG, "handleMessageSaveResult()");
        if (savedMessages != null) {
            for (PostMessage pm : savedMessages) {
                this.mPostMessageCache.remove(pm);
            }
        }
    }

    private int getCarSpeed() {
        try {
            int carSpeed = (int) this.mVcuManager.getRawCarSpeed();
            return carSpeed;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private NetInfo getNetworkInfo() {
        NetInfo netInfo = new NetInfo();
        netInfo.setNetType(NetUtils.getNetworkTypeName());
        if (this.mGsmCellInfos.size() > 0) {
            GsmCellInfo ci = this.mGsmCellInfos.get(0);
            netInfo.setCellID(String.valueOf(ci.getCid()));
            netInfo.setLac(String.valueOf(ci.getLac()));
            netInfo.setMcc(String.valueOf(ci.getMcc()));
            netInfo.setMnc(String.valueOf(ci.getMnc()));
        }
        TboxApnInfo tboxApnInfo = this.mApnInfo;
        if (tboxApnInfo != null) {
            if (tboxApnInfo.getNetworkType() == 2) {
                netInfo.setLteSignal(this.mApnInfo.getRssi());
            } else {
                netInfo.setGsmSignal(this.mApnInfo.getRssi());
            }
        }
        netInfo.setAliveTime(this.mTboxConnectionTime);
        return netInfo;
    }

    private void postProbeResultToOss(PostMessage postMessage) {
        Log.d(TAG, "postProbeResultToOss()");
        if (postMessage != null) {
            Message msg = obtainMessage(6, postMessage);
            msg.sendToTarget();
        }
    }

    private void handlePostProbeResult(PostMessage postMessage) {
        Log.d(TAG, "handlePostProbeResult()");
        if (this.mRemoteStorage != null) {
            this.mPostMessageCache.add(postMessage);
            UploadMessageTask uploadTask = new UploadMessageTask(this.mRemoteStorage, sGson, this, POST_MESSAGE_CACHE_DIR);
            uploadTask.setUploadBucket(OssConstants.sBucketMaps.get(this.mCountryCode));
            uploadTask.setPostMessage(postMessage);
            ThreadUtils.postNormal(uploadTask);
        }
    }

    private void handleProbeConfigChange(String config) {
        Log.d(TAG, "handleProbeConfigChange(): config = " + config);
        if (this.mProbeExecutors == null) {
            this.mProbeExecutors = Executors.newSingleThreadExecutor();
            makeProbeDir();
        }
        if (!TextUtils.isEmpty(config)) {
            parseProbeConfig(config);
        } else {
            tryGetConfigFromFile(POST_MESSAGE_CACHE_DIR + File.separator + LOCAL_JASON_CONFIG_FILE);
        }
        checkProbeTask();
    }

    private void makeProbeDir() {
        File file = new File(POST_MESSAGE_CACHE_DIR);
        if (!file.exists()) {
            file.mkdirs();
            file.setReadable(true, true);
            file.setWritable(true, true);
        }
    }

    private void parseProbeConfig(String config) {
        Log.d(TAG, "parseProbeConfig()");
        if (!TextUtils.isEmpty(config)) {
            try {
                this.mProbeConfig = ConfigParser.parseProbeConfig(config);
                for (ProbeTaskConfig taskConfig : this.mProbeConfig.getTaskConfig()) {
                    ProbeTaskConfig cf = this.mTaskConfig.get(taskConfig.getTaskId());
                    Log.d(TAG, "parseProbeConfig(): taskConfig = " + taskConfig);
                    if (cf == null || !taskConfig.equals(cf)) {
                        this.mTaskConfig.put(taskConfig.getTaskId(), taskConfig);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "fail to parse config");
                e.printStackTrace();
            }
        }
    }

    private void tryGetConfigFromFile(String filePath) {
        Log.d(TAG, "tryGetConfigFromFile(): path = " + filePath);
        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);
            if (file.exists()) {
                FileReader fileReader = null;
                BufferedReader reader = null;
                try {
                    try {
                        fileReader = new FileReader(file);
                        reader = new BufferedReader(fileReader);
                        String config = reader.readLine();
                        if (config != null) {
                            config.replaceAll("\n", "");
                            Log.d(TAG, "tryGetConfigFromFile(): config = " + config);
                            parseProbeConfig(config);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } finally {
                    FileUtil.closeQuietly(reader);
                    FileUtil.closeQuietly(fileReader);
                }
            }
        }
    }

    private void checkProbeTask() {
        Log.d(TAG, "checkProbeTask()");
        for (int i = 0; i < this.mTaskConfig.size(); i++) {
            ProbeTaskConfig config = this.mTaskConfig.valueAt(i);
            if (config.isValidTask() && config.allowToRun()) {
                config.setProbeState(3);
                Message msg = obtainMessage(3, config);
                sendMessageDelayed(msg, config.getStartTime() - System.currentTimeMillis());
            }
        }
    }

    @Nullable
    private Location getLocation() {
        Location location = null;
        boolean isGpsLocEnabled = this.mLocationMgr.isProviderEnabled("gps");
        if (!isGpsLocEnabled || (location = this.mLocationMgr.getLastKnownLocation("gps")) == null) {
            boolean isNetworkLocEnabled = this.mLocationMgr.isProviderEnabled("network");
            if (isNetworkLocEnabled) {
                Location location2 = this.mLocationMgr.getLastKnownLocation("network");
                return location2;
            }
            return location;
        }
        return location;
    }

    private static String toEventString(int event) {
        switch (event) {
            case 1:
                return "EVENT_GET_PROBE_CONFIG";
            case 2:
                return "EVENT_PROBE_CONFIG_CHANGE";
            case 3:
                return "EVENT_START_NETWORK_PROBE";
            case 4:
                return "EVENT_NETWORK_PROBE_DONE";
            case 5:
                return "EVENT_RETRY_NETWORK_PROBE";
            case 6:
                return "EVENT_POST_PROBE_RESULT";
            case 7:
                return "EVENT_UPLOAD_MESSAGE_DONE";
            case 8:
                return "EVENT_NETWORK_STATE_CHANGE";
            case 9:
                return "EVENT_FETCH_MESSAGE_CACHE";
            default:
                return "UNKNOWN";
        }
    }
}
