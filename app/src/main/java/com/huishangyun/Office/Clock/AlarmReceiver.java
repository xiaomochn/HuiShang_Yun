package com.huishangyun.Office.Clock;

import java.util.Map;

import com.huishangyun.Util.L;
import com.huishangyun.App.MyApplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 闹钟广播接收
 * @author xsl
 *
 */
public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
//        Toast.makeText(context, "闹钟时间到了" + System.currentTimeMillis(), Toast.LENGTH_SHORT).show();
		String ManagerID = MyApplication.getInstance().getManagerID()+"";
		int index = intent.getIntExtra("index", 0);
        String sql = "select* from mClock where ManagerID = ? and mIndex = ?";
		DBManager dbManager = new DBManager(context);
		Map<String, String> map = dbManager.queryBySQL(sql, new String[] {ManagerID,index+""});
		if (map.get("isOpen").equals("1")) {
        L.e("================广播==========");
        intent = new Intent(context,RemindShow.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("index", index);
        context.startActivity(intent);
		}else {
			L.e("闹钟未开启！");
		}
	}

}
