package com.huishangyun.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Base64;

/**
 * <p>Base64转换帮助类</p>
 * @author Pan
 * @version 亿企云APP 2014-08-26 15:52
 *
 */
public class Base64Util {
	/**
	 * <p>将文件转成base64 字符串</p>
	 * @param path 文件路径
	 * @return 转换后的base64码
	 * @throws Exception
	 */
	public static String encodeBase64File(String path) throws Exception{
		File file = new File(path);
		FileInputStream inputStream = new FileInputStream(file);
		byte[] buffer = new byte[(int)file.length()];
		inputStream.read(buffer);
		inputStream.close();
		return Base64.encodeToString(buffer, Base64.DEFAULT);
	}
	
	/**
	 * <p>将base64字符解码保存文件</p>
	 * @param base64Code
	 * @param targetPath
	 * @throws Exception
	 */
	public static void decoderBase64File(String base64Code, String targetPath) throws Exception{
	   byte [] baseByte = android.util.Base64.decode(base64Code, Base64.DEFAULT);
       FileOutputStream out = new FileOutputStream(targetPath);
       out.write(baseByte);
       out.close();
	}
	
	/**
	 * <p>将base64字符保存文本文件</p>
	 * @param base64Code
	 * @param targetPath
	 * @throws Exception
	 */
	public static void toFile(String base64Code, String targetPath) throws Exception{
		byte[] buffer = base64Code.getBytes();
        FileOutputStream out = new FileOutputStream(targetPath);
        out.write(buffer);
        out.close();
	}
	
}
