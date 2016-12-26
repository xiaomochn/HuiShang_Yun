package com.huishangyun.Channel.Opport;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.manager.EnumManager;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Content;
import com.huishangyun.model.EnumKey;
import com.huishangyun.model.Methods;
import com.huishangyun.swipelistview.MyXListView;
import com.huishangyun.swipelistview.MyXListView.MyXListViewListener;
import com.huishangyun.yun.R;

public class OpportSearch extends BaseActivity implements MyXListViewListener {
	protected static final String TAG = null;
	private RelativeLayout back;// 返回
	private RelativeLayout search;// 搜索
	private EditText ed_text;// 编辑框
	private RelativeLayout transparent_background;//半透明界面
	private LinearLayout search_hint;//搜索提示
	private LinearLayout listview_backgroud;//listview层
	private MyXListView listview;
	MyAdater adapter;//listview自定义适配器
	List<OpportList> ItemLists = new ArrayList<OpportList>();
	List<OpportList> list = new ArrayList<OpportList>();
	private int pager_number_my = 1;// 页码
	private int pager_size_all = 10;// 单页显示的条数
	CharSequence filterContent;//获取搜索编辑框文本内容
	private String search_text;//搜索输入内容
	private int flag = -1;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);  
		setContentView(R.layout.activity_clue_search);
		Intent intent = getIntent();
		flag = intent.getIntExtra("flag", -1);
		init();
		adapter = new MyAdater(this);
		listview.setAdapter(adapter);
		listview.setPullLoadEnable(true);
		listview.setMyXListViewListener(this);
		edtTextChanage();
	}

	/**
	 * 控件实例化
	 */
	private void init() {
		back = (RelativeLayout)findViewById(R.id.back);
		search = (RelativeLayout)findViewById(R.id.search);
		ed_text = (EditText) findViewById(R.id.ed_text);
		//将提示语改成：请输入主题
		ed_text.setHint("请输入主题");
		transparent_background = (RelativeLayout) findViewById(R.id.transparent_background);
		search_hint = (LinearLayout) findViewById(R.id.search_hint);
		listview_backgroud = (LinearLayout)findViewById(R.id.listview_backgroud);
		listview = (MyXListView) findViewById(R.id.search_listview);
		
		
		
		back.setOnClickListener(new ButtonClickListener());
		search.setOnClickListener(new ButtonClickListener());
		transparent_background.setOnClickListener(new ButtonClickListener());
		
		// 单击listview的item跳转到详情页面
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (position - 1 >= 0) {
					Intent intent = new Intent(
							OpportSearch.this,
							DetailOpportActivity.class);
					// 将integer转int
					int int_id = (list.get(position - 1).getID()).intValue();
					// 传值
					intent.putExtra("ID", int_id + "");
					intent.putExtra("ChangeOpport", "No");
					startActivity(intent);
				}

			}
		});
		
		
		//强制弹出软键盘，延时目的是为了避免控件未加载完弹出软键盘失败
		Handler mHandler = new Handler();
		mHandler.postDelayed(new Runnable() {		
			@Override
			public void run() {
				// TODO Auto-generated method stub
				 //让软键盘自动弹出
			      ed_text.setFocusable(true);
			      ed_text.setFocusableInTouchMode(true);
			      ed_text.requestFocus();
			     InputMethodManager inputManager = 
			     (InputMethodManager)ed_text.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			       inputManager.showSoftInput(ed_text, 0);
			}
		}, 200);
	}

	/**
	 * 单击事件处理
	 * 
	 * @author xsl
	 * 
	 */
	public class ButtonClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.back:
				InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);				 
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				finish();
				break;
			case R.id.search:
				setData(0,0,0);
				break;
			case R.id.ed_text:

				break;
			case R.id.transparent_background:
				InputMethodManager imm1 = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);				 
				imm1.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    finish();
				break;
			default:
				break;
			}
		}

	}
	
	/**
	 * 根据权限设置加载数据和显示界面
	 * @param refresh_index 刷新指数 
	 * 0：刚进来加载
	 * 1：下拉刷新
	 * 2：上拉加载 
	 * @param onload_index 判断下拉刷新那个
	 * @param loadmore_index 判断上拉加载那个
	 * 
	 */
	private void setData(int refresh_index, int onload_index, int loadmore_index ){
		
				 
		// 用户编号
		int uid = Integer.parseInt(preferences.getString(Constant.HUISHANGYUN_UID,
				"0"));
		// 所属部门id
		int department_id = preferences.getInt(Constant.HUISHANG_DEPARTMENT_ID,
				0);

		switch (refresh_index) {
		case 0:
			if (flag == 0) {
				// 获取全部商机
				getNetData(preferences.getInt(Content.COMPS_ID, 1016), 0, department_id, search_text, 1,
						pager_size_all, 1);
			}else if (flag == 1) {
				// 获取个人商机
				getNetData(preferences.getInt(Content.COMPS_ID, 1016), uid, department_id, search_text, 1,
						pager_size_all, 1);
			}
				
			
			

			break;

		case 1:

			if (flag == 0) {
				// 获取全部商机
				getNetData(preferences.getInt(Content.COMPS_ID, 1016), 0, department_id, search_text, 1,
						pager_size_all, 1);
			}else if (flag == 1) {
				// 获取全部商机
				getNetData(preferences.getInt(Content.COMPS_ID, 1016), uid, department_id, search_text, 1,
						pager_size_all, 1);
			}

			break;

		// 上拉加载处理
		case 2:

			if (flag == 0) {
				// 获取全部商机
				getNetData(preferences.getInt(Content.COMPS_ID, 1016), 0, department_id, search_text, 1,
						pager_size_all, 2);
			}else if (flag == 1) {
				// 获取全部商机
				getNetData(preferences.getInt(Content.COMPS_ID, 1016), uid, department_id, search_text, 1,
						pager_size_all, 2);
			}

			break;

		default:
			break;
		}
	
	}
	
	/**
	 * 获取网络数据
	 * @param Company_ID 公司id
	 * @param Manager_ID 用户编号
	 * @param Department_ID 部门编号
	 * @param keywords 搜索关键字
	 * @param pageIndex 页码
	 * @param index 根据指数判断给那个数组赋值 1.2.3：为刷新对应值 --4.5.6为：加载对应值
	 */
	private void getNetData(final int Company_ID, final int Manager_ID,
			final int Department_ID, final String keywords, final int pageIndex,final int pageSize, final int index){
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub					
				String result = DataUtil.callWebService(
						Methods.SUPPLY_LIST,
						getJson(Company_ID, Manager_ID, Department_ID,
								keywords, pageIndex, pageSize));
				Log.e(TAG, "result:" + result+index);
				//先判断网络数据是否获取成功，防止网络不好导致程序崩溃
				if (result != null) {
					// 获取对象的Type
					Type type = new TypeToken<ZJResponse<OpportList>>() {
					}.getType();
					ZJResponse<OpportList> zjResponse = JsonUtil.fromJson(result,
							type);
					// 获取对象列表
					// 获取对象列表
					// 用于下拉刷新或者刚进来时加载数据，需要清除存储的数据
					switch (index) {
					case 1:
						ItemLists.clear();
						ItemLists = zjResponse.getResults();
						//通知listview改变
						handler.sendEmptyMessage(1);
						break;

					// 用于上拉加载或者，搜索后结果很多后的加载，此时不需要清空列表
					case 2:
						ItemLists = zjResponse.getResults();
						handler.sendEmptyMessage(1);
						break;
					
					default:
						break;
					}
				} else {
					Toast.makeText(OpportSearch.this, "没有更多数据！", Toast.LENGTH_SHORT).show();
				}
			}
		}.start();
	}
	
	/**
	 * 设置json对象
	 * @param Company_ID 公司id
	 * @param Manager_ID 用户编号
	 * @param Department_ID 部门编号
	 * @param keywords 搜索关键字
	 * @param pageIndex 页码
	 * @return
	 */
	private String getJson(int Company_ID, int Manager_ID,
			int Department_ID, String keywords, int pageIndex, int pageSize) {
		ZJRequest zjRequest = new ZJRequest();
		
		// 公司id
		zjRequest.setCompany_ID(Company_ID);
		
		// 用户编号
		zjRequest.setManager_ID(Manager_ID);
	
		// 设置部门号，0时为相当没有部门编号查询
		zjRequest.setDepartment_ID(Department_ID);
		// 设置搜索关键字
		zjRequest.setKeywords(keywords);
		// 设置页码，默认是1
		zjRequest.setPageIndex(pageIndex);
		// 设置当页显示条数
		zjRequest.setPageSize(pageSize);
		return JsonUtil.toJson(zjRequest);

	}
	/**
	 * 通知listview进行更新
	 */
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case 1:
					//将ItemLists复制给list,这样做可以避免listview更新造成空指针错误
					list.clear();
					for ( int i = 0; i < ItemLists.size(); i++) {
						list.add(ItemLists.get(i));
					}
					
					if (list.size()<=0) {
						transparent_background.setVisibility(View.GONE);
						listview_backgroud.setVisibility(View.GONE);
						search_hint.setVisibility(View.VISIBLE);
					}else {
						//隐藏半透明布局,显示listview
						transparent_background.setVisibility(View.GONE);
						listview_backgroud.setVisibility(View.VISIBLE);
						search_hint.setVisibility(View.GONE);
					}
	               adapter.notifyDataSetChanged();
					break;
				default:
					break;
			}
		};
	};
	
	/**
	 * 自定义--我的线索--listview适配器
	 */
	public class MyAdater extends BaseAdapter {

		private LayoutInflater mInflater;// 动态布局映射

		public MyAdater(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		// 决定listview显示条数
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
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
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			 ViewHolder holder; 
				if (convertView == null) {
					convertView = mInflater.inflate(
							R.layout.activity_opport_listview_item, null);// 根据布局文件实例化view
					
					holder = new ViewHolder();
					
					holder.theme_name = (TextView) convertView
							.findViewById(R.id.theme_name);// 找某个控件 --标题
					holder.state = (TextView) convertView.findViewById(R.id.state);// 找某个控件--状态
					holder.ps_name = (TextView) convertView
							.findViewById(R.id.ps_name);// 找某个控件 --公司名称
					holder.date = (TextView) convertView.findViewById(R.id.date);// 找某个控件--时间
					holder.price = (TextView) convertView.findViewById(R.id.price);// 找某个控件--价格
//					holder.iv = (ImageView) convertView.findViewById(R.id.iv);// 公司头像
//					holder.price_img = (ImageView) convertView.findViewById(R.id.price_img);// 价格图标
//					holder.date_img = (ImageView) convertView
//							.findViewById(R.id.date_img);// 日期图标
				
					convertView.setTag(holder);

				} else {
					
					holder = (ViewHolder) convertView.getTag();
					
				}
				
				holder.theme_name.setText(list.get(position).getTitle());
				holder.state.setText(backStage(list.get(position).getStatus()));
				if (list.get(position).getSalesStage()==1) {
					holder.state.setTextColor(Color.parseColor("#e60000"));
				}else if (list.get(position).getSalesStage()==2) {
					holder.state.setTextColor(Color.parseColor("#e95811"));
				}else if (list.get(position).getSalesStage()==3) {
					holder.state.setTextColor(Color.parseColor("#039958"));
				}else if (list.get(position).getSalesStage()==4) {
					holder.state.setTextColor(Color.parseColor("#21a5de"));
				}else if (list.get(position).getSalesStage()== 5) {
					holder.state.setTextColor(Color.parseColor("#02b8cd"));
				}else if (list.get(position).getSalesStage()== 6) {
					holder.state.setTextColor(Color.parseColor("#0554f4"));
				}else if (list.get(position).getSalesStage()== 7) {
					holder.state.setTextColor(Color.parseColor("#cbb629"));
				}
				holder.ps_name.setText(list.get(position).getMember_Name());
				holder.price.setText( backPrice(list.get(position).getForecastMoney()));
				holder.date.setText(backDate(list.get(position).getForecastDate()));
//				holder.iv.setBackgroundResource(R.drawable.contact_ps);
//				holder.price_img.setBackgroundResource(R.drawable.contact_ps);
//				holder.date_img.setBackgroundResource(R.drawable.datetime);
				return convertView;
			}

		}

    /**
     * 容器
     * @author xsl
     *
     */
    static class ViewHolder { 
         //标题
        private TextView theme_name; 
        //状态
        private TextView state; 
        //日期
        private TextView date; 
        //创建人
        private TextView ps_name;
        //价格
        private TextView price; 
       
        //创建人图标
        ImageView iv; 
        //价格图标
        ImageView price_img; 
        //日期图标
        ImageView date_img; 
       

        } 
    
	
	/**
	 * 下拉刷新动作
	 * @param load_index
	 */
	private void onLoad() {
		listview.stopRefresh();
		listview.stopLoadMore();
		listview.setRefreshTime();	
		
	}
	
	/**
	 * 监听文本编辑框输入内容
	 */
     private void edtTextChanage(){
    	 ed_text.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub	
				
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				//获取编辑框的文本
				filterContent =s.toString();
				if (((String) filterContent).trim().length()>0) {
					//输入有内容，先清除列表
					list.clear();
					search_text = s.toString().trim();
					Log.e(TAG, "---" + search_text);
					setData(0,0,0);
									
				}else {
					//显示半透明布局
					transparent_background.setVisibility(View.VISIBLE);
					listview_backgroud.setVisibility(View.GONE);
					search_hint.setVisibility(View.GONE);
						
					}			
			}
		});
     }
	/**
	 * 下拉刷新
	 */
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				onLoad();
			}
		}, 2000);//2s后执行onLoad（）方法
		
		//不必要传入参数默认为0传入
		setData(1, 0, 0);
	}

	/**
	 * 上拉加载
	 */
	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				onLoad();
//				pager_number_my += 1;
							
			}
		}, 2000);//2s后执行onLoad（）方法
		pager_size_all += 10;	
		//不必要传入参数默认为0传入
		setData(2, 0, 0);
	}
	
	/**
	 * 传入阶段数值，返回对应中文意思。
	 * 
	 * @param data
	 *            传入数值
	 * @return
	 */
	private String backStage(Integer data) {
		String stage = null;
		com.huishangyun.model.Enum enum1 = EnumManager.getInstance(context)
				.getEmunForIntKey(EnumKey.ENUM_SALES_STAGE, "" + data);
		stage = enum1.getLab();

		return stage;

	}
	
	/**
	 * 传入后台接到的时间，返回我们需要的格式
	 * @param date 传入时间参数
	 * @return
	 */
	private String backDate(String date){
		//这里包含0位但不包含5即（0，5】
		String a = date.substring(0,4);
		String b = date.substring(5,7);
		String c = date.substring(8,10);
		String datesString = a + "年" + b + "月" + c + "日" ;
		
		return datesString;		
	}
	
	/**
	 * 传入一个double的价格返回一个保留两位小数点的string价格。
	 * @param getprice
	 * @return
	 */
	private String backPrice(double getprice){
		DecimalFormat df = new DecimalFormat("#.00");
		df.format(getprice);
		return df.format(getprice);
		
	}
}
