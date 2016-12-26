package com.huishangyun.Channel.Plan;

import java.util.List;

import com.huishangyun.Channel.Visit.BitmapTools;
import com.huishangyun.Util.L;
import com.huishangyun.App.MyApplication;
import com.huishangyun.model.Constant;
import com.huishangyun.yun.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class SortAdapter extends BaseAdapter implements SectionIndexer{
	private static final String TAG = null;
	private List<SortModel> list = null;
	private Context mContext;
	private String code;
	
	public SortAdapter(Context mContext, List<SortModel> list, String code) {
		this.mContext = mContext;
		this.list = list;
		this.code = code;
	}
	
	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * @param list
	 * 
	 */
	public void updateListView(List<SortModel> list){
		this.list = list;
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
//		view.setVisibility(View.GONE);
		
		ViewHolder viewHolder = null;		
		final SortModel mContent = list.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			if (list.get(position).getCode().equals("0")||list.get(position).getCode().equals("3")) {
				//选择客户界面
				view = LayoutInflater.from(mContext).inflate(R.layout.activity_plan_customers_item, null);
			}else if (list.get(position).getCode().equals("1")) {
				//选择人员界面
				view = LayoutInflater.from(mContext).inflate(R.layout.activity_plan_person_item, null);
				
				viewHolder.Dynamic = (TextView) view.findViewById(R.id.Dynamic);
			}else if(list.get(position).getCode().equals("2")){
				view = LayoutInflater.from(mContext).inflate(R.layout.activity_plan_customers_item, null);
				viewHolder.img_right = (ImageView) view.findViewById(R.id.img_right); 
			}
			viewHolder.members = (LinearLayout) view.findViewById(R.id.members);
			viewHolder.group = (LinearLayout) view.findViewById(R.id.group);
			viewHolder.groupname = (TextView) view.findViewById(R.id.groupname);
			viewHolder.tvTitle = (TextView) view.findViewById(R.id.customers);
			viewHolder.tv_company = (TextView) view.findViewById(R.id.company_name);
			viewHolder.person_img = (ImageView) view.findViewById(R.id.person_img);			
			viewHolder.select_img = (ImageView) view.findViewById(R.id.select_img);
			viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
			viewHolder.item = (LinearLayout) view.findViewById(R.id.item);
			viewHolder.divid_line = (View) view.findViewById(R.id.divid_line);
			viewHolder.divid_line1 = (View) view.findViewById(R.id.divid_line1);
			viewHolder.divid_line_no = (View) view.findViewById(R.id.divid_line_no);
			
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		
		//根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);
//		L.e("list.get(position).getParentID():" + list.get(position).getParentID());
		if (list.get(position).getParentID() != null 
				&& (list.get(position).getPerson_img()).equals("0")) {
			if (list.get(position).getCode().equals("0") || list.get(position).getCode().equals("3")) {
				viewHolder.person_img.setImageResource(R.drawable.contact_group);
			}else if (list.get(position).getCode().equals("1")) {			
				viewHolder.person_img.setImageResource(R.drawable.contact_group);
						L.e("部门：" + position);
			}else if (list.get(position).getCode().equals("2")) {
				viewHolder.person_img.setImageResource(R.drawable.contact_group);
			}

		}else if (list.get(position).getParentID() == null
				&& list.get(position).getGroup_ID() != null
				&& !(list.get(position).getPerson_img()).equals("0")) {

			if (list.get(position).getCode().equals("0") || list.get(position).getCode().equals("3")) {
				viewHolder.person_img.setImageResource(R.drawable.contact_person);
				String person_url = Constant.pathurl+
						MyApplication.getInstance().getCompanyID()+"/Photo/100x100/" + list.get(position).getPerson_img();
				viewHolder.person_img.setTag(person_url);
				//new BitmapTools().downLoadImage(viewHolder.person_img, 100, 100,true,mContext);
				ImageLoader.getInstance().displayImage(person_url,viewHolder.person_img,MyApplication.getInstance().getOptions());

			}else if (list.get(position).getCode().equals("1")) {
				L.e("人员：" + position);
				viewHolder.person_img.setImageResource(R.drawable.contact_person);
				String person_url = Constant.pathurl+
						MyApplication.getInstance().getCompanyID()+"/Photo/100x100/" + list.get(position).getPerson_img();
				viewHolder.person_img.setTag(person_url);
				//new BitmapTools().downLoadImage(viewHolder.person_img, 100, 100,true,mContext);
				ImageLoader.getInstance().displayImage(person_url,viewHolder.person_img,MyApplication.getInstance().getOptions());
				
			}else if (list.get(position).getCode().equals("2")) {
				
				viewHolder.person_img.setImageResource(R.drawable.product_img);
				String person_url = Constant.pathurl+
						MyApplication.getInstance().getCompanyID()+"/Product/100x100/" + list.get(position).getPerson_img();
				viewHolder.person_img.setTag(person_url);
				new BitmapTools().downLoadImage(viewHolder.person_img, 100, 100,true,mContext);
			}
			if (list.get(position).getCode().equals("2")) {
				viewHolder.img_right.setVisibility(View.INVISIBLE);
			}
			
		}
		
		//如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if(position == getPositionForSection(section)){
			viewHolder.tvLetter.setVisibility(View.VISIBLE);
			viewHolder.tvLetter.setText(mContent.getSortLetters());
		}else{
			viewHolder.tvLetter.setVisibility(View.GONE);
		}
		
		//判断分割线显示
		if (position >= list.size()-1) {
			viewHolder.divid_line_no.setVisibility(View.VISIBLE);
			viewHolder.divid_line.setVisibility(View.GONE);
			viewHolder.divid_line1.setVisibility(View.GONE);
		}else {
		
		if (list.get(position).getSortLetters().equals(list.get(position+1).getSortLetters())) {
			viewHolder.divid_line_no.setVisibility(View.GONE);
			viewHolder.divid_line.setVisibility(View.VISIBLE);
			viewHolder.divid_line1.setVisibility(View.VISIBLE);
		}else {
			viewHolder.divid_line_no.setVisibility(View.VISIBLE);
			viewHolder.divid_line.setVisibility(View.GONE);
			viewHolder.divid_line1.setVisibility(View.GONE);
		 }
		}
		
		if (code.equals("2")) {
			viewHolder.tv_company.setVisibility(View.GONE);
			viewHolder.tv_company.setGravity(Gravity.CENTER_VERTICAL);
		} else {
			viewHolder.tv_company.setVisibility(View.VISIBLE);
		}
	     //code为0时为客户选择界面多选，为其它的时候为单选
		if (list.get(position).getCode().equals("0")||list.get(position).getCode().equals("3")) {
			
			viewHolder.groupname.setText(this.list.get(position).getName());
			viewHolder.tvTitle.setText(this.list.get(position).getName());
			viewHolder.tv_company.setText("责任人  " + this.list.get(position).getManger_Name());
			if (list.get(position).getSelect()==1) {
				//单选显示
				viewHolder.select_img.setVisibility(View.INVISIBLE);
			}else {
				//多选显示
				if ((CustomersListActivity.list_id.contains(list.get(position)
						.getID() + ""))
						&& list.get(position).getGroup_ID() != null) {
					viewHolder.select_img.setBackgroundResource(R.drawable.plan_selcet);
				} else {				
					viewHolder.select_img.setBackgroundResource(R.drawable.plan_notselect);
				}
				
			}
			
			if ( list.get(position).getParentID() != null) {
				//组，显示群item
				viewHolder.group.setVisibility(View.VISIBLE);
				viewHolder.members.setVisibility(View.GONE);
			}else {
				//成员显示//成员item
				viewHolder.group.setVisibility(View.GONE);
				viewHolder.members.setVisibility(View.VISIBLE);
			}

		} else if (list.get(position).getCode().equals("1")){
			viewHolder.groupname.setText(this.list.get(position).getName());
			viewHolder.tvTitle.setText(this.list.get(position).getName());
			viewHolder.tv_company.setText(this.list.get(position).getDepartment_Name() + " | " + this.list.get(position).getManger_Name());
			viewHolder.Dynamic.setText(this.list.get(position).getNote());
			viewHolder.select_img.setVisibility(View.INVISIBLE);
			
			if ( list.get(position).getParentID() != null) {					
				viewHolder.group.setVisibility(View.VISIBLE);
				viewHolder.members.setVisibility(View.GONE);
			}else {
				viewHolder.group.setVisibility(View.GONE);
				viewHolder.members.setVisibility(View.VISIBLE);
			}
			
			//产品部分
		}else if (list.get(position).getCode().equals("2")) {
			viewHolder.groupname.setText(this.list.get(position).getName());
			viewHolder.tvTitle.setText(this.list.get(position).getName());
			//单选显示
			viewHolder.select_img.setVisibility(View.INVISIBLE);
			
			if ( list.get(position).getParentID() != null) {					
				viewHolder.group.setVisibility(View.VISIBLE);
				viewHolder.members.setVisibility(View.GONE);
			}else {
				viewHolder.group.setVisibility(View.GONE);
				viewHolder.members.setVisibility(View.VISIBLE);
			}
			
//				//组，显示群item
//				viewHolder.group.setVisibility(View.VISIBLE);
//				viewHolder.members.setVisibility(View.GONE);
		
			
		}
		
		
		return view;

	}
	


	final static class ViewHolder {
		private TextView tvLetter;//首字母
		private TextView tvTitle;//客户姓名
		private TextView tv_company;//客户公司名称
		private ImageView person_img;//用户头像
		private LinearLayout item;//item
		private ImageView select_img;//选择图标
		private View divid_line;//分割线
		private View divid_line1;
		private View divid_line_no;
		private TextView groupname;//群主名
		private TextView Dynamic;//动态
		private LinearLayout group;//群item
		private LinearLayout members;//成员item
		private ImageView img_right;//组右箭头图标
	}


	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return list.get(position).getSortLetters().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		
		return -1;
	}
	
//	/**
//	 * 提取英文的首字母，非英文字母用#代替。
//	 * 
//	 * @param str
//	 * @return
//	 */
//	private String getAlpha(String str) {
//		String  sortStr = str.trim().substring(0, 1).toUpperCase();
//		// 正则表达式，判断首字母是否是英文字母
//		if (sortStr.matches("[A-Z]")) {
//			return sortStr;
//		} else {
//			return "#";
//		}
//	}
//
	@Override
	public Object[] getSections() {
		return null;
	}
}