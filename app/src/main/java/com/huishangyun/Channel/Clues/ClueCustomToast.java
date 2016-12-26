package com.huishangyun.Channel.Clues;

import com.huishangyun.Util.L;
import com.huishangyun.yun.R;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 自定义toast
 * @author XieShuiLin
 *
 */
public class ClueCustomToast{

	/**
	 * 自定义Toast 
	 * 示例调用：new ClueCustomToast().showToast(ModifyClueActivity.this, R.drawable.toast_sucess, "提交成功 !");
	 * @param context 上下文对象
	 * @param drawable 要现实的图片
	 * @param showTextStr 要现实的文字
	 */
	
	private static Toast toast;
	public static void showToast(Context context,int drawable, String showTextStr){
		// 获取LayoutInflater对象，该对象能把XML文件转换为与之一直的View对象
		try {
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		// 根据指定的布局文件创建一个具有层级关系的View对象
		// 第二个参数为View对象的根节点，即LinearLayout的ID
		View layout = inflater.inflate(
				R.layout.activity_clue_custom_toast,
				(ViewGroup) ((Activity) context).findViewById(R.id.toast_linearLayout_root));

		// 查找ImageView控件
		// 注意是在layout中查找
		ImageView image = (ImageView) layout
				.findViewById(R.id.toast_img);
		image.setImageResource(drawable);
		TextView text = (TextView) layout.findViewById(R.id.toast_text);
		text.setText(showTextStr);
		if (null == toast) {
			toast = new Toast(context);
		}
		// 设置Toast的位置
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
//		toast.setDuration(Toast.LENGTH_LONG);
		toast.setDuration(Toast.LENGTH_SHORT);
		// 让Toast显示为我们自定义的样子
		toast.setView(layout);
		toast.show();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			L.e("java.lang.NullPointerException");
			return;
		}
	}
}
