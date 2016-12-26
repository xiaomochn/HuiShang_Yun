package com.huishangyun.Channel.xiaoliang;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Channel.Clues.ClueCustomToast;
import com.huishangyun.Channel.Clues.ShowDialog;
import com.huishangyun.Channel.Plan.SortModel;
import com.huishangyun.Interface.OnDialogDown;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.Util.webServiceHelp;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Methods;
import com.huishangyun.model.XiaoshouModle;
import com.huishangyun.Channel.Plan.CustomersListActivity;
import com.huishangyun.yun.R;

public class XiaoLiang extends BaseActivity implements OnDialogDown {
	
	private webServiceHelp<XiaoshouModle> mServiceHelp;
	
	/*销售量的集合*/
	private List<XiaoshouModle> mXiaoshouModles = new ArrayList<XiaoshouModle>();
	
	private LinearLayout back;
	private ListView listView;
	private TextView customer, time, goods, danwei;
	private ImageView bt_add;
	private EditText xiaoliang, daohuo, jingpin, tuihuo, qizhong;
	private ArrayList<Map<String, String>> data = new ArrayList<Map<String,String>>();
	private MyAdapter myAdapter;
	private static final int SOURCE_DIALOG = 0;
	/**
	 * Constant.HUISHANG_DEPARTMENT_ID 部门ID int
	 * Constant.HUISHANG_DEPARTMENT_NAME 部门名称  string
	 * 
	 * Constant.XMPP_MY_REAlNAME manager_name string
	 * 
	 * Constant.HUISHANGYUN_UID manager_id String
	 * 
	 * 
	 */
	
	private int mBumengId;
	private String mBumengname;
	private String mManagerId;
	private String mKehuName;
	private String mZhouqi;
	private Integer mKehuId;
	private int mCompanyId;
	private Integer mChanpingId;
	private Integer mdanweiId;
	
	private int widths, heights;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.xiaoliangshangbao);
		
		mServiceHelp = new webServiceHelp<XiaoshouModle>(Methods.SET_XIAOSHOULIANG,
				new TypeToken<ZJResponse<XiaoshouModle>>(){}.getType());
		mServiceHelp.setOnServiceCallBack(onServiceCallBack);
		back = (LinearLayout) findViewById(R.id.back);
		customer = (TextView) findViewById(R.id.customer);
		time = (TextView) findViewById(R.id.time);
		goods = (TextView) findViewById(R.id.goods);
		danwei = (TextView) findViewById(R.id.danwei);
		bt_add = (ImageView) findViewById(R.id.bt_add);
		
		xiaoliang = (EditText) findViewById(R.id.xiaoliang);
		daohuo = (EditText) findViewById(R.id.daohuo);
		jingpin = (EditText) findViewById(R.id.jingpin);
		tuihuo = (EditText) findViewById(R.id.tuihuo);
		qizhong = (EditText) findViewById(R.id.qizhong);
		
		listView = (ListView)findViewById(R.id.listview);
		
		//添加按钮
		bt_add.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (customer.getText().toString().trim().equals("")) {
					showCustomToast("客户不为空", false);
					return;
				}else if(time.getText().toString().trim().equals("")){
					showCustomToast("周期不为空", false);
					return;
				}else if(goods.getText().toString().trim().equals("")){
					showCustomToast("产品不为空", false);
					return;
				}else if(danwei.getText().toString().trim().equals("")){
//					showCustomToast("客户不为空", false);
				}
								
//				new Ays().execute();
				addDate();
			}
		});
		
		
		
		init();
		initDate();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mServiceHelp.removeOnServiceCallBack();
		super.onDestroy();
	}
	
	/**
	 * Constant.HUISHANG_DEPARTMENT_ID 部门ID int
	 * Constant.HUISHANG_DEPARTMENT_NAME 部门名称  string
	 * 
	 * Constant.XMPP_MY_REAlNAME manager_name string
	 * 
	 * Constant.HUISHANGYUN_UID manager_id String
	 * 
	 * 
	 */
	/**
	 * 初始化数据
	 */
	private void initDate(){
		
		 mBumengId = MyApplication.preferences.getInt(Constant.HUISHANG_DEPARTMENT_ID, 0);
		 mBumengname = MyApplication.preferences.getString(Constant.HUISHANG_DEPARTMENT_NAME, "");
		 mManagerId = MyApplication.preferences.getString(Constant.HUISHANGYUN_UID, "");
		 mKehuName = MyApplication.preferences.getString(Constant.XMPP_MY_REAlNAME, "");
		
	}
	
	private void addDate(){
		
		String s[] = {goods.getText().toString() , danwei.getText().toString() , getText(xiaoliang)
						,getText(daohuo) , getText(jingpin) , getText(tuihuo)
						,getText(qizhong)}; 
		String key[] = {"a","aa","b","c","d","e","f"};
		Map<String, String> map3 = new HashMap<String, String>();
		for(int i = 0 ; i < s.length ; i ++){
			map3.put(key[i], s[i]);
		}
		
		data.add(map3);
		XiaoshouModle itemModle = new XiaoshouModle();
//		itemModle.setID(iD);
		itemModle.setManager_ID(mKehuId);
		itemModle.setManager_Name(mKehuName);
		itemModle.setProduct_ID(mChanpingId);
		itemModle.setProduct_Name(s[0]);
		itemModle.setDepartment_ID(mBumengId);
		itemModle.setDepartment_Name(mBumengname);
		itemModle.setUnit_ID(mdanweiId);
		itemModle.setMember_ID(mKehuId);
		String timeString = time.getText().toString().trim();
		String string = timeString.length() <= 7 ? "month" : "day";
		itemModle.setCycleType(string);
		itemModle.setMember_Name(customer.getText().toString().trim());
		itemModle.setCompany_ID(MyApplication.getInstance().getCompanyID());
		itemModle.setUnit_Name(s[1]);
		itemModle.setQty1(Float.parseFloat(s[2].length() == 0 ? "0" : s[2]));
		itemModle.setQty4(Float.parseFloat(s[6].length() == 0 ? "0" : s[6]));
		itemModle.setQty2(Float.parseFloat(s[3].length() == 0 ? "0" : s[3]));
		itemModle.setQty3(Float.parseFloat(s[4].length() == 0 ? "0" : s[4]));
		itemModle.setQty5(Float.parseFloat(s[5].length() == 0 ? "0" : s[5]));
		itemModle.setBelongDate(getTimeString());
		
		mXiaoshouModles.add(itemModle);  
		
		
		myAdapter = new MyAdapter(XiaoLiang.this);
		listView.setAdapter(myAdapter);
		Common_Xiao.setListViewHeight(listView, myAdapter);
		
		clear();
		
	}
	
	private void clear(){
		
		xiaoliang.setText("");
		daohuo.setText("");
		jingpin.setText("");
		tuihuo.setText("");
		qizhong.setText("");
		
	}
	
	class Ays extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			addDate();
			
			return null;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			myAdapter = new MyAdapter(XiaoLiang.this);
			listView.setAdapter(myAdapter);
//			myAdapter.setData(data);
//			myAdapter.notifyDataSetChanged();
		}
		
	}
	
	private  String getTimeString(){
		Date date = new Date();
		return ("20"+(date.getYear()+"").substring(1, 3))+
				(((date.getMonth()+"").length() + 1) == 1 ? "0"+(date.getMonth()+1) : (date.getMonth()+1))+
				((date.getDate()+"").length() == 1 ? "0"+date.getDate() : date.getDate());
	}
	
	private String getText(EditText tV){
		
		return tV == null ? "" :(tV.getText().toString().length() == 0 ? "0" : tV.getText().toString()); 
		
	}
	
	
	public void bt_submit(View view) {
		if (mXiaoshouModles.size() == 0) {
			showCustomToast("请填写数据", false);
		} else {
			ZJRequest<XiaoshouModle> zjRequest = new ZJRequest<XiaoshouModle>();
			zjRequest.setDatas(mXiaoshouModles);
			L.e("json = " + JsonUtil.toJson(zjRequest));
			mServiceHelp.start(JsonUtil.toJson(zjRequest));
			showNotDialog("正在提交数据...");
		}
		
	}
	
	private void init(){
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();				
			}
		});
		customer.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(XiaoLiang.this, CustomersListActivity.class);
				//选择个客户/人员界面，客户传值“0”，人员传值“1”（注意传String类型）
				intent.putExtra("mode", "0");
				//多选传0，单选传1
				intent.putExtra("select", 1);
				//传递分组名称
				intent.putExtra("groupName", "分类");
				intent.putExtra("Tittle", "选择客户");
				startActivityForResult(intent, 1);				
			}
		});		
		goods.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent1 = new Intent(XiaoLiang.this, CustomersListActivity.class);
				//选择个客户/人员界面，客户传值“0”，人员传值“1”（注意传String类型）
				intent1.putExtra("mode", "2");
				//多选传0，单选传1
				intent1.putExtra("select", 1);
				//传递分组名称
				intent1.putExtra("groupName", "分类");
				intent1.putExtra("Tittle", "产品选择");
				startActivityForResult(intent1, 2);				
			}
		});
		time.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				ArrayList<HashMap<String, Object>> data_time = new ArrayList<HashMap<String, Object>>();
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("name", "月");
				data_time.add(map);
				
				map = new HashMap<String, Object>();				
				map.put("name", "日");
				data_time.add(map);
				
				//传入参数，实现一个借口的回调方法。
				new ShowDialog(XiaoLiang.this, SOURCE_DIALOG, data_time, "选择周期",
						XiaoLiang.this, "请选择周期！",null).customDialog();				
			}
		});
	}
	
	
	private ArrayList<SortModel> arrayList = new ArrayList<SortModel>();//客户数据
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		//获取客户
		if (requestCode == 1 && resultCode == RESULT_OK) {
			Bundle bundle = data.getBundleExtra("bundle");//得到新Activity 关闭后返回的数据
	        arrayList = (ArrayList<SortModel>) bundle.getSerializable("result");//接收泛型
	        if (arrayList.size() > 0) {
	        	customer.setText((arrayList.get(0).getCompany_name()).trim());//用户名称
	        	//客户id
	        	mKehuId = arrayList.get(0).getID();
	        	
	        	//company_ID
	        	mCompanyId = MyApplication.getInstance().getCompanyID();
			}	    
			
	    //获取产品	
		}else if (requestCode == 2 && resultCode == RESULT_OK) {
			Bundle bundle1 = data.getBundleExtra("bundle");
	        arrayList = (ArrayList<SortModel>) bundle1.getSerializable("result");
	        if (arrayList.size() > 0) {
	        	//产品id
	        	mChanpingId = arrayList.get(0).getID();
	        	goods.setText(arrayList.get(0).getCompany_name());//产品名称
				danwei.setText(arrayList.get(0).getProduct_Unit_Name());//产品单位
				mdanweiId = arrayList.get(0).getProduct_Unit_ID();//单位ID
			}	
	        
		}
	}	
	
	private class MyAdapter extends BaseAdapter{
		
		private LayoutInflater inflater;// 用来导入布局

//		private List<Map<String, String>> data
				//	= new ArrayList<Map<String,String>>();
		
		public MyAdapter(Context context) {// 构造器
			this.inflater = LayoutInflater.from(context);
		}
		
//		@SuppressWarnings("unchecked")
//		public void setData(ArrayList<Map<String, String>> datas){
//			data.clear();
//			data.addAll(datas);
////			data = (List<Map<String, String>>) datas.clone();
//		}
		
		public int getCount() {
			System.out.println(data.size()+"数据量");
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View view, ViewGroup parent) {
			
			final ViewHolder holder;
			if (view == null) {
				holder = new ViewHolder();
				view = inflater.inflate(R.layout.list_xiaoliang, null);
				
				holder.chanpin = (TextView) view.findViewById(R.id.chanpin);
				holder.danwei = (TextView) view.findViewById(R.id.danwei);
				holder.xiaoliang = (TextView) view.findViewById(R.id.xiaoliang);
				holder.daohuo = (TextView) view.findViewById(R.id.daohuo);
				holder.jingpin = (TextView) view.findViewById(R.id.jingpin);
				holder.tuihuo = (TextView) view.findViewById(R.id.tuihuo);
				holder.qizhong = (TextView) view.findViewById(R.id.qizhong);
				
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			
			holder.chanpin.setText(data.get(position).get("a"));	
			holder.danwei.setText("(" + data.get(position).get("aa") + ")");
			holder.xiaoliang.setText(data.get(position).get("b"));
			holder.daohuo.setText(data.get(position).get("c"));
			holder.jingpin.setText(data.get(position).get("d"));
			holder.tuihuo.setText(data.get(position).get("e"));
			holder.qizhong.setText(data.get(position).get("f"));
			
			return view;
		}
		
		public final class ViewHolder {// 写这个类是为了点击list的时候好调用view
			public TextView chanpin;
			public TextView danwei;
			public TextView xiaoliang;
			public TextView daohuo;
			public TextView jingpin;
			public TextView tuihuo;
			public TextView qizhong;
		}
	}
	
	
	@Override
	public void onDialogDown(int position, int type) {
		switch (type) {
		case SOURCE_DIALOG:			
			if (position == 0) {
					new Common_Xiao().datePickDialog2(this, time);					
				}else if (position == 1) {
					new Common_Xiao().datePickDialog(this, time);
				}				
			break;

		default:
			break;
		}
	}
	
	private webServiceHelp.OnServiceCallBack<XiaoshouModle> onServiceCallBack = new webServiceHelp.OnServiceCallBack<XiaoshouModle>() {

		public void onServiceCallBack(boolean haveCallBack,
				ZJResponse<XiaoshouModle> zjResponse) {
			// TODO Auto-generated method stub
			
			if (haveCallBack && zjResponse != null) {
				if (zjResponse.getCode() != 0) {
					Message msg = new Message();
					msg.what = HanderUtil.case2;
					msg.obj = zjResponse.getDesc();
					mHandler.sendMessage(msg);
					return;
				}
				Message msg = new Message();
				msg.what = HanderUtil.case1;
				mHandler.sendMessage(msg);
				//提交成功
				XiaoLiang.this.finish();
			} else {
				Message msg = new Message();
				msg.what = HanderUtil.case2;
				msg.obj = "服务器连接失败....";
				mHandler.sendMessage(msg);
			}
			
		}
		
	};
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			dismissDialog();
			switch (msg.what) {
			case HanderUtil.case1:
				//ClueCustomToast.showToast(XiaoLiang.this, R.drawable.toast_sucess, "提交成功");
				showCustomToast("提交成功", true);
				
				break;
			case HanderUtil.case2:
				ClueCustomToast.showToast(XiaoLiang.this, R.drawable.toast_warn, (String) msg.obj);
				break;
			default:
				break;
			}
		};
	};
		
}
