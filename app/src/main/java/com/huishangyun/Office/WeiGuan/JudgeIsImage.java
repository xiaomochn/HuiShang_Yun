package com.huishangyun.Office.WeiGuan;


/**
 * 判断文件是不是图片 返回tru or false
 * @author xsl
 *
 */
public class JudgeIsImage {

	
	 /**
     * 根据后缀名判断是否是图片文件
     * 
     * @param type
     * @return 是否是图片结果true or false
     */
    public static boolean isImage(String fileName) {
    	 String type = getFileType(fileName);
            if (type != null
                            && (type.equals("jpg") || type.equals("gif")
                                            || type.equals("png") || type.equals("jpeg")
                                            || type.equals("bmp") || type.equals("wbmp")
                                            || type.equals("ico") || type.equals("jpe"))) {
                    return true;
            }
            return false;
    }
    
    /**
     * 获取文件后缀名
     * 
     * @param fileName
     * @return 文件后缀名
    */
    public static String getFileType(String fileName) {
            if (fileName != null) {
                    int typeIndex = fileName.lastIndexOf(".");
                    if (typeIndex != -1) {
                            String fileType = fileName.substring(typeIndex + 1)
                                            .toLowerCase();
                            return fileType;
                    }
            }
            return "";
    }

	
}
