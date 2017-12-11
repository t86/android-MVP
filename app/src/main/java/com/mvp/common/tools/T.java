package com.mvp.common.tools;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.mvp.common.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Toast统一管理类
 */
public class T {

    private static WindowManager manager;
    private static View toastView;
    private static CharSequence currentShowMsg;//当前展示消息
    private static Context mContext;//当前上下文

    private T() {
        /** cannot be instantiated**/
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showShort(Context context, CharSequence message) {
        show(context, message, 2000);
    }

    public static void showShortStartX_Y(Context context, CharSequence message, int x, int y) {
        showStartX_Y(context, message, 2000, x, y);
    }

    public static void showShortTop(Context context, CharSequence message) {
        showTop(context, message, 2000);
    }

    public static void showShortBottom(Context context, CharSequence message) {
        showBottom(context, message, 2000);
    }

    /**
     * 长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showLong(Context context, CharSequence message) {
        show(context, message, 3500);
    }

    public static void showLongTop(Context context, CharSequence message) {
        showTop(context, message, 3500);
    }

    public static void showLongStartX_Y(Context context, CharSequence message, int x, int y) {
        showStartX_Y(context, message, 3500, x, y);
    }

    public static void showLongBottom(Context context, CharSequence message) {
        showBottom(context, message, 3500);
    }


    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public static void show(Context context, CharSequence message, int duration) {
        showToastSwitchPerms(context, message, duration, getWindowMangerParam(context));
    }

    /**
     * x,y坐标显示
     *
     * @param context
     * @param message
     * @param duration
     * @param x
     * @param y
     */
    public static void showStartX_Y(Context context, CharSequence message, int duration, int x, int y) {
        showToastSwitchPerms(context, message, duration, getParamX_Y(context, x, y));
    }

    /**
     * 底部居中显示
     *
     * @param context
     * @param message
     * @param duration
     */
    public static void showBottom(Context context, CharSequence message, int duration) {
        showToastSwitchPerms(context, message, duration, getParamBottom(context));
    }

    /**
     * 顶部居中显示
     *
     * @param context
     * @param message
     * @param duration
     */
    public static void showTop(Context context, CharSequence message, int duration) {
        showToastSwitchPerms(context, message, duration, getParamTop(context));
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public static void showBottom(Context context, int message, int duration) {
        try {
            showBottom(context, context.getResources().getText(message), duration);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showStartX_Y(Context context, int message, int duration, int x, int y) {
        try {
            showStartX_Y(context, context.getResources().getText(message), duration, x, y);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showTop(Context context, int message, int duration) {
        try {
            showTop(context, context.getResources().getText(message), duration);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据条件来选择进行逻辑，此类底层方法
     *
     * @param context
     * @param message
     * @param duration
     * @param mParams
     */
    private static void showToastSwitchPerms(Context context, CharSequence message, int duration, WindowManager.LayoutParams mParams) {
        if (context != null && context instanceof Activity && !isNotificationEnabled(context)) {
            if (!checkAlertPerms(context))
                showCheckToastPermsHintDialog(context);
            else
                showHintView(context, message, duration, mParams);
        } else {
            Toast.makeText(context, message, duration >= 3000 ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 判断 悬浮窗口权限是否打开
     * 适用于魅族小米华为等大部分机型
     *
     * @param context
     * @return true 允许  false禁止
     */
    @SuppressWarnings("unchecked")
    public static boolean checkAlertPerms(Context context) {
        if (context == null)
            return false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                Object localObject = context
                        .getSystemService(Context.APP_OPS_SERVICE);
                Class localClass = localObject.getClass();
                Class[] arrayOfClass = new Class[3];
                arrayOfClass[0] = Integer.TYPE;
                arrayOfClass[1] = Integer.TYPE;
                arrayOfClass[2] = String.class;
                Method localMethod = localClass.getMethod("checkOp",
                        arrayOfClass);
                Object[] arrayOfObject = new Object[3];
                arrayOfObject[0] = 24;
                arrayOfObject[1] = Binder.getCallingUid();
                arrayOfObject[2] = context.getPackageName();
                int j = (int) localMethod.invoke(localObject,
                        arrayOfObject);
                return j == AppOpsManager.MODE_ALLOWED;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 辅助toast弹框，但系统限制了toast和悬浮窗权限时，需要调用此dialog来提示打开通知栏或消息栏权限
     *
     * @param context
     */
    public static void showCheckToastPermsHintDialog(final Context context) {
        if (context == null)
            return;
        if (context instanceof Activity && !((Activity) context).isFinishing()) {
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setMessage("建议在设置中开启本应用通知消息或者悬浮窗权限，否则相关提示无法弹出")
                    .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                            intent.setData(uri);
                            dialog.dismiss();
                            context.startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            dialog.show();
        }
    }

    @SuppressWarnings("unchecked")
    public static boolean isNotificationEnabled(Context context) {
        /**
         * 判断是否开启通知栏权限，返回true判断开启了
         */
        if (context == null)
            return false;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {//SDK19分隔
            return true;
        } else {
            AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            ApplicationInfo appInfo = context.getApplicationInfo();
            String pkg = context.getApplicationContext().getPackageName();
            int uid = appInfo.uid;
            Class appOpsClass = null; /* Context.APP_OPS_MANAGER */
            try {
                appOpsClass = Class.forName(AppOpsManager.class.getName());
                Method checkOpNoThrowMethod = appOpsClass.getMethod("checkOpNoThrow", Integer.TYPE, Integer.TYPE, String.class);
                Field opPostNotificationValue = appOpsClass.getDeclaredField("OP_POST_NOTIFICATION");
                int value = (int) opPostNotificationValue.get(Integer.class);
                return ((int) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    private static WindowManager.LayoutParams getWindowMangerParam(Context context) {
        //标配WindowManager.LayoutParams
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        try {
            params.packageName = context.getApplicationContext().getPackageName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            params.type = WindowManager.LayoutParams.TYPE_TOAST;
            params.gravity = Gravity.CENTER;
            params.format = PixelFormat.TRANSLUCENT;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    /**
     * 给param设置参照位置为Gravity.start位置，然后设置xy坐标
     *
     * @return
     */
    private static WindowManager.LayoutParams getParamX_Y(Context context, int x, int y) {
        WindowManager.LayoutParams params = getWindowMangerParam(context);
        try {
            params.gravity = Gravity.START;
            params.x = x;
            params.y = y;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    /**
     * 给param设置参照位置为Gravity.top位置，然后横向居中
     *
     * @return
     */
    private static WindowManager.LayoutParams getParamTop(Context context) {
        WindowManager.LayoutParams params = getWindowMangerParam(context);
        try {
            params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    /**
     * 给param设置参照位置为Gravity.bottom位置,然后横向居中
     *
     * @return
     */
    private static WindowManager.LayoutParams getParamBottom(Context context) {
        WindowManager.LayoutParams params = getWindowMangerParam(context);
        try {
            params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    //自定义展示的View,代替toast,因为用户可以关闭相关权限就会toast不展示
    //参数：上下文，展示内容，展示时间(ms)
    public static void showHintView(Context context, CharSequence hintMsg, long duration, WindowManager.LayoutParams params) {
        try {
            if (context == null || params == null || TextUtils.isEmpty(hintMsg) || hintMsg.equals(currentShowMsg) || duration <= 0 || ((Activity) context).isFinishing())
                return;
            myHandler.removeCallbacks(myRunnable);
            if (manager == null || context != mContext) {
                mContext = null;
                mContext = context;
                if (manager != null && toastView != null) {
                    manager.removeView(toastView);
                    toastView = null;
                    currentShowMsg = null;
                }
                manager = null;
                manager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
                toastView = null;
                toastView = View.inflate(context, R.layout.toast_loan_view, null);
                ((TextView) toastView.findViewById(R.id.toast_text)).setText(hintMsg);
                manager.addView(toastView, params);
            } else {
                if (toastView == null) {
                    toastView = View.inflate(context, R.layout.toast_loan_view, null);
                    ((TextView) toastView.findViewById(R.id.toast_text)).setText(hintMsg);
                    manager.addView(toastView, params);
                } else {
                    ((TextView) toastView.findViewById(R.id.toast_text)).setText(hintMsg);
                    manager.updateViewLayout(toastView, params);
                }
            }
            currentShowMsg = hintMsg;
            myHandler.postDelayed(myRunnable, duration);
        } catch (Exception e) {
            e.printStackTrace();
            manager = null;
            toastView = null;
            currentShowMsg = null;
            try {
                Toast.makeText(context, hintMsg, duration > 2 ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    private static Handler myHandler = new Handler();
    private static Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (manager != null && toastView != null && mContext != null && !((Activity) mContext).isFinishing()) {
                    manager.removeView(toastView);
                    toastView = null;
                    currentShowMsg = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 销毁用于提示的toastView,主要用于依赖activity销毁时toast依然存在的情况，原有handler任务不能执行了
     */
    public static void destroyToastView() {
        try {
            if (manager != null && toastView != null)
                manager.removeView(toastView);
            toastView = null;
            currentShowMsg = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}