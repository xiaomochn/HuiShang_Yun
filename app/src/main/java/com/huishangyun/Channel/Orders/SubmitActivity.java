package com.huishangyun.Channel.Orders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Util.L;
import com.huishangyun.model.Constant;
import com.huishangyun.App.MyApplication;
import com.huishangyun.yun.R;

public class SubmitActivity extends BaseActivity {
	private LinearLayout back;
	private TextView nub, price;
			
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_submit);
		
		nub = (TextView)findViewById(R.id.nub);
		price = (TextView)findViewById(R.id.price);
		price.setText("￥" + getIntent().getStringExtra("count"));
		
		nub.setText(getIntent().getStringExtra("result"));
								
		back = (LinearLayout)findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(SubmitActivity.this, OrderMainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//设置要跳转的页面置顶	
				startActivity(intent);
			}
		});				
	}
	
	public void finishs(View view) {
		if (Integer.parseInt(MyApplication.preferences.getString(Constant.HUISHANG_TYPE,"0")) == 1) {
			L.e("权限：" + Integer.parseInt(MyApplication.preferences.getString(Constant.HUISHANG_TYPE, "0")));
			
			Intent intent = new Intent(SubmitActivity.this, DingDan_CommonActivity.class);			
			if (getIntent().getStringExtra("flage").equals("TUI")) {//如果是退货单就跳到退货的界面。
				intent.putExtra("flage", "TUI");
				L.e("高权限，创建退货单");
			}else{//订货单和复制走这里
				intent.putExtra("flage", "DING");
				L.e("高权限，创建订货单");
			}
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//设置要跳转的页面置顶	
			startActivity(intent);
			
		}else {
			Intent intent = new Intent(SubmitActivity.this, DingDanListActivity.class);
			
			if (getIntent().getStringExtra("flage").equals("TUI")) {//如果是退货单就跳到退货的界面。
				intent.putExtra("flage", "TUI");
				L.e("低权限，创建订货单");
			}else if(getIntent().getStringExtra("flage").equals("DING")){
				intent.putExtra("flage", "DING");
				L.e("低权限，创建订货单");
			}	
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//设置要跳转的页面置顶	
			startActivity(intent);
		}

		finish();
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent intent = new Intent(SubmitActivity.this, OrderMainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//设置要跳转的页面置顶	
		startActivity(intent);
	}
		
}
