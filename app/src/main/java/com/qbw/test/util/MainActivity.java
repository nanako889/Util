package com.qbw.test.util;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.qbw.log.XLog;
import com.qbw.util.AppUtil;
import com.qbw.util.DeviceUtil;
import com.qbw.util.FileUtil;
import com.qbw.util.MatcherUtil;
import com.qbw.util.NetUtil;
import com.qbw.util.PhotoUtil;
import com.qbw.util.ToastUtil;
import com.qbw.util.image.BitmapUtil;
import com.qbw.util.image.ImageDiskLruCacheUtil;
import com.qbw.util.image.ImageMemoryLruCacheUtil;
import com.qbw.util.image.ImageUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        XLog.setDebug(true);
        XLog.setSaveToFile("util");
//        showMemoryInfo();
//        testApp();
//        testUi();
//        testCache();
//        testMatch();
        //testCompress();
        testImage();
    }

    private void showMemoryInfo() {
        XLog.d(BitmapUtil.stringBitmapSize(Runtime.getRuntime().maxMemory()));//最大内存
        XLog.d(BitmapUtil.stringBitmapSize(Runtime.getRuntime().totalMemory()));//已申请内存
        XLog.d(BitmapUtil.stringBitmapSize(Runtime.getRuntime().freeMemory()));//已申请内存中没有使用的内存
    }

    private void testMatch() {
        XLog.d("isEmail:" + MatcherUtil.matchEmail("qbaowei@qq.com"));
        XLog.d("isEmail:" + MatcherUtil.matchEmail("qbaowei@qq.com1"));
        XLog.d("isEmail:" + MatcherUtil.matchEmail("qbaoweiqq.com"));
    }

    private void testCache() {
        ImageMemoryLruCacheUtil imageMemoryLruCacheUtil = new ImageMemoryLruCacheUtil.Builder().cacheMaxMemoryPercent(4).build();
        String file = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "q1.jpg";
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        for (int i = 0; i < 20; i++) {
            Bitmap bitmap = BitmapFactory.decodeFile(file, options);
            XLog.v("add [%s]", file + i);
            imageMemoryLruCacheUtil.addCache(file + i, bitmap);
        }

        List<String> keyList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            if (null != imageMemoryLruCacheUtil.getCache(file + i)) {
                XLog.d("cache[%s] exist", file + i);
                keyList.add(file + i);
            }
        }

        imageMemoryLruCacheUtil.removeCache(keyList.get(0));
        options.inSampleSize = 8;
        imageMemoryLruCacheUtil.addCache(keyList.get(1), BitmapFactory.decodeFile(file, options));

        ImageDiskLruCacheUtil imageDiskLruCacheUtil = new ImageDiskLruCacheUtil.Builder().cacheName("test").cacheSize(20 * 1024 * 1024).build(this);
        options.inSampleSize = 2;
        Bitmap bitmap = BitmapFactory.decodeFile(file, options);
        for (int i = 0; i < 20; i++) {
            try {
                file = FileUtil.getDiskCacheDir(this, "f" + i).getAbsolutePath();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
                XLog.v("add disk cache [%s]", file);
                imageDiskLruCacheUtil.addCache(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < 20; i++) {
            file = FileUtil.getDiskCacheDir(this, "f" + i).getAbsolutePath();
            if (null == imageDiskLruCacheUtil.getCache(file)) {
                XLog.d("%s was removed", file);
            }
        }
    }

    private void testApp() {
        XLog.d(AppUtil.getVersionCode(this) + "");
        XLog.d(AppUtil.getVersionName(this));
        XLog.d("imei:" + DeviceUtil.getIMEI(this));
        XLog.d("imsi" + DeviceUtil.getIMSI(this));
        XLog.d("number" + DeviceUtil.getPhoneNumber(this));
        XLog.d(DeviceUtil.getSDK() + "");
        XLog.d(DeviceUtil.getUA());
        XLog.d("net available " + NetUtil.isNetAvailable(this));
        XLog.d("wifi " + NetUtil.isWifi(this));
        XLog.d("2g " + NetUtil.is2G(this));
        XLog.d("3g " + NetUtil.is3G(this));
        XLog.d("4g " + NetUtil.is4G(this));
    }

    private void testUi() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                for (int loop = 0; loop <= 10; loop++) {
                    ToastUtil.showToast(MainActivity.this, "test " + loop);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private void testCompress() {
        String file = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "q1.jpg";

        XLog.d(BitmapUtil.getExifRotation(new File(file)) + "");

        try {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inSampleSize = 2;
            FileOutputStream fileOutputStream = null;

            Bitmap bitmap = BitmapFactory.decodeFile(file, options);
            XLog.d(bitmap.getConfig().toString());
            bitmap = BitmapUtil.compress(this, bitmap, 3 * 1024 * 1024);
            if (null == bitmap) {
                XLog.e("null == bitmap");
            }
            fileOutputStream = new FileOutputStream(Environment.getExternalStorageDirectory() + File.separator + "tempb.jpg");
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            bitmap.recycle();


        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Bitmap bitmap = BitmapUtil.compress(this, file, 100 * 1024);
            FileOutputStream fileOutputStream = new FileOutputStream(Environment.getExternalStorageDirectory() + File.separator + "tempf1.jpg");
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            bitmap.recycle();


            bitmap = BitmapUtil.compress(this, file, 1000 * 1024);
            fileOutputStream = new FileOutputStream(Environment.getExternalStorageDirectory() + File.separator + "tempf2.jpg");
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            bitmap.recycle();

            bitmap = BitmapUtil.compress(this, file, 100 * 1024 * 1024);
            fileOutputStream = new FileOutputStream(Environment.getExternalStorageDirectory() + File.separator + "tempf3.jpg");
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            bitmap.recycle();

            bitmap = BitmapUtil.compress(this, file, 5 * 1024 * 1024);
            fileOutputStream = new FileOutputStream(Environment.getExternalStorageDirectory() + File.separator + "tempf4.jpg");
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            bitmap.recycle();

            bitmap = BitmapUtil.compress(this, file, 3 * 1024 * 1024);
            fileOutputStream = new FileOutputStream(Environment.getExternalStorageDirectory() + File.separator + "tempf5.jpg");
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            bitmap.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 从网络获取图片保存到缓存
     *
     * @param imageUrl
     * @return
     */
    public boolean getImageBitmap(String imageUrl) {
        boolean b = false;
        HttpURLConnection http = null;
        try {
            http = (HttpURLConnection) new URL(imageUrl).openConnection();
            http.setConnectTimeout(5000);
            http.setReadTimeout(5000);
            http.setRequestMethod("GET");
            http.connect();
            if (HttpURLConnection.HTTP_OK == http.getResponseCode()) {
                InputStream inputStream = http.getInputStream();
                //b = addCache(imageUrl, inputStream);
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            if (null != http) {
                http.disconnect();
            }
        }
        return b;
    }

    public void testImage() {
        XLog.d(FileUtil.getDiskCacheDir(this).getAbsolutePath());
        XLog.d(FileUtil.getFileDir(this).getAbsolutePath());
        XLog.d(getCacheDir().getAbsolutePath());
        XLog.d(getFilesDir().getAbsolutePath());
        XLog.d(FileUtil.getGalleryDir().getAbsolutePath());
        List<String> imageInfos = ImageUtil.getAllImagesPath(this);
        XLog.line(true);
        XLog.d("image cout=%d", null == imageInfos ? 0 : imageInfos.size());
        for (String s : imageInfos) {
            XLog.d(s);
        }
        XLog.line(false);

        ImageUtil.getAllImagesPathAsync(this, mImageCallback);
    }

    private ImageUtil.CallBack mImageCallback = new ImageUtil.CallBack() {
        @Override
        public void onGetImageInfo(String imageInfo) {
            XLog.d(imageInfo);
        }
    };

    private PhotoUtil.CallBack mCallBack = new PhotoUtil.CallBack() {
        @Override
        public void onPhotoCamera(String photoPath) {
        }

        @Override
        public void onPhotoGallery(String photoPath) {
        }

        @Override
        public void onPhotoCrop(String photoPath) {
        }

        @Override
        public void onPhotoCancel() {
            XLog.d("onPhotoCancel");
        }

        @Override
        public void onPhotoFailed() {
            XLog.d("onPhotoFailed");
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PhotoUtil.onActivityResult(this, requestCode, resultCode, data);
    }

    public void onCameraClick(View v) {
        //PhotoUtil.getPhotoFromCamera(this, FileUtil.getFileDir(this) + File.separator + "camera.jpg", mCallBack);
        PhotoUtil.getPhotoFromCamera(this, mCallBack);
    }

    public void onCameraCropClick(View v) {
        //PhotoUtil.getPhotoFromCamera(this, FileUtil.getFileDir(this) + File.separator + "camera.jpg", 140, 140, FileUtil.getFileDir(this) + File.separator + "crop.%s", mCallBack);
        PhotoUtil.getPhotoFromCamera(this, 140, 140, mCallBack);
    }

    public void onGalleryClick(View v) {
        PhotoUtil.getPhotoFromGallery(this, mCallBack);
    }

    public void onGalleryCropClick(View v) {
        //PhotoUtil.getPhotoFromGallery(this, 140, 140, FileUtil.getFileDir(this) + File.separator + "crop.%s", mCallBack);
        PhotoUtil.getPhotoFromGallery(this, 140, 140, mCallBack);
    }
}
