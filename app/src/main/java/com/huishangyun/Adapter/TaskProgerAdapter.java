package com.huishangyun.Adapter;

import java.util.List;

import com.huishangyun.Util.FileTools;
import com.huishangyun.Util.L;
import com.huishangyun.Util.TaskProgerUtil;
import com.huishangyun.Util.TimeUtil;
import com.huishangyun.yun.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TaskProgerAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<TaskProgerUtil> mList;
	private LayoutInflater mInflater;
	
	public TaskProgerAdapter(Context mContext, List<TaskProgerUtil> mList) {
		this.mContext = mContext;
		this.mList = mList;
		this.mInflater = LayoutInflater.from(this.mContext);
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
		final ViewHolder mHolder;
		TaskProgerUtil taskUtil = mList.get(position);
		if (convertView == null) {
			mHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.task_item_prog, null);
			mHolder.progname = (TextView) convertView.findViewById(R.id.task_proglist_progname);
			mHolder.name = (TextView) convertView.findViewById(R.id.task_proglist_name);
			mHolder.prog = (TextView) convertView.findViewById(R.id.task_proglist_prog);
			mHolder.time = (TextView) convertView.findViewById(R.id.task_proglist_time);
			mHolder.miaoshu = (TextView) convertView.findViewById(R.id.task_proglist_miaoshu);
			mHolder.layout = (LinearLayout) convertView.findViewById(R.id.task_proglist_line);
			convertView.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) convertView.getTag();
			mHolder.layout.removeAllViews();
		}
		
		mHolder.progname.setText("进度" + (position + 1));
		mHolder.name.setText(taskUtil.getManager_Name());
		mHolder.prog.setText(taskUtil.getProgressNum() + "");
		mHolder.time.setText(TimeUtil.getTime(taskUtil.getAddDateTime()));
		mHolder.miaoshu.setText(taskUtil.getNote());
		if (taskUtil.getAttachment() != null && !taskUtil.getAttachment().equals("")) {
			mHolder.layout.setVisibility(View.VISIBLE);
			String filenames[] = taskUtil.getAttachment().split("#"); 
			String filePath[] = taskUtil.getAttachmentPath().split("#");
			L.e(filenames.length + " 这是数组长度");
			for (int i = 0; i < filenames.length; i++) {
				mHolder.layout.addView(getFileView(filenames[i], filePath[i]));
			}
		} else {
			mHolder.layout.setVisibility(View.GONE);
		}
		
		return convertView;
	}
	
	
	private View getFileView(String fileName,final String filePath){
		View view = mInflater.inflate(R.layout.file_view, null);
		TextView textView = (TextView) view.findViewById(R.id.task_prog_listfiles);
		textView.setText(fileName);
		textView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FileTools.decodeFile("http://img.huishangyun.com/UploadFile/huishang/1016/Task/" + filePath, mContext);
			}
		});
		return view;
		
	}
	
	static class ViewHolder {
		public TextView progname;
		public TextView name;
		public TextView prog;
		public TextView time;
		public TextView miaoshu;
		public LinearLayout layout;
	}

}
