package com.mvp.common.tools;

import android.util.Base64;

public class Base64Utils {

    private Base64Utils() {
    }

    /**
     * 将字节数组编码为字符串
     *
     * @param data
     */
    public static String encode(byte[] data) {
        return new String(Base64.encode(data, Base64.DEFAULT));
    }

    /**\
     * 将base64字符串解码为字节数组
     *
     * @param str
     */
    public static byte[] decode(String str) {
        return Base64.decode(str, Base64.DEFAULT);
    }
}