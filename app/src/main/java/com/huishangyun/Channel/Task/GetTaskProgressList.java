package com.huishangyun.Channel.Task;

import java.lang.reflect.Type;
import java.util.List;

import android.os.Handler;
import android.os.Message;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.TaskProgerUtil;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.model.Methods;

/**
 * 获取任务进度的线程
 * @author Pan
 *
 */
public class GetTaskProgressList implements Runnable{
	
	private int ID;
	private ResultForTask rForTask;
	
	public GetTaskProgressList(int ID, ResultForTask rForTask) {
		this.ID = ID;
		this.rForTask = rForTask;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		String result = DataUtil.callWebService(Methods.TASK_GETTASKPROG, getJson());
		L.e("results = " + result);
		if (result != null) {//判断是否有返回值
			Type type = new TypeToken<ZJResponse<TaskProgerUtil>>(){}.getType();
			ZJResponse<TaskProgerUtil> zjResponse = JsonUtil.fromJson(result, type);
			if (zjResponse.getCode() == 0) {//判断查询是否成功
				
				//接口回调
				Message msg = new Message();
				msg.what = HanderUtil.case1;
				msg.obj = zjResponse.getResults();
				mHandler.sendMessage(msg);
			} else {
				//接口回调
				mHandler.sendEmptyMessage(HanderUtil.case2);
			}
		} else {
			//接口回调
			mHandler.sendEmptyMessage(HanderUtil.case2);
		}
	}
	
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HanderUtil.case1:
				if (rForTask != null) {
					rForTask.onResults(ResultForTask.RESULT_OK, (List<TaskProgerUtil>) msg.obj);
				}
				
				break;
			case HanderUtil.case2:
				if (rForTask != null) {
					rForTask.onResults(ResultForTask.RESULT_FULL, null);
				}
				
				break;

			default:
				break;
			}
		};
	};
	
	/**
	 * 生成查询语句
	 * @return
	 */
	private String getJson() {
		
		ZJRequest zjRequest = new ZJRequest();
		zjRequest.setID(ID);
		return JsonUtil.toJson(zjRequest);
	}

}
