package com.huishangyun.Adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.huishangyun.Util.L;
import com.huishangyun.Util.TakeDetails;
import com.huishangyun.Util.TimeUtil;
import com.huishangyun.manager.EnumManager;
import com.huishangyun.model.EnumKey;
import com.huishangyun.Util.ColorUtil;
import com.huishangyun.yun.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 任务主界面适配器
 * @author Pan
 *
 */
public class TaskAdapter extends BaseAdapter{
	
	private Context context;
	private List<TakeDetails> mList;
	private LayoutInflater mInflater;
	private Context mContext;
	
	public TaskAdapter(Context context, List<TakeDetails> mList) {
		this.context = context;
		this.mList = mList;
		this.mInflater = LayoutInflater.from(context);
		mContext = context;
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
		final ViewHolder mHolder;//组件容器
		
		//如果convertView不存在就重新实例化各组件
		if (convertView == null) {
			//加载布局文件
			convertView = mInflater.inflate(R.layout.listview_task_item, null);
			mHolder = new ViewHolder();
			
			//根据布局寻找组件
			mHolder.content = (TextView) convertView.findViewById(R.id.item_task_content);
			mHolder.stact = (TextView) convertView.findViewById(R.id.item_task_stact);
			mHolder.contact = (TextView) convertView.findViewById(R.id.item_task_contact);
			mHolder.time = (TextView) convertView.findViewById(R.id.item_task_time);
			
			//设置convertView的Tag
			convertView.setTag(mHolder);
		} else {
			//通过查找Tag避免重新寻找组件ID
			mHolder = (ViewHolder) convertView.getTag();
		}
		mHolder.content.setText(mList.get(position).getTitle());
		mHolder.contact.setText(mList.get(position).getExeManager_Name());
		mHolder.time.setText(TimeUtil.getTime(mList.get(position).getAddDateTime()));
		//mList.get(position).get
		String stact = EnumManager.getInstance(context).getEmunForIntKey(EnumKey.ENUM_TASKSTATUS,  "" + mList.get(position).getStatus()).getLab();
		L.e("stact = " + stact);
		try {
			SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = formatDate.parse(TimeUtil.getTimeForT(mList.get(position).getEndTime()));
			long time = date.getTime();
			L.e("逾期时间" + time);
			L.e("当前时间" + new Date().getTime());
			if ((time + 86400000) < new Date().getTime() && (mList.get(position).getStatus() == 1 || mList.get(position).getStatus() == 2)) {
				stact = stact + "(逾期)";
			} 
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		mHolder.stact.setText(stact);
		ColorUtil.setTaskColor(mContext, mList.get(position).getStatus(), mHolder.stact);
		return convertView;
	}
	
	
	/**
	 * 控件适配器
	 * @author Pan
	 *
	 */
	static class ViewHolder{
		//内容
		public TextView content;
		
		//状态
		public TextView stact;
		
		//联系人
		public TextView contact;
		
		//时间
		public TextView time;
	}
	
}
