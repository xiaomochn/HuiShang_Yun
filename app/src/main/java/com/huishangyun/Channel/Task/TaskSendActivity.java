package com.huishangyun.Channel.Task;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Channel.Plan.CustomersListActivity;
import com.huishangyun.Channel.Plan.SortModel;
import com.huishangyun.Fragment.TimeFragment;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.TimeUtil;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.Util.webServiceHelp;
import com.huishangyun.manager.EnumManager;
import com.huishangyun.model.Constant;
import com.huishangyun.model.EnumKey;
import com.huishangyun.model.Methods;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.T;
import com.huishangyun.yun.R;

/**
 * <p>任务转派界面</p>
 * @author Pan
 * @version 亿企云APP V1.0 2014-08-27 10:32  
 */
public class TaskSendActivity extends BaseActivity {
	private TextView themeEdt;// 任务主题
	private TextView startDateTxt;// 开始时间
	private TextView endDateTxt;// 结束时间
	private TextView priorityTxt;// 优先级
	private TextView executorTxt;// 执行人
	private TextView customersTxt;// 关联客户
	private TextView opportunitiesTxt;// 关联商机
	private TextView descriptionEdt;// 任务描述
	private Button submitBtn;// 提交按钮
	private TimeFragment timeFragment;// 时间选择悬浮框
	
	private static final int CHOOSE_MANAGER = 0;//选择执行人
	private static final int CHOOSE_MEMBER = 1;//关联客户
	private static final int CHOOSE_OPPORT = 2;
	
	private static final int START_DATE_DIALOG = 0;//开始时间的选择
	private static final int END_DATE_DIALOG = 1;//结束时间的类型
	private static final int PRIORITY_DIALOG = 2;
	private int ExeManager_ID = 0;
	private int Flag = 0;
	private TaskModel taskModel;
	private webServiceHelp<T> mServiceHelp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_send);
		taskModel = (TaskModel) getIntent().getSerializableExtra("model");
		ExeManager_ID = taskModel.getExeManager_ID();
		initView();
		initListener();
		initBackTitle("任务转派");
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mServiceHelp.removeOnServiceCallBack();
		super.onDestroy();
	}
	
	private void initView() {
		mServiceHelp = new webServiceHelp<T>(Methods.SET_TASK_OPER, new TypeToken<ZJResponse>(){}.getType());
		mServiceHelp.setOnServiceCallBack(onServiceCallBack);
		themeEdt = (TextView) this.findViewById(R.id.task_send_theme);
		startDateTxt = (TextView) this.findViewById(R.id.task_send_start_date);
		endDateTxt = (TextView) this.findViewById(R.id.task_send_end_date);
		priorityTxt = (TextView) this.findViewById(R.id.task_send_priority);
		executorTxt = (TextView) this.findViewById(R.id.task_send_executor);
		customersTxt = (TextView) this.findViewById(R.id.task_send_customers);
		opportunitiesTxt = (TextView) this
				.findViewById(R.id.task_send_opportunities);
		descriptionEdt = (TextView) this
				.findViewById(R.id.task_send_description);
		submitBtn = (Button) this.findViewById(R.id.task_send_submit);
		
		themeEdt.setText(taskModel.getTitle());
		//设置默认的时间
		startDateTxt.setText(TimeUtil.getTime(taskModel.getStartTime()));
		endDateTxt.setText(TimeUtil.getTime(taskModel.getEndTime()));
		String prior = EnumManager.getInstance(TaskSendActivity.this).getEmunForIntKey(EnumKey.ENUM_TASKFLAG, taskModel.getFlag() + "").getLab();
		priorityTxt.setText(prior);
		executorTxt.setText(taskModel.getExeManager_Name());
		customersTxt.setText(taskModel.getMember_Name());
		opportunitiesTxt.setText(taskModel.getBusiness_Title());
		descriptionEdt.setText(taskModel.getNote());
		
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
		opportunitiesTxt.setOnClickListener(mClickListener);
	}
	
	/**
	 * -点击时间监听器
	 */
	private OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.task_send_submit:// 提交按钮
				showDialog("正在提交");
				//new Thread(new SubmitTask()).start();
				mServiceHelp.start(getJson());
				break;
			case R.id.task_send_executor:
				//选择执行人
				Intent mIntent = new Intent(TaskSendActivity.this, CustomersListActivity.class);
				mIntent.putExtra("mode", "1");
				mIntent.putExtra("groupName", "组");
				mIntent.putExtra("Tittle", "选择执行人");
				mIntent.putExtra("select", "1");
				startActivityForResult(mIntent, CHOOSE_MANAGER);
				break;

			default:
				break;
			}
		}
	};
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case CHOOSE_MANAGER://选择执行人返回
			if (resultCode == RESULT_OK) {
				Bundle bundle = data.getBundleExtra("bundle");
				List<SortModel> mList = new ArrayList<SortModel>();
				mList = (List<SortModel>) bundle.getSerializable("result");
				if (mList.size() > 0) {//判断是否有返回值
					ExeManager_ID = mList.get(0).getID();
					executorTxt.setText(mList.get(0).getCompany_name());
				}
			}
			break;
		

		default:
			break;
		}
	};
	
	/**
	 * 获取需要提交的Json数据
	 * @return
	 */
	private String getJson() {
		ZJRequest zjRequest = new ZJRequest();
		zjRequest.setAction("Change");
		zjRequest.setID(this.taskModel.getID());
		zjRequest.setManager_ID(ExeManager_ID);
		//zjRequest.setNote(descriptionEdt.getText().toString().trim());
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
				sendBroadcast(new Intent().setAction(Constant.HUISHANG_RE_ACTION));
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
	
	private webServiceHelp.OnServiceCallBack<T> onServiceCallBack = new webServiceHelp.OnServiceCallBack<T>() {

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
}