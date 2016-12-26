package com.huishangyun.Activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.huishangyun.App.MyApplication;
import com.huishangyun.Util.FaceUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.StringUtil;
import com.huishangyun.Util.TimeUtil;
import com.huishangyun.manager.DepartmentManager;
import com.huishangyun.manager.GroupManager;
import com.huishangyun.manager.MemberManager;
import com.huishangyun.manager.ServiceManager;
import com.huishangyun.model.Constant;
import com.huishangyun.model.ScreenEntity;
import com.huishangyun.yun.R;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 锁屏弹出窗口
 * @author Pan
 *
 */
public class ScreenActivity extends Activity{
	private ListView mListView;
	private ImageView mImageView;
	private List<ScreenEntity> mList;
	private ScreenAdapter screenAdapter;
	private long startTime = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		/*final Window win = getWindow();
		 win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
		 | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		 win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
		 | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);*/
		setContentView(R.layout.activity_screen);
		
		initView();
	}
	
	/**
	 * 实例化各组件
	 */
	private void initView() {
		//获取系统服务  
		mListView = (ListView) findViewById(R.id.screen_listview);
		mImageView = (ImageView) findViewById(R.id.screen_finish);
		mImageView.setOnClickListener(mClickListener);
		mList = (List<ScreenEntity>) getIntent().getExtras().get("screenEntities");
		screenAdapter = new ScreenAdapter();
		mListView.setAdapter(screenAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
			@SuppressLint("NewApi")
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				long endTime = System.currentTimeMillis();
				if (endTime - startTime > 2000) {
					startTime = endTime;
					//showCustomToast("双击进入慧商云", false);
				} else {
					
					//keyguardLock.disableKeyguard();
					PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
					PowerManager.WakeLock mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.SCREEN_DIM_WAKE_LOCK, "SimpleTimer");
					mWakelock.acquire();
					mWakelock.release();
					KeyguardManager mKeyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
					L.e("isKeyguardSecure = " + mKeyguardManager.isKeyguardSecure());
					if (!mKeyguardManager.isKeyguardSecure()) {
						KeyguardLock keyguardLock = mKeyguardManager.newKeyguardLock("");
						keyguardLock.disableKeyguard();
					}
					finish();
				}
			}
		});
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(Constant.HUISHANG_SCREEN_ACTION);
		mFilter.setPriority(1000);
		registerReceiver(mBroadcastReceiver, mFilter);
	}
	
	/**
	 * 点击事件监听
	 */
	private OnClickListener mClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.screen_finish:
				finish();
				break;
			default:
				break;
			}
		}
	};
	
	/**
	 * 接收广播刷新列表
	 */
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);  
			if (!pm.isScreenOn()) {//如果屏幕未点亮，则点亮屏幕
				PowerManager.WakeLock m_wakeLockObj = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
	                    | PowerManager.ACQUIRE_CAUSES_WAKEUP
	                    | PowerManager.ON_AFTER_RELEASE, "My Tag");


	            m_wakeLockObj.acquire(5);
			}
			mList = (List<ScreenEntity>) intent.getExtras().get("screenEntities");
			screenAdapter = new ScreenAdapter();
			mListView.setAdapter(screenAdapter);
		}
		
	};
	
	protected void onDestroy() {
		//保存显示状态
		MyApplication.preferences.edit().putBoolean(Constant.HUISHANG_SCREEN_SHOW, false).commit();
		unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	};
	
	/**
	 * 消息列表适配器
	 * @author Administrator
	 *
	 */
	private class ScreenAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder mHolder;
			if (convertView == null) {
				mHolder = new ViewHolder();
				convertView = LayoutInflater.from(ScreenActivity.this).inflate(R.layout.screen_item, null);
				mHolder.Name = (TextView) convertView.findViewById(R.id.screen_name);
				mHolder.Time = (TextView) convertView.findViewById(R.id.screen_time);
				mHolder.Content = (TextView) convertView.findViewById(R.id.screen_content);
				mHolder.Size = (TextView) convertView.findViewById(R.id.screen_size);
				convertView.setTag(mHolder);
			} else {
				mHolder = (ViewHolder) convertView.getTag();
			}
			
			//判断是否为群
			if (mList.get(position).getIsGroup()) {
				String name = GroupManager.getInstance(ScreenActivity.this).getGroupName(mList.get(position).getID() + "");
				if (name == null) {
					name = mList.get(position).getID() + "";
				}
				if (name.contains("0_n")||name.equals("")){
					mHolder.Name.setText("系统消息");
				}else
				{
					mHolder.Name.setText(name);
				}
				String JID = mList.get(position).getName();
				//依次判断本地是否有该账号的资料
				String userName =  DepartmentManager.getInstance(ScreenActivity.this).getManager(JID);
				if (userName == null || userName.equals("")) {
					userName = MemberManager.getInstance(ScreenActivity.this).getMember(JID);
                    if (userName == null || userName.equals("")) {
							userName = mList.get(position).getName();
					}
				}
				mHolder.Content.setText(userName +": " + FaceUtil.convertNormalStringToSpannableString(ScreenActivity.this, mList.get(position).getContent(), true));
				mHolder.Size.setText(mList.get(position).getSize());
				SimpleDateFormat sdf = new SimpleDateFormat(Constant.MS_FORMART);
				long date = TimeUtil.getLongtime(mList.get(position).getTime());
				mHolder.Time.setText(TimeUtil.getScreenTime(date));
				
			} else {
				String JID = mList.get(position).getName();
				L.e("JID = " + JID);
                //依次判断本地是否有该账号的资料
				String name = DepartmentManager.getInstance(ScreenActivity.this).getManager(JID);
				if (name == null || name.equals("")) {
					name = MemberManager.getInstance(ScreenActivity.this).getMember(JID);
					if (name == null || name.equals("")) {
						if (name == null || name.equals("")) {
							name = ServiceManager.getInstance(ScreenActivity.this).getServiceName(JID);
                            if (name == null || name.equals("")) {
                                name = StringUtil.getUserNameByJid(mList.get(position).getName());
                            }
						}
					}
				}
				Log.e("TAGS" ,"name显示名字="+ name);
				if (name.contains("0_n")||name.equals("")){
					mHolder.Name.setText("系统消息");
				}else
				{
					mHolder.Name.setText(name);
				}
				mHolder.Content.setText(FaceUtil.convertNormalStringToSpannableString(ScreenActivity.this, mList.get(position).getContent(), true));
				mHolder.Size.setText(mList.get(position).getSize());
				SimpleDateFormat sdf = new SimpleDateFormat(Constant.MS_FORMART);
				long date = TimeUtil.getLongtime(mList.get(position).getTime());
				mHolder.Time.setText(TimeUtil.getScreenTime(date));
			}
			
			return convertView;
		}
		
	}
	
	/**
	 * 控件适配器
	 * @author Pan
	 *
	 */
	static class ViewHolder {
		public TextView Name;
		public TextView Time;
		public TextView Content;
		public TextView Size;
	}
}
