package com.xiaopeng.networkmonitor.utils;

import com.xiaopeng.lib.utils.FileUtils;
import java.io.Closeable;
import java.io.File;
/* loaded from: classes.dex */
public final class FileUtil {
    public static boolean deleteFile(String filePath) {
        File fileToDel = new File(filePath);
        if (fileToDel.exists()) {
            return fileToDel.delete();
        }
        return false;
    }

    public static void closeQuietly(Closeable closeable) {
        FileUtils.closeQuietly(closeable);
    }
}
