package com.huishangyun.Channel.Clues;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huishangyun.Interface.OnDialogDown;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.manager.EnumManager;
import com.huishangyun.model.Enum;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.model.Constant;
import com.huishangyun.model.EnumKey;
import com.huishangyun.model.Methods;
import com.huishangyun.yun.R;

public class NewClueActivity extends BaseActivity implements OnDialogDown {
	private static final String TAG = null;
	private RelativeLayout back;//返回线索主页按钮	
	private TextView submit;//提交按钮
	String netbackresult;//新增线索添加返回结果	
	String jsondata;//json返回添加成功或失败数据
	private EditText company_name;//公司名称
	private EditText contact_Name;//联系人
	private EditText telephone_number;//输入电话号码
	private EditText address;//地址
	private TextView source;//来源填写按钮
	private EditText note;//内容
	private LinearLayout customdialog_sure;//自定义对话框，确定按钮
	ListView customdialog_listview;//自定义对话框listview
	private TextView customdialoglistview_item_text;//对话框listview的item数据
	private ImageView customdialoglistview_item_img;//自定义对话框图片对勾。
	private ImageView cancel;//自定义对话框顶部取消按钮
	
	/**创建一个二维数组存储自定义对话框的listview的数据*/
	ArrayList<HashMap<String, Object>> ItemLists = new ArrayList<HashMap<String, Object>>();	
	HashMap<String, Object> map;//定义一个map函数
	private ImageView mImageView ;//定义一个imageview监听自定义对话框对勾图片地址
	private String mtextview;//监听对话框item选定文字
	
	private int ClueID; //线索ID（新增则传0）
	private int Company_ID;//公司id
	private String Source;//业务来源类型（1：手机客户端；2：网站后台）
	private String Title;//标题
	private String Company;//公司名称
	private String Name;//姓名
	private String Tel;//固定电话
	private String Mobile;//手机号码
	private String QQ;//时通讯软件账号
	private String Email;//Email
	private String Address;//地址
	private String Note;//内容 
	private int Manager_ID;//提交人编号
	private String Manager_Name;//提交人姓名
	private static final int SOURCE_DIALOG = 0; 
	private CreateClue list = new CreateClue();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clue_new);
		init();
	}

	
	/**
	 * 布局加载及初始化
	 */
	private void init(){
		back = (RelativeLayout)findViewById(R.id.back);		
		submit = (TextView)findViewById(R.id.submit);
		
		company_name = (EditText)findViewById(R.id.company_name);
		contact_Name = (EditText)findViewById(R.id.contact_Name);
		telephone_number = (EditText)findViewById(R.id.telephone_number);
		address = (EditText)findViewById(R.id.address);
		source = (TextView)findViewById(R.id.source);		
		note = (EditText)findViewById(R.id.note);
		
		
		
		
		back.setOnClickListener(new ButtonClickListener());
		submit.setOnClickListener(new ButtonClickListener());
		telephone_number.getText().toString();
		source.setOnClickListener(new ButtonClickListener());
	}
	
	
	
	/**
	 * 单击事件处理
	 * @author Administrator
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
			case R.id.submit:
				//先进行判断和提示再提交
				Company = company_name.getText().toString().trim();
				Name = contact_Name.getText().toString().trim();
				Mobile = telephone_number.getText().toString().trim();
				Address = address.getText().toString();
				//数据字典查询对应状态值
			    Source = EnumManager.getInstance(context).getIntKey(source.getText().toString(), EnumKey.ENUM_CLUE_SOURCE);
				
				Note = note.getText().toString().trim();
				
				Log.e(TAG, "Company:" + Company);
				Log.e(TAG, "Name:" + Name);
				Log.e(TAG, "Mobile:" + Mobile);
				Log.e(TAG, "Address:" + Address);
				Log.e(TAG, "Source:" + Source);
				Log.e(TAG, "Note:" + Note);
				if (isWrite()) {
					mHandler.sendEmptyMessage(1);
				}
			    
			
				break;
				
			case R.id.source:
				
				/*customDialog();*/
				ItemLists.clear();
				setItemDate();
				new ShowDialog(NewClueActivity.this, SOURCE_DIALOG, ItemLists, "选择来源", NewClueActivity.this,"请选择来源！",source.getText().toString().trim()).customDialog();
				break;

			default:
				break;
			}
			
		}
	}
	
	/**
	 * 判断是否输入完成
	 * @return 
	 */
	private boolean isWrite(){
		
		if (company_name.getText().toString().trim().equals("") 
				&& contact_Name.getText().toString().trim().equals("")) {
			showDialog("公司和联系人至少填写一项！");
			return false;
		}
		if (telephone_number.getText().toString().trim().equals("") ) {
			showDialog("请填写电话号码！");
			return false;
		}
		if (source.getText().toString().trim().equals("")) {
			showDialog("请选择来源！");
			return false;
		}
		if (note.getText().toString().trim().equals("")) {
			showDialog("请填写描述内容！");
			return false;
		}
		
		
//		if (!isPhoneNumberValid(Mobile)) {
//			showDialog("请检查号码是否正确！");
//			return false;
//		}
		
		
		return true;
		
		
	}
	
	public void showDialog(String TXT){
		// Toast
		new ClueCustomToast().showToast(NewClueActivity.this,
				R.drawable.toast_warn, TXT);

	}

	
	/**
	 * 通过网络提交数据
	 * @param ID  编号：传入0时表示为新增操作，非0时为修改操作。
	 * @param Source 来源
	 * @param Title 主题
	 * @param Company 公司名称
	 * @param Name 联系人
	 * @param Tel 联系电话
	 * @param Mobile 手机
	 * @param QQ QQ号
	 * @param Email 邮件
	 * @param Address 地址
	 * @param Note 备注
	 * @param Manager_ID 人员编号
	 * @param Department_ID 部门编号
	 */
	private void getNetData(final int ID, final String Source,
			final String Title, final String Company, final String Name,
			final String Tel, final String Mobile, final String QQ,
			final String Email, final String Address, final String Note,
			final int Manager_ID, final int Department_ID) {
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String yyjjString = getJson(ID, Source, Title, Company, Name, Tel, Mobile,
						QQ, Email, Address, Note, Manager_ID,
						Department_ID);
				Log.e(TAG, "yyjjString:" + yyjjString);
				String result = DataUtil.callWebService(
						Methods.CREATE_CLUE,
						getJson(ID, Source, Title, Company, Name, Tel, Mobile,
								QQ, Email, Address, Note, Manager_ID,
								Department_ID));			
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
					mHandler.sendEmptyMessage(2);
				}
				
			}
		}.start();
	}
	
	/**
	 * 设置json对象
	 * @param Company_ID 公司id
	 * @param Manager_ID 用户编号
	 * @param Department_ID 部门编号
	 * @param keywords 搜索关键字
	 * @param pageIndex 页码
	 * @return
	 */
	private String getJson( int ID,  String Source,
			 String Title,  String Company,  String Name,
			 String Tel,  String Mobile,  String QQ,
			 String Email, String Address, String Note,
			 int Manager_ID,  int Department_ID) {
		
		//添加时Action设为Insert，修改时设为Update      
		list.setAction("Insert");
		list.setID(ID);
		list.setSource(Source);
		list.setTitle(Title);
		list.setCompany(Company);
		list.setName(Name);
		list.setTel(Tel);
		list.setMobile(Mobile);
		list.setQQ(QQ);
		list.setEmail(Email);
		list.setAddress(Address);
		list.setNote(Note);
		list.setManager_ID(Integer.parseInt(preferences.getString(Constant.HUISHANGYUN_UID, "0")));
		list.setDepartment_ID(preferences.getInt(Constant.HUISHANG_DEPARTMENT_ID, 0));
		ZJRequest zjRequest = new ZJRequest();
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
				new ClueCustomToast().showToast(NewClueActivity.this,
						R.drawable.toast_sucess, "创建线索成功！");
				NewClueActivity.this.setResult(1);
				NewClueActivity.this.finish();
				break;
			case 1:
				
			// 提交数据
			getNetData(0, Source, "", Company, Name, "", Mobile, "",
					"", Address, Note, 0, 0);
		
				break;
				
			case 2:
				new ClueCustomToast().showToast(NewClueActivity.this,
						R.drawable.toast_defeat, "创建线索失败！");
				break;
			
			default:
				// 创建失败
				Log.e(TAG, "失败");
				break;
			}
		};
	};
		
	 /**
	  * 向二维数组赋值
	  */
	private void setItemDate(){
		List<com.huishangyun.model.Enum>  list = new ArrayList<Enum>();
		//根据数字查对应中文
//		Enum enum1 = EnumManager.getInstance(context).getEmunForIntKey(EnumKey.ENUM_CLUE_SOURCE, "" + source);
		
		//查询全部
		list = EnumManager.getInstance(context).getAllEnums(EnumKey.ENUM_CLUE_SOURCE);	
		for (int i = 0; i < list.size(); i++) {
			map = new HashMap<String, Object>();
			map.put("name", list.get(i).getLab());
			ItemLists.add(map);
			Log.e(TAG,"ItemLists.SIZE:" +  ItemLists.size());
		}

	}
	
	
	

	@Override
	public void onDialogDown(int position, int type) {
		// TODO Auto-generated method stub
		switch (type) {
		case SOURCE_DIALOG:
			HashMap<String, Object> map = ItemLists.get(position);
			source.setText((CharSequence) map.get("name"));
			break;

		default:
			break;
		}
		
	}
	 /**
	  * 创建线索处理和判断
	  */
	private void judage(){
		//公司和联系人都没填则不让提交
		if ((company_name.getText().toString()).equals("")
				&& (contact_Name.getText().toString()).equals("")) {
			//请填写公司或者联系人至少一项
			

		} else {
			
		}
	} 
	
	/**
	 * 验证号码 手机号 固话均可
	 * @param phoneNumber
	 * @return
	 */
	public static boolean isPhoneNumberValid(String phoneNumber) {
		boolean isValid = false;


		String expression = "((^(13|15|18)[0-9]{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9] {1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-? \\d{7,8}-(\\d{1,4})$))";
		CharSequence inputStr = phoneNumber;


		Pattern pattern = Pattern.compile(expression);


		Matcher matcher = pattern.matcher(inputStr);


		if (matcher.matches() ) {
		isValid = true;
		}


		return isValid;


		 
		}
	
}
