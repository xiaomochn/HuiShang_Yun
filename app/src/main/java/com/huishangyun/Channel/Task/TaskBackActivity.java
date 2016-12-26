package com.huishangyun.Channel.Task;

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
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Fragment.TimeFragment;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.T;
import com.huishangyun.Util.TimeUtil;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.Util.webServiceHelp;
import com.huishangyun.manager.EnumManager;
import com.huishangyun.model.Constant;
import com.huishangyun.model.EnumKey;
import com.huishangyun.model.Methods;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.yun.R;

/**
 * <p>任务退回界面</p>
 * @author Pan
 * @version 亿企云APP V1.0 2014-08-27 10:32  
 */
public class TaskBackActivity extends BaseActivity {
	
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
	private EditText backCause;
	
	private static final int CHOOSE_MANAGER = 0;//选择执行人
	private static final int CHOOSE_MEMBER = 1;//关联客户
	private static final int CHOOSE_OPPORT = 2;
	
	private static final int START_DATE_DIALOG = 0;//开始时间的选择
	private static final int END_DATE_DIALOG = 1;//结束时间的类型
	private static final int PRIORITY_DIALOG = 2;
	private int ExeManager_ID = 0;
	private int Flag = 0;
	private TaskModel taskModel;
	private webServiceHelp<T> sumitServiceHelp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_back);
		taskModel = (TaskModel) getIntent().getSerializableExtra("model");
		initView();
		initListener();
		initBackTitle("任务退回");
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		sumitServiceHelp.removeOnServiceCallBack();
		super.onDestroy();
	}
	
	private void initView() {
		sumitServiceHelp = new webServiceHelp<T>(Methods.SET_TASK_OPER,
				new TypeToken<ZJResponse>(){}.getType());
		sumitServiceHelp.setOnServiceCallBack(onServiceCallBack);
		themeEdt = (TextView) this.findViewById(R.id.task_back_theme);
		startDateTxt = (TextView) this.findViewById(R.id.task_back_start_date);
		endDateTxt = (TextView) this.findViewById(R.id.task_back_end_date);
		priorityTxt = (TextView) this.findViewById(R.id.task_back_priority);
		executorTxt = (TextView) this.findViewById(R.id.task_back_executor);
		customersTxt = (TextView) this.findViewById(R.id.task_back_customers);
		opportunitiesTxt = (TextView) this
				.findViewById(R.id.task_back_opportunities);
		descriptionEdt = (TextView) this
				.findViewById(R.id.task_back_description);
		submitBtn = (Button) this.findViewById(R.id.task_back_submit);
		backCause = (EditText) this.findViewById(R.id.task_back_cause);
		themeEdt.setText(taskModel.getTitle());
		//设置默认的时间
		startDateTxt.setText(TimeUtil.getTime(taskModel.getStartTime()));
		endDateTxt.setText(TimeUtil.getTime(taskModel.getEndTime()));
		String prior = EnumManager.getInstance(TaskBackActivity.this).getEmunForIntKey(EnumKey.ENUM_TASKFLAG, taskModel.getFlag() + "").getLab();
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
			case R.id.task_back_submit:// 提交按钮
				if (backCause.getText().toString().equals("")) {
					showCustomToast("请输入退回原因", false);
					return;
				}
				showDialog("正在提交");
				//new Thread(new SubmitTask()).start();
				sumitServiceHelp.start(getJson());
				break;

			default:
				break;
			}
		}
	};
	
	/**
	 * 获取需要提交的Json数据
	 * @return
	 */
	private String getJson() {
		ZJRequest zjRequest = new ZJRequest();
		zjRequest.setAction("Back");
		zjRequest.setID(this.taskModel.getID());
		zjRequest.setNote(backCause.getText().toString().trim());
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
	
	/**
	 * 提交任务的线程
	 * @author Pan
	 *
	 */
	/*private class SubmitTask implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			L.e("json = " + getJson());
			String result = DataUtil.callWebService(Methods.SET_TASK_OPER, getJson());
			if (result != null) {
				Type type = new TypeToken<ZJResponse>(){}.getType();
				ZJResponse zjResponse = JsonUtil.fromJson(result, type);
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
		
	}*/
	
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
