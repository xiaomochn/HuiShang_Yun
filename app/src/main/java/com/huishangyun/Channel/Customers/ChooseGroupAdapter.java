package com.huishangyun.Channel.Customers;

import java.util.ArrayList;
import java.util.List;

import com.huishangyun.yun.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

/**
 * 列表适配器
 * @author Pan
 *
 */
public class ChooseGroupAdapter extends BaseAdapter implements SectionIndexer{
	private List<GroupModel> mList;
	private Context mContext;
	private List<GroupModel> select = new ArrayList<GroupModel>();
	private OnItemCheked onItemCheked;
	
	public ChooseGroupAdapter(Context mContext, List<GroupModel> mList, OnItemCheked onItemCheked) {
		this.mList = mList;
		this.mContext = mContext;
		this.onItemCheked = onItemCheked;
	}
	
	public interface OnItemCheked {
		public void onItemCheked(List<GroupModel> models);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		for (int i = 0; i < mList.size(); i++) {
			GroupModel groupModel = mList.get(i);
			if (i == mList.size() - 1) {
				groupModel.setLine(true);
			} else {
				if (groupModel.getTitle().equals(mList.get(i + 1).getTitle())) {
					groupModel.setLine(false);
				} else {
					groupModel.setLine(true);
				}
			}
		}
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder mHolder;
		if (convertView == null) {
			mHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_choose_group, null);
			mHolder.Name = (TextView) convertView.findViewById(R.id.choose_groupname);
			mHolder.Title = (TextView) convertView.findViewById(R.id.choose_catalog);
			mHolder.Chekbox = (ImageView) convertView.findViewById(R.id.choose_select_img);
			mHolder.Line = (View) convertView.findViewById(R.id.choose_line);
			mHolder.NoLine = (View) convertView.findViewById(R.id.choose_line_no);
			convertView.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		//显示隐藏线条
		if (mList.get(position).isLine()) {
			mHolder.Line.setVisibility(View.GONE);
			mHolder.NoLine.setVisibility(View.VISIBLE);
		} else {
			mHolder.Line.setVisibility(View.VISIBLE);
			mHolder.NoLine.setVisibility(View.GONE);
		}
		mHolder.Title.setText(mList.get(position).getTitle());
		if (position == 0) {//第一个字母默认显示
			mHolder.Title.setVisibility(View.VISIBLE);
		} else {
			if (mList.get(position).getTitle().equals(mList.get(position - 1).getTitle())) {//首字母是否相同
				mHolder.Title.setVisibility(View.GONE);
			} else {
				mHolder.Title.setVisibility(View.VISIBLE);
			}
		}
		mHolder.Name.setText(mList.get(position).getName());
		if (mList.get(position).isSelect()) {
			mHolder.Chekbox.setImageResource(R.drawable.plan_selcet);
		} else {
			mHolder.Chekbox.setImageResource(R.drawable.plan_notselect);
		}
		//添加点击时间监听
		mHolder.Chekbox.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (mList.get(position).isSelect()) {
					setChekde(position, false);
					for (int i = 0; i < select.size(); i++) {//去除已选中项
						if (select.get(i).getID().equals(mList.get(position).getID())) {
							select.remove(i);
							break;
						}
					}
				} else {
					setChekde(position, true);
					select.add(mList.get(position));
				}
				//刷新
				notifyDataSetChanged();
				onItemCheked.onItemCheked(select);
			}
		});
		return convertView;
	}
	
	/**
	 * 设置是否选中
	 * @param selectNum
	 * @param isSelect
	 */
	private void setChekde(int selectNum, boolean isSelect) {
		GroupModel groupModel = mList.get(selectNum);
		groupModel.setSelect(isSelect);
		mList.remove(selectNum);
		mList.add(selectNum, groupModel);
	}
	
	/**
	 * 获取选中的数据
	 * @return
	 */
	public List<GroupModel> getSelects() {
		return select;
	}
	
	/**
	 * 控件适配器
	 * @author Pan
	 *
	 */
	static class ViewHolder {
		public TextView Title;
		public TextView Name;
		public ImageView Chekbox;
		public View Line;
		public View NoLine;
	}


	@Override
	public int getPositionForSection(int sectionIndex) {
		// TODO Auto-generated method stub
		GroupModel groupModel;
		String l;
		for (int i = 0; i < getCount(); i++) {
			groupModel = (GroupModel) mList.get(i);
			l = groupModel.getTitle();
			char firstChar = l.toUpperCase().charAt(0);
			if (firstChar == sectionIndex) {
				return i;
			}

		}
		groupModel = null;
		l = null;
		return -1;
	}
	@Override
	public int getSectionForPosition(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public Object[] getSections() {
		// TODO Auto-generated method stub
		return null;
	}

}
