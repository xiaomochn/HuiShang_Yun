package com.huishangyun.Channel.Clues;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Methods;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.yun.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ModifyClueActivity extends BaseActivity {
	protected static final String TAG = null;
	private TextView submit;// 提交按钮
	private RelativeLayout back;
	private EditText company_name, contact_name, 
	                 telephone_number, address, content;// 公司、联系人、电话、地址、内容
	private String company, c_name, Tel, get_address, note, clueid,
			Manger_Name, source; // 联系人、公司名称、电话、地址、内容、线索id、创建人、来源
	int ID;//修改传入ID
	
	
	private CreateClue list = new CreateClue();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clue_modify);
		//接收产品id
		Intent intent = getIntent();
		clueid = intent.getStringExtra("ClueID");
	    ID = Integer.parseInt(clueid);//线索id
	    company = intent.getStringExtra("company");
	    c_name = intent.getStringExtra("contact_name");
	    Tel = intent.getStringExtra("telephone");
	    get_address = intent.getStringExtra("address");
	    note = intent.getStringExtra("note");
	    Manger_Name = intent.getStringExtra("Manger_Name");//创建人
	    source = intent.getStringExtra("source");//来源
		init();
	}
	
	
	/**
	 * 初始化布局，加载布局控件
	 */
	private void init() {
		submit = (TextView) findViewById(R.id.submit);
		back = (RelativeLayout) findViewById(R.id.back);
		company_name = (EditText) findViewById(R.id.company_name);
		contact_name = (EditText) findViewById(R.id.contact_name);
		telephone_number = (EditText) findViewById(R.id.telephone_number);
		address = (EditText) findViewById(R.id.address);
		content = (EditText) findViewById(R.id.content);
		
		company_name.setText(company);
		contact_name.setText(c_name);
		telephone_number.setText(Tel);
		address.setText(get_address);
		content.setText(note);
		
		submit.setOnClickListener(new ButtonClickListener());
		back.setOnClickListener(new ButtonClickListener());

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
			case R.id.back:
				  finish();

				break;
				
				/**
				 * 保持不能修改的值不变
				 */
			case R.id.submit:
				//判断有没对数据修改
				if (!(company_name.getText().toString().trim()).equals("")) {
					company = company_name.getText().toString();//公司名称
				}
				if (!(contact_name.getText().toString().trim()).equals("")) {
					c_name = contact_name.getText().toString();//联系人
				}
				if (!(telephone_number.getText().toString().trim()).equals("")) {
					Tel = telephone_number.getText().toString();//电话
				}
				if (!(address.getText().toString().trim().equals(""))) {
					get_address = address.getText().toString();//地址
				}
				if (!(content.getText().toString().trim()).equals("")) {
					note = content.getText().toString();//内容
				}
				if (isWrite()) {
					
					//提取网络修改数据,ID传入0为新增，其它为修改
					getNetData(ID, source, "", company, c_name, "",
							Tel, "", "", get_address, note, 0,
							0);
				}
				

				break;


			default:
				break;
			}

		}
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
					mHandler.sendEmptyMessage(1);
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
		list.setAction("Update");
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
	 * 更新UI
	 */
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			 super.handleMessage(msg);
			 switch (msg.what) {
			case 0://修改成功
				new ClueCustomToast().showToast(ModifyClueActivity.this, R.drawable.toast_sucess, "修改成功 !");				
//				Intent intent = new Intent(ModifyClueActivity.this, DetailClueActivity.class);
//				//传值
//				intent.putExtra("ID", clueid);
//				startActivity(intent);
//				finish();
				if (preferences.getString(Constant.HUISHANG_TYPE, "0").equals("1")) {
					Intent intent = new Intent(ModifyClueActivity.this,MainClueOrdinaryEntryActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}else {
					Intent intent = new Intent(ModifyClueActivity.this,MainClueActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
				
				break;
				case 1:
					new ClueCustomToast().showToast(ModifyClueActivity.this, R.drawable.toast_defeat, "修改失败 !");		
					break;

			default:
				break;
			}
		};
	};
	
	/**
	 * 判断是否输入完成
	 * @return 
	 */
	private boolean isWrite(){
		
		if (company_name.getText().toString().trim().equals("") 
				&& contact_name.getText().toString().trim().equals("") ) {
			showDialog("公司和联系人至少填写一项！");
			return false;
		}
		
		if (telephone_number.getText().toString().trim().equals("") ) {
			showDialog("请填写电话号码！");
			return false;
		}
		
		if (content.getText().toString().trim().equals("")) {
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
		new ClueCustomToast().showToast(ModifyClueActivity.this,
				R.drawable.toast_warn, TXT);

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