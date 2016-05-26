package com.qbw.util.image;

import android.content.Context;

import com.qbw.encryption.MD5Util;
import com.qbw.log.XLog;
import com.qbw.util.AppUtil;
import com.qbw.util.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author qbw
 * @createtime 2016/04/14 19:30
 */


public class ImageDiskLruCacheUtil {

    /**
     * 缓存目录的名字
     */
    private String cacheName;

    /**
     * 缓存的大小
     */
    private int cacheSize;

    private DiskLruCache diskLruCache;

    private ImageDiskLruCacheUtil() {
    }

    /**
     * @param imagePath imageUrl地址对应的文件(将会作为key)
     * @return 是否添加成功
     */
    public boolean addCache(String imagePath) {
        boolean b = false;
        try {
            InputStream inputStream = new FileInputStream(imagePath);
            //这个key要是唯一的，而且这个key 最长120个字符，且只能包括a-z,0-9,下划线以及减号
            //通过'imageUrl'得到MD5值,保证key的合法
            DiskLruCache.Editor editor = diskLruCache.edit(MD5Util.encryptHex(imagePath));
            //index的取值范围[0,valueCount},我们初始化valueCount为1,所以index只能取0值
            OutputStream outputStream = editor.newOutputStream(0);
            byte[] byteBuff = new byte[8 * 1024];
            int readLen;
            while (-1 != (readLen = inputStream.read(byteBuff))) {
                outputStream.write(byteBuff, 0, readLen);
            }
            outputStream.flush();
            editor.commit();
            diskLruCache.flush();
            b = true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    public InputStream getCache(String imagePath) {
        try {
            DiskLruCache.Snapshot snapshot = diskLruCache.get(MD5Util.encryptHex(imagePath));
            if (null != snapshot) {
                return snapshot.getInputStream(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void removeCache(String imagePath) {
        try {
            diskLruCache.remove(MD5Util.encryptHex(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class Builder {

        protected ImageDiskLruCacheUtil mImageDiskLruCacheUtil;

        public Builder() {
            mImageDiskLruCacheUtil = new ImageDiskLruCacheUtil();
        }

        /**
         * @param cacheName 缓存文件夹的名字
         */
        public Builder cacheName(String cacheName) {
            mImageDiskLruCacheUtil.cacheName = cacheName;
            return this;
        }

        /**
         * @param cacheSize 缓存的大小
         */
        public Builder cacheSize(int cacheSize) {
            mImageDiskLruCacheUtil.cacheSize = cacheSize;
            return this;
        }

        public ImageDiskLruCacheUtil build(Context context) {
            try {
                XLog.v("disk cache info [%s, %s]", mImageDiskLruCacheUtil.cacheName, BitmapUtil.stringBitmapSize(mImageDiskLruCacheUtil.cacheSize));
                File cacheDir = FileUtil.getDiskCacheDir(context, mImageDiskLruCacheUtil.cacheName);
                mImageDiskLruCacheUtil.diskLruCache = DiskLruCache.open(cacheDir, AppUtil.getVersionCode(context), 1, mImageDiskLruCacheUtil.cacheSize);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return mImageDiskLruCacheUtil;
        }
    }
}
