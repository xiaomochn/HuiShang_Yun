package com.huishangyun.Channel.Customers;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
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
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Channel.Clues.ShowDialog;
import com.huishangyun.Channel.Orders.Common;
import com.huishangyun.Channel.Plan.CustomersListActivity;
import com.huishangyun.Channel.Plan.SortModel;
import com.huishangyun.Interface.OnDialogDown;
import com.huishangyun.Util.Base64Util;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.FileUtils;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.PhotoHelp;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.manager.AreaManager;
import com.huishangyun.manager.MemberManager;
import com.huishangyun.manager.OperationTime;
import com.huishangyun.model.AreaModel;
import com.huishangyun.model.CallSystemApp;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Content;
import com.huishangyun.model.MemberDetail;
import com.huishangyun.model.MemberGroups;
import com.huishangyun.model.MemberLevel;
import com.huishangyun.model.Members;
import com.huishangyun.model.Methods;
import com.huishangyun.model.OperTime;
import com.huishangyun.model.Order_address;
import com.huishangyun.task.UpLoadFileTask.OnUpLoadResult;
import com.huishangyun.task.UpLoadImgSignText;
import com.huishangyun.yun.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerSet extends BaseActivity implements OnUpLoadResult{
	
	private int result;
	private int widths, heights;
	private EditText edt01_01, edt01_05, edt01_06,
	edt02_01, edt02_02, edt02_03,
	edt03_01, edt03_02, edt03_03, edt03_04, edt03_06, edt03_07,
	edt04_01, edt04_02, edt04_03, edt04_05, edt04_06;
	
	private TextView edt01_02, edt01_03, edt01_04, edt03_05, edt04_04, dingwei_address;
	private ImageView item03_img01, item03_img02, item03_img03, dingwei_img;
	
	private LinearLayout back, lin01, lin02, lin03, lin04, dingwei, customer_detail_edit, customer_detail_more;
	private ImageView rel01, rel02, rel03, rel04;

	/**
	 * 头部的操作
	 */	
	private RelativeLayout tijiao;
		
		
	/**
	 * 省市县对话框变量
	 */
	private int index = -1;
	private ListView listView;
	private DialogAdapter dialogAdapter;
	private TextView title;
	private String address1, address2, address3;
	private List<AreaModel> data = new ArrayList<AreaModel>();
	private List<AreaModel> data2 = new ArrayList<AreaModel>();
	private List<Order_address> data_address = new ArrayList<Order_address>();
	private int ID = -1, ID2 = -1, ID3 = -1;
	private int MemberID, MemberID2;//MemberID2上级经销商的ID.
	
	private String[] items = new String[] { "选择本地图片", "拍照" };
	/**
	 * 拍照功能的变量
	 */
	private String url_path = Constant.SAVE_IMG_PATH + File.separator;
	private String path1;//照片本地路径
	private String path01;//照片名称
	private String path_new;
	
//	public static List<AddImage> list_img = new ArrayList<AddImage>();//照片路径
	public static List<String> path = new ArrayList<String>();
	public static List<String> path_serve = new ArrayList<String>();
	
	public static int currentItem = 0; // 当前图片的索引号
	private Calendar calendar;
	private ProgressDialog pDialog;
	
	private String Group_ID = "";
	private MemberDetail detail;//客户详情
	
	/********【xsl添加代码】***/
	private String CompanyName;
	private String ContactName;
	private String telephone;
	private String Address;
	/*******【end】**********/
	private Handler myHandler = new Handler(){
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case HanderUtil.case1:
				
				updataMember();
				break;
				
			case HanderUtil.case2:
//				pDialog.dismiss();
				dismissDialog();
				showCustomToast((String) msg.obj, true);
				break;
				
			case HanderUtil.case3:
				new ShowDialog(CustomerSet.this, 1, mList, "选择等级", onDialogDown,"请选择等级！",null).customDialog();
//				pDialog.dismiss();
				dismissDialog();
				break;
				
			case HanderUtil.case4:				
//				pDialog.dismiss();
				dismissDialog();
				MemberID = result;
				if (edt04_01.getText().toString().trim().equals("") && 
						edt04_02.getText().toString().trim().equals("") &&
						edt04_03.getText().toString().trim().equals("") &&
						edt04_04.getText().toString().trim().equals("") &&
						edt04_05.getText().toString().trim().equals("")) {
					
				}else {
					addAdress(0, "Insert");//新增地址
				}
				showCustomToast("操作成功", true);
				setResult(RESULT_OK);
				
				updataInfo();//更新本地数据库
				/******xsl修改*******/
				Intent intent = new Intent(); 
				intent.setAction("OPPORT_DATA");
				intent.putExtra("MemberID", MemberID);
				intent.putExtra("MemberName", edt01_01.getText().toString().trim());
				CustomerSet.this.sendBroadcast(intent);
				/******【end】*******/
				finish();
				break;
				
			case HanderUtil.case5:			
				Log.e("-------------------", "地址提交成功");	
//				pDialog.dismiss();
				dismissDialog();
				break;	
				
//			case HanderUtil.case6://获取地址
//				for (int i = 0; i < data_address.size(); i++) {	//选择客户id后获取到该客户的默认地址			
//					if (data_address.get(i).isIsDefault()) {
//						edt04_01.setText(data_address.get(i).getName());
//						edt04_02.setText(data_address.get(i).getJob());
//						edt04_03.setText(data_address.get(i).getMobile());
//						String shengshixian = AreaManager.getInstance(CustomerSet.this).getAddress(data_address.get(i).getArea_ID());
//						edt04_04.setText(shengshixian);
//						edt04_05.setText(data_address.get(i).getAddress());
//						edt04_06.setText(data_address.get(i).getIMID());
//					}				
//				}						
//				break;
//				
//			case HanderUtil.case7:	//删除地址		
//				Log.e("-------------------", "地址删除成功");	
//				dismissDialog();
//				break;
				
			case HanderUtil.case8://照片上传服务器成功
				// 所有ImageView重置
				item03_img01.setImageResource(R.drawable.visit_img);
				item03_img02.setImageResource(R.drawable.visit_img);
				item03_img03.setImageResource(R.drawable.visit_img);
				
				if (path.size() >= 1){
					//谢的压缩处理图片的方法
//			    	Bitmap bitmap = new BitmapTools().getBitmap(path.get(0), 480, 800);
//			    	//对图片进行剪切成100*100后显示
//			    	Bitmap bitmap1 = new BitmapTools().cutBitmap(bitmap, 100, 100);
//			    	item03_img01.setImageBitmap(bitmap1);
			    	
			    	Bitmap bitmap1 = Common.getImageThumbnail(path.get(0), 80, 80);
			    	item03_img01.setImageBitmap(bitmap1);
			    	
					if (path.size() >= 2) {									
						Bitmap bitmap2 = Common.getImageThumbnail(path.get(1), 80, 80);
				    	item03_img02.setImageBitmap(bitmap2);
				    	
				    	if (path.size() >= 3) {		    					    		
				    		Bitmap bitmap3 = Common.getImageThumbnail(path.get(2), 80, 80);
					    	item03_img03.setImageBitmap(bitmap3);
						}
					}
				}						
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
		setContentView(R.layout.customerset);
		
		// 获得手机的宽度和高度
        DisplayMetrics dm = new DisplayMetrics();
 		getWindowManager().getDefaultDisplay().getMetrics(dm);
 		widths = dm.widthPixels;
 		heights = dm.heightPixels;
 		
 		tijiao = (RelativeLayout)findViewById(R.id.tijiao);
		back = (LinearLayout)findViewById(R.id.back);
		back.setOnClickListener(mClickListener);

		init();
		init01();
		init02();
		init03();
		init04();
		
		isDatail();
		setResult(RESULT_CANCELED);
	}
	
	/**
	 * 判断是不是详情界面
	 */
	private void isDatail(){
		if (getIntent().getStringExtra("flage").equals("chuangjian")) {
			
		}else if (getIntent().getStringExtra("flage").equals("xiangqing")) {						
						
	    /******xsl添加代码****/
		}else if (getIntent().getStringExtra("flage").equals("shangji")) {
			CompanyName = getIntent().getStringExtra("CompanyName");
			ContactName = getIntent().getStringExtra("ContactName");
			telephone = getIntent().getStringExtra("telephone");
			Address = getIntent().getStringExtra("Address");
			edt01_01.setText(CompanyName);
			edt01_05.setText(ContactName);
			edt01_06.setText(telephone);
			edt04_05.setText(Address);
		}
		/******【end】**********/
	}
	
	
	/**
	 * 一级菜单
	 */
	private void init(){
		rel01 = (ImageView)findViewById(R.id.rel01);
		rel02 = (ImageView)findViewById(R.id.rel02);
		rel03 = (ImageView)findViewById(R.id.rel03);
		rel04 = (ImageView)findViewById(R.id.rel04);
		rel01.setOnClickListener(mClickListener);
		rel02.setOnClickListener(mClickListener);
		rel03.setOnClickListener(mClickListener);
		rel04.setOnClickListener(mClickListener);
										
		lin01 = (LinearLayout)findViewById(R.id.lin01); 
		lin02 = (LinearLayout)findViewById(R.id.lin02); 
		lin03 = (LinearLayout)findViewById(R.id.lin03);	
		lin04 = (LinearLayout)findViewById(R.id.lin04);				
	}
	
	/**
	 * 第一个一级菜单（基本信息）
	 */
	private void init01(){
		edt01_01 = (EditText)findViewById(R.id.item01_01);
		edt01_02 = (TextView)findViewById(R.id.item01_02);
		edt01_03 = (TextView)findViewById(R.id.item01_03);
		edt01_04 = (TextView)findViewById(R.id.item01_04);
		edt01_05 = (EditText)findViewById(R.id.item01_05);
		edt01_06 = (EditText)findViewById(R.id.item01_06);
		//分组
		edt01_02.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(CustomerSet.this, CustomerChooseGroup.class);
				startActivityForResult(intent, 3);
			}
		});
		edt01_03.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
//				pDialog = ProgressDialog.show(CustomerSet.this, "请等待...", "正在加载数据...",true);
				showDialog("正在加载数据...");	
				new Thread(new GetLevels()).start();
			}
		});
		edt01_04.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				createAlertDialog2();
			}
		});
	}
	
	/**
	 * 第二个一级菜单（登录信息）
	 */
	private void init02(){
		edt02_01 = (EditText)findViewById(R.id.item02_01);
		edt02_02 = (EditText)findViewById(R.id.item02_02);
		edt02_03 = (EditText)findViewById(R.id.item02_03);
	}
	
	/**
	 * 第三个一级菜单（扩展信息）
	 */
	private void init03(){
		edt03_01 = (EditText)findViewById(R.id.item03_01);
		edt03_02 = (EditText)findViewById(R.id.item03_02);
		edt03_03 = (EditText)findViewById(R.id.item03_03);
		edt03_04 = (EditText)findViewById(R.id.item03_04);
		edt03_05 = (TextView)findViewById(R.id.item03_05);
		edt03_06 = (EditText)findViewById(R.id.item03_06);
		edt03_07 = (EditText)findViewById(R.id.item03_07);
		dingwei = (LinearLayout) findViewById(R.id.dingwei);
		dingwei_address = (TextView)findViewById(R.id.dingwei_address);
		dingwei_img = (ImageView)findViewById(R.id.dingwei_img);
				
		edt03_05.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
			
//				createAlertDialog4();
				Intent intent = new Intent(CustomerSet.this, CustomersListActivity.class);
				//选择个客户/人员界面，客户传值“0”，人员传值“1”（注意传String类型）
				intent.putExtra("mode", "3");
				//多选传0，单选传1
				intent.putExtra("select", 1);
				//传递分组名称
				intent.putExtra("groupName", "组");
				intent.putExtra("Tittle", "选择上级经销商");
				startActivityForResult(intent, 1);
				
			}
		});
		
		dingwei.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(CustomerSet.this, CustomerLocationActivity.class);
				startActivityForResult(intent, 2);				
			}
		});
		
		item03_img01 = (ImageView) findViewById(R.id.item03_img01);
		item03_img02 = (ImageView) findViewById(R.id.item03_img02);
		item03_img03 = (ImageView) findViewById(R.id.item03_img03);
		
		item03_img01.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (path.size() < 1) {
//					initPhoto();//但拍照下面是可选择本地照片
					showDialogs();
//					path01 = getSystemTime() + ".jpg";
//					path1 = Constant.SAVE_IMG_PATH + File.separator + path01;
//					
//					preferences.edit().putString("path1", path1).commit();
//					preferences.edit().putString("path01", path01).commit();
//					CallSystemApp.callCamera(CustomerSet.this, path1);
//					
//					L.e("第一张path01:" + path01);
//					L.e("path1:" + path1);
				}else {
					currentItem = 0;//跳传的时候传递当前图片的页码
					tiaozhuan(path1);
				}				
			}
		});
		item03_img02.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				if (path.size() < 2) {
					showDialogs();
				}else {
					currentItem = 1;
					tiaozhuan(path1);
				}
			}
		});
		item03_img03.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				if (path.size() < 3) {
					showDialogs();
				}else {
					currentItem = 2;
					tiaozhuan(path1);
				}				
			}
		});
	}
	
	private String Group_Name = ""; 
	/**
	 * 第四个一级菜单（联系人信息）
	 */
	private void init04(){
		edt04_01 = (EditText)findViewById(R.id.item04_01);
		edt04_02 = (EditText)findViewById(R.id.item04_02);
		edt04_03 = (EditText)findViewById(R.id.item04_03);
		edt04_04 = (TextView)findViewById(R.id.item04_04);
		edt04_05 = (EditText)findViewById(R.id.item04_05);
		edt04_06 = (EditText)findViewById(R.id.item04_06);
		
		edt04_04.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				data2.clear();	
				//获取省
				data2 = AreaManager.getInstance(CustomerSet.this).getAreas(0, false);
				data.clear();
				for (int i = 0; i < data2.size(); i++) {
					data.add(data2.get(i));
				}
				Log.e("---------------", "获取省：" + data.toString());
				createAlertDialog3();			
				title.setText("选择省份 :");
			}
		});
	}
	private int Department_ID;
	private int Manager_ID;
	private double Lat;
	private double Lng;
	private String Loc;
	
	/**
	 * 每次拍完照后从相机页面出来   进入onResume方法加载照片
	 */
	protected void onResume() {
		super.onResume();
												
	}
	
	/**
	 * 程序在未经同意的情况下销毁调用此方法
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		
//		for (int i = 0; i < path.size(); i++) {
//			path2.add(path.get(i));
//		}		
	}
	
	/**
	 * 程序真正销毁了调用此方法
	 */
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		Log.e("----------------", "进入CustomerSet   onRestoreInstanceState");
		
//		for (int i = 0; i < path2.size(); i++) {
//			path.add(path2.get(i));
//		}	
	}
	
	/**
	 * 获取   人员/照片   返回的信息
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

//获取上级经销商
		if (requestCode == 1 && resultCode == RESULT_OK) {
			Bundle bundle = data.getBundleExtra("bundle");
			ArrayList<SortModel> arrayList = (ArrayList<SortModel>) bundle.getSerializable("result");
			
			MemberID2 = arrayList.get(0).getID();
			edt03_05.setText(arrayList.get(0).getCompany_name());
					
		}else if (requestCode == 5 && resultCode == RESULT_OK) {
			Bitmap bm = null;			 
		    //外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
		    ContentResolver resolver = getContentResolver();
			    		   
	        try {
	            Uri uri = data.getData();//获得图片的uri 	 
	            bm = MediaStore.Images.Media.getBitmap(resolver, uri);//显得到bitmap图片
	 
	            //这里开始的第二部分，获取图片的路径：	 
	            String[] proj = {MediaStore.Images.Media.DATA};	 
	            //好像是android多媒体数据库的封装接口，具体的看Android文档
	            Cursor cursor = managedQuery(uri, proj, null, null, null); 
	            //按我个人理解 这个是获得用户选择的图片的索引值
	            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	            //将光标移至开头 ，这个很重要，不小心很容易引起越界
	            cursor.moveToFirst();
	            //最后根据索引值获取图片路径
	            String path = cursor.getString(column_index);
	            if (path == null) {
					path = FileUtils.getPath(CustomerSet.this, uri);
				}
	            
	            //获取当前时间
	            path_new = Constant.SAVE_IMG_PATH + File.separator + getSystemTime() + ".jpg";		            			        								
	            path1 = path;
	            showNotDialog("正在上传图片...");		
											    
				new Thread(){
					public void run() {
						File file;
						try {
							Log.e("------------------", "本地图片的path:" + path1);
							file = PhotoHelp.compressImage(path1, path_new);
							Message msg = new Message();
							msg.obj = file;
							msg.what = HanderUtil.case1;
							mHandler.sendMessage(msg);
						} catch (Exception e) {
							e.printStackTrace();
							mHandler.sendEmptyMessage(HanderUtil.case2);
						}
					};
				}.start();
							
	        }catch (IOException e) {
	            e.printStackTrace(); 
	            
	        } catch (Exception e) {
				e.printStackTrace();
			}

			//获取照片
		}else if (requestCode == CallSystemApp.EXTRA_TAKE_PHOTOS && resultCode == RESULT_OK) {
			L.e("进入onActivityResult！");
			showNotDialog("正在上传图片...");		
			path_new = Constant.SAVE_IMG_PATH + File.separator + getSystemTime() + ".jpg";
			L.e("path1:" + path1);
			L.e("path01:" + path01);
			L.e("path_new:" + path_new);
		    
			new Thread(){
				public void run() {
					File file;
					try {
						
						file = PhotoHelp.compressImage(path1, path_new);
						Message msg = new Message();
						msg.obj = file;
						msg.what = HanderUtil.case1;
						mHandler.sendMessage(msg);
					} catch (Exception e) {
						e.printStackTrace();
						mHandler.sendEmptyMessage(HanderUtil.case2);
					}
				};
			}.start();
	   
		    
//		    setImageToNet(path1, path01);
//			}			
			
//获取定位信息				
		}else if (requestCode == 2 && resultCode == RESULT_OK) {
			Loc = data.getStringExtra("Name");
//			double weidu = data.getDoubleArrayExtra("Latitude");
//			double jingdu = data.getDoubleArrayExtra("Longitude");
			dingwei_address.setText(Loc);
			dingwei_img.setImageResource(R.drawable.visit_position);
			Lat = data.getDoubleExtra("Latitude", 0);
			Lng = data.getDoubleExtra("Longitude", 0);
//获取分组信息	
		}else if (requestCode == 3 && resultCode == RESULT_OK) {
			List<GroupModel> list = new ArrayList<GroupModel>();
			list = (List<GroupModel>) data.getExtras().getSerializable("Group_List");
			if (list.size() != 0) {
				
				for (int i = 0; i < list.size(); i++) {
					if (i == 0) {
						Group_ID = "," + list.get(i).getID() + ",";
						Group_Name = "," + list.get(i).getName() + ",";
					} else {
						Group_ID = Group_ID + list.get(i).getID() + ",";
						Group_Name = Group_Name + list.get(i).getName() + ",";
					}
				}
				edt01_02.setText(Group_Name);
			}
		} else if (requestCode == 4 && resultCode == RESULT_OK){
			// 所有ImageView重置
			item03_img01.setImageResource(R.drawable.visit_img);
			item03_img02.setImageResource(R.drawable.visit_img);
			item03_img03.setImageResource(R.drawable.visit_img);
			Bitmap bitmap;
			if (path.size() >= 1){		    	
		    	bitmap = Common.getImageThumbnail(path.get(0), 80, 80);
		    	item03_img01.setImageBitmap(bitmap);
		    	
				if (path.size() >= 2) {								
					bitmap = Common.getImageThumbnail(path.get(1), 80, 80);
			    	item03_img02.setImageBitmap(bitmap);
			    	if (path.size() >= 3) {		    					    		
			    		bitmap = Common.getImageThumbnail(path.get(2), 80, 80);
				    	item03_img03.setImageBitmap(bitmap);
					}
				}
			}
		}
	}
	
	public void submit(View view){
		if (edt01_01.getText().toString().trim().equals("")) {
			showCustomToast("请输入客户名称", false);//false有错的时候true成功的时候
			
		}else if(edt01_02.getText().toString().equals("")){
			showCustomToast("请选择客户分组", false);
			
		}else if(edt01_03.getText().toString().equals("")){
			showCustomToast("请选择客户等级", false);
			
		}else if(edt01_04.getText().toString().equals("")){
			showCustomToast("请选择客户类型", false);
			
		}else if(edt01_05.getText().toString().trim().equals("")){
			showCustomToast("请输入负责人", false);
			
		}else if(edt01_06.getText().toString().trim().equals("")){
			showCustomToast("请输入联系电话", false);
		
		}else if(!edt02_02.getText().toString().trim().equals("") && 
				edt02_02.getText().toString().length() < 6){
			showCustomToast("密码为6到12位之间", false);
		
		}else if (!edt02_02.getText().toString().trim().equals("") && 
				!edt02_02.getText().toString().trim().equals(edt02_03.getText().toString().trim())) {
			showCustomToast("确认密码不一致", false);
			
		}else {
//			pDialog = ProgressDialog.show(CustomerSet.this, "请等待...", "正在提交...",true);  
			showDialog("正在加载数据...");	
			
			submit("Insert");			
		}
	}
	
	/**
	 * 一级菜单的点击方法
	 */
	private OnClickListener mClickListener = new OnClickListener() {		
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.rel01:
				rel02.setBackgroundResource(R.drawable.customer_pressbg);
				rel03.setBackgroundResource(R.drawable.customer_pressbg);
				rel04.setBackgroundResource(R.drawable.customer_pressbg);
				rel01.setBackgroundResource(R.drawable.customer_currentbg);
				lin01.setVisibility(View.VISIBLE);
				lin02.setVisibility(View.GONE);
				lin03.setVisibility(View.GONE);
				lin04.setVisibility(View.GONE);
				
				break;
				
			case R.id.rel02:
				rel01.setBackgroundResource(R.drawable.customer_pressbg);
				rel03.setBackgroundResource(R.drawable.customer_pressbg);
				rel04.setBackgroundResource(R.drawable.customer_pressbg);
				rel02.setBackgroundResource(R.drawable.customer_currentbg);
				lin01.setVisibility(View.GONE);
				lin02.setVisibility(View.VISIBLE);
				lin03.setVisibility(View.GONE);
				lin04.setVisibility(View.GONE);

				break;
				
			case R.id.rel03:
				rel01.setBackgroundResource(R.drawable.customer_pressbg);
				rel02.setBackgroundResource(R.drawable.customer_pressbg);
				rel04.setBackgroundResource(R.drawable.customer_pressbg);
				rel03.setBackgroundResource(R.drawable.customer_currentbg);
				lin01.setVisibility(View.GONE);
				lin02.setVisibility(View.GONE);
				lin03.setVisibility(View.VISIBLE);
				lin04.setVisibility(View.GONE);

				break;
				
			case R.id.rel04:
				rel01.setBackgroundResource(R.drawable.customer_pressbg);
				rel02.setBackgroundResource(R.drawable.customer_pressbg);
				rel03.setBackgroundResource(R.drawable.customer_pressbg);
				rel04.setBackgroundResource(R.drawable.customer_currentbg);
				lin01.setVisibility(View.GONE);
				lin02.setVisibility(View.GONE);
				lin03.setVisibility(View.GONE);
				lin04.setVisibility(View.VISIBLE);
				
				break;
											
			case R.id.back:
				finish();
				break;	
			default:
				break;
			}
			
		}
	};
	
	
	private String levelName = "";
	private int levelID; 
	private OnDialogDown onDialogDown = new OnDialogDown() {
		public void onDialogDown(int position, int type) {
			// TODO Auto-generated method stub
			MemberLevel memberLevel = (MemberLevel) mList.get(position).get("level");
			levelName = memberLevel.getName();
			levelID = memberLevel.getID();
			edt01_03.setText(levelName);
		}
	};
	
	private int Type = 0;
	//创建       类型           对话框方法
	public void createAlertDialog2(){
		//对话框对象		 
		final AlertDialog alertDialog;		
		LayoutInflater layoutInflater = LayoutInflater.from(CustomerSet.this);
					
		View view = layoutInflater.inflate(R.layout.alertdialog_customer2, null);
		alertDialog = new AlertDialog.Builder(CustomerSet.this).create();		
		//alertDialog.setIcon(Color.parseColor("#FFFFFF"));
		alertDialog.setView(view, 0, 0, 0, 0);
		
		//alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		final List<String> data = new ArrayList<String>();
		data.add("经销商");
		data.add("终端客户");
		TextView tv = (TextView) view.findViewById(R.id.tv_dialog);
		tv.setText("选择客户");
		
		ListView listView = (ListView) view.findViewById(R.id.listview);
		index = -1;//多处都是用这个下标，用之前初始化。
		final DialogAdapter2 dialogAdapter2 = new DialogAdapter2(this, data, 2);
		listView.setAdapter(dialogAdapter2);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				index = position;
				dialogAdapter2.notifyDataSetChanged();
			}
		});
				
					
		ImageView cacel = (ImageView) view.findViewById(R.id.cancle);
		cacel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				alertDialog.dismiss();
			}
		});
												
		//确定按钮
		Button confirm = (Button)view.findViewById(R.id.enter);	
		confirm.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (index == 0) {
					edt01_04.setText(data.get(index));
					Type = 2;
					alertDialog.dismiss();
					
				}else if (index == 1) {
					edt01_04.setText("终端客户");
					Type = 1;
					alertDialog.dismiss();
					
				}else {
					showCustomToast("请选择类型", false);				
				}
												
			}	
		});
		
		alertDialog.show();	
		//默认居中就不需要下面这句
	    //alertDialog.getWindow().setLayout((int)(widths* 0.85), (int)(heights*0.85));//宽高  
	}
		
	//创建      上级经销商        对话框方法
//	public void createAlertDialog4(){
//			//对话框对象		 
//			final AlertDialog alertDialog;		
//			LayoutInflater layoutInflater = LayoutInflater.from(CustomerSet.this);
//						
//			View view = layoutInflater.inflate(R.layout.alertdialog_customer2, null);
//			alertDialog = new AlertDialog.Builder(CustomerSet.this).create();		
//			//alertDialog.setIcon(Color.parseColor("#FFFFFF"));
//			alertDialog.setView(view, 0, 0, 0, 0);
//					
//			final List<String> data = new ArrayList<String>();
//			data.add("经销商");
//			data.add("终端客户");
//			TextView tv = (TextView) view.findViewById(R.id.tv_dialog);
//			tv.setText("选择上级经销商");
//			
//			ListView listView = (ListView) view.findViewById(R.id.listview);
//			index = -1;//多处都是用这个下标，用之前初始化。
//			final DialogAdapter2 dialogAdapter2 = new DialogAdapter2(this, data, 2);
//			listView.setAdapter(dialogAdapter2);
//			listView.setOnItemClickListener(new OnItemClickListener() {
//				public void onItemClick(AdapterView<?> parent, View view,
//						int position, long id) {
//					index = position;
//					dialogAdapter2.notifyDataSetChanged();
//				}
//			});
//					
//						
//			ImageView cacel = (ImageView) view.findViewById(R.id.cancle);
//			cacel.setOnClickListener(new OnClickListener() {
//				public void onClick(View v) {
//					alertDialog.dismiss();
//				}
//			});
//													
//			//确定按钮
//			Button confirm = (Button)view.findViewById(R.id.enter);	
//			confirm.setOnClickListener(new OnClickListener() {
//				public void onClick(View v) {
//					if (index == 0) {
//						edt03_05.setText(data.get(index));
//						Type = 2;
//						alertDialog.dismiss();
//						
//					}else if (index == 1) {
//						edt03_05.setText("终端客户");
//						Type = 1;
//						alertDialog.dismiss();
//						
//					}else {
//						showCustomToast("请选择类型", false);				
//					}
//													
//				}	
//			});
//			
//			alertDialog.show();				
//		}
	
	//创建   省市县      对话框方法
	public void createAlertDialog3(){
		//对话框对象		 
		final AlertDialog alertDialog;
		
		LayoutInflater layoutInflater = LayoutInflater.from(CustomerSet.this);
		View view = layoutInflater.inflate(R.layout.alertdialog_address, null);
		
		alertDialog = new AlertDialog.Builder(CustomerSet.this).create();		
		//alertDialog.setIcon(Color.parseColor("#FFFFFF"));
		alertDialog.setView(view, 0, 0, 0, 0);
		//alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		          		
		title = (TextView) view.findViewById(R.id.title);

		ImageView cacel = (ImageView) view.findViewById(R.id.cancle);
		cacel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				alertDialog.dismiss();
			}
		});
		
		listView = (ListView) view.findViewById(R.id.listview);
		dialogAdapter = new DialogAdapter(this);
		listView.setAdapter(dialogAdapter);
						
		//确定按钮
		Button confirm = (Button)view.findViewById(R.id.enter);	
		confirm.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//防止上个页面选中的项传到下一个，每次确定的时候都清-1
				index = -1;
				//判断是哪层选择的界面
				if (title.getText().toString().trim().equals("选择省份 :")) {
					//防止不选择直接确定的操作
					if (address1 != null && !address1.equals("")) {
						
						data2.clear();
						data.clear();
						//获取市
						data2 = AreaManager.getInstance(CustomerSet.this).getAreas(ID, false);						
						for (int i = 0; i < data2.size(); i++) {
							data.add(data2.get(i));
						}
						Log.e("---------------", "获取市：" + data.toString());
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
						data2 = AreaManager.getInstance(CustomerSet.this).getAreas(ID2, false);
						if (data2.size() > 0) {//排除没有县级单位的
							for (int i = 0; i < data2.size(); i++) {
								data.add(data2.get(i));
							}
							dialogAdapter.notifyDataSetChanged();
							title.setText("选择区县 :");
						}else {
							alertDialog.dismiss();
							edt04_04.setText(address1 + address2);
						}
						
					}else {
						showCustomToast("请选择市", false);						
					}
										
				}else if(title.getText().toString().trim().equals("选择区县 :")){
					if (address3 != null && !address3.equals("")) {
						
						alertDialog.dismiss();
						edt04_04.setText(address1 + address2 + address3);
					
					}else {
						showCustomToast("请选择县", false);						
					}					
				}																
			}
		});
		
		alertDialog.show();		
//		alertDialog.getWindow().setLayout((int)(widths* 0.85), (int)(heights*0.85));//宽高  
	}
	
	/**
	 * 选择类型  和 上级经销商  公用的Adapter
	 * @author Administrator
	 *
	 */
	public class DialogAdapter2 extends BaseAdapter{
		 
	    private LayoutInflater mInflater;
	    private List<String> data = new ArrayList<String>();
	    private int size;
	    
		public DialogAdapter2(Context context, List<String> data, int size) {
			this.mInflater = LayoutInflater.from(context);
			this.data = data;
			this.size = size;
		}			

		public int getCount() {
			//判断是哪个界面来返回不同的数据长度			
			return size;	
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
						
			holder.tv.setText(data.get(position));						
			holder.img.setImageResource(R.drawable.gou);
			
			//getview从position=0开始加载数据，index默认是-1所以第一次加载都是未选中的
			if (index == position) {
				holder.img.setVisibility(View.VISIBLE);
				
			}else {			
				holder.img.setVisibility(View.INVISIBLE);
			}
						
			return view;
		}
		
		public class ViewHolder{
			public TextView tv;
			public ImageView img;
		}
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
						Log.e("------------------", "ID:" + ID);
						Log.e("------------------", "address1:" + address1);
					}else if (title.getText().toString().trim().equals("选择城市 :")) {
						address2 = data.get(position).getName();
						ID2 = data.get(position).getID();						
						Log.e("------------------", "ID2:" + ID2);
						Log.e("------------------", "address2:" + address2);
					}else if(title.getText().toString().trim().equals("选择区县 :")){
						address3 = data.get(position).getName();	
						ID3 = data.get(position).getID();
						Log.e("------------------", "ID3:" + ID3);
						Log.e("------------------", "address3:" + address3);
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
	
	// 获取listview的高度
	private void setListViewHeight(ListView listView, SimpleAdapter adapter) {
			// 获取ListView对应的Adapter
			adapter = (SimpleAdapter) listView.getAdapter();
			if (adapter == null) {
				return;
			}

			int totalHeight = 0;
			for (int i = 0, len = adapter.getCount(); i < len; i++) {// listAdapter.getCount()返回数据项的数目

				View listItem = adapter.getView(i, null, listView);
				listItem.measure(0, 0); // 计算子项View 的宽高
				totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
			}

			ViewGroup.LayoutParams params = listView.getLayoutParams();
			params.height = totalHeight
					+ (listView.getDividerHeight() * (adapter.getCount() - 1));
			// params.height += 5;// 因为我在listview的属性添加了padding=5dip

			// listView.getDividerHeight()获取子项间分隔符占用的高度
			// params.height最后得到整个ListView完整显示需要的高度
			listView.setLayoutParams(params);
		}

		
	/**
	 * 加载本地图片
	 * @param url
	 * @return
	 */
    public static Bitmap getLoacalBitmap(String url) {
         try {
              FileInputStream fis = new FileInputStream(url);
              return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片         

           } catch (FileNotFoundException e) {
              e.printStackTrace();
              return null;
         }                
         
    }
    
    /**
     * 跳转到照片浏览页面
     */
    private void tiaozhuan(String path){
		Intent intents = new Intent(CustomerSet.this,Customer_Photo.class);
//		intents.putExtra("photo", path);
		startActivityForResult(intents, 4);
	}

    private Drawable makeDrawable(String path){
				
		Drawable drawable = null;
		BitmapFactory.Options options = new BitmapFactory.Options();		
		options.inPreferredConfig = Bitmap.Config.RGB_565;	
		
		drawable = resizeImage(BitmapFactory.decodeFile(path, options),
				(int)getResources().getDimension(R.dimen.width), 
				(int)getResources().getDimension(R.dimen.width));
						
		return drawable;
	}
    
    //这里传入的不是最终想得到的图片宽高，而是与图片进行等比缩放对应的宽高。	
  	public static Drawable resizeImage(Bitmap bitmap, int w, int h) {
  		// 传入图片的宽高
  		int width = bitmap.getWidth();
  		int height = bitmap.getHeight();
  		int newWidth = w;
  		int newHeight = h;
  		// 宽高的比例
  		float scaleWidth = ((float) newWidth) / width;
  		float scaleHeight = ((float) newHeight) / height;
  		// create a matrix for the manipulatio
  		Matrix matrix = new Matrix();
  		// resize the Bitmap
  		matrix.postScale(scaleWidth, scaleHeight);//scaleWidth, scaleHeight


  		Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width,
  				height, matrix, true);

  		bitmap.recycle();//释放图片内存
  		bitmap = null;
  		System.gc();

  		return new BitmapDrawable(newBitmap);
  	}		
    
  	
    /**
	 * 传图片到服务器
	 */
	private void setImageToNet(String path, String FileName) {
		//获得系统时间2014-09-20
		SimpleDateFormat  formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date  curDate=new  Date(System.currentTimeMillis());//获取当前时间
		String  time =formatter.format(curDate);
		
		String companyID = preferences.getInt(Content.COMPS_ID, 1016) + "";
		String modulePage = "Member";
		String picData = "";
		try {
			picData = Base64Util.encodeBase64File(path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   UpLoadImgSignText upLoadImgSignText = new UpLoadImgSignText(picData, modulePage, FileName, companyID, time);
	   upLoadImgSignText.setUpLoadResult(CustomerSet.this);
	   new Thread(upLoadImgSignText).start();
	}

	/**
	 * 继承上传图片接口  实现的方法
	 */
	public void onUpLoadResult(String FileName, String FilePath,
			boolean isSuccess) {
		if (isSuccess) {
			showCustomToast("上传成功", true);
			L.e("上传服务器成功");
			path_serve.add(FilePath);
			path.add(path_new);			
			L.e("上传服务器后path:" + path.toString());
			L.e("服务端图片地址：" + FilePath);
			
			dismissDialog();			
			myHandler.sendEmptyMessage(HanderUtil.case8);
			
		}else {
			showCustomToast("上传失败", false);
			L.e("失败后path:" + path.toString());
			dismissDialog();
		}
	}
	
	private void initPhoto(){//调用系统的拍照功能		
		
		path01 = getSystemTime() + ".jpg";
		path1 = Constant.SAVE_IMG_PATH + File.separator + path01;		
//		preferences.edit().putString("path1", path1).commit();
//		preferences.edit().putString("path01", path01).commit();
		
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File tmp = new File(path1);
		if (!tmp.getParentFile().exists()){
			tmp.getParentFile().mkdirs();//不存在则创建
		}
		intent.putExtra(MediaStore.EXTRA_OUTPUT, 
				Uri.fromFile(tmp));
		startActivityForResult(intent, CallSystemApp.EXTRA_TAKE_PHOTOS);
		L.e("path01:" + path01);
		L.e("path1:" + path1);
		
				
//		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
//
//			File dir = new File(Environment.getExternalStorageDirectory() + "/ycz/pictures");
//			if(!dir.exists()){
//				dir.mkdir();
//			}
//			file = new File(dir, new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())+ ".jpg");
//			startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
//							MediaStore.EXTRA_OUTPUT, Uri.fromFile(file)), REQUEST_CODE_SHOOT);
//		} else {
//			Util.showToast(this, "内存卡不存在");
//		}
				
	}	
		
		
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		path.clear();
		path_serve.clear();
		item03_img01 = null;
		item03_img02 = null;
		item03_img03 = null;
		
	}
	
	/**
	 * 提交的接口
	 * "Insert"
	 */
	private void submit(String Action){
		//封装进Insert的
		ZJRequest<MemberDetail> zjRequest = new ZJRequest<MemberDetail>();
		MemberDetail memberDetail = new MemberDetail();
				
		memberDetail.setAction(Action);
		memberDetail.setRealName(edt01_01.getText().toString());
		memberDetail.setGroup_ID(Group_ID);
		memberDetail.setGroup_Name(Group_Name);
		memberDetail.setLevel_ID(levelID);
		memberDetail.setLevel_Name(levelName);		
		memberDetail.setType(Type);
		memberDetail.setManager_ID(Integer.parseInt(preferences.getString(Constant.HUISHANGYUN_UID, "0")));
		memberDetail.setContact(edt01_05.getText().toString());
		memberDetail.setMobile(edt01_06.getText().toString());
		
		memberDetail.setLoginName(edt02_01.getText().toString());
		memberDetail.setPassword(edt02_02.getText().toString());
				
		memberDetail.setIDNumber(edt03_01.getText().toString());
		memberDetail.setApiID(edt03_02.getText().toString());		
		memberDetail.setChannel(edt03_03.getText().toString());
		memberDetail.setRange(edt03_04.getText().toString());
		memberDetail.setParent_ID(MemberID2);
		memberDetail.setBrand(edt03_06.getText().toString());
		memberDetail.setLat(Lat);
		memberDetail.setLng(Lng);
		memberDetail.setLoc(Loc);//定位信息
		String pathStr = "";
		for (int i = 0; i < path_serve.size(); i++) {
			if (i == 0) {
				pathStr = path_serve.get(i);
			} else {
				pathStr = pathStr  + "#" + path_serve.get(i);
			}
		}
		L.e("pathStr =  " + pathStr);
		memberDetail.setPicture(pathStr);
		Log.e("------------------", "path_serve:" + path_serve.toString());
		Log.e("------------------", "pathStr:" + pathStr);
		memberDetail.setNote(edt03_07.getText().toString());
		
		memberDetail.setCompany_ID(preferences.getInt(Content.COMPS_ID, 0));
		
		zjRequest.setData(memberDetail);
		final String json = JsonUtil.toJson(zjRequest);
	    L.e("(创建客户)json:" + json);
	    			    
		new Thread(){
			public void run() {
				String jsonString = DataUtil.callWebService(Methods.SET_MEMBER, json);
				L.e("(创建客户)jsonString:" + jsonString);
				try {										
					if (jsonString != null) {
						Type type = new TypeToken<ZJResponse<Integer>>(){}.getType();
					    ZJResponse<Integer> zjResponse = JsonUtil.fromJson(jsonString, type);
					    
					    Message message = new Message();
					    if (zjResponse.getCode() == 0) {	
					    	result = zjResponse.getResult();
					    	Log.e("----------------", "创建成功返回ID：" + result);
					    	message.what = HanderUtil.case1;
						    myHandler.sendMessage(message);
						    
						} else {								
						    message.what = HanderUtil.case2;
						    message.obj = zjResponse.getDesc();
						    myHandler.sendMessage(message);
						}
					} else {
						Message message = new Message();
						message.what = HanderUtil.case2;
					    message.obj = "服务器连接失败";
					    myHandler.sendMessage(message);
					}		
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
	}
	
	/**
	 * 更新客户列表
	 */
	private void updataMember() {
		new Thread(new Runnable() {
			public void run() {
				// TODO Auto-generated method stub
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date curDate = new Date(0);// 获取当前时间
				String time = formatter.format(curDate);
				ZJRequest zjRequest = new ZJRequest();
				zjRequest.setCompany_ID(preferences.getInt(Content.COMPS_ID, 0));
				zjRequest.setOperationTime(time);
				L.e("json == " + JsonUtil.toJson(zjRequest));
				String result = DataUtil.callWebService(Methods.UPDATA_MEMBER, JsonUtil.toJson(zjRequest));
				if (result == null) {
					return;
				}
				Type type = new TypeToken<ZJResponse<Members>>() {
				}.getType();
				ZJResponse<Members> zjResponse = JsonUtil.fromJson(
						result, type);
				if (zjResponse.getCode() == 0) {
					List<Members> departments = zjResponse.getResults();
					for (Members members: departments) {
						MemberManager.getInstance(CustomerSet.this).saveMembers(members);
					}
					myHandler.sendEmptyMessage(HanderUtil.case4);
					return;
				} else {
					return;
				}
			}
		}).start();
	}
	
	private ArrayList<HashMap<String, Object>> mList = new ArrayList<HashMap<String,Object>>();
	//获取客户列表
	private class GetLevels implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			ZJRequest zjRequest = new ZJRequest();
			zjRequest.setCompany_ID(preferences.getInt(Content.COMPS_ID, 0));
			String result = DataUtil.callWebService(Methods.CUSTOMER_GETLEVEL, JsonUtil.toJson(zjRequest));
			L.e("result = " + result);
			if (result != null) {
				Type type = new TypeToken<ZJResponse<MemberLevel>>(){}.getType();
				ZJResponse<MemberLevel> zjResponse = JsonUtil.fromJson(result, type);
				if (zjResponse.getCode() == 0) {
					mList.clear();
					List<MemberLevel> levels = zjResponse.getResults();
					for (int i = 0; i < levels.size(); i++) {
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("name", levels.get(i).getName());
						map.put("level", levels.get(i));
						mList.add(map);
					}
					myHandler.sendEmptyMessage(HanderUtil.case3);
				} else {
					Message msg = new Message();
					msg.what = HanderUtil.case2;
					msg.obj = zjResponse.getDesc();
					myHandler.sendMessage(msg);
				}
			} else {
				Message msg = new Message();
				msg.what = HanderUtil.case2;
				msg.obj = "无法访问服务器";
				myHandler.sendMessage(msg);
			}
		}
		
	}
	
	
	/**
	 * 创建地址
	 * @paramID客户ID
	 * @paramAction添加和修改的时候传不同
	 */	
	public void addAdress(int id, String Action){
		    
		//封装进Insert的
		ZJRequest<Order_address> zjRequest = new ZJRequest<Order_address>();
		Order_address order_address = new Order_address();
		
		order_address.setAction(Action);
		order_address.setMember_ID(MemberID);

		order_address.setName(edt04_01.getText().toString());
		order_address.setJob(edt04_02.getText().toString());
		order_address.setMobile(edt04_03.getText().toString());
		order_address.setAddress(edt04_04.getText().toString() + edt04_05.getText().toString());
		order_address.setIMID(edt04_06.getText().toString());
		order_address.setIsDefault(true);//将新增的地址设为默认地址
		order_address.setID(id);
		
		order_address.setArea_ID(ID3);
				
		zjRequest.setData(order_address);
		final String json = JsonUtil.toJson(zjRequest);
	    			    
		new Thread(){
			public void run() {
				String jsonString = DataUtil.callWebService(Methods.ORDER_ADDRESS_ADD, json);
				L.e("(客户模块添加地址)jsonString:" + jsonString);
				try {										
					if (jsonString != null) {
						Type type = new TypeToken<ZJResponse<Order_address>>(){}.getType();
					    ZJResponse zjResponse = JsonUtil.fromJson(jsonString, type);
					    
					    Message message = new Message();
					    if (zjResponse.getCode() == 0) {					    	
					    	message.what = HanderUtil.case5;
						    myHandler.sendMessage(message);
						    
						} else {								
//						    message.what = HanderUtil.case2;
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
		
//	/**
//	 * 显示地址
//	 */
//	private void getAdress(){
//		//实例化请求类
//	    ZJRequest zjRequest = new ZJRequest(); 
//	    //业务员id
//	    zjRequest.setMember_ID(MemberID);
//	    //把要传的参数组装成json字符串。
//	    final String json = JsonUtil.toJson(zjRequest);
//		
//		L.e("(客户详情获取默认地址)json:" + json);
//	    
//		new Thread(){
//			public void run() {
//				try {			
//					String jsonString = DataUtil.callWebService(Methods.ORDER_ADDRESS_SHOW, json);
//					L.e("(客户详情获取默认地址)jsonString:" + jsonString);
//					
//					if (jsonString != null) {						
//						Type type = new TypeToken<ZJResponse<Order_address>>(){}.getType();
//					    ZJResponse zjResponse = JsonUtil.fromJson(jsonString, type);
//					    
//					    Message message = new Message();
//					    if (zjResponse.getCode() == 0) {
//					    	data_address = zjResponse.getResults();					    	
//					    	message.what = HanderUtil.case6;
//						    myHandler.sendMessage(message);
//						    
//						} else {								
//						    message.what = HanderUtil.case2;
//						    message.obj = zjResponse.getDesc();
//						    myHandler.sendMessage(message);
//						}
//					}		
//					
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			};
//		}.start();
//	}
		
//	/**
//	 * 删除地址
//	 */
//	private void delAddress(int ID){
//		//实例化请求类
//	    ZJRequest zjRequest = new ZJRequest(); 
//	    //业务员id
//	    zjRequest.setID(ID);
//	  //把要传的参数组装成json字符串。
//	    final String json = JsonUtil.toJson(zjRequest);
//		
//		L.e("(删除客户地址)json:" + json);
//	    
//		new Thread(){
//			public void run() {
//				try {			
//					String jsonString = DataUtil.callWebService(Methods.ORDER_ADDRESS_DEL, json);
//					L.e("(删除客户地址)jsonString:" + jsonString);
//					
//					if (jsonString != null) {						
//						Type type = new TypeToken<ZJResponse<Order_address>>(){}.getType();
//					    ZJResponse zjResponse = JsonUtil.fromJson(jsonString, type);
//					    
//					    Message message = new Message();
//					    if (zjResponse.getCode() == 0) {					    					    	
//					    	message.what = HanderUtil.case7;
//						    myHandler.sendMessage(message);
//						    
//						} else {								
//						    message.what = HanderUtil.case2;
//						    message.obj = zjResponse.getDesc();
//						    myHandler.sendMessage(message);
//						}
//					}		
//					
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			};
//		}.start();
//	}
	
	/**
	 * 获取访问服务器的Json
	 * 
	 * @return
	 */
	private String getJson(String Table_Name) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(0);// 获取当前时间
		String time = formatter.format(curDate);
		ZJRequest zjRequest = new ZJRequest();
		zjRequest.setCompany_ID(preferences.getInt(Content.COMPS_ID, 0));
		OperTime operTime = new OperTime();
		operTime = OperationTime.getInstance(CustomerSet.this).getOperTime(Table_Name);
		if (operTime == null || operTime.getOperationTime() == null || operTime.getOperationTime().equals("")) {
			zjRequest.setOperationTime(time);
		} else {
			zjRequest.setOperationTime(operTime.getOperationTime());
		}
		L.e("json == " + JsonUtil.toJson(zjRequest));
		return JsonUtil.toJson(zjRequest);

	}
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			
			switch (msg.what) {
			case HanderUtil.case9:
				dismissDialog();
				showCustomToast("更新成功", true);
				sendBroadcast(new Intent().setAction(Constant.HUISHANG_OK_ACTION));
				break;
				
			case HanderUtil.case10:
				dismissDialog();
				showCustomToast("更新失败", false);
				break;
				
			case HanderUtil.case3:
				updataDialog((String) msg.obj);
				break;
				
			case HanderUtil.case1:
				File file = (File) msg.obj;
				setImageToNet(file.getAbsolutePath(), file.getName());
				break;
				
			case HanderUtil.case2:
				dismissDialog();
				showCustomToast("上传失败", false);
				break;
			default:
				break;
			}
		};
	};
	
	/**
	 * 更新基础数据
	 */
	public void updataInfo() {
		new Thread(new UpdataInfo()).start();
		showDialog("正在更新数据...");
	}
	
	/**
	 * 更新数据的线程
	 * @author Pan
	 *
	 */
	private class UpdataInfo implements Runnable {
		private Message msg;
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (getMemberGroup() && getMemberLink()) { //依次更新数据
				mHandler.sendEmptyMessage(HanderUtil.case9);
			} else {
				//更新数据失败
				mHandler.sendEmptyMessage(HanderUtil.case10);
			}
		}
		
		/**
		 * 更新客户列表
		 */
		private boolean getMemberGroup() {
			msg = new Message();
			msg.what = HanderUtil.case3;
			msg.obj = "正在更新客户分组...";
			mHandler.sendMessage(msg);
			String departlist = DataUtil.callWebService(
					Methods.UPDATA_MEMBERGROUP, getJson(MemberManager.MEMBER_GROUP_TABLE_NAME));
			L.e("更新客户分组 =" + departlist);
			if (departlist != null) {
				Type type = new TypeToken<ZJResponse<MemberGroups>>() {
				}.getType();
				ZJResponse<MemberGroups> zjResponse = JsonUtil.fromJson(
						departlist, type);
				if (zjResponse.getCode() == 0) {
					List<MemberGroups> memberGroups = zjResponse.getResults();
					for (int i = 0; i < memberGroups.size(); i++) {
						MemberManager.getInstance(CustomerSet.this).saveGroups(memberGroups.get(i));
					}
					if (memberGroups.size() > 0) {
						OperTime operTime = new OperTime();
						operTime.setOperationTime(memberGroups.get(0).getOperationTime());
						operTime.setTable_Name(MemberManager.MEMBER_GROUP_TABLE_NAME);
						OperationTime.getInstance(CustomerSet.this).saveTime(operTime);
					}
					
					return true;
				} else {
					return false;
				}

			} else {
				return false;
			}
		}
		
		/**
		 * 更新客户详情
		 */
		private boolean getMemberLink() {
			msg = new Message();
			msg.what = HanderUtil.case3;
			msg.obj = "正在更新客户列表...";
			mHandler.sendMessage(msg);
			String departlist = DataUtil.callWebService(
					Methods.UPDATA_MEMBER, getJson2(MemberManager.MEMBER_TABLE_NAME));
			L.e("更新客户 =" + departlist);
			if (departlist != null) {
				Type type = new TypeToken<ZJResponse<Members>>() {
				}.getType();
				ZJResponse<Members> zjResponse = JsonUtil.fromJson(
						departlist, type);
				if (zjResponse.getCode() == 0) {
					List<Members> departments = zjResponse.getResults();
					for (int i = 0; i < departments.size(); i++) {
						MemberManager.getInstance(CustomerSet.this).saveMembers(departments.get(i));
					}
					if (departments.size() > 0) {
						OperTime operTime = new OperTime();
						operTime.setOperationTime(departments.get(0).getOperationTime());
						operTime.setTable_Name(MemberManager.MEMBER_TABLE_NAME);
						OperationTime.getInstance(CustomerSet.this).saveTime(operTime);
						operTime.setTable_Name(MemberManager.MEMBER_IN_GROUP);
						OperationTime.getInstance(CustomerSet.this).saveTime(operTime);
					}
					
					return true;
				} else {
					return false;
				}

			} else {
				return false;
			}
		}
	}
	
	/**
	 * 把图片下载到本地
	 * @param path
	 */
	private void upPic(final String path_serve, final String path, final ImageView imageView){
		new Thread(new Runnable() {

			public void run() {
			//命名可能重复，添加hashCode
			String name = getSystemTime() + path_serve.hashCode() + ".jpg";
			URL url;
			Bitmap bitmap;
			try {
				url = new URL(path);
				InputStream is = url.openStream();
				bitmap = BitmapFactory.decodeStream(is);
//				saveFile(bitmap, name);
				if (saveBitmap2file(bitmap, name)) {
					Message msg = new Message();
					/*Bundle bundle = new Bundle();
					bundle.putString("path_serve", path_serve);
					bundle.putString("name", name);
					bundle.putParcelable("imageView", imageView)*/
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("path_serve", path_serve);
					map.put("name", name);
					map.put("imageView", imageView);
					msg.obj = map;
					msg.what = HanderUtil.case9;
					myHandler.sendMessage(msg);
				}				
				is.close();
			
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			}
		}).start();
	}
	
	//传入下载到的Bitmap 和要命名的名称    下载到本地。
	public void saveFile(Bitmap bm, String fileName) throws IOException {
		
		File dirFile = new File(url_path);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		File myCaptureFile = new File(url_path + fileName);
		BufferedOutputStream bos = new BufferedOutputStream(
		new FileOutputStream(myCaptureFile));
		bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
		bos.flush();
		bos.close();
	}
	
	private String getJson2(String Table_Name) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(0);// 获取当前时间
		String time = formatter.format(curDate);
		ZJRequest zjRequest = new ZJRequest();
		if (MyApplication.preferences.getString(Constant.HUISHANG_TYPE, "").equals("1")) {
			zjRequest.setManager_ID(Integer.parseInt(MyApplication.preferences.getString(Constant.HUISHANGYUN_UID, "")));
		} else {
			zjRequest.setDepartment_ID(MyApplication.preferences.getInt(Constant.HUISHANG_DEPARTMENT_ID, 0));
		}
		zjRequest.setCompany_ID(preferences.getInt(Content.COMPS_ID, 0));
		OperTime operTime = new OperTime();
		operTime = OperationTime.getInstance(this).getOperTime(Table_Name);
		if (operTime == null || operTime.getOperationTime() == null || operTime.getOperationTime().equals("")) {
			zjRequest.setOperationTime(time);
		} else {
			zjRequest.setOperationTime(operTime.getOperationTime());
		}
		L.e("json2 == " + JsonUtil.toJson(zjRequest));
		return JsonUtil.toJson(zjRequest);
	}
	
	public boolean saveBitmap2file(Bitmap bmp,String filename){  
       CompressFormat format= Bitmap.CompressFormat.JPEG;  
       int quality = 100;  
       OutputStream stream = null;  
       try {  
             stream = new FileOutputStream(url_path + filename);  
       } catch (FileNotFoundException e) {  
             e.printStackTrace();  
       }  
 
       return bmp.compress(format, quality, stream);  
    } 
	
	/**
	 * 显示选择对话框
	 */
	private void showDialogs() {
		new AlertDialog.Builder(this).setTitle("设置头像")
				.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
														
				switch (which) {
				case 0:
					Intent intent = new Intent();
					intent.setType("image/*"); // 设置文件类型
					intent.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(intent, 5);
										
					break;
					
				case 1:				
					path01 = getSystemTime() + ".jpg";
					path1 = Constant.SAVE_IMG_PATH + File.separator + path01;		
//					preferences.edit().putString("path1", path1).commit();
//					preferences.edit().putString("path01", path01).commit();
					
					Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					File tmp = new File(path1);
					if (!tmp.getParentFile().exists()){
						tmp.getParentFile().mkdirs();//不存在则创建
					}
					intent2.putExtra(MediaStore.EXTRA_OUTPUT, 
							Uri.fromFile(tmp));
					startActivityForResult(intent2, CallSystemApp.EXTRA_TAKE_PHOTOS);
					break;
					
				default:
					break;
				}
			}


		}).show();
	}
	

}
