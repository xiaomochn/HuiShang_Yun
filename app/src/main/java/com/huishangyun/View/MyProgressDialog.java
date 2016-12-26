package com.huishangyun.View;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * 自定义进度条
 * @author Pan
 *
 */
public class MyProgressDialog extends DialogFragment {
	private String message;
	private CustomProgressDialog dialog;
	
	/**
	 * 设置需要显示的文字
	 * @param message-需要显示的文字
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * 更新显示的文字
	 */
	public void updataMenssage(String message) {
		dialog.setMessage(message);
	}
	
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		dialog = CustomProgressDialog.createDialog(getActivity());
		dialog.setMessage(message);
		return dialog;
	}
		
}
