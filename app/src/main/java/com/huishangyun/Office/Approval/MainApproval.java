package com.huishangyun.Office.Approval;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Util.HttpThread;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ResponseBean;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Content;
import com.huishangyun.task.DownLoadFileThreadTask;
import com.huishangyun.download.DownloadInterface;
import com.huishangyun.yun.R;

import java.io.File;

/**
 * 信息主页
 *
 * @author xsl
 */
public class MainApproval extends BaseActivity implements View.OnClickListener,DownloadInterface {

    private RelativeLayout back;//返回
    private FrameLayout collection;//收藏
    private TextView headname;//头名
    private ProgressBar percent;//加载网页百分比
    private WebView webView;
    private LinearLayout linear_error;
    private Button btn_error;
    private final String FILE_PATH = Environment.getExternalStorageDirectory().toString() + "/HSY_Yun/download";
    private String save_file_name;
    private ProgressDialog dialog = null; //下载进度条

    Handler mhandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                webView.loadUrl(url);
                    break;
            }
        }
    };
    private String pathurl=MyApplication.getInstance().getSharedPreferences().getString(Constant.HUISHANG_WEBDOMAIN, "http://webapp.huishangyun.com");
    private String url =pathurl +"/Adminlogin.aspx?";//OA信息

    @Override
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
            public void onClick(View view) {
               String urls=webView.getUrl();
                Uri uri= Uri.parse(urls);
                urls =urls.replace(uri.getHost(),"");
                if (urls.equals("http:///WebForm/H5/Default.aspx")){
                    finish();
                }else {
                    webView.loadUrl("http://"+uri.getHost()+"/WebForm/H5/Default.aspx");
                }
            }
        });


//	pathurl = url + "d=" + MyApplication.preferences.getInt(Content.COMPS_ID, 0) + "&n="
//			+ preferences.getString(Constant.USERNAME, "") + "&p="
//			+ preferences.getString(Constant.PASSWORD, "")+"&u="+"OA/Article/new_list.aspx";
//
//	Log.e("==========>", "pathurl审批="+pathurl);

        webView = (WebView) findViewById(R.id.webview1);

      //  webView.getSettings().setSupportZoom(true);    //支持缩放

        //启用内置缩放功能，但隐藏缩放装置
       // WebViewControl.setZoomViewInvisible(webView);
        //设置WebView属性，能够执行Javascript脚本
        webView.getSettings().setJavaScriptEnabled(true);
        //加载网络地址
   //	 webView.removeJavascriptInterface("searchBoxJavaBredge_");
        webView.addJavascriptInterface(new javascriptobject(), "myObj");
        //webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//优先使用缓存
        //webView.setDownloadListener(new MyWebViewDownLoadListener());//文件下载的方法
       // webView.getSettings().setAllowFileAccess(true);//设置webview可以访问 数据文件
        webView.setWebViewClient(new WebViewClient() {

            //当点击链接时,希望覆盖而不是打开新窗口
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;    //返回true,代表事件已处理,事件流到此终止
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

                webView.loadUrl("file:///android_asset/unnetwork.html");
                super.onReceivedError(view, errorCode, description, failingUrl);
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
                MainApproval.this.setProgress(newProgress * 100);
                if (newProgress == 100) {
                    percent.setVisibility(View.GONE);
                } else {
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
        /**
         * 设置下载监听，当url为文件时候开始下载文件
         */
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                download(url);
            }
        });
        url = url + "d=" + preferences.getInt(Content.COMPS_ID, 0)
                + "&n=" + preferences.getString(Constant.USERNAME, "") + "&p="
                + preferences.getString(Constant.PASSWORD, "") + "&u=OA/WorkFlow/Default.aspx";
       // Log.e("==========>", "url审批=" + url);
        webView.loadUrl(url);

    }
//    private class MyWebViewDownLoadListener implements DownloadListener {
//
//        @Override
//        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
//                                    long contentLength) {
//            //调用自己的下载方式
//            // new HttpThread(url).start();
//            //调用系统浏览器下载
//            Uri uri = Uri.parse(url);
//            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//            startActivity(intent);
//        }
//
//    }
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
    /**
     * 显示下载进度条
     */
    private void showDownloadDialog() {
        if (dialog == null) {
            dialog = new ProgressDialog(this);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setTitle("下载中...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
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
    public void onClick(View v) {

    }

    /*
    * js交互方法
    * */
    public class javascriptobject {

        @JavascriptInterface
        public void fun1FromAndroid(String s)
        {
            mhandler.sendEmptyMessage(0);
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
