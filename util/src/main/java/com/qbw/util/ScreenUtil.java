package com.qbw.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.qbw.log.XLog;

/**
 * @author qbw
 * @createtime 2016/05/25 15:11
 */


public class ScreenUtil {

    /**
     * @param activity
     * @return 是否是竖屏
     */
    public static boolean isPortrait(Activity activity) {
        return Configuration.ORIENTATION_PORTRAIT == activity.getResources().getConfiguration().orientation;
    }

    /**
     * @param activity
     * @return 是否是横屏
     */
    public static boolean isLandscape(Activity activity) {
        return Configuration.ORIENTATION_LANDSCAPE == activity.getResources().getConfiguration().orientation;
    }

    /**
     * 强制竖屏
     * @param activity
     */
    public static void requestPortrait(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * 强制横屏
     * @param activity
     */
    public static void requestLandscape(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    /**
     * @param context
     * @return 屏幕实际的宽高（sdk19以上包括了虚拟按键的高度）
     */
    public static Point getRealSize(Context context) {
        Point screenSize = new Point();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            screenSize = getSize(context);
        } else {
            ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRealSize(screenSize);
        }
        XLog.v(String.format("screen size[%d, %d]", screenSize.x, screenSize.y));
        return screenSize;
    }

    /**
     * @param context
     * @return 屏幕的宽高（不包括虚拟按键）
     */
    public static Point getSize(Context context) {
        Point screenSize = new Point();
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        screenSize.set(dm.widthPixels, dm.heightPixels);
        return screenSize;
    }

    /**
     * 横屏的时候，设置全屏
     * @param activity
     */
    public static void onConfigureChangedFullScreen(Activity activity) {
        if (isLandscape(activity)) {
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            activity.getWindow().setAttributes(attrs);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    /**
     * 横屏的时候，设置全屏(sdk19以后的系统如果有虚拟按键会隐藏掉)
     * @param activity
     */
    public static void onConfigureChangedRealFullScreen(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            onConfigureChangedFullScreen(activity);
        } else {
            if (isLandscape(activity)) {
                View decorView = activity.getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            } else {
                View decorView = activity.getWindow().getDecorView();
                int source = decorView.getSystemUiVisibility();
                source &= (~View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                source &= (~View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                source &= (~View.SYSTEM_UI_FLAG_FULLSCREEN);
                decorView.setSystemUiVisibility(source);
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }
        }
    }

    /**
     * @param context
     * @param dp
     * @return px value correspond to dp value
     */
    public static int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * @param context
     * @param px
     * @return dp value correspond to px value
     */
    public static int px2dp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }
}
