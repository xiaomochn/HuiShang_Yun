package com.huishangyun.Adapter;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.huishangyun.Util.HanderUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

public class GetBitmap {
	
	//public static Bitmap bitmap;
	//public static Handler myHandler;
	public static ImageView img;
	
	public GetBitmap(ImageView img){
		this.img = img;
		
	}
		static Handler myHandler = new Handler(){
			public void handleMessage(Message msg) {
				if (msg.what == HanderUtil.case1) {
					Bitmap bitmap = (Bitmap) msg.obj;
					img.setImageBitmap(bitmap);
					Log.e("--------------------", "myHandler:" + bitmap);
				}
			};
		};

	
	public static void getImg(final String path){
		new Thread(){
			public void run() {
				try {
					Bitmap bitmap = getBitmap(path);
					Message msg = new Message();
					msg.obj = bitmap;
					msg.what = HanderUtil.case1;
					myHandler.sendMessage(msg);
					Log.e("--------------------", "Thread:" + bitmap);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			};
		}.start();

	}
	
	//第二种方法  
    public static Bitmap getBitmap(String s){  
        Bitmap bitmap = null;  
        try{  
            URL url = new URL(s);  
            bitmap = BitmapFactory.decodeStream(url.openStream());  
        } catch (Exception e){  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
          
        return bitmap;  
    } 
    
  //第一种方法  
    public static Bitmap getHttpBitmap(String data) {  
        Bitmap bitmap = null;  
        try {  
            //初始化一个URL对象  
            URL url = new URL(data);  
            //获得HTTPConnection网络连接对象  
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();  
            connection.setConnectTimeout(5*1000);  
            connection.setDoInput(true);  
            connection.connect();  
            //得到输入流  
            InputStream is = connection.getInputStream();  
            Log.e("TAG", "*********inputstream**"+is);  
            bitmap = BitmapFactory.decodeStream(is);  
            Log.e("TAG", "*********bitmap****"+bitmap);  
            //关闭输入流  
            is.close();  
            //关闭连接  
            connection.disconnect();  
        } catch (Exception e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
          
        return bitmap;  
    }  
}
