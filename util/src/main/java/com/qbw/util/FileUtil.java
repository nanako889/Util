package com.qbw.util;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.qbw.log.XLog;

import java.io.File;

/**
 * @author qbw
 * @createtime 2016/05/25 15:03
 */


public class FileUtil {

    /**
     * @param context
     * @param uniqueName
     * @return '缓存路径' + '/' + uniqueName
     */
    public static File getDiskCacheDir(Context context, String uniqueName) {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        return new File(isExternalStorageExist() ? context.getExternalCacheDir().getPath() : context.getCacheDir().getPath() + File.separator + uniqueName);
    }

    /**
     * @param context
     * @return 缓存路径
     */
    public static File getDiskCacheDir(Context context) {
        return isExternalStorageExist() ? context.getExternalCacheDir() : context.getCacheDir();
    }

    /**
     * @param context
     * @return 保存文件的路径
     */
    public static File getFileDir(Context context) {
        return isExternalStorageExist() ? context.getExternalFilesDir(null) : context.getFilesDir();
    }

    /**
     * @param context
     * @return 如果存在sd卡则返回sd卡根目录, 否则返回内置'fileDir'
     */
    public static File getExternalStorageDir(Context context) {
        return isExternalStorageExist() ? Environment.getExternalStorageDirectory() : context.getFilesDir();
    }

    /**
     * @return 保存相机图片的路径
     */
    public static File getGalleryDir() {
        return isExternalStorageExist() ? new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DCIM) : new File("");
    }

    /**
     * @return 是否存在外置sd卡
     */
    public static boolean isExternalStorageExist() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable();
    }

    /**
     * @param url
     * @return 文件后缀
     */
    public static String getFileExtensionFromUrl(String url) {
        return MimeTypeMap.getFileExtensionFromUrl(url);
    }

    /**
     * @param path
     * @return 文件后缀
     */
    public static String getFileExtensionFromPath(String path) {
        return path.substring(path.lastIndexOf(".") + 1);
    }

    /**
     * @param extension 文件后缀(文件格式)
     * @return 文件格式对应的Mime类型(比如'jpg'->'image/*')
     */
    public static String getMimeTypeFromExtension(String extension) {
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    /**
     * 删除一个文件
     *
     * @param filePath
     * @return 是否删除成功
     */
    public static boolean deleteFile(String filePath) {
        boolean b = false;
        if (TextUtils.isEmpty(filePath)) {
            XLog.w("file path is null or empty!");
            return b;
        }
        File file = new File(filePath);
        if (file.exists()) {
            b = file.delete();
            XLog.d("delete %s %s", filePath, b ? "success" : "failed");
        } else {
            b = true;
            XLog.w("%s is not exist!", filePath);
        }
        return b;
    }
}
