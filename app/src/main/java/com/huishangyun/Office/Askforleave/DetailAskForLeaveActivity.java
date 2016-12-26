package com.huishangyun.Office.Askforleave;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Channel.Clues.ClueCustomToast;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Content;
import com.huishangyun.model.Methods;
import com.huishangyun.yun.R;

public class DetailAskForLeaveActivity extends BaseActivity {
	private static final String TAG = null;
	private RelativeLayout back;//返回
	private RelativeLayout addaskforleave;//新增请假
	private ViewPager mPager;
	private ArrayList<Fragment> fragmentList;
	private FragmentForDetail fragment1;
	public int Company_ID;//公司id
	public int Manager_ID;//登录人id
	public int Department_ID;//部门id
	private int CurentManagerID;
	private List<AskForLeaveList> list = new ArrayList<AskForLeaveList>();
	private int flag;
	private TextView Manager_Name;//当前数据人姓名
	private String managerName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_office_askforleave_detail);
		Intent intent = getIntent();
		Manager_ID = intent.getIntExtra("Manager_ID", -1);
		Company_ID = preferences.getInt(Content.COMPS_ID, 1016);
		Department_ID = preferences.getInt(Constant.HUISHANG_DEPARTMENT_ID,0);
		getIntent().putExtra("Company_ID", Company_ID);
		getIntent().putExtra("Department_ID", Department_ID);
		getNetData();
		Log.e(TAG, "--------->:" + Department_ID);
		init();
		
	}
	
	/**
	 * 初始化界面
	 */
	private void init(){
		back = (RelativeLayout) findViewById(R.id.back);
		Manager_Name = (TextView) findViewById(R.id.Manager_Name);
		addaskforleave = (RelativeLayout) findViewById(R.id.addaskforleave);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		addaskforleave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(DetailAskForLeaveActivity.this,AddAskForLeaveActivivty.class);
				startActivity(intent);
			}
		});
		
	}

	/**
	 * 获取managerid的集合
	 */
	private void getNetData(){
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub					
				String result = DataUtil.callWebService(
						Methods.GET_LEAVE_MANGAGER_LIST,
						getJson());	
				Log.e(TAG, "result:" + result );
				Log.e(TAG, "=========>:" + getJson());
				
				//先判断网络数据是否获取成功，防止网络不好导致程序崩溃
				if (result != null) {
					// 获取对象的Type
					Type type = new TypeToken<ZJResponse<AskForLeaveList>>() {
					}.getType();
					ZJResponse<AskForLeaveList> zjResponse = JsonUtil.fromJson(result,
							type);
					// 获取对象列表
					list.clear();
					list = zjResponse.getResults();
					mHandler.sendEmptyMessage(0);
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
	 * @param pageSize 单页显示条数
	 * @param pageIndex 页码
	 * @return
	 */
	private String getJson() {
		
		ZJRequest zjRequest = new ZJRequest();
		// 公司id
		zjRequest.setCompany_ID(Company_ID);	
		// 设置部门号，0时为相当没有部门编号查询
		zjRequest.setDepartment_ID(Department_ID);
		return JsonUtil.toJson(zjRequest);

	}
	
	/**
	 * 动态更新UI
	 */
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				InitViewPager();
				break;
			case 1:
				showDialog("未获得任何数据，请检查网络连接！"); 
			   break;
			case 2:
				Manager_Name.setText(managerName);
				Intent intent = new Intent(); 
				intent.setAction("AskForLeave_DETAIL");
				intent.putExtra("CurentManagerID", CurentManagerID);
				DetailAskForLeaveActivity.this.sendBroadcast(intent);
				break;
			default:
				break;
			}
		};
	};
	
	/**
	 * toast
	 * 提醒方法
	 */
	public void showDialog(String TXT){
		new ClueCustomToast().showToast(DetailAskForLeaveActivity.this,
				R.drawable.toast_warn, TXT);

	}
	
	/**
	 * 初始化ViewPager
	 */
	public void InitViewPager(){
		
		mPager = (ViewPager)findViewById(R.id.viewpager);
		fragmentList = new ArrayList<Fragment>();
		Log.e(TAG, "11111111:" +list.size() );
		for (int i = 0; i < list.size(); i++) {
			fragment1 = new FragmentForDetail();
			fragmentList.add(fragment1);
		}
		
		Log.e(TAG, "00000000000:" +fragmentList.size() );
	    for (int i = 0; i < list.size(); i++) {
	    	if (list.get(i).getManager_ID() == Manager_ID) {
	    		CurentManagerID = list.get(i).getManager_ID();
	    		managerName = list.get(i).getManager_Name();
	    		flag = i;
				break;
			}
		}
	    
		//给ViewPager设置适配器
		mPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList));
		mPager.setCurrentItem(flag);//设置当前显示标签页为第一页
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());//页面变化时的监听器
		mHandler.sendEmptyMessage(2);
	
	}

	

     /**
      * viewpager滑动选项卡监听
      * @author xsl
      *
      */
	public class MyOnPageChangeListener implements OnPageChangeListener{
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
		    CurentManagerID = list.get(arg0).getManager_ID();
		    managerName = list.get(arg0).getManager_Name();
		    mHandler.sendEmptyMessage(2);
			//发送普通广播消息机制
			Intent intent = new Intent(); 
			intent.setAction("AskForLeave_DETAIL");
			intent.putExtra("CurentManagerID", CurentManagerID);
//			intent.putExtra("index", arg0);
			DetailAskForLeaveActivity.this.sendBroadcast(intent);
	 }
	}
	
	/**
	 * Fragment适配器
	 * @author xsl
	 *
	 */
	public class MyFragmentPagerAdapter extends FragmentPagerAdapter{
		ArrayList<Fragment> list;  
		public MyFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> list) {
			super(fm);
			 this.list = list; 
		}

		@Override
		public Fragment getItem(int arg0) {
			// TODO Auto-generated method stub
			return list.get(arg0);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}
		
	}

}
