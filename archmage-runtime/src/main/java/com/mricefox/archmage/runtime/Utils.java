package com.mricefox.archmage.runtime;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import static android.text.TextUtils.isEmpty;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2017/12/26
 */

/*package*/ class Utils {
    private Utils() {
    }

    static <T> T checkNull(T t, String tag) {
        if (t == null) {
            throw new NullPointerException(tag + " should not be null");
        }
        return t;
    }

    static void checkEmpty(String str, String tag) {
        if (str == null || str.trim().length() == 0) {
            throw new NullPointerException(tag + " should not be empty");
        }
    }

    static boolean isMainProcess(Context context) {
        context = context.getApplicationContext();

        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        String mainProcess = packageInfo.applicationInfo.processName;
        String processName = getCurrentProcessName(context);

        return mainProcess.equals(processName);
    }

    static String getCurrentProcessName(Context context) {
        final int myPid = android.os.Process.myPid();
        String name = getProcessNameByPid(myPid);

        if (isEmpty(name)) {
            name = getProcessNameByPid(context, myPid);
        }
        return name;
    }

    static String getProcessNameByPid(int pid) {
        BufferedReader reader = null;
        try {
            File file = new File("/proc/" + pid + "/" + "cmdline");
            reader = new BufferedReader(new FileReader(file));
            return reader.readLine().trim();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            closeQuitely(reader);
        }
    }

    static String getProcessNameByPid(Context context, int pid) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
        if (runningProcesses == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo process : runningProcesses) {
            if (process.pid == pid) {
                return process.processName;
            }
        }
        return null;
    }

    static void closeQuitely(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException ignore) {
            }
        }
    }
}
