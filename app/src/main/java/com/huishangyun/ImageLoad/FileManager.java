package com.huishangyun.ImageLoad;


public class FileManager {

	public static String getSaveFilePath() {
		if (CommonUtil.hasSDCard()) {
			return CommonUtil.getRootFilePath() + "HSY_Yun/img/";
		} else {
			return CommonUtil.getRootFilePath() + "HSY_Yun/img";
		}
	}
}
