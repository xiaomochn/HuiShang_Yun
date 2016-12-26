package com.huishangyun.Channel.Orders;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.model.Order_address;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.View.ProgressBar_Loading;
import com.huishangyun.manager.AreaManager;
import com.huishangyun.model.AreaModel;
import com.huishangyun.model.Methods;
import com.huishangyun.yun.R;

public class AddressNewActivity extends BaseActivity {
	private LinearLayout back;
	private RelativeLayout sheng;
	private TextView text_sheng;
	private EditText edt_name, edt_phone, edt_tel, edt_address;
	/**
	 * 对话框的控件
	 */
	private ListView listView;
	private ImageView cacel, lizi;
	private Button confirm;
	private TextView text,title;
	private int widths, heights;
	private String address1, address2, address3;
	private String shengshixian;
	private List<Order_address> data_list = new ArrayList<Order_address>();
	
	private int ID = -1, ID2 = -1, ID3 = -1;
	
	private List<AreaModel> data = new ArrayList<AreaModel>();
	private List<AreaModel> data2 = new ArrayList<AreaModel>();
	private DialogAdapter dialogAdapter;
    private SimpleAdapter adapter_sheng, adapter_shi, adapter_xian;
    private int Member_ID;
    /**
     * 记录当下点击的项，第一次为-1，就不会有默认选中项
     */
    private int index = -1;
    private Intent intent;
    
    private Handler myHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HanderUtil.case5:
				ProgressBar_Loading.dismiss(AddressNewActivity.this);
				showCustomToast("操作成功", true);
				ID = -1;
				ID2 = -1; 
				ID3 = -1;	
				finish();
				break;
			case HanderUtil.case2:
				ProgressBar_Loading.dismiss(AddressNewActivity.this);
				showCustomToast((String) msg.obj, false);
				break;
								
			default:
				break;
			}
		};
	};
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_addressnew);
		
		if (getIntent().getStringExtra("DINGTUI").equals("TUI")) {
			 text = (TextView)findViewById(R.id.text);
			 text.setText("退货信息");
		}
		Member_ID = Integer.parseInt(getIntent().getStringExtra("Member_ID"));
		// 获得手机的宽度和高度
        DisplayMetrics dm = new DisplayMetrics();
 		getWindowManager().getDefaultDisplay().getMetrics(dm);
 		widths = dm.widthPixels;
 		heights = dm.heightPixels;
 		
 		dialogAdapter = new DialogAdapter(this);
 		edt_name = (EditText)findViewById(R.id.edt_name);
		edt_phone = (EditText)findViewById(R.id.edt_phone);
		edt_tel = (EditText)findViewById(R.id.edt_tel);
		edt_address = (EditText)findViewById(R.id.edt_address);
		text_sheng = (TextView)findViewById(R.id.text_sheng);
		
		init();
		//修改的地址
		intent = getIntent();
		L.e("flage:" + intent.getStringExtra("flage"));
		if (intent.getStringExtra("flage").equals("GAI")) {
			edt_name.setText(intent.getStringExtra("name"));
			edt_phone.setText(intent.getStringExtra("moble"));
			edt_tel.setText(intent.getStringExtra("tel"));
			
			ID3 = intent.getIntExtra("Area_ID", 0);
			if (ID3 > 0) {
				shengshixian = AreaManager.getInstance(AddressNewActivity.this)
						.getAddress(ID3);			
//				text_sheng.setText(shengshixian);详情不显示省市县
			}			
			edt_address.setText(intent.getStringExtra("adderss"));
			
			L.e("获取到的id：" + intent.getIntExtra("Area_ID", 0));
			L.e("获取到的地址：" + shengshixian);
			
//			ID3 = intent.getStringExtra("Area_ID");			
//			ID = Integer.parseInt(intent.getStringExtra("shengID"));//修改的时候要获取该条数据的省市县的ID	
//			ID2 = Integer.parseInt(intent.getStringExtra("shiID"));			
		}
	}
			
	private void init(){				
		back = (LinearLayout)findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		sheng = (RelativeLayout)findViewById(R.id.sheng);
		sheng.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				data2.clear();	
				//获取省
				data2 = AreaManager.getInstance(AddressNewActivity.this).getAreas(0, false);
				data.clear();
				for (int i = 0; i < data2.size(); i++) {
					data.add(data2.get(i));
				}
				createAlertDialog();			
				title.setText("选择省份 :");
			}
		});		
	}
	
	public void submit(View view) {		
		if (edt_name.getText().toString().trim().equals("")) {
			showCustomToast("请输入收货人", false);
			
		}else if (edt_phone.getText().toString().trim().equals("")) {
			showCustomToast("请输入手机号码", false);
			
		}else if (text_sheng.getText().toString().trim().equals("（必填）")) {
			showCustomToast("请选择省市县", false);
			
		}else if (edt_address.getText().toString().trim().equals("")) {
			showCustomToast("请输入详细地址", false);
			
		}else {
			ProgressBar_Loading.showProgressBar(AddressNewActivity.this, true, "Loading....");
			if (intent.getStringExtra("flage").equals("GAI")) {
				addAdress(Integer.parseInt(intent.getStringExtra("id")), "Update");
				
			}else {
				addAdress(0, "Insert");
			}			
		}		
	}
	
			
	//创建选择对话框方法
	private void createAlertDialog(){
		//对话框对象		 
		final AlertDialog alertDialog;
		
		LayoutInflater layoutInflater = LayoutInflater.from(AddressNewActivity.this);
		View view = layoutInflater.inflate(R.layout.alertdialog_address, null);
		
		alertDialog = new AlertDialog.Builder(AddressNewActivity.this).create();		
		//alertDialog.setIcon(Color.parseColor("#FFFFFF"));
		alertDialog.setView(view, 0, 0, 0, 0);
		//alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		          		
		title = (TextView) view.findViewById(R.id.title);

		cacel = (ImageView) view.findViewById(R.id.cancle);
		cacel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				alertDialog.dismiss();
			}
		});
		
		listView = (ListView) view.findViewById(R.id.listview);
		listView.setAdapter(dialogAdapter);
						
		//确定按钮
		confirm = (Button)view.findViewById(R.id.enter);	
		confirm.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				edt_address.setText("");//点击选择省市县确认就清空下面的详细地址。
				
				//防止上个页面选中的项传到下一个，每次确定的时候都清-1
				index = -1;
				//判断是哪层选择的界面
				if (title.getText().toString().trim().equals("选择省份 :")) {
					//防止不选择直接确定的操作
					if (address1 != null && !address1.equals("")) {
						
						data2.clear();
						data.clear();
						//获取市
						data2 = AreaManager.getInstance(AddressNewActivity.this).getAreas(ID, false);						
						for (int i = 0; i < data2.size(); i++) {
							data.add(data2.get(i));
						}
						
						dialogAdapter.notifyDataSetChanged();
						title.setText("选择城市 :");
												
					}else {
						showCustomToast("请选择省份", false);
					}
										
				}else if(title.getText().toString().trim().equals("选择城市 :")){
					if (address2 != null && !address2.equals("")) {
						
						data2.clear();
						data.clear();
						//获取县
						data2 = AreaManager.getInstance(AddressNewActivity.this).getAreas(ID2, false);
						if (data2.size() > 0) {//排除没有县级单位的
							for (int i = 0; i < data2.size(); i++) {
								data.add(data2.get(i));
							}
							dialogAdapter.notifyDataSetChanged();
							title.setText("选择区县 :");
						}else {
							alertDialog.dismiss();
							text_sheng.setText(address1 + address2);
						}
						
					}else {
						showCustomToast("请选择市", false);
						
					}
										
				}else if(title.getText().toString().trim().equals("选择区县 :")){
					if (address3 != null && !address3.equals("")) {
						
						alertDialog.dismiss();
						text_sheng.setText(address1 + address2 + address3);
					
					}else {
						showCustomToast("请选择县", false);
						
					}					
				}																
			}
		});
		
		alertDialog.show();	
		//不设置固定的大小，默认居中显示
//		alertDialog.getWindow().setLayout((int)(widths* 0.85), (int)(heights*0.85));//宽高  
	}
	
	/**
	 * 省市县的Adapter
	 * @author Administrator
	 *
	 */
	public class DialogAdapter extends BaseAdapter{		 
	    private LayoutInflater mInflater;
	    	   
		public DialogAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}			

		public int getCount() {
			//判断是哪个界面来返回不同的数据长度
			if (title.getText().toString().trim().equals("选择省份 :")) {
				return data.size();
				
			}else if (title.getText().toString().trim().equals("选择城市 :")) {
				return data.size();
				
			}else if (title.getText().toString().trim().equals("选择区县 :")) {
				return data.size();
			}
			return 0;	
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View view, ViewGroup parent) {
			// TODO Auto-generated method stub				
			final ViewHolder holder;
			if (view == null) {
				holder = new ViewHolder();
				view = mInflater.inflate(R.layout.list_shengshi, null);
				
				holder.tv = (TextView) view.findViewById(R.id.tv);
				holder.img = (ImageView) view.findViewById(R.id.img);
				
				view.setTag(holder);
			}else {
				holder = (ViewHolder) view.getTag();
			}
			
			//判断是哪个页面再加载相应的数据，图片都是一样的
			if (title.getText().toString().trim().equals("选择省份 :")) {
				holder.tv.setText(data.get(position).getName());
				
			}else if (title.getText().toString().trim().equals("选择城市 :")) {
				holder.tv.setText(data.get(position).getName());
				
			}else if (title.getText().toString().trim().equals("选择区县 :")) {
				holder.tv.setText(data.get(position).getName());
			}			
			holder.img.setImageResource(R.drawable.gou);
			
			//getview从position=0开始加载数据，index默认是-1所以第一次加载都是未选中的
			if (index == position) {
				holder.img.setVisibility(View.VISIBLE);
				
			}else {			
				holder.img.setVisibility(View.INVISIBLE);
			}
			
			//lsitview点击方法是可以再getview里面进行的
			listView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
									
					//点击后这项为选中状态，把这个值赋给index，通知数据改变，然后下面再getview
					holder.img.setVisibility(View.VISIBLE);
					index = position;
					notifyDataSetChanged();
					
					//获取到点击项的数据。
					if (title.getText().toString().trim().equals("选择省份 :")) {
						
						address1 = data.get(position).getName();
						ID = data.get(position).getID();
						L.e("ID:" + ID);
						L.e("address1:" + address1);
					}else if (title.getText().toString().trim().equals("选择城市 :")) {
						address2 = data.get(position).getName();
						ID2 = data.get(position).getID();						
						L.e("ID2:" + ID2);
						L.e("address2:" + address2);
					}else if(title.getText().toString().trim().equals("选择区县 :")){
						address3 = data.get(position).getName();	
						ID3 = data.get(position).getID();
						L.e("点击的县ID3:" + ID3);
						L.e("address3:" + address3);
					}
				}
			});
			return view;
		}
		
		public class ViewHolder{
			public TextView tv;
			public ImageView img;
		}
	 }
	
	
	/**
	 * 创建地址
	 * @param ID客户ID
	 * @param Action添加和修改的时候传不同
	 */	
	public void addAdress(int id, String Action){
		    
		//封装进Insert的
		ZJRequest<Order_address> zjRequest = new ZJRequest<Order_address>();
		Order_address order_address = new Order_address();
		
		order_address.setAction(Action);
		order_address.setMember_ID(Member_ID);
		order_address.setName(edt_name.getText().toString());
		order_address.setMobile(edt_phone.getText().toString());
		order_address.setTel(edt_tel.getText().toString());
		//创建地址的时候把省市县都放入详细地址储存，显示详情的时候省市县不显示，重选省市县下面的详细地址置空。
		order_address.setAddress(text_sheng.getText().toString() + edt_address.getText().toString());//text_sheng.getText().toString() + 
		order_address.setIsDefault(true);//将新增的地址设为默认地址
		order_address.setID(id);
		
		if (ID3 == -1) {//判断有没有县级单位
			order_address.setArea_ID(ID2);
			L.e("生成的市ID2:" + ID2);
		}else {
			order_address.setArea_ID(ID3);
			L.e("生成的县ID3:" + ID3);
		}				
//		order_address.setSiteData_ProvinceID(ID);
//		order_address.setSiteData_CityID(ID2);
//		order_address.setSiteData_AreaID(ID3);
				
		zjRequest.setData(order_address);
		final String json = JsonUtil.toJson(zjRequest);
	    L.e("(添加地址)json:" + json);
	    			    
		new Thread(){
			public void run() {
				String jsonString = DataUtil.callWebService(Methods.ORDER_ADDRESS_ADD, json);
				L.e("(添加地址)jsonString:" + jsonString);
				try {										
					if (jsonString != null) {
						Type type = new TypeToken<ZJResponse<Order_address>>(){}.getType();
					    ZJResponse zjResponse = JsonUtil.fromJson(jsonString, type);
					    
					    Message message = new Message();
					    if (zjResponse.getCode() == 0) {					    	
					    	message.what = HanderUtil.case5;
						    myHandler.sendMessage(message);
						    
						} else {								
						    message.what = HanderUtil.case2;
						    message.obj = zjResponse.getDesc();
						    myHandler.sendMessage(message);
						}
					}		
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
	}
				
	
}
