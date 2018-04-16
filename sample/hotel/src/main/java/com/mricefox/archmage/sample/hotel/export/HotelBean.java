package com.mricefox.archmage.sample.hotel.export;

import java.util.List;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2018/3/27
 */

public class HotelBean {
    private String name;
    private String location;
    private int availableRoomNum;
    private int totalRoomNum;
    private List<RoomBean> roomBeanList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getAvailableRoomNum() {
        return availableRoomNum;
    }

    public void setAvailableRoomNum(int availableRoomNum) {
        this.availableRoomNum = availableRoomNum;
    }

    public int getTotalRoomNum() {
        return totalRoomNum;
    }

    public void setTotalRoomNum(int totalRoomNum) {
        this.totalRoomNum = totalRoomNum;
    }

    public List<RoomBean> getRoomBeanList() {
        return roomBeanList;
    }

    public void setRoomBeanList(List<RoomBean> roomBeanList) {
        this.roomBeanList = roomBeanList;
    }

    enum ROOM_TYPE {
        SINGLE,
        DOUBLE
    }

    public static class RoomBean {
        private ROOM_TYPE type;
        private int roomId;

        public ROOM_TYPE getType() {
            return type;
        }

        public void setType(ROOM_TYPE type) {
            this.type = type;
        }

        public int getRoomId() {
            return roomId;
        }

        public void setRoomId(int roomId) {
            this.roomId = roomId;
        }
    }
}