package com.huishangyun.Channel.stock;

import com.huishangyun.Office.Information.WebViewControl;
import com.huishangyun.Util.L;
import com.huishangyun.model.Constant;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.model.Content;
import com.huishangyun.yun.R;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

/**
 * 库存报表界面
 * 
 * @author 熊文娟
 * 
 */
public class StockMainActivity extends BaseActivity {

	private LinearLayout backLayout = null;//返回
	private LinearLayout addLayout = null;//新增
	private Intent mIntent = null;
	private WebView webView;
	private ProgressBar percent;
	private String mUrl = "http://webapp.huishangyun.com/Adminlogin.aspx?";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stock_table);
		initView();
	}

	/**
	 * 初始化组件
	 * 
	 */
	private void initView() {
		// TODO Auto-generated method stub
		addLayout = (LinearLayout) findViewById(R.id.root_kucun_add);
		backLayout = (LinearLayout) findViewById(R.id.root_back);
		percent = (ProgressBar) findViewById(R.id.percent);
		webView = (WebView) this.findViewById(R.id.webview1);
		// 添加事件
		addLayout.setOnClickListener(new myOnClickListener());
		backLayout.setOnClickListener(new myOnClickListener());
		LoadWebView();
	}

	private class myOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.root_back:// 返回
				finish();
				break;
			case R.id.root_kucun_add:// 新增
				mIntent = new Intent(StockMainActivity.this, StockReport.class);
				startActivity(mIntent);
				break;
			default:
				break;
			}

		}

	}
	
	/**
	 * 加载webview
	 */
	@SuppressLint("NewApi")
	private void LoadWebView(){
		
		webView.getSettings().setSupportZoom(true);          //支持缩放
		webView.getSettings().setBuiltInZoomControls(true);  //启用内置缩放装置
		 webView.removeJavascriptInterface("searchBoxJavaBredge_");
		WebViewControl.setZoomViewInvisible(webView);
		 //设置WebView属性，能够执行Javascript脚本       
		webView.getSettings().setJavaScriptEnabled(true);  
		//加载网络地址
	    
		
		 webView.setWebViewClient(new WebViewClient() {

	         //当点击链接时,希望覆盖而不是打开新窗口
	         public boolean shouldOverrideUrlLoading(WebView view, String url) {
	             view.loadUrl(url);  //加载新的url
	             return true;    //返回true,代表事件已处理,事件流到此终止
	         }
	         
	     });
		 
	     //点击后退按钮,让WebView后退一页(也可以覆写Activity的onKeyDown方法)
	     webView.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				
				if (event.getAction() == KeyEvent.ACTION_DOWN) {

	                if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {

	                    webView.goBack();   //后退

	                    return true;    //已处理

	                }

	            }

				return false;
			}

	     });

	                     
	     webView.setWebChromeClient(new WebChromeClient() {

	         //当WebView进度改变时更新窗口进度

	         @Override

	         public void onProgressChanged(WebView view, int newProgress) {

	             //Activity的进度范围在0到10000之间,所以这里要乘以100
	        	 StockMainActivity.this.setProgress(newProgress * 100);
	        	 L.e("newProgress:" + newProgress);
	        	 if (newProgress==100) {
	        		 percent.setVisibility(View.GONE);
				}else {
					percent.setVisibility(View.VISIBLE);
				    percent.setProgress(newProgress);
				}

	         }
	         //获取网页标题
	         @Override
	        public void onReceivedTitle(WebView view, String title) {
	        	// TODO Auto-generated method stub
	        	super.onReceivedTitle(view, title);
//	        	 headname.setText(title);
	        	 if (title.contains("MemberID")) {
	        		String Member_ID = title.replace("MemberID=", "");
	        		L.e("Member_ID:" + Member_ID);
	        		Intent webviewIntent = new Intent(StockMainActivity.this, StockRecord.class);
	 	        	webviewIntent.putExtra("Member_ID", Integer.parseInt(Member_ID));
	 	        	webviewIntent.putExtra("falge", "MIAN");
	 				startActivity(webviewIntent);
				}
	        	
	        	 
	        }

	     }); 
	    String url = mUrl + "d=" + preferences.getInt(Content.COMPS_ID, 0) 
					+ "&n=" + preferences.getString(Constant.USERNAME, "") + "&p="
					+ preferences.getString(Constant.PASSWORD, "") + "&u=Crm/Sales/MemberStatock.aspx";
	     L.e("url = " + url);
	   
	     webView.loadUrl(url);
	}
	
	
	
}
