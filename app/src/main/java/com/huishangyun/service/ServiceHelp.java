package com.huishangyun.service;

import java.util.List;

import com.huishangyun.Util.L;

import android.app.ActivityManager;
import android.content.Context;

public class ServiceHelp {
	/**
     * 用来判断服务是否运行.
     * @param context
     * @param className 判断的服务名字：包名+类名
     * @return true 在运行, false 不在运行
     */
      
    public boolean isServiceRunning(Context context,String className) {        
      
        boolean isRunning = false;
      
        ActivityManager activityManager = (ActivityManager)context.getSystemService(context.ACTIVITY_SERVICE);
      
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
      
        if (!(serviceList.size()>0)) {
            return false;
        }
      
        for (int i=0; i<serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().toString().equals(className) == true) {
            	L.e("获取到的服务名称为 = " + serviceList.get(i).service.getClassName().toString());
                isRunning = true;
                break;
            }
        }
      
        /*Log.i(TAG,"service is running?=="+isRunning);*/
        L.e("service is running?=="+isRunning);
        return isRunning;
    }
    
    public void runServiceName(Context context) {
    	   ActivityManager activityManager =  (ActivityManager)context.getSystemService(context.ACTIVITY_SERVICE);
    	        List<ActivityManager.RunningServiceInfo> serviceList  = activityManager.getRunningServices(Integer.MAX_VALUE);
    	   for (ActivityManager.RunningServiceInfo runningServiceInfo : serviceList) {
			L.e("Service名称 = " + runningServiceInfo.service.getClassName().toString());
		}
    }
}
