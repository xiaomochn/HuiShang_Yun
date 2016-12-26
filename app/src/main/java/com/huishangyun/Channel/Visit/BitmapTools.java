package com.huishangyun.Channel.Visit;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Environment;
import android.os.Handler;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.widget.ImageView;

import com.huishangyun.Util.L;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.View.MyProgressDialog;
import com.huishangyun.model.Constant;

/**
 * 图片处理工具类（包括图片剪切、sd卡读取图片、resources目录下读取图片，网络下载图片保存的sd卡，并包含所有对图片的自定义大小处理）
 * 对于图片浏览，完美解决内存溢出的问题
 *
 * @author xsl
 */
public class BitmapTools {
    private MyProgressDialog progressDialog;//进度条
    private final String TAG = "";
    private ExecutorService executorService = Executors.newFixedThreadPool(5);
    Context context;

    public BitmapTools() {
    }

    /**
     * 从resource资源文件获取图片进行处理
     *
     * @param resources 资源文件
     * @param resId     解码位图的id
     * @param reqWith   指定输出位图的宽度
     * @param reqHeight 指定输出位图的高度
     */
    public Bitmap decodeBitmap(Resources resources, int resId,
                               int reqWith, int reqHeight) {
        // 对位图进行解码的参数设置
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 在对位图进行解码的过程中，避免申请内存空间
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, resId, options);
        // 对图片进行一定比例的压缩处理
        options.inSampleSize = computeSampleSize(options, -1,
                reqWith * reqHeight);
        options.inJustDecodeBounds = false;// 真正输出位图
        return BitmapFactory.decodeResource(resources, resId, options);
    }

    // /**********************从SD卡读取文件***************************************//

    /**
     * 从sd卡获得图片进行处理
     *
     * @paramimageName 图片文件名 path 文件sd卡完整路径
     * @param reqWith   要求处理后的宽
     * @param reqHeight 高
     * @return
     */
    public static Bitmap getBitmap(String path, int reqWith, int reqHeight) {
        // 判断sd卡是否挂载状态
        String sdState = Environment.getExternalStorageState();
        // 读取sd卡根目录
        // String path = Environment.getExternalStorageDirectory().toString();
        Bitmap bitmap = null;
        File imagePic;
        // 假如sd可读写
        if (sdState.equals(Environment.MEDIA_MOUNTED)) {
            imagePic = new File(path);
            if (imagePic.exists()) {
                try {
                    // 对位图进行解码的参数设置
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    // 在对位图进行解码的过程中，避免申请内存空间
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(new FileInputStream(imagePic),
                            null, options);
                    // 对图片进行一定比例的压缩处理
//					options.inSampleSize = calculateInSimpleSize(options,
//							reqWith, reqHeight);

                    options.inSampleSize = computeSampleSize(options, -1, reqWith * reqHeight);
                    options.inJustDecodeBounds = false;// 真正输出位图
//					Log.e(TAG, "======>options:" + options.inSampleSize);
//					Log.e(TAG, "======>options1111111:" + options);

                    bitmap = BitmapFactory.decodeStream(new FileInputStream(
                            imagePic), null, options);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

    /**
     * 剪切图片中间自定义尺寸
     *
     * @param bitmap 原始图片
     * @param width  剪切后要求的宽
     * @param Height 高
     * @return
     */
    public static Bitmap cutBitmap(Bitmap bitmap, int width, int Height) {
        Bitmap bitmap1 = null;
        // 获取图片的宽，高

        int widthOrg = bitmap.getWidth();
        int heightOrg = bitmap.getHeight();

        int xTopLeft;
        int yTopLeft;
        //判断图图片是否小于100*100
        if (widthOrg < width || heightOrg < Height) {
            //三目运算取较小的尺寸
            int wh = (heightOrg > widthOrg) ? widthOrg : heightOrg;
            xTopLeft = (widthOrg - wh) / 2;
            yTopLeft = (heightOrg - wh) / 2;
            // 按要求剪切图片中间自定义尺寸
            bitmap1 = Bitmap.createBitmap(bitmap, xTopLeft, yTopLeft, wh,
                    wh);
        } else {
            // 从图中截取正中间的正方形部分。xTopLeft、yTopLeft分别为图片截取的起始X、Y
            xTopLeft = (widthOrg - width) / 2;
            yTopLeft = (heightOrg - Height) / 2;
            // 按要求剪切图片中间自定义尺寸
            bitmap1 = Bitmap.createBitmap(bitmap, xTopLeft, yTopLeft, width,
                    Height);
        }

        return bitmap1;
    }


    /**
     * 从网络下载图片并保存到sd卡，sd卡有，就从sd读取并对要显示的图片进行处理
     *
     * @param imageView 图片控件名称BitmapTools.downLoadImage(holder.person_img, 100, 100,true);
     * @param width     要求处理后的宽
     * @param Height    高
     * @param bool      true为需要剪切，flose为不需要剪切
     * @return
     */
    public void downLoadImage(ImageView imageView, int width, int Height, boolean bool, Context context) {

        progressDialog = new MyProgressDialog();
        progressDialog.setMessage("正在加载中...");
        Bitmap bitmap = null, bitmap1 = null;
        // 判断sd卡是否挂载状态
        String sdState = Environment.getExternalStorageState();
        String path = (String) imageView.getTag();

        // // 读取sd卡根目录
        // String path = Environment.getExternalStorageDirectory().toString();

        // 判断sd卡是否可读写
        if (sdState.equals(Environment.MEDIA_MOUNTED)) {
            File imagePic = new File(Environment.getExternalStorageDirectory().toString() + Constant.huishang_img+ path.hashCode() + ".jpg");
            // 判断文件在sd卡是否存在
            try {
                if (imagePic.exists()) {
                    //从sd卡取，图片大会内存溢出

                    if (bool) {
                        bitmap = getBitmap(Environment.getExternalStorageDirectory().toString()
                                + Constant.huishang_img+ path.hashCode() + ".jpg", 480, 800);
                        Log.e("TAGS","bitmap图片1"+bitmap);
                            //将图片处理剪切成小图
                        bitmap1 = cutBitmap(bitmap, width, Height);
//						L.e("图片存在");
                    } else {
                        bitmap = getBitmap(Environment.getExternalStorageDirectory().toString()
                                + Constant.huishang_img + path.hashCode() + ".jpg", width, Height);
                        Log.e("TAGS","bitmap图片2"+bitmap);
                        bitmap1 = bitmap;
                    }


                    if (path.equals(imageView.getTag())) {
                        imageView.setImageBitmap(bitmap1);
                    }
                    return;
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
//			L.e("图片不存在，启动下载并保存到sd卡");
            // 在sd中不存在，启动下载并存入sd卡相应目录
            if (bool) {
                executorService.submit(new downLoadBitmap(imageView, width, Height, bool));
            } else {
                executorService.submit(new downLoadBitmap(imageView, width, Height, bool));
                progressDialog.show(((BaseActivity) context).getSupportFragmentManager(), "mydialog");

            }

        }

    }

    /**
     * 下载图片并按要求等比例缩放
     *
     * @param imageView
     * @param width
     * @param Height
     * @param bool      传true
     */
    public void downImage(ImageView imageView, int width, int Height, boolean bool) {
        new downLoadBitmap(imageView, width, Height, bool);
    }

    /**
     * 下载图片
     *
     * @author Administrator
     */
    private class downLoadBitmap implements Runnable {
        private ImageView imageView;
        private String path;
        private Bitmap bitmap = null;
        private byte[] data = null;
        private int width;
        private int Height;
        private boolean bool;

        public downLoadBitmap(ImageView imageView, int width, int Height, boolean bool) {
            this.imageView = imageView;
            path = (String) imageView.getTag();
            this.width = width;
            this.Height = Height;
            this.bool = bool;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            data = sendPost(path);// 下载图片
            L.e("data:" + data.toString());
            // 将原图保存到sd卡
            boolean flag = savaFillScard(data, path);
            if (flag && data != null) {
                // 保存成功则对图片进行处理
                bitmap = byteBitmap(data, width, Height);

                if (path.equals(imageView.getTag())) {

                    handler.sendEmptyMessage(0);

                }
            }
        }


        /**
         * 线程处理ui改变
         */
        public Handler handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case 0:
                        try {//有的手机在这一步会报错，加一个try-catch，因为报错对程序运行其实没有影响。
                            if (bool) {
                                imageView.setImageBitmap(cutBitmap(bitmap, width, Height));
                            } else {
                                progressDialog.dismiss();
                                imageView.setImageBitmap(bitmap);
                            }
                        } catch (Exception e) {
                            // TODO: handle exception
                            e.printStackTrace();
                        }

                        break;

                    default:
                        break;
                }

            }

            ;
        };

    }


    /**
     * 下载文件
     *
     * @param path
     * @return
     */
    private byte[] sendPost(String path) {
        /*HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(path);
		HttpResponse response = null;
		try {
			response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == 200) {
				return EntityUtils.toByteArray(response.getEntity());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}*/
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


    /**
     * 向sd卡存储数据
     *
     * @param data 保存数据
     * @parampath 保存文件夹路径
     */
    public boolean savaFillScard(byte[] data, String Paths) {
        boolean flag = false;
        // 数据I/O流
        FileOutputStream fileOutputStream = null;
        // 读取sd卡根目录
        String path = Environment.getExternalStorageDirectory().toString();

        File file = new File(path + "/HSY_Yun/img/" + Paths.hashCode() + ".jpg");//存放文件的路径

        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            try {
                fileOutputStream = new FileOutputStream(file);
                //写入数据
                fileOutputStream.write(data);
                flag = true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            flag = false;
        }

        return flag;

    }

    /**
     * 对数据流进行处理
     *
     * @param data
     * @param reqWith
     * @param reqHeight
     * @return
     */
    public Bitmap byteBitmap(byte[] data, int reqWith, int reqHeight) {
        // 对位图进行解码的参数设置
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 在对位图进行解码的过程中，避免申请内存空间
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        // 对图片进行一定比例的压缩处理
        options.inSampleSize = computeSampleSize(options, -1,
                reqWith * reqHeight);
        options.inJustDecodeBounds = false;// 真正输出位图
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }


    /**
     * 加水印 也可以加文字
     *
     * @param src       要加水印图片
     * @param watermark 水印图片
     * @param title     要加的文字
     * @return
     */
    public Bitmap watermarkBitmap(Bitmap src, Bitmap watermark,
                                  String title) {
        if (src == null) {
            return null;
        }
        int w = src.getWidth();
        int h = src.getHeight();
        //需要处理图片太大造成的内存超过的问题,这里我的图片很小所以不写相应代码了        
        Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
        Canvas cv = new Canvas(newb);
        cv.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入src    
        Paint paint = new Paint();
        //加入图片
        if (watermark != null) {
            int ww = watermark.getWidth();
            int wh = watermark.getHeight();
            paint.setAlpha(50);
            cv.drawBitmap(watermark, w - ww + 5, h - wh + 5, paint);// 在src的右下角画入水印            
        }
        //加入文字
        if (title != null) {
            String familyName = "宋体";
            Typeface font = Typeface.create(familyName, Typeface.BOLD);
            TextPaint textPaint = new TextPaint();
            textPaint.setColor(Color.RED);
            textPaint.setTypeface(font);
            textPaint.setTextSize(22);
            //这里是自动换行的
            StaticLayout layout = new StaticLayout(title, textPaint, w, Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
            layout.draw(cv);
            //文字就加左上角算了
            //cv.drawText(title,0,40,paint); 
        }
        cv.save(Canvas.ALL_SAVE_FLAG);// 保存
        cv.restore();// 存储
        return newb;
    }

    /**
     * 获取圆角图片
     *
     * @param bitmap
     * @param roundPx
     * @return
     */
    public Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Bitmap output = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, w, h);
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }


    /**
     * Bitmap → byte[]转换
     *
     * @param bm
     * @return
     */
    public byte[] BitmapBytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);//100%的质量
        return baos.toByteArray();
    }


    /************动态计算options.inSampleSize****************************/
    /**
     * 这种计算方法可以解决内存溢出的问题
     *
     * @param options
     * @param minSideLength
     * @param maxNumOfPixels
     * @return
     */
    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 :
                (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 :
                (int) Math.min(Math.floor(w / minSideLength),
                        Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }


        if ((maxNumOfPixels == -1) &&
                (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    /******************End**************************************/

}
