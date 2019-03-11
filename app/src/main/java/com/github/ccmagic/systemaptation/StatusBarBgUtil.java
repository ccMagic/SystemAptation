package com.github.ccmagic.systemaptation;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import androidx.annotation.DrawableRes;

/**
 * @author kxmc
 * 修改状态栏背景颜色
 */
public class StatusBarBgUtil {

    public static boolean initStatusBar(Activity activity, @DrawableRes int bgResource) {
        try {
            int identifier = activity.getResources().getIdentifier("statusBarBackground", "id", "android");
            View statusBarView = activity.getWindow().findViewById(identifier);
            if (statusBarView != null) {
                statusBarView.setBackgroundResource(bgResource);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean initStatusBar(Activity activity) {
        try {
            int identifier = activity.getResources().getIdentifier("statusBarBackground", "id", "android");
            View statusBarView = activity.getWindow().findViewById(identifier);
            if (statusBarView != null) {
                statusBarView.setBackgroundColor(Color.parseColor("#33ffffff"));
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
