package com.qbw.util.image;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qbw
 * @createtime 2016/06/16 13:46
 */


public class ImageUtil {

    /**
     * @param uri
     * @return uri图片对应的路径
     */
    public static String getImagePath(Context context, Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public static List<String> getAllImagesPath(Context context) {
        return getAllImagesPath(context, null);
    }

    public static void getAllImagesPathAsync(final Context context, final CallBack callBack) {
        new Thread() {
            @Override
            public void run() {
                getAllImagesPath(context, callBack);
            }
        }.start();
    }

    /**
     * Getting All Images Path(以拍照时间降序排列)
     *
     * @param context
     * @param callBack 不为null则异步回调每一张图片的信息
     * @return List with images Path
     */
    private static List<String> getAllImagesPath(Context context, CallBack callBack) {

        List<String> imageInfoList = new ArrayList<>();

        int columnIndexData;

        String[] projection = {MediaStore.MediaColumns.DATA};

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, new String(MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC"));

        columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        while (cursor.moveToNext()) {
            String path = cursor.getString(columnIndexData);
            if (null != callBack) {
                callBack.onGetImageInfo(path);
            }
            imageInfoList.add(path);
        }

        return imageInfoList;
    }

    public interface CallBack {
        void onGetImageInfo(String imagePath);
    }
}
