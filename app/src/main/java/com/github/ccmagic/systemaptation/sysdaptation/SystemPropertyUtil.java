package com.github.ccmagic.systemaptation.sysdaptation;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import androidx.annotation.WorkerThread;

/**
 * 2019/3/2
 *
 * @author kxmc
 */
public class SystemPropertyUtil {
    private static final String TAG = "SystemPropertyUtil";

    /***
     * 当前方法存在线程阻塞，不能在UI线程操作
     * */
    @WorkerThread
    public static String getSystemProperty(String propName) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            Log.e(TAG, "Unable to read sysprop " + propName, ex);
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    Log.e(TAG, "Exception while closing InputStream", e);
                }
            }
        }
        return line;
    }
}
