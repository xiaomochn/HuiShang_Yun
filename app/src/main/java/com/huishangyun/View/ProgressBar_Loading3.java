package com.huishangyun.View;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.huishangyun.Util.L;
import com.huishangyun.yun.R;

/**
 * (中式英语)This is an defined Dialog,it can show anywhere if you want.
 * 调用方法示例：显示：ProgressBar_Loading.showProgressBar(context,true, "Loading...");
 *          隐藏：ProgressBar_Loading.showProgressBar(context,false, "Loading...");
 *          或者ProgressBar_Loading.dismiss(context);
 * @author xsl
 *
 */
public class ProgressBar_Loading3 {
	private  static Dialog dialog = null;
	public static void dismiss(Context context){
		showProgressBar(context,false, null);
	}
	
	/**
	 * 显示进度
	 * @param bool true为显示，false为隐藏
	 * @param str 提示语
	 */
	public  static void showProgressBar(Context context,boolean bool,String str){
		
			LayoutInflater inflater = LayoutInflater.from(context);
			View view = inflater.inflate(R.layout.define_progressbar, null);
			TextView message = (TextView) view.findViewById(R.id.message);
			if (str!=null) {
				message.setText(str);
			}
			if (bool) {
				/* 初始化普通对话框。并设置样式 */
				dialog = new Dialog(context, R.style.dialog);
				dialog.setCanceledOnTouchOutside(false);
				/* 设置普通对话框的布局 */
				dialog.setContentView(view);
				dialog.show();//显示对话框
			}else {
				//加try--catch防止调用dialog.dismiss()前而没调用dialog.show()导致程序异常退出
				try {
					dialog.dismiss();
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					L.e("进度条dialog.dismiss();空指针");
				}
			}
	}
	
	
}
