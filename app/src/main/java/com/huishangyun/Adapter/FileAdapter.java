package com.huishangyun.Adapter;

import java.util.List;
import java.util.Map;

import com.huishangyun.Interface.onItemChanger;
import com.huishangyun.yun.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FileAdapter extends BaseAdapter {
	private Context mContext;
	private List<Map<String, Object>> mList;
	private LayoutInflater mInflater;
	private onItemChanger onItemChanger = null;
	
	public FileAdapter(Context mContext, List<Map<String, Object>> mList){
		this.mContext = mContext;
		this.mList = mList;
		this.mInflater = LayoutInflater.from(this.mContext);
	}
	
	public void setOnItemChanger(onItemChanger onItemChanger) {
		this.onItemChanger = onItemChanger;
	}
	
	public void refresh() {
		this.notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder mHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.task_item_file, null);
			mHolder = new ViewHolder();
			mHolder.fileNo = (TextView) convertView.findViewById(R.id.task_prog_no);
			mHolder.fileType = (ImageView) convertView.findViewById(R.id.task_prog_filetype);
			mHolder.fileName = (TextView) convertView.findViewById(R.id.task_prog_filename);
			mHolder.delete = (ImageView) convertView.findViewById(R.id.task_prog_delete);
			convertView.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		Map<String, Object> map = mList.get(position);
		mHolder.fileName.setText((CharSequence) map.get("filename"));
		mHolder.fileNo.setText((position + 1) + "");
		mHolder.delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (onItemChanger != null) {
					//通知接口重新计算高度
					onItemChanger.itemChaenged(position);
				}
			}
		});
		return convertView;
	}
	
	static class ViewHolder{
		public TextView fileNo;
		public ImageView fileType;
		public TextView fileName;
		public ImageView delete;
	}

}
