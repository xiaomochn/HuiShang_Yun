package com.huishangyun.model;

import java.io.File;

import com.huishangyun.Channel.Task.TaskFilelistActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

/**
 * 调用系统功能
 * @author Pan
 * @version v1.0-2014-08-15
 *
 */
public class CallSystemApp {
	
	/**
	 * 调用拍照返回码
	 */
	public static final int EXTRA_TAKE_PHOTOS = 0;
	
	/**
	 * 调用摄像返回码
	 */
	public static final int EXTRA_TAKE_VIDEOS = 1;
	
	/**
	 * 调用录音返回码
	 */
	public static final int EXTRA_TAKE_RECORDS = 2;
	
	/**
	 * 调用录文件选择返回码
	 */
	public static final int EXTRA_TAKE_FILES = 3;
	
	/**
	 * 调用系统相机拍照
	 * @param mContext-上下文对象
	 * @param path-保存路径
	 * 
	 */
	public static void callCamera(Context mContext, String path) {
		Intent mIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		mIntent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File(path)));
		((Activity) mContext).startActivityForResult(mIntent, EXTRA_TAKE_PHOTOS);
	}
	
	
	public static void callCamera(Context mContext, String path, int requestCode) {
		Intent mIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		mIntent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File(path)));
		((Activity) mContext).startActivityForResult(mIntent, requestCode);
	}
	
	/**
	 * 调用系统相机摄像
	 * @param mContext-上下文对象
	 * @param path-保存路径
	 * 
	 */
	public static void callVideo(Context mContext, String path) {
		Intent mIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		mIntent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File(path)));
		((Activity) mContext).startActivityForResult(mIntent, EXTRA_TAKE_VIDEOS);
	}
	
	/**
	 * 调用系统录音
	 * @param mContext-上下文对象
	 */
	public static void callRecord(Context mContext) {
		Intent mIntent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
		((Activity) mContext).startActivityForResult(mIntent, EXTRA_TAKE_RECORDS);
	}
	
	
	/*public static void callContentCamera(Context mContext, String path) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		SimpleDateFormat timeStampFormat = new SimpleDateFormat(
		"yyyy_MM_dd_HH_mm_ss");
		String filename = timeStampFormat.format(new Date());
		ContentValues values = new ContentValues();
		values.put(Media.TITLE, filename);

		photoUri = mContext.getContentResolver().insert(
		MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

		intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

		startActivityForResult(intent, reqCode);
	}*/
	
	/**
	 * 调用系统文件选择器
	 * @param mContext-上下文对象
	 */
	public static void callFiles(Context mContext) {
		Intent intent = new Intent();
		intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory().toString())), "*/*");      // 设置起始文件夹和文件类型
		intent.setClass(mContext, TaskFilelistActivity.class);
		((Activity) mContext).startActivityForResult(intent, EXTRA_TAKE_FILES);
	}
}
