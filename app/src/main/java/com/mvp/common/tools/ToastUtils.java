package com.mvp.common.tools;

import android.content.Context;

/**
 * Created by Allon on 2016/1/4.
 */
public class ToastUtils {

    //正常居中显示
    public static void toastLong(Context context, String message) {
        if (context == null)
            return;
        T.showLong(context, message);
    }
    //x,y坐标显示
    public static void toastLongStartX_Y(Context context, String message,int x,int y) {
        if (context == null)
            return;
        T.showLongStartX_Y(context, message,x,y);
    }
    //顶部居中显示
    public static void toastLongTop(Context context, String message) {
        if (context == null)
            return;
        T.showLongTop(context, message);
    }
    //底部居中显示
    public static void toastLongBottom(Context context, String message) {
        if (context == null)
            return;
        T.showLongBottom(context, message);
    }

    //正常居中显示
    public static void toastShort(Context context, String message) {
        if (context == null)
            return;
        T.showShort(context, message);
    }
    //x,y坐标显示
    public static void toastShortStartX_Y(Context context, String message,int x,int y) {
        if (context == null)
            return;
        T.showShortStartX_Y(context, message,x,y);
    }
    //顶部居中显示
    public static void toastShortTop(Context context, String message) {
        if (context == null)
            return;
        T.showShortTop(context, message);
    }
    //底部居中显示
    public static void toastShortBottom(Context context, String message) {
        if (context == null)
            return;
        T.showShortBottom(context, message);
    }

    //正常居中显示
    public static void toastLong(Context context, int id) {
        if (context == null)
            return;
        toastLong(context, context.getString(id));
    }
    //x,y坐标显示
    public static void toastLongStartX_Y(Context context, int id,int x,int y) {
        if (context == null)
            return;
        toastLongStartX_Y(context, context.getString(id),x,y);
    }
    //顶部居中显示
    public static void toastLongTop(Context context, int id) {
        if (context == null)
            return;
        toastLongTop(context, context.getString(id));
    }
    //底部居中显示
    public static void toastLongBottom(Context context, int id) {
        if (context == null)
            return;
        toastLongBottom(context, context.getString(id));
    }

    //正常居中显示
    public static void toastShort(Context context, int id) {
        if (context == null)
            return;
        T.showShort(context, context.getString(id));
    }
    //x,y坐标显示
    public static void toastShortStartX_Y(Context context, int id,int x,int y) {
        if (context == null)
            return;
        T.showShortStartX_Y(context, context.getString(id),x,y);
    }
    //顶部居中显示
    public static void toastShortTop(Context context, int id) {
        if (context == null)
            return;
        T.showShortTop(context, context.getString(id));
    }
    //底部居中显示
    public static void toastShortBottom(Context context, int id) {
        if (context == null)
            return;
        T.showShortBottom(context, context.getString(id));
    }
}
