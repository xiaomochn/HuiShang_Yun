package com.huishangyun.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huishangyun.Util.HttpThread;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ResponseBean;
import com.huishangyun.task.DownLoadFileThreadTask;
import com.huishangyun.download.DownloadInterface;
import com.huishangyun.yun.R;

import java.io.File;

public class ChatWebview extends BaseActivity implements View.OnClickListener,DownloadInterface {
	private WebView myweb; // webView
	private ProgressBar title_bar; // 标题栏进度条
	private ProgressBar loadingTv; // 中间进度条
	private ImageButton back; // 后退
	private ImageButton forward; // 前进
	private ImageButton refresh; // 刷新
	private ProgressDialog dialog = null; //下载进度条
	private TextView titleView;//标题栏
	private String url;//要访问的网址
	private final String FILE_PATH = Environment.getExternalStorageDirectory().toString() + "/HSY_Yun/download";
	private String save_file_name;
	private LinearLayout finish;
	private RelativeLayout parent;

	Handler mhandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case 0:
					myweb.loadUrl(url);
					break;
			}
		}
	};

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
		setContentView(R.layout.chat_service_webview);
		url = getIntent().getStringExtra("httpurl");
		initView();
		setListener();
		initWeb();
		loadurl(myweb, url);
	}
	
	/**
	 * 初始化控件
	 */
	private void initView() {
		myweb = (WebView) findViewById(R.id.mychatweb);
		title_bar = (ProgressBar) findViewById(R.id.loadingbar);
		loadingTv = (ProgressBar) findViewById(R.id.loadingTv);
		back = (ImageButton) findViewById(R.id.btn_back);
		forward = (ImageButton) findViewById(R.id.btn_go);
		refresh = (ImageButton) findViewById(R.id.btn_refresh);
		titleView = (TextView) findViewById(R.id.title);
		titleView.setText("正在加载");
		finish = (LinearLayout) findViewById(R.id.web_title_back);
		parent = (RelativeLayout) findViewById(R.id.webviewRelativeLayout);
	}
	
	/**
	 * 设置监听器
	 */
	private void setListener() {
		// TODO Auto-generated method stub
		back.setOnClickListener(this);
		forward.setOnClickListener(this);
		refresh.setOnClickListener(this);
		finish.setOnClickListener(this);
	}
	
	/**
	 * 载入链接
	 * @param view
	 * @param url
	 */
	public void loadurl(final WebView view, final String url) {
	/*	new Thread() {
			public void run() {*/
				view.loadUrl(url);// 载入网页
	/*		}
		}.start();*/
	}
	
	/**
	 * 初始化webView
	 */
	@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
	private void initWeb() {
		myweb.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);// 滚动条风格，为0就是不给滚动条留空间，滚动条覆盖在网页上
		/*WebSettings ws = myweb.getSettings();
		ws.setJavaScriptEnabled(true); // 设置支持javascript脚本
		ws.setAllowFileAccess(true); // 允许访问文件
		ws.setBuiltInZoomControls(true); // 设置不显示缩放按钮
		ws.setSupportZoom(true); // 支持缩放
		ws.setUseWideViewPort(true);*/
		//ws.setDisplayZoomControls(false);
		
		myweb.getSettings().setJavaScriptEnabled(true);
		myweb.getSettings().setDomStorageEnabled(true);
		myweb.removeJavascriptInterface("searchBoxJavaBredge_");
		myweb.getSettings().setAppCacheEnabled(true);
        String appCacheDir = this.getApplicationContext()
                        .getDir("cache", Context.MODE_PRIVATE).getPath();
        //myweb.getSettings().setPluginsEnabled(true);
        myweb.getSettings().setUseWideViewPort(true);
        myweb.getSettings().setAllowFileAccess(true);
        myweb.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        myweb.getSettings().setAppCachePath(appCacheDir);
        myweb.getSettings().setPluginState(PluginState.ON);
        myweb.getSettings().setRenderPriority(RenderPriority.HIGH);
		//myweb.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。

       // myweb.getSettings().setBuiltInZoomControls(false);294367888
       // myweb.getSettings().setUseWideViewPort(true);
      //  myweb.getSettings().setBuiltInZoomControls(false); // 支持页面放大缩小按钮 易注释
		//myweb.getSettings().setSupportZoom(false);
		//调用引用js交互方法
		myweb.addJavascriptInterface(new javascriptobject(), "myObj");
		//myweb.setDownloadListener(new MyWebViewDownLoadListener());//文件下载的方法
		//myweb.getSettings().setAllowFileAccess(true);//设置webview可以访问 数据文件

		myweb.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				loadurl(view, url);// 载入网页
				return false;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// TODO Auto-generated method stub
				L.e("onPageStarted：" + "开始");
				loadingTv.setVisibility(View.VISIBLE);
				title_bar.setVisibility(View.VISIBLE);
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				L.e("onPageFinished："+"结束");
				loadingTv.setVisibility(View.GONE);
				title_bar.setVisibility(View.GONE);
				if (myweb.canGoBack()) {//可以返回
					back.setImageResource(R.drawable.wap_prev_true);
				} else {
					back.setImageResource(R.drawable.wap_prev);
				}
				
				if (myweb.canGoForward()) {//可以前进
					forward.setImageResource(R.drawable.wap_next_true);
				} else {
					forward.setImageResource(R.drawable.wap_next);
				}
				super.onPageFinished(view, url);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {//获取页面标题
				// TODO Auto-generated method stub
				myweb.loadUrl("file:///android_asset/unnetwork.html");
				showCustomToast(description + " 错误代码：" + errorCode, false);
				super.onReceivedError(view, errorCode, description, failingUrl);
			}
			
		});
		myweb.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {// 载入进度改变而触发
				L.e("进度："+ progress + "%");
				loadingTv.setProgress(progress);
				super.onProgressChanged(view, progress);
			}
			
			@Override
			public void onReceivedTitle(WebView view, String title) {
				// TODO Auto-generated method stub
				titleView.setText(title);
				super.onReceivedTitle(view, title);
			}
		});
		/**
		 * 设置下载监听，当url为文件时候开始下载文件
		 */
		myweb.setDownloadListener(new DownloadListener() {
			@Override
			public void onDownloadStart(String url, String userAgent,
					String contentDisposition, String mimetype,
					long contentLength) {
				download(url);
			}
		});

	}
//	private class MyWebViewDownLoadListener implements DownloadListener {
//
//		@Override
//		public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
//									long contentLength) {
//			//调用自己的下载方式
//			// new HttpThread(url).start();
//			//调用系统浏览器下载
//			Uri uri = Uri.parse(url);
//			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//			startActivity(intent);
//		}
//
//	}
	/**
	 * 下载文件
	 */
	private void download(String url) {
		// TODO Auto-generated method stub
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File file = new File(FILE_PATH);
			if (!file.exists()) {
				file.mkdirs();
			}
			L.e("下载了");
			showDownloadDialog(); //显示下载窗口
			String expand_name = url.substring(url.lastIndexOf("."), url.length()); //获取下载文件的扩展名
			save_file_name = System.currentTimeMillis()+expand_name; //新文件名
			final DownLoadFileThreadTask task = new DownLoadFileThreadTask(url,
					file.getAbsolutePath() + "/"+save_file_name, this);
			Thread thread = new Thread(task);
			thread.start();
			dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					task.stop();
				}
			});
		} else {
			//T.showShort(this, "SD卡不可用！");
			showCustomToast("SD卡不可用！", false);
		}
	}

	/*
 * js交互方法
  * */
	public class javascriptobject {

		@JavascriptInterface
		public void fun1FromAndroid(String s) {
			mhandler.sendEmptyMessage(0);
		}

	}
	/**
	 * 显示下载进度条
	 */
	private void showDownloadDialog() {
		if (dialog == null) {
			dialog = new ProgressDialog(this);
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			dialog.setTitle("下载中...");
			dialog.setCanceledOnTouchOutside(false);
			dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.cancel();
				}
			});
		};
		dialog.show();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		/*if ((keyCode == KeyEvent.KEYCODE_BACK)&& myweb.canGoBack()) {
			goBack();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			ChatWebview.this.finish();
		}*/
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (myweb != null && myweb.canGoBack()) {
				goBack();
				return true;
			}else {
				finish();
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		parent.removeView(myweb);
		myweb.removeAllViews();
		myweb.destroy();
		L.e("------- 已经退出-----");
		super.onDestroy();
	}
	
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_back: // 返回
			goBack();
			break;
		case R.id.btn_go: // 前进
			goForward();
			break;
		case R.id.btn_refresh: // 刷新
			refresh();
			break;
		case R.id.web_title_back://退出
			finish();
			break;
		default:
			break;
		}
	}
	
	
	/**
	 * 返回
	 */
	private void goBack() {
		// TODO Auto-generated method stub
		if (myweb.canGoBack()) {
			myweb.goBack();
		}
	}

	/**
	 * 前进
	 */
	private void goForward() {
		// TODO Auto-generated method stub
		if (myweb.canGoForward()) {
			myweb.goForward();
		}
	}

	/**
	 * 刷新
	 */
	private void refresh() {
		// TODO Auto-generated method stub
		if (myweb != null) {
			myweb.reload();
		}
	}

	@Override
	public void sendMsg(ResponseBean response) {
		// TODO Auto-generated method stub
		if (response == null) {
			return;
		}
		switch (response.getStatus()) {
		case DownLoadFileThreadTask.DOWNLOAD_ING:
			dialog.setMax(response.getTotal());
			dialog.setProgress(response.getCurrent());
			break;
		case DownLoadFileThreadTask.DOWNLOAD_SUCCESS:
			dialog.dismiss();
			Looper.prepare(); //注意这段代码
			//T.showShort(ChatWebview.this, "下载成功，已经下载到"+FILE_PATH+"目录！");
			showCustomToast("下载成功，已经下载到"+FILE_PATH+"目录！", false);
		   Intent i= HttpThread.openFile(FILE_PATH+"/"+save_file_name);
			if(i!=null)
			{
				startActivity(i);
			}
			Looper.loop();
			break;
		case DownLoadFileThreadTask.DOWNLOAD_FAILURE:
			dialog.dismiss();
			Looper.prepare();
			showCustomToast(response.getMsg(), false);
			//T.showShort(ChatWebview.this, response.getMsg());
			Looper.loop();
			break;
		default:
			break;
		}
	};
}
