package com.huishangyun.Channel.Plan;


import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Channel.Clues.ClueCustomToast;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Fragment.TimeFragment.TimeFace;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.model.Methods;
import com.huishangyun.yun.R;

public class CreatePlanActivity extends BaseActivity implements TimeFace {
	protected static final String TAG = null;
	private LinearLayout back;//返回按钮
	private EditText theme;//主题
	private TextView person;//执行人
	private TextView start_date;//开始日期
	private TextView end_date;//结束日期
	private Button nextButton;//下一步按钮
	private PlanList list = new PlanList();
	private PlanList list1 = new PlanList();
	private PlanList Tlist;
	ArrayList<SortModel> arrayList = new ArrayList<SortModel>();
	private static final int STATER_TIME = 1;
	private static final int ENDTIME = 2;
	private int PlanID;//创建计划时返回的ID;
	private int ID = 0;//计划id
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plan_create_first_step);
		Intent intent = getIntent();
		ID = intent.getIntExtra("ID", -1);
		Log.e(TAG, "ID:" + ID);
		init();
	}
	
	
	/**
	 * 实例化控件
	 */
	private void init() {
		
	  back = (LinearLayout) findViewById(R.id.back);
      theme = (EditText) findViewById(R.id.create_plan_theme);
      person = (TextView) findViewById(R.id.create_plan_carry_out_person);
      start_date = (TextView) findViewById(R.id.create_plan_start_date);
      end_date = (TextView) findViewById(R.id.create_plan_end_date);
      nextButton = (Button) findViewById(R.id.create_plan_next);
      
		if (ID != -1) {
            //获得历史计划数据
			getNetData1(ID);
			// 执行人不能修改
			person.setTextColor(Color.parseColor("#969696"));
		} else {
			// 可选择执行人
			person.setOnClickListener(new ButtonClickListener());
		}
      
      back.setOnClickListener(new ButtonClickListener());
      nextButton.setOnClickListener(new ButtonClickListener());
      start_date.setOnClickListener(new ButtonClickListener());
      end_date.setOnClickListener(new ButtonClickListener());
      
	}
	
	public class ButtonClickListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.back:
				finish();				
				break;
			case R.id.create_plan_start_date:
				NormalDatePickDialog.setDate(getContext(),start_date, null);
				break;
				
			case R.id.create_plan_end_date:
				if (!(start_date.getText().toString().trim()).equals("")) {
					NormalDatePickDialog.setDate(getContext(),end_date, start_date.getText().toString());
				}else {
					NormalDatePickDialog.setDate(getContext(),end_date, null);
				}
				break;
				
			case R.id.create_plan_carry_out_person:
				
				Intent intent = new Intent(CreatePlanActivity.this, CustomersListActivity.class);
				//选择个客户/人员界面，客户传值“0”，人员传值“1”（注意传String类型）
				intent.putExtra("mode", "1");
				//多选传0，单选传1
				intent.putExtra("select", 1);
				//传递分组名称
				intent.putExtra("groupName", "组");
				intent.putExtra("Tittle", "选择人员");
				startActivityForResult(intent, 0);
				break;
			case R.id.create_plan_next:
				 
					if (isWrite()) {
						
					
					try {	
					
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					Date d1 = df.parse(start_date.getText().toString());
					Date d2 = df.parse(end_date.getText().toString());
					long diff = d2.getTime() - d1.getTime();
					long days = diff/ (1000 * 60 * 60 * 24);
					if (days>=0) {
						// ID传0为新增，其它为修改
						if (ID != -1) {
							// 修改
							mHandler.sendEmptyMessage(2);
						} else {
							mHandler.sendEmptyMessage(1);
						}
					}else {
//						Toast.makeText(CreatePlanActivity.this, "结束日期不能小于开始日期！",
//								Toast.LENGTH_SHORT).show();
						showDialog("结束日期不能小于开始日期！");
					 }
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
				
				break;
			default:
				break;
			}
		}
		
	}
	
	/**
	 * 修改或者创建计划
	 * @param ID
	 * @param title
	 * @paramstartDate
	 * @paramendDate
	 * @param Manager_ID
	 */
	private void getNetData(final int ID, final String title,
			final String StartDate, final String EndDate, final int Manager_ID) {
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String yyjjString = getJson(ID, title, StartDate, EndDate, Manager_ID);
				Log.e(TAG, "yyjjString:" + yyjjString);
				String result = DataUtil.callWebService(
						Methods.PLAN_CREATE,
						getJson(ID, title, StartDate, EndDate, Manager_ID));
				
				//先判断网络数据是否获取成功，防止网络不好导致程序崩溃
				if (result != null) {
					//json解析返回结果
					try {
						JSONObject jsonObject = new JSONObject(result);
						Log.e(TAG, "code:" + jsonObject.getInt("Code"));
						int code = jsonObject.getInt("Code");
						PlanID = jsonObject.getInt("Result");
						Log.e(TAG, "Result:" + PlanID);
						Message msg =  mHandler.obtainMessage();
						msg.what = code;
						mHandler.sendMessage(msg);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}											
						
				} else {
//					Toast.makeText(CreateOpportActivity.this, "没有更多数据！", Toast.LENGTH_SHORT).show();
				}
				
			}
		}.start();
	}
	
	/**
	 * 设置json对象
	 * @paramCompany_ID 公司id
	 * @param Manager_ID 用户编号
	 * @paramDepartment_ID 部门编号
	 * @paramkeywords 搜索关键字
	 * @parampageIndex 页码
	 * @return
	 */
	private String getJson( int ID, String title, String StartDate, String EndDate , int Manager_ID) {
		if (ID != 0) {
			list.setAction("Update");
		}else {
			//添加时Action设为Insert，修改时设为Update      
			list.setAction("Insert");
		}
		list.setID(ID);
		list.setTitle(title);
		list.setStartDate(StartDate);
		list.setEndDate(EndDate);
		list.setManager_ID(Manager_ID);
		ZJRequest<PlanList> zjRequest = new ZJRequest<PlanList>();
		zjRequest.setData(list);
		return JsonUtil.toJson(zjRequest);

	}
	
	 /**
	 * 获取计划历史数据
	 * @param ID
	 */
	private void getNetData1(final int ID){
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub	
				String yyjjString = getJson1(ID);
				Log.e(TAG, "yyjjString:" + yyjjString);
				String result = DataUtil.callWebService(
						Methods.PLAN_DETAIL,
						getJson1(ID));				
				Log.e(TAG,"result----:" + result);
				//先判断网络数据是否获取成功，防止网络不好导致程序崩溃
				if (result != null) {
					// 获取对象的Type
					Type type = new TypeToken<ZJResponse<PlanList>>() {
					}.getType();
					ZJResponse<PlanList> zjResponse = JsonUtil.fromJson(result,
							type);
					// 获取对象列表
					/*ItemLists = zjResponse.getResult();*/
					Tlist = zjResponse.getResult();
					mHandler.sendEmptyMessage(4);
				} else {
					mHandler.sendEmptyMessage(2);
					
				}
				
			}
		}.start();
	}
	
	/**
	 * json对象
	 * @param ID 计划id
	 * @return
	 */
	private String getJson1(int ID) {
		
		ZJRequest zjRequest1 = new ZJRequest();
		zjRequest1.setID(ID);
		return JsonUtil.toJson(zjRequest1);

	}
	
	public Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				
				//提交成功
				Intent intent1 = new Intent(CreatePlanActivity.this, CreatePlanSecondStep.class);
				intent1.putExtra("Plan_ID", PlanID);//传值ID
				intent1.putExtra("index", ID);
				startActivity(intent1);
				 
				break;
				
            case 1:
            	
				getNetData(0, theme.getText().toString(), start_date.getText()
						.toString(), end_date.getText().toString(), arrayList
						.get(0).getID());
				
				break;
				
          case 2:
        	  int Manager_ID;
            	if ((person.getText().toString()).equals(Tlist.getManager_Name())) {
            		Manager_ID = Tlist.getManager_ID();
				}else {
					Manager_ID = arrayList.get(0).getID();
				}
				getNetData(ID, theme.getText().toString(), start_date.getText()
						.toString(), end_date.getText().toString(), Manager_ID);
				
				break;
            case 3:
            	person.setText(arrayList.get(0).getCompany_name());
            	break;
            case 4:
            	theme.setText(Tlist.getTitle());
            	person.setText(Tlist.getManager_Name());
            	start_date.setText(backDate(Tlist.getStartDate()));
            	end_date.setText(backDate(Tlist.getEndDate()));
            	break;
            case 101:
            	 showDialog("执行人计划有冲突，请检查！");
            	break;
			default:
				break;
			}
		};
	};
	
	 /**
		 * 传入后台接到的时间，返回我们需要的格式
		 * @param date 传入时间参数
		 * @return
		 */
		private String backDate(String date){
			//这里包含0位但不包含5即[0，5)
			String a = date.substring(0,4);
			String b = date.substring(5,7);
			String c = date.substring(8,10);
			String datesString = a + "-" + b + "-" + c ;
			
			return datesString;		
		}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			System.out.println("resultCode=="+resultCode+"requestCode=="+requestCode);
	        Bundle bundle = data.getBundleExtra("bundle");//得到新Activity 关闭后返回的数据
	        arrayList = (ArrayList<SortModel>) bundle.getSerializable("result");//接收泛型
	        if (arrayList.size()!=0) {
	        	 mHandler.sendEmptyMessage(3);
			}
		}
	}


	@Override
	public void chooseTime(String time, int type, long timeStamp) {
		// TODO Auto-generated method stub
		switch (type) {
		case STATER_TIME:
			
			//比较日期大小然后提示
			try {
				Calendar ca1 = Calendar.getInstance();//得到一个Calendar的实例 
				int year = ca1.get(Calendar.YEAR);
				int month = ca1.get(Calendar.MONTH);
				int dayOfMonth = ca1.get(Calendar.DAY_OF_MONTH);
				Date date = new Date(year - 1900, month, dayOfMonth);
				SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd");
                String today = sf1.format(date);
				 
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			Date d1 = df.parse(today);
			Date d2 = df.parse(time);//选择时间
			long diff = d2.getTime() - d1.getTime();
			long days = diff / (1000 * 60 * 60 * 24);
			Log.e(TAG, "days:" + days);
			if (days >= 0) {
				start_date.setText(time);
			 }else {
				//提示
				 showDialog("开始日期不能小于今天日期！");
			}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			
			
			break;
			
		case ENDTIME:
			if (start_date.getText().toString().trim().equals("")) {
				end_date.setText(time);
			}else {
				
			//比较日期大小然后提示
			try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			Date d1 = df.parse(start_date.getText().toString());
			Date d2 = df.parse(time);
			long diff = d2.getTime() - d1.getTime();
			long days = diff / (1000 * 60 * 60 * 24);
			Log.e(TAG, "days:" + days);
			if (days >= 0) {
//				SimpleDateFormat sf2 = new SimpleDateFormat("yyyy-MM-dd");
				end_date.setText(time);
			 }else {
				//提示
				 showDialog("结束日期不能小于开始日期！");
			}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			}
			break;

		default:
			break;
		}
	}
	
	/**
	 * 判断是否输入完成
	 * @return 
	 */
	private boolean isWrite(){
		if (theme.getText().toString().trim().equals("")) {
			showDialog("请填写主题！");
			return false;
		}
		if (person.getText().toString().trim().equals("")) {
			showDialog("请选择执行人！");
			return false;
		}
		
		if (start_date.getText().toString().trim().equals("")) {
			showDialog("请选择开始日期！");
			return false;
		}
		
		if (end_date.getText().toString().trim().equals("")) {
			showDialog("请选择结束日期！");
			return false;
		}
		
		return true;
		
		
	}
	
	public void showDialog(String TXT){
		new ClueCustomToast().showToast(CreatePlanActivity.this,
				R.drawable.toast_warn, TXT);

	}
	
	
}
