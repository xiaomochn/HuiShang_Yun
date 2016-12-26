package com.huishangyun.Office.Information;

import java.lang.reflect.Field;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ZoomButtonsController;

/**
 * 隐藏webview放大缩小控件，同时具备放大缩小功能
 * 调用方法：WebViewControl.setZoomViewInvisible(webView);
 * @author xsl
 *
 */
public class WebViewControl {
	
	/**
	 * 隐藏缩放按钮
	 * @paramsettings
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void setZoomViewInvisible(WebView webView) {
		WebSettings settings = webView.getSettings();
		//去掉滚动条  
		webView.setVerticalScrollBarEnabled(false);  
		webView.setHorizontalScrollBarEnabled(false); 
        
        // 设置可缩放 
        settings.setBuiltInZoomControls(true);  
        // 根据版本号设置缩放按钮不显示
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {//用于判断是否为Android 3.0系统, 然后隐藏缩放控件
			  settings.setDisplayZoomControls(false);
		}else {// Android 3.0(11) 以下使用以下方法
			setZoomControlGone(webView);
		}
	}
	
	//实现放大缩小控件隐藏  
	private static void setZoomControlGone(View view) {  
	    Class classType;  
	    Field field;  
	    try {  
	        classType = WebView.class;  
	        field = classType.getDeclaredField("mZoomButtonsController");  
	        field.setAccessible(true);  
	        ZoomButtonsController mZoomButtonsController = new ZoomButtonsController(view);  
	        mZoomButtonsController.getZoomControls().setVisibility(View.GONE);  
	        try {  
	            field.set(view, mZoomButtonsController);  
	        } catch (IllegalArgumentException e) {  
	            e.printStackTrace();  
	        } catch (IllegalAccessException e) {  
	            e.printStackTrace();  
	        }  
	    } catch (SecurityException e) {  
	        e.printStackTrace();  
	    } catch (NoSuchFieldException e) {  
	        e.printStackTrace();  
	    }  
	}  

}
