package com.qbw.util.statusbar;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


/**
 * 导航条颜色设置还有问题，不要使用此功能
 */
public class SystemBarTintHelper {

    public void onCreate(Activity act, boolean statusBar, int statusRes) {
        onCreate(act, statusBar, statusRes, false);
    }

    public void onCreate(Activity act, boolean statusBar, int statusRes, boolean androidMLightStatusBar) {
        onCreate(act, statusBar, statusRes, false, 0, androidMLightStatusBar);
    }

    public void onCreate(Activity act, boolean statusBar, int statusRes, boolean navBar, int navRes, boolean androidMLightStatusBar) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            onCreateLollipop(act, statusBar, statusRes, navBar, navRes, androidMLightStatusBar);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            onCreateKitkat(act, statusBar, statusRes, navBar, navRes);
        }
    }

    public void onCreateOnlyKitkat(Activity act, boolean statusBar, int statusRes, boolean navBar, int navRes) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            onCreateKitkat(act, statusBar, statusRes, navBar, navRes);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onCreateLollipop(Activity act, boolean statusBar, int statusRes, boolean navBar, int navRes, boolean androidMLightStatusBar) {
        Window window = act.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        int visibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && androidMLightStatusBar) {
            visibility |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        window.getDecorView().setSystemUiVisibility(visibility);

        if (statusBar || navBar) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        if (statusBar) {
            window.setStatusBarColor(act.getResources().getColor(statusRes));
        }

        if (navBar) {
            window.setNavigationBarColor(act.getResources().getColor(navRes));
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void onCreateKitkat(Activity act, boolean statusBar, int statusRes, boolean navBar, int navRes) {
        Window window = act.getWindow();
        int flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (statusBar) {
            window.addFlags(flags);
        } else {
            window.clearFlags(flags);
        }
        flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
        if (navBar) {
            window.addFlags(flags);
        } else {
            window.clearFlags(flags);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(act);
        tintManager.setStatusBarTintEnabled(statusBar);
        tintManager.setStatusBarTintResource(statusRes);
        tintManager.setNavigationBarTintEnabled(navBar);
        tintManager.setNavigationBarTintResource(navRes);
    }
}
