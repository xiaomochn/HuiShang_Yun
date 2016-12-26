package com.huishangyun.model;

/**
 * Created by pan on 2015/8/4.
 */
public class CallList {
    private Integer ID;
    private String Guest_Company;
    private String Guest_Name;
    private String Guest_Role;
    private String Host_Company;
    private String Host_Name;
    private String Host_Role;
    private String ReserveTime;

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getGuest_Company() {
        return Guest_Company;
    }

    public void setGuest_Company(String guest_Company) {
        Guest_Company = guest_Company;
    }

    public String getGuest_Name() {
        return Guest_Name;
    }

    public void setGuest_Name(String guest_Name) {
        Guest_Name = guest_Name;
    }

    public String getGuest_Role() {
        return Guest_Role;
    }

    public void setGuest_Role(String guest_Role) {
        Guest_Role = guest_Role;
    }

    public String getHost_Company() {
        return Host_Company;
    }

    public void setHost_Company(String host_Company) {
        Host_Company = host_Company;
    }

    public String getHost_Name() {
        return Host_Name;
    }

    public void setHost_Name(String host_Name) {
        Host_Name = host_Name;
    }

    public String getHost_Role() {
        return Host_Role;
    }

    public void setHost_Role(String host_Role) {
        Host_Role = host_Role;
    }

    public String getReserveTime() {
        return ReserveTime;
    }

    public void setReserveTime(String reserveTime) {
        ReserveTime = reserveTime;
    }
}
