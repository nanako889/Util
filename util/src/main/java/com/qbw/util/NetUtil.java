package com.qbw.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * @author qbw
 * @createtime 2016/04/29 11:48
 */


public class NetUtil {
    /** Unknown network class. {@hide} */
    public static final int NETWORK_CLASS_UNKNOWN = 0;
    /** Class of broadly defined "2G" networks. {@hide} */
    public static final int NETWORK_CLASS_2_G = 1;
    /** Class of broadly defined "3G" networks. {@hide} */
    public static final int NETWORK_CLASS_3_G = 2;
    /** Class of broadly defined "4G" networks. {@hide} */
    public static final int NETWORK_CLASS_4_G = 3;

    /**
     * @param context
     * @return 网络是否可用
     */
    public static boolean isNetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return null != networkInfo && networkInfo.isAvailable();
    }


    public static boolean isWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    public static boolean is2G(Context context) {
        return NETWORK_CLASS_2_G == getNetworkClass(context);
    }

    public static boolean is3G(Context context) {
        return NETWORK_CLASS_3_G == getNetworkClass(context);
    }

    public static boolean is4G(Context context) {
        return NETWORK_CLASS_4_G == getNetworkClass(context);
    }

    private static int getNetworkClass(Context context) {
        int networkType = -1;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null) {
            networkType = activeNetInfo.getSubtype();
        }
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return NETWORK_CLASS_2_G;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return NETWORK_CLASS_3_G;
            case TelephonyManager.NETWORK_TYPE_LTE:
                return NETWORK_CLASS_4_G;
            default:
                return NETWORK_CLASS_UNKNOWN;
        }
    }
}
