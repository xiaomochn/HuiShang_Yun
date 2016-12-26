package com.huishangyun.Channel.Clues;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huishangyun.Interface.OnDialogDown;
import com.huishangyun.yun.R;

public class ShowDialog {
	private Context mContext;
	private ArrayList<HashMap<String, Object>> mList;
	private String titleString;
	private TextView customdialog_sure;// 自定义对话框，确定按钮
	private ListView customdialog_listview;// 自定义对话框listview
	private ImageView cancel;// 自定义对话框顶部取消按钮
	private OnDialogDown mDialogDown;
	private int mPosition = -1;
	private int type;
	private TextView dialog_titlestr;
	private String hint;// 提示语
	private String defaultValue;// 选中的默认值

	/**
	 * 构造方法 -示例{ new ShowDialog(NewClueActivity.this, SOURCE_DIALOG, ItemLists,
	 * "选择阶段", NewClueActivity.this,defaultValue); }
	 * 
	 * @param mContext
	 *            -当前Activity的上下文对象(不能为Application的对象)
	 * @param type
	 *            -当前选择的类型
	 * @param mList
	 *            -要显示的list集合
	 * @param titleString
	 *            -悬浮框的标题
	 * @param mDialogDown
	 *            -实现的接口
	 */
	public ShowDialog(Context mContext, int type,
			ArrayList<HashMap<String, Object>> mList, String titleString,
			OnDialogDown mDialogDown, String hint, String defaultValue) {
		this.mContext = mContext;
		this.mList = mList;
		this.titleString = titleString;
		this.mDialogDown = mDialogDown;
		this.type = type;
		this.hint = hint;
		this.defaultValue = defaultValue;
	}

	/**
	 * 显示悬浮框
	 */
	public void customDialog() {
		LayoutInflater layoutInflater = LayoutInflater.from(mContext);
		View customdialog = layoutInflater.inflate(
				R.layout.activity_clue_new_customdialog, null);

		// 创建一个对话框对象
		final AlertDialog alertDialog = new AlertDialog.Builder(mContext)
				.create();
		// 设置对话框背景颜色
		alertDialog.setIcon(R.color.white);
		// 设置对话显示位置
		alertDialog.setView(customdialog, -1, -1, 0, -1);
		alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 自定义对话框确定按钮
		customdialog_sure = (TextView) customdialog
				.findViewById(R.id.customdialog_sure);
		customdialog_listview = (ListView) customdialog
				.findViewById(R.id.customdialog_listview);
		dialog_titlestr = (TextView) customdialog
				.findViewById(R.id.dialog_titlestr);
		dialog_titlestr.setText(titleString);
		cancel = (ImageView) customdialog.findViewById(R.id.cancel);
		alertDialog.show();
		final DialogAdapter dialog_listview_adapter = new DialogAdapter(mContext);
		customdialog_listview.setAdapter(dialog_listview_adapter);
		for (int i = 0; i < mList.size(); i++) {
			if (mList.get(i).get("name").equals(defaultValue)) {
				//lsitview跳转到对应位置
				customdialog_listview.setSelection(i);
				break;
			}
		}
		
		customdialog_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				defaultValue = (String)mList.get(position).get("name");
				mPosition = position;
				//通知listview强行刷新一次
				dialog_listview_adapter.upDate();
				
			}
		});
		// 自定义单击事件，确定按钮单击事件
		customdialog_sure.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 设置文字
				// source.setText(mtextview);
				if (mPosition == -1) {
					// alertDialog.cancel();
					new ClueCustomToast().showToast(mContext,
							R.drawable.toast_warn, hint);

				} else {
					mDialogDown.onDialogDown(mPosition, type);
					alertDialog.cancel();
				}

			}
		});

		// 取消按钮单击事件
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alertDialog.cancel();
			}
		});
	}

	/**
	 * 自定义对话框lsitview适配器
	 * 
	 * @author XSL
	 * 
	 */
	private class DialogAdapter extends BaseAdapter {

		private LayoutInflater mInflater;// 动态布局映射

		public DialogAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		// 决定listview显示条数
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
        public void upDate(){
        	this.notifyDataSetChanged();
        }
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			final ViewHolder holder;
			if (convertView==null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(
						R.layout.activity_clue_new_customdialog_listviewitem,
						null);
				holder.customdialoglistview_item_text = (TextView) convertView
						.findViewById(R.id.customdialoglistview_item_text);
				holder.customdialoglistview_item_img = (ImageView) convertView
						.findViewById(R.id.customdialoglistview_item_img);
				holder.item = (RelativeLayout) convertView
						.findViewById(R.id.item);
				convertView.setTag(holder);
			}else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.customdialoglistview_item_text.setText((String) mList.get(
					position).get("name"));
			String values = (mList.get(position).get("name")).toString().trim();
			if (values.equals(defaultValue)) {
				holder.customdialoglistview_item_img
						.setVisibility(View.VISIBLE);
				mPosition = position;
			} else {
				holder.customdialoglistview_item_img
						.setVisibility(View.INVISIBLE);
			}

			return convertView;
		}
	}

	private  static class ViewHolder {
		private TextView customdialoglistview_item_text;
		private ImageView customdialoglistview_item_img;
		private RelativeLayout item;
	}

}
