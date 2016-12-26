package com.huishangyun.Channel.Visit;

/**
 * Created by Administrator on 2016/7/25.
 */
public class Dialog_Visit {
    private String Tag_Name;
    private String Tag_Id;
    private int state=0;//状态 0未选中

    public String getTag_Id() {
        return Tag_Id;
    }

    public void setTag_Id(String tag_Id) {
        Tag_Id = tag_Id;
    }

    public String getTag_Name() {
        return Tag_Name;
    }

    public void setTag_Name(String tag_Name) {
        Tag_Name = tag_Name;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
