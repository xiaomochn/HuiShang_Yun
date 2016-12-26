package com.huishangyun.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import com.huishangyun.Util.ResponseBean;
import com.huishangyun.download.DownloadInterface;

import android.util.Log;

public class DownLoadFileThreadTask implements Runnable{
	private String path; // 服务器路径
	private String filepath; // 本地文件路径
	public static final int DOWNLOAD_SUCCESS = 999;
	public static final int DOWNLOAD_FAILURE = -110;
	public static final int DOWNLOAD_ING = 111;
	private boolean stop = false; //停止下载标志
	private DownloadInterface callback; //消息回调
	
	/**
	 * 构造函数，用于初始化相应参数
	 * @param path 服务器路径
	 * @param filepath 文件保存路径
	 * @param callback 回调接口
	 */
	public DownLoadFileThreadTask(String path, String filepath, DownloadInterface callback) {
		this.path = path;
		this.filepath = filepath;
		this.callback = callback;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		download();
	}
	
	/**
	 * 停止下载
	 */
	public void stop(){
		if (!stop) {
			stop = true;
			Log.e("停止", "停止指令已经执行!");
		}
	}
	
	
	/**
	 * 开始下载文件
	 */
	private void download(){
		if (path == null) {
			return;
		}
		if (filepath == null) {
			return;
		}
		if ( callback == null ) {
			return;
		}
		try {
			URL url;
			url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5000);
			if(conn.getResponseCode() == 200){
				int total =  conn.getContentLength();
				stop = false;
				ResponseBean response = new ResponseBean();
				InputStream is = conn.getInputStream();
				File file = new File(filepath);
				FileOutputStream fos = new FileOutputStream(file);
				byte[] buffer = new byte[1024 * 1024]; //1M
				int len = 0;
				int process = 0;
				while((len = is.read(buffer))!=-1 && !stop){
					fos.write(buffer, 0, len);
					process +=len;
					response.setCurrent(process/1024);
					response.setMsg(null);
					response.setTotal(total/1024);
					if (total == process) {
						response.setStatus(DOWNLOAD_SUCCESS);
					} else {
						response.setStatus(DOWNLOAD_ING);
					}
					callback.sendMsg(response);
					Thread.sleep(50);
				}
				fos.flush();
				fos.close();
				is.close();
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sendFailureMsg(e.getMessage());
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sendFailureMsg(e.getMessage());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sendFailureMsg(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sendFailureMsg(e.getMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			sendFailureMsg(e.getMessage());
			e.printStackTrace();
		} 
	}
	
	/**
	 * 发送错误消息
	 * @param msg
	 */
	private void sendFailureMsg(String msg){
		ResponseBean errorBean = new ResponseBean();
		errorBean.setStatus(DOWNLOAD_FAILURE);
		errorBean.setMsg(msg);
		callback.sendMsg(errorBean);
	}
}
