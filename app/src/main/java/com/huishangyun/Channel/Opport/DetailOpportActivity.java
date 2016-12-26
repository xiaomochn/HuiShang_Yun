package com.huishangyun.Channel.Opport;


import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Channel.Task.TaskAddActivity;
import com.huishangyun.Channel.Task.TaskDetailsActivity;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.manager.EnumManager;
import com.huishangyun.model.EnumKey;
import com.huishangyun.model.Methods;
import com.huishangyun.yun.R;

public class DetailOpportActivity extends BaseActivity {

	private static  String TAG = null;
	private RelativeLayout back;//返回按钮
	private ImageView rewrite;//编辑
	private TextView customer;//客户
	private TextView theme;//主题
	private TextView stage;//阶段
	private TextView money_sum;//金额
	private TextView state;//状态
	private TextView create_person;//客户
	private TextView create_date;//创建日期
	private TextView describe;//描述
	private String  IDSTING;//获得item的ID
	private int  ID;//获得item的ID
	private ImageView jiantou;//状态右边箭头
	private CreateBusinses Opport;
	private String ChangeOpport = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_opport_detail);
		Intent intent = getIntent();
		ChangeOpport = intent.getStringExtra("ChangeOpport");
		IDSTING = intent.getStringExtra("ID");
		ID = Integer.parseInt(IDSTING);//获得id整型值
		
		L.i("ID:" + ID);
//		getNetData(ID);
		init();
	
		
	}
	
	/**
	 * 布局控件实例化
	 */
     private void init() {
 
    	 //头部控件
    	 back = (RelativeLayout)findViewById(R.id.back);
    	 rewrite = (ImageView) findViewById(R.id.rewrite);
    	 
    	 //列表
    	 customer = (TextView) findViewById(R.id.create_opport_customer);
    	 theme = (TextView) findViewById(R.id.create_opport_theme);
    	 stage = (TextView) findViewById(R.id.create_opport_stage);
    	 money_sum = (TextView) findViewById(R.id.create_opport_money_sum);
    	 state = (TextView) findViewById(R.id.create_opport_state);
    	 create_person = (TextView) findViewById(R.id.create_opport_person);
    	 create_date = (TextView) findViewById(R.id.create_opport_date);
    	 describe = (TextView) findViewById(R.id.create_opport_description);
    	 jiantou = (ImageView) findViewById(R.id.jiantou);
    	 
    	 back.setOnClickListener(new ButtonClickListener());
    	 rewrite.setOnClickListener(new ButtonClickListener());
    	 state.setOnClickListener(new ButtonClickListener());
    	    	
     } 
     
     /**
      * 单击事件处理类
      * @author xsl
      *
      */
     public class ButtonClickListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			
			case R.id.back:
				
				finish();
				
				break;
				
			case R.id.rewrite://跳转编辑页面
				
				Intent intent = new Intent(DetailOpportActivity.this, DetailEditOpportActivity.class);
				intent.putExtra("ID", Opport.getID() + "");//传值ID
				intent.putExtra("customer", Opport.getMember_Name());//客户
				intent.putExtra("theme", Opport.getTitle());//主题
				intent.putExtra("stage", stageTransform(Opport.getSalesStage()));//阶段
				intent.putExtra("Menber_ID", Opport.getMember_ID());
				/**  以特定格式显示   */
				NumberFormat nf = NumberFormat.getNumberInstance();
			    nf.setMaximumFractionDigits(2);
				intent.putExtra("money_sum", Opport.getForecastMoney() + "");//金额
				intent.putExtra("describe", Opport.getNote());//描述
				startActivity(intent);
				
				break;
			case R.id.create_opport_state://转任务
//				if (ChangeOpport.equals("ChangeOpport")) {
//					L.e("不跳转");
//				}else {
				
				if ((state.getText().toString()).equals("未处理")) {	
					
				Intent intent1 = new Intent(DetailOpportActivity.this, TaskAddActivity.class);
				intent1.putExtra("Business_ID", Opport.getID());//商机id
				L.e("Opport.getID():" + Opport.getID());
				intent1.putExtra("Title", Opport.getTitle());//商机主题---关联任务主题和关联商机关联商机
				intent1.putExtra("opportDescribe", describe.getText().toString().trim());//商机描述--关联任务的描述
				intent1.putExtra("Member_ID", Opport.getMember_ID());//关联任务的客户
				intent1.putExtra("MemberName", customer.getText().toString().trim());//客户名称
				startActivity(intent1);
				
				} else if ((state.getText().toString()).equals("转任务")
						|| (state.getText().toString()).equals("已完成")) {
		
					//跳转到查看任务详情
					Intent intent2 = new Intent(DetailOpportActivity.this, TaskDetailsActivity.class);
					intent2.putExtra("ID", Opport.getTask_ID());
					startActivity(intent2);
//				 }
			}
				break;
			default:
				break;
			}
		}
    	 
     }
     
     /**
 	 * 获取详情数据
 	 * @param ID
 	 */
 	private void getNetData(final int ID){
 		new Thread(){
 			@Override
 			public void run() {
 				// TODO Auto-generated method stub	
 			
 				String result = DataUtil.callWebService(
 						Methods.SUPPLY_DETAILS,
 						getJson(ID));				
 				L.i("result:" + result);
 				//先判断网络数据是否获取成功，防止网络不好导致程序崩溃
 				if (result != null) {
 					// 获取对象的Type
 					Type type = new TypeToken<ZJResponse<CreateBusinses>>() {
 					}.getType();
 					ZJResponse<CreateBusinses> zjResponse = JsonUtil.fromJson(result,
							type);
 					// 获取对象列表
 					/*ItemLists = zjResponse.getResult();*/
 					Opport = zjResponse.getResult();
 					mHandler.sendEmptyMessage(1);
 				} else {
 					mHandler.sendEmptyMessage(2);
 					
 				}
 				
 			}
 		}.start();
 	}
 	
 	/**
 	 * 设置json对象
 	 * @param ID 详情id
 	 * @return
 	 */
 	private String getJson(int ID) {
 		ZJRequest zjRequest = new ZJRequest();
 		// 详情id
 		zjRequest.setID(ID);
 		
 		return JsonUtil.toJson(zjRequest);

 	}
 	
 	public Handler mHandler = new Handler(){
 		public void handleMessage(android.os.Message msg) {
 			super.handleMessage(msg);
 			switch (msg.what) {
			case 1:
				L.i("Manager_Name:" + Opport.getManager_Name() );
				customer.setText(Opport.getMember_Name());//客户
				theme.setText(Opport.getTitle());//主题				
				stage.setText(stageTransform(Opport.getSalesStage()));//阶段
				/**  以特定格式显示   */
//				NumberFormat nf = NumberFormat.getNumberInstance();
//			    nf.setMaximumFractionDigits(2);
				money_sum.setText("¥ " +  backPrice(Double.parseDouble(Opport.getForecastMoney())));//金额
				state.setText(backStatus(Opport.getStatus()));//状态
				create_person.setText(Opport.getManager_Name());//创建人
				create_date.setText(backDate(Opport.getAddDateTime()));
				describe.setText(Opport.getNote());
				
				 //对界面初始化处理
		    	 if ((Opport.getStatus())==1) {
		    		 rewrite.setVisibility(View.VISIBLE);
		    		 state.setText("未处理");
		    		 state.setTextColor(Color.parseColor("#e60000"));
				}else {
					 rewrite.setVisibility(View.INVISIBLE);
					 if ((Opport.getStatus())==2) {
						 state.setText("转任务");
						 state.setTextColor(Color.parseColor("#21a5de"));
					}else {
						 state.setText("已完成");
						state.setTextColor(Color.parseColor("#e60000"));
					}
					 
				}

				break;

			default:
				break;
			}
 		};
 	};
 	
 	
 	/**
	 * 传入一个double的价格返回一个保留两位小数点的string价格。
	 * @param getprice
	 * @return
	 */
	private String backPrice(double getprice){
		DecimalFormat df = new DecimalFormat("#.00");
		df.format(getprice);
		return df.format(getprice);
		
	}
 	
 	/**
 	 * 阶段string类型转换
 	 * @param stage 传入int类型阶段状态
 	 * @return 返回转换后的string值
 	 */
 	private String stageTransform(int index){
 		String backresult = null;
 		//数据字典查状态
 		com.huishangyun.model.Enum enum1 = EnumManager.getInstance(context)
					.getEmunForIntKey(EnumKey.ENUM_SALES_STAGE, "" + index);
 		
 		try {//主动抛异常，判断数据情况
 			
 			backresult = enum1.getLab();
 		    
		} catch (Exception e) {
			// TODO: handle exception
			showCustomToast("相应商机内容已做出改变或不存在！", false);		
		}
 		return backresult;
 	}
 	
 	/**
	 * 传入后台接到的时间，返回我们需要的格式
	 * @param date 传入时间参数
	 * @return
	 */
	private String backDate(String date){
		//这里包含0位但不包含5即（0，5】
		String a = date.substring(0,4);
		String b = date.substring(5,7);
		String c = date.substring(8,10);
		String datesString = a + "-" + b + "-" + c ;
		
		return datesString;		
	}
	
	/**
	 * 状态转化
	 * @param index
	 * @return
	 */
	private String backStatus(int index){
		String backresult = null;
		switch (index) {
		case 1:
			backresult = "未处理";
			state.setTextColor(0xffe60000);
			break;
		case 2:			
			backresult = "转商机";
			state.setTextColor(0xff21a5de);
			break;
		case 3:			
			backresult = "已关闭";
			state.setTextColor(0xff969696);	
			jiantou.setVisibility(View.INVISIBLE);
			break;
		default:
			break;
		}
		return backresult;
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		getNetData(ID);
		super.onResume();
	}
}
