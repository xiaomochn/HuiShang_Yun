package com.huishangyun.Channel.Customers;

import java.lang.reflect.Type;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.L;
import com.huishangyun.model.MemberDetail;
import com.huishangyun.model.Methods;

import android.os.Handler;
import android.os.Message;

/**
 * 客户操作帮助类
 * @author Pan
 *
 */
public class MemberUtil {
	public static final int CALLBACK_FALL = -1;
	public static final int CALLBACK_OK = 0;
	private OnResultCallBack callBack;
	/**
	 * 获取任务详情
	 * @param callBack
	 * @param MemberID 客户ID
	 */
	public void getMemberDetail(OnResultCallBack callBack, int MemberID) {
		this.callBack = callBack;
		new Thread(new getMenberInfo(MemberID)).start();
	}
	
	/**
	 * 删除客户
	 * @param callBack 
	 * @param MemberID 客户ID
	 */
	public void deleteMember(OnResultCallBack callBack, int MemberID) {
		this.callBack = callBack;
		new Thread(new DeleteMember(MemberID)).start();
	}
	
	public interface OnResultCallBack{
		public void onResultBack(int resultCode, MemberDetail detail);
	}
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HanderUtil.case1:
				if (callBack != null) {
					callBack.onResultBack(CALLBACK_OK, (MemberDetail) msg.obj);
				}
				break;
			case HanderUtil.case2:
				if (callBack != null) {
					callBack.onResultBack(CALLBACK_FALL, null);
				}
				break;
			case HanderUtil.case3:
				if (callBack != null) {//删除成功
					callBack.onResultBack(CALLBACK_OK, null);
				}
				break;
			default:
				break;
			}
		};
	};
	
	private class getMenberInfo implements Runnable {
		private int MemberID;
		public getMenberInfo(int MemberID) {
			this.MemberID = MemberID;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			ZJRequest zjRequest = new ZJRequest();
			zjRequest.setID(MemberID);
			
			String result = DataUtil.callWebService(Methods.GET_MEMBER, JsonUtil.toJson(zjRequest));
			L.e("result = " + result);
			if (result == null) {
				mHandler.sendEmptyMessage(HanderUtil.case2);
				return;
			}
			Type type = new TypeToken<ZJResponse<MemberDetail>>(){}.getType();
			ZJResponse<MemberDetail> zjResponse = JsonUtil.fromJson(result, type);
			if (zjResponse.getCode() != 0) {
				mHandler.sendEmptyMessage(HanderUtil.case2);
				return;
			}
			Message msg = new Message();
			msg.what = HanderUtil.case1;
			msg.obj = zjResponse.getResult();
			mHandler.sendMessage(msg);
			return;
		}
		
	}
	
	private class DeleteMember implements Runnable {
		private int MemberID;
		public DeleteMember(int MemberID) {
			this.MemberID = MemberID;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			ZJRequest zjRequest = new ZJRequest();
			zjRequest.setCompany_ID(MyApplication.getInstance().getCompanyID());
			zjRequest.setID(MemberID);
			L.e("json = " + JsonUtil.toJson(zjRequest));
			String result = DataUtil.callWebService(Methods.SET_MEMBER_DELETE, JsonUtil.toJson(zjRequest));
			if (result == null) {
				mHandler.sendEmptyMessage(HanderUtil.case2);
				return;
			}
			Type type = new TypeToken<ZJResponse>(){}.getType();
			ZJResponse zjResponse = JsonUtil.fromJson(result, type);
			if (zjResponse.getCode() != 0) {
				mHandler.sendEmptyMessage(HanderUtil.case2);
				return;
			}
			mHandler.sendEmptyMessage(HanderUtil.case3);
			return;
		}
	}
}
