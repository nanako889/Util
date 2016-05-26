package com.qbw.util;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Bond on 2016/5/4.
 */
public class MatcherUtil {

    /**
     * @param phoneNumber（11位数字）
     * @return 电话号码是否合法
     */
    public static boolean matchPhoneNumber(String phoneNumber) {
        boolean b;
        if (TextUtils.isEmpty(phoneNumber) || !phoneNumber.startsWith("1") || phoneNumber.length() != 11) {
            b = false;
        } else {
            String expression = "((^(12|13|14|15|16|17|18|19)[0-9]{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9] {1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-? \\d{7,8}-(\\d{1,4})$))";
            b = Pattern.compile(expression).matcher(phoneNumber).matches();
        }
        return b;
    }

    /**
     * @param vefifyCode（数字）
     * @return 验证码是否合法
     */
    public static boolean matchVerifyCode(String vefifyCode, int length) {
        boolean b;
        if (TextUtils.isEmpty(vefifyCode)) {
            b = false;
        } else {
            b = Pattern.compile(String.format("[\\d]{%d}", length)).matcher(vefifyCode).matches();
        }
        return b;
    }

    /**
     * @param password(字母数字下划线)
     * @return 密码是否合法
     */
    public static boolean matchPassword(String password, int minLength, int maxLength) {
        boolean b;
        if (TextUtils.isEmpty(password)) {
            b = false;
        } else {
            b = Pattern.compile(String.format("[0-9a-zA-Z_]{%d,%d}", minLength, maxLength)).matcher(password).matches();
        }
        return b;
    }

    /**
     * @param email
     * @return 邮箱是否合法
     */
    public static boolean matchEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        }
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }
}
