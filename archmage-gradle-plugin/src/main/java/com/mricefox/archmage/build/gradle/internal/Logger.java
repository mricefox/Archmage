package com.mricefox.archmage.build.gradle.internal;

import org.gradle.api.logging.Logging;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2017/12/20
 */

public class Logger {
    private final String tag;
    private final org.gradle.api.logging.Logger logger;

    private Logger(Class c) {
        this.tag = c.getSimpleName();
        this.logger = Logging.getLogger(c);
    }

    public static Logger getLogger(Class c) {
        return new Logger(c);
    }

    public void info(String msg) {
        logger.error("[Archmage/INFO" + " " + this.tag + "]" + ":" + msg);
    }

    public void warn(String msg) {
        logger.error("[Archmage/WARN" + " " + this.tag + "]" + ":" + msg);
    }

    public void error(String msg) {
        logger.error("[Archmage/ERROR" + " " + this.tag + "]" + ":" + msg);
    }
}
