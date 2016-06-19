package com.qbw.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import com.qbw.log.XLog;
import com.qbw.util.image.ImageUtil;

import java.io.File;

/**
 * 提供一个统一的接口，用于从相机或者图库获取照片
 */
public class PhotoUtil {

    public static final int REQUEST_FROM_CAMERA = 0;
    public static final int REQUEST_FROM_GALLERY = 1;
    public static final int REQUEST_CROP = 2;

    /**
     * 是否需要裁剪图片
     */
    private static boolean sNeedCrop;
    private static int sCropWidth;
    private static int sCropHeight;


    /**
     * 从相机获取图片的时候,图片保存到这个路径下面
     */
    private static String sPhotoCameraSavePath;

    /**
     * 裁剪之后的图片保存在此路径
     */
    private static String sPhotoCropSavePath;

    /**
     * 保存请求值，用于删除相机产生的临时文件
     */
    private static int sRequestFrom;

    private static CallBack sCallBack;

    /**
     * 从相机获取图片
     */
    public static void getPhotoFromCamera(Activity activity, String photoCameraSavePath, int cropWidth, int cropHeight, String photoCropSavePath, CallBack callBack) {
        sPhotoCameraSavePath = photoCameraSavePath;
        sNeedCrop = 0 != cropWidth && 0 != cropHeight;
        sCropWidth = cropWidth;
        sCropHeight = cropHeight;
        sPhotoCropSavePath = photoCropSavePath;
        sCallBack = callBack;

        XLog.d("photoCameraSavePath[%s], sNeedCrop[%b], cropWidth[%d], cropHeight[%d], photoCropSavePath[%s]", photoCameraSavePath, sNeedCrop, cropWidth, cropHeight, photoCropSavePath);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(photoCameraSavePath)));
        activity.startActivityForResult(intent, REQUEST_FROM_CAMERA);
    }

    public static void getPhotoFromCamera(Activity activity, String photoCameraSavePath, CallBack callBack) {
        getPhotoFromCamera(activity, photoCameraSavePath, 0, 0, "", callBack);
    }

    /**
     * 从图库获取图片
     */
    public static void getPhotoFromGallery(Activity activity, int cropWidth, int cropHeight, String photoCropSavePath, CallBack callBack) {
        sNeedCrop = 0 != cropWidth && 0 != cropHeight;
        sCropWidth = cropWidth;
        sCropHeight = cropHeight;
        sPhotoCropSavePath = photoCropSavePath;
        sCallBack = callBack;

        XLog.d("sNeedCrop[%b], cropWidth[%d], cropHeight[%d], photoCropSavePath[%s]", sNeedCrop, cropWidth, cropHeight, photoCropSavePath);

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activity.startActivityForResult(intent, REQUEST_FROM_GALLERY);
    }

    public static void getPhotoFromGallery(Activity activity, CallBack callBack) {
        getPhotoFromGallery(activity, 0, 0, "", callBack);
    }

    /**
     * 裁剪照片
     *
     * @param uri
     * @author qinbaowei
     * @version 创建时间 2015-10-16
     */
    public static void cropPhoto(Activity activity, Uri uri, String photoFormat) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("scale", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", sCropWidth);
        intent.putExtra("outputY", sCropHeight);
        intent.putExtra("outputFormat", photoFormat);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(sPhotoCropSavePath)));
        intent.putExtra("noFaceDetection", true);
        activity.startActivityForResult(intent, REQUEST_CROP);
    }

    /**
     * 处理activity的onActivityResult
     *
     * @param requestCode
     * @param resultCode
     * @param intent
     * @author qinbaowei
     * @version 创建时间 2015-10-16
     */
    public static void onActivityResult(Activity activity, int requestCode,
                                        int resultCode, Intent intent) {
        switch (resultCode) {
            case Activity.RESULT_OK:
                onActivityResultOk(activity, requestCode, intent);
                break;
            case Activity.RESULT_CANCELED:
                sCallBack.onPhotoCancel();
                break;
            case Activity.RESULT_FIRST_USER:
                // Start of user-defined activity results
                break;
            default:
                sCallBack.onPhotoFailed();
                break;
        }
    }

    /**
     * 处理选取图片成功的情况
     *
     * @param requestCode
     * @param intent
     * @author qinbaowei
     * @version 创建时间 2015-10-16
     */
    private static void onActivityResultOk(Activity activity, int requestCode, Intent intent) {
        if (REQUEST_FROM_CAMERA == requestCode) {
            sRequestFrom = requestCode;
            if (sNeedCrop) {
                cropPhoto(activity, Uri.fromFile(new File(sPhotoCameraSavePath)), "jpg");
            } else {
                sCallBack.onPhotoCamera(sPhotoCameraSavePath);
            }
        } else if (REQUEST_FROM_GALLERY == requestCode) {
            sRequestFrom = requestCode;
            if (null != intent && null != intent.getData()) {
                String imagePath = ImageUtil.getImagePath(activity, intent.getData());
                XLog.d("pick image[%s] from gallery", imagePath);
                if (sNeedCrop) {
                    cropPhoto(activity, intent.getData(), FileUtil.getFileExtensionFromPath(imagePath));
                } else {
                    sCallBack.onPhotoGallery(imagePath);
                }
            } else {
                sCallBack.onPhotoFailed();
            }
        } else if (REQUEST_CROP == requestCode) {
            sCallBack.onPhotoCrop(sPhotoCropSavePath);
            if (sRequestFrom == REQUEST_FROM_CAMERA) {
                FileUtil.deleteFile(sPhotoCameraSavePath);
            }
        }
    }

    public interface CallBack {
        void onPhotoCamera(String photoPath);

        void onPhotoGallery(String photoPath);

        void onPhotoCrop(String photoPath);

        void onPhotoCancel();

        void onPhotoFailed();
    }
}
