package com.huishangyun.Channel.stock;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huishangyun.Adapter.StockReportAdapter;
import com.huishangyun.Channel.Clues.ClueCustomToast;
import com.huishangyun.Channel.Clues.ShowDialog;
import com.huishangyun.Channel.Plan.CustomersListActivity;
import com.huishangyun.Channel.Plan.SortModel;
import com.huishangyun.Interface.OnDialogDown;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Methods;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.View.YMPickerDialog;
import com.huishangyun.model.Content;
import com.huishangyun.yun.R;

/**
 * 库存上报界面
 * 
 * @author xsl
 * 
 */
public class StockReport extends BaseActivity implements OnDialogDown {

	private LinearLayout backLayout = null;// 返回
	private ListView listView;
	private Intent mIntent = null;
	private StockReportAdapter adapter = null;;
	private RelativeLayout client_root;//选择客户
	private RelativeLayout stock_period_layout;//选择所属周期
	private TextView period_name;//周期名称
	private TextView txt_clientName;//客户
	private TextView txt_productName;//产品
	private EditText edit_stock_report_total;//数量
	private RelativeLayout report_unit;//单位选择
	private TextView text_stock_report_unit;//单位
	private Button btn_stock_report_confirm;//确定
	private TextView txt_stock_report_submit;//提交
	private ImageView customer_select;//客户可选择标志
	ArrayList<SortModel> arrayList = new ArrayList<SortModel>();//客户数据
	ArrayList<SortModel> arrayList1 = new ArrayList<SortModel>();//产品数据
	private static final int SOURCE_DIALOG = 0;
	private static final String TAG = null; 
	StockList list = new StockList();
	StockList mList = new StockList();
	private List<StockList> lists = new ArrayList<StockList>();
	private int ListView_position;//item位置
	private int flag = 0;
	private int flag1 = 0;//决定客户是否可选
	ArrayList<HashMap<String, Object>> ItemLists = new ArrayList<HashMap<String, Object>>();	
	HashMap<String, Object> map;//定义一个map函数
	private String Cycle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stock_report);
		initView();
		adapter = new StockReportAdapter(StockReport.this,lists);
		listView.setAdapter(adapter);
	}

	/**
	 * 初始化组件
	 * 
	 */
	private void initView() {
		// TODO Auto-generated method stub
		// 获取资源
		backLayout = (LinearLayout) findViewById(R.id.stock_report_back);
		listView = (ListView) findViewById(R.id.stock_report_list);
		stock_period_layout = (RelativeLayout) findViewById(R.id.stock_period_layout);
		period_name = (TextView) findViewById(R.id.period_name);
		client_root = (RelativeLayout) findViewById(R.id.client_root);
		txt_clientName = (TextView) findViewById(R.id.txt_clientName);
		txt_productName = (TextView) findViewById(R.id.txt_productName);
		edit_stock_report_total = (EditText) findViewById(R.id.edit_stock_report_total);
		report_unit = (RelativeLayout) findViewById(R.id.report_unit);
		text_stock_report_unit = (TextView)findViewById(R.id.text_stock_report_unit);
		btn_stock_report_confirm = (Button) findViewById(R.id.btn_stock_report_confirm);
		txt_stock_report_submit = (TextView) findViewById(R.id.txt_stock_report_submit);
		customer_select = (ImageView) findViewById(R.id.customer_select);
		text_stock_report_unit.setFocusable(false);
		text_stock_report_unit.setEnabled(false);
		

		
		// 添加事件
		backLayout.setOnClickListener(new myOnClickListener());
		txt_productName.setOnClickListener(new myOnClickListener());
	    client_root.setOnClickListener(new myOnClickListener());
		report_unit.setOnClickListener(new myOnClickListener());
		btn_stock_report_confirm.setOnClickListener(new myOnClickListener());
		txt_stock_report_submit.setOnClickListener(new myOnClickListener());
		stock_period_layout.setOnClickListener(new myOnClickListener());
		
		/**
		 * 产品列表listview单击事件处理
		 */
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
					deleteDialog();
					ListView_position = position;
			
				return false;
			}
		});
		
		
	}
	
	/**
	 * 自定义删除对话框
	 */
	private void deleteDialog() {
		flag = 0;
		LayoutInflater layoutInflater = LayoutInflater
				.from(StockReport.this);
		View customdialog = layoutInflater.inflate(
				R.layout.activity_stock_delete_dialog, null);
		// 创建一个对话框对象
		final AlertDialog alertDialog = new AlertDialog.Builder(
				StockReport.this).create();
		// 设置对话框背景颜色
		alertDialog.setIcon(R.color.white);
		// 设置对话显示位置
		alertDialog.setView(customdialog, -1, -1, 0, -1);
		alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		ImageView cancel = (ImageView) customdialog.findViewById(R.id.cancel);
		final TextView delete = (TextView) customdialog.findViewById(R.id.delete);
		final TextView rewrite = (TextView) customdialog.findViewById(R.id.rewrite); 
		alertDialog.show();
		
		//取消
		cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alertDialog.dismiss();
			}
		});
		
		//删除
		delete.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				delete.setBackgroundColor(Color.parseColor("#b7e2f6"));
				lists.remove(ListView_position);
				adapter.notifyDataSetChanged();
				alertDialog.dismiss();
			}
		});
		
		//修改
		rewrite.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				rewrite.setBackgroundColor(Color.parseColor("#b7e2f6"));
				flag = 1;
				alertDialog.dismiss();
			}
		});
	}

	/**
	 * 单击事件
	 * @author xsl
	 * 
	 */
	private class myOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.stock_report_back:// 返回
				finish();
				break;
			case R.id.client_root://选择客户
				if (flag1 == 0) {
					Intent intent = new Intent(StockReport.this, CustomersListActivity.class);
					//选择个客户/人员界面，客户传值“0”，人员传值“1”（注意传String类型）
					intent.putExtra("mode", "0");
					//多选传0，单选传1
					intent.putExtra("select", 1);
					//传递分组名称
					intent.putExtra("groupName", "分类");
					intent.putExtra("Tittle", "选择客户");
					startActivityForResult(intent, 1);
				}
				
				break;
			case R.id.txt_productName:// 产品
//				mIntent = new Intent(StockReport.this, StockProduct.class);
//				startActivity(mIntent);
				
				Intent intent1 = new Intent(StockReport.this, CustomersListActivity.class);
				//选择个客户/人员界面，客户传值“0”，人员传值“1”（注意传String类型）
				intent1.putExtra("mode", "2");
				//多选传0，单选传1
				intent1.putExtra("select", 1);
				//传递分组名称
				intent1.putExtra("groupName", "分类");
				intent1.putExtra("Tittle", "产品选择");
				startActivityForResult(intent1, 2);
				break;
			case R.id.btn_stock_report_confirm:
				//获取当前的日期
				Calendar calendar = Calendar.getInstance();
				int year = calendar.get(Calendar.YEAR);
				int month = calendar.get(Calendar.MONTH);
				int day = calendar.get(Calendar.DAY_OF_MONTH);
				Date date1 = new Date(year - 1900, month, day); // 获取时间转换为Date对象
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
				String time = sf.format(date1);
				 //获取登录人id
			    int Manager_ID  = Integer.parseInt(preferences.getString(Constant.HUISHANGYUN_UID,"0"));
			    //公司编号
			    int Company_ID = preferences.getInt(Content.COMPS_ID, 1016);
			    // 所属部门id
				int Department_ID = preferences.getInt(Constant.HUISHANG_DEPARTMENT_ID,0);
			    list = new StockList();
				list.setID(0);
				if (isWrite()) {
				list.setMember_ID(arrayList.get(0).getID());//客户编号
				list.setMember_Name(arrayList.get(0).getCompany_name());//客户名称
				list.setProduct_ID(arrayList1.get(0).getID());//产品编号
//				list.setProduct_Name(txt_productName.getText().toString());//产品名称
				list.setProduct_Name(arrayList1.get(0).getCompany_name());//产品名称
				list.setQuantity(Float.valueOf(edit_stock_report_total.getText().toString()));//数量
				list.setCycleType(backCycleType(Cycle));//周期类型
				list.setUnit_ID(arrayList1.get(0).getProduct_Unit_ID());//单位编号
				list.setUnit_Name(text_stock_report_unit.getText().toString());//单位名称
				list.setBelongDate(period_name.getText().toString());//上报日期
				list.setManager_ID(Manager_ID);//人员编号			
//				list.setManager_Name(manager_Name);//人员姓名
				list.setDepartment_ID(Department_ID);//部门id
//				list.setDepartment_Name(department_Name);//部门名称
				list.setCompany_ID(Company_ID);//企业编号
				if (flag==1) {
					flag = 0;
					lists.remove(ListView_position);
					mHandler.sendEmptyMessage(3);
					
				}
				lists.add(list);
				Log.e(TAG, "lists---0:" + lists.size());
				Log.e(TAG, "lists---1:" + lists.get(0).getProduct_Name());
				Log.e(TAG, "lists---2:" + lists.get(0).getQuantity());
				for (int i = 0; i < lists.size(); i++) {
					Log.e(TAG, "lists---name:" + lists.get(i).getProduct_Name());
				}
				//通知listview改变
				adapter.notifyDataSetChanged();
				edit_stock_report_total.setText("");
				
				}else {
					//不操作
				}
				break;
			case R.id.report_unit://单位

				break;
				
			case R.id.txt_stock_report_submit://提交
				if (lists.size()<=0) {
					showDialog("请添加要提交的数据！");
				}else {
					String data = "";
					for (int i = 0; i < lists.size(); i++) {
						StockList stockList = lists.get(i);
						if (i == 0) {

							
							data =  stockList.getProduct_ID() + "," +  stockList.getUnit_ID()
									+ "," + stockList.getQuantity();
							
						} else {

							data = data + "#" + stockList.getProduct_ID() + "," +  stockList.getUnit_ID()
									+ "," + stockList.getQuantity();
						}
					}
					
					Log.e(TAG,"数据：" + data);
					getNetData( lists.get(0).getManager_ID(), lists.get(0).getDepartment_ID(),
	                      lists.get(0).getCompany_ID(), lists.get(0).getBelongDate(),
	                      lists.get(0).getMember_ID(),data,lists.get(0).getCycleType());
				}
				
				break;
				case R.id.stock_period_layout:
					
					ItemLists.clear();
					setItemDate();
					new ShowDialog(StockReport.this, SOURCE_DIALOG, ItemLists, "选择周期", StockReport.this,"请选择周期！",null).customDialog();
					
				break;
			default:
				break;
			}

		}

	}
	
	/**
	 * 上报库存
	 * @param Manager_ID 上报人id
	 * @param Department_ID  部门id
	 * @param Company_ID  公司id
	 * @param BelongDate 上报时间
	 * @param Array_IDs 批量数据
	 */
	private void getNetData( final int Manager_ID,final int Department_ID,
			final int Company_ID,final String BelongDate, final int Member_ID,
			final String Array_IDs,final String cycleType) {
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				android.util.Log.e(TAG,"提交数据：" + getJson( Manager_ID, Department_ID, Company_ID,
										 BelongDate,Member_ID, Array_IDs,cycleType));
				String result = DataUtil.callWebService(
						Methods.STOCK_CHECK_IN,
						getJson(Manager_ID, Department_ID, Company_ID,
								BelongDate, Member_ID, Array_IDs, cycleType));
				
				// 先判断网络数据是否获取成功，防止网络不好导致程序崩溃
				if (result != null) {
					// 获取对象的Type
					try {
						JSONObject jsonObject = new JSONObject(result);
						// Log.e(TAG, "code:" + jsonObject.getInt("Code"));
						int code = jsonObject.getInt("Code");
						android.util.Log.e(TAG, "code------:" + code);
						// 假如code返回为0则表示删除成功,否则为失败
						if (code == 0) {
							mHandler.sendEmptyMessage(0);
						} else {
							mHandler.sendEmptyMessage(5);
						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					mHandler.sendEmptyMessage(5);

				}

			}
		}.start();
	}

	/**
	 * 设置json对象
	 * 
	 * @param
	 * @return
	 */
	private String getJson(int Manager_ID,int Department_ID,
			int Company_ID,String BelongDate, int Member_ID,String Array_IDs,String cycleType) {
		ZJRequest<StockList> zjRequest = new ZJRequest<StockList>();
		
		mList = new StockList();
		mList.setAction("Insert");
		// id新增传入0
		mList.setID(0);
		mList.setManager_ID(Manager_ID);
		mList.setDepartment_ID(Department_ID);
		mList.setCompany_ID(Company_ID);
		mList.setBelongDate(BelongDate);
		mList.setMember_ID(Member_ID);
		mList.setArray_IDs(Array_IDs);
		mList.setCycleType(cycleType);
		zjRequest.setData(mList);
		return JsonUtil.toJson(zjRequest);

	}
	
	
	/**
	 * handler异步更新数据
	 */
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				
				//清除产品信息
				arrayList1.clear();
				txt_productName.setText("");
				text_stock_report_unit.setText("");
			
				// 创建成功
				new ClueCustomToast().showToast(StockReport.this,
						R.drawable.toast_sucess, "库存添加成功！");
				lists.clear();
				//通知listview改变
				adapter.notifyDataSetChanged();
				flag1 = 0;
				customer_select.setVisibility(View.VISIBLE);
			
				break;
			case 1://客户设置
				txt_clientName.setText((arrayList.get(0).getCompany_name()).trim());
				Log.e(TAG, "=======" + arrayList.get(0).getCompany_name().length());
			  break;
			  
			case 2:
				txt_productName.setText(arrayList1.get(0).getCompany_name());//产品名称
				text_stock_report_unit.setText(arrayList1.get(0).getProduct_Unit_Name());//产品单位
				break;
			case 3:
				// 修改成功
				new ClueCustomToast().showToast(StockReport.this,
						R.drawable.toast_sucess, "修改成功！");
				break;
			case 5:
				ClueCustomToast.showToast(StockReport.this,
						R.drawable.toast_sucess, "提交失败！");
				break;
				
			default:
				break;
			}
			};
		};
		
	
	/**
	 * 对返回数据处理
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		L.i("resultCode==" + resultCode + "requestCode==" + requestCode);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 1://选择客户返回			
				System.out.println("resultCode=="+resultCode+"requestCode=="+requestCode);
		        Bundle bundle = data.getBundleExtra("bundle");//得到新Activity 关闭后返回的数据
		        arrayList = (ArrayList<SortModel>) bundle.getSerializable("result");//接收泛型
		        if (arrayList.size()!=0) {
		        	 mHandler.sendEmptyMessage(1);
		        	 customer_select.setVisibility(View.INVISIBLE);
		        	 flag1 = 1;
				}	        
				break;
			case 2:
				System.out.println("resultCode=="+resultCode+"requestCode=="+requestCode);
		        Bundle bundle1 = data.getBundleExtra("bundle");//得到新Activity 关闭后返回的数据
		        arrayList1 = (ArrayList<SortModel>) bundle1.getSerializable("result");//接收泛型
		        if (arrayList1.size()!=0) {
		        	 mHandler.sendEmptyMessage(2);
				}	
		        for (int i = 0; i < arrayList1.size(); i++) {
					Log.e(TAG, "id;;;;;" + arrayList1.get(i).getID());
				}
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
		if (txt_clientName.getText().toString().trim().equals("")) {
			showDialog("请选择客户！");
			return false;
		}
		if (period_name.getText().toString().trim().equals("")) {
			showDialog("请选择周期！");
			return false;
		}
		if (txt_productName.getText().toString().trim().equals("")) {
			showDialog("请选择产品！");
			return false;
		}
		if (edit_stock_report_total.getText().toString().equals("")) {
			showDialog("请填写数量！");
			return false;
		}
		if (text_stock_report_unit.getText().toString().equals("")) {
			showDialog("单位不能为空！");
			return false;
		}
		return true;
		
		
	}
	
	private String backCycleType(String TXT){
		if (TXT.equals("月")) {
			TXT = "month";
		}else if (TXT.equals("周")) {
			TXT = "week";
		}else if (TXT.equals("日")) {
			TXT = "day";
		}
		return TXT;
		
	}
	
	public void showDialog(String TXT){
		// 修改成功
		new ClueCustomToast().showToast(StockReport.this,
				R.drawable.toast_defeat, TXT);

	}

	 /**
	  * 向二维数组赋值
	  */
	private void setItemDate(){
		
		List<String> list = new ArrayList<String>();
		list.add("月");
		list.add("日");
		for (int i = 0; i < list.size(); i++) {
			map = new HashMap<String, Object>();
			map.put("name", list.get(i));
			ItemLists.add(map);
		}

	}
	
	@Override
	public void onDialogDown(int position, int type) {
		// TODO Auto-generated method stub
		switch (type) {
		case SOURCE_DIALOG:
			HashMap<String, Object> map = ItemLists.get(position);
			if (!(map.get("name")).equals("")) {
				if ((map.get("name")).equals("月")) {
					datePickDialog2();
				}else if ((map.get("name")).equals("日")) {
					datePickDialog();
				}
				Cycle = map.get("name") + "";
			}
			break;

		default:
			break;
		}
	}
	
	/**
	 * 选择年、月、日时间控件
	 */
	private void datePickDialog(){
		Calendar cal = Calendar.getInstance();
		Dialog dialog = new DatePickerDialog(context, 
				new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				// TODO Auto-generated method stub
				Date date = new Date(year - 1900, monthOfYear, dayOfMonth); //获取时间转换为Date对象  
                SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd");
                String time = sf1.format(date);
				period_name.setText(time);
			}
		}, cal.get(Calendar.YEAR),
		cal.get(Calendar.MONTH), 
		cal.get(Calendar.DAY_OF_MONTH));
		dialog.show();
	}
	
	/**
	 * 选择年、月时间控件
	 */
	private void datePickDialog2(){
		
			final Calendar cal = Calendar.getInstance();		
			YMPickerDialog mDialog = new YMPickerDialog(this,
					new DatePickerDialog.OnDateSetListener() {
						public void onDateSet(DatePicker view, int year,
								int monthOfYear, int dayOfMonth) {
							Date date = new Date(year - 1900, monthOfYear, dayOfMonth); //获取时间转换为Date对象  
			                SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM");
			                String time = sf1.format(date);
							period_name.setText(time);
						}
					}, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH));
			mDialog.show();
			
		}
	
}