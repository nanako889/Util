package com.qbw.util.image;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.qbw.log.XLog;

/**
 * Created by qbw on 2015/8/18.
 */
public class ImageMemoryLruCacheUtil {

    private int mMaxMemPercent = 4;

    private LruCache<String, Bitmap> mLruCache;

    private ImageMemoryLruCacheUtil() {
    }

    public Bitmap getCache(String key) {
        return mLruCache.get(key);
    }

    public void addCache(String key, Bitmap bitmap) {
        mLruCache.put(key, bitmap);
    }

    public void removeCache(String key) {
        mLruCache.remove(key);
    }

    public void removeAllCache() {
        mLruCache.evictAll();
    }

    public static class Builder {
        private ImageMemoryLruCacheUtil mImageMemoryLruCacheUtil;

        public Builder() {
            mImageMemoryLruCacheUtil = new ImageMemoryLruCacheUtil();
        }

        /**
         * @param percent 占最大运行时内存的几分之一
         */
        public Builder cacheMaxMemoryPercent(int percent) {
            mImageMemoryLruCacheUtil.mMaxMemPercent = percent;
            return this;
        }

        public ImageMemoryLruCacheUtil build() {
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            // Use 1/8th of the available memory for this memory cache.
            final int cacheSize = maxMemory / mImageMemoryLruCacheUtil.mMaxMemPercent;
            XLog.v("memory cache size [%s]", BitmapUtil.stringBitmapSize(cacheSize * 1024));
            mImageMemoryLruCacheUtil.mLruCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    return BitmapUtil.getSizeInBytes(value) / 1024;
                }

                //true if the entry is being removed to make space, false if the removal was caused by a put(K, V) or remove(K).
                @Override
                protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                    XLog.v(evicted ? key + ",removed to make space" : key + ",removed by a put or remove");
                    oldValue.recycle();
                }
            };
            return mImageMemoryLruCacheUtil;
        }
    }
}
