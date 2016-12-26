package com.huishangyun.Util;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by liu on 2016/5/14.
 * 清理缓存文件管理类
 */
public class DataCleanManager {

    /**
     * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache)
     */
    public static void cleanInternalCache(Context context) {
        deleteFilesByDirectory(context.getCacheDir());
    }

    /**
     * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases) * * @param context
     */
    public static void cleanDatabases(Context context) {
        deleteFilesByDirectory(new File("/data/data/"
                + context.getPackageName() + "/databases"));
    }

    /**
     * * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs) * * @param
     * context
     */
    public static void cleanSharedPreference(Context context) {
        deleteFilesByDirectory(new File("/data/data/"
                + context.getPackageName() + "/shared_prefs"));
    }

    /**
     * 清除本应用app_vebview(/data/data/com.xxx.xxx/app_vebview)
     */
    public static void cleanAppWebView(Context context) {
        deleteFilesByDirectory(new File("/data/data/"
                + context.getPackageName() + "/app_webview"));
    }

    /**
     * 清除本应用Acache(/data/data/com.xxx.xxx/Acache)
     *
     * @param context
     */
    public static void cleanAcache(Context context) {
        RecursionDeleteFile(new File("/data/data/"
                + context.getPackageName() + "/cache"));
    }

    /**
     * 清除/data/data/com.xxx.xxx/files下的内容 * * @param context
     */
    public static void cleanFiles(Context context) {
        deleteFilesByDirectory(context.getFilesDir());
    }

    /**
     * * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache/ACache)
     * context
     */
    public static void cleanExternalCache(Context context) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED))
            deleteFilesByDirectory(context.getExternalCacheDir());
    }

    /**
     * 清除自定义路径下的文件，使用需小心，请不要误删。而且只支持目录下的文件删除
     */
    public static void cleanCustomCache(String filePath) {
        deleteFilesByDirectory(new File(filePath));
    }

    /**
     * 清除本应用所有的数据
     */
    public static void cleanApplicationData(Context context, String... filepath) {
        cleanInternalCache(context);
        cleanFiles(context);
        cleanAppWebView(context);
        cleanAcache(context);
        cleanExternalCache(context);
        if (isSDCardExsit()) {//清除缓存在SD卡的图片文件
            for (String filePath : filepath) {
                cleanCustomCache(filePath);
            }
        }
    }

    /**
     * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理
     */
    private static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory())
            for (File item : directory.listFiles()) {
                item.delete();
            }
    }


    /**
     * 判断SD卡是否存在（可读可写） 存在进行操作 反之不操作
     */
    public static boolean isSDCardExsit() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return true;
        return false;
    }

    /**
     * 递归删除文件 及 文件夹
     */
    public static void RecursionDeleteFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                RecursionDeleteFile(f);
            }
            file.delete();
        }
    }
}