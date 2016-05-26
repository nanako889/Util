package com.qbw.util;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * @author qbw
 * @createtime 2016/04/29 18:12
 * 在UI线程中弹toast
 */


public class ToastUtil {

    private static Handler sHandler;
    private static Toast sToast = null;

    public static void showToast(Context context, String message) {
        showToast(context, message, Toast.LENGTH_SHORT);
    }

    public static void showToast(Context context, int messageResId) {
        showToast(context, context.getResources().getString(messageResId));
    }

    public static void showToastLong(Context context, String message) {
        showToast(context, message, Toast.LENGTH_LONG);
    }

    public static void showToastLong(Context context, int messageResId) {
        showToastLong(context, context.getResources().getString(messageResId));
    }

    public static void showToast(Context context, int messageResId, int duration) {
        showToast(context, context.getResources().getString(messageResId), duration);
    }

    public static void showToast(final Context context, final String message, final int duration) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        if (null == sHandler) {
            HandlerThread handlerThread = new HandlerThread("ToastUtil");
            handlerThread.start();
            sHandler = new Handler(handlerThread.getLooper());
        }
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                if (null == sToast) {
                    sToast = Toast.makeText(context.getApplicationContext(), message, duration);
                } else {
                    sToast.setText(message);
                    sToast.setDuration(duration);
                }
                sToast.show();
            }
        });
    }

    public static void cancelToast() {
        if (null != sToast) {
            sToast.cancel();
            sToast = null;
        }
    }
}
