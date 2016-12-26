package com.huishangyun.task;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;
import org.json.JSONTokener;

import com.huishangyun.Util.L;

import android.os.AsyncTask;
/**
 * 上传头像
 * @author Pan
 *
 */
public class HeadImgTask extends AsyncTask<String, String, Boolean>{
	private static final String url = "http://newws.huishangyun.com/MicroWeb/upload.aspx"; //头像上传接口
	private ImageLoadCallback imageLoadCallback;
	private String Msg;
	/**
	 * 设置接口
	 * @param imageLoadCallback
	 */
	public void setImgCallback(ImageLoadCallback imageLoadCallback){
		this.imageLoadCallback = imageLoadCallback;
	}
	/**
	 * 注销接口
	 */
	public void removeImgCallBack(){
		if (imageLoadCallback != null) {
			imageLoadCallback = null;
		}
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if(result){
			imageLoadCallback.LoadImgCallback(Msg, true);
		}else {
			imageLoadCallback.LoadImgCallback("", false);
		}
	}
	
	@Override
	protected Boolean doInBackground(String... params) {
		// TODO Auto-generated method stub
		String filepath = params[0];
		HttpURLConnection connection = null;
		DataOutputStream dos = null;
		FileInputStream fin = null;
		String boundary = "---------7d4a6d158c9";
		String urlServer = url;
		String lineEnd = "\r\n";
		int bytesAvailable, bufferSize, bytesRead;
		int maxBufferSize = 1*1024*512;  
        byte[] buffer = null;
        try {
        	URL url = new URL(urlServer);
        	connection = (HttpURLConnection) url.openConnection();  
            connection.setConnectTimeout(20000);
              
            // 允许向url流中读写数据  
            connection.setDoInput(true);  
            connection.setDoOutput(true);  
            connection.setUseCaches(true);  
              
            // 启动post方法  
            connection.setRequestMethod("POST");  
            // 设置请求头内容  
            connection.setRequestProperty("connection", "Keep-Alive");  
            connection.setRequestProperty("Content-Type", "text/plain");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            // 开始伪造POST Data里面的数据  
            dos = new DataOutputStream(connection.getOutputStream());
            fin = new FileInputStream(filepath);
            L.d("filepath = " + filepath);
            //--------------------开始伪造上传信息-----------------------------------  
            String fileMeta = 
            		"--" + boundary + lineEnd +  
                    "Content-Disposition: form-data; name=\"uploadedfile\"; filename=\"" + filepath + "\"" + lineEnd +  
                    "Content-Type: image/jpeg" + lineEnd + lineEnd;
            String action = "--" + boundary + lineEnd + 
            		"Content-Disposition: form-data; name=\"action\"" + lineEnd +lineEnd +"Users" +
            		lineEnd;
            // 向流中写入fileMeta  
            dos.write((action + fileMeta).getBytes());  
            bytesAvailable = fin.available();  
            bufferSize = Math.min(bytesAvailable, maxBufferSize);  
            buffer = new byte[bufferSize];  
            bytesRead = fin.read(buffer, 0, bufferSize);  
            while(bytesRead > 0) {  
                dos.write(buffer, 0, bufferSize);  
                bytesAvailable = fin.available();  
                bufferSize = Math.min(bytesAvailable, maxBufferSize);  
                bytesRead = fin.read(buffer, 0, bufferSize);  
            }  
            dos.writeBytes(lineEnd+lineEnd);  
            // POST Data结束  
            dos.writeBytes("--" + boundary + "--");  
            // Server端返回的信息  
            L.d("Server端返回的信息  = " + connection.getResponseCode());
            if (connection.getResponseCode() == 200) {
            	InputStream is=connection.getInputStream();  
                //将输入流转换成字符串  
                ByteArrayOutputStream baos=new ByteArrayOutputStream();  
                byte [] buffer2=new byte[1024];  
                int len=0;  
                while((len=is.read(buffer2))!=-1){  
                    baos.write(buffer2, 0, len);  
                }  
                String json=baos.toString();  
                baos.close();  
                is.close();
                JSONTokener jsonParser = new JSONTokener(json);
                JSONObject person = (JSONObject) jsonParser.nextValue();
                Msg = person.getString("Msg");
                L.d("Msg:" + Msg);
            	return true;
            }
        }catch (Exception e) {  
	           e.printStackTrace();  
	    }
		return false;
	}

}
