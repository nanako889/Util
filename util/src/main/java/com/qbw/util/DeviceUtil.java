package com.qbw.util;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * @author qbw
 * @createtime 2016/04/15 11:26
 * 获取设备的一些配置信息
 */


public class DeviceUtil {

    /**
     * @param context
     * @return 设备ID
     */
    public static String getIMEI(Context context) {
        String imei = null;
        TelephonyManager tel = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        imei = tel.getDeviceId();
        return null == imei ? "" : imei;
    }

    /**
     * @param context
     * @return 手机IMSI
     */
    public static String getIMSI(Context context) {
        String imsi = null;
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        imsi = telephonyManager.getSubscriberId();
        return null == imsi ? "" : imsi;
    }

    /**
     * @return phone type ,手机型号,如: Moto,HTC
     */
    public static String getUA() {
        return android.os.Build.MODEL;
    }

    /**
     * @return SDK版本号
     */
    public static int getSDK() {
        return android.os.Build.VERSION.SDK_INT;
    }

    /**
     * @return 手机电话号码
     */
    public static String getPhoneNumber(Context context) {
        TelephonyManager tel = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String number = tel.getLine1Number();
        return null == number ? "" : number;
    }
}
