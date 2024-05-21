package com.xiaopeng.networkmonitor.netprobe.http;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import com.xiaopeng.lib.utils.LogUtils;
import com.xiaopeng.networkmonitor.netprobe.ConfigConstants;
import com.xiaopeng.networkmonitor.netprobe.ProbeTask;
import com.xiaopeng.networkmonitor.netprobe.ProbeTaskConfig;
import com.xiaopeng.networkmonitor.netprobe.model.ProbeResult;
import com.xiaopeng.networkmonitor.utils.FileUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
/* loaded from: classes.dex */
public class HttpProbeTask extends ProbeTask {
    private static final String DEFAULT_POST_STRING = "{\"probe\": \"a new world\"}";
    private static final long HTTP_CONNECT_TIMEOUT = TimeUnit.SECONDS.toMillis(15);
    private static final long HTTP_READ_TIMEOUT = TimeUnit.SECONDS.toMillis(20);
    private static final long HTTP_REQUEST_TIMEOUT = TimeUnit.SECONDS.toMillis(45);
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String TAG = "HttpProbeTask";
    private int mConnectTimeout;
    private Handler mHandler;
    private OkHttpClient mHttpClient;
    private int mMaxRetries;
    private ProbeResult mProbeResult;
    private long mStartTime;
    private HttpTaskConfig mTaskConfig;

    public HttpProbeTask(ProbeTaskConfig taskItem, Handler handler, int maxRetryTimes) {
        super(handler.getLooper());
        this.mHttpClient = buildHttpClient();
        this.mTaskConfig = (HttpTaskConfig) taskItem;
        this.mHandler = handler;
        this.mConnectTimeout = this.mTaskConfig.getConnectTimeout() <= 0 ? (int) HTTP_CONNECT_TIMEOUT : this.mTaskConfig.getConnectTimeout();
        this.mProbeResult = new ProbeResult();
        this.mProbeResult.setUrl(this.mTaskConfig.getProbeUrl());
        this.mProbeResult.setTaskId(this.mTaskConfig.getTaskId());
        this.mProbeResult.setTaskName(this.mTaskConfig.getTaskName());
        this.mProbeResult.setTaskRunTime(SystemClock.uptimeMillis());
        this.mStartTime = SystemClock.uptimeMillis();
        this.mMaxRetries = maxRetryTimes;
    }

    @Override // com.xiaopeng.networkmonitor.netprobe.ProbeTask
    public void probe() {
        super.probe();
        LogUtils.d(TAG, "probe()");
        try {
            Request request = buildHttpRequest();
            Response response = this.mHttpClient.newCall(request).execute();
            this.mProbeResult.setStatusCode(response.code());
            if (response.isSuccessful()) {
                int totalLen = 0;
                char[] buffer = new char[2048];
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    InputStreamReader isr = null;
                    BufferedReader br = null;
                    try {
                        isr = new InputStreamReader(responseBody.byteStream());
                        br = new BufferedReader(isr);
                        for (int len = br.read(buffer); len > 0; len = br.read(buffer)) {
                            if (totalLen > this.mTaskConfig.getSizeLimit()) {
                                break;
                            }
                            totalLen += len;
                        }
                        FileUtil.closeQuietly(isr);
                        FileUtil.closeQuietly(br);
                    } catch (Exception e) {
                        FileUtil.closeQuietly(isr);
                        FileUtil.closeQuietly(br);
                    } catch (Throwable th) {
                        FileUtil.closeQuietly(isr);
                        FileUtil.closeQuietly(br);
                        FileUtil.closeQuietly(responseBody);
                        throw th;
                    }
                    FileUtil.closeQuietly(responseBody);
                }
                this.mProbeResult.setIsSuccess(true);
                this.mProbeResult.setRtt((int) (SystemClock.uptimeMillis() - this.mStartTime));
                sendProbeResult();
                return;
            }
            this.mProbeResult.setIsSuccess(false);
            this.mProbeResult.setRtt((int) (SystemClock.uptimeMillis() - this.mStartTime));
            if (shouldRetry()) {
                retryNetworkProbe();
            } else {
                sendProbeResult();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private Request buildHttpRequest() {
        char c;
        RequestBody rb;
        Request.Builder builder = new Request.Builder();
        String method = this.mTaskConfig.getHttpMethod();
        int hashCode = method.hashCode();
        if (hashCode == 70454) {
            if (method.equals(HttpMethod.HTTP_METHOD_GET)) {
                c = 0;
            }
            c = 65535;
        } else if (hashCode != 2213344) {
            if (hashCode == 2461856 && method.equals(HttpMethod.HTTP_METHOD_POST)) {
                c = 2;
            }
            c = 65535;
        } else {
            if (method.equals(HttpMethod.HTTP_METHOD_HEAD)) {
                c = 1;
            }
            c = 65535;
        }
        if (c == 0) {
            builder.get();
        } else if (c == 1) {
            builder.head();
        } else if (c == 2) {
            if (!TextUtils.isEmpty(this.mTaskConfig.getPostJsonString())) {
                rb = RequestBody.create(JSON, this.mTaskConfig.getPostJsonString());
            } else {
                rb = RequestBody.create(JSON, DEFAULT_POST_STRING);
            }
            builder.post(rb);
        } else {
            LogUtils.d(TAG, "unknown http method: " + method);
            builder.get();
        }
        HttpUrl url = new HttpUrl.Builder().scheme(this.mTaskConfig.getProbeType()).host(this.mTaskConfig.getProbeUrl()).build();
        return builder.url(url).build();
    }

    private OkHttpClient buildHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(this.mConnectTimeout, TimeUnit.SECONDS).readTimeout(HTTP_READ_TIMEOUT, TimeUnit.SECONDS).retryOnConnectionFailure(false).addInterceptor(new RetryInterceptor(0));
        return builder.build();
    }

    @Override // com.xiaopeng.networkmonitor.netprobe.ProbeTask, java.lang.Runnable
    public void run() {
        probe();
    }

    @Override // android.os.Handler
    public void handleMessage(Message msg) {
        if (msg.what == 1) {
            probe();
        }
    }

    private void sendProbeResult() {
        LogUtils.d(TAG, "sendProbeResult()");
        this.mTaskConfig.setProbeState(2);
        this.mTaskConfig.setStartTime(SystemClock.uptimeMillis() + this.mTaskConfig.getProbeInterval());
        this.mProbeResult.setRetryTimes(this.mTaskConfig.getRetryCount());
        Message message = this.mHandler.obtainMessage(4);
        message.obj = this.mProbeResult;
        message.sendToTarget();
    }

    private void retryNetworkProbe() {
        LogUtils.d(TAG, "retryNetworkProbe()");
        this.mTaskConfig.incRetryCount(1);
        this.mTaskConfig.setProbeState(2);
        sendEmptyMessageDelayed(1, ConfigConstants.NETWORK_PROBE_RETRY_DELAY);
    }

    private boolean shouldRetry() {
        return this.mTaskConfig.getRetryCount() <= this.mMaxRetries;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class RetryInterceptor implements Interceptor {
        private int maxRetryTimes;
        private int retryCount = 0;

        RetryInterceptor(int maxTimes) {
            this.maxRetryTimes = maxTimes;
        }

        @Override // okhttp3.Interceptor
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Log.d(HttpProbeTask.TAG, "intercept()");
            Request request = chain.request();
            Response response = chain.proceed(request);
            while (!response.isSuccessful() && this.retryCount < this.maxRetryTimes) {
                LogUtils.d(HttpProbeTask.TAG, "intercept(): request failed");
                this.retryCount++;
                response = chain.proceed(request);
            }
            return response;
        }
    }
}
