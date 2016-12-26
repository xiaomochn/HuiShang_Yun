package com.huishangyun.Util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.huishangyun.Channel.Clues.ClueCustomToast;
import com.huishangyun.View.MyProgressDialog;
import com.huishangyun.View.RoundAngleImageView;
import com.huishangyun.model.Constant;
import com.huishangyun.yun.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;


/**
 * 文件下载帮助类
 *
 * @author Pan
 */
public class FileTools {
    private static ExecutorService executorService = Executors.newFixedThreadPool(5);
    private static MyProgressDialog progressDialog;
    private static MediaPlayer mPlayer;

    public static void decodeFile(String filePath, Context mContext) {
        String sdState = Environment.getExternalStorageState();
        File file = null;
        if (sdState.equals(Environment.MEDIA_MOUNTED)) {
            /* 取得扩展名 */
            String end = filePath.substring(filePath.lastIndexOf(".")
                    + 1, filePath.length()).toLowerCase();
            L.e("扩展名为：" + end);
            file = new File(Constant.SAVE_IMG_PATH + File.separator + filePath.hashCode() + "." + end);
            try {
                if (file.exists()) {
                    OpenFileUtil.openFile(file, mContext);
                    return;
                }
            } catch (Exception e) {
                // TODO: handle exception
                L.e("文件不存在");
                e.printStackTrace();
            }
            progressDialog = new MyProgressDialog();
            progressDialog.setMessage("正在下载文件");
            progressDialog.show(((FragmentActivity) mContext).getSupportFragmentManager(), "dialogTag");
            executorService.submit(new downLoadFile(filePath, mContext));

        }

    }

    /**
     * 下载录音文件
     *
     * @param filePath
     * @param isRight
     */
    public static void decodeRecording(String filePath, final boolean isRight, LinearLayout layout, final ImageView imageView) {
        String sdState = Environment.getExternalStorageState();
        final File file;
        if (sdState.equals(Environment.MEDIA_MOUNTED)) {
			/* 取得扩展名 */
            String end = filePath.substring(filePath.lastIndexOf(".")
                    + 1, filePath.length()).toLowerCase();
            L.e("扩展名为：" + end);
            file = new File(Constant.SAVE_IMG_PATH + File.separator + filePath.hashCode() + "." + end);
            try {
                if (file.exists()) {
                    //OpenFileUtil.openFile(file, mContext);
                    layout.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            startPlaying(file.getAbsolutePath(), imageView);
                        }
                    });
                    return;
                }
            } catch (Exception e) {
                // TODO: handle exception
                L.e("文件不存在");
                e.printStackTrace();
            }
            executorService.submit(new downLoadPlay(filePath, imageView, layout));
        }
    }

    /**
     * 下载图片并添加点击事件
     *
     * @param filePath
     * @param imageView
     */
    public static void decodeImage(String filePath, ImageView imageView, final Context mContext) {
        String sdState = Environment.getExternalStorageState();
        final File file;
        if (sdState.equals(Environment.MEDIA_MOUNTED)) {
			/* 取得扩展名 */
            String end = filePath.substring(filePath.lastIndexOf(".")
                    + 1, filePath.length()).toLowerCase();
            file = new File(Constant.SAVE_IMG_PATH + File.separator + filePath.hashCode() + "." + end);
            try {
                if (file.exists()) {
                    //OpenFileUtil.openFile(file, mContext);
                    //imageView.setImageBitmap(FileUtils.chatBitmap(file.getAbsolutePath()));
                    imageView.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            // 查看图库
                            Intent intent = new Intent();
                            intent.setAction(android.content.Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.fromFile(file), "image/*");
                            mContext.startActivity(intent);
                        }
                    });
                    return;
                }
            } catch (Exception e) {
                // TODO: handle exception
                L.e("文件不存在");
                e.printStackTrace();
            }
            executorService.submit(new downLoadImage(filePath, imageView, mContext));
        }
    }

    /**
     * 下载显示广告页
     * @param filePath
     * @param imageView
     */
    public static void decodeImage2(String filePath, ImageView imageView) {
        String sdState = Environment.getExternalStorageState();
        final File file;
        if (sdState.equals(Environment.MEDIA_MOUNTED)) {
			/* 取得扩展名 */
            String end = filePath.substring(filePath.lastIndexOf(".")
                    + 1, filePath.length()).toLowerCase();
            file = new File(Constant.SAVE_IMG_PATH + File.separator + filePath.hashCode() + "." + end);
            try {
                if (file.exists()) {
                    //OpenFileUtil.openFile(file, mContext);
                    if (imageView != null)
                        imageView.setImageBitmap(BitmapFactory.decodeFile(Constant.SAVE_IMG_PATH + File.separator + filePath.hashCode() + "." + end));
                    return;
                }
            } catch (Exception e) {
                // TODO: handle exception
                L.e("文件不存在");
                e.printStackTrace();
            }
            executorService.submit(new downLoadImage2(filePath, imageView));
        }
    }

    public static void decodePhoto(String filePath, RoundAngleImageView imageView) {
        String sdState = Environment.getExternalStorageState();
        final File file;
        if (sdState.equals(Environment.MEDIA_MOUNTED)) {
			/* 取得扩展名 */
            String end = filePath.substring(filePath.lastIndexOf(".")
                    + 1, filePath.length()).toLowerCase();
            file = new File(Constant.SAVE_IMG_PATH + File.separator + filePath.hashCode() + "." + end);
            L.e("filePath.hashCode() = " + filePath.hashCode());
            try {
                if (file.exists()) {
                    //OpenFileUtil.openFile(file, mContext);
                    imageView.setImageBitmap(FileUtils.chatBitmap(file.getAbsolutePath()));

                    return;
                }
            } catch (Exception e) {
                // TODO: handle exception
                L.e("文件不存在");
                e.printStackTrace();
            }
            executorService.submit(new downLoadPhoto(filePath, imageView));
        }
    }

    public static void decodePhoto(String filePath, ImageView imageView) {
        String sdState = Environment.getExternalStorageState();
        final File file;
        if (sdState.equals(Environment.MEDIA_MOUNTED)) {
			/* 取得扩展名 */
            String end = filePath.substring(filePath.lastIndexOf(".")
                    + 1, filePath.length()).toLowerCase();
            file = new File(Constant.SAVE_IMG_PATH + File.separator + filePath.hashCode() + "." + end);
            L.e("filePath.hashCode() = " + filePath.hashCode());
            try {
                if (file.exists()) {
                    //OpenFileUtil.openFile(file, mContext);
                    imageView.setImageBitmap(FileUtils.chatBitmap(file.getAbsolutePath()));

                    return;
                }
            } catch (Exception e) {
                // TODO: handle exception
                L.e("文件不存在");
                e.printStackTrace();
            }
            executorService.submit(new downLoadPhoto2(filePath, imageView));
        }
    }


    /**
     * 播放录音文件
     */
    public static void startPlaying(String FilePath, final ImageView imageView) {
        try {
            //获得动画
			/*SpannedString spanStr = (SpannedString) textView.getText();
			ImageSpan[] span = spanStr.getSpans(0, spanStr.length(), ImageSpan.class);*/
            FileTools.stopPlaying(null, null);
            final AnimationDrawable aDrawable = (AnimationDrawable) imageView.getBackground();
            aDrawable.setOneShot(false);
            aDrawable.start();
            mPlayer = new MediaPlayer();
            mPlayer.setDataSource(FilePath);
            mPlayer.prepare();
            mPlayer.start();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                    stopPlaying(aDrawable, imageView);
                }
            });
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }// 获取绝对路径来播放音频
    }

    /**
     * 停止播放
     */
    public static void stopPlaying(AnimationDrawable aDrawable, ImageView imageView) {
        if (aDrawable != null && imageView != null) {
            aDrawable.stop();
            imageView.setBackgroundDrawable(null);
            if (imageView.getId() == R.id.chat_left_player) {
                imageView.setBackgroundResource(R.drawable.player_left);
            } else if (imageView.getId() == R.id.chat_right_player) {
                imageView.setBackgroundResource(R.drawable.player_right);
            } else {
                imageView.setBackgroundResource(R.drawable.player_left);
            }
        }
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    /**
     * 下载文件
     *
     * @author Pan
     */
    public static class downLoadFile implements Runnable {
        private String filePath;
        private Context mContext;
        private byte[] data = null;

        public downLoadFile(String filePath, Context mContext) {
            this.filePath = filePath;
            this.mContext = mContext;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            data = sendPost(filePath);// 下载文件
            boolean flag = savaFillScard(data, filePath, mContext);
        }

    }

    public static class downLoadPlay implements Runnable {
        private String filePath;
        private byte[] data = null;
        private ImageView imageView;
        private LinearLayout layout;

        public downLoadPlay(String filePath, ImageView imageView, LinearLayout layout) {
            this.filePath = filePath;
            this.imageView = imageView;
            this.layout = layout;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            data = sendPost(filePath);// 下载文件
            boolean flag = savaFillScard(data, filePath, imageView, layout);
        }

    }

    public static class downLoadPhoto implements Runnable {
        private String filePath;
        private RoundAngleImageView imageView;
        private byte[] data = null;

        public downLoadPhoto(String filePath, RoundAngleImageView imageView) {
            this.filePath = filePath;
            this.imageView = imageView;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            data = sendPost(filePath);// 下载文件
            boolean flag = savaImagePhoto(data, filePath, imageView);
        }
    }


    public static class downLoadPhoto2 implements Runnable {
        private String filePath;
        private ImageView imageView;
        private byte[] data = null;

        public downLoadPhoto2(String filePath, ImageView imageView) {
            this.filePath = filePath;
            this.imageView = imageView;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            data = sendPost(filePath);// 下载文件
            boolean flag = savaImagePhoto(data, filePath, imageView);
        }
    }

    public static class downLoadImage implements Runnable {
        private String filePath;
        private byte[] data = null;
        private ImageView imageView;
        private Context mContext;

        public downLoadImage(String filePath, ImageView imageView, Context mContext) {
            this.filePath = filePath;
            this.imageView = imageView;
            this.mContext = mContext;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            data = sendPost(filePath);// 下载文件
            boolean flag = savaImageScard(data, filePath, mContext, imageView);
        }

    }

    public static class downLoadImage2 implements Runnable {
        private String filePath;
        private byte[] data = null;
        private ImageView imageView;

        public downLoadImage2(String filePath, ImageView imageView) {
            this.filePath = filePath;
            this.imageView = imageView;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            data = sendPost(filePath);// 下载文件
            boolean flag = savaImageScard(data, filePath, imageView);
        }

    }


    /**
     * 保存文件
     *
     * @param data
     * @param Paths
     * @param mContext
     * @return
     */
    public static boolean savaFillScard(byte[] data, String Paths, Context mContext) {
        boolean flag = false;
        // 数据I/O流
        FileOutputStream fileOutputStream = null;
        // 读取sd卡根目录
        String path = Environment.getExternalStorageDirectory().toString();


        String end = Paths.substring(Paths.lastIndexOf(".")
                + 1, Paths.length()).toLowerCase();

        File file = new File(Constant.SAVE_IMG_PATH + File.separator + Paths.hashCode() + "." + end);//存放文件的路径

        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            try {
                fileOutputStream = new FileOutputStream(file);
                //写入数据
                fileOutputStream.write(data);
                flag = true;
            } catch (FileNotFoundException e) {
                file.delete();
                e.printStackTrace();
                Message msg = new Message();
                msg.what = HanderUtil.case2;
                msg.obj = mContext;
                mHandler.sendMessage(msg);
                return false;
            } catch (Exception e) {
                file.delete();
                e.printStackTrace();
                Message msg = new Message();
                msg.what = HanderUtil.case2;
                msg.obj = mContext;
                mHandler.sendMessage(msg);
                return false;
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Message msg = new Message();
                        msg.what = HanderUtil.case2;
                        msg.obj = mContext;
                        mHandler.sendMessage(msg);
                        return false;
                    }
                }
            }
            Message msg = new Message();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("file", new File(Constant.SAVE_IMG_PATH + File.separator + Paths.hashCode() + "." + end));
            map.put("context", mContext);
            msg.what = HanderUtil.case1;
            msg.obj = map;
            mHandler.sendMessage(msg);
        } else {
            flag = false;
            Message msg = new Message();
            msg.what = HanderUtil.case2;
            msg.obj = mContext;
            mHandler.sendMessage(msg);
        }

        return flag;
    }

    /**
     * 保存文件
     *
     * @param data
     * @param Paths
     * @return
     */
    public static boolean savaFillScard(byte[] data, String Paths, ImageView imageView, LinearLayout layout) {
        boolean flag = false;
        // 数据I/O流
        FileOutputStream fileOutputStream = null;
        // 读取sd卡根目录
        String path = Environment.getExternalStorageDirectory().toString();


        String end = Paths.substring(Paths.lastIndexOf(".")
                + 1, Paths.length()).toLowerCase();

        File file = new File(Constant.SAVE_IMG_PATH + File.separator + Paths.hashCode() + "." + end);//存放文件的路径

        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            try {
                fileOutputStream = new FileOutputStream(file);
                //写入数据
                fileOutputStream.write(data);
                flag = true;
            } catch (FileNotFoundException e) {
                file.delete();
                e.printStackTrace();
				/*Message msg = new Message();
				msg.what = HanderUtil.case2;
				msg.obj = mContext;
				mHandler.sendMessage(msg);*/
                return false;
            } catch (Exception e) {
                file.delete();
                e.printStackTrace();
				/*Message msg = new Message();
				msg.what = HanderUtil.case2;
				msg.obj = mContext;
				mHandler.sendMessage(msg);*/
                return false;
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
						/*Message msg = new Message();
						msg.what = HanderUtil.case2;
						msg.obj = mContext;
						mHandler.sendMessage(msg);*/
                        return false;
                    }
                }
            }
            Message msg = new Message();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("file", new File(Constant.SAVE_IMG_PATH + File.separator + Paths.hashCode() + "." + end));
            map.put("imageview", imageView);
            map.put("layout", layout);
            msg.what = HanderUtil.case3;
            msg.obj = map;
            mHandler.sendMessage(msg);
        } else {
            flag = false;
			/*Message msg = new Message();
			msg.what = HanderUtil.case2;
			msg.obj = mContext;
			mHandler.sendMessage(msg);*/
        }

        return flag;
    }

    public static boolean savaImagePhoto(byte[] data, String Paths, RoundAngleImageView imageView) {
        boolean flag = false;
        // 数据I/O流
        FileOutputStream fileOutputStream = null;
        // 读取sd卡根目录
        String path = Environment.getExternalStorageDirectory().toString();


        String end = Paths.substring(Paths.lastIndexOf(".")
                + 1, Paths.length()).toLowerCase();

        File file = new File(Constant.SAVE_IMG_PATH + File.separator + Paths.hashCode() + "." + end);//存放文件的路径

        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            try {
                fileOutputStream = new FileOutputStream(file);
                //写入数据
                fileOutputStream.write(data);
                flag = true;
            } catch (FileNotFoundException e) {
                file.delete();
                e.printStackTrace();
				/*Message msg = new Message();
				msg.what = HanderUtil.case2;
				msg.obj = mContext;
				mHandler.sendMessage(msg);*/
                return false;
            } catch (Exception e) {
                file.delete();
                e.printStackTrace();
				/*Message msg = new Message();
				msg.what = HanderUtil.case2;
				msg.obj = mContext;
				mHandler.sendMessage(msg);*/
                return false;
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
						/*Message msg = new Message();
						msg.what = HanderUtil.case2;
						msg.obj = mContext;
						mHandler.sendMessage(msg);*/
                        return false;
                    }
                }
            }
            Message msg = new Message();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("file", new File(Constant.SAVE_IMG_PATH + File.separator + Paths.hashCode() + "." + end));
            map.put("imageview", imageView);
            msg.what = HanderUtil.case5;
            msg.obj = map;
            mHandler.sendMessage(msg);
        } else {
            flag = false;
			/*Message msg = new Message();
			msg.what = HanderUtil.case2;
			msg.obj = mContext;
			mHandler.sendMessage(msg);*/
        }

        return flag;
    }


    public static boolean savaImagePhoto(byte[] data, String Paths, ImageView imageView) {
        boolean flag = false;
        // 数据I/O流
        FileOutputStream fileOutputStream = null;
        // 读取sd卡根目录
        String path = Environment.getExternalStorageDirectory().toString();


        String end = Paths.substring(Paths.lastIndexOf(".")
                + 1, Paths.length()).toLowerCase();

        File file = new File(Constant.SAVE_IMG_PATH + File.separator + Paths.hashCode() + "." + end);//存放文件的路径

        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            try {
                fileOutputStream = new FileOutputStream(file);
                //写入数据
                fileOutputStream.write(data);
                flag = true;
            } catch (FileNotFoundException e) {
                file.delete();
                e.printStackTrace();
				/*Message msg = new Message();
				msg.what = HanderUtil.case2;
				msg.obj = mContext;
				mHandler.sendMessage(msg);*/
                return false;
            } catch (Exception e) {
                file.delete();
                e.printStackTrace();
				/*Message msg = new Message();
				msg.what = HanderUtil.case2;
				msg.obj = mContext;
				mHandler.sendMessage(msg);*/
                return false;
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
						/*Message msg = new Message();
						msg.what = HanderUtil.case2;
						msg.obj = mContext;
						mHandler.sendMessage(msg);*/
                        return false;
                    }
                }
            }
            Message msg = new Message();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("file", new File(Constant.SAVE_IMG_PATH + File.separator + Paths.hashCode() + "." + end));
            map.put("imageview", imageView);
            msg.what = HanderUtil.case6;
            msg.obj = map;
            mHandler.sendMessage(msg);
        } else {
            flag = false;
			/*Message msg = new Message();
			msg.what = HanderUtil.case2;
			msg.obj = mContext;
			mHandler.sendMessage(msg);*/
        }

        return flag;
    }


    /**
     * 保存文件
     *
     * @param data
     * @param Paths
     * @param mContext
     * @return
     */
    public static boolean savaImageScard(byte[] data, String Paths, Context mContext, ImageView imageView) {
        boolean flag = false;
        // 数据I/O流
        FileOutputStream fileOutputStream = null;
        // 读取sd卡根目录
        String path = Environment.getExternalStorageDirectory().toString();


        String end = Paths.substring(Paths.lastIndexOf(".")
                + 1, Paths.length()).toLowerCase();

        File file = new File(Constant.SAVE_IMG_PATH + File.separator + Paths.hashCode() + "." + end);//存放文件的路径

        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            try {
                fileOutputStream = new FileOutputStream(file);
                //写入数据
                fileOutputStream.write(data);
                flag = true;
            } catch (FileNotFoundException e) {
                file.delete();
                e.printStackTrace();
				/*Message msg = new Message();
				msg.what = HanderUtil.case2;
				msg.obj = mContext;
				mHandler.sendMessage(msg);*/
                return false;
            } catch (Exception e) {
                file.delete();
                e.printStackTrace();
				/*Message msg = new Message();
				msg.what = HanderUtil.case2;
				msg.obj = mContext;
				mHandler.sendMessage(msg);*/
                return false;
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
						/*Message msg = new Message();
						msg.what = HanderUtil.case2;
						msg.obj = mContext;
						mHandler.sendMessage(msg);*/
                        return false;
                    }
                }
            }
            Message msg = new Message();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("file", new File(Constant.SAVE_IMG_PATH + File.separator + Paths.hashCode() + "." + end));
            map.put("context", mContext);
            map.put("imageview", imageView);
            msg.what = HanderUtil.case4;
            msg.obj = map;
            mHandler.sendMessage(msg);
        } else {
            flag = false;
			/*Message msg = new Message();
			msg.what = HanderUtil.case2;
			msg.obj = mContext;
			mHandler.sendMessage(msg);*/
        }

        return flag;
    }

    /**
     * 保存文件
     *
     * @param data
     * @param Paths
     * @return
     */
    public static boolean savaImageScard(byte[] data, String Paths, ImageView imageView) {
        boolean flag = false;
        // 数据I/O流
        FileOutputStream fileOutputStream = null;
        // 读取sd卡根目录
        String path = Environment.getExternalStorageDirectory().toString();


        String end = Paths.substring(Paths.lastIndexOf(".")
                + 1, Paths.length()).toLowerCase();

        File file = new File(Constant.SAVE_IMG_PATH + File.separator + Paths.hashCode() + "." + end);//存放文件的路径

        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            try {
                fileOutputStream = new FileOutputStream(file);
                //写入数据
                fileOutputStream.write(data);
                flag = true;
            } catch (FileNotFoundException e) {
                file.delete();
                e.printStackTrace();
                return false;
            } catch (Exception e) {
                file.delete();
                e.printStackTrace();
                return false;
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            }
            Message msg = new Message();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("file", new File(Constant.SAVE_IMG_PATH + File.separator + Paths.hashCode() + "." + end));
            map.put("imageview", imageView);
            msg.what = HanderUtil.case7;
            msg.obj = map;
            mHandler.sendMessage(msg);
        } else {
            flag = false;
			/*Message msg = new Message();
			msg.what = HanderUtil.case2;
			msg.obj = mContext;
			mHandler.sendMessage(msg);*/
        }

        return flag;
    }


    private static Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case HanderUtil.case1:
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    Map<String, Object> map = (Map<String, Object>) msg.obj;
                    if ((Context) map.get("context") != null) {
                        /**
                         * 调用系统打开,打开文件
                         */
                        OpenFileUtil.openFile((File) map.get("file"), (Context) map.get("context"));
                    }
                    break;

                case HanderUtil.case2:
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    ClueCustomToast.showToast((Context) msg.obj, R.drawable.toast_warn, "文件下载失败");


                    break;

                case HanderUtil.case3:
                    final Map<String, Object> map1 = (Map<String, Object>) msg.obj;
                    if ((LinearLayout) map1.get("layout") != null) {
                        ((LinearLayout) map1.get("layout")).setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                startPlaying(((File) map1.get("file")).getAbsolutePath(), (ImageView) map1.get("imageview"));
                            }
                        });
                    }
                    break;
                case HanderUtil.case4:
                    final Map<String, Object> map4 = (Map<String, Object>) msg.obj;

                    if ((Context) map4.get("context") != null) {
                        final Context mContext = (Context) map4.get("context");
                        ImageView imageview = (ImageView) map4.get("imageview");
                        final File file = (File) map4.get("file");
                        //imageview.setImageBitmap(FileUtils.chatBitmap(file.getAbsolutePath()));
                        imageview.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                // 查看图库
                                Intent intent = new Intent();
                                intent.setAction(android.content.Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.fromFile(file), "image/*");
                                mContext.startActivity(intent);
                            }
                        });
                    }

                    break;

                case HanderUtil.case5:
                    final Map<String, Object> map5 = (Map<String, Object>) msg.obj;

                    if ((RoundAngleImageView) map5.get("imageview") != null) {
                        RoundAngleImageView imageview = (RoundAngleImageView) map5.get("imageview");
                        final File file = (File) map5.get("file");
                        imageview.setImageBitmap(new BitmapFactory().decodeFile(file.getAbsolutePath()));
                    }
                    break;

                case HanderUtil.case6:
                    final Map<String, Object> map6 = (Map<String, Object>) msg.obj;

                    if ((ImageView) map6.get("imageview") != null) {
                        ImageView imageview = (ImageView) map6.get("imageview");
                        final File file = (File) map6.get("file");
                        imageview.setImageBitmap(new BitmapFactory().decodeFile(file.getAbsolutePath()));
                    }
                    break;

                case HanderUtil.case7:
                    final Map<String, Object> map7 = (Map<String, Object>) msg.obj;
                    if ((ImageView) map7.get("imageview") != null) {
                        ImageView imageview = (ImageView) map7.get("imageview");
                        final File file = (File) map7.get("file");
                        imageview.setImageBitmap(new BitmapFactory().decodeFile(file.getAbsolutePath()));
                    }
                    break;

                default:
                    break;
            }
        }

        ;
    };

    /**
     * 下载文件
     *
     * @param path
     * @return
     */
    private static byte[] sendPost(String path) {
        try {
            URL imageUrl = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) imageUrl
                    .openConnection();
            conn.setConnectTimeout(30000);

            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is = conn.getInputStream();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int n = 0;
            while (-1 != (n = is.read(buffer))) {
                output.write(buffer, 0, n);
            }
            return output.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
