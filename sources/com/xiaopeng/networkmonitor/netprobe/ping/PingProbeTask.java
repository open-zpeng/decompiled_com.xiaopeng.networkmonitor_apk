package com.xiaopeng.networkmonitor.netprobe.ping;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import com.xiaopeng.networkmonitor.netprobe.ProbeTask;
import com.xiaopeng.networkmonitor.netprobe.ProbeTaskConfig;
import com.xiaopeng.networkmonitor.netprobe.model.ProbeResult;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class PingProbeTask extends ProbeTask {
    private static final int DEFAULT_TIMEOUT = (int) TimeUnit.SECONDS.toMillis(30);
    private static final long PING_INTERVAL = TimeUnit.SECONDS.toMillis(1);
    private static final String TAG = "PingProbeTask";
    private int mCurrentCount;
    private Handler mHandler;
    private int mPcktLossCount;
    private PingResult mPingResult;
    private ProbeResult mProbeResult;
    private PingTaskConfig mTaskConfig;
    private float mTotalRtt;

    public PingProbeTask(ProbeTaskConfig config, Handler handler) {
        this.mTaskConfig = (PingTaskConfig) config;
        Log.d(TAG, "PingProbeTask(): ping count = " + this.mTaskConfig.getPingCount());
        this.mHandler = handler;
        this.mTotalRtt = 0.0f;
        this.mPcktLossCount = 0;
        this.mCurrentCount = 0;
        this.mPingResult = new PingResult();
        this.mProbeResult = new ProbeResult();
        this.mProbeResult.setUrl(config.getProbeUrl());
        this.mProbeResult.setTaskId(this.mTaskConfig.getTaskId());
        this.mProbeResult.setTaskName(this.mTaskConfig.getTaskName());
        this.mProbeResult.setTaskRunTime(System.currentTimeMillis());
    }

    @Override // com.xiaopeng.networkmonitor.netprobe.ProbeTask
    public void probe() {
        super.probe();
        Log.d(TAG, "probe()");
        for (int i = 0; i < this.mTaskConfig.getPingCount(); i++) {
            try {
                long before = SystemClock.elapsedRealtimeNanos();
                InetAddress dest = Inet4Address.getByName(this.mTaskConfig.getProbeUrl());
                boolean isReachable = dest.isReachable(DEFAULT_TIMEOUT);
                long after = SystemClock.elapsedRealtimeNanos();
                if (isReachable) {
                    float rtt = (float) (((after - before) * 1.0d) / 1000000.0d);
                    Log.d(TAG, "probe(): ping count = " + this.mCurrentCount + ", rtt=" + rtt);
                    this.mPingResult.maxRtt = Math.max(rtt, this.mPingResult.maxRtt);
                    this.mPingResult.minRtt = Math.min(rtt, this.mPingResult.minRtt);
                    this.mTotalRtt = this.mTotalRtt + rtt;
                } else {
                    this.mPcktLossCount++;
                }
            } catch (Exception e) {
                Log.e(TAG, this.mTaskConfig.getProbeUrl() + " is unreachable");
            }
        }
        this.mTaskConfig.setProbeState(2);
        int count = this.mTaskConfig.getPingCount();
        int successCnt = count - this.mPcktLossCount;
        if (successCnt > 0) {
            this.mPingResult.averRtt = this.mTotalRtt / successCnt;
            this.mProbeResult.setIsSuccess(true);
            this.mProbeResult.addReportInfo(this.mPingResult.toString());
            this.mProbeResult.addReportInfo(String.valueOf(this.mPcktLossCount / count));
        } else {
            this.mProbeResult.setIsSuccess(false);
            PingResult pingResult = this.mPingResult;
            pingResult.minRtt = 0.0f;
            pingResult.maxRtt = 0.0f;
            pingResult.averRtt = 0.0f;
        }
        sendProbeResult();
    }

    @Override // com.xiaopeng.networkmonitor.netprobe.ProbeTask, java.lang.Runnable
    public void run() {
        Log.d(TAG, "run()");
        this.mProbeResult.setTaskRunTime(System.currentTimeMillis());
        this.mTaskConfig.setProbeState(1);
        probe();
    }

    private void sendProbeResult() {
        Log.d(TAG, "sendProbeResult(): success=" + this.mProbeResult.isSuccess());
        this.mTaskConfig.setProbeState(2);
        this.mTaskConfig.setStartTime(System.currentTimeMillis() + this.mTaskConfig.getProbeInterval());
        this.mProbeResult.setRetryTimes(this.mTaskConfig.getRetryCount());
        Message message = this.mHandler.obtainMessage(4);
        message.obj = this.mProbeResult;
        message.sendToTarget();
    }
}
