package com.huishangyun.Channel.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Util.L;
import com.huishangyun.Util.TimeUtil;
import com.huishangyun.manager.FileManager;
import com.huishangyun.model.CallSystemApp;
import com.huishangyun.model.Files;
import com.huishangyun.yun.R;

public class TaskChooseFileActivity extends BaseActivity {
	
	private TextView chooseFile;//文件选择按钮
	private ListView fileListView;//最近文件列表
	private List<Map<String, Object>> files;
	private ChooseFileAdapter adapter;
	private int positions = -1;
	private Button submit;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_choosefile);
		initView();
		initBackTitle("返回");
		setResult(RESULT_CANCELED);
	}
	
	/**
	 * 实例化各组件
	 */
	private void initView() {
		chooseFile = (TextView) this.findViewById(R.id.task_choosefile_file);
		fileListView = (ListView) this.findViewById(R.id.task_choosefile_list);
		files = new ArrayList<Map<String,Object>>();
		List<Files> mFiles = FileManager.getInstance(TaskChooseFileActivity.this).getFileLsit();
		for (Files file : mFiles) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("files", file);
			map.put("check", false);
			files.add(map);
		}
		adapter = new ChooseFileAdapter();
		fileListView.setAdapter(adapter);
		chooseFile.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CallSystemApp.callFiles(TaskChooseFileActivity.this);
			}
		});
		fileListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				L.e("进入列表点击事件");
				if (positions == -1) {
					positions = arg2;
					Map<String, Object> map = files.get(arg2);
					if ((Boolean) map.get("check")) {
						map.put("check", false);
					} else {
						map.put("check", true);
					}
					files.remove(arg2);
					files.add(arg2, map);
				} else {
					Map<String, Object> map = files.get(positions);
					if ((Boolean) map.get("check")) {
						map.put("check", false);
					} else {
						map.put("check", true);
					}
					files.remove(positions);
					files.add(positions, map);
					positions = arg2;
					Map<String, Object> map2 = files.get(arg2);
					if ((Boolean) map2.get("check")) {
						map2.put("check", false);
					} else {
						map2.put("check", true);
					}
					files.remove(arg2);
					files.add(arg2, map2);
				}
				adapter.notifyDataSetChanged();
				
			}
		});
		submit = (Button) this.findViewById(R.id.task_choosefile_submit);
		submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				L.e("进入点击事件");
				if (positions != -1) {
					Files mFiles = (Files) files.get(positions).get("files");
					Bundle conData = new Bundle();
					conData.putString("results", "Thanks Thanks");
					conData.putString("FileName", mFiles.getFile_Name());
					Intent intent = new Intent();
					intent.putExtras(conData);
					Uri startDir = Uri.fromFile(new File(mFiles.getFile_Path()));
					intent.setDataAndType(startDir,
							"vnd.android.cursor.dir/lysesoft.andexplorer.file");
					setResult(RESULT_OK, intent);
					finish();
				}
			}
		});
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (requestCode) {
		
		case CallSystemApp.EXTRA_TAKE_FILES:
			if (resultCode == RESULT_OK) {//返回有结果
				setResult(RESULT_OK,data);//将路径信息返回到上一层
				Files files = new Files();
				files.setFile_Name(data.getExtras().getString("FileName"));
				files.setFile_Path(data.getData().getPath());
				files.setTime(System.currentTimeMillis());
				FileManager.getInstance(TaskChooseFileActivity.this).saveFiles(files);
				finish();
			}
			
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private class ChooseFileAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return files.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return files.get(position);
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
				convertView = LayoutInflater.from(TaskChooseFileActivity.this).inflate(R.layout.task_item_choosefile, null);
				mHolder = new ViewHolder();
				mHolder.filename = (TextView) convertView.findViewById(R.id.task_choosefile_filename);
				mHolder.Time = (TextView) convertView.findViewById(R.id.task_choosefile_time);
				mHolder.checkBox = (CheckBox) convertView.findViewById(R.id.task_choosefile_chekbox);
				convertView.setTag(mHolder);
			} else {
				mHolder = (ViewHolder) convertView.getTag();
			}
			Map<String, Object> map = files.get(position);
			Files mFiles = (Files) map.get("files");
			mHolder.filename.setText(mFiles.getFile_Name());
			mHolder.Time.setText(TimeUtil.getInfoTime(mFiles.getTime()));
			if ((Boolean) map.get("check")) {
				mHolder.checkBox.setChecked(true);
			} else {
				mHolder.checkBox.setChecked(false);
			}
			
			return convertView;
		}
		
	}
	
	static class ViewHolder {
		public TextView filename;
		public TextView Time;
		public CheckBox checkBox;
	}
}
