package com.huishangyun.Office.Information;

import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Util.L;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Content;
import com.huishangyun.yun.R;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainInformation extends BaseActivity {
	private RelativeLayout back;//返回
	private FrameLayout collection;//收藏
	private TextView headname;//头名
	private ProgressBar percent;//加载网页百分比
	private WebView webView;
	private String url = "http://webapp.huishangyun.com/Adminlogin.aspx?";//OA信息
//	private String url = "http://220.175.122.90:703/Adminlogin.aspx?";//OA信息
	
	private String pathurl;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_office_information_main);
		init();
	}
	
	/**
	 * 初始化控件
	 */
  @SuppressLint("NewApi")
private void init() {
	back = (RelativeLayout) findViewById(R.id.back);
	collection = (FrameLayout) findViewById(R.id.collection);
	headname = (TextView) findViewById(R.id.headname);
	percent = (ProgressBar) findViewById(R.id.percent);
	
	back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			finish();
		}
	});
	
//	pathurl = url + "d=" + preferences.getString(Content.COMPS_DOMAIN, "") + "&n=" 
//			+ preferences.getString(Constant.USERNAME, "") + "&p=" 
//			+ preferences.getString(Constant.PASSWORD, "")+"&u="+"OA/Article/Index.aspx";
//	
//	L.e("==========>", pathurl);
	
	webView = (WebView) findViewById(R.id.webview1);
	
	webView.getSettings().setSupportZoom(true);          //支持缩放
	//启用内置缩放功能，但隐藏缩放装置
	WebViewControl.setZoomViewInvisible(webView);
	 //设置WebView属性，能够执行Javascript脚本       
	webView.getSettings().setJavaScriptEnabled(true);  
	//加载网络地址
	 webView.removeJavascriptInterface("searchBoxJavaBredge_");
	
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
        	 MainInformation.this.setProgress(newProgress * 100);
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
        	 headname.setText(title);
        }

     }); 
     
     url = url + "d=" + preferences.getInt(Content.COMPS_ID, 0)
				+ "&n=" + preferences.getString(Constant.USERNAME, "") + "&p="
				+ preferences.getString(Constant.PASSWORD, "") + "&u=OA/Article/Index.aspx";
	  Log.e("==========>", "url门户="+url);
     
//     webView.loadUrl("http://www.youku.com");
     webView.loadUrl(url);
   
}
  
  
}
