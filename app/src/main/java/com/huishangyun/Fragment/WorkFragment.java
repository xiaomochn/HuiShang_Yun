package com.huishangyun.Fragment;

import java.io.File;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.huishangyun.model.Constant;
import com.huishangyun.model.Content;
import com.huishangyun.yun.R;
import com.huishangyun.Util.FileUtils;
import com.huishangyun.Util.T;

public class WorkFragment extends BaseFragment{
	private WebView webView;
	private ValueCallback<Uri> mUploadMessage;
	private static final int SHOW_PROGRESS_DIALOG = 22;
	private static final int DISMISS_PROGRESS_DIALOG = 33;
	public static final int FILECHOOSER_RESULTCODE = 1;
	private static final int REQ_CAMERA = FILECHOOSER_RESULTCODE+1;
	private static final int REQ_CHOOSE = REQ_CAMERA+1;
	//http://192.168.0.130:8888/Index.aspx?domain=yqy&name=zheng&pwd=aaaaaa  测试网址
	private String URL = "http://app.huishangyun.com/Index.aspx?";
	private SharedPreferences preferences;
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
		};
	};
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_work, container,false);
		initview(view);
		return view;
	}
	
	private void initview(View view){
		preferences = getActivity().getSharedPreferences(Constant.LOGIN_SET, 0);
		URL = URL +"domain=" + preferences.getString(Content.COMPS_DOMAIN, "yqy")/*分销商名称ID*/+ "&name=" +
				preferences.getString(Constant.USERNAME, "") +"&pwd=" + 
				preferences.getString(Constant.PASSWORD, "");
		webView = (WebView)view.findViewById(R.id.fragment_work_webview);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setDomStorageEnabled(true);
		webView.getSettings().setAppCacheEnabled(true);
        String appCacheDir = getActivity().getApplicationContext()
                        .getDir("cache", Context.MODE_PRIVATE).getPath();
        //webView.getSettings().setPluginsEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setAppCachePath(appCacheDir);
        webView.getSettings().setPluginState(PluginState.ON);
        webView.getSettings().setRenderPriority(RenderPriority.HIGH);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true); // 支持页面放大缩小按钮
        webView.getSettings().setSupportZoom(true);
		webView.setWebChromeClient(new MyWebChromeClient());
		webView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url){ 
			//  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                view.loadUrl(url);
                return true;
           	}

			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
				System.out.println("****onPageFinished=="+url);
			}

			@Override
			public void onPageStarted(WebView view, String url,
					Bitmap favicon) {
				// TODO Auto-generated method stub
				super.onPageStarted(view, url, favicon);
				System.out.println("****onPageStarted=="+url);
			}
			
			@Override
				public void onReceivedError(WebView view, int errorCode,
						String description, String failingUrl) {
					// TODO Auto-generated method stub
					super.onReceivedError(view, errorCode, description, failingUrl);
					handler.sendEmptyMessage(0);
				}
			
			
		   });
			

			webView.setOnLongClickListener(new WebView.OnLongClickListener() {
	           @Override
	           public boolean onLongClick(View v) {
	               return true;
	           }
	       });
		webView.loadUrl(URL);
	}
	
	private class MyWebChromeClient extends WebChromeClient{
		/*@Override
		public void onProgressChanged(WebView view, int newProgress) {
			// TODO Auto-generated method stub
			getActivity().setTitle("系 统 加 载 中 , 请 稍 后 . . .");       
			getActivity().setProgress(newProgress * 100);     
		     if(newProgress == 100)            
		    	 getActivity().setTitle(R.string.app_name);       
			super.onProgressChanged(view, newProgress);
		}*/
	      
	      /*****************android中使用WebView来打开本机的文件选择器 *************************/
	      //  js上传文件的<input type="file" name="fileField" id="fileField" />事件捕获
	      //  Android  > 4.1.1 调用这个方法
	       public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) 
	       { 
	              mUploadMessage = uploadMsg;
	                 Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
	                 intent.addCategory(Intent.CATEGORY_OPENABLE);
	                 intent.setType("image/*");
	                 startActivityForResult(Intent.createChooser(intent, "完成操作需要使用"), FILECHOOSER_RESULTCODE);  
	        
	       }
	      
	     //3.0 + 调用这个方法
	         public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
	             mUploadMessage = uploadMsg;
	             Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
	             intent.addCategory(Intent.CATEGORY_OPENABLE);
	             intent.setType("image/*");
	             startActivityForResult(Intent.createChooser(intent, "完成操作需要使用"), FILECHOOSER_RESULTCODE);
	         }
	      // Android < 3.0 调用这个方法
	         public void openFileChooser(ValueCallback<Uri> uploadMsg) 
	         {
	             mUploadMessage = uploadMsg;
	             Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
	             intent.addCategory(Intent.CATEGORY_OPENABLE);
	             intent.setType("image/*");
	             startActivityForResult(Intent.createChooser(intent, "完成操作需要使用"), FILECHOOSER_RESULTCODE);
	         
	          }
	         /**************end***************/
	}
	
	public final boolean checkSDcard() {
		boolean flag = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
		if (!flag) {
			T.showShort(getActivity(), "请检查你的SD卡");
		}
		return flag;
	}
	
	private String compressPath = "";
	
	protected final void selectImage() {
		if (!checkSDcard())
			return;
		String[] selectPicTypeStr = { "camera","photo" };
		new AlertDialog.Builder(getActivity())
				.setItems(selectPicTypeStr,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								switch (which) {
								// 相机拍摄
								case 0:
									openCarcme();
									break;
								// 手机相册
								case 1:
									chosePic();
									break;
								default:
									break;
								}
								compressPath = Environment
										.getExternalStorageDirectory()
										.getPath()
										+ "/fuiou_wmp/temp";
								new File(compressPath).mkdirs();
								compressPath = compressPath + File.separator
										+ "compress.jpg";
							}
						}).show();
	}
	
	String imagePaths;
	Uri  cameraUri;
	
	private void openCarcme() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		imagePaths = Environment.getExternalStorageDirectory().getPath()
				+ "/fuiou_wmp/temp/"
				+ (System.currentTimeMillis() + ".jpg");
		// 必须确保文件夹路径存在，否则拍照后无法完成回调
		File vFile = new File(imagePaths);
		if (!vFile.exists()) {
			File vDirPath = vFile.getParentFile();
			vDirPath.mkdirs();
		} else {
			if (vFile.exists()) {
				vFile.delete();
			}
		}
		cameraUri = Uri.fromFile(vFile);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
		startActivityForResult(intent, REQ_CAMERA);
	}
	
	/**
	 * 拍照结束后
	 */
	private void afterOpenCamera() {
		File f = new File(imagePaths);
		addImageGallery(f);
		File newFile = FileUtils.compressFile(f.getPath(), compressPath);
	}
	
	/** 解决拍照后在相册中找不到的问题 */
	private void addImageGallery(File file) {
		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
		values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
		getActivity().getContentResolver().insert(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
	}
	
	/**
	 * 本地相册选择图片
	 */
	private void chosePic() {
		FileUtils.delFile(compressPath);
		Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT); // "android.intent.action.GET_CONTENT"
		String IMAGE_UNSPECIFIED = "image/*";
		innerIntent.setType(IMAGE_UNSPECIFIED); // 查看类型
		Intent wrapperIntent = Intent.createChooser(innerIntent, null);
		startActivityForResult(wrapperIntent, REQ_CHOOSE);
	}

	/**
	 * 选择照片后结束
	 * 
	 * @param data
	 */
	private Uri afterChosePic(Intent data) {

		// 获取图片的路径：
		String[] proj = { MediaStore.Images.Media.DATA };
		// 好像是android多媒体数据库的封装接口，具体的看Android文档
		Cursor cursor = getActivity().managedQuery(data.getData(), proj, null, null, null);
		if(cursor == null ){
			T.showShort(getActivity(), "上传的图片仅支持png或jpg格式");
			return null;
		}
		// 按我个人理解 这个是获得用户选择的图片的索引值
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		// 将光标移至开头 ，这个很重要，不小心很容易引起越界
		cursor.moveToFirst();
		// 最后根据索引值获取图片路径
		String path = cursor.getString(column_index);
		if(path != null && (path.endsWith(".png")||path.endsWith(".PNG")||path.endsWith(".jpg")||path.endsWith(".JPG"))){
			File newFile = FileUtils.compressFile(path, compressPath);
			return Uri.fromFile(newFile);
		}else{
			T.showShort(getActivity(), "上传的图片仅支持png或jpg格式");
		}
		return null;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

		if(requestCode == REQ_CAMERA ){
			if (null == mUploadMessage)
	               return;
			afterOpenCamera();
			Uri result = data == null || resultCode != getActivity().RESULT_OK ? null : cameraUri;
			mUploadMessage.onReceiveValue(result);
			mUploadMessage = null;
		}else if(requestCode == REQ_CHOOSE){
			if (null == mUploadMessage)
	               return;
			Uri result = data == null || resultCode != getActivity().RESULT_OK ? null : afterChosePic(data);
			mUploadMessage.onReceiveValue(result);
			mUploadMessage = null;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		webView.getSettings().setBuiltInZoomControls(true);
		if (webView != null) {
			webView.getSettings().setSupportZoom(true);
			webView.getSettings().setBuiltInZoomControls(true); // 支持页面放大缩小按钮
		}
	}
	/**
	 * 返回键监听,MianACtivity调用
	 */
	public boolean onBackPressed(){
		if (webView.canGoBack()) {
            webView.goBack();
            return true;
        }else {
			return false;
		}
	}
	
	
}
