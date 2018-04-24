package com.mricefox.archmage.sample.hotel.export;

import com.mricefox.archmage.runtime.IService;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2018/3/27
 */

public interface HotelService extends IService {
    HotelBean getHotelDetail(long id);
}
