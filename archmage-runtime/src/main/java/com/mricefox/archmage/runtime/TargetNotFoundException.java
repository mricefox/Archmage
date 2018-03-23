package com.mricefox.archmage.runtime;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2017/12/25
 */

public class TargetNotFoundException extends ArchmageException {
    public TargetNotFoundException() {
        super();
    }

    public TargetNotFoundException(String s) {
        super(s);
    }

    public TargetNotFoundException(String s, Throwable ex) {
        super(s, ex);
    }

    public TargetNotFoundException(Throwable cause) {
        super(cause);
    }
}
