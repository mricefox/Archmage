package com.mricefox.archmage.sample.share.export;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2018/4/23
 */

public interface ShareFragmentContract {
    void shareDone(String shareResult, ShareCallback callback);

    interface ShareCallback {
        void callback(String data);
    }
}
