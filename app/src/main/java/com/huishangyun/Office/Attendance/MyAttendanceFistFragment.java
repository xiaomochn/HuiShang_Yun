package com.huishangyun.Office.Attendance;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.model.Methods;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.yun.R;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 考勤——我的考勤fragment
 * 
 * @author xsl
 * 
 */
public class MyAttendanceFistFragment extends Fragment {
	protected static final String TAG = null;
	View myView;
	private String MangerName;//用户名
	private TextView attendance_name;//考勤人
	private TextView attendance_month;//考勤月份
	private LinearLayout drawlayout;
	private TextView Janurary;// 一月
	private TextView February;// 二月
	private TextView March;// 三月
	private TextView April;// 四月
	private TextView May;// 五月
	private TextView June;// 六月
	private TextView July;// 七月
	private TextView August;// 八月
	private TextView September;// 九月
	private TextView October;// 十月
	private TextView November;// 十一月
	private TextView December;// 十二月
	private TextView askforleave;//请假
	private TextView later;//迟到
	private TextView leftearly;//早退
	private TextView bussinesstrip;//出差
	private TextView reallyattendance;//实际出勤
	Calendar calendar = Calendar.getInstance();
    private List<OfficeList> list = new  ArrayList<OfficeList>();
	private boolean isonDestroy = false;
  
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		myView = inflater.inflate(
				R.layout.activity_office_myatendance_fistfragment, null);
		drawlayout = (LinearLayout) myView.findViewById(R.id.drawlayout);
		init();
		isonDestroy = false;
		return myView;
	}
	
	/**
	 * 获得网络数据
	 */
	private void setNetData(final int month) {
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String yyjjString = getJson(month);
				Log.e(TAG, "yyjjString:" + yyjjString);
				String result = DataUtil.callWebService(
						Methods.GET_ATTENDANCE_LIST,
						getJson(month));	
				//先判断网络数据是否获取成功，防止网络不好导致程序崩溃
				if (result != null) {
					
					// 获取对象的Type
					Type type = new TypeToken<ZJResponse<OfficeList>>() {
					}.getType();
					ZJResponse<OfficeList> zjResponse = JsonUtil.fromJson(result,
							type);
					list = zjResponse.getResults();
					if (list.size() != 0) {
						Log.e(TAG, "list:" + list.size());
						Log.e(TAG, "list====>:" + list.get(0).getLate());
					}
					
				    mHandler.sendEmptyMessage(0);
						
				} else {
					mHandler.sendEmptyMessage(1);
				}
				
			}
		}.start();
	}

	@Override
	public void onDestroy() {
		isonDestroy = true;
		super.onDestroy();
	}

	/**
	 * 设置json对象
	 *
	 */
	private String getJson(int month) {
		
		ZJRequest zjRequest = new ZJRequest();
		zjRequest.setCompany_ID(getActivity().getIntent().getIntExtra("Company_ID", 1016));//公司id
		zjRequest.setMonth(month);
		zjRequest.setManager_ID(getActivity().getIntent().getIntExtra("Manager_ID", 0));
		return JsonUtil.toJson(zjRequest);

	}
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			if (isonDestroy) {
				//已经退出就不进行下一步
				return;
			}
			switch (msg.what) {
			case 0:
				Log.e(TAG, "================>:" + list.size());
				FanTypeView view = new FanTypeView(getActivity(),list);
				//重绘前情况清空画布
				drawlayout.removeAllViews();
				view.setMinimumHeight(200);
				view.setMinimumWidth(200);
				// 通知view组件重绘
				view.invalidate();
				drawlayout.addView(view);
				if (list.size()!= 0) {
					askforleave.setText("请假" + "(" +list.get(0).getLeave()  + "天" + ")");
					later.setText("迟到" + "(" +list.get(0).getLate()  + "天" + ")");
					leftearly.setText("早退" + "(" +list.get(0).getEarly()  + "天" + ")");
					bussinesstrip.setText("出差" + "(" +list.get(0).getTravel()  + "天" + ")");
					reallyattendance.setText("实际出勤" + "(" +list.get(0).getNormal() + "天" +")");
				}else {
					askforleave.setText("请假" + "(" + "0.0天" + ")");
					later.setText("迟到" + "(" + "0.0天" + ")");
					leftearly.setText("早退" + "("  + "0.0天" + ")");
					bussinesstrip.setText("出差" + "(" + "0.0天" + ")");
					reallyattendance.setText("实际出勤" + "(" + "0.0天" +")");
				}
				
				break;
			case 1:
				drawlayout.removeAllViews();
				Toast.makeText(getActivity(), "未查到考勤数据！", Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
		};
	};


	/**
	 * 初始化控件
	 */
	private void init() {
		MangerName = getActivity().getIntent().getStringExtra("MangerName");
		attendance_name = (TextView) myView.findViewById(R.id.attendance_name);
		attendance_month = (TextView) myView.findViewById(R.id.attendance_month);
		Janurary = (TextView) myView.findViewById(R.id.Janurary);
		February = (TextView) myView.findViewById(R.id.February);
		March = (TextView) myView.findViewById(R.id.March);
		April = (TextView) myView.findViewById(R.id.April);
		May = (TextView) myView.findViewById(R.id.May);
		June = (TextView) myView.findViewById(R.id.June);
		July = (TextView) myView.findViewById(R.id.July);
		August = (TextView) myView.findViewById(R.id.August);
		September = (TextView) myView.findViewById(R.id.September);
		October = (TextView) myView.findViewById(R.id.October);
		November = (TextView) myView.findViewById(R.id.November);
		December = (TextView) myView.findViewById(R.id.December);
		askforleave = (TextView) myView.findViewById(R.id.askforleave);
		later = (TextView) myView.findViewById(R.id.later);
		leftearly = (TextView) myView.findViewById(R.id.leftearly);
		bussinesstrip = (TextView) myView.findViewById(R.id.bussinesstrip);
		reallyattendance = (TextView) myView.findViewById(R.id.reallyattendance);
	
		Janurary.setOnClickListener(new ButtonClickListener());
		February.setOnClickListener(new ButtonClickListener());
		March.setOnClickListener(new ButtonClickListener());
		April.setOnClickListener(new ButtonClickListener());
		May.setOnClickListener(new ButtonClickListener());
		June.setOnClickListener(new ButtonClickListener());
		July.setOnClickListener(new ButtonClickListener());
		August.setOnClickListener(new ButtonClickListener());
		September.setOnClickListener(new ButtonClickListener());
		October.setOnClickListener(new ButtonClickListener());
		November.setOnClickListener(new ButtonClickListener());
		December.setOnClickListener(new ButtonClickListener());
		attendance_name.setText(MangerName);
		int month = calendar.get(Calendar.MONTH) + 1;
		attendance_month.setText(month + "月份考勤");
		setNetData(month);
		setDefaultMonth(month);
		
	}

	/**
	 * 单击事件处理
	 * 
	 * @author Administrator
	 * 
	 */
	private class ButtonClickListener implements View.OnClickListener {

		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.Janurary://一月
				setTextView();
				Janurary.setBackgroundResource(R.drawable.bt);
				Janurary.setTextColor(0xffffffff);
				attendance_month.setText("1月份考勤");
				setNetData(1);
				break;
			case R.id.February://二月
				setTextView();
				February.setBackgroundResource(R.drawable.bt);
				February.setTextColor(0xffffffff);
				attendance_month.setText("2月份考勤");
				setNetData(2);
				break;
			case R.id.March://三月
				setTextView();
				March.setBackgroundResource(R.drawable.bt);
				March.setTextColor(0xffffffff);
				attendance_month.setText("3月份考勤");
				setNetData(3);
				break;
			case R.id.April://四月
				setTextView();
				April.setBackgroundResource(R.drawable.bt);
				April.setTextColor(0xffffffff);
				attendance_month.setText("4月份考勤");
				setNetData(4);
				break;
			case R.id.May://五月
				setTextView();
				May.setBackgroundResource(R.drawable.bt);
				May.setTextColor(0xffffffff);
				attendance_month.setText("5月份考勤");
				setNetData(5);
				break;
			case R.id.June://六月
				setTextView();
				June.setBackgroundResource(R.drawable.bt);
				June.setTextColor(0xffffffff);
				attendance_month.setText("6月份考勤");
				setNetData(6);
				break;
			case R.id.July://七月
				setTextView();
				July.setBackgroundResource(R.drawable.bt);
				July.setTextColor(0xffffffff);
				attendance_month.setText("7月份考勤");
				setNetData(7);
				break;
			case R.id.August://八月
				setTextView();
				August.setBackgroundResource(R.drawable.bt);
				August.setTextColor(0xffffffff);
				attendance_month.setText("8月份考勤");
				setNetData(8);
				break;
			case R.id.September://九月
				setTextView();
				September.setBackgroundResource(R.drawable.bt);
				September.setTextColor(0xffffffff);
				attendance_month.setText("9月份考勤");
				setNetData(9);
				break;
			case R.id.October://十月
				setTextView();
				October.setBackgroundResource(R.drawable.bt);
				October.setTextColor(0xffffffff);
				attendance_month.setText("10月份考勤");
				setNetData(10);
				break;
			case R.id.November://十一月
				setTextView();
				November.setBackgroundResource(R.drawable.bt);
				November.setTextColor(0xffffffff);
				attendance_month.setText("11月份考勤");
				setNetData(11);
				break;
			case R.id.December://十二月
				setTextView();
				December.setBackgroundResource(R.drawable.bt);
				December.setTextColor(0xffffffff);
				attendance_month.setText("12月份考勤");
				setNetData(12);
				break;
			

			default:
				break;
			}
		}

	}
	
	/**
	 * 设置月份初始属性
	 */
	private void setTextView(){
		Janurary.setBackgroundResource(R.drawable.ed);
		February.setBackgroundResource(R.drawable.ed);
		March.setBackgroundResource(R.drawable.ed);
		April.setBackgroundResource(R.drawable.ed);
		May.setBackgroundResource(R.drawable.ed);
		June.setBackgroundResource(R.drawable.ed);
		July.setBackgroundResource(R.drawable.ed);
		August.setBackgroundResource(R.drawable.ed);
		September.setBackgroundResource(R.drawable.ed);
		October.setBackgroundResource(R.drawable.ed);
		November.setBackgroundResource(R.drawable.ed);
		December.setBackgroundResource(R.drawable.ed);

		Janurary.setTextColor(0xff969696);
		February.setTextColor(0xff969696);
		March.setTextColor(0xff969696);
		April.setTextColor(0xff969696);
		May.setTextColor(0xff969696);
		June.setTextColor(0xff969696);
		July.setTextColor(0xff969696);
		September.setTextColor(0xff969696);
		October.setTextColor(0xff969696);
		August.setTextColor(0xff969696);
		November.setTextColor(0xff969696);
		December.setTextColor(0xff969696);
	}
	
	/**
	 * 设置默认月份事件
	 */
	private void setDefaultMonth(int month){
		
		if (month==1) {
			setTextView();
			Janurary.setBackgroundResource(R.drawable.bt);
			Janurary.setTextColor(0xffffffff);
		}else if (month==2) {
			setTextView();
			February.setBackgroundResource(R.drawable.bt);
			February.setTextColor(0xffffffff);
		}else if (month==3) {
			setTextView();
			March.setBackgroundResource(R.drawable.bt);
			March.setTextColor(0xffffffff);
		}else if (month==4) {
			setTextView();
			April.setBackgroundResource(R.drawable.bt);
			April.setTextColor(0xffffffff);
		}else if (month==5) {
			setTextView();
			May.setBackgroundResource(R.drawable.bt);
			May.setTextColor(0xffffffff);
		}else if (month==6) {
			setTextView();
			June.setBackgroundResource(R.drawable.bt);
			June.setTextColor(0xffffffff);
		}else if (month==7) {
			setTextView();
			July.setBackgroundResource(R.drawable.bt);
			July.setTextColor(0xffffffff);
		}else if (month==8) {
			setTextView();
			August.setBackgroundResource(R.drawable.bt);
			August.setTextColor(0xffffffff);
		}else if (month==9) {
			setTextView();
			September.setBackgroundResource(R.drawable.bt);
			September.setTextColor(0xffffffff);
		}else if (month==10) {
			setTextView();
			October.setBackgroundResource(R.drawable.bt);
			October.setTextColor(0xffffffff);
		}else if (month==11) {
			setTextView();
			November.setBackgroundResource(R.drawable.bt);
			November.setTextColor(0xffffffff);
		}else if (month==12) {
			setTextView();
			December.setBackgroundResource(R.drawable.bt);
			December.setTextColor(0xffffffff);
		}
		
	}

}
