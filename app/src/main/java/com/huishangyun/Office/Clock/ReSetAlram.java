package com.huishangyun.Office.Clock;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.huishangyun.Util.L;

/**
 * 回调闹钟设置操作类,初始闹钟设置
 * @author xsl
 *
 */
public class ReSetAlram {
	
	private  Context context;
	private  String alrm_Time;//闹钟时间
	private  String Manager_ID;//登入人id
	private int index;
	 //冒泡排序完的集合
    private List<Integer> mList = new ArrayList<Integer>();
    
	public ReSetAlram(Context context,String Manager_ID,int requestCode,int flags){
		this.context = context;
		this.Manager_ID = Manager_ID;
		this.index = requestCode;
	}

	/**
	 * 设置闹钟
	 */
	public  void setAlarm() {

		int Differdays = queryBySQL();
		L.e("Differdays:" + Differdays);
		if (Differdays < 0 ) {
			return;
		}
		long millionSeconds = 0;
		long NowSeconds = 0;
		try {
			// 相差时间计算
			Date d = new Date(System.currentTimeMillis());
			SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
			String date = sf.format(d);
			date = date + alrm_Time;
			L.e("date = " + date);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
			NowSeconds = sdf.parse(sdf.format(d)).getTime();
	        millionSeconds = sdf.parse(date).getTime() + Differdays* 24 * 60 * 60 * 1000;
			
			if (millionSeconds > NowSeconds+500 && Differdays >= 0) {
				millionSeconds = sdf.parse(date).getTime() + Differdays* 24 * 60 * 60 * 1000;
			}else if (millionSeconds <= NowSeconds+500 && Differdays==0) {
				if (mList.size()>1) {
//					 L.e("===========-----mList.size()>1:" + mList.get(1));
					 Differdays = mList.get(1);//取第二小的数据
					 millionSeconds = sdf.parse(date).getTime() + Differdays* 24 * 60 * 60 * 1000;
				}else if (mList.size()==1) {
//					L.e("===========-----mList.size()==1:" + mList.get(0));
					 millionSeconds = sdf.parse(date).getTime() + 7* 24 * 60 * 60 * 1000;
				}
			}else if (millionSeconds <= NowSeconds+500 && Differdays>0) {
				millionSeconds = sdf.parse(date).getTime() + Differdays* 24 * 60 * 60 * 1000;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (index==1) {
    		ManagerAlarm.setAlarm(context,millionSeconds,1,1);
    		L.e("millionSeconds:" + millionSeconds);
		}else if (index==2) {
			ManagerAlarm.setAlarm(context,millionSeconds,2,2);
		}
	
	}
	
	/**
	 * 查询数据库，闹钟是否开启
	 * 返回最近一个闹钟和当前离今天的时间
	 */
	public  int queryBySQL(){
		String sql = "select* from mClock where ManagerID = ? and mIndex = ?";
		DBManager dbManager = new DBManager(context);
		Map<String, String> map = dbManager.queryBySQL(sql, new String[] {Manager_ID,index+""});
		if (map.size()>0) {
//			L.e( "查询数据1alarmTime===>:" + map.get("alarmTime"));
//			L.e( "查询数据2repeatDays===>:" + map.get("repeatDays"));
			alrm_Time = map.get("alarmTime");
			//根据查得的repeatDays，计算最近的一个闹钟
			return getCompareDays(map.get("repeatDays"));
		}
		return -1;
	}
	

	  
	/**
	 * 比较数据大小，找到最近一个时间一个闹钟
	 * @param repeatDays
	 * @return
	 */
	private int getCompareDays(String repeatDays) {
		List<Integer> list = new ArrayList<Integer>();
		Calendar cal = Calendar.getInstance();
		int Min = 0;
		// 星期int代码值星期天为一周的第一天
		// 星期天为 1，星期六为7
		int week = cal.get(Calendar.DAY_OF_WEEK);
		String[] temp = null;
		temp = repeatDays.split(",");
			for (int i = 0; i < temp.length; i++) {
				if (!temp[i].equals("")) {
					// 对存储数据和今天比较的结果记录。
					list.add(compareDayNowToNext(week,
							Integer.parseInt(temp[i])));
				} else {
					break;
				}
			}
			//进行冒泡排序
			mList.clear();
			mList.addAll(bubbleSort(list, true));
			if (list.size() > 0) {
				Min = list.get(0);
				// 取出结果记录最小的一个作为返回下个闹钟的设置参数
				for (int i = 0; i < list.size(); i++) {
					Min = (list.get(i) >= Min) ? Min : list.get(i);
//					L.e("Min" + Min);
//					L.e("list.get(i)" + list.get(i));
				}
				return Min;

			} else {
				return -1;
			}
	}

	/**
	  * compare nowDay to nextDay 比较今天和下一天相差大小
	  * @param nowDay
	  * @param nextDay
	  * @return
	  */
	  public  int compareDayNowToNext(int nowDay,int nextDay){
	    	if(nextDay > nowDay){
	    		return (nextDay-nowDay);
	    	}else if(nextDay == nowDay){
	    	    return 0;	
	    	}else{
	    		return (7-(nowDay-nextDay));
	    	}
	    	
	    }
	  
	  /**
	   * 冒泡排序法，包含降序和升序排列
	   * @param list 传入Integer数组集合
	   * @param listdown 从小到大降序排列 传true ，升序传 false；
	   * @return 返回排序后的数据
	   * @author 谢水林
	   */
	private List<Integer> bubbleSort(List<Integer> list, boolean listdown) {
		List<Integer> backList = new ArrayList<Integer>();
		for (int i = 0; i < list.size(); i++) {
			backList.add(list.get(i));
		}
		int temp;// 定义一个中间变量
		if (listdown) {
			for (int i = 0; i < backList.size(); i++) {
				for (int j = 0; j < backList.size()-i-1; j++) {
					if (backList.get(j) > backList.get(j + 1)) {
						temp = backList.get(j + 1);
						// 这里注意顺序
						backList.set(j + 1, backList.get(j));
						backList.set(j, temp);
					}
				}
			}
		} else {
			for (int i = 0; i < backList.size(); i++) {
				for (int j = 0; j < backList.size()-i-1; j++) {
					if (list.get(j) < backList.get(j + 1)) {
						temp = backList.get(j + 1);
						// 这里注意顺序
						backList.set(j + 1, backList.get(j));
						backList.set(j, temp);
					}
				}
			}
		}
		
		for (int i = 0; i < backList.size(); i++) {
//			L.e("backList:" + backList.get(i));
		}
		return backList;
	}
	
}
