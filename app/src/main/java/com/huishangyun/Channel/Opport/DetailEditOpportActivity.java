package com.huishangyun.Channel.Opport;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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
import android.widget.TextView;

import com.huishangyun.Channel.Plan.SortModel;
import com.huishangyun.manager.EnumManager;
import com.huishangyun.model.*;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Channel.Clues.ClueCustomToast;
import com.huishangyun.Channel.Clues.ShowDialog;
import com.huishangyun.Channel.Plan.CustomersListActivity;
import com.huishangyun.Interface.OnDialogDown;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.model.Enum;
import com.huishangyun.model.EnumKey;
import com.huishangyun.yun.R;

public class DetailEditOpportActivity extends BaseActivity implements OnDialogDown {
	private RelativeLayout back;//返回按钮
	private TextView customer;// 客户
	private EditText theme;// 主题
	private TextView stage;// 阶段
	private EditText money_sum;// 金额
	private EditText describe;// 描述
	private Button submit;// 提交按钮
	private static final int SOURCE_DIALOG = 0;
	private static final String TAG = null;
	/**创建一个二维数组存储自定义对话框的listview的数据*/
	ArrayList<HashMap<String, Object>> ItemLists = new ArrayList<HashMap<String, Object>>();
	ArrayList<HashMap<String, Object>> ItemLists1 = new ArrayList<HashMap<String, Object>>();
	HashMap<String, Object> map;//定义一个map函数
	ArrayList<SortModel> arrayList = new ArrayList<SortModel>();
	private CreateBusinses list = new CreateBusinses();
	private int ID;
	private String Customer;//客户
	private String Theme;//主题
	private String Stage;//阶段
	private String Money_Sum;//金额
	private String Describe;//描述
	
	private String titleString;
	private LinearLayout customdialog_sure;//自定义对话框，确定按钮
	private ListView customdialog_listview;//自定义对话框listview
	private TextView customdialoglistview_item_text;//对话框listview的item数据
	private ImageView customdialoglistview_item_img;//自定义对话框图片对勾。
	private ImageView cancel;//自定义对话框顶部取消按钮
	private ImageView mImageView ;//定义一个imageview监听自定义对话框对勾图片地址
	private OnDialogDown mDialogDown;
	private int mPosition = 0;
	private int type;
	
	private String select_name;
	private int index;
	private int Menber_ID;
	private String flag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		String id = intent.getStringExtra("ID");
		ID = Integer.parseInt(id) ;
		Customer = intent.getStringExtra("customer");
		Theme = intent.getStringExtra("theme");
		Stage = intent.getStringExtra("stage");
		Money_Sum = intent.getStringExtra("money_sum");
		Describe = intent.getStringExtra("describe");
		Menber_ID = intent.getIntExtra("Menber_ID", 0);
		flag = MainOpportOrdinaryEntryActivity.flag;
		setContentView(R.layout.activity_opport_detail_edit);
		init();
		
		
	}

	/**
	 * 布局控件实例化
	 */
	private void init() {
		back = (RelativeLayout) findViewById(R.id.back);
		customer = (TextView) findViewById(R.id.edit_opport_customer);
		theme = (EditText) findViewById(R.id.edit_opport_theme);
		stage = (TextView) findViewById(R.id.edit_opport_stage);
		money_sum = (EditText) findViewById(R.id.edit_opport_money_sum);
		describe = (EditText) findViewById(R.id.edit_opport_description);
		submit = (Button) findViewById(R.id.edit_opport_submit);
		
		back.setOnClickListener(new ButtonClickListener());
		stage.setOnClickListener(new ButtonClickListener());
		customer.setOnClickListener(new ButtonClickListener());
		submit.setOnClickListener(new ButtonClickListener());
		
		//初始化表格内容
		customer.setText(Customer);
		theme.setText(Theme);
		stage.setText(Stage);
		money_sum.setText(backPrice(Double.parseDouble(Money_Sum)));
		describe.setText(Describe);
	}
	
	/**
	 * 单击事件处理
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
				//选择阶段
			case R.id.edit_opport_stage:
				ItemLists.clear();
				setItemDate();
				new ShowDialog(DetailEditOpportActivity.this, SOURCE_DIALOG,
						ItemLists, "选择阶段", DetailEditOpportActivity.this,"请选择阶段！",stage.getText().toString().trim())
						.customDialog();
				break;
				//选择客户
			case R.id.edit_opport_customer:
				
				Intent intent = new Intent(DetailEditOpportActivity.this, CustomersListActivity.class);
				//选择个客户界面，多选传值0，单选传值1（注意传String类型）
				intent.putExtra("mode", "0");
				intent.putExtra("select", 1);
				//传递分组名称
				intent.putExtra("groupName", "分类");
				intent.putExtra("Tittle", "选择客户");
				startActivityForResult(intent, 0);
				
//				customDialog();
//				Intent intent = new Intent(DetailEditOpportActivity.this, CustomersListActivity.class);
//				//选择个客户界面，多选传值0，单选传值1（注意传String类型）
//				intent.putExtra("mode", "0");
//				intent.putExtra("select", 1);
//				//传递分组名称
//				intent.putExtra("groupName", "分类");
//				intent.putExtra("Tittle", "选择人员");
//				startActivityForResult(intent, 0);
				
				break;
			case R.id.edit_opport_submit:
				String forecastMoney;
				String Customer = customer.getText().toString();
				String Tittle = theme.getText().toString();
				int SalesStage = backStage(stage.getText().toString());
				if (!(money_sum.getText().toString()).equals("")) {
					forecastMoney = money_sum.getText().toString();
				}else {
					forecastMoney = "0.0";
				}				
				String Note = describe.getText().toString();
				//假如客户没选，则memberid用原先的。
				if (arrayList.size()>0) {
					Menber_ID = arrayList.get(0).getID();
				}
				if (isWrite()) {
					getNetData(ID, Menber_ID, Tittle, SalesStage, forecastMoney, Note);
				}
				
				break;
			default:
				break;
			}
		}
		
	}
	
	private void getNetData(final int ID,  final int customer,
			 final String Title,  final int SalesStage, 
			 final String forecastMoney,final String Note) {
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String yyjjString = getJson(ID, customer, Title, SalesStage, forecastMoney, Note);
				Log.e(TAG, "yyjjString:" + yyjjString);
				String result = DataUtil.callWebService(
						Methods.SUPPLY_EDIT,
						getJson(ID, customer, Title, SalesStage, forecastMoney, Note));			
				//先判断网络数据是否获取成功，防止网络不好导致程序崩溃
				if (result != null) {
					//json解析返回结果
					try {
						JSONObject jsonObject = new JSONObject(result);
						Log.e(TAG, "code:" + jsonObject.getInt("Code"));
						int code = jsonObject.getInt("Code");
						Message msg =  mHandler.obtainMessage();
						msg.what = code;
						mHandler.sendMessage(msg);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}											
						
				} else {
//					Toast.makeText(ModifyClueActivity.this, "没有更多数据！", Toast.LENGTH_SHORT).show();
				}
				
			}
		}.start();
	}
	
	/**
	 * 设置json对象
	 * @paramCompany_ID 公司id
	 * @paramManager_ID 用户编号
	 * @paramDepartment_ID 部门编号
	 * @paramkeywords 搜索关键字
	 * @parampageIndex 页码
	 * @return
	 */
	private String getJson( int ID,  int customer,
			 String Title,  int SalesStage, 
			 String forecastMoney,String Note
			) {
		
		//添加时Action设为Insert，修改时设为Update      
		list.setAction("Update");
		list.setID(ID);
		list.setTitle(Title);//主题
		list.setSalesStage(SalesStage);//阶段
		list.setForecastMoney(forecastMoney);//金额
		list.setNote(Note);//描述
		list.setMember_ID(customer);//客户
		list.setManager_ID(Integer.parseInt(preferences.getString(Constant.HUISHANGYUN_UID, "0")));
		list.setDepartment_ID(preferences.getInt(Constant.HUISHANG_DEPARTMENT_ID, 0));
		ZJRequest zjRequest = new ZJRequest();
		zjRequest.setData(list);
		return JsonUtil.toJson(zjRequest);

	}
	
	/**
	 * 异步处理数据，实现提交数据的交互操作
	 */
	public Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				new ClueCustomToast().showToast(DetailEditOpportActivity.this, R.drawable.toast_sucess, "修改成功 !");				
//				Intent intent = new Intent(DetailEditOpportActivity.this, DetailOpportActivity.class);
//				//传值
//				intent.putExtra("ID", ID + "");
//				startActivity(intent);
//				finish();
				if (preferences.getString(Constant.HUISHANG_TYPE, "0").equals("1")) {
					Intent intent = new Intent(DetailEditOpportActivity.this,MainOpportOrdinaryEntryActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra("Member_ID", Menber_ID+"");
					intent.putExtra("falge", flag);
					startActivity(intent);
				}else {
					Intent intent = new Intent(DetailEditOpportActivity.this,MainOpportActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
				
				break;
				
			case 1:
				customer.setText(arrayList.get(0).getCompany_name());
				break;

			default:
				break;
			}
		};
	};
	
	 /**
	  * 向二维数组赋值
	  */
	private void setItemDate(){
		
		
		List<Enum>  list = new ArrayList<com.huishangyun.model.Enum>();
		//查询全部
		list = EnumManager.getInstance(context).getAllEnums(EnumKey.ENUM_SALES_STAGE);
		String[] list1 = {"选择客户", "创建客户"};
		for (int i = 0; i < list.size(); i++) {
			map = new HashMap<String, Object>();
			map.put("name", list.get(i).getLab());
			ItemLists.add(map);
			Log.e(TAG,"ItemLists.SIZE:" +  ItemLists.size());
		}
		
		for (int i = 0; i < list1.length; i++) {
			map = new HashMap<String, Object>();
			map.put("name", list1[i]);
			ItemLists1.add(map);
			Log.e(TAG,"ItemLists1.SIZE:" +  ItemLists1.size());
		}
		
//		String[] list = {"初期沟通", "立项跟踪","呈报方案"};
//		String[] list1 = {"选择客户", "创建客户"};
//		for (int i = 0; i < list.length; i++) {
//			map = new HashMap<String, Object>();
//			map.put("name", list[i]);
//			ItemLists.add(map);
//			Log.e(TAG,"ItemLists.SIZE:" +  ItemLists.size());
//		}
//		
//		for (int i = 0; i < list1.length; i++) {
//			map = new HashMap<String, Object>();
//			map.put("name", list1[i]);
//			ItemLists1.add(map);
//			Log.e(TAG,"ItemLists1.SIZE:" +  ItemLists1.size());
//		}
		
	}

	@Override
	public void onDialogDown(int position, int type) {
		// TODO Auto-generated method stub
		switch (type) {
		case SOURCE_DIALOG:
			HashMap<String, Object> map = ItemLists.get(position);
			stage.setText((CharSequence) map.get("name"));
			break;

		default:
			break;
		}
		
	}
	/**
	 * 返回阶段数字代码
	 * @param index
	 * @return
	 */
	private int backStage(String index){
		
		if (!index.equals("")) {
			//数据字典查询
			
		return Integer.parseInt(EnumManager.getInstance(context)
					.getIntKey(index,
							EnumKey.ENUM_SALES_STAGE));
		}
		
		return (Integer) null;
		
	}
	
	/**
	 * 显示悬浮框
	 */
	@SuppressLint("WrongViewCast") public void customDialog(){
		ItemLists1.clear();
		setItemDate();
		LayoutInflater layoutInflater = LayoutInflater
				.from(DetailEditOpportActivity.this);
		View  customdialog= layoutInflater.inflate(R.layout.activity_clue_new_customdialog, null);
		
        //创建一个对话框对象
		final AlertDialog alertDialog = new AlertDialog.Builder(
				DetailEditOpportActivity.this).create();
		//设置对话框背景颜色
		alertDialog.setIcon(R.color.white);
		//设置对话显示位置
		alertDialog.setView(customdialog, -1, -1, 0, -1);
		alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//自定义对话框确定按钮
		customdialog_sure = (LinearLayout) customdialog.findViewById(R.id.customdialog_sure);
		customdialog_listview =(ListView) customdialog.findViewById(R.id.customdialog_listview);
		cancel =(ImageView) customdialog.findViewById(R.id.cancel);
		//这个控件本身没有用，只是用来过渡第一次按键效果，使mImageView.setVisibility(View.INVISIBLE);不会报空指针。
		mImageView =(ImageView) customdialog.findViewById(R.id.cancel_nice);
		alertDialog.show();
		DialogAdapter dialog_listview_adapter = new DialogAdapter(DetailEditOpportActivity.this);
		customdialog_listview.setAdapter(dialog_listview_adapter);
		
		//listview的item单击事件
		customdialog_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub	
				
				ImageView img = (ImageView) view.findViewById(R.id.customdialoglistview_item_img);
				TextView textView = (TextView) view.findViewById(R.id.customdialoglistview_item_text);
				index = position;
				Log.e(TAG, "index:" + index );
				//判断控件是否隐藏
				switch (img.getVisibility()) {
//				case View.VISIBLE:					
//					img.setVisibility(View.INVISIBLE);						
//					break;
					
				case View.INVISIBLE:					
					mImageView.setVisibility(View.INVISIBLE);
					img.setVisibility(View.VISIBLE);
					//设置文字
					//mtextview = textView.getText().toString();
					mPosition = position;
					mImageView = img;
					
					break;
				default:
					break;
				}

			}			
		});
		
		//自定义单击事件，确定按钮单击事件
		customdialog_sure.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//设置文字
				//source.setText(mtextview);
//				mDialogDown.onDialogDown(mPosition, type);
//				alertDialog.cancel();
				if (index==0) {
					Intent intent = new Intent(DetailEditOpportActivity.this, CustomersListActivity.class);
					//选择个客户界面，多选传值0，单选传值1（注意传String类型）
					intent.putExtra("mode", "0");
					intent.putExtra("select", 1);
					//传递分组名称
					intent.putExtra("groupName", "分类");
					intent.putExtra("Tittle", "选择客户");
					startActivityForResult(intent, 0);
					
				}else if (index==1) {
					
					//跳转到创建客户界面
				}
				alertDialog.cancel();
			}
		});
		
		//取消按钮单击事件
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alertDialog.cancel();
			}
		});
	}
	
	/**
	 * 自定义对话框lsitview适配器
	 * @author XSL
	 *
	 */
	 private class DialogAdapter extends BaseAdapter{
		 
		 private LayoutInflater mInflater;// 动态布局映射

			public DialogAdapter(Context context) {
				this.mInflater = LayoutInflater.from(context);
			}			

			// 决定listview显示条数
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return ItemLists1.size();
			}

			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return position;
			}

			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				convertView = mInflater.inflate(R.layout.activity_clue_new_customdialog_listviewitem, null);
				customdialoglistview_item_text = (TextView)convertView.findViewById(R.id.customdialoglistview_item_text);
				customdialoglistview_item_img = (ImageView)convertView.findViewById(R.id.customdialoglistview_item_img);
				customdialoglistview_item_text.setText((String) ItemLists1.get(position).get("name"));
				return convertView;
			}
	 }	
	 
	
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {	
    	System.out.println("resultCode=="+resultCode+"requestCode=="+requestCode);
        Bundle bundle = data.getBundleExtra("bundle");//得到新Activity 关闭后返回的数据
        arrayList = (ArrayList<SortModel>) bundle.getSerializable("result");//接收泛型
        if (arrayList.size()!=0) {
        	 mHandler.sendEmptyMessage(1);
		}
	  }
	}
	
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
	 * 判断是否输入完成
	 * @return 
	 */
	private boolean isWrite(){
		
		if (customer.getText().toString().trim().equals("") ) {
			showDialog("请选择客户！");
			return false;
		}
		if (theme.getText().toString().trim().equals("") ) {
			showDialog("请填写主题！");
			return false;
		}
		if (stage.getText().toString().trim().equals("")) {
			showDialog("请选择阶段！");
			return false;
		}
		if (money_sum.getText().toString().trim().equals("")) {
			showDialog("请填写金额！");
			return false;
		}
		if (describe.getText().toString().trim().equals("")) {
			showDialog("请填写描述内容！");
			return false;
		}
		return true;
		
		
	}
	
	public void showDialog(String TXT){
		// Toast
		new ClueCustomToast().showToast(DetailEditOpportActivity.this,
				R.drawable.toast_warn, TXT);

	}
	
	
}
