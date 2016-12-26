package com.huishangyun.Channel.Clues;

import java.lang.reflect.Type;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Channel.Opport.CreateOpportActivity;
import com.huishangyun.Channel.Opport.DetailOpportActivity;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.manager.EnumManager;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.model.EnumKey;
import com.huishangyun.model.Methods;
import com.huishangyun.yun.R;

public class DetailClueActivity extends BaseActivity {
	private static final String TAG = null;
	private RelativeLayout rewrite;// 修改按钮
	private RelativeLayout back;
	private  TextView company_name, contact_name, 
	telephone_number, address, status, manager_name, 
	add_time,content;// 公司、联系人、电话、地址、状态、创建人、创建时间、内容
	private String ClueID;//获得传值线索id
	private LinearLayout layout_status;//状态条
	private ImageView img_right;//标记可跳转和点击图片
	
	
	private TextView Source;//线索来源
	private Detail_Clue clue;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clue_detail);
		Intent intent =getIntent();
		ClueID = intent.getStringExtra("ID");		
		init();
//		getNetData(Integer.parseInt(ClueID));
//		//判断是否有网络
//		if (PingService()==false) {
//			mHandler.sendEmptyMessage(3);
//		}
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
						Methods.DETAIL_CLUE,
						getJson(ID));				
				Log.e(TAG, "result:" + result);
				//先判断网络数据是否获取成功，防止网络不好导致程序崩溃
				if (result != null) {
					// 获取对象的Type
					Type type = new TypeToken<ZJResponse<Detail_Clue>>() {
					}.getType();
					ZJResponse<Detail_Clue> zjResponse = JsonUtil.fromJson(result,
							type);
					// 获取对象列表
					/*ItemLists = zjResponse.getResult();*/
					clue = zjResponse.getResult();
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
	
	/**
	 * 初始化布局，加载布局控件
	 */
	private void init() {
		rewrite = (RelativeLayout) findViewById(R.id.rewrite);
		back = (RelativeLayout) findViewById(R.id.back);
		company_name = (TextView)findViewById(R.id.company_name);
		contact_name = (TextView)findViewById(R.id.contact_name);
		telephone_number = (TextView)findViewById(R.id.telephone_number);
		address = (TextView)findViewById(R.id.address);
		status = (TextView)findViewById(R.id.status);
		manager_name = (TextView)findViewById(R.id.manager_name);
		add_time = (TextView)findViewById(R.id.add_time);
		content = (TextView)findViewById(R.id.content);
		Source = (TextView)findViewById(R.id.source);
		layout_status = (LinearLayout) findViewById(R.id.layout_status);
		img_right = (ImageView) findViewById(R.id.img_right);
		
		rewrite.setOnClickListener(new ButtonClickListener());
		back.setOnClickListener(new ButtonClickListener());
		layout_status.setOnClickListener(new ButtonClickListener());

	}

	/**
	 * 单击事件处理
	 * 
	 * @author Administrator
	 * 
	 */
	public class ButtonClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.rewrite:
				if (PingService()) {
				//传值
				Intent intent = new Intent(DetailClueActivity.this, ModifyClueActivity.class);
				intent.putExtra("ClueID", ClueID);//线索id
				//公司
				intent.putExtra("company", clue.getCompany());
				//联系人
				intent.putExtra("contact_name", clue.getName());
				//电话
				intent.putExtra("telephone", clue.getMobile());
				//地址
				intent.putExtra("address", clue.getAddress());				
				//来源
				intent.putExtra("source", clue.getSource());//线索来源
				//内容
				intent.putExtra("note", clue.getNote());
				//创建人
				intent.putExtra("Manger_Name", clue.getManager_Name());
			
				startActivity(intent);
				finish();
			
				}else {
					 mHandler.sendEmptyMessage(3);
				}
				break;

			case R.id.back:
				  finish();

				break;
			case R.id.layout_status:
				if (status.getText().toString().equals("未处理")) {
					Intent intent = new Intent(DetailClueActivity.this,CreateOpportActivity.class);
					intent.putExtra("Clue_ID", Integer.parseInt(ClueID));//传线索id
					intent.putExtra("Note", content.getText().toString());
					intent.putExtra("name", "转商机");
					intent.putExtra("CompanyName", company_name.getText().toString().trim());//公司
					intent.putExtra("ContactName", contact_name.getText().toString().trim());//联系人
					intent.putExtra("telephone", telephone_number.getText().toString().trim());//电话
					intent.putExtra("Address", address.getText().toString().trim());//地址
					startActivity(intent);
					
				}else if (status.getText().toString().equals("转商机")) {
					Intent intent = new Intent(DetailClueActivity.this,DetailOpportActivity.class);
					intent.putExtra("ID", clue.getBusiness_ID()+ "");//传线索id
					intent.putExtra("ChangeOpport", "ChangeOpport");
					startActivity(intent);
					
				}else if (status.getText().toString().equals("已关闭")) {
					
					
				}
				break;

			default:
				break;
			}

		}
	}
	
	/**
	 * 更新详情数据
	 */
	public Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				
				
				String statusString;
				if (clue.getStatus()==1) {
					statusString="未处理";
					status.setTextColor(0xffe60000);
				}else if (clue.getStatus()==2) {
					statusString="转商机";
					rewrite.setVisibility(View.INVISIBLE);
					status.setTextColor(0xff21a5de);
				}else {
					statusString= "已关闭";
					status.setTextColor(0xff969696);
					img_right.setVisibility(View.INVISIBLE);
				}
				
				company_name.setText(clue.getCompany());
				contact_name.setText(clue.getName());
				telephone_number.setText(clue.getMobile());
				address.setText(clue.getAddress());
				status.setText(statusString);
				manager_name.setText(clue.getManager_Name());
				add_time.setText(backDate(clue.getAddDateTime()));		
				content.setText(clue.getNote());
				
				//根据数字查对应中文
				com.huishangyun.model.Enum enum1 = EnumManager.getInstance(context)
						.getEmunForIntKey(EnumKey.ENUM_CLUE_SOURCE, "" + clue.getSource());
				if (enum1 == null) {
					Source.setText("来源不存在");
					Source.setTextColor(0xffe60000);
				}else {
					Source.setText(enum1.getLab());
				}
				
				
				
				break;
			case 2:
//				Toast.makeText(DetailClueActivity.this, "没有获得网络数据！", Toast.LENGTH_SHORT).show();
				break;
			case 3:
				Toast.makeText(DetailClueActivity.this, "无网络,请检查网络连接！", Toast.LENGTH_SHORT).show();
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
		//这里包含0位但不包含5即（0，5】
		String a = date.substring(0,4);
		String b = date.substring(5,7);
		String c = date.substring(8,10);
		String datesString = a + "-" + b + "-" + c ;
		
		return datesString;		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		getNetData(Integer.parseInt(ClueID));
		super.onResume();
	}
}