package com.qbw.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * @author qbw
 * @createtime 2016/05/25 15:03
 */


public class FileUtil {

    /**
     * @param context
     * @param uniqueName
     * @return 获取缓存的完整路径
     */
    public static File getDiskCacheDir(Context context, String uniqueName) {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                        !Environment.isExternalStorageRemovable() ? context.getExternalCacheDir().getPath() :
                        context.getCacheDir().getPath();

        return new File(cachePath + File.separator + uniqueName);
    }
}
