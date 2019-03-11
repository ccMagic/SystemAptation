package com.github.ccmagic.systemaptation.xiaomi;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.github.ccmagic.systemaptation.BuildConfig;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 * https://dev.mi.com/console/doc/detail?pId=1159
 * 问题
 * 一、在 Android 6.0 以前，Android 没有方法可以实现“状态栏黑色字符”效果，因此 MIUI 自己做了一个接口。
 * 二、在 Android 6.0 及以上版本，Android 提供了标准的方法实现“状态栏黑色字符”效果，
 * 但这个方法和 MIUI 的方法产生了冲突，
 * 以致于当开发者使用 Android 标准方法时，没有出现预期的效果，
 * 这给很多开发者都造成了困扰，尤其是海外开发者。
 * <p>
 * <p>
 * 解决方法
 * 基于以上背景，我们决定兼容 Android 的方法，舍弃 MIUI 的自己的实现方法。
 * 从今天的 7.7.13 开发版生效，之后随 MIUI 9 外发。
 * 非常抱歉给各位开发者带来麻烦，但长远来看，兼容 Android 的标准，对 MIUI 和开发者都更为有利。
 */
class StatusBarColorUtils {

    private static final String TAG = "StatusBarColorUtils";

    /**
     * 设置MIUI操作系统的状态栏字体颜色
     *
     * @param dark true黑色字体，false白色字体
     * @return true设置成功，false设置失败
     */
    public static boolean setDarkStatusBar(Activity activity, boolean dark) {
        boolean success;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M && versionUP7713()) {
            /*
            使用 View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR ，来设置“状态栏黑色字符”效果
            同时要设置 WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS，
            并且不设置 WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            */
            Window window = activity.getWindow();
            View decorView = window.getDecorView();
            int vis = decorView.getSystemUiVisibility();
            if (dark) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            if (decorView.getSystemUiVisibility() != vis) {
                decorView.setSystemUiVisibility(vis);
            }
            success = true;
        } else {
            Class<? extends Window> clazz = activity.getWindow().getClass();
            try {
                int darkModeFlag = 0;
                Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                extraFlagField.invoke(activity.getWindow(), dark ? darkModeFlag : 0, darkModeFlag);
                success = true;
            } catch (Exception e) {
                Log.e(TAG, "setDarkStatusBar: ", e);
                success = false;
            }
        }
        return success;
    }


    /**
     * 当前MIUI系统版本是否大于7.7.13
     */
    private static boolean versionUP7713() {
        String version = MIUI.getMIUISystemVersion();
        Log.d(TAG, "versionUP7713 version: " + version);
        String[] versionStrs = version.split("\\.");
        Log.d(TAG, "versionUP7713 BuildConfig.DEBUG: " + BuildConfig.DEBUG);

        if (BuildConfig.DEBUG) {
            for (String s : versionStrs) {
                Log.d(TAG, "versionUP7713 s: " + s);
            }
        }
        if (versionStrs.length >= 1) {
            int firstCode = Integer.valueOf(versionStrs[0]);
            if (firstCode >= 7) {
                if (firstCode > 7) {
                    return true;
                } else {
                    //==7
                    if (versionStrs.length >= 2) {
                        int secondCode = Integer.valueOf(versionStrs[1]);
                        if (secondCode >= 7) {
                            if (secondCode > 7) {
                                return true;
                            } else {
                                //=7
                                if (versionStrs.length >= 3) {
                                    int thirdCode = Integer.valueOf(versionStrs[2]);
                                    return thirdCode >= 13;
                                } else {
                                    return false;
                                }
                            }
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            } else {
                return false;
            }
        } else {
            Log.d(TAG, "versionUP7713: 11111");
            return false;
        }
    }
}
