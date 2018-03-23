package com.mricefox.archmage.runtime;

import android.net.Uri;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2018/1/18
 */

public interface TargetUriParser {
    String getGroup(Uri uri);

    String getPath(Uri uri);

    boolean checkUri(Uri uri);
}
