package com.github.ccmagic.systemaptation.qiku;

import android.os.Build;

/**
 * @author kxmc
 */
public class QIKU {
    public static boolean is360Device() {
        String name = Build.MANUFACTURER.toUpperCase();
        return "QIKU".equals(name) || "360".equals(name);
    }
}
