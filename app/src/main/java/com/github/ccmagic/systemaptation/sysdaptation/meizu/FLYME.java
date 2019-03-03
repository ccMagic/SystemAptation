package com.github.ccmagic.systemaptation.sysdaptation.meizu;

import android.app.Activity;
import android.os.Build;
import android.text.TextUtils;

/**
 * 2019/3/3
 *
 * @author kxmc
 * <p>
 * 魅族flyme系统
 */
public class FLYME {

    /**
     * @return true是魅族系统，false不是魅族系统
     * */
    public static boolean isFlymeDevice() {
        return !TextUtils.isEmpty(Build.DISPLAY) && "FLYME".equals(Build.DISPLAY.toUpperCase());
    }

    public static boolean setStatusBarColorUtils(Activity activity, boolean dark){
      return  StatusBarColorUtils.setStatusBarDarkIcon(activity,dark);
    }
}
