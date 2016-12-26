package com.huishangyun.Channel.Opport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Channel.Clues.ClueCustomToast;
import com.huishangyun.Channel.Clues.ShowDialog;
import com.huishangyun.Channel.Plan.CustomersListActivity;
import com.huishangyun.Channel.Plan.SortModel;
import com.huishangyun.Interface.OnDialogDown;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.manager.EnumManager;
import com.huishangyun.model.Enum;
import com.huishangyun.Channel.Customers.CustomerSet;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.model.Constant;
import com.huishangyun.model.EnumKey;
import com.huishangyun.model.Methods;
import com.huishangyun.yun.R;

public class CreateOpportActivity extends BaseActivity implements OnDialogDown {
	private RelativeLayout back;//返回按钮
	private TextView customer;//客户
	private EditText theme;//主题
	private TextView stage;//阶段
	private EditText money_sum;//金额
	private EditText description;//描述
	private Button submit;//提交
	
	private String Customer;//客户
	private String Theme;//主题
	private int Stage = -1;//阶段
	private String Money;//金额
	private String Note;//描述
	/**创建一个二维数组存储自定义对话框的listview的数据*/
	ArrayList<HashMap<String, Object>> ItemLists = new ArrayList<HashMap<String, Object>>();
	ArrayList<HashMap<String, Object>> ItemLists1 = new ArrayList<HashMap<String, Object>>();
	HashMap<String, Object> map;//定义一个map函数
	private static final int SOURCE_DIALOG = 0;
	private static final String TAG = null; 
	private CreateBusinses list = new CreateBusinses();
	ArrayList<SortModel> arrayList = new ArrayList<SortModel>();
	String sumM = "0.0";
	
	private String titleString;
	private TextView customdialog_sure;//自定义对话框，确定按钮
	private ListView customdialog_listview;//自定义对话框listview
	private TextView customdialoglistview_item_text;//对话框listview的item数据
	private ImageView customdialoglistview_item_img;//自定义对话框图片对勾。
	private ImageView cancel;//自定义对话框顶部取消按钮
	private ImageView mImageView ;//定义一个imageview监听自定义对话框对勾图片地址
	private OnDialogDown mDialogDown;
	private int mPosition = 0;
	private int type;
	private TextView Title;//标题
	private String select_name;
	private int index;
	private String TitleName = null;//头名
	private int Clue_ID;//线索id
	private String content = "";
	private int Member_ID;
	private String MemberName;
	
	private String CompanyName;
	private String ContactName;
	private String telephone;
	private String Address;
	private int resultCode = 1;//商机创建成功的返回码
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_opport_create);
		Intent intent = getIntent();
		TitleName = intent.getStringExtra("name");
		Clue_ID = intent.getIntExtra("Clue_ID", -1);
		content = intent.getStringExtra("Note");
		Member_ID = intent.getIntExtra("Member_ID", -1);
		MemberName = intent.getStringExtra("MemberName");
		if (TitleName.equals("转商机")) {
			CompanyName = intent.getStringExtra("CompanyName");
			ContactName = intent.getStringExtra("ContactName");
			telephone = intent.getStringExtra("telephone");
			Address = intent.getStringExtra("Address");
		}
		
		init();
		
	}
	
	/**
	 * 布局控件实例化
	 */
	private void init() {
		//注册广播
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("OPPORT_DATA");
		intentFilter.setPriority(Integer.MAX_VALUE);
		this.registerReceiver(broadcastReceiver, intentFilter);
		//返回按钮
		back = (RelativeLayout)findViewById(R.id.back);
		//提交按钮
		submit = (Button) findViewById(R.id.create_opport_submit);
		//列表要填写的控件
		customer = (TextView) findViewById(R.id.create_opport_customer);
		theme = (EditText) findViewById(R.id.create_opport_theme);
		stage = (TextView) findViewById(R.id.create_opport_stage);
		money_sum = (EditText) findViewById(R.id.create_opport_money_sum);
		description = (EditText) findViewById(R.id.create_opport_description);
		Title = (TextView) findViewById(R.id.Title);
         if (TitleName != null) {
        	 Title.setText(TitleName);
		  }
         
         if (!content.equals("")) {
        	 description.setText(content);
		}else {
			//不操作
		}
         //客户进来的，直接关联客户
         if (Member_ID != 0 ) {
        	 customer.setText(MemberName);
		}
		//调用单击事件类
		back.setOnClickListener(new ButtonClickListener());
		stage.setOnClickListener(new ButtonClickListener());
		submit.setOnClickListener(new ButtonClickListener());
		customer.setOnClickListener(new ButtonClickListener());
	}
	
	
	/**
	 * 广播接收器
	 */
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			int  MemberID = intent.getIntExtra("MemberID", -1);
			String MemberName = intent.getStringExtra("MemberName");
			customer.setText(MemberName);
			Member_ID = MemberID;
			
		}
	};
	
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
			
			// 返回主页
			case R.id.back:
				finish();
				break;
			case R.id.create_opport_stage:
				ItemLists.clear();
				setItemDate();
				new ShowDialog(CreateOpportActivity.this, SOURCE_DIALOG, ItemLists, "选择阶段", CreateOpportActivity.this,"请选择阶段！",stage.getText().toString().trim()).customDialog();
				break;
			case R.id.create_opport_submit:
//				Log.e(TAG, "进来了");
				Customer = customer.getText().toString().trim();
				Theme = theme.getText().toString().trim();
				Money = money_sum.getText().toString();
				if (!Money.equals("")) {
//					sumM = Double.parseDouble(Money);
					sumM = Money;
				}				
				Note = description.getText().toString().trim();
				
				if (!stage.getText().toString().trim().equals("")) {
					//数据字典查询
					Stage = Integer.parseInt(EnumManager.getInstance(context)
							.getIntKey(stage.getText().toString(),
									EnumKey.ENUM_SALES_STAGE));
				}
				
				mHandler.sendEmptyMessage(1);
				
				break;
				
			//客户跳出选择客户和创建客户选项	
			case R.id.create_opport_customer:
				
				if (TitleName.equals("转商机")) {
					customDialog();
				}else {
					Intent intent = new Intent(CreateOpportActivity.this, CustomersListActivity.class);
					//选择个客户界面，多选传值0，单选传值1（注意传String类型）
					intent.putExtra("mode", "0");
					intent.putExtra("select", 1);
					//传递分组名称
					intent.putExtra("groupName", "分类");
					intent.putExtra("Tittle", "选择客户");
					startActivityForResult(intent, 0);
				}
				
				break;

			default:
				break;
			}
			
		}
		
	}
	/**
	  * 向二维数组赋值
	  */
	private void setItemDate(){
		List<com.huishangyun.model.Enum>  list = new ArrayList<Enum>();
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
	
	private void getNetData(final int ID, final int Clue_ID, final int Member_ID, final int Source, final String Title,
			final String ForecastMoney, final int SalesStage, final String Note, final String ContactName, 
			 final int Manager_ID) {
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String yyjjString = getJson(ID, Clue_ID, Member_ID, Source, Title,
						 ForecastMoney, SalesStage,
						Note, ContactName, Manager_ID);
				Log.e(TAG, "yyjjString:" + yyjjString);
				String result = DataUtil.callWebService(
						Methods.SUPPLY_CREATE,
						getJson(ID, Clue_ID, Member_ID, Source, Title,
								  ForecastMoney, SalesStage,
								Note, ContactName, Manager_ID));			
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
                  showCustomToast("操作失败", false);
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
	private String getJson( int ID,int Clue_ID,int Member_ID, int Source,String Title,
			String ForecastMoney, int SalesStage, String Note, String ContactName, int Manager_ID) {
		
		//添加时Action设为Insert，修改时设为Update      
		list.setAction("Insert");
		list.setID(ID);
		list.setClue_ID(Clue_ID);
		list.setMember_ID(Member_ID);
		list.setSource(Source);
		list.setTitle(Title);
//		list.setFocus(Focus);
//		list.setCompetition(Competition);
//		list.setPrecondition(Precondition);
//		list.setProbability(Probability);
		list.setForecastMoney(ForecastMoney);
		list.setSalesStage(SalesStage);
		list.setNote(Note);
//		list.setContactName(ContactName);
//		list.setManager_Name(ContactName);
//		list.setContactTel(ContactTel);
//		list.setContactJob(ContactJob);
//		list.setManager_ID(Manager_ID);
		list.setManager_ID(Integer.parseInt(preferences.getString(Constant.HUISHANGYUN_UID, "0")));
//		list.setDepartment_ID(preferences.getInt(Constant.HUISHANG_DEPARTMENT_ID, 0));
		ZJRequest<CreateBusinses> zjRequest = new ZJRequest<CreateBusinses>();
		zjRequest.setData(list);
		return JsonUtil.toJson(zjRequest);

	}
	
	/**
	 * 处理创建线索是否成功的交互
	 */
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				// 创建成功
				new ClueCustomToast().showToast(CreateOpportActivity.this,
						R.drawable.toast_sucess, "创建商机成功！");
				CreateOpportActivity.this.setResult(resultCode);
				CreateOpportActivity.this.finish();
				break;
			case 1:
				
				if (isWrite()) {
					
					if (Clue_ID != -1) {
						//提交信息,商机中新建来源传值3
						if (Member_ID==-1) {
							getNetData(0, Clue_ID, arrayList.get(0).getID(), 3, Theme, sumM, Stage, Note, Customer, 0);
						}else {
							getNetData(0, Clue_ID, Member_ID, 3, Theme, sumM, Stage, Note, Customer, 0);
						}

					}else {
						//提交信息,商机中新建来源传值3
						getNetData(0, 0, arrayList.get(0).getID(), 3, Theme, sumM, Stage, Note, Customer, 0);
					}
					
				}

			
				break;
				
			case 2:
				customer.setText(arrayList.get(0).getCompany_name());
			break;
			
			default:
				// 创建失败
				showCustomToast("操作失败", false);
				Log.e(TAG, "失败");
				break;
			}
		};
	};
	
	/**
	 * 显示悬浮框
	 */
	public void customDialog(){
		ItemLists1.clear();
		index = -1;
		setItemDate();
		LayoutInflater layoutInflater = LayoutInflater
				.from(CreateOpportActivity.this);
		View  customdialog= layoutInflater.inflate(R.layout.activity_clue_new_customdialog, null);
		
        //创建一个对话框对象
		final AlertDialog alertDialog = new AlertDialog.Builder(
				CreateOpportActivity.this).create();
		//设置对话框背景颜色
		alertDialog.setIcon(R.color.white);
		//设置对话显示位置
		alertDialog.setView(customdialog, -1, -1, 0, -1);
		alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//自定义对话框确定按钮
		customdialog_sure = (TextView) customdialog.findViewById(R.id.customdialog_sure);
		customdialog_listview =(ListView) customdialog.findViewById(R.id.customdialog_listview);
		TextView dialog_titlestr = (TextView) customdialog.findViewById(R.id.dialog_titlestr);
		dialog_titlestr.setText("选择客户来源");
		cancel =(ImageView) customdialog.findViewById(R.id.cancel);
		//这个控件本身没有用，只是用来过渡第一次按键效果，使mImageView.setVisibility(View.INVISIBLE);不会报空指针。
		mImageView =(ImageView) customdialog.findViewById(R.id.cancel_nice);
		alertDialog.show();
		DialogAdapter dialog_listview_adapter = new DialogAdapter(CreateOpportActivity.this);
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
					Intent intent = new Intent(CreateOpportActivity.this, CustomersListActivity.class);
					//选择个客户/人员界面，客户传值“0”，人员传值“1”（注意传String类型）
					intent.putExtra("mode", "0");
					//多选传0，单选传1
					intent.putExtra("select", 1);
					//传递分组名称
					intent.putExtra("groupName", "分类");
					intent.putExtra("Tittle", "选择客户");
					startActivityForResult(intent, 0);
					alertDialog.cancel();
					
				}else if (index==1) {
					
					//跳转到创建客户界面
					Intent intent = new Intent(CreateOpportActivity.this, CustomerSet.class);
					intent.putExtra("flage", "shangji");
					intent.putExtra("CompanyName", CompanyName);//公司
					intent.putExtra("ContactName", ContactName);//联系人
					intent.putExtra("telephone", telephone);//电话
					intent.putExtra("Address", Address);//地址
					startActivity(intent);
					alertDialog.cancel();
				}else if (index==-1) {
					showCustomToast("请选择客户来源！", false);
				}
				
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
		if (resultCode==RESULT_OK) {
		
    	System.out.println("resultCode=="+resultCode+"requestCode=="+requestCode);
        Bundle bundle = data.getBundleExtra("bundle");//得到新Activity 关闭后返回的数据
        arrayList = (ArrayList<SortModel>) bundle.getSerializable("result");//接收泛型
        if (arrayList.size()!=0) {
        	 mHandler.sendEmptyMessage(2);
		}
		}
	}
	
	/**
	 * 判断是否输入完成
	 * @return 
	 */
	private boolean isWrite(){
		
		if (Customer.equals("") ) {
			showDialog("请选择客户！");
			return false;
		}
		if (Theme.equals("") ) {
			showDialog("请填写主题！");
			return false;
		}
		if (Stage==-1) {
			showDialog("请选择阶段！");
			return false;
		}
		if (Money.equals("")) {
			showDialog("请填写金额！");
			return false;
		}
		if (Note.equals("")) {
			showDialog("请填写描述内容！");
			return false;
		}
		return true;
		
		
	}
	
	public void showDialog(String TXT){
		// Toast
		new ClueCustomToast().showToast(CreateOpportActivity.this,
				R.drawable.toast_warn, TXT);

	}
}
