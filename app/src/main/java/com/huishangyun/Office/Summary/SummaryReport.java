package com.huishangyun.Office.Summary;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Channel.Clues.ClueCustomToast;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Methods;
import com.huishangyun.yun.R;

/**
 * 小结上报界面
 * 
 * @author xsl
 * 
 */
public class SummaryReport extends BaseActivity {
	protected static final String TAG = null;
	private RelativeLayout back;// 返回
	private EditText todayedit;// 今天上报编辑内容
	private ImageView todayadd;// 今天工作内容添加按钮
	private EditText experience;// 心得体会
	private EditText tomorrowEdit;// 明日安排编辑内容
	private ImageView tomorrowadd;// 明日安排添加按钮
	private ImageView judge;// 是否补报
	private TextView afterReportdate;// 补报日期
	private LinearLayout bubaolayput;
	private Button submit;// 提交按钮
	private Calendar calendar = Calendar.getInstance();
	private boolean isBuBao = false;
	private SummaryDateList list = new SummaryDateList();
	private LinearLayout todaylayout;
	private LinearLayout planslayout;
	private ScrollView todayscroll;
	private ScrollView plansscroll;
	private List<EditText> todayEditTextlist = new ArrayList<EditText>();
	private List<EditText> plansEditTextlist = new ArrayList<EditText>();
	private int layoutIndex = 0;
	private int planslayoutIndex = 0;
	private String mTpye;
	private LinearLayout selectdate;
	private String isSubmit;//等于“1”为已提交数据，“0”为未提交数据
	private int ManagerID;//登入人id
	private int Company_ID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_office_summary_report);
		Company_ID = MyApplication.getInstance().getCompanyID();
		ManagerID = MyApplication.getInstance().getManagerID();
		mTpye = preferences.getString(Constant.HUISHANG_TYPE, "0");
		init();
	}
	
	/**
	 * 小结上报
	 * @param works 今日工作
	 * @param tips  心得体会
	 * @param plans 明日计划
	 * @param isAdd 是否补报
	 * @param belongDate 补报时间
	 */
	private void getNetData(final String works, final String tips, final String plans,
			final boolean isAdd, final String belongDate) {
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				Log.e(TAG, "===========>:" + getJson(works, tips, plans, isAdd,
				belongDate));

				String result = DataUtil.callWebService(
						Methods.SUMMARY_REPORT,
						getJson(works, tips, plans, isAdd,
								belongDate));
				

				// 先判断网络数据是否获取成功，防止网络不好导致程序崩溃
				if (result != null) {
					// 获取对象的Type
					try {
						JSONObject jsonObject = new JSONObject(result);
						// Log.e(TAG, "code:" + jsonObject.getInt("Code"));
						int code = jsonObject.getInt("Code");
						Log.e(TAG, "code:" + code);
						if (code == 0) {
							 mHandler.sendEmptyMessage(0);
						} else {
							 mHandler.sendEmptyMessage(1);
						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					 mHandler.sendEmptyMessage(1);

				}

			}
		}.start();
	}

	/**
	 * 设置json对象
	 * 
	 * @param
	 * @return
	 */
	private String getJson(String works, String tips, String plans,
			boolean isAdd, String belongDate) {
		
		list.setAction("Insert");
		// id新增传入0
		list.setID(0);
		list.setManager_ID(Integer.parseInt(preferences.getString(
				Constant.HUISHANGYUN_UID, "0")));
		list.setWorks(works);
		list.setTips(tips);
		list.setPlans(plans);
		list.setIsAdd(isAdd);
		list.setBelongDate(belongDate);
		ZJRequest<SummaryDateList> zjRequest = new ZJRequest<SummaryDateList>();
		zjRequest.setData(list);
		return JsonUtil.toJson(zjRequest);

	}
	
	/**
	 * 更新UI
	 */
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				new ClueCustomToast().showToast(SummaryReport.this,
        				R.drawable.toast_sucess, "提交成功！");
				submit.setClickable(true);
				Intent intent = new Intent(); 
				intent.setAction("SUMMARY_LIST_REFRESH");
				if (mTpye.equals("1")) {
					
					intent.putExtra("mflag", 1);
					 SummaryReport.this.sendBroadcast(intent);
				}else {
					intent.putExtra("mflag", 2);
					 SummaryReport.this.sendBroadcast(intent);
				}
				isSubmit = "1";
				finish();
				break;
            case 1:
            	new ClueCustomToast().showToast(SummaryReport.this,
        				R.drawable.toast_warn, "提交失败！");
            	submit.setClickable(true);
				break;
			default:
				break;
			}
		};
	};


	/**
	 * 初始化布局
	 */
	private void init() {
		todayEditTextlist.clear();
		plansEditTextlist.clear();
		back = (RelativeLayout) findViewById(R.id.back);
		todayedit = (EditText) findViewById(R.id.todayedit);
		todayadd = (ImageView) findViewById(R.id.todayadd);
		experience = (EditText) findViewById(R.id.experience);
		tomorrowEdit = (EditText) findViewById(R.id.tomorrowEdit);
		tomorrowadd = (ImageView) findViewById(R.id.tomorrowadd);
		todaylayout = (LinearLayout) findViewById(R.id.todaylayout);
		planslayout = (LinearLayout) findViewById(R.id.planslayout);
		todayscroll = (ScrollView) findViewById(R.id.todayscroll);
		plansscroll = (ScrollView) findViewById(R.id.plansscroll);
		selectdate = (LinearLayout) findViewById(R.id.selectdate);
		selectdate.setVisibility(View.INVISIBLE);
		
		judge = (ImageView) findViewById(R.id.judge);
		afterReportdate = (TextView) findViewById(R.id.afterReportdate);
		bubaolayput = (LinearLayout) findViewById(R.id.bubaolayput);
		submit = (Button) findViewById(R.id.submit);

		back.setOnClickListener(ButtonClickListener);
		todayadd.setOnClickListener(ButtonClickListener);
		tomorrowadd.setOnClickListener(ButtonClickListener);
		afterReportdate.setOnClickListener(ButtonClickListener);
		bubaolayput.setOnClickListener(ButtonClickListener);
		submit.setOnClickListener(ButtonClickListener);
	
		todayscroll.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (todayEditTextlist.size()<=0) {
					++layoutIndex;
					getTodayLayout(layoutIndex,null);
				}
				return false;
			}
		});
		
		plansscroll.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (plansEditTextlist.size()<=0) {
					++planslayoutIndex;
					getPlansLayout(planslayoutIndex,null);
				}
				return false;
			}
		});
		
		try {
			getUIdata();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			L.e("ui数据获取失败");
		}
	}
	
	/**
	 * 单击事件处理
	 * 
	 * @author xsl
	 * 
	 */
	private OnClickListener ButtonClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.back://返回
				 isSubmit = "0";
				 saveUIdata();
                 finish();
				break;

			case R.id.todayadd://今日工作添加按钮
			
				if (todayEditTextlist.size()>0) {
					if (todayEditTextlist.get(layoutIndex-1).getText().toString().trim().equals("")) {
						showDialog("请填写要添加第"+layoutIndex+"条的内容！");
					}else {
						++layoutIndex;
						getTodayLayout(layoutIndex,null);
					}
				}else {
					++layoutIndex;
					getTodayLayout(layoutIndex,null);
					
				}
				
				
				
				break;
			case R.id.tomorrowadd://明日计划工作添加按钮
				if (plansEditTextlist.size()>0) {
					if (plansEditTextlist.get(planslayoutIndex-1).getText().toString().trim().equals("")) {
						showDialog("请填写要添加第"+planslayoutIndex+"条的内容！");
					}else {
						++planslayoutIndex;
						getPlansLayout(planslayoutIndex,null);
					}
				}else {
					++planslayoutIndex;
					getPlansLayout(planslayoutIndex,null);
				}
				
				break;
			case R.id.bubaolayput://是否补报按钮
				if (!isBuBao) {// 补办
					judge.setBackgroundResource(R.drawable.visit_select_box);
					isBuBao = true;
					//清空数据
					afterReportdate.setText("");
					selectdate.setVisibility(View.VISIBLE);
				} else {// 不补办
					judge.setBackgroundResource(R.drawable.visit_box);
					isBuBao = false;
					afterReportdate.setText("");
					selectdate.setVisibility(View.INVISIBLE);
				}

				break;
			case R.id.afterReportdate://选择日期
				//如果补报，则可以去选择日期，而且是必填的
				if (isBuBao) {
					compareDate();
				}
				
				break;
			case R.id.submit://提交
	
				if (isWrite()) {
				String belongDate;
				String works;
				String plans;
				String data = null;
				String tips = experience.getText().toString().trim();
				if (afterReportdate.getText().toString().trim().equals("")) {
					belongDate = calendar.get(Calendar.YEAR) + "-" + 
				                (calendar.get(Calendar.MONTH) +1)+ "-" + calendar.get(Calendar.DAY_OF_MONTH);
				}else {
					belongDate = afterReportdate.getText().toString().trim();
				}
				 //今日工作内容
				works = backBuffer(todayEditTextlist); 
				 //明日工作内容
				plans = backBuffer(plansEditTextlist);
				if (works.equals("")) {
					showDialog("今日工作不能为空！");
					break;
				}
				if (plans.equals("")) {
					showDialog("明日安排不能为空！");
					break;
				}
			    submit.setClickable(false);
				getNetData(works, tips, plans, isBuBao, belongDate);
				}
				break;
			default:
				break;
			}
		}
	};
	
	/**
	 * 动态添加控件
	 */
	private void getTodayLayout(final int i,String str){
		LayoutInflater mInflater = LayoutInflater.from(this);
		final View view = mInflater.inflate(R.layout.activity_office_summary_note_item, null);
		TextView index = (TextView) view.findViewById(R.id.index);
		EditText note = (EditText) view.findViewById(R.id.note);
		if (str != null) {
			note.setText(str);
		}
		index.setText(layoutIndex+"、");
		//获取焦点，自动弹出软键盘
		note.setFocusable(true);
		note.setFocusableInTouchMode(true); 
		note.requestFocus(); 
        InputMethodManager inputManager = 
        (InputMethodManager)note.getContext().getSystemService(Context.INPUT_METHOD_SERVICE); 
        inputManager.showSoftInput(note, 0); 
        
		todayEditTextlist.add(note);
		todaylayout.addView(view);
	}
	
	/**
	 * 动态添加控件
	 */
	private void getPlansLayout(final int i,String str){
		LayoutInflater mInflater = LayoutInflater.from(this);
		final View view = mInflater.inflate(R.layout.activity_office_summary_note_item, null);
		TextView index = (TextView) view.findViewById(R.id.index);
		EditText note = (EditText) view.findViewById(R.id.note);
		if (str != null) {
			note.setText(str);
		}
		note.setFocusable(true);
		note.setFocusableInTouchMode(true); 
		note.requestFocus(); 
        InputMethodManager inputManager = 
        (InputMethodManager)note.getContext().getSystemService(Context.INPUT_METHOD_SERVICE); 
        inputManager.showSoftInput(note, 0); 
		index.setText(planslayoutIndex + "、");
		
		plansEditTextlist.add(note);
		planslayout.addView(view);
	}
	
	
	/**
	 * 但点出发时，对比开始日期是否是今天，如果不是，要求重新选择出发日期。
	 * @return 
	 */
	private void compareDate(){
		 final Date d1;
		try {
			Calendar calendar = Calendar.getInstance();
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH);
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			Date date1 = new Date(year - 1900, month, day); // 获取时间转换为Date对象
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
			String time = sf.format(date1);
		    d1 = sf.parse(time);
			
		Dialog dialog = new DatePickerDialog(SummaryReport.this,
				new DatePickerDialog.OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						// TODO Auto-generated method stub
					
					    try {
					    	Date date1 = new Date(year - 1900, monthOfYear, dayOfMonth); // 获取时间转换为Date对象
							SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd");
							String time1 = sf1.format(date1);
							Date d2 = sf1.parse(time1);
							
							long diff1 = d1.getTime() - d2.getTime();
							long days1 = diff1 / (1000 * 60 * 60 * 24);
							if (days1>=1) {
								afterReportdate.setText(+year + "-" + (monthOfYear + 1)
										+ "-" + dayOfMonth);
	                            
							}else {
								new ClueCustomToast().showToast(SummaryReport.this,
										R.drawable.toast_warn, "您只能补报今天之前的日期！");
							}
							
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					

					}
				}, calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH));
		dialog.show();
		
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * 判断是否输入完成
	 * @return 
	 */
	private boolean isWrite(){
		if (todayEditTextlist.size()<=0) {
			showDialog("请填写今日工作！");
			return false;
		}
		
//		if (experience.getText().toString().trim().equals("")) {
//			showDialog("请填写心得体会！");
//			return false;
//		}
		if (plansEditTextlist.size() <= 0) {
			showDialog("请填写明日安排！");
			return false;
		}
		if (isBuBao && afterReportdate.getText().toString().trim().equals("")) {
			showDialog("请选择补报日期！");
			return false;
		}
		
		return true;
		
		
	}
	
	public void showDialog(String TXT){
		new ClueCustomToast().showToast(SummaryReport.this,
				R.drawable.toast_warn, TXT);

	}
	
	/**
	 * 保存ui数据
	 */
	private void saveUIdata(){
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("isSubmit", isSubmit);
		map.put("TodayWorks", backBuffer(todayEditTextlist));
		map.put("experience", experience.getText().toString().trim());
		map.put("TomorrowPlans", backBuffer(plansEditTextlist));
		L.e("计划数据保存：" +  backBuffer(plansEditTextlist));
		if (isBuBao) {
			map.put("IsReport", "1");
		}else {
			map.put("IsReport", "0");
		}
		map.put("ReportDate", afterReportdate.getText().toString().trim());
		boolean flag = SharedPrefenceTools.saveData(getContext(), "SummaryReport"+ Company_ID +ManagerID, map);
		if (flag) {
			L.e("日报上报UI数据保存成功！");
		}else {
			L.e("日报上报UI数据保存失败");
		}
	
	}
	
	/**
	 * 获得保存的UI数据
	 */
	private void getUIdata(){
		HashMap<String, Object> map = new HashMap<String, Object>();
		map = SharedPrefenceTools.getData(getContext(), "SummaryReport" + Company_ID + ManagerID);
		if (map.size()>0) {
			if ((map.get("isSubmit")).equals("0")) {
				experience.setText(map.get("experience")+"");
				String[] temper1 = null;
				String[] temper2 = null;
				temper1 = (map.get("TodayWorks")+"").split("#");
				temper2 = (map.get("TomorrowPlans")+"").split("#");
				
				for (int i = 0; i < temper1.length; i++) {
					if (!temper1[i].equals("")) {
						++layoutIndex;
						getTodayLayout(layoutIndex,temper1[i]);
					}
				}
				
				for (int i = 0; i < temper2.length; i++) {
					if (!temper2[i].equals("")) {
						++planslayoutIndex;
						getPlansLayout(i, temper2[i]);
					}
				}
				
				if ((map.get("IsReport")+"").equals("1")) {
					isBuBao = true;
					judge.setBackgroundResource(R.drawable.visit_select_box);
					afterReportdate.setText(map.get("ReportDate")+"");
					selectdate.setVisibility(View.VISIBLE);
				}else if ((map.get("IsReport")+"").equals("0")) {
					judge.setBackgroundResource(R.drawable.visit_box);
					isBuBao = false;
					afterReportdate.setText("");
					selectdate.setVisibility(View.INVISIBLE);
				}
			}
		}
	}

	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		isSubmit = "0";
		saveUIdata();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (isSubmit.equals("0")) {
			isSubmit = "0";
			saveUIdata();
		}else if (isSubmit.equals("1")) {
			isSubmit = "1";
			SharedPrefenceTools.ClearData(getContext(), "SummaryReport" + Company_ID + ManagerID);
		}
		
	}

	/**
	 * 拼接字符串数据
	 * @param list 传入列表
	 * @return 返回字符串
	 */
	private String backBuffer(List<EditText> list) {
		// 今日工作拼接
		StringBuffer stringBuffer = new StringBuffer("");
		for (int i = 0; i < list.size(); i++) {

			if (i > 0
					&& !list.get(i - 1).getText().toString().trim().equals("")) {
				stringBuffer.append("#");
			}

			stringBuffer.append(list.get(i).getText().toString()
					.trim());
		}
		String str = stringBuffer.toString();
		return str;
	}
}
