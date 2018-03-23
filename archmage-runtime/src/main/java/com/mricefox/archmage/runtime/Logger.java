package com.mricefox.archmage.runtime;

import android.util.Log;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2017/12/25
 */

final class Logger {
    static boolean DEBUG;
    private final String tag;

    private Logger(String tag) {
        this.tag = tag;
    }

    private Logger(Class clazz) {
        this.tag = clazz.getSimpleName();
    }

    static Logger getLogger(Class c) {
        return new Logger(c);
    }

    void info(String msg) {
        if (DEBUG) {
            Log.i(this.tag, msg);
        }
    }

    void error(String msg) {
        if (DEBUG) {
            Log.e(this.tag, msg);
        }
    }

    void error(String msg, Throwable tr) {
        if (DEBUG) {
            Log.e(this.tag, msg, tr);
        }
    }

    void warn(String msg) {
        if (DEBUG) {
            Log.w(this.tag, msg);
        }
    }
}
