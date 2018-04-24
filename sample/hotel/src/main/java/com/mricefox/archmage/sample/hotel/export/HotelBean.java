package com.mricefox.archmage.sample.hotel.export;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2018/3/27
 */

public class HotelBean {
    private long id;
    private String name;
    private int totalRoomNum;

    public HotelBean(long id, String name, int totalRoomNum) {
        this.id = id;
        this.name = name;
        this.totalRoomNum = totalRoomNum;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalRoomNum() {
        return totalRoomNum;
    }

    public void setTotalRoomNum(int totalRoomNum) {
        this.totalRoomNum = totalRoomNum;
    }

    @Override
    public String toString() {
        return "HotelBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", totalRoomNum=" + totalRoomNum +
                '}';
    }
}