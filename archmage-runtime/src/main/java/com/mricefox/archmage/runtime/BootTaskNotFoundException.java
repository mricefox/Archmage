package com.mricefox.archmage.runtime;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2018/1/19
 */

public class BootTaskNotFoundException extends ArchmageException {
    public BootTaskNotFoundException() {
        super();
    }

    public BootTaskNotFoundException(String s) {
        super(s);
    }

    public BootTaskNotFoundException(String s, Throwable ex) {
        super(s, ex);
    }
}
