package com.mricefox.archmage.sample.share;

import com.mricefox.archmage.annotation.ServiceImpl;
import com.mricefox.archmage.sample.share.export.ShareService;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2018/4/23
 */
@ServiceImpl
public class ShareServiceImpl implements ShareService {
    @Override
    public String[] getSharePlatforms() {
        return new String[]{"Weixin", "Sina", "Twitter", "Facebook"};
    }
}
