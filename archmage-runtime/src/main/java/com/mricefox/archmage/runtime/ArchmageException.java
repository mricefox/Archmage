package com.mricefox.archmage.runtime;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2017/12/26
 */

/*package*/ class ArchmageException extends RuntimeException {

    ArchmageException() {
        super();
    }

    ArchmageException(String message) {
        super(message);
    }

    ArchmageException(String message, Throwable cause) {
        super(message, cause);
    }

    ArchmageException(Throwable cause) {
        super(cause);
    }
}
