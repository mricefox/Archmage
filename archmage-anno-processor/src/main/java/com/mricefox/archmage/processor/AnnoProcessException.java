package com.mricefox.archmage.processor;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2018/1/29
 */

public class AnnoProcessException extends RuntimeException {
    public AnnoProcessException() {
    }

    public AnnoProcessException(String s) {
        super(s);
    }

    public AnnoProcessException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public AnnoProcessException(Throwable throwable) {
        super(throwable);
    }

    public AnnoProcessException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
