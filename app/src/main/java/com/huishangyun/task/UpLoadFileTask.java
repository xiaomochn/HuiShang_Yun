package com.huishangyun.task;

import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.UpLoadFileUtil;

import android.os.Handler;
import android.os.Message;

public class UpLoadFileTask implements Runnable{
	
	private OnUpLoadResult oResult = null;
	private String fileData;
	private String modulePage;
	private String fileName;
	private String companyID;
	
	/**
	 * 构造方法
	 */
	public UpLoadFileTask(String fileData, String modulePage, String fileName, String companyID) {
		this.fileData = fileData;
		this.modulePage = modulePage;
		this.fileName = fileName;
		this.companyID = companyID;
	}
	
	/**
	 * 返回上传结果的接口
	 * @author Pan
	 *
	 */
	public interface OnUpLoadResult {
		
		/**
		 * 附件上传返回结果监听
		 * @param FileName-文件名
		 * @param FilePath-文件地址(服务器上的文件地址)
		 * @param isSuccess-是否上传成功
		 */
		public void onUpLoadResult(String FileName, String FilePath, boolean isSuccess);
	}
	
	/**
	 * 设置接口
	 * @param oResult
	 */
	public void setUpLoadResult(OnUpLoadResult oResult) {
		this.oResult = oResult;
	}
	
	/**
	 * 移除接口
	 */
	public void removeUpLoadResult() {
		this.oResult = null;
	}
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HanderUtil.case1:
				if (oResult != null) {
					oResult.onUpLoadResult(fileName, (String) msg.obj, true);
				}
				break;
			case HanderUtil.case2:
				if (oResult != null) {
					oResult.onUpLoadResult(fileName, "", false);
				}
				break;

			default:
				break;
			}
		};
	};
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		String result = UpLoadFileUtil.UpLoadFile(fileData, modulePage, fileName, companyID);
		L.e("result = " + result);
		if (result != null) {
			Message msg = new Message();
			msg.what = HanderUtil.case1;
			msg.obj = result;
			mHandler.sendMessage(msg);
		}  else {
			mHandler.sendEmptyMessage(HanderUtil.case2);
		}
	}
	
	
	
}
