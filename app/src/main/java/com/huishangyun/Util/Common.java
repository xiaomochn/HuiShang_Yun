package com.huishangyun.Util;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class Common {
	//语音操作对象  
    public static MediaPlayer mPlayer = null;  
    public static MediaRecorder mRecorder = null;  
    public static boolean isRecvoid = false;
    
	//开始录音    
    public static void startLu(String FileName) { 
    	 mRecorder = new MediaRecorder();
         mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);  
         mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);  
         mRecorder.setOutputFile(FileName);  
         mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);  
         try {  
             mRecorder.prepare();  
         } catch (IOException e) {   
         }  
         mRecorder.start();
         isRecvoid = true;
    }  

    //停止录音     
    public static void stopLu() {   
         mRecorder.stop();  
         mRecorder.release();  
         mRecorder = null;  
         isRecvoid = false;
    }  
    
    
    public static void stopRecovid() {
    	L.e("调用stopRecovid");
    	if (isRecvoid) {
    		stopLu();
		} else {
			mRecorder = null;
			isRecvoid = false;
		}
    	
    	
    }
 
    //播放录音  
    public static void startplay(String FileName) {   
        mPlayer = new MediaPlayer();  
        try{  
            mPlayer.setDataSource(FileName);  
            mPlayer.prepare();  
            mPlayer.start();  
        }catch(IOException e){  
             
        }  
    } 
    
  //停止播放录音  
    public static void stopPlay() {   
        mPlayer.release();  
        mPlayer = null;  
    }
    
    /**
     * 获取图片缩略图的方法
     * @param imagePath
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getImageThumbnail(String imagePath, int width, int height) {  
        Bitmap bitmap = null;  
        BitmapFactory.Options options = new BitmapFactory.Options();  
        options.inJustDecodeBounds = true;  
        // 获取这个图片的宽和高，注意此处的bitmap为null  
        bitmap = BitmapFactory.decodeFile(imagePath, options);  
        options.inJustDecodeBounds = false; // 设为 false  
        // 计算缩放比  
        int h = options.outHeight;  
        int w = options.outWidth;  
        int beWidth = w / width;  
        int beHeight = h / height;  
        int be = 1;  
        if (beWidth < beHeight) {  
            be = beWidth;  
        } else {  
            be = beHeight;  
        }  
        if (be <= 0) {  
            be = 1;  
        }  
        options.inSampleSize = be;  
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false  
        bitmap = BitmapFactory.decodeFile(imagePath, options);  
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象  
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,  
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);  
        return bitmap;  
    }  

 // 获取listview的高度
 	public static void setListViewHeight(ListView listView, BaseAdapter adapter) {
		// 获取ListView对应的Adapter
		adapter = (BaseAdapter) listView.getAdapter();
		if (adapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0, len = adapter.getCount(); i < len; i++) {// listAdapter.getCount()返回数据项的数目

			View listItem = adapter.getView(i, null, listView);
			listItem.measure(0, 0); // 计算子项View 的宽高
			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (adapter.getCount() - 1));
// 		params.height += 5;// 因为我在listview的属性添加了padding=5dip

		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		Log.i("-----------", "获取到listview的高度:" + params);
		listView.setLayoutParams(params);
 	}
}
