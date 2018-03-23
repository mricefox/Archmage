package com.mricefox.archmage.runtime;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2017/12/25
 */

public class ServiceNotFoundException extends ArchmageException {
    public ServiceNotFoundException() {
        super();
    }

    public ServiceNotFoundException(String s) {
        super(s);
    }

    public ServiceNotFoundException(String s, Throwable ex) {
        super(s, ex);
    }
}
