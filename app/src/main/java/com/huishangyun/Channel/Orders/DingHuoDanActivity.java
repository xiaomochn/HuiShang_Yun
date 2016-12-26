package com.huishangyun.Channel.Orders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huishangyun.yun.R;

/**
 * 未使用的类
 * @author Administrator
 *
 */
public class DingHuoDanActivity extends Activity {
	private LinearLayout back;
	private RelativeLayout info, qingdan;
	private TextView address, price, count;
	private EditText beizhu;
	private Button submit;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_dinghuodan);
		
		init();
	}
	
	private void init(){
		back = (LinearLayout)findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		info = (RelativeLayout)findViewById(R.id.info);
		info.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(DingHuoDanActivity.this, AddressActivity.class);
				startActivity(intent);
			}
		});
		qingdan = (RelativeLayout)findViewById(R.id.qingdan);
		qingdan.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(DingHuoDanActivity.this, GoodsListActivity.class);
				startActivity(intent);
			}
		});
	}
}
