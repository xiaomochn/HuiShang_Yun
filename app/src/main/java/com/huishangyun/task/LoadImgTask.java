package com.huishangyun.task;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.TextView;

public class LoadImgTask extends AsyncTask <String, Integer, Bitmap>{
	private String oldPath;
	private int bitmapMaxWidth;
	private TextView textView;
	private Context context;
	public ImageCallback imageCallback;
	
	public LoadImgTask(String oldPath,int bitmapMaxWidth,TextView textView,Context context,ImageCallback imageCallback){
		this.oldPath = oldPath;
		this.bitmapMaxWidth = bitmapMaxWidth;
		this.textView = textView;
		this.context = context;
		this.imageCallback = imageCallback;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Bitmap doInBackground(String... arg0) {
		// TODO Auto-generated method stub
		try {
			return getThumbUploadPath(oldPath, bitmapMaxWidth);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
	}
	
	@Override
	protected void onPostExecute(Bitmap result) {
		//回调到接口
		imageCallback.HaveImageCallback(result, textView);
	}
	
	
	/**
	 * 压缩图片
	 * 
	 * @param oldPath
	 * @param bitmapMaxWidth
	 * @return Bitmap
	 * @throws Exception
	 */
	private Bitmap getThumbUploadPath(String oldPath, int bitmapMaxWidth)
			throws Exception {

		BitmapFactory.Options options = new BitmapFactory.Options();

		options.inJustDecodeBounds = true;

		BitmapFactory.decodeFile(oldPath, options);

		int height = options.outHeight;

		int width = options.outWidth;

		int reqHeight = 0;

		int reqWidth = bitmapMaxWidth;

		reqHeight = (reqWidth * height) / width;

		// 在内存中创建bitmap对象，这个对象按照缩放大小创建的

		options.inSampleSize = calculateInSampleSize(options,
				bitmapMaxWidth, reqHeight);

		// System.out.println("calculateInSampleSize(options, 480, 800);==="

		// + calculateInSampleSize(options, 480, 800));

		options.inJustDecodeBounds = false;

		Bitmap bitmap = BitmapFactory.decodeFile(oldPath, options);

		// Log.e("asdasdas",
		// "reqWidth->"+reqWidth+"---reqHeight->"+reqHeight);

		Bitmap bbb = compressImage(Bitmap.createScaledBitmap(bitmap,
				bitmapMaxWidth, reqHeight, false));

	//	String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")

	//	.format(new Date());
		String newName = "Small" + new File(oldPath).getName();
		//return BitmapUtils.saveImg(bbb, newName);
		return bbb;

	}

	private int calculateInSampleSize(BitmapFactory.Options options,

	int reqWidth, int reqHeight) {

		// Raw height and width of image

		final int height = options.outHeight;

		final int width = options.outWidth;

		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			if (width > height) {

				inSampleSize = Math.round((float) height
						/ (float) reqHeight);

			} else {

				inSampleSize = Math.round((float) width / (float) reqWidth);

			}

		}

		return inSampleSize;

	}

	private Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		image.compress(Bitmap.CompressFormat.JPEG, 80, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中

		int options = 100;

		while (baos.toByteArray().length / 1024 > 50) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩

			options -= 10;// 每次都减少10

			baos.reset();// 重置baos即清空baos

			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中

		}

		ByteArrayInputStream isBm = new ByteArrayInputStream(
				baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中

		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片

		return bitmap;

	}

}
