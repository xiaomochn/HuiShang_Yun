package com.huishangyun.service;

/** 
 * @Title: MUCInfo.java 
 * @Package ouc.sei.suxin.android.data.entity 
 * @Description: 用于传输从Server端传输过来的MUC的信息 
 * @author Yang Zhilong 
 * @blog http://blog.csdn.net/yangzl2008 
 * @date 2013年11月27日 上午9:27:25 
 * @version V1.0 
 */  
public class MUCInfo {
	private String account;  
    private String room;  
    private String nickname;  
  
    public String getAccount() {  
        return account;  
    }  
  
    public void setAccount(String account) {  
        this.account = account;  
    }  
  
    public String getRoom() {  
        return room;  
    }  
  
    public void setRoom(String room) {  
        this.room = room;  
    }  
  
    public String getNickname() {  
        return nickname;  
    }  
  
    public void setNickname(String nickname) {  
        if (nickname.contains("@")) {  
            this.nickname = nickname.substring(0, account.indexOf("@"));  
            return;  
        }  
        this.nickname = nickname;  
    }  
  
    @Override  
    public String toString() {  
        return "MUCInfo [account=" + account + ", room=" + room + ", nickname="  
                + nickname + "]";  
    }  
}
