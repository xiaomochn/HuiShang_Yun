
package com.huishangyun.Util;

import java.io.File;
import java.text.DecimalFormat;
import java.io.FileInputStream;

public class GetFileSizeUtil
{

	private static GetFileSizeUtil instance;

	public GetFileSizeUtil()
	{

	}

	public static GetFileSizeUtil getInstance()
	{
		if (instance == null)
		{
			instance = new GetFileSizeUtil();
		}
		return instance;
	}

	/**
	 * 获取单个文件的大小
	 * 
	 * @param f
	 * @return
	 * @throws Exception
	 */
	public long getFileSizes(File f) throws Exception
	{
		long s = 0;
		if (f.exists())
		{
			FileInputStream fis = null;
			fis = new FileInputStream(f);
			s = fis.available();
		}
		else
		{
			f.createNewFile();
			System.out.println("文件不存在");
		}
		return s;
	}

	/**
	 * 获取文件夹的大小
	 * 
	 * @return
	 * @throws Exception
	 */
	public long getFileSize(File f) throws Exception
	{
		long size = 0;
		File flist[] = f.listFiles();
		for (int i = 0; i < flist.length; i++)
		{
			if (flist[i].isDirectory())
			{
				size = size + getFileSize(flist[i]);
			}
			else
			{
				size = size + flist[i].length();
			}
		}
		return size;
	}

	/**
	 * 转换文件的大小
	 * 
	 * @param fileS
	 * @return
	 */
	public String FormetFileSize(long fileS)
	{
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024)
		{
			fileSizeString = df.format((double) fileS) + "B";
		}
		else if (fileS < 1048576)
		{
			fileSizeString = df.format((double) fileS / 1024) + "K";
		}
		else if (fileS < 1073741824)
		{
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		}
		else
		{
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

}