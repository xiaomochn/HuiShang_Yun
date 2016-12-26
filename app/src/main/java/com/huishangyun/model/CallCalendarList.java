package com.huishangyun.model;

/**
 * Created by pan on 2015/8/4.
 */
public class CallCalendarList {
    private String BelongDate;
    private Integer OutCount;
    private Integer InCount;
    private Boolean Status;

    public String getBelongDate() {
        return BelongDate;
    }

    public void setBelongDate(String belongDate) {
        BelongDate = belongDate;
    }

    public Integer getOutCount() {
        return OutCount;
    }

    public void setOutCount(Integer outCount) {
        OutCount = outCount;
    }

    public Integer getInCount() {
        return InCount;
    }

    public void setInCount(Integer inCount) {
        InCount = inCount;
    }

    public Boolean getStatus() {
        return Status;
    }

    public void setStatus(Boolean status) {
        Status = status;
    }
}
