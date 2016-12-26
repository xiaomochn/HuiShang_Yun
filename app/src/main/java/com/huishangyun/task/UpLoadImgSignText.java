package com.huishangyun.task;

import android.os.Handler;
import android.os.Message;

import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.UpLoadFileUtil;

/**
 * 上传文件并添加水印
 * @author Pan
 *
 */

public class UpLoadImgSignText implements Runnable{
	
	private UpLoadFileTask.OnUpLoadResult oResult = null;
	private String picData;
	private String modulePage;
	private String fileName;
	private String companyID;
	private String signText;
	
	/**
	 * 构造方法
	 * @param picData 文件的base64码
	 * @param modulePage 要上传到的文件夹
	 * @param fileName 当前的文件名
	 * @param companyID 分销商ID
	 * @param signText 要添加的水印
	 */
	public UpLoadImgSignText(String picData, String modulePage, String fileName, String companyID, String signText) {
		this.picData = picData;
		this.modulePage = modulePage;
		this.fileName = fileName;
		this.companyID = companyID;
		this.signText = signText;
	}
	
	/**
	 * 设置监听接口
	 * @param oResult
	 */
	public void setUpLoadResult(UpLoadFileTask.OnUpLoadResult oResult) {
		this.oResult = oResult;
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
		String result = UpLoadFileUtil.UpLoadImgSignText(picData, modulePage, fileName, companyID, signText);
		if (result != null) {
			Message msg = new Message();
			msg.what = HanderUtil.case1;
			msg.obj = result;
			mHandler.sendMessage(msg);
		} else {
			mHandler.sendEmptyMessage(HanderUtil.case2);
		}
		
	}

}
