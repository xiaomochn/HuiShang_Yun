package com.huishangyun.Adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.huishangyun.Util.L;
import com.huishangyun.Util.UpLoadFileUtil;
import com.huishangyun.model.Constant;
import com.huishangyun.model.ContactBean;
import com.huishangyun.yun.R;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Util.FileTools;
import com.huishangyun.View.RoundAngleImageView;

/**
 * 
 * @author 通讯录适配器
 *
 */
public class ContactListAcapter extends BaseAdapter implements SectionIndexer {
	private Context context;
	private List<ContactBean> list;
	private int type;
	
	public ContactListAcapter(Context context,List<ContactBean> list,int type){
		this.list = list;
		this.context = context;
		this.type = type;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		super.notifyDataSetChanged();
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		final ContactBean cBean = list.get(position);
		boolean latter; 
		if (position == 0) {
			latter = true;
		}else {
			String lastCatalog = list.get(position - 1).getLetter();
			if (cBean.getLetter().equals(lastCatalog)) {
				latter = false;
			}else {
				latter = true;
			}
		}
		if (!cBean.isIsgroup()) {
			//不是组
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(R.layout.item_contact, null);
                holder = new ViewHolder();
				holder.headView = (ImageView)convertView.findViewById(R.id.contact_head);
				holder.name = (TextView)convertView.findViewById(R.id.contact_name);
				holder.letter = (TextView)convertView.findViewById(R.id.contact_letter);
				holder.department = (TextView) convertView.findViewById(R.id.contact_department);
				holder.signature = (TextView) convertView.findViewById(R.id.contact_signature);
				holder.lineTrue = (View) convertView.findViewById(R.id.line_true);
				holder.lineFalse = (View) convertView.findViewById(R.id.line_false);
				convertView.setTag(holder);
			}else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (latter) {
				holder.letter.setVisibility(View.VISIBLE);
			}else {
				holder.letter.setVisibility(View.GONE);
			}
			
			if (position == (list.size() - 1)) {
				holder.lineTrue.setVisibility(View.VISIBLE);
				holder.lineFalse.setVisibility(View.GONE);
			} else {
				
				String lastCatalog = list.get(position + 1).getLetter();
				if (cBean.getLetter().equals(lastCatalog)) {
					holder.lineTrue.setVisibility(View.GONE);
					holder.lineFalse.setVisibility(View.VISIBLE);
				}else {
					holder.lineTrue.setVisibility(View.VISIBLE);
					holder.lineFalse.setVisibility(View.GONE);
				}
				
			}
			
			holder.headView.setImageResource(R.drawable.plan_person);
			holder.letter.setText(cBean.getLetter());
			//易注释
//			if (type==1){
//				holder.name.setText(cBean.getDepartment());
//			}else{
//				if (cBean.getName().length()<4) {
//					holder.name.setText(cBean.getName());
//				} else {
//					holder.name.setText(cBean.getName());
//					holder.name.setEllipsize(TruncateAt.MARQUEE);
//				}
//			}
			if (cBean.getName().length()<4) {
				holder.name.setText(cBean.getName());
			} else {
				holder.name.setText(cBean.getName());
				holder.name.setEllipsize(TruncateAt.MARQUEE);
			}

			holder.name.setTag(cBean);
			L.d("position = " + position);
			/*new ImageLoader(context).DisplayImage("http://img.huishangyun.com/UploadFile/huishang/"+
					MyApplication.preferences.getInt(Content.COMPS_ID, 1016)+"/Photo/" + cBean.getPhoto(), holder.headView, false);*/
			/*FileTools.decodePhoto("http://img.huishangyun.com/UploadFile/huishang/"+
					MyApplication.getInstance().getCompanyID() + "/Photo/" + cBean.getPhoto(), holder.headView);*/
			com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(Constant.pathurl+
					MyApplication.getInstance().getCompanyID() + "/Photo/" + cBean.getPhoto(),holder.headView,MyApplication.getInstance().getOptions());
			if (type == 1) {
				//是客户
				holder.department.setText(cBean.getPhoneNum());//电话号码
				//holder.signature.setText(cBean.getName());//易加名称
				holder.signature.setText(cBean.getDepartment());//易加名称
				holder.department.setVisibility(View.VISIBLE);
				holder.signature.setVisibility(View.VISIBLE);

			}else {
				//是同事
				holder.department.setText( cBean.getRealName()+ " | " +cBean.getPhoneNum() );//电话号码
				holder.signature.setText(cBean.getSignature());
				holder.department.setVisibility(View.VISIBLE);
				holder.signature.setVisibility(View.VISIBLE);
			}
			
		}else {
			//是组
            // [删除]hbzhang 2014.7.24 显示组时，如果取的是个人的布局，会导致组名称显示不全。
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(R.layout.group_item_contact, null);
                holder = new ViewHolder();
				holder.headView = (ImageView)convertView.findViewById(R.id.contact_head);
				holder.name = (TextView)convertView.findViewById(R.id.contact_name);
				holder.letter = (TextView)convertView.findViewById(R.id.contact_letter);
				holder.department = (TextView) convertView.findViewById(R.id.contact_department);
				holder.signature = (TextView) convertView.findViewById(R.id.contact_signature);
				holder.lineTrue = (View) convertView.findViewById(R.id.line_true);
				holder.lineFalse = (View) convertView.findViewById(R.id.line_false);
				convertView.setTag(holder);
			}else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (latter) {
				holder.letter.setVisibility(View.VISIBLE);
			}else {
				holder.letter.setVisibility(View.GONE);
			}
			
			if (position == (list.size() - 1)) {
				holder.lineTrue.setVisibility(View.VISIBLE);
				holder.lineFalse.setVisibility(View.GONE);
			} else {
				
				String lastCatalog = list.get(position + 1).getLetter();
				if (cBean.getLetter().equals(lastCatalog)) {
					holder.lineTrue.setVisibility(View.GONE);
					holder.lineFalse.setVisibility(View.VISIBLE);
				}else {
					holder.lineTrue.setVisibility(View.VISIBLE);
					holder.lineFalse.setVisibility(View.GONE);
				}
				
			}
			holder.headView.setImageResource(R.drawable.contact_group);
			holder.letter.setText(cBean.getLetter());
			if (!cBean.getName().equals("")) {
				holder.name.setText(cBean.getName());
			}else {
				holder.name.setText(cBean.getJID());
			}
			holder.name.setTag(cBean);
			L.d("position = " + position);
			holder.department.setVisibility(View.GONE);
			holder.signature.setVisibility(View.GONE);
		}
		return convertView;
	}
	
	
	static class ViewHolder{
		public ImageView headView;
		public TextView name;
		public TextView letter;
		public TextView signature;
		public TextView department;
		public View lineTrue;
		public View lineFalse;

	}

	@Override
	public int getPositionForSection(int sectionIndex) {
		// TODO Auto-generated method stub
		ContactBean mContent;
		String l;
		for (int i = 0; i < getCount(); i++) {
			mContent = (ContactBean) list.get(i);
			l = mContent.getLetter();
			char firstChar = l.toUpperCase().charAt(0);
			if (firstChar == sectionIndex) {
				return i;
			}

		}
		mContent = null;
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
