package com.qbw.util.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.NonNull;

import com.qbw.log.XLog;
import com.qbw.util.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author qbw
 * @createtime 2016/05/25 17:49
 */


public class BitmapUtil {

    /**
     * @param bitmap
     * @return 字节数
     */
    public static int getSizeInBytes(@NonNull Bitmap bitmap) {

        // There's a known issue in KitKat where getAllocationByteCount() can throw an NPE. This was
        // apparently fixed in MR1: http://bit.ly/1IvdRpd. So we do a version check here, and
        // catch any potential NPEs just to be safe.
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            try {
                return bitmap.getAllocationByteCount();
            } catch (NullPointerException npe) {
                // Swallow exception and try fallbacks.
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        }

        // Estimate for earlier platforms.
        return bitmap.getHeight() * bitmap.getRowBytes();
    }

    /**
     * @param options
     * @param reqWidth  目标宽度
     * @param reqHeight 目标高度
     * @return inSampleSize的值可以缩放到目标大小
     */
    public static int calculateInSampleSize(@NonNull BitmapFactory.Options options, int reqWidth, int reqHeight) {
        XLog.line(true);
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        XLog.v(String.format("options size[%d, %d]", width, height));
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        XLog.line(false);
        return inSampleSize;
    }

    /**
     * 按jpg质量100压缩
     * @param context
     * @param bitmap
     * @param reqSize
     * @return 压缩到目标大小的bitmap(null,表示压缩失败)
     */
    public static Bitmap compress(@NonNull Context context, @NonNull Bitmap bitmap, int reqSize) {
        return compress(context, bitmap, Bitmap.CompressFormat.JPEG, 100, reqSize);
    }

    /**
     * 将图片转换成inputstream，循环增加inSampleSize，压缩到指定大小
     *
     * @param context
     * @param bitmap
     * @param reqSize
     * @return 压缩到目标大小的bitmap(null,表示压缩失败)
     */
    public static Bitmap compress(@NonNull Context context, @NonNull Bitmap bitmap, Bitmap.CompressFormat compressFormat, int quality, int reqSize) {
        XLog.line(true);
        Bitmap currBitmap = null;

        int currSize = getSizeInBytes(bitmap);
        XLog.v("reqsize[%s], currsize[%s]", stringBitmapSize(reqSize), stringBitmapSize(currSize));
        if (currSize <= reqSize) {
            currBitmap = bitmap;
        } else {
            try {
                File tempFile = FileUtil.getDiskCacheDir(context, SystemClock.uptimeMillis() + "");
                XLog.v("temp file %s", tempFile.getAbsolutePath());
                XLog.v("before compress to file");
                FileOutputStream outputStream = new FileOutputStream(tempFile);
                bitmap.compress(compressFormat, quality, outputStream);
                XLog.v("after compress to file");
                currBitmap = compress(context, tempFile.getAbsolutePath(), reqSize);
                tempFile.delete();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        XLog.line(false);
        return currBitmap;
    }

    /**
     * @param imagePath 图片路径
     * @param reqSize   目标大小（单位字节）
     * @return 压缩到目标大小的bitmap(null,表示压缩失败)
     */
    public static Bitmap compress(@NonNull Context context, @NonNull String imagePath, int reqSize) {
        XLog.line(true);
        Bitmap currBitmap = null;
        if (!new File(imagePath).exists()) {
            XLog.e("%s not exist!", imagePath);
        } else {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            final int beginInSampleSize = 4;//2的2次方,采用二分法以4为中间点,循环增大或减小inSampleSize压缩图片
            options.inSampleSize = beginInSampleSize;

            for (; ; ) {
                try {
                    XLog.v("inSampleSize[%d]", options.inSampleSize);
                    currBitmap = BitmapFactory.decodeFile(imagePath, options);
                    int currSize = getSizeInBytes(currBitmap);
                    XLog.v("reqsize[%s], currsize[%s]", stringBitmapSize(reqSize), stringBitmapSize(currSize));
                    if (currSize > reqSize) {
                        currBitmap.recycle();
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    XLog.e(e);
                }
                options.inSampleSize *= 2;//必须是2的n次方，设置成其他的值不会对压缩的结果产生任何变化
            }

            if (options.inSampleSize <= beginInSampleSize) {//按beginInSampleSize压缩的时候就已经小于reqSize,需要继续减小inSampleSize找一个最接近reqSize
                Bitmap preCurrBitmap = currBitmap;
                final int currInSampleSize = options.inSampleSize;
                for (int i = currInSampleSize / 2; i >= 1; i /= 2) {
                    options.inSampleSize = i;
                    try {
                        XLog.v("inSampleSize[%d]", options.inSampleSize);
                        currBitmap = BitmapFactory.decodeFile(imagePath, options);
                        int currSize = getSizeInBytes(currBitmap);
                        XLog.v("reqsize[%s], currsize[%s]", stringBitmapSize(reqSize), stringBitmapSize(currSize));
                        if (currSize > reqSize) {
                            currBitmap.recycle();
                            currBitmap = preCurrBitmap;
                            break;
                        } else {
                            preCurrBitmap.recycle();
                            if (currSize < reqSize) {
                                preCurrBitmap = currBitmap;
                            } else {
                                break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        XLog.e(e);
                    }
                }
            }
        }
        XLog.line(false);
        return currBitmap;
    }

    /**
     * @param imageFile
     * @return 图片的旋转角度
     */
    public static int getExifRotation(@NonNull File imageFile) {
        try {
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            // We only recognize a subset of orientation tag values
            switch (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
                default:
                    return ExifInterface.ORIENTATION_UNDEFINED;
            }
        } catch (IOException e) {
            e.printStackTrace();
            XLog.e(e);
            return 0;
        }
    }

    /**
     * @param bitmap
     * @param rotation
     * @return 旋转之后的bitmap
     */
    public static Bitmap rotateImage(@NonNull Bitmap bitmap, int rotation) {
        try {
            Matrix matrix = new Matrix();
            matrix.setRotate(rotation);
            return bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (Exception e) {
            e.printStackTrace();
            XLog.e(e);
        }
        return null;
    }

    public static String stringBitmapSize(long size) {
        if (size < 1024) {
            return String.format("%d, %dbyte", size, size);
        } else if (size < 1024 * 1024) {
            return String.format("%d, %dkb", size, size / 1024);
        } else {
            return String.format("%d, %dm", size, size / 1024 / 1024);
        }
    }
}
