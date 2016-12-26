package com.huishangyun.Office.Clock;

import com.huishangyun.Util.L;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * 闹钟设置操作类
 * @author xsl
 *
 */
public class ManagerAlarm {
	private static Intent intent;
	private static AlarmManager alarmManager;
	private static Context context;
	
	/**
	 *  启动闹钟设置
	 * @param context
	 * @param setTime
	 * @param requestCode 每个闹钟值必须唯一，不然设置下一个闹钟的时候会覆盖上一个闹钟
	 * @param flags
	 */
	public static void setAlarm(Context context,long setTime,int requestCode,int flags){
		L.e("requestCode:" + requestCode);
		//设置闹钟
		//if (requestCode != IMChatService.PING_CODE) {
			ManagerAlarm.context = context;
		    intent = new Intent(context,AlarmReceiver.class);
		    intent.putExtra("index", requestCode);
		    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, flags);
		    alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
			//单次闹钟设置
			alarmManager.set(AlarmManager.RTC_WAKEUP, setTime, pendingIntent);
		/*} else {
			L.e("setAlarm");
			ManagerAlarm.context = context;
		    intent = new Intent(context,PingReceiver.class);
		    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, flags);
		    alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
			//单次闹钟设置
			alarmManager.set(AlarmManager.RTC_WAKEUP, setTime, pendingIntent);
		//}
		
//		Toast.makeText(context, "闹钟已经设置，开始计时！", Toast.LENGTH_SHORT).show();
*/	}
   
	/**
	 * 取消闹钟
	 */
	public static void cancelAlarm(int requestCode,int flags){
		
		Intent intent = new Intent(context,
				 AlarmReceiver.class);
         PendingIntent sender = PendingIntent.getBroadcast(
        		 context, requestCode, intent,flags);
         // And cancel the alarm.
         AlarmManager am = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
         am.cancel(sender);
		
		 
	}
	
	/**
	 * 取消闹钟
	 */
	/*public static void cancelAlarm(Context context, int requestCode,int flags) {
	
		Intent intent = new Intent(context,
				 PingReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(
       		 context, requestCode, intent,flags);
        // And cancel the alarm.
        AlarmManager am = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        am.cancel(sender);
	}*/
	
}
