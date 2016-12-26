package com.huishangyun.Channel.Opport;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Content;
import com.huishangyun.model.Methods;
import com.huishangyun.yun.R;

public class MoneyOpportActivity extends BaseActivity {
	protected static final String TAG = null;
	private LinearLayout back;// 杩斿洖閿�
	private LinearLayout search_opport;// 鍟嗘満鎼滅储
	private LinearLayout create_opport;// 鍒涘缓鍟嗘満
	private LinearLayout more_option;// 鏇村鍟嗘満
	private RelativeLayout top_bar;//椤堕儴鏉�
	private LinearLayout search_bar;//鎼滅储鏉�
	private RelativeLayout rl_canvers;//鍗婇�鏄庡眰
	private EditText ed_text;//鎼滅储妗�
	PopupWindow mWindow;//popupwindows
	private ViewPager vPager;// 椤靛崱鍐呭
	private TextView textView1, textView2, textView3;//閫夐」鍗℃爣棰樻枃瀛�
	private int offset = 0;// 鍔ㄧ敾鍥剧墖鍋忕Щ閲�
	private int currIndex = 0;// 褰撳墠椤靛崱缂栧彿
	private int bmpW;// 鍔ㄧ敾鍥剧墖瀹藉害
	View view1, view2, view3;// 涓変釜viewpager閫夐」鍗￠〉闈�
	ImageView lineImg;//婊氬姩鍔ㄧ敾鍥剧墖
	DisplayMetrics dm;
	ArrayList<View> pagerList;//椤靛崱瀹瑰櫒
	LayoutInflater mInflater;
	ListView all_opport, my_opport, department_opport;//涓夐�椤瑰崱listview
	MoneyListViewAdapter  adapter;//listview閫傞厤鍣�
	private LinearLayout linearLayout1;
	List<CreateBusinses> ItemLists = new ArrayList<CreateBusinses>();
	List<CreateBusinses> List = new ArrayList<CreateBusinses>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_opport_mian);
		
		init();
		viewpagerinit();
		initViewPager();
		initPoptWindow();
		getNetData();
	}
	
	/**
 	 * 获取详情数据
 	 * @param ID
 	 */
 	private void getNetData(){
 		new Thread(){
 			@Override
 			public void run() {
 				// TODO Auto-generated method stub	
 			
 				String result = DataUtil.callWebService(
						Methods.SUPPLY_STAGE,
						getJson());
 				Log.e(TAG,"result:" + getJson());
 				//先判断网络数据是否获取成功，防止网络不好导致程序崩溃
 				if (result != null) {
 					// 获取对象的Type
 					Type type = new TypeToken<ZJResponse<CreateBusinses>>() {
 					}.getType();
 					ZJResponse<CreateBusinses> zjResponse = JsonUtil.fromJson(result,
							type);
 					// 获取对象列表
 					ItemLists = zjResponse.getResults();
 					mHandler.sendEmptyMessage(1);
 				} else {
 				
 					
 				}
 				
 			}
 		}.start();
 	}
 	
 	/**
 	 * 设置json对象
 	 * @return
 	 */
 	private String getJson() {
 		ZJRequest zjRequest = new ZJRequest();
// 		zjRequest.setManager_ID(Integer.parseInt(preferences.getString(Constant.HUISHANGYUN_UID, "0")));
 		zjRequest.setCompany_ID(preferences.getInt(Content.COMPS_ID, 1016));
 		zjRequest.setKeywords("Price");
 		zjRequest.setDepartment_ID(preferences.getInt(Constant.HUISHANG_DEPARTMENT_ID,0));
 		return JsonUtil.toJson(zjRequest);

 	}
	/**
	 * 椤甸潰鎺т欢瀹炰緥鍖�
	 */
	private void init() {
		linearLayout1 = (LinearLayout)findViewById(R.id.linearLayout1);
		linearLayout1.setVisibility(View.GONE);
        //澶撮儴缁勪欢
		top_bar = (RelativeLayout) findViewById(R.id.top_bar);
		back = (LinearLayout) findViewById(R.id.back);
		search_opport = (LinearLayout) findViewById(R.id.search_opport);
		search_opport.setVisibility(View.GONE);
		create_opport = (LinearLayout) findViewById(R.id.create_opport);
		more_option = (LinearLayout) findViewById(R.id.more_option);
		
	
		
		//viewpager椤靛崱鍚嶇О缁勪欢
		textView1 = (TextView) findViewById(R.id.tv1);
		textView2 = (TextView) findViewById(R.id.tv2);
		textView3 = (TextView) findViewById(R.id.tv3);
		
        //瀵瑰簲鍗曞嚮浜嬩欢
		back.setOnClickListener(new ButtonClickListener());
//		search_opport.setOnClickListener(new ButtonClickListener());
		create_opport.setOnClickListener(new ButtonClickListener());
		more_option.setOnClickListener(new ButtonClickListener());

		
	}

	/**
	 * 鍗曞嚮浜嬩欢澶勭悊
	 * 
	 * @author xsl
	 * 
	 */
	public class ButtonClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			switch (v.getId()) {
			case R.id.back://杩斿洖涓荤晫闈�
				finish();
				Intent intent = new Intent(MoneyOpportActivity.this,MainOpportActivity.class);
				startActivity(intent);
				break;

			case R.id.create_opport:
				//璺宠浆鍒板垱寤哄晢鏈洪〉闈�
				Intent createintent = new Intent(MoneyOpportActivity.this,
						CreateOpportActivity.class);
				startActivity(createintent);

				break;

				
                   
//				break;
			case R.id.more_option:
				//寮瑰嚭popupwindow
                mWindow.showAsDropDown(more_option);
				break;
				
				
				//popupwindow鍗曞嚮浜嬩欢
			case R.id.opport_pop_stage://鏈〉闈�
				mWindow.dismiss();
				finish();
				Intent intent2 = new Intent(MoneyOpportActivity.this,
						StageOpportActivity.class);
				startActivity(intent2);
				break;

			case R.id.opport_pop_time://璺宠浆鍒版椂闂撮〉闈�
				mWindow.dismiss();
				finish();
				Intent intent1 = new Intent(MoneyOpportActivity.this,TimeOpportActivity.class);
				startActivity(intent1);
				break;
				
			case R.id.opport_pop_priority://璺宠浆鍒伴噾棰濋〉闈�
				mWindow.dismiss();
				break;
				
			default:
				break;
			}

		}

	}
	/**
	 * 鍒涘缓PopuptWindow瀵硅薄
	 */
	private void initPoptWindow(){
		//鍔犺浇甯冨眬
		View mPopView = mInflater.inflate(R.layout.activity_apport_popuwindow, null);
		mWindow = new PopupWindow(mPopView);
		
		//璁剧疆鑾峰彇鐒︾偣
		mWindow.setFocusable(true); 
		
		//闃叉寮瑰嚭鑿滃崟鑾峰彇鐒︾偣涔嬪悗锛岀偣鍑籥ctivity鐨勫叾浠栫粍浠舵病鏈夊搷搴�
		mWindow.setBackgroundDrawable(new PaintDrawable()); 
		
		//璁剧疆鐐瑰嚮绐楀彛澶栬竟绐楀彛娑堝け 
		mWindow.setOutsideTouchable(true); 		
		mWindow.update();
		
		 // 璁剧疆寮瑰嚭绐椾綋鐨勫
		mWindow.setWidth(dm.widthPixels / 2);
		
        // 璁剧疆寮瑰嚭绐椾綋鐨勯珮
		mWindow.setHeight(LayoutParams.WRAP_CONTENT);
		
		//鑾峰彇鎺т欢瀵硅薄
		LinearLayout stage = (LinearLayout) mPopView.findViewById(R.id.opport_pop_stage);
		LinearLayout time = (LinearLayout) mPopView.findViewById(R.id.opport_pop_time);
		LinearLayout priority = (LinearLayout) mPopView.findViewById(R.id.opport_pop_priority);
		
		//璁剧疆鐐瑰嚮鏃堕棿鐩戝惉
		stage.setOnClickListener(new ButtonClickListener());
		time.setOnClickListener(new ButtonClickListener());
		priority.setOnClickListener(new ButtonClickListener());
		
	}

	/**
	 * viewpager瀹炰緥鍖栧悇缁勪欢
	 */
	private void viewpagerinit() {
		
		// ViewPager缁勪欢
		lineImg = (ImageView) findViewById(R.id.cursor);
		lineImg.setVisibility(View.GONE);
		vPager = (ViewPager) findViewById(R.id.vPager);
		pagerList = new ArrayList<View>();
		mInflater = getLayoutInflater().from(MoneyOpportActivity.this);

		// 鑾峰彇甯冨眬瀵硅薄
		view1 = mInflater.inflate(R.layout.activity_opport_morelistview, null);
		view2 = mInflater.inflate(R.layout.activity_clue_mian_my, null);
		view3 = mInflater.inflate(R.layout.activity_clue_main_department, null);
		
		all_opport = (ListView) view1.findViewById(R.id.all_listview);
//		my_opport = (ListView) view2.findViewById(R.id.my_listview);
//		department_opport = (ListView) view3.findViewById(R.id.department_listview);
//		adapter1 = new AllListViewAdapter(this);
		adapter = new MoneyListViewAdapter (this);
		all_opport.setAdapter(adapter);
//		my_opport.setAdapter(adapter4);
//		department_opport.setAdapter(adapter4);
		
		// 灏嗗竷灞�坊鍔犲埌瀹瑰櫒
		pagerList.add(view1);
		pagerList.add(view2);
		pagerList.add(view3);
	}
	
	
	/**
	 * 璁剧疆ViewPager
	 */
	private void initViewPager() {
		
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.line_task).getWidth();// 鑾峰彇鍥剧墖瀹藉害
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 鑾峰彇鍒嗚鲸鐜囧搴�
		offset = (screenW / 3 - bmpW) / 2;// 璁＄畻鍋忕Щ閲�
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		lineImg.setImageMatrix(matrix);// 璁剧疆鍔ㄧ敾鍒濆浣嶇疆

		// 娣诲姞Adapter
		vPager.setAdapter(new MyPagerAdapter(pagerList));
		vPager.setCurrentItem(0);
		vPager.setOnPageChangeListener(new MyOnPageChangeListener());

	}
	
	/**
	 * 椤靛崱鍙樺姩鐩戝惉
	 * 
	 */
	private class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
			setTopView(arg0);
			L.d("arg0 == " + arg0);
		}

	}

	/**
	 * 璁剧疆椤堕儴鍋忕Щ
	 *
	 */
	private void setTopView(int arg0) {
		int one = offset * 2 + bmpW;// 椤靛崱1 -> 椤靛崱2 鍋忕Щ閲�
		int two = one * 2;// 椤靛崱1 -> 椤靛崱3 鍋忕Щ閲�
		Animation animation = null;
		switch (arg0) {
		case 0:
			if (currIndex == 1) {//鍒ゆ柇褰撳墠閫変腑椤�
				animation = new TranslateAnimation(one, 0, 0, 0);
			} else if (currIndex == 2) {
				animation = new TranslateAnimation(two, 0, 0, 0);

			}
			textView1.setTextColor(Color.parseColor("#21a5de"));
			textView2.setTextColor(Color.parseColor("#646464"));
			textView3.setTextColor(Color.parseColor("#646464"));
			break;
		case 1:
			if (currIndex == 0) {
				animation = new TranslateAnimation(offset, one, 0, 0);
			} else if (currIndex == 2) {
				animation = new TranslateAnimation(two, one, 0, 0);
			}
			textView1.setTextColor(Color.parseColor("#646464"));
			textView2.setTextColor(Color.parseColor("#21a5de"));
			textView3.setTextColor(Color.parseColor("#646464"));
			break;
		case 2:
			if (currIndex == 0) {
				animation = new TranslateAnimation(offset, two, 0, 0);
			} else if (currIndex == 1) {
				animation = new TranslateAnimation(one, two, 0, 0);
			}
			textView1.setTextColor(Color.parseColor("#646464"));
			textView2.setTextColor(Color.parseColor("#646464"));
			textView3.setTextColor(Color.parseColor("#21a5de"));
			break;

		default:
			break;
		}
		currIndex = arg0;
		animation.setFillAfter(true);// True:鍥剧墖鍋滃湪鍔ㄧ敾缁撴潫浣嶇疆
		animation.setDuration(200);
		lineImg.startAnimation(animation);
	}
	

	/**
	 * viewpager閫傞厤鍣�
	 * @author xsl
	 *
	 */
	public class MyPagerAdapter extends PagerAdapter{
		public List<View> mListViews;
		
		public MyPagerAdapter(List<View> mListViews){
			this.mListViews = mListViews;
		}
		
		@Override
		public void destroyItem(View container, int position, Object object) {
			// TODO Auto-generated method stub
			((ViewPager) container).removeView(mListViews.get(position));
		}
		
		@Override
		public void finishUpdate(ViewGroup container) {
			// TODO Auto-generated method stub
			super.finishUpdate(container);
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
//			return mListViews.size();
			return 1;
		}
		
		@Override
		public Object instantiateItem(View container, int position) {
			// TODO Auto-generated method stub
			((ViewPager) container).addView(mListViews.get(position), 0);
			 return mListViews.get(position);
		}
		
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			 return arg0 == (arg1);
		}
		
		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
			// TODO Auto-generated method stub
			super.restoreState(state, loader);
		}
		
		@Override
		public Parcelable saveState() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public void startUpdate(View container) {
			// TODO Auto-generated method stub
		}
     }

	
	/**
	 * 鑷畾涔�-閲戦--listview閫傞厤鍣�
	 */
	public class MoneyListViewAdapter extends BaseAdapter {

		private LayoutInflater mInflater;// 鍔ㄦ�甯冨眬鏄犲皠

		public MoneyListViewAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		// 鍐冲畾listview鏄剧ず鏉℃暟
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return List.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolderStage holder; 
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.activity_opport_money_listview_item, null);// 鏍规嵁甯冨眬鏂囦欢瀹炰緥鍖杤iew
				
				holder = new ViewHolderStage();
				holder.item = (LinearLayout) convertView.findViewById(R.id.item);
				holder.money = (TextView) convertView
						.findViewById(R.id.money);// 鎵炬煇涓帶浠�--閲戦鍚嶇О
				holder.view = (View) convertView.findViewById(R.id.view);
				holder.number = (TextView) convertView.findViewById(R.id.number);// 鎵炬煇涓帶浠�-姝ら噾棰濇暟閲�			
				holder.money_img = (ImageView) convertView.findViewById(R.id.money_img);//閲戦鍥炬爣	
				convertView.setTag(holder);

			} else {
				
				holder = (ViewHolderStage) convertView.getTag();
				
			}
			if (position==0) {
				holder.view.setVisibility(View.VISIBLE);
			}else {
				holder.view.setVisibility(View.GONE);
			}
			
			
			holder.money.setText(List.get(position).getTitle());
			holder.number.setText(List.get(position).getCo() + "");			
			holder.money_img.setBackgroundResource(R.drawable.opport_money_group);
			
			holder.item.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(MoneyOpportActivity.this, MainOpportActivity.class);
					intent.putExtra("index", 3);
					intent.putExtra("imei", List.get(position).getTitle() );
					startActivity(intent);
					finish();
				}
			}); 
			
			return convertView;
		}

	}
	
	/***----ViewHolder涓嶆槸Android鐨勫紑鍙慉PI锛岃�鏄竴绉嶈璁℃柟娉曪紝灏辨槸璁捐涓潤鎬佺被锛岀紦瀛樹竴涓嬶紝鐪佸緱Listview鏇存柊鐨勬椂鍊欙紝杩樿閲嶆柊鎿嶄綔銆�----**/	
     
	/**
	 * ViewHolder 妯″紡, 鏁堢巼鎻愰珮 50%
	 * 
	 * @author XSL
	 * @version 涓嶇敤杩欎釜瀹瑰櫒浼氬鑷磋繘鍏iewpager鐗瑰埆鍗�
	 */
    static class ViewHolderStage { 
         //閲戦鍚嶇О
        private TextView money; 
        //姝ら噾棰濇潯鏁�
        private TextView number; 
   
        //閲戦鍥炬爣
        ImageView money_img; 
        //item
        private LinearLayout item;
        //item头
        private View view;
        } 

    /**
     * 更新数据列表
     */
	public Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				
				break;
            case 1:
            	List.clear();
				for ( int i = 0; i < ItemLists.size(); i++) {
					List.add(ItemLists.get(i));
				}
				Log.e(TAG, "list.size:" + List.size());
				Log.e(TAG, "item.size:" + ItemLists.size());
				adapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		};
	};

}
