package com.qbw.util;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * 常用的一些工具方法
 */
public class Util {
    private static int lastClickId;
    private static long lastClickTime;

    public static boolean isFastClick(int id) {
        if (id == lastClickId) {
            long time = System.currentTimeMillis();
            if (time - lastClickTime < 500) {
                return true;
            }
            lastClickTime = time;
        } else {
            lastClickId = id;
        }
        return false;
    }

    /**
     * 关闭键盘
     * @param context
     */
    public static void closeKeybord(final Context context) {
        InputMethodManager imm = ((InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE));
        final View currentFocusView = ((Activity)context).getCurrentFocus();
        if (currentFocusView != null) {
            final IBinder windowToken = currentFocusView.getWindowToken();
            if (imm != null && windowToken != null) {
                imm.hideSoftInputFromWindow(windowToken, 0);
            }
        }
    }

    /**
     * 打开键盘
     * @param context
     * @param view
     */
    public static void openKeybord(final Context context, final View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,  InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
}