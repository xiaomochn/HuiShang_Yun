package com.huishangyun.Channel.Customers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Channel.Customers.CustomerSet.DialogAdapter;
import com.huishangyun.common.AddImage;
import com.huishangyun.model.AreaModel;
import com.huishangyun.yun.R;

public class CustomerDetails extends BaseActivity{
	private int MemberID;
	private int widths, heights;	
	private EditText edt01_01, edt02_01, edt02_02, edt02_03,
	edt03_01, edt03_02, edt03_03, edt03_04, edt03_05, edt03_06,
	edt04_01, edt04_02, edt04_03, edt04_05, edt04_06;
	
	private TextView edt01_02, edt01_03, edt01_04, edt01_05, edt01_06, edt04_04, dingwei_address;
	private ImageView item03_img01, item03_img02, item03_img03;
	
	private LinearLayout back, lin01, lin02, lin03, lin04, dingwei;
	private RelativeLayout rel01, rel02, rel03, rel04;
	private ImageView img01, img02, img03, img04, dingwei_img;
	/**
	 * 一级菜单的伸缩判断
	 */
	private boolean flage1 = false;
	private boolean flage2 = false;
	private boolean flage3 = false;
	private boolean flage4 = false;
	/**
	 * 等级的伸缩判断
	 */
	boolean flage01 = false;
	boolean flage02 = false;
	boolean flage03 = false;
	boolean flage04 = false;
	/**
	 * 类型的伸缩判断
	 */
	boolean flage05 = false;
	boolean flage06 = false;
		
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
	private int ID = 1, ID2 = 1, ID3 = 1;
	
	/**
	 * 拍照功能的变量
	 */
	private String path1, path2, path3;//照片本地路径
	private String path01, path02, path03;//照片名称
	public static List<AddImage> list_img = new ArrayList<AddImage>();//照片路径
	public static List<String> path = new ArrayList<String>();
	public static List<String> path_serve = new ArrayList<String>();
	public static int currentItem = 0; // 当前图片的索引号
	private Calendar calendar;
	private ProgressDialog pDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_customer_details);
		initView();
	}
	
	private void initView() {
		MemberID = getIntent().getIntExtra("ID", 0);
		// 获得手机的宽度和高度
	}
}
