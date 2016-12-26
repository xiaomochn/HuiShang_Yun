package com.huishangyun.Channel.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.yun.R;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 自定义的文件浏览器
 * @author Pan
 * 
 * @version 亿企云APP V1.0 2014-08-18 16:42
 *
 * @see无
 */
public class TaskFilelistActivity extends BaseActivity {
	private List<Map<String, Object>> mData;
	private String mDir = "/sdcard";
	private ListView filelist;
	private MyAdapter adapter;
	private boolean isSdcard = true;
	private TextView noFileView; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_filelist);
		initBackTitle("返回");
		filelist = (ListView) findViewById(R.id.task_filelist_listview);
		noFileView = (TextView) findViewById(R.id.task_filelist_nofile);
		Intent intent = this.getIntent();
		Bundle bl = intent.getExtras();
		Uri uri = intent.getData(); // 接收起始目录
		mDir = uri.getPath(); // 设置起始目录
		mData = getData(); // 向链表mData填充目录的数据
		adapter = new MyAdapter(this);
		filelist.setAdapter(adapter); // 设置MyAdapter类为ListView控件提供数据
		filelist.setOnItemClickListener(clickListener);
		setResult(RESULT_CANCELED);
	}
	
	private OnItemClickListener clickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> l, View v, int position,
				long id) {
			// TODO Auto-generated method stub
			if ((Boolean) mData.get(position).get("isDirectory")) {
				isSdcard = false;
				mDir = (String) mData.get(position).get("info");
				mData = getData(); // 点击目录时进入子目录
				if (mData==null){
					return;
				}else {
					adapter.notifyDataSetChanged();
				}
			} else { // 点击文件时关闭文件管理器，并将选取结果返回
				finishWithResult((String) mData.get(position).get("info"), (String) mData.get(position).get("title"));
			}
		}
	};
	
	/**
	 * 获取文件列表
	 * @return
	 */
	private List<Map<String, Object>> getData() { // 将目录数据填充到链表中
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		if (mDir==null||mDir.equals("/sdcard")){
			return null;
		}
		File f = new File(mDir); // 打开当前目录
		File[] files = f.listFiles(); // 获取当前目录中文件列表

		if (!mDir.equals("/sdcard")) { // 不充许进入/sdcard上层目录
			map = new HashMap<String, Object>(); // 加返回上层目录项
			map.put("title", "返回上层目录");
			map.put("info", f.getParent());
			map.put("isDirectory", true);
			list.add(map);
		}
		if (files != null) { // 将目录中文件填加到列表中
			for (int i = 0; i < files.length; i++) {
				map = new HashMap<String, Object>();
				map.put("title", files[i].getName());
				map.put("info", files[i].getPath());
				if (files[i].isDirectory()) {// 按不同类型显示不同图标
					map.put("isDirectory", true);
				} else {
					map.put("isDirectory", false);
				}
				list.add(map);
			}
		}
		return list;
	}
	
	/**
	 * 文件列表适配器
	 * @author Pan
	 *
	 */
	private class MyAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mData==null?0:mData.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			final ViewHolder mHolder;
			final Map<String, Object> map;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.task_item_filelist,
						null);
				mHolder = new ViewHolder();
				mHolder.img = (ImageView) convertView
						.findViewById(R.id.task_filelist_img);
				mHolder.title = (TextView) convertView
						.findViewById(R.id.task_filelist_txt);
				mHolder.right = (ImageView) convertView
						.findViewById(R.id.task_filelist_right);
				convertView.setTag(mHolder);
			} else {
				mHolder = (ViewHolder) convertView.getTag();
			}
			map = mData.get(position);
			if ((Boolean) map.get("isDirectory")) {
				mHolder.img.setImageResource(R.drawable.ex_folder);
				mHolder.right.setVisibility(View.VISIBLE);
			} else {
				mHolder.img.setImageResource(R.drawable.file_no_type);
				mHolder.right.setVisibility(View.GONE);
			}
			mHolder.title.setText((String) map.get("title"));
			return convertView;
		}

	}
	
	/**
	 * 控件适配器
	 * @author Pan
	 *
	 */
	public final class ViewHolder { // 定义每个列表项所含内容
		public ImageView img; // 显示图片ID
		public TextView title; // 文件目录名
		public ImageView right;// 是否为文件夹
	}
	
	/**
	 * 选中文件的时候返回文件的路劲
	 * @param path
	 */
	private void finishWithResult(String path,String name) {
		Bundle conData = new Bundle();
		conData.putString("results", "Thanks Thanks");
		conData.putString("FileName", name);
		Intent intent = new Intent(); // 以intent的方式将结果返回调用类
		intent.putExtras(conData);
		Uri startDir = Uri.fromFile(new File(path));
		intent.setDataAndType(startDir,"vnd.android.cursor.dir/lysesoft.andexplorer.file");
		setResult(RESULT_OK, intent);
		finish();
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (mDir.equals(Environment.getExternalStorageDirectory().toString())) { //当前目录是否为根目录
			finish();
		} else {
			//返回上层目录
			mDir = (String) mData.get(0).get("info");
			mData = getData(); // 点击目录时进入子目录
			adapter.notifyDataSetChanged();
		}
	}
}
