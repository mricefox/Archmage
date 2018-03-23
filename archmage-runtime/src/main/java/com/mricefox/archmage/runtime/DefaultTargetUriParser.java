package com.mricefox.archmage.runtime;

import android.net.Uri;

import java.util.Iterator;

import static com.mricefox.archmage.runtime.Utils.checkEmpty;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2018/1/23
 */
public final class DefaultTargetUriParser implements TargetUriParser {
    public static final String SCHEME = "native";
    public static final String HOST = "transfer.target";

    public static Uri createUri(String group, String path) {
        checkEmpty(group, "Group");
        checkEmpty(path, "Path");

        return new Uri.Builder().scheme(SCHEME).authority(HOST).appendPath(group).appendPath(path).build();
    }

    @Override
    public String getGroup(Uri uri) {
        return uri.getPathSegments().get(0);
    }

    @Override
    public String getPath(Uri uri) {
        return uri.getPathSegments().get(1);
    }

    @Override
    public boolean checkUri(Uri uri) {
        //native://transfer.target/hotel/detail
        if (uri == null) {
            return false;
        }

        if (!SCHEME.equals(uri.getScheme())) {
            return false;
        }

        if (!HOST.equals(uri.getHost())) {
            return false;
        }

        for (Iterator<String> iterator = uri.getPathSegments().iterator();
             iterator.hasNext(); ) {
            String segment = iterator.next();
            if (segment == null || segment.trim().length() == 0) {
                return false;
            }
        }

        if (uri.getPathSegments().size() != 2) {
            return false;
        }

        return true;
    }
}
