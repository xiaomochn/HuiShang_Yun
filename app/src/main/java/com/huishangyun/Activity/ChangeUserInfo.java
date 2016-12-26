package com.huishangyun.Activity;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Util.Base64Util;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.T;
import com.huishangyun.Util.TimeUtil;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.manager.DepartmentManager;
import com.huishangyun.manager.OperationTime;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Content;
import com.huishangyun.model.LoginResult;
import com.huishangyun.model.Managers;
import com.huishangyun.model.Methods;
import com.huishangyun.model.OperTime;
import com.huishangyun.task.UpLoadFileTask.OnUpLoadResult;
import com.huishangyun.task.UpLoadImgSignText;
import com.huishangyun.yun.R;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
/**
 * 修改个人信息
 * @author pan
 *
 */
public class ChangeUserInfo extends BaseActivity implements OnUpLoadResult{
	private EditText usernameView;
	private EditText passwordView;
	private EditText nameView;
	private EditText phoneView;
	private EditText emailView;
	private EditText sexView;
	private Button uploadbtn;
	private EditText biaoqian;
	private EditText beizhu;
	private Map<String, String> map;
	private LinearLayout user_head;
	private ImageView headImg;
	private String[] items = new String[] { "选择本地图片", "拍照" };
	/* 请求码*/
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESULT_REQUEST_CODE = 2;
	private int uid; 
	private ProgressDialog proDialog;// 进度条
	private String loadPath = ""; 
	private UpLoadImgSignText mImgSignText;
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			MyApplication.getInstance().showDialog(ChangeUserInfo.this, false, "Loading...");
			switch (msg.what) {
			case HanderUtil.case1:
				proDialog.dismiss();
				showCustomToast("修改成功!", true);
				preferences.edit().putString(Constant.CHANGE_NAME, nameView.getText().toString()).commit();
				preferences.edit().putString(Constant.CHANGE_BIAOQIAN, biaoqian.getText().toString()).commit();
				preferences.edit().putString(Constant.CHANGE_PHONE, phoneView.getText().toString()).commit();
				preferences.edit().putString(Constant.CHANGE_EMAIL,emailView.getText().toString()).commit();
				preferences.edit().putString(Constant.CHANGE_BEIZHU, beizhu.getText().toString()).commit();
				preferences.edit().putString("headurl", loadPath).commit();
				setResult(1);
				finish();
				break;
			case HanderUtil.case2:
				proDialog.dismiss();
				showCustomToast((String) msg.obj, false);
				break;
			case HanderUtil.case3:
				setinfo((LoginResult) msg.obj);
				break;
			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chageuserinfo);
		initBackTitle("个人信息");
		setResult(0);
		initview();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		//headImgTask.removeImgCallBack();
		super.onDestroy();
	}
	/**
	 * 实例化组件并添加监听
	 */
	private void initview(){
		proDialog = new ProgressDialog(ChangeUserInfo.this);
		proDialog.setMessage("正在提交...");
		// 设置不可被返回键关闭
		proDialog.setCancelable(false);
		user_head = (LinearLayout)findViewById(R.id.chage_user_head);
		headImg = (ImageView)findViewById(R.id.chage_head);
		user_head.setOnClickListener(listener);
		usernameView = (EditText) findViewById(R.id.chage_name);
		passwordView = (EditText)findViewById(R.id.chage_password);
		sexView = (EditText)findViewById(R.id.chage_sex);
		/*sexView.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				String string = s.toString();
				if (!s.equals("男") && !s.equals("女") && !s.equals("不明")) {
					sexView.setText("不明");
				} 
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});*/
		nameView = (EditText)findViewById(R.id.chage_lastname);
		phoneView = (EditText)findViewById(R.id.chage_phone);
		emailView = (EditText)findViewById(R.id.chage_email);
		uploadbtn =  (Button)findViewById(R.id.chage_upload);
		uploadbtn.setOnClickListener(listener);
		biaoqian = (EditText)findViewById(R.id.chage_biaoqian);
		beizhu = (EditText)findViewById(R.id.chage_beizhu);
		uid = Integer.parseInt(preferences.getString(Constant.HUISHANGYUN_UID, "0"));
		closeInput();
		loadPath = preferences.getString("headurl", "");
		String path = Constant.pathurl+
				MyApplication.getInstance().getCompanyID() +"/Photo/" + loadPath;
		headImg.setImageResource(R.drawable.contact_person);
		//new ImageLoader(ChangeUserInfo.this).DisplayImage(path, headImg, false);
		com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(path, headImg, MyApplication.getInstance().getOptions());
		usernameView.setText(preferences.getString(Constant.USERNAME, ""));
		passwordView.setText(preferences.getString(Constant.PASSWORD, ""));
		L.d(preferences.getString(Constant.XMPP_LOGIN_NAME, ""));
		nameView.setText(preferences.getString(Constant.CHANGE_NAME, ""));
		biaoqian.setText(preferences.getString(Constant.CHANGE_BIAOQIAN, ""));
		phoneView.setText(preferences.getString(Constant.CHANGE_PHONE, ""));
		emailView.setText(preferences.getString(Constant.CHANGE_EMAIL, ""));
		beizhu.setText(preferences.getString(Constant.CHANGE_BEIZHU, ""));
		selectinfo(preferences.getString(Constant.XMPP_LOGIN_NAME, ""));
		
	}
	/**
	 * 按键监听方法
	 */
	private OnClickListener listener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.img_title_work_back:
				finish();
				break;
				
			case R.id.chage_user_head://设置头像！
				showDialog();
				break;
			case R.id.chage_upload:
				//关闭键盘
				closeInput();
				if (isCanUpload()) {
					proDialog.show();
					new Thread(){
						public void run() {
							//向服务器提交数据
							ZJRequest<LoginResult> zjRequest = new ZJRequest<LoginResult>();
							LoginResult loginResult = new LoginResult();
							loginResult.setID(uid);
							loginResult.setPhoto(loadPath);
							loginResult.setAction("Update");
							loginResult.setLoginName(preferences.getString(Constant.USERNAME, ""));
							loginResult.setRealName(nameView.getText().toString().trim());
							loginResult.setMobile(phoneView.getText().toString().trim());
							loginResult.setEmail(emailView.getText().toString().trim());
							loginResult.setSign(biaoqian.getText().toString().trim());
							String sex = sexView.getText().toString().trim();
							if (sex.equals("男")) {
								loginResult.setSex("男");
							} else if (sex.equals("女")) {
								loginResult.setSex("女");
							}
							loginResult.setNote(beizhu.getText().toString().trim());
							zjRequest.setData(loginResult);
							String json = JsonUtil.toJson(zjRequest);
							L.e("json = " + json);
							String result = DataUtil.callWebService(Methods.SET_MANAGER_INFO, json);
							if (result == null) {
								Message msg = new Message();
								msg.what = HanderUtil.case2;
								msg.obj = "无法连接到服务器";
								handler.sendMessage(msg);
								return;
							}
							Type type = new TypeToken<ZJResponse>(){}.getType();
							ZJResponse zjResponse = JsonUtil.fromJson(result, type);
							if (zjResponse.getCode() == 0) {
								preferences.edit().putString(Constant.XMPP_MY_REAlNAME, nameView.getText().toString().trim()).commit();
								getManager();
								handler.sendEmptyMessage(HanderUtil.case1);
							} else {
								Message msg = new Message();
								msg.what = HanderUtil.case2;
								msg.obj = zjResponse.getDesc();
								handler.sendMessage(msg);
							}
						};
					}.start();
				}
				break;

			default:
				break;
			}
		}
	};
	/**
	 * 是否满足提交条件
	 * @return
	 */
	private boolean isCanUpload(){
		if (TextUtils.isEmpty(nameView.getText().toString().trim())) {
			T.showShort(ChangeUserInfo.this, "姓名不得为空");
			return false;
		} else if (TextUtils.isEmpty(phoneView.getText().toString().trim())) {
			T.showShort(ChangeUserInfo.this, "手机号不得为空");
			return false;
		} else if (TextUtils.isEmpty(sexView.getText().toString().trim())) {
			T.showShort(ChangeUserInfo.this, "性别不得为空");
			return false;
		} else if (!sexView.getText().toString().trim().equals("男") && !sexView.getText().toString().equals("女")) {
			T.showShort(ChangeUserInfo.this, "性别填写错误");
			return false;
		}
		return true;
	}
	
	/**
	 * 查询用户数据
	 */
	private void selectinfo(final String UID){
		MyApplication.getInstance().showDialog(ChangeUserInfo.this, true, "Loading...");
		new Thread(){
			public void run() {
				ZJRequest zjRequest = new ZJRequest();
				zjRequest.setManager_ID(Integer.parseInt(preferences.getString(Constant.HUISHANGYUN_UID, "0")));
				String json = JsonUtil.toJson(zjRequest);
				String result = DataUtil.callWebService(Methods.GET_MANAGER_INFO, json);
				if (result == null) {
					Message msg = new Message();
					msg.what = HanderUtil.case2;
					msg.obj = "无法连接到服务器";
					handler.sendMessage(msg);
					return;
				}
				Type type = new TypeToken<ZJResponse<LoginResult>>(){}.getType();
				ZJResponse<LoginResult> zjResponse = JsonUtil.fromJson(result, type);
				if (zjResponse.getCode() != 0) {
					Message msg = new Message();
					msg.what = HanderUtil.case2;
					msg.obj = zjResponse.getDesc();
					handler.sendMessage(msg);
					return;
				}
				Message msg = new Message();
				msg.what = HanderUtil.case3;
				msg.obj = zjResponse.getResult();
				handler.sendMessage(msg);
			};
		}.start();
		
	}
	/**
	 * 设置个人信息
	 */
	private void setinfo(LoginResult loginResult){
		usernameView.setText(loginResult.getLoginName());
		nameView.setText(loginResult.getRealName());
		biaoqian.setText(loginResult.getSign());
		phoneView.setText(loginResult.getMobile());
		emailView.setText(loginResult.getEmail());
		beizhu.setText(loginResult.getNote());
		/*if (loginResult.getSex().equals("M")) {
			sexView.setText("男");
		} else if(loginResult.getSex().equals("W")){
			sexView.setText("女");
		} else {
			sexView.setText("不明");
		}*/
		sexView.setText(loginResult.getSex() + "");
		String path = "http://img.huishangyun.com/UploadFile/huishang/"+
				MyApplication.getInstance().getCompanyID() +"/Photo/" + preferences.getString("headurl", "");
		L.d("path" + path);
		//new ImageLoader(ChangeUserInfo.this).DisplayImage(path, headImg, false);
		com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(path, headImg, MyApplication.getInstance().getOptions());
	}
	
	/**
	 * 显示选择对话框
	 */
	private void showDialog() {
		new AlertDialog.Builder(this).setTitle("设置头像")
				.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				switch (which) {
				case 0:
					Intent intentFromGallery = new Intent();
					intentFromGallery.setType("image/*"); // 设置文件类型
					intentFromGallery
							.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(intentFromGallery,
							IMAGE_REQUEST_CODE);
					break;
				case 1:

					Intent intent = new Intent(
							MediaStore.ACTION_IMAGE_CAPTURE);
					// 判断存储卡是否可以用，可用进行存储
					if (hasSdcard()) {
						//下面这句指定调用相机拍照后的照片存储的路径
						intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
								.fromFile(new File(Environment
										.getExternalStorageDirectory(),"camera.jpg")));
						startActivityForResult(intent, CAMERA_REQUEST_CODE);
					}
					break;
				default:
					break;
				}
			}

		}).show();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		L.d("data:" + data +"");
		if (resultCode == RESULT_OK) {
			
		
		switch (requestCode) {
		// 如果是直接从相册获取
		case IMAGE_REQUEST_CODE:
			startPhotoZoom(data.getData());
			break;
		// 如果是调用相机拍照时
		case CAMERA_REQUEST_CODE:
			File temp = new File(Environment.getExternalStorageDirectory()
					+ "/camera.jpg");
			startPhotoZoom(Uri.fromFile(temp));
			break;
		//裁剪图片返回	
		case 3:
			if (data != null) {
				setPicToView(data);
			}
			break;
		}
		}
	}
	
	
	/**
	 * 裁剪图片方法实现
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		//下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 3);
	}
	
	/**
	 * 保存裁剪之后的图片数据
	 * @param picdata
	 */
	private void setPicToView(Intent picdata) {
		Bundle extras = picdata.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			Drawable drawable = new BitmapDrawable(photo);
			//设置图片
			//headImg.setImageDrawable(drawable);
			//获取图片保存到指定路径
			String fileName = TimeUtil.getFileTime(System.currentTimeMillis()) + ".jpg";
			String path = Constant.SAVE_IMG_PATH + File.separator + fileName;
			try {
				File file =new File(path);//输出路径  
				FileOutputStream out = new FileOutputStream(file);//设置输出流 
				//退出时保存   
				photo.compress(Bitmap.CompressFormat.JPEG, 100, out);  
	            out.flush();  
	            out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			//开始上传
			proDialog.show();
			String bace64;
			try {
				bace64 = Base64Util.encodeBase64File(path);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				proDialog.dismiss();
				showCustomToast("上传失败", false);
				return;
			}
			
			//headImgTask.execute(path);
			mImgSignText = new UpLoadImgSignText(bace64, "Photo", fileName, MyApplication.getInstance().getCompanyID() + "", "");
			mImgSignText.setUpLoadResult(this);
			new Thread(mImgSignText).start();
		}
	}

	/**
	 * 判断是否有SD卡
	 * 
	 * @return
	 */
	public boolean hasSdcard() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 获取访问服务器的Json
	 * 
	 * @return
	 */
	private String getJson(String Table_Name) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(0);// 获取当前时间
		String time = formatter.format(curDate);
		ZJRequest zjRequest = new ZJRequest();
		zjRequest.setCompany_ID(preferences.getInt(Content.COMPS_ID, 0));
		OperTime operTime = new OperTime();
		operTime = OperationTime.getInstance(ChangeUserInfo.this).getOperTime(Table_Name);
		if (operTime == null || operTime.getOperationTime() == null || operTime.getOperationTime().equals("")) {
			zjRequest.setOperationTime(time);
		} else {
			zjRequest.setOperationTime(operTime.getOperationTime());
		}
		L.e("json == " + JsonUtil.toJson(zjRequest));
		return JsonUtil.toJson(zjRequest);

	}
	
	/**
	 * 更新人员列表
	 */
	private boolean getManager() {
		String departlist = DataUtil.callWebService(
				Methods.UPDATA_MANAGER, getJson(DepartmentManager.MANAGER_TABLENAME));
		if (departlist != null) {
			Type type = new TypeToken<ZJResponse<Managers>>() {
			}.getType();
			ZJResponse<Managers> zjResponse = JsonUtil.fromJson(
					departlist, type);
			if (zjResponse.getCode() == 0) {
				List<Managers> departments = zjResponse.getResults();
				for (int i = 0; i < departments.size(); i++) {
					DepartmentManager.getInstance(ChangeUserInfo.this)
							.saveManagers(departments.get(i));
				}
				if (departments.size() > 0) {
					OperTime operTime = new OperTime();
					operTime.setOperationTime(departments.get(0).getOperationTime());
					operTime.setTable_Name(DepartmentManager.MANAGER_TABLENAME);
					OperationTime.getInstance(ChangeUserInfo.this).saveTime(operTime);
				}
				return true;
			} else {
				return false;
			}

		} else {
			return false;
		}

	}

	@Override
	public void onUpLoadResult(String FileName, String FilePath,
			boolean isSuccess) {
		// TODO Auto-generated method stub
		proDialog.dismiss();
		if (isSuccess) {
			showCustomToast("头像上传成功", true);
			loadPath = FilePath;
			String path = "http://img.huishangyun.com/UploadFile/huishang/"+
					MyApplication.getInstance().getCompanyID() +"/Photo/" + loadPath;
			L.d("path" + path);
			//new ImageLoader(ChangeUserInfo.this).DisplayImage(path, headImg, false);
			com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(path, headImg, MyApplication.getInstance().getOptions());
			L.d("loadPath:" + FilePath);
		} else {
			showCustomToast("头像上传失败", false);
		}
		
	}
	
}
