package com.mricefox.archmage.runtime;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2017/12/25
 */

public interface ServiceFindCallback<Service> {
    void found(Service service);

    void notFound(Class<Service> alias);
}
