package com.xiaopeng.networkmonitor.netprobe.dns;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import com.xiaopeng.lib.security.xmartv1.XmartV1Constants;
import com.xiaopeng.networkmonitor.netprobe.ProbeTask;
import com.xiaopeng.networkmonitor.netprobe.ProbeTaskConfig;
import com.xiaopeng.networkmonitor.netprobe.model.ProbeResult;
import com.xiaopeng.networkmonitor.utils.FileUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class DnsProbeTask extends ProbeTask {
    private static final int DNS_LOCAL_PORT = 9898;
    private static final int DNS_REPONSE_HEADER_LEN = 6;
    private static final int DNS_SERVER_PORT = 53;
    private static final int MAX_RETRY_COUNT = 3;
    private static final int RR_TYPE_A = 1;
    private static final int RR_TYPE_AAAA = 28;
    private static final int RR_TYPE_CNAME = 5;
    private static final String TAG = "DnsProbeTask";
    private DatagramSocket mDnsSocket;
    private Handler mHandler;
    private boolean mIsLocalNS;
    private int mMaxRetry;
    private ProbeResult mProbeResult;
    private List<String> mResolvedIps;
    private int mRetryCount;
    private DnsTaskConfig mTaskConfig;
    private static final long[] DNS_RETRY_DELAY = {5000, 10000, 20000, 30000};
    private static final int DEFAULT_SOCKET_TIMEOUT = (int) TimeUnit.SECONDS.toMillis(15);

    public DnsProbeTask(ProbeTaskConfig config, Handler handler, int maxRetry) {
        super(handler.getLooper());
        this.mTaskConfig = (DnsTaskConfig) config;
        this.mHandler = handler;
        this.mMaxRetry = maxRetry <= 3 ? maxRetry : 3;
        this.mProbeResult = new ProbeResult();
        this.mProbeResult.setUrl(config.getProbeUrl());
        this.mProbeResult.setTaskId(this.mTaskConfig.getTaskId());
        this.mProbeResult.setTaskName(this.mTaskConfig.getTaskName());
        this.mProbeResult.setTaskRunTime(System.currentTimeMillis());
        this.mResolvedIps = new ArrayList();
        this.mIsLocalNS = DnsTaskConfig.LOCAL_NAME_SERVER.equals(this.mTaskConfig.getNameServer());
    }

    @Override // com.xiaopeng.networkmonitor.netprobe.ProbeTask
    public void probe() {
        boolean z;
        super.probe();
        Log.d(TAG, "probe(): retry count = " + this.mRetryCount);
        try {
            this.mDnsSocket = new DatagramSocket((SocketAddress) null);
            this.mDnsSocket.setReuseAddress(true);
            this.mDnsSocket.setSoTimeout(DEFAULT_SOCKET_TIMEOUT);
            this.mDnsSocket.bind(new InetSocketAddress(DNS_LOCAL_PORT));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (this.mRetryCount <= this.mMaxRetry) {
            if (this.mIsLocalNS) {
                z = doLocalDns(this.mTaskConfig.getProbeUrl());
            } else {
                z = requestDns(this.mTaskConfig.getProbeUrl()) && getDnsResult();
            }
            boolean isDnsQueryOk = z;
            if (isDnsQueryOk) {
                this.mProbeResult.setIsSuccess(true);
                sendProbeResult();
                return;
            }
            sendEmptyMessageDelayed(1, DNS_RETRY_DELAY[this.mRetryCount % 3]);
            return;
        }
        this.mProbeResult.setIsSuccess(false);
        sendProbeResult();
    }

    @Override // com.xiaopeng.networkmonitor.netprobe.ProbeTask, java.lang.Runnable
    public void run() {
        this.mProbeResult.setTaskRunTime(SystemClock.uptimeMillis());
        probe();
    }

    @Override // android.os.Handler
    public void handleMessage(Message msg) {
        Log.d(TAG, "handleMessage()");
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
        for (String ip : this.mResolvedIps) {
            this.mProbeResult.addReportInfo(ip);
        }
        Message message = this.mHandler.obtainMessage(4);
        message.obj = this.mProbeResult;
        message.sendToTarget();
        closeSocket();
    }

    private void closeSocket() {
        FileUtil.closeQuietly(this.mDnsSocket);
    }

    private boolean doLocalDns(String target) {
        Log.d(TAG, "doLocalDns()");
        try {
            InetAddress[] addrs = InetAddress.getAllByName(target);
            for (InetAddress ia : addrs) {
                if (ia != null) {
                    this.mResolvedIps.add(ia.getHostAddress());
                }
            }
            return true;
        } catch (Exception e) {
            Log.e(TAG, "fail to resolve");
            return false;
        }
    }

    private boolean requestDns(String target) {
        DatagramPacket dp;
        Log.d(TAG, "requestDns()");
        if (!TextUtils.isEmpty(target) && (dp = composeRequestDatagramPacket(target)) != null) {
            try {
                this.mDnsSocket.send(dp);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                closeSocket();
            }
        }
        return false;
    }

    private boolean getDnsResult() {
        Log.d(TAG, "getDnsResult()");
        byte[] buf = new byte[2048];
        DatagramPacket dp = new DatagramPacket(buf, buf.length);
        try {
            this.mDnsSocket.receive(dp);
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(buf));
            Log.d(TAG, "QueryID:0x" + String.format("%x", Short.valueOf(dis.readShort())));
            int numAnswers = 0;
            int numQuestions = 0;
            for (int i = 1; i < 6; i++) {
                if (i == 2) {
                    numQuestions = dis.readShort();
                } else if (i == 3) {
                    numAnswers = dis.readShort();
                } else {
                    dis.readShort();
                }
            }
            Log.d(TAG, "DNS answers count = " + numAnswers);
            for (int i2 = 0; i2 < numQuestions; i2++) {
                readQName(dis);
                dis.readShort();
                dis.readShort();
            }
            for (int i3 = 0; i3 < numAnswers; i3++) {
                readQName(dis);
                int type = dis.readShort();
                Log.d(TAG, "DNS query type = " + type);
                dis.readShort();
                int ttl = dis.readInt();
                Log.d(TAG, "DNS ttl = " + ttl);
                readResponseData(type, dis, buf);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isNamePointer(byte b) {
        return (b & 128) > 0 && (b & 64) > 0;
    }

    private String readResponseData(int type, DataInputStream dis, byte[] buf) throws Exception {
        StringBuilder sb = new StringBuilder();
        if (isHostAddress(type)) {
            int recLen = dis.readShort();
            for (int j = 0; j < recLen; j++) {
                sb.append(String.format("%d", Integer.valueOf(dis.readByte() & 255)));
                if (j != recLen - 1) {
                    sb.append(".");
                }
            }
            Log.d(TAG, "resolved ip address = " + sb.toString());
            return sb.toString();
        }
        int totalLen = dis.readShort();
        byte firstByte = dis.readByte();
        int recLen2 = firstByte;
        int count = 1;
        while (recLen2 > 0 && count < totalLen) {
            recLen2 = firstByte;
            if (isNamePointer(firstByte)) {
                byte[] offset = {(byte) (firstByte & 63), dis.readByte()};
                int off = getShort(offset, 0);
                sb.append(getNameFromBuf(buf, off));
                count += 2;
                if (count < totalLen) {
                    sb.append(".");
                    firstByte = dis.readByte();
                    count++;
                }
            } else {
                byte[] record = new byte[recLen2];
                for (int j2 = 0; j2 < recLen2; j2++) {
                    record[j2] = dis.readByte();
                }
                sb.append(new String(record));
                count += recLen2;
                recLen2 = firstByte;
                if (count < totalLen) {
                    sb.append(".");
                    firstByte = dis.readByte();
                    count++;
                }
            }
        }
        Log.d(TAG, "CNAME is " + sb.toString());
        return sb.toString();
    }

    private String getNameFromBuf(byte[] buf, int offset) {
        int len = buf[offset];
        byte[] record = new byte[len];
        for (int i = offset + 1; i < len + offset + 1; i++) {
            record[(i - offset) - 1] = buf[i];
        }
        return new String(record);
    }

    private int readQName(DataInputStream dis) throws Exception {
        byte firstByte = dis.readByte();
        if (isNamePointer(firstByte)) {
            byte[] offset = {(byte) (firstByte & 63), dis.readByte()};
            Log.d(TAG, "name pointer");
            return getShort(offset, 0);
        }
        StringBuilder sb = new StringBuilder();
        int recLen = firstByte;
        while (recLen > 0) {
            byte[] record = new byte[recLen];
            for (int j = 0; j < recLen; j++) {
                record[j] = dis.readByte();
            }
            sb.append(new String(record));
            sb.append(".");
            recLen = dis.readByte();
        }
        return 0;
    }

    private short getShort(byte[] b, int off) {
        return (short) ((b[off + 1] & 255) + (b[off] << 8));
    }

    private boolean isHostAddress(int type) {
        return type == 1 || type == 28;
    }

    private DatagramPacket composeRequestDatagramPacket(String target) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            dos.writeShort(4660);
            dos.writeShort(256);
            dos.writeShort(1);
            dos.writeShort(0);
            dos.writeShort(0);
            dos.writeShort(0);
            String[] urlParts = target.split("\\.");
            for (String str : urlParts) {
                byte[] byteParts = str.getBytes(XmartV1Constants.UTF8_ENCODING);
                dos.writeByte(byteParts.length);
                dos.write(byteParts);
            }
            dos.writeByte(0);
            dos.writeShort(1);
            dos.writeShort(1);
            InetAddress ia = InetAddress.getByName(this.mTaskConfig.getDnsServer());
            byte[] dnsRequest = baos.toByteArray();
            DatagramPacket dp = new DatagramPacket(dnsRequest, dnsRequest.length, ia, (int) DNS_SERVER_PORT);
            return dp;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
