package com.huishangyun.Activity;


import com.huishangyun.yun.R;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class WriterEmail extends BaseActivity implements OnClickListener{
	
	private LinearLayout layTop, layContent;
	private Button btnSend;
	private String Email;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_writeremail);
		Email = getIntent().getStringExtra("Email");
		//initTitleChat("写邮件", null, null, null);
		init();
		addListener();
		
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_send:
			for(int i=0;i<layTop.getChildCount();i++){
				EditText etTemp = (EditText)layTop.getChildAt(i).findViewById(R.id.et_writeremail_content);
				Log.d("debug -->>", "hint == "+etTemp.getHint());
			}
			break;
		case R.id.img_writeremail_addicon:
			startActivity(new Intent(this, CheckContacter.class));
			break;
		}
	}
	
	private void init(){
		layTop = (LinearLayout)this.findViewById(R.id.lay_writeremail_toplay);
		layContent = (LinearLayout)this.findViewById(R.id.lay_writeremail_content);
		btnSend = (Button)this.findViewById(R.id.btn_send);
		
		layTop.addView(getWriteEmailLine("收件人：", 1, this, null));
		layTop.addView(getWriteEmailLine("抄送：", 2, this, null));
		layTop.addView(getWriteEmailLine("密送:", 3, this, null));
		layContent.addView(getWriteEmailLine("主题:", 4, null, R.drawable.icon_attach_mini));
	}
	
	private void addListener(){
		btnSend.setOnClickListener(this);
	}
	
	private LinearLayout getWriteEmailLine(String hint,int tag, OnClickListener listener, Integer imgResId){
		LinearLayout result = (LinearLayout)LayoutInflater.from(this)
				.inflate(R.layout.writeremail_line, null);
		
		ImageView imgAdd = (ImageView)result.findViewById(R.id.img_writeremail_addicon);
		if(imgResId != null)
			imgAdd.setImageResource(imgResId);
		EditText etItem = (EditText)result.findViewById(R.id.et_writeremail_content);
		etItem.setHint(hint);
		imgAdd.setTag(tag);
		imgAdd.setOnClickListener(listener);
		
		return result;
	}

}
