package com.huishangyun.Channel.Task;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Channel.Plan.CustomersListActivity;
import com.huishangyun.Channel.Plan.SortModel;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Channel.Clues.ShowDialog;
import com.huishangyun.model.EnumKey;
import com.huishangyun.Fragment.TimeFragment;
import com.huishangyun.Fragment.TimeFragment.TimeFace;
import com.huishangyun.Interface.OnDialogDown;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.T;
import com.huishangyun.Util.TimeUtil;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.Util.webServiceHelp;
import com.huishangyun.Util.webServiceHelp.OnServiceCallBack;
import com.huishangyun.manager.EnumManager;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Enum;
import com.huishangyun.model.Methods;
import com.huishangyun.yun.R;

/**
 * -添加任务界面
 * 
 * @author-pan
 * @version-V1.0 2014/8/09
 * @see-无
 * @since-亿企云app/渠道
 * 
 */
public class TaskAddActivity extends BaseActivity implements TimeFace,OnDialogDown {

	private static final int START_DATE_DIALOG = 0;//开始时间的选择
	private static final int END_DATE_DIALOG = 1;//结束时间的类型
	private static final int PRIORITY_DIALOG = 2;
	private EditText themeEdt;// 任务主题
	private TextView startDateTxt;// 开始时间
	private TextView endDateTxt;// 结束时间
	private TextView priorityTxt;// 优先级
	private TextView executorTxt;// 执行人
	private TextView customersTxt;// 关联客户
	private TextView opportunitiesTxt;// 关联商机
	private EditText descriptionEdt;// 任务描述
	private Button submitBtn;// 提交按钮
	private TimeFragment timeFragment;// 时间选择悬浮框
	private ArrayList<HashMap<String, Object>> mList;
	private int Flag = 0;
	private static final int CHOOSE_MANAGER = 0;//选择执行人
	private static final int CHOOSE_MEMBER = 1;//关联客户
	private static final int CHOOSE_OPPORT = 2;
	private int ExeManager_ID = 0;
	private int member_ID = -1;
	private int bussiness_ID = -1;
	private long startTime = 0;
	private long endTime = 0;
	private String OpportTitle = "";//商机标题
	private String opportDescribe = "";//商机描述
	private String MemberName ="";//客户名称
	private webServiceHelp<T> webServiceHelp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_add);
		
		/**xsl添加*/
		Intent intent = getIntent();
		bussiness_ID = intent.getIntExtra("Business_ID", 0);
		OpportTitle = intent.getStringExtra("Title");
		if (OpportTitle != null && !OpportTitle.equals("")) {
			member_ID = intent.getIntExtra("Member_ID", -1);
			MemberName = intent.getStringExtra("MemberName");
			opportDescribe = intent.getStringExtra("opportDescribe");
		}
		/**End*/
		
		initView();
		initListener();
		webServiceHelp = new webServiceHelp<T>(Methods.TASK_ADD, new TypeToken<ZJResponse>(){}.getType());
		webServiceHelp.setOnServiceCallBack(onServiceCallBack);
		
		/**xsl修改*/
		if (OpportTitle != null && bussiness_ID != 0) {
			initBackTitle("转任务");
		}else {
			initBackTitle("新增任务");
		}
		setResult(RESULT_CANCELED);
		/**xsl修改END*/
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		webServiceHelp.removeOnServiceCallBack();
		super.onDestroy();
	}

	/**
	 * -初始化各组件
	 */
	private void initView() {
		//查找组件
		themeEdt = (EditText) this.findViewById(R.id.task_add_theme);
		startDateTxt = (TextView) this.findViewById(R.id.task_add_start_date);
		endDateTxt = (TextView) this.findViewById(R.id.task_add_end_date);
		priorityTxt = (TextView) this.findViewById(R.id.task_add_priority);
		executorTxt = (TextView) this.findViewById(R.id.task_add_executor);
		customersTxt = (TextView) this.findViewById(R.id.task_add_customers);
		opportunitiesTxt = (TextView) this
				.findViewById(R.id.task_add_opportunities);
		descriptionEdt = (EditText) this
				.findViewById(R.id.task_add_description);
		submitBtn = (Button) this.findViewById(R.id.task_add_submit);
		String timeString = TimeUtil.getInfoTime(getSystemTime());
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = format.parse(timeString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		startTime = date.getTime();
		endTime = startTime;
		//设置默认的时间
		startDateTxt.setText(timeString);
		endDateTxt.setText(timeString);
		
		/*******xsl修改*******/
		if (OpportTitle != null && !OpportTitle.equals("")) {
		themeEdt.setText(OpportTitle);
		customersTxt.setText(MemberName);
		descriptionEdt.setText(opportDescribe);
		}
		/********End******/
		
		
		//初始化List容器
		mList = new ArrayList<HashMap<String,Object>>();
		List<Enum> list = EnumManager.getInstance(TaskAddActivity.this).getAllEnums(EnumKey.ENUM_TASKFLAG);
		for (Enum enum1 : list) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("name", enum1.getLab());
			map.put("key", Integer.parseInt(enum1.getIntKey()));
			mList.add(map);
		}
	}

	/**
	 * -设置监听事件
	 */
	private void initListener() {
		submitBtn.setOnClickListener(mClickListener);
		startDateTxt.setOnClickListener(mClickListener);
		endDateTxt.setOnClickListener(mClickListener);
		priorityTxt.setOnClickListener(mClickListener);
		executorTxt.setOnClickListener(mClickListener);
		customersTxt.setOnClickListener(mClickListener);
		
		/**xsl修改*/
		if (OpportTitle != null && bussiness_ID != 0) {
			//设置关联商机名称
			opportunitiesTxt.setText(OpportTitle);
		}else {
			opportunitiesTxt.setOnClickListener(mClickListener);	
		}
		/**end*/
	}

	/**
	 * -点击时间监听器
	 */
	private OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.task_add_submit:// 提交按钮
				if (chekSubmit()) {
					showDialog("正在提交...");
					
					//new Thread(new SubmitTask()).start();
					webServiceHelp.start(getJson());
				}
				break;
			case R.id.task_add_start_date:
				// 选择开始时间
				timeFragment = new TimeFragment();
				timeFragment.setIndex(TaskAddActivity.this,
						START_DATE_DIALOG);
				timeFragment.show(getSupportFragmentManager(), "dialog");
				break;
				
			case R.id.task_add_end_date:
				// 选择结束时间
				timeFragment = new TimeFragment();
				timeFragment.setIndex(TaskAddActivity.this,
						END_DATE_DIALOG);
				timeFragment.show(getSupportFragmentManager(), "dialog");
				break;
				
			case R.id.task_add_priority:
				//显示悬浮框
				new ShowDialog(TaskAddActivity.this, PRIORITY_DIALOG, mList, "选择优先级", TaskAddActivity.this,"请选择优先级！",null).customDialog();
				break;
				
			case R.id.task_add_executor:
				//选择执行人
				Intent mIntent = new Intent(TaskAddActivity.this, CustomersListActivity.class);
				mIntent.putExtra("mode", "1");
				mIntent.putExtra("groupName", "组");
				mIntent.putExtra("Tittle", "选择执行人");
				mIntent.putExtra("select", 1);
				startActivityForResult(mIntent, CHOOSE_MANAGER);
				break;
			case R.id.task_add_customers:
				//关联客户
				Intent intent = new Intent(TaskAddActivity.this, CustomersListActivity.class);
				intent.putExtra("mode", "0");
				intent.putExtra("groupName", "组");
				intent.putExtra("Tittle", "选择客户");
				intent.putExtra("select", 1);
				startActivityForResult(intent, CHOOSE_MEMBER);
				break;
				
			case R.id.task_add_opportunities:
				//关联商机
				Intent intent2 = new Intent(TaskAddActivity.this, TaskOpportActivity.class);
				startActivityForResult(intent2, CHOOSE_OPPORT);
				break;

			default:
				break;
			}
		}
	};
	
	/**
	 * 
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case CHOOSE_MANAGER://选择执行人返回
			Bundle bundle = data.getBundleExtra("bundle");
			List<SortModel> mList = new ArrayList<SortModel>();
			mList = (List<SortModel>) bundle.getSerializable("result");
			if (mList.size() > 0) {//判断是否有返回值
				ExeManager_ID = mList.get(0).getID();
				executorTxt.setText(mList.get(0).getCompany_name());
			}
			break;
		case CHOOSE_MEMBER://选择客户返回
			Bundle bundle2 = data.getBundleExtra("bundle");
			List<SortModel> sortModels = new ArrayList<SortModel>();
			sortModels = (List<SortModel>) bundle2.getSerializable("result");
			L.e("sortModels = " + sortModels.size());
			if (sortModels.size() > 0) {
				member_ID = sortModels.get(0).getID();
				customersTxt.setText(sortModels.get(0).getCompany_name());
			}
			break;
			
		case CHOOSE_OPPORT:
			opportunitiesTxt.setText(data.getStringExtra("Name"));
			bussiness_ID = data.getIntExtra("ID", 0);
			break;

		default:
			break;
		}
	};

	@Override
	public void chooseTime(String time, int type, long timeStamp) {
		// TODO Auto-generated method stub
		switch (type) {// 根据所传回的类型区分开始时间和结束时间

		case START_DATE_DIALOG:// 开始时间
			startDateTxt.setText(time);
			startTime = timeStamp;
			break;

		case END_DATE_DIALOG:// 结束时间
			endDateTxt.setText(time);
			endTime = timeStamp;
			L.e("startTime = " + startTime);
			L.e("endTime = " + endTime);
			break;

		default:
			break;
		}
	}

	@Override
	public void onDialogDown(int position, int type) {
		// TODO Auto-generated method stub
		//根据返回的位置显示优先级
		Map<String, Object> map = mList.get(position);
		priorityTxt.setText((CharSequence) map.get("name"));
		Flag = (Integer) map.get("key");
	}
	
	/**
	 * 获取需要提交的Json数据
	 * @return
	 */
	private String getJson() {
		ZJRequest<AddTaskModel> zjRequest = new ZJRequest<AddTaskModel>();
		AddTaskModel taskModel = new AddTaskModel();
		taskModel.setID(0);
		taskModel.setAction("Insert");
		taskModel.setTitle(themeEdt.getText().toString().trim());
		taskModel.setExeManager_ID(ExeManager_ID);
		//.......添加必要的属性
		taskModel.setStartTime(startDateTxt.getText().toString().trim());
		taskModel.setEndTime(endDateTxt.getText().toString().trim());
		taskModel.setParent_ID(0);
		taskModel.setProportion(0);
		taskModel.setIsSplit(false);
		taskModel.setFlag(Flag);
		taskModel.setNote(descriptionEdt.getText().toString().trim());
		taskModel.setStatus(1);
		taskModel.setProgressNum(0);
		taskModel.setCancelMsg("");
		taskModel.setCheckManager_ID(Integer.parseInt(preferences.getString(Constant.HUISHANGYUN_UID, "0")));
		taskModel.setManager_ID(Integer.parseInt(preferences.getString(Constant.HUISHANGYUN_UID, "0")));
		taskModel.setMember_ID(member_ID);
		taskModel.setBussiness_ID(bussiness_ID);
		zjRequest.setData(taskModel);
		return JsonUtil.toJson(zjRequest);
		
	}
	
	/**
	 * 处理需要改变UI的操作
	 */
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			dismissDialog();
			switch (msg.what) {
			case HanderUtil.case1://提交成功
				showCustomToast("提交成功", true);
				setResult(RESULT_OK);
				finish();
				break;
				
			case HanderUtil.case2://提交失败
				showCustomToast((String) msg.obj, false);
				break;
			default:
				break;
			}
		};
	};
	
	/**
	 * 接口返回监听
	 */
	private OnServiceCallBack<T> onServiceCallBack = new OnServiceCallBack<T>() {

		@Override
		public void onServiceCallBack(boolean haveCallBack,
				ZJResponse<T> zjResponse) {
			// TODO Auto-generated method stub
			if (haveCallBack && zjResponse != null) {
				if (zjResponse.getCode() == 0) {
					mHandler.sendEmptyMessage(HanderUtil.case1);
				} else {
					Message msg = new Message();
					msg.what = HanderUtil.case2;
					msg.obj = zjResponse.getDesc();
					mHandler.sendMessage(msg);
				}
			} else {
				Message msg = new Message();
				msg.what = HanderUtil.case2;
				msg.obj = "无网络连接";
				mHandler.sendMessage(msg);
			}
		}
	};
	
	/**
	 * 校验数据
	 * @return 
	 */
	private boolean chekSubmit() {
		if (!isCanSubmit(themeEdt.getText().toString().trim())) {
			showCustomToast("请填写主题", false);
			return false;
		}
		if (!isCanSubmit(priorityTxt.getText().toString().trim())) {
			showCustomToast("请选择优先级", false);
			return false;
		}
		if (!isCanSubmit(executorTxt.getText().toString().trim())) {
			showCustomToast("请选择执行人", false);
			return false;
		}
		if (!isCanSubmit(descriptionEdt.getText().toString().trim())) {
			showCustomToast("请填写任务描述", false);
			return false;
		}
		if (endTime < startTime) {
			showCustomToast("请选择正确的任务时间", false);
			return false;
		}
		
		return true;
		
	}
	
	private boolean isCanSubmit(String str) {
		if (str.equals("")) {
			return false;
		}
		return true;
	}
}
