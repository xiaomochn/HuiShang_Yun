package com.huishangyun.Util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class StreamTool {
	public static byte[] getBytes(InputStream is) throws Exception{  
        ByteArrayOutputStream bos = new ByteArrayOutputStream();  
        byte[] buffer = new byte[1024];  
        int len = 0;  
        while((len = is.read(buffer))!=-1){  
            bos.write(buffer, 0, len);  
        }  
        is.close();  
        bos.flush();  
        byte[] result = bos.toByteArray();   
        return  result;  
    }  
}
