package com.huishangyun.Office.Attendance;


import java.util.Calendar;

import com.huishangyun.App.MyApplication;
import com.huishangyun.Office.Information.WebViewControl;
import com.huishangyun.Util.L;
import com.huishangyun.yun.R;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;


public class MyAttendanceSecondFragment extends Fragment {
	private static final String TAG = null;
	private View departmentView;
	private WebView webView;
	private TextView Janurary;//月份
	private TextView February;
	private TextView March;
	private TextView April;
	private TextView May;
	private TextView June;
	private TextView July;
	private TextView August;
	private TextView September;
	private TextView October;
	private TextView November;
	private TextView December;
	private Calendar cal;
	private int year;//当前年份
	private int month;//月份
	private String mUrl = "http://webapp.huishangyun.com/Adminlogin.aspx?";

	
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	if (departmentView==null) {
	 departmentView = inflater.inflate(R.layout.activity_office_myattendance_secondfragment, null);
	webView = (WebView) departmentView.findViewById(R.id.webview1);
	init();
	}
	return departmentView;
}

/**
 * 销毁fragment
 */
@Override
public void onDestroyView() {
    Log.e(TAG , "-->onDestroyView");
    super .onDestroyView();
    if (null != departmentView) {
        ((ViewGroup) departmentView.getParent()).removeView(departmentView);
    }
}

/**
 * 设置月份初始属性
 */
private void setTextView(){
	Janurary.setBackgroundResource(R.drawable.ed);
	February.setBackgroundResource(R.drawable.ed);
	March.setBackgroundResource(R.drawable.ed);
	April.setBackgroundResource(R.drawable.ed);
	May.setBackgroundResource(R.drawable.ed);
	June.setBackgroundResource(R.drawable.ed);
	July.setBackgroundResource(R.drawable.ed);
	August.setBackgroundResource(R.drawable.ed);
	September.setBackgroundResource(R.drawable.ed);
	October.setBackgroundResource(R.drawable.ed);
	November.setBackgroundResource(R.drawable.ed);
	December.setBackgroundResource(R.drawable.ed);

	Janurary.setTextColor(0xff969696);
	February.setTextColor(0xff969696);
	March.setTextColor(0xff969696);
	April.setTextColor(0xff969696);
	May.setTextColor(0xff969696);
	June.setTextColor(0xff969696);
	July.setTextColor(0xff969696);
	September.setTextColor(0xff969696);
	October.setTextColor(0xff969696);
	August.setTextColor(0xff969696);
	November.setTextColor(0xff969696);
	December.setTextColor(0xff969696);
}

/**
 * 设置默认月份事件
 */
private void setDefaultMonth(int month){
	
	if (month==1) {
		setTextView();
		Janurary.setBackgroundResource(R.drawable.bt);
		Janurary.setTextColor(0xffffffff);
	}else if (month==2) {
		setTextView();
		February.setBackgroundResource(R.drawable.bt);
		February.setTextColor(0xffffffff);
	}else if (month==3) {
		setTextView();
		March.setBackgroundResource(R.drawable.bt);
		March.setTextColor(0xffffffff);
	}else if (month==4) {
		setTextView();
		April.setBackgroundResource(R.drawable.bt);
		April.setTextColor(0xffffffff);
	}else if (month==5) {
		setTextView();
		May.setBackgroundResource(R.drawable.bt);
		May.setTextColor(0xffffffff);
	}else if (month==6) {
		setTextView();
		June.setBackgroundResource(R.drawable.bt);
		June.setTextColor(0xffffffff);
	}else if (month==7) {
		setTextView();
		July.setBackgroundResource(R.drawable.bt);
		July.setTextColor(0xffffffff);
	}else if (month==8) {
		setTextView();
		August.setBackgroundResource(R.drawable.bt);
		August.setTextColor(0xffffffff);
	}else if (month==9) {
		setTextView();
		September.setBackgroundResource(R.drawable.bt);
		September.setTextColor(0xffffffff);
	}else if (month==10) {
		setTextView();
		October.setBackgroundResource(R.drawable.bt);
		October.setTextColor(0xffffffff);
	}else if (month==11) {
		setTextView();
		November.setBackgroundResource(R.drawable.bt);
		November.setTextColor(0xffffffff);
	}else if (month==12) {
		setTextView();
		December.setBackgroundResource(R.drawable.bt);
		December.setTextColor(0xffffffff);
	}
	
}

/**
 * 控件初始化
 */
private void init(){
	cal = Calendar.getInstance();
	year = cal.get(Calendar.YEAR);
	month = cal.get(Calendar.MONTH) + 1;
	Janurary = (TextView) departmentView.findViewById(R.id.Janurary);
	February = (TextView) departmentView.findViewById(R.id.February);
	March = (TextView) departmentView.findViewById(R.id.March);
	April = (TextView) departmentView.findViewById(R.id.April);
	May = (TextView) departmentView.findViewById(R.id.May);
	June = (TextView) departmentView.findViewById(R.id.June);
	July = (TextView) departmentView.findViewById(R.id.July);
	August = (TextView) departmentView.findViewById(R.id.August);
	September = (TextView) departmentView.findViewById(R.id.September);
	October = (TextView) departmentView.findViewById(R.id.October);
	November = (TextView) departmentView.findViewById(R.id.November);
	December = (TextView) departmentView.findViewById(R.id.December);
	
	Janurary.setOnClickListener(buListener);
	February.setOnClickListener(buListener);
	March.setOnClickListener(buListener);
	April.setOnClickListener(buListener);
	May.setOnClickListener(buListener);
	June.setOnClickListener(buListener);
	July.setOnClickListener(buListener);
	August.setOnClickListener(buListener);
	September.setOnClickListener(buListener);
	October.setOnClickListener(buListener);
	November.setOnClickListener(buListener);
	December.setOnClickListener(buListener);
	setDefaultMonth(month);
	LoadWebView(year + "" + month);
}

	/**
	 * 单击事件
	 */
	private OnClickListener buListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.Janurary://一月
				LoadWebView(year + "01");
				setDefaultMonth(1);
				break;
			case R.id.February://二月
				LoadWebView(year + "02");
				setDefaultMonth(2);
				break;
			case R.id.March://三月
				LoadWebView(year + "03");
				setDefaultMonth(3);
				break;
			case R.id.April://四月
				LoadWebView(year + "04");
				setDefaultMonth(4);
				break;
			case R.id.May://五月
				LoadWebView(year + "05");
				setDefaultMonth(5);
				break;
			case R.id.June://六月
				LoadWebView(year + "06");
				setDefaultMonth(6);
				break;
			case R.id.July://七月
				LoadWebView(year + "07");
				setDefaultMonth(7);
				break;
			case R.id.August://八月
				LoadWebView(year + "08");
				setDefaultMonth(8);
				break;
			case R.id.September://九月
				LoadWebView(year + "09");
				setDefaultMonth(9);
				break;
			case R.id.October://十月
				LoadWebView(year + "10");
				setDefaultMonth(10);
				break;
			case R.id.November://十一月
				LoadWebView(year + "11");
				setDefaultMonth(11);
				break;
			case R.id.December://十二月
				LoadWebView(year + "12");
				setDefaultMonth(12);
				break;
			default:
				break;
			}

		}
	};

	
/**
 * 加载webview
 */
@SuppressLint("NewApi")
private void LoadWebView(String mh){
	
	webView.getSettings().setSupportZoom(true);          //支持缩放
	webView.getSettings().setBuiltInZoomControls(true);  //启用内置缩放装置
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
        	 getActivity().setProgress(newProgress * 100);
        	 L.e("newProgress:" + newProgress);
        	 if (newProgress==100) {
//        		 percent.setVisibility(View.GONE);
			}else {
//				percent.setVisibility(View.VISIBLE);
//			    percent.setProgress(newProgress);
			}

         }
         //获取网页标题
         @Override
        public void onReceivedTitle(WebView view, String title) {
        	// TODO Auto-generated method stub
        	super.onReceivedTitle(view, title);
//        	 headname.setText(title);
        
        }

     }); 
    
     String url = mUrl + "d=" + MyApplication.getInstance().getCompanyID()
				+ "&n=" + getActivity().getIntent().getStringExtra("UseName") + "&p=" 
				+ getActivity().getIntent().getStringExtra("PassWords") 
				+ "&u=OA/Attendance/AttendanceReport.aspx?Month="+ mh;
     L.e("url = " + url);
     

     webView.loadUrl(url);
}

}
