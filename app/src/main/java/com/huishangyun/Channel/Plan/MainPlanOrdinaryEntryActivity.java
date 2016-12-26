package com.huishangyun.Channel.Plan;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Channel.Clues.ClueCustomToast;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Content;
import com.huishangyun.model.Methods;
import com.huishangyun.swipelistview.MyXListView;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.yun.R;

/**
 * 计划，普通用户进入界面
 * @author xsl
 *
 */
public class MainPlanOrdinaryEntryActivity extends BaseActivity implements
		MyXListView.MyXListViewListener {
	protected static final String TAG = null;
	private RelativeLayout back;// 返回九宫格界面
	private RelativeLayout search;// 搜索
	private RelativeLayout add;// 新增线索
	private MyXListView listview;
	MyAdater adapter;//listview自定义适配器
	List<PlanList> ItemLists = new ArrayList<PlanList>();
	List<PlanList> list = new ArrayList<PlanList>();
	private int pager_number_my = 1;// 页码
	private int pager_size_my = 10;// 单页显示的条数
	private TextView title_name;
	private ImageView no_iformation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clue_main_ordinary_entry);
		init();
		adapter = new MyAdater(this);
		listview.setAdapter(adapter);
		listview.setPullLoadEnable(true);
		listview.setMyXListViewListener(this);	
		MyApplication.getInstance().showDialog(MainPlanOrdinaryEntryActivity.this, true, "Loading...");
		setData(0,0,0);
		handler.sendEmptyMessage(2);

	}

	/**
	 * 实例化个控件
	 */
	private void init() {
		back = (RelativeLayout) findViewById(R.id.back_mian);
		search = (RelativeLayout) findViewById(R.id.search);
		add = (RelativeLayout) findViewById(R.id.addclue);
		title_name =(TextView) findViewById(R.id.title_name);
		title_name.setText("计划");
		listview = (MyXListView) findViewById(R.id.ordinary_entry_listview);
		no_iformation = (ImageView) findViewById(R.id.no_information);

		back.setOnClickListener(new ButtonClickListener());
		search.setOnClickListener(new ButtonClickListener());
		add.setOnClickListener(new ButtonClickListener());
		
		// 单击listview的item跳转到详情页面
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (position - 1 >= 0) {
					Intent intent = new Intent(
							MainPlanOrdinaryEntryActivity.this,
							PlanDetailActivity.class);
                    //传值
					intent.putExtra("ID", list.get(position - 1).getID());
					startActivity(intent);
				}

			}
		});
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
			case R.id.back_mian:// 返回到九宫格
				finish();
				break;
			case R.id.search:
				Intent intent1 = new Intent(MainPlanOrdinaryEntryActivity.this, PlanSearch.class);
				intent1.putExtra("flag", 1);//0表示搜索全部，1表示搜索个人
				startActivity(intent1);
				break;
			case R.id.addclue:
				Intent intent2 = new Intent(MainPlanOrdinaryEntryActivity.this, CreatePlanActivity.class);
				startActivity(intent2);
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
		//用户编号
		int uid = Integer.parseInt(preferences.getString(Constant.HUISHANGYUN_UID, "0"));
		//所属部门id
		int department_id = preferences.getInt(Constant.HUISHANG_DEPARTMENT_ID, 0);
		switch (refresh_index) {
		case 0:
			// 获取我的线索
//			getNetData(preferences.getInt(Content.COMPS_ID, 1016), uid, department_id, "", pager_number_my,
//					pager_size_my, 1);
			
			getNetData(preferences.getInt(Content.COMPS_ID, 1016), uid, 0, "", 1, pager_size_my,1);
			break;
			//下拉刷新
		case 1:			
//			getNetData(preferences.getInt(Content.COMPS_ID, 1016), uid, department_id, "", pager_number_my,
//					pager_size_my, 1);
			getNetData(preferences.getInt(Content.COMPS_ID, 1016), uid, 0, "", 1, pager_size_my,1);
			break;
			
			//上拉加载
		case 2:
//			getNetData(preferences.getInt(Content.COMPS_ID, 1016), uid, department_id, "", pager_number_my,
//					pager_size_my, 2);
			getNetData(preferences.getInt(Content.COMPS_ID, 1016), uid, 0, "", pager_number_my, pager_size_my,2);
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
						Methods.PLAN_LIST,
						getJson(Company_ID, Manager_ID, Department_ID,
								keywords, pageIndex,pageSize));				
//				Log.e(TAG, "result:" + result+index);
				//先判断网络数据是否获取成功，防止网络不好导致程序崩溃
				if (result != null) {
					// 获取对象的Type
					Type type = new TypeToken<ZJResponse<PlanList>>() {
					}.getType();
					ZJResponse<PlanList> zjResponse = JsonUtil.fromJson(result,
							type);
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
						handler.sendEmptyMessage(4);
						break;
					
					default:
						break;
					}
				} else {
					handler.sendEmptyMessage(5);
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
		// zjRequest.setCompany_ID(preferences.getInt("1016", 1016));
		// 用户编号
		zjRequest.setManager_ID(Manager_ID);
		// zjRequest.setManager_ID(Integer.parseInt(preferences.getString("0",
		// "0")));
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
					if (list.size()==0) {
						no_iformation.setVisibility(View.VISIBLE);
					}else {
						no_iformation.setVisibility(View.GONE);
					}
	               adapter.notifyDataSetChanged();
	               MyApplication.getInstance().showDialog(MainPlanOrdinaryEntryActivity.this, false, "Loading...");
					break;
			case 4:
				if (ItemLists.size()<=0) {
					new ClueCustomToast().showToast(MainPlanOrdinaryEntryActivity.this,
							R.drawable.toast_warn, "没有更多数据！");
				}else {
					for ( int i = 0; i < ItemLists.size(); i++) {
						list.add(ItemLists.get(i));
					}
					
	               adapter.notifyDataSetChanged();
	               handler.sendEmptyMessage(3);
				}
				break;
				case 5:
					MyApplication.getInstance().showDialog(MainPlanOrdinaryEntryActivity.this, false, "Loading...");
					new ClueCustomToast().showToast(MainPlanOrdinaryEntryActivity.this,
							R.drawable.toast_warn, "未获得任何数据，请检查网络是否连接！");
					break;
				default:
					break;
			}
		};
	};
	
	/**
	 * 自定义--我的商机--listview适配器
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
							R.layout.activity_clue_listview_item, null);// 根据布局文件实例化view
					
					holder = new ViewHolder();
					
					holder.theme_name = (TextView) convertView
							.findViewById(R.id.theme_name);// 找某个控件 --标题
					holder.state = (TextView) convertView.findViewById(R.id.state);// 找某个控件--状态
					holder.state.setVisibility(View.GONE);
					holder.ps_name = (TextView) convertView
							.findViewById(R.id.ps_name);// 找某个控件 --公司名称
					holder.date = (TextView) convertView.findViewById(R.id.date);// 找某个控件--时间
					holder.iv = (ImageView) convertView.findViewById(R.id.iv);// 公司头像
					holder.date_img = (ImageView) convertView
							.findViewById(R.id.date_img);// 日期图标
					
					holder.theme_name.setText(list.get(position).getTitle());
					holder.ps_name.setText(list.get(position).getManager_Name());
					holder.date.setText(backDate(list.get(position).getAddDateTime()));
					holder.iv.setBackgroundResource(R.drawable.contact_ps);
					holder.date_img.setBackgroundResource(R.drawable.datetime);
					
					convertView.setTag(holder);

				} else {
					
					holder = (ViewHolder) convertView.getTag();
					
				}
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
      
       //创建人图标
       ImageView iv; 
       //日期图标
       ImageView date_img; 
      

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
	 * 下拉刷新动作
	 * @param load_index
	 */
	private void onLoad() {
		listview.stopRefresh();
		listview.stopLoadMore();
		listview.setRefreshTime();	
		
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
		pager_number_my = 1;	
		//不必要传入参数默认为0传入
		setData(1, 1, 0);
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
		pager_number_my += 1;	
		//不必要传入参数默认为0传入
		setData(2, 0, 1);
	}
	
	
}
