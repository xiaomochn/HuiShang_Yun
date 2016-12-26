package com.huishangyun.common;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.apache.http.util.ByteArrayBuffer;

public class StreamTool {

	public static byte[] read(InputStream is) throws Exception{
		//把读到的数据先放在内存里面。
		ByteArrayOutputStream oStream = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int len = 0;
		//is.read(data)返回的是int类型，是byte数组的长度
		while ((len=is.read(data))!=-1) {
			oStream.write(data, 0, len);//一边读一边往内存里写入数据。
			
		}
		oStream.close();
		is.close();
		return oStream.toByteArray();//返回内存中的数据
	}

}
