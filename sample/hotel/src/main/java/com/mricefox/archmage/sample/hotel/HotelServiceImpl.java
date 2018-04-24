package com.mricefox.archmage.sample.hotel;

import com.mricefox.archmage.annotation.ServiceImpl;
import com.mricefox.archmage.sample.hotel.export.HotelBean;
import com.mricefox.archmage.sample.hotel.export.HotelService;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2018/4/23
 */

@ServiceImpl
public class HotelServiceImpl implements HotelService {
    private Map<Long, HotelBean> hotels = new HashMap<Long, HotelBean>() {
        {
            put(1L, new HotelBean(1L, "HOTEL_A", 200));
            put(2L, new HotelBean(2L, "HOTEL_B", 50));
            put(3L, new HotelBean(3L, "HOTEL_C", 100));
            put(4L, new HotelBean(4L, "HOTEL_D", 80));
            put(5L, new HotelBean(5L, "HOTEL_E", 300));
        }
    };

    @Override
    public HotelBean getHotelDetail(long id) {
        return hotels.get(id);
    }
}
