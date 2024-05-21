package com.xiaopeng.networkmonitor.netprobe;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.google.gson.Gson;
import com.xiaopeng.lib.framework.moduleinterface.netchannelmodule.remotestorage.Callback;
import com.xiaopeng.lib.framework.moduleinterface.netchannelmodule.remotestorage.IRemoteStorage;
import com.xiaopeng.lib.framework.moduleinterface.netchannelmodule.remotestorage.StorageException;
import com.xiaopeng.lib.utils.DateUtils;
import com.xiaopeng.lib.utils.LogUtils;
import com.xiaopeng.lib.utils.crypt.AESUtils;
import com.xiaopeng.networkmonitor.netprobe.model.EncryptMessage;
import com.xiaopeng.networkmonitor.netprobe.model.OssConstants;
import com.xiaopeng.networkmonitor.netprobe.model.PostMessage;
import com.xiaopeng.networkmonitor.utils.FileUtil;
import com.xiaopeng.networkmonitor.utils.NetUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class UploadMessageTask implements Runnable {
    private static final String TAG = "UploadMessageTask";
    private String mFilePath;
    private Gson mGson;
    private Handler mHandler;
    private PostMessage mPostMessage;
    private boolean mPostSuccess;
    private IRemoteStorage mRemoteStorage;
    private final CountDownLatch mUploadLatch = new CountDownLatch(1);
    private String mUploadBucket = OssConstants.OSS_BUCKET_CN;

    public UploadMessageTask(IRemoteStorage remoteStorage, Gson gson, Handler handler, String path) {
        this.mRemoteStorage = remoteStorage;
        this.mGson = gson;
        this.mHandler = handler;
        this.mFilePath = path;
    }

    public void setUploadBucket(String bucket) {
        this.mUploadBucket = bucket;
    }

    public void setPostMessage(PostMessage postMessage) {
        this.mPostMessage = postMessage;
    }

    @Override // java.lang.Runnable
    public void run() {
        uploadMessage();
    }

    private void uploadMessage() {
        Log.d(TAG, "uploadMessage()");
        if (this.mRemoteStorage != null) {
            EncryptMessage encryptMessage = new EncryptMessage();
            encryptMessage.setTime(DateUtils.formatDate9(System.currentTimeMillis()));
            String encryptString = this.mGson.toJson(this.mPostMessage);
            encryptMessage.setEncryptMessage(AESUtils.encryptWithBase64(encryptString, "xiaopeng-auto-gz"));
            String uploadString = this.mGson.toJson(encryptMessage);
            try {
                NetUtils.setTrafficStatsUidTag();
                final String uploadFile = saveJsonToFile(uploadString);
                this.mRemoteStorage.uploadWithPathAndCallback(this.mUploadBucket, OssConstants.buildRemoteOssFileName(), uploadFile, new Callback() { // from class: com.xiaopeng.networkmonitor.netprobe.UploadMessageTask.1
                    @Override // com.xiaopeng.lib.framework.moduleinterface.netchannelmodule.remotestorage.Callback
                    public void onStart(String s, String s1) {
                    }

                    @Override // com.xiaopeng.lib.framework.moduleinterface.netchannelmodule.remotestorage.Callback
                    public void onSuccess(String s, String s1) {
                        LogUtils.d(UploadMessageTask.TAG, "onSuccess()");
                        UploadMessageTask.this.mPostSuccess = true;
                        FileUtil.deleteFile(uploadFile);
                        UploadMessageTask.this.mUploadLatch.countDown();
                    }

                    @Override // com.xiaopeng.lib.framework.moduleinterface.netchannelmodule.remotestorage.Callback
                    public void onFailure(String s, String s1, StorageException e) {
                        LogUtils.d(UploadMessageTask.TAG, "onFailure(): " + e);
                        UploadMessageTask.this.mPostSuccess = false;
                        FileUtil.deleteFile(uploadFile);
                        UploadMessageTask.this.mUploadLatch.countDown();
                    }
                });
            } catch (Exception e) {
                LogUtils.e(TAG, "fail to upload message:" + e);
                this.mPostSuccess = false;
            }
            try {
                this.mUploadLatch.await(10L, TimeUnit.SECONDS);
            } catch (Exception e2) {
                Log.e(TAG, "latch await is interrupt: " + e2);
            }
            this.mPostMessage.setUploadState(this.mPostSuccess);
            Message msg = this.mHandler.obtainMessage(7, this.mPostMessage);
            msg.sendToTarget();
        }
    }

    private String saveJsonToFile(String jsonString) {
        String fileName = String.valueOf(System.currentTimeMillis());
        File out = new File(this.mFilePath, fileName);
        FileWriter fileWriter = null;
        BufferedWriter bufWriter = null;
        try {
            try {
                fileWriter = new FileWriter(out);
                bufWriter = new BufferedWriter(fileWriter);
                bufWriter.write(jsonString);
                bufWriter.flush();
            } catch (Exception e) {
                LogUtils.e(TAG, "fail to write json: " + e);
            }
            return out.getPath();
        } finally {
            FileUtil.closeQuietly(fileWriter);
            FileUtil.closeQuietly(bufWriter);
        }
    }
}
