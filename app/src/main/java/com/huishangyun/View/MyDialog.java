package com.huishangyun.View;

import com.huishangyun.yun.R;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 自定义Dialog
 * @author frpan_000
 *
 */
public class MyDialog {
	
	/**
	 * 上下文对象
	 */
	private Context mContext;
	
	/**
	 * 标题栏
	 */
	private TextView titleTxt;
	
	/**
	 * 中间的文字信息
	 */
	private TextView messageTxt;
	
	/**
	 * 右上角取消按钮
	 */
	private ImageView cancelBtn;
	
	/**
	 * 确定按钮
	 */
	private TextView customdialog_sure;
	
	/**
	 * 取消按钮
	 */
	private TextView customdialog_no;
	
	/**
	 * 按键监听事件
	 */
	private OnMyDialogClickListener onMyDialogClickListener;
	
	/**
	 * 自定义布局的父布局
	 */
	private LinearLayout layout;
	
	/**
	 * 悬浮框
	 */
	private AlertDialog alertDialog;
	
	/**
	 * 悬浮框布局
	 */
	private View dialogView;
	
	/**
	 * 是否显示确定按钮
	 */
	private boolean isShowTrue = true;
	
	/**
	 * 是否显示取消按钮
	 */
	private boolean isShowFalse = true;
	
	/**
	 * 构造方法
	 * @param mContext
	 */
	public MyDialog(Context mContext) {
		this.mContext = mContext;
		initView();
	}
	
	/**
	 * 点击方法监听
	 * @author frpan_000
	 *
	 */
	public interface OnMyDialogClickListener {
		//点击确定
		public void onTrueClick(MyDialog dialog);
		
		//点击取消
		public void onFlaseClick(MyDialog dialog);
		
	}
	
	/**
	 * 实例化组件
	 */
	private void initView() {
		dialogView = LayoutInflater.from(mContext).inflate(R.layout.chek_message, null);
		titleTxt = (TextView) dialogView.findViewById(R.id.dialog_titlestr);
		messageTxt = (TextView) dialogView.findViewById(R.id.chek_meaasge);
		cancelBtn = (ImageView) dialogView.findViewById(R.id.cancel);
		customdialog_sure = (TextView) dialogView.findViewById(R.id.customdialog_sure);
		customdialog_no = (TextView) dialogView.findViewById(R.id.customdialog_no);
		layout = (LinearLayout) dialogView.findViewById(R.id.chek_layout);
		cancelBtn.setOnClickListener(onClickListener);
		customdialog_sure.setOnClickListener(onClickListener);
		customdialog_no.setOnClickListener(onClickListener);
		alertDialog = new AlertDialog.Builder(mContext).create();
		alertDialog.setIcon(R.color.white);
		alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		alertDialog.setView(dialogView, -1, -1, 0, -1);
	}
	
	/**
	 * 设置窗口属性
	 * @param type
	 */
	public void setWindowsType(int type) {
		alertDialog.getWindow().setType(type);
	}
	
	/**
	 * 设置是否显示确定按钮
	 * @param isShowTrue
	 */
	public void showTrue(boolean isShowTrue) {
		this.isShowTrue = isShowTrue;
	}
	
	/**
	 * 设置是否显示取消按钮
	 * @param isShowFalse
	 */
	public void showFalse(boolean isShowFalse) {
		this.isShowFalse = isShowFalse;
	}
	
	/**
	 * 设置按键监听方法
	 * @param onMyDialogClickListener
	 */
	public void setOnMyDialogClickListener(OnMyDialogClickListener onMyDialogClickListener) {
		this.onMyDialogClickListener = onMyDialogClickListener;
	}
	
	/**
	 * 移除监听
	 */
	public void removeOnMyDialogClickListener() {
		this.onMyDialogClickListener = null;
	}
	
	/**
	 * 设置自定义布局
	 * @param view
	 */
	public void setView(View view) {
		layout.addView(view);
	}
	
	/**
	 * 显示对话框
	 */
	public void show() {
		
		if (!isShowTrue) {
			customdialog_sure.setVisibility(View.GONE);
		}
		if (!isShowFalse) {
			customdialog_no.setVisibility(View.GONE);
		}
		alertDialog.show();
	}
	
	/**
	 * 是否能被返回键消失
	 * @param isCancel
	 */
	public void setCancel(boolean isCancel) {
		alertDialog.setCancelable(isCancel);
	}
	
	/**
	 * 将对话框消失
	 */
	public void dismiss() {
		alertDialog.cancel();
	}
	
	/**
	 * 设置标题文字
	 * @param title
	 */
	public void setTitle(String title) {
		titleTxt.setText(title);
	}
	
	/**
	 *设置显示的文字
	 */
	public void setMessage(String message) {
		messageTxt.setText(message);
	}
	
	/**
	 * 设置文字
	 * @param text
	 */
	public void setTuerText(String text) {
		customdialog_sure.setText(text);
	}
	
	/**
	 * 设置文字
	 * @param text
	 */
	public void setFalseText(String text) {
		customdialog_no.setText(text);
	}
	
	/**
	 * 点击方法监听
	 */
	private OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.cancel:
				if (onMyDialogClickListener != null) {
					onMyDialogClickListener.onFlaseClick(MyDialog.this);
				}
				break;
			case R.id.customdialog_sure:
				if (onMyDialogClickListener != null) {
					onMyDialogClickListener.onTrueClick(MyDialog.this);
				}
				break;
			case R.id.customdialog_no:
				if (onMyDialogClickListener != null) {
					onMyDialogClickListener.onFlaseClick(MyDialog.this);
				}
				break;

			default:
				break;
			}
		}
	};
	
	
}
