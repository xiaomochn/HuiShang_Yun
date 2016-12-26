package com.huishangyun.Office.Askforleave;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Channel.Clues.ClueCustomToast;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.model.Constant;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Office.Businesstrip.DefineDatePickDialog;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.model.Methods;
import com.huishangyun.yun.R;

/**
 * 请假新增主页
 * @author xsl
 *
 */
public class AddAskForLeaveActivivty extends BaseActivity {

	protected static final String TAG = null;
	private RelativeLayout back;// 返回
	private TextView startdate;// 开始日期
	private TextView enddate;// 结束日期
	private EditText note;// 备注
	private TextView writenote;// 备注按钮
	private TextView submit;// 提交按钮
	private View view1;
	private LinearLayout beizhu;
	private int flag=0;
	private AskForLeaveList list = new AskForLeaveList();
	private String mTpye;
	private String isSubmit= "0";
	private String FileName = "AskForLeaveData";
	private int ManagerID;//登入人id
	private int Company_ID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_office_askforleave_add);
		ManagerID = MyApplication.getInstance().getManagerID();
		Company_ID = MyApplication.getInstance().getCompanyID();
		FileName = FileName + Company_ID + ManagerID;
		mTpye = preferences.getString(Constant.HUISHANG_TYPE, "0");
		init();
	}

	/**
	 * 初始化界面
	 */
	private void init() {
		
		back = (RelativeLayout) findViewById(R.id.back);
		startdate = (TextView) findViewById(R.id.startdate);
		enddate = (TextView) findViewById(R.id.enddate);
		note = (EditText) findViewById(R.id.note);
		writenote = (TextView) findViewById(R.id.writenote);
		submit = (TextView) findViewById(R.id.submit);
		view1 = (View)  findViewById(R.id.view1);
		beizhu = (LinearLayout) findViewById(R.id.beizhu);
		try {
			getUiData();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			L.e("ui数据获取失败");
		}
		
		back.setOnClickListener(mClickListener);
		startdate.setOnClickListener(mClickListener);
		enddate.setOnClickListener(mClickListener);
		writenote.setOnClickListener(mClickListener);
		submit.setOnClickListener(mClickListener);

	}

	/**
	 * 单击事件处理
	 */
	private OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.back://返回
				saveUiData();
				finish();
				break;
			case R.id.startdate://开始日期
				dateSelect(startdate,"");
				break;
			case R.id.enddate://结束日期	
				if (!(startdate.getText().toString().trim()).equals("")) {
					dateSelect(enddate,backDate(startdate.getText().toString().trim()));
					L.e("日期：" + backDate(startdate.getText().toString().trim()));
				}else {
					dateSelect(enddate,"");
				}
				break;
			case R.id.writenote://显示备注填写按钮
				if (flag==0) {
					view1.setVisibility(View.VISIBLE);
					beizhu.setVisibility(View.VISIBLE);
					flag=1;
				}else {
					view1.setVisibility(View.GONE);
					beizhu.setVisibility(View.GONE);
					flag=0;
				}
				
				break;
			case R.id.submit://提交按钮
				if (isWrite()) {
					submit.setClickable(false);
					getNetData(startdate.getText().toString(), enddate.getText().toString(), note.getText().toString());
				}
				break;

			default:
				break;
			}
		}
	};
	
	/**
	 * 字符串分割再组合
	 * @param date
	 * @return
	 */
	private String backDate(String date) {
		String a = date.substring(0, 4);
		String b = date.substring(5, 7);
		String c = date.substring(8, 10);
		String d = date.substring(11, 16);
		return a + "年" + b + "月" + c + "日" + " " + d;
	}
	
	/**
	 * 提交请假数据
	 * @param StartTime
	 * @param EndTime
	 * @param Note
	 */
	private void getNetData(final String StartTime, final String EndTime, final String Note) {
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				Log.e(TAG, "===========>:" + getJson(StartTime, EndTime, Note));

				String result = DataUtil.callWebService(
						Methods.SET_LEAVE_DATA,
						getJson(StartTime, EndTime, Note));
				
				
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
	private String getJson(String StartTime, String EndTime, String Note) {
		
		list.setAction("Insert");
		// id新增传入0
		list.setID(0);
		list.setStartTime(StartTime);
		list.setEndTime(EndTime);
		list.setNote(Note);
		list.setManager_ID(Integer.parseInt(preferences.getString(
				Constant.HUISHANGYUN_UID, "0")));
		ZJRequest<AskForLeaveList> zjRequest = new ZJRequest<AskForLeaveList>();
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
				new ClueCustomToast().showToast(AddAskForLeaveActivivty.this,
        				R.drawable.toast_sucess, "提交成功！");
				submit.setClickable(true);
				Intent intent = new Intent(); 
				intent.setAction("ASKFORLEAVE_LIST_REFRESH");
				if (mTpye.equals("1")) {
					intent.putExtra("mflag", 1);
					AddAskForLeaveActivivty.this.sendBroadcast(intent);
				}else {
					intent.putExtra("mflag", 2);
					AddAskForLeaveActivivty.this.sendBroadcast(intent);
				}
				isSubmit = "1";
				saveUiData();
				finish();
				break;
            case 1:
            	submit.setClickable(true);
            	new ClueCustomToast().showToast(AddAskForLeaveActivivty.this,
        				R.drawable.toast_warn, "提交失败！");
				break;
			default:
				break;
			}
		};
	};


	
	
	/**
	 * 日期选择对话框  
	 * 选择出发日期和结束日期
	 * @param widgetname
	 *            控件名称
	 */
	private void dateSelect(final TextView widgetname,String time) {
		String sysTimeStr;
		final long sysTime = System.currentTimeMillis();
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
		if (time.equals("") || time == null) {
			sysTimeStr = format.format(new Date(sysTime));
		}else {
			sysTimeStr = time;
		}
		final DefineDatePickDialog dateTimePicKDialog = new DefineDatePickDialog(
				AddAskForLeaveActivivty.this, sysTimeStr);//初始时间
		//dateTimePicKDialog.dateTimePicKDialog(widgetname);
		AlertDialog alertDialog = dateTimePicKDialog.dateTimePicKDialog()
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				 try {
				SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				String time1 = sf1.format(new Date(sysTime));//当前时间
				Date d1 = sf1.parse(time1);
				Date d2 = sf1.parse(dateTimePicKDialog.getDataTime());
				long diff1 = d2.getTime() - d1.getTime();
				long days1 = diff1 / (1000 * 60 * 60 * 24);
				if (days1>=0) {
					if (widgetname == startdate) {
						if (enddate.getText().toString().trim().equals("")) {
							widgetname.setText(dateTimePicKDialog.getDataTime());
						}else {
							DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");	
							Date d01 = df.parse(enddate.getText().toString());
							Date d02 = d2;
							long diff = d02.getTime() - d01.getTime();
							long days = diff/ (1000 * 60 * 60 * 24);
							if (days<=0) {
								widgetname.setText(dateTimePicKDialog.getDataTime());
							}else {
								showDialog("选择的开始日期不能大于到达日期！");
							}
						}
						
						
					}else if (widgetname == enddate) {
						if (startdate.getText().toString().trim().equals("")) {
							
							widgetname.setText(dateTimePicKDialog.getDataTime());
				
						}else {
							DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");	
							Date d01 = df.parse(startdate.getText().toString());
							Date d02 = df.parse(dateTimePicKDialog.getDataTime());
							long diff = d02.getTime() - d01.getTime();
							//不按天计算
//							long days = diff/ (1000 * 60 * 60 * 24);
							if (diff>=0) {
								widgetname.setText(dateTimePicKDialog.getDataTime());
							}else {
								showDialog("选择的到达日期不能小于开始日期！");
							}
						}
					
					}
				}else {
					showDialog("请选择今天或以后的日期！");
				}
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				 
			dialog.dismiss();
			}
		}).
		setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		}).show();
		dateTimePicKDialog.setAlertDialog(alertDialog);

	}
	
	
	/**
	 * 判断是否输入完成
	 * 
	 * @return
	 */
	private boolean isWrite() {
		if (startdate.getText().toString().trim().equals("")) {
			showDialog("请选择起始日期！");
			return false;
		}
		if (enddate.getText().toString().trim().equals("")) {
			showDialog("请选择结束日期！");
			return false;
		}

		if (note.getText().toString().trim().equals("")) {
			showDialog("请填写请假事由！");
			return false;
		}

		return true;

	}
	
	public void showDialog(String TXT) {
		new ClueCustomToast().showToast(AddAskForLeaveActivivty.this,
				R.drawable.toast_warn, TXT);

	}
	
	/**
	 * 获得UI数据，并保存
	 */
	private void saveUiData(){
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("isSubmit", isSubmit);
		map.put("StartDate", startdate.getText().toString().trim());
		map.put("EndDate", enddate.getText().toString().trim());
		map.put("note", note.getText().toString().trim());
		boolean flag = AskForLeaveSharedPrefence.saveData(getContext(), FileName, map);
		if (flag) {
			L.e("请假UI数据保存成功！");
		}else {
			L.e("请假UI数据保存失败！");
		}
	}
	
	/**
	 * 获取存储的ui数据，并初始化数据
	 */
	private void getUiData(){
		HashMap<String, Object> map = new HashMap<String, Object>();
		map = AskForLeaveSharedPrefence.getData(getContext(), FileName);
		if (map.size()>0) {
			if ((map.get("isSubmit")).equals("0")) {
				startdate.setText(map.get("StartDate")+"");
				enddate.setText(map.get("EndDate")+"");
				if (!(map.get("note")).equals("")) {
					note.setText(map.get("note")+"");
					view1.setVisibility(View.VISIBLE);
					beizhu.setVisibility(View.VISIBLE);
					flag=1;
				}
				
			}
			
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (isSubmit.equals("0")) {
			saveUiData();
		}else if (isSubmit.equals("1")) {
			AskForLeaveSharedPrefence.ClearData(getContext(), FileName);
		}
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		saveUiData();
		super.onBackPressed();
	}
	
}
