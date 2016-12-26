package com.huishangyun.Office.Location;

import java.io.Serializable;

/**
 * Created by pan on 2015/6/23.
 */
public class LocationHistory implements Serializable{
    private double Latitude;
    private double Longitude;
    private String Address;
    private String AddTime;

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getAddTime() {
        return AddTime;
    }

    public void setAddTime(String addTime) {
        AddTime = addTime;
    }
}
