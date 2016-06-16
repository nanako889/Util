package com.qbw.util;

import android.app.Activity;
import android.content.Context;
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
    private boolean mNeedCrop;
    private int mCropWidth;
    private int mCropHeight;

    /**
     * 图片保存的总目录
     */
    private String mPhotoSavePath;

    /**
     * 从相机获取图片的时候,图片保存到这个路径下面
     */
    private String mPhotoCameraSavePath;

    /**
     * 裁剪之后的图片保存在此路径
     */
    private String mPhotoCropSavePath;

    /**
     * 保存请求值，用于删除相机产生的临时文件
     */
    private int mRequestFrom;

    private Context mContext;

    private CallBack mCallBack;

    public PhotoUtil(Context context, CallBack callBack) {
        this(context, false, 0, 0, callBack);
    }

    public PhotoUtil(Context context, boolean needCrop, int cropWidth, int cropHeight, CallBack callBack) {
        mContext = context;
        mNeedCrop = needCrop;
        mCropWidth = cropWidth;
        mCropHeight = cropHeight;
        mCallBack = callBack;

        mPhotoSavePath = FileUtil.getFileDir(mContext).getAbsolutePath() + File.separator + "photo_pick";
        XLog.d("mPhotoSavePath [%s]", mPhotoSavePath);
        File file = new File(mPhotoSavePath);
        if (!file.exists()) {
            XLog.d("create dir [%s]", mPhotoSavePath);
            file.mkdirs();
        }
        mPhotoCameraSavePath = mPhotoSavePath + File.separator + "camera.jpg";
        XLog.d("mPhotoCameraSavePath [%s]", mPhotoCameraSavePath);
    }

    /**
     * 从相机获取图片
     *
     * @param activity
     */
    public void getPhotoFromCamera(Activity activity) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mPhotoCameraSavePath)));
        activity.startActivityForResult(intent, REQUEST_FROM_CAMERA);
    }

    /**
     * 从图库获取图片
     *
     * @param activity
     */
    public void getPhotoFromGallery(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activity.startActivityForResult(intent, REQUEST_FROM_GALLERY);
    }

    /**
     * 裁剪照片
     *
     * @param uri
     * @author qinbaowei
     * @version 创建时间 2015-10-16
     */
    public void cropPhoto(Activity activity, Uri uri, String photoFormat) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("scale", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", mCropWidth);
        intent.putExtra("outputY", mCropHeight);
        intent.putExtra("outputFormat", photoFormat);
        intent.putExtra("return-data", false);
        mPhotoCropSavePath = String.format("%s%s%d.%s", mPhotoSavePath, File.separator, System.currentTimeMillis(), photoFormat);
        XLog.d("mPhotoCropSavePath [%s]", mPhotoCropSavePath);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mPhotoCropSavePath)));
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
    public void onActivityResult(Activity activity, int requestCode,
                                 int resultCode, Intent intent) {
        switch (resultCode) {
            case Activity.RESULT_OK:
                onActivityResultOk(activity, requestCode, intent);
                break;
            case Activity.RESULT_CANCELED:
                mCallBack.onPhotoCancel();
                break;
            case Activity.RESULT_FIRST_USER:
                // Start of user-defined activity results
                break;
            default:
                mCallBack.onPhotoFailed();
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
    private void onActivityResultOk(Activity activity, int requestCode, Intent intent) {
        if (REQUEST_FROM_CAMERA == requestCode) {
            XLog.d("get photo from camera");
            mRequestFrom = requestCode;
            if (mNeedCrop) {
                cropPhoto(activity, Uri.fromFile(new File(mPhotoCameraSavePath)), "jpg");
            } else {
                mCallBack.onPhotoCamera(mPhotoCameraSavePath);
            }
        } else if (REQUEST_FROM_GALLERY == requestCode) {
            XLog.d("get photo from gallery");
            mRequestFrom = requestCode;
            if (null != intent && null != intent.getData()) {
                String imagePath = ImageUtil.getImagePath(activity, intent.getData());
                XLog.d("pick image[%s] from gallery", imagePath);
                if (mNeedCrop) {
                    cropPhoto(activity, intent.getData(), FileUtil.getFileExtensionFromPath(imagePath));
                } else {
                    mCallBack.onPhotoGallery(imagePath);
                }
            } else {
                mCallBack.onPhotoFailed();
            }
        } else if (REQUEST_CROP == requestCode) {
            mCallBack.onPhotoCrop(mPhotoCropSavePath);
            if (mRequestFrom == REQUEST_FROM_CAMERA) {
                FileUtil.deleteFile(mPhotoCameraSavePath);
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
