package com.huishangyun.Channel.Task;

import java.lang.reflect.Type;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.model.Methods;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.JsonUtil;

import android.os.Handler;
import android.os.Message;

/**
 * 获取任务详情的线程
 * @author Pan
 *
 */
public class GetTaskDetails implements Runnable{
	
	private ResultForTask rForTask;
	private int ID;
	
	/**
	 * 构造方法
	 * @param rForTask
	 * @param json
	 */
	public GetTaskDetails(ResultForTask rForTask, int ID) {
		this.rForTask = rForTask;
		this.ID = ID;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		String result = DataUtil.callWebService(Methods.TASK_GETTASK, getJson());
		L.e("result = " + result);
		if (result != null) {//判断是否有返回值
			Type type = new TypeToken<ZJResponse<TaskModel>>(){}.getType();
			ZJResponse<TaskModel> zjResponse = JsonUtil.fromJson(result, type);
			if (zjResponse.getCode() == 0) {//判断查询是否成功
				
				//接口回调
				Message msg = new Message();
				msg.what = HanderUtil.case1;
				msg.obj = zjResponse.getResult();
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
					rForTask.onResult(ResultForTask.RESULT_OK, (TaskModel) msg.obj);
				}
				break;
			case HanderUtil.case2:
				if (rForTask != null) {
					rForTask.onResult(ResultForTask.RESULT_FULL, null);
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
