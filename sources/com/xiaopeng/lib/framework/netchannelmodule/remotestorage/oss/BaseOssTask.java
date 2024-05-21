package com.xiaopeng.lib.framework.netchannelmodule.remotestorage.oss;

import android.app.Application;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.xiaopeng.lib.framework.moduleinterface.netchannelmodule.remotestorage.Callback;
import com.xiaopeng.lib.framework.moduleinterface.netchannelmodule.remotestorage.StorageException;
import com.xiaopeng.lib.framework.netchannelmodule.remotestorage.exception.StorageExceptionImpl;
import com.xiaopeng.lib.framework.netchannelmodule.remotestorage.statistic.StorageCounter;
import com.xiaopeng.lib.framework.netchannelmodule.remotestorage.token.TokenRetriever;
import com.xiaopeng.lib.utils.LogUtils;
import java.util.Map;
/* loaded from: classes.dex */
public abstract class BaseOssTask implements Runnable {
    private static final int CONNECTION_TIMEOUT = 60000;
    private static final int ERROR_FORBIDDEN = 403;
    private static final int MAX_CONCURRENT_REQUEST = 2;
    private static final int MAX_ERROR_RETRY = 3;
    private static final int SOCKET_TIMEOUT = 60000;
    private static final String TAG = "NetChannel-BaseOssTask";
    private static OSS sOssClient;
    private Application mApplication;
    private Bucket mBucket;
    private Callback mCallback;
    protected Map<String, String> mCallbackParams;
    protected String mLocalFilePath;
    protected long mLocalFileSize;
    private String mModuleName;
    private String mRemoteFolder;
    protected String mRemoteObjectKey;
    protected String mRemoteUrl;

    abstract void performRealTask();

    public BaseOssTask(@NonNull Bucket bucket) {
        this.mBucket = bucket;
    }

    public BaseOssTask application(@NonNull Application application) {
        this.mApplication = application;
        return this;
    }

    public BaseOssTask module(@NonNull String name) throws IllegalArgumentException {
        if (!TextUtils.isEmpty(this.mRemoteFolder)) {
            throw new IllegalArgumentException("Remote folder has been assigned.");
        }
        this.mModuleName = name;
        return this;
    }

    public BaseOssTask remoteFolder(@NonNull String name) throws IllegalArgumentException {
        if (!TextUtils.isEmpty(this.mModuleName)) {
            throw new IllegalArgumentException("Module name has been assigned.");
        }
        this.mRemoteFolder = name;
        return this;
    }

    public BaseOssTask filePath(@NonNull String filePath) {
        this.mLocalFilePath = filePath;
        return this;
    }

    public BaseOssTask callback(@NonNull Callback callback) {
        this.mCallback = callback;
        return this;
    }

    public BaseOssTask remoteCallbackParams(Map<String, String> remoteCallbackParams) {
        this.mCallbackParams = remoteCallbackParams;
        return this;
    }

    public BaseOssTask build() throws StorageException {
        if (TextUtils.isEmpty(this.mRemoteFolder) && TextUtils.isEmpty(this.mModuleName)) {
            throw new StorageExceptionImpl(3);
        }
        String str = this.mRemoteFolder;
        if (str == null) {
            this.mRemoteObjectKey = this.mBucket.generateObjectKey(this.mModuleName);
        } else {
            this.mRemoteObjectKey = str;
        }
        this.mRemoteUrl = this.mBucket.getUrl() + this.mRemoteObjectKey;
        return this;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void doSuccess() {
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onSuccess(this.mRemoteUrl, this.mLocalFilePath);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void doFailure(@NonNull StorageException exception) {
        LogUtils.d(TAG, "Failed! Reason is-->" + exception.getMessage());
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onFailure(this.mRemoteUrl, this.mLocalFilePath, exception);
        }
        if (exception.getReasonCode() == ERROR_FORBIDDEN) {
            TokenRetriever.getInstance().clearToken();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String bucketRootName() {
        return this.mBucket.getRootName();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public long bucketMaxObjectSize() {
        return this.mBucket.getMaxObjectSize();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public synchronized OSS createOssClient(@NonNull String accessKeyId, @NonNull String accessKeySecret, @NonNull String securityToken) {
        OSSCredentialProvider ossCredentialProvider = new OSSStsTokenCredentialProvider(accessKeyId, accessKeySecret, securityToken);
        if (sOssClient == null) {
            ClientConfiguration configuration = new ClientConfiguration();
            configuration.setConnectionTimeout(60000);
            configuration.setSocketTimeout(60000);
            configuration.setMaxConcurrentRequest(2);
            configuration.setMaxErrorRetry(3);
            sOssClient = new OSSClient(this.mApplication, Bucket.END_POINT, ossCredentialProvider, configuration);
        } else {
            sOssClient.updateCredentialProvider(ossCredentialProvider);
        }
        return sOssClient;
    }

    @Override // java.lang.Runnable
    public void run() {
        this.mCallback.onStart(this.mRemoteUrl, this.mLocalFilePath);
        performRealTask();
        StorageCounter.getInstance().increaseRequestCount();
    }
}
