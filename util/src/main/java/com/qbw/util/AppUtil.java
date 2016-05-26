package com.qbw.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * @author qbw
 * @createtime 2016/04/15 11:33
 */


public class AppUtil {

    /**
     * @param context
     * @return 程序版本号
     */
    public static int getVersionCode(Context context) {
        int vercode = 0;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            vercode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return vercode;
    }

    /**
     * @param context
     * @return 程序版本名
     */
    public static String getVersionName(Context context) {
        String vername = "";
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            vername = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return vername;
    }

    /**
     * @param context
     * @param name
     * @return meta配置
     */
    public static String getMetaData(Context context, String name) {
        String value = "";
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            value = applicationInfo.metaData.getString(name);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return value;
    }
}
