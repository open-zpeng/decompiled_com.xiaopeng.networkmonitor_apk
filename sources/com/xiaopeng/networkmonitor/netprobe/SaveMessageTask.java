package com.xiaopeng.networkmonitor.netprobe;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.google.gson.Gson;
import com.xiaopeng.networkmonitor.netprobe.model.PostMessage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class SaveMessageTask implements Runnable {
    private static final String TAG = "SaveMessageTask";
    private Gson mGson;
    private final Handler mHandler;
    private List<PostMessage> mPostMsgs;
    private String mSaveFileName;
    private String mSavedDir;

    public SaveMessageTask(String dir, String cacheFile, Gson gson, List<PostMessage> messages, Handler handler) {
        this.mSavedDir = dir;
        this.mSaveFileName = cacheFile;
        this.mGson = gson;
        this.mPostMsgs = messages;
        this.mHandler = handler;
    }

    @Override // java.lang.Runnable
    public void run() {
        saveMessagesToDisk();
    }

    private void saveMessagesToDisk() {
        Log.d(TAG, "saveMessagesToDisk()");
        File file = new File(this.mSavedDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        File cacheFile = new File(file, this.mSaveFileName);
        if (!cacheFile.exists()) {
            try {
                cacheFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        FileWriter fileWriter = null;
        BufferedWriter bufWriter = null;
        List<PostMessage> savedMessages = new ArrayList<>();
        try {
            try {
                try {
                    fileWriter = new FileWriter(cacheFile);
                    bufWriter = new BufferedWriter(fileWriter);
                    for (PostMessage pm : this.mPostMsgs) {
                        String jsonString = this.mGson.toJson(pm);
                        bufWriter.write(jsonString);
                        bufWriter.newLine();
                        bufWriter.flush();
                        savedMessages.add(pm);
                    }
                    bufWriter.flush();
                    Message msg = this.mHandler.obtainMessage(16, savedMessages);
                    this.mHandler.sendMessage(msg);
                    try {
                        fileWriter.close();
                    } catch (Exception e2) {
                    }
                    bufWriter.close();
                } catch (Throwable th) {
                    if (fileWriter != null) {
                        try {
                            fileWriter.close();
                        } catch (Exception e3) {
                        }
                    }
                    if (bufWriter != null) {
                        try {
                            bufWriter.close();
                        } catch (Exception e4) {
                        }
                    }
                    throw th;
                }
            } catch (Exception e5) {
                Log.e(TAG, "fail to save message to disk: " + e5);
                if (fileWriter != null) {
                    try {
                        fileWriter.close();
                    } catch (Exception e6) {
                    }
                }
                if (bufWriter != null) {
                    bufWriter.close();
                }
            }
        } catch (Exception e7) {
        }
    }
}
