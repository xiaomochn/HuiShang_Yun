package com.huishangyun.Office.Markman;

/**
 * Created by Pan on 2015/8/11.
 */
public class MarkmanAdd {
    private String Action;//[Insert:新增;Update:编辑]
    private Integer ID;
    private String Guest_Company;
    private String Guest_Department;
    private String Guest_Name;
    private String Guest_Role;
    private Integer Guest_ID;
    private String Host_Company;
    private String Host_Department;
    private String Host_Name;
    private String Host_Mobile;
    private Integer Host_CompanyID;
    private String Host_Role;
    private Integer Host_ID;
    private Integer Num;
    private String ReserveTime;
    private String Type;//选填 预约拜访,邀请来访
    private String Note;
    private String CancelMsg;
    private Integer Company_ID;
    private Integer Status;//0:删除;1:正常;2:已确认;3:已验证;4:已谢绝

    public String getHost_Mobile() {
        return Host_Mobile;
    }

    public void setHost_Mobile(String host_Mobile) {
        Host_Mobile = host_Mobile;
    }

    public String getAction() {
        return Action;
    }

    public void setAction(String action) {
        Action = action;
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

    public String getGuest_Department() {
        return Guest_Department;
    }

    public void setGuest_Department(String guest_Department) {
        Guest_Department = guest_Department;
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

    public Integer getGuest_ID() {
        return Guest_ID;
    }

    public void setGuest_ID(Integer guest_ID) {
        Guest_ID = guest_ID;
    }

    public String getHost_Company() {
        return Host_Company;
    }

    public void setHost_Company(String host_Company) {
        Host_Company = host_Company;
    }

    public String getHost_Department() {
        return Host_Department;
    }

    public void setHost_Department(String host_Department) {
        Host_Department = host_Department;
    }

    public String getHost_Name() {
        return Host_Name;
    }

    public void setHost_Name(String host_Name) {
        Host_Name = host_Name;
    }

    public Integer getHost_CompanyID() {
        return Host_CompanyID;
    }

    public void setHost_CompanyID(Integer host_CompanyID) {
        Host_CompanyID = host_CompanyID;
    }

    public String getHost_Role() {
        return Host_Role;
    }

    public void setHost_Role(String host_Role) {
        Host_Role = host_Role;
    }

    public Integer getHost_ID() {
        return Host_ID;
    }

    public void setHost_ID(Integer host_ID) {
        Host_ID = host_ID;
    }

    public Integer getNum() {
        return Num;
    }

    public void setNum(Integer num) {
        Num = num;
    }

    public String getReserveTime() {
        return ReserveTime;
    }

    public void setReserveTime(String reserveTime) {
        ReserveTime = reserveTime;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    public String getCancelMsg() {
        return CancelMsg;
    }

    public void setCancelMsg(String cancelMsg) {
        CancelMsg = cancelMsg;
    }

    public Integer getCompany_ID() {
        return Company_ID;
    }

    public void setCompany_ID(Integer company_ID) {
        Company_ID = company_ID;
    }

    public Integer getStatus() {
        return Status;
    }

    public void setStatus(Integer status) {
        Status = status;
    }
}
