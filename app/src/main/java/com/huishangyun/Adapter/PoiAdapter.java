package com.huishangyun.Adapter;

import java.util.List;

import com.huishangyun.model.PoiUtils;
import com.huishangyun.yun.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * 附件兴趣点
 * @author Pan
 *
 */
public class PoiAdapter extends BaseAdapter{
	private List<PoiUtils> mList;
	private LayoutInflater mInflater;
	
	public PoiAdapter(Context convertView, List<PoiUtils> mList) {
		this.mList = mList;
		this.mInflater = LayoutInflater.from(convertView);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder mHolder;
		if (convertView == null) {
			mHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_poi_detail, null);
			mHolder.name = (TextView) convertView.findViewById(R.id.poi_name);
			mHolder.address = (TextView) convertView.findViewById(R.id.poi_address);
			mHolder.checkBox = (CheckBox) convertView.findViewById(R.id.poi_chekbox);
			convertView.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		mHolder.name.setText(mList.get(position).getPoiInfo().name);
		mHolder.address.setText(mList.get(position).getPoiInfo().address);
		if (mList.get(position).getIsCheked()) {
			mHolder.checkBox.setChecked(true);
		} else {
			mHolder.checkBox.setChecked(false);
		}
		return convertView;
	}
	
	static class ViewHolder {
		public TextView name;
		public TextView address;
		public CheckBox checkBox;
	}

}
