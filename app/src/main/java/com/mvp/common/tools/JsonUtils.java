package com.mvp.common.tools;

import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Json相关工具类
 */

public class JsonUtils {
    /**
     * 将Json字符串中第一层的数据提取出加入Bundle对象数据，键值一一对应,返回bundle，非空
     */
    public static Bundle getBundleFromJsonString(Bundle bundle, String jsonString) {
        if (bundle == null)
            bundle = new Bundle();
        try {
            JSONObject jo = new JSONObject(jsonString);
            bundle=getBundleFromJsonObject(bundle,jo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bundle;
    }

    /**
     * 将Json对象中第一层的数据提取出加入Bundle对象数据，键值一一对应,返回bundle，非空
     */
    private static Bundle getBundleFromJsonObject(Bundle bundle, JSONObject jo) {
        if (bundle == null)
            bundle = new Bundle();
        try {
            while (jo.keys().hasNext()) {
                String key = jo.keys().next();
                if (!TextUtils.isEmpty(key)&&jo.has(key)) {
                    Object value = jo.get(key);
                    if (value != null) {
                        if (value instanceof String) {
                            bundle.putString(key, (String) value);
                        } else if (value instanceof Integer) {
                            bundle.putInt(key, (Integer) value);
                        } else if (value instanceof Boolean) {
                            bundle.putBoolean(key, (Boolean) value);
                        } else if (value instanceof Double) {
                            bundle.putDouble(key, (Double) value);
                        } else if (value instanceof Long) {
                            bundle.putLong(key, (Long) value);
                        } else if (value instanceof Float) {
                            bundle.putFloat(key, (Float) value);
                        } else if (value instanceof Bundle) {
                            bundle.putBundle(key, (Bundle) value);
                        } else if (value instanceof Byte) {
                            bundle.putByte(key, (Byte) value);
                        } else if (value instanceof Character) {
                            bundle.putChar(key, (Character) value);
                        } else if (value instanceof CharSequence) {
                            bundle.putCharSequence(key, (CharSequence) value);
                        } else if (value instanceof Short) {
                            bundle.putShort(key, (Short) value);
                        } else if (value instanceof byte[]) {
                            bundle.putByteArray(key, (byte[]) value);
                        } else if (value instanceof int[]) {
                            bundle.putIntArray(key, (int[]) value);
                        } else if (value instanceof String[]) {
                            bundle.putStringArray(key, (String[]) value);
                        } else if (value instanceof double[]) {
                            bundle.putDoubleArray(key, (double[]) value);
                        } else if (value instanceof long[]) {
                            bundle.putLongArray(key, (long[]) value);
                        } else if (value instanceof float[]) {
                            bundle.putFloatArray(key, (float[]) value);
                        } else if (value instanceof char[]) {
                            bundle.putCharArray(key, (char[]) value);
                        } else if (value instanceof CharSequence[]) {
                            bundle.putCharSequenceArray(key, (CharSequence[]) value);
                        } else if (value instanceof short[]) {
                            bundle.putShortArray(key, (short[]) value);
                        } else if (value instanceof boolean[]) {
                            bundle.putBooleanArray(key, (boolean[]) value);
                        } else if (value instanceof ArrayList) {
                            bundle.putSerializable(key, (ArrayList) value);
                        } else if (value instanceof Parcelable[]) {
                            bundle.putParcelableArray(key, (Parcelable[]) value);
                        } else if (value instanceof Parcelable) {
                            bundle.putParcelable(key, (Parcelable) value);
                        } else if (value instanceof Serializable) {
                            bundle.putSerializable(key, (Serializable) value);
                        }
                    }
                    jo.remove(key);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bundle;
    }

    /**
     * 将bundle数据提取成Json对象数据
     *
     * @param bundle
     * @return
     */
    public static JSONObject getJsonObjectFromBundle(Bundle bundle) {
        if (bundle == null || bundle.size() == 0)
            return null;
        JSONObject jo = new JSONObject();
        for (String key : bundle.keySet()) {
            try {
                jo.put(key, bundle.get(key));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jo;
    }

    /**
     * 将bundle数据提取成Json字符串数据
     *
     * @param bundle
     * @return
     */
    public static String getJsonStringFromBundle(Bundle bundle) {
        JSONObject jo = getJsonObjectFromBundle(bundle);
        return jo != null ? jo.toString() : null;
    }
}
