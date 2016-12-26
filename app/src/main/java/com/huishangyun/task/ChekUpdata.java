package com.huishangyun.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.L;
import com.huishangyun.View.MyDialog;
import com.huishangyun.App.MyApplication;
import com.huishangyun.View.MyDialog.OnMyDialogClickListener;
import com.huishangyun.yun.R;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
/**
 * 检查服务器是否有更新
 * @author pan
 *
 */
public class ChekUpdata{
	
	private Context context;
	private String updataurl;
	private ProgressBar myProgress;
	private TextView myProgressTxt;
	private boolean CancelUpdata = false;
	private MyDialog mDownloadDialog;
	private String mSavePath;
	private boolean isIndex;
	
	public ChekUpdata(Context context) {
		this.context = context;
	}
	
	public void showChekUpdata(String updataurl,boolean isIndex) {
		L.d("url = " + updataurl);
		this.updataurl = updataurl;
		this.isIndex = isIndex;
		MyDialog mDialog = new MyDialog(context);
		mDialog.setMessage(this.updataurl.split("#")[0]);
		mDialog.setTitle("版本更新");
		mDialog.setTuerText("更新");
		mDialog.setOnMyDialogClickListener(new OnMyDialogClickListener() {
			
			@Override
			public void onTrueClick(MyDialog dialog) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				showDownloadDialog();
			}
			
			@Override
			public void onFlaseClick(MyDialog dialog) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				if (ChekUpdata.this.isIndex) {
					MyApplication.getInstance().removeActivity();
				}
			}
		});
		mDialog.show();
		
	}
	
	/**
	 * 显示下载对话框
	 */
	private void showDownloadDialog(){
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.softupdata, null);
		myProgress = (ProgressBar) view.findViewById(R.id.update_progress);
		myProgressTxt = (TextView) view.findViewById(R.id.update_progress_txt);
		myProgressTxt.setText("0%");
		mDownloadDialog = new MyDialog(context);
		mDownloadDialog.setTitle("正在更新");
		mDownloadDialog.setView(view);
		mDownloadDialog.showTrue(false);
		mDownloadDialog.setCancel(false);
		mDownloadDialog.setOnMyDialogClickListener(new OnMyDialogClickListener() {
			
			@Override
			public void onTrueClick(MyDialog dialog) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onFlaseClick(MyDialog dialog) {
				// TODO Auto-generated method stub
				CancelUpdata = true;
				dialog.dismiss();
				if (ChekUpdata.this.isIndex) {
					MyApplication.getInstance().removeActivity();
				}
			}
		});
		mDownloadDialog.show();
		downloadApk();
	}
	private int progress;
	/**
	 * 下载APK
	 */
	private void downloadApk(){
		new Thread(){
			public void run() {
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					String sdpath = Environment.getExternalStorageDirectory()
							+ "/";
					mSavePath = sdpath + "HSY_Yun/App";
					try {
						URL url = new URL(updataurl.split("#")[1]);
						HttpURLConnection conn = (HttpURLConnection) url
								.openConnection();
						conn.connect();
						int length = conn.getContentLength();
						InputStream is = conn.getInputStream();
						File file = new File(mSavePath);
						if (!file.exists()) {
							file.mkdir();
						}
						File apkFile = new File(mSavePath, "慧商云");
						FileOutputStream fos = new FileOutputStream(apkFile);
						int count = 0;
						byte buf[] = new byte[1024];
						do {
							int numread = is.read(buf);
							count += numread;
							progress = (int) (((float) count / length) * 100);
							handler.sendEmptyMessage(HanderUtil.case1);
							if (numread <= 0) {
								handler.sendEmptyMessage(HanderUtil.case2);
								break;
							}
							fos.write(buf, 0, numread);
						}while(!CancelUpdata);
						fos.close();
						is.close();
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				mDownloadDialog.dismiss();
			};
		}.start();
	}
	
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HanderUtil.case1:
				myProgress.setProgress(progress);
				myProgressTxt.setText(progress + "%");
				break;
			case HanderUtil.case2:
				//installApk();
				File apkfile = new File(mSavePath,"慧商云");
				openAPK(apkfile);
				break;

			default:
				break;
			}
		}
	};
	/**
	 * 安装APK文件
	 * @param file
	 */
	private void openAPK(File file) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}
}
