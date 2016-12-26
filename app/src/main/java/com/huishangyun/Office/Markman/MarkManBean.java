package com.huishangyun.Office.Markman;

/**
 * Created by Pan on 2015/8/12.
 */
public class MarkManBean {
    private Integer ID;
    private Integer Guest_ID;
    private String Guest_Company;
    private String Guest_Name;
    private String Guest_Role;
    private String Host_Company;
    private String Host_Name;
    private String Host_Role;
    private String ReserveTime;
    private String Note;
    private Integer Status;
    private String Guest_Mobile;
    private String Host_Mobile;

    public Integer getGuest_ID() {
        return Guest_ID;
    }

    public void setGuest_ID(Integer guest_ID) {
        Guest_ID = guest_ID;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

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

    public Integer getStatus() {
        return Status;
    }

    public void setStatus(Integer status) {
        Status = status;
    }

    public String getGuest_Mobile() {
        return Guest_Mobile;
    }

    public void setGuest_Mobile(String guest_Mobile) {
        Guest_Mobile = guest_Mobile;
    }

    public String getHost_Mobile() {
        return Host_Mobile;
    }

    public void setHost_Mobile(String host_Mobile) {
        Host_Mobile = host_Mobile;
    }
}
