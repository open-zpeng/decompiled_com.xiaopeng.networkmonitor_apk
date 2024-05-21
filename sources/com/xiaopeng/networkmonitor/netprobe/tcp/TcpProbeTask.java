package com.xiaopeng.networkmonitor.netprobe.tcp;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import com.xiaopeng.libconfig.ipc.IpcConfig;
import com.xiaopeng.networkmonitor.netprobe.ProbeTask;
import com.xiaopeng.networkmonitor.netprobe.ProbeTaskConfig;
import com.xiaopeng.networkmonitor.netprobe.model.ProbeResult;
import com.xiaopeng.networkmonitor.utils.FileUtil;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class TcpProbeTask extends ProbeTask {
    private static final int MAX_RETRY_TIMES = 3;
    private static final String TAG = "TcpProbeTask";
    private Handler mHandler;
    private int mMaxRetry;
    private ProbeResult mProbeResult;
    private int mRetryCount;
    private Socket mSocket;
    private TcpTaskConfig mTaskConfig;
    private static final int DEFAULT_TCP_SOL_TIMEOUT = (int) TimeUnit.SECONDS.toMillis(10);
    private static final int[] TCP_RETRY_DELAY = {IpcConfig.MessageCenterConfig.IPC_ID_SUBSCRIBE, 20000, 30000, 60000};

    public TcpProbeTask(ProbeTaskConfig config, Handler handler, int maxRetry) {
        super(handler.getLooper());
        this.mTaskConfig = (TcpTaskConfig) config;
        this.mProbeResult = new ProbeResult();
        this.mProbeResult.setUrl(config.getProbeUrl());
        this.mProbeResult.setTaskId(this.mTaskConfig.getTaskId());
        this.mProbeResult.setTaskName(this.mTaskConfig.getTaskName());
        this.mProbeResult.setTaskRunTime(System.currentTimeMillis());
        this.mHandler = handler;
        this.mMaxRetry = maxRetry <= 3 ? maxRetry : 3;
    }

    @Override // com.xiaopeng.networkmonitor.netprobe.ProbeTask
    public void probe() {
        super.probe();
        Log.d(TAG, "probe()");
        try {
            this.mSocket = new Socket();
            this.mSocket.setSoTimeout(DEFAULT_TCP_SOL_TIMEOUT);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        if (this.mRetryCount <= this.mMaxRetry) {
            long startTime = SystemClock.uptimeMillis();
            if (startConnect()) {
                this.mProbeResult.setIsSuccess(true);
                this.mProbeResult.setRtt((int) (SystemClock.uptimeMillis() - startTime));
                sendProbeResult();
                return;
            }
            this.mProbeResult.setIsSuccess(false);
            sendEmptyMessageDelayed(1, TCP_RETRY_DELAY[this.mRetryCount % 3]);
            return;
        }
        this.mProbeResult.setIsSuccess(false);
        this.mProbeResult.setRetryTimes(this.mRetryCount);
        sendProbeResult();
    }

    @Override // com.xiaopeng.networkmonitor.netprobe.ProbeTask, java.lang.Runnable
    public void run() {
        this.mProbeResult.setTaskRunTime(System.currentTimeMillis());
        probe();
    }

    @Override // android.os.Handler
    public void handleMessage(Message msg) {
        Log.d(TAG, "handleMessage():what=" + msg.what);
        if (msg.what == 1) {
            this.mRetryCount++;
            probe();
        }
    }

    private void sendProbeResult() {
        Log.d(TAG, "sendProbeResult(): success=" + this.mProbeResult.isSuccess());
        this.mTaskConfig.setProbeState(2);
        this.mTaskConfig.setStartTime(System.currentTimeMillis() + this.mTaskConfig.getProbeInterval());
        this.mProbeResult.setRetryTimes(this.mTaskConfig.getRetryCount());
        Message message = this.mHandler.obtainMessage(4);
        message.obj = this.mProbeResult;
        message.sendToTarget();
        closeSocket();
    }

    private boolean startConnect() {
        Log.d(TAG, "startConnect()");
        try {
            InetAddress ia = InetAddress.getByName(this.mTaskConfig.getProbeUrl());
            SocketAddress sa = new InetSocketAddress(ia, this.mTaskConfig.getTcpPort());
            this.mSocket.connect(sa, DEFAULT_TCP_SOL_TIMEOUT);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            closeSocket();
            return false;
        }
    }

    private void closeSocket() {
        FileUtil.closeQuietly(this.mSocket);
    }
}
