package com.mricefox.archmage.runtime;

import android.net.Uri;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2018/1/22
 */

public interface TargetFindCallback<T> {
    void found(T t);

    void notFound(Uri uri);
}
