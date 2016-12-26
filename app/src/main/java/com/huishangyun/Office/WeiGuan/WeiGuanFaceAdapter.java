package com.huishangyun.Office.WeiGuan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.huishangyun.App.MyApplication;
import com.huishangyun.yun.R;

public class WeiGuanFaceAdapter extends BaseAdapter{
	private LayoutInflater inflater;
	private int currentPage = 0;
	private Map<String, Integer> mFaceMap;
	private List<Integer> faceList = new ArrayList<Integer>();

	public WeiGuanFaceAdapter(Context context, int currentPage,Map<String, Integer> mFaceMap) {
		// TODO Auto-generated constructor stub
		this.inflater = LayoutInflater.from(context);
		this.currentPage = currentPage;
		this.mFaceMap = mFaceMap;
		initData();
	}

	private void initData() {
		for(Map.Entry<String, Integer> entry:mFaceMap.entrySet()){
			faceList.add(entry.getValue());
		}
	}

	@Override
	public int getCount() {
		return MyApplication.NUM + 1;
	}

	@Override
	public Object getItem(int position) {
		return faceList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.face, null);
			viewHolder.faceIV = (ImageView) convertView
					.findViewById(R.id.face_iv);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (position == MyApplication.NUM) {
			viewHolder.faceIV.setImageResource(R.drawable.emotion_del_selector);
			viewHolder.faceIV.setBackgroundDrawable(null);
		} else {
			int count = MyApplication.NUM * currentPage + position;
			if (count < mFaceMap.size()) {
				viewHolder.faceIV.setImageResource(faceList.get(count));
			} else {
				viewHolder.faceIV.setImageDrawable(null);
			}
		}
		return convertView;
	}

	public static class ViewHolder {
		ImageView faceIV;
	}
}
