package com.github.ccmagic.systemaptation.xiaomi;

import android.app.Activity;
import android.os.Build;

import com.github.ccmagic.systemaptation.SystemPropertyUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.WorkerThread;

/**
 * 2019/3/2
 *
 * @author kxmc
 * <p>
 * 小米MIUI系统
 * <p>
 * 如何检测小米设备：
 * <p>
 * 请使用android.os.Build对象，查询MANUFACTURER和MODEL的值，MANUFACTURER值为Xiaomi即为小米设备
 * <p>
 * 如何检测MIUI V5：
 * <p>
 * 查询property: ro.miui.ui.version.name ，值是"V5" 就是MIUI V5系统；值是"V6"就是MIUI 6系统。
 */
public class MIUI {
    private static final String TAG = "MIUI";


    private static final String KEY_VERSION_MIUI = "ro.miui.ui.version.name";


    private static final String XIAOMI = "Xiaomi";
    /**
     * "V5" 就是MIUI V5系统
     */
    private static final String VERSION_V5 = "V5";
    /**
     * "V6"就是MIUI 6系统。
     */
    private static final String VERSION_V6 = "V6";

    /**
     * 检测是否为小米设备：
     *
     * @return true当前设备是小米设备，false当前设备不是小米设备
     */
    public static boolean isXiaomiDevice() {
        return XIAOMI.equals(Build.MANUFACTURER);
    }

    /**
     * 检测小米设备MIUI系统是否为  MIUI V5系统
     */
    @WorkerThread
    public static boolean checkMIUIV5() {
        return VERSION_V5.equals(SystemPropertyUtil.getSystemProperty(KEY_VERSION_MIUI));
    }

    @WorkerThread
    public static boolean isMiUIV6() {
        return VERSION_V6.equals(SystemPropertyUtil.getSystemProperty(KEY_VERSION_MIUI));
    }

    /**
     * 获取MIUI系统版本号，并且转换成int类型
     */
    public static String getMIUISystemVersion() {
        String systemVersion = Build.VERSION.INCREMENTAL;
        //MIUI版本号一般格式 V10.2.1.0.OEDCNXM
        String regEx = "[^0123456789.]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(systemVersion);
        systemVersion = m.replaceAll(" ").trim();
        return systemVersion;
    }

    /**
     * 修改状态栏字体颜色
     */
    public static boolean setStatusbarColorUtils(Activity activity, boolean dark) {
        return StatusBarColorUtils.setDarkStatusBar(activity, dark);
    }
}
