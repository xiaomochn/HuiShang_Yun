package com.huishangyun.Channel.Task;

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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Channel.Opport.OpportList;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.Util.webServiceHelp;
import com.huishangyun.model.Constant;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.swipelistview.MyXListView;
import com.huishangyun.model.Content;
import com.huishangyun.model.Methods;
import com.huishangyun.swipelistview.MyXListView.MyXListViewListener;
import com.huishangyun.yun.R;

/**
 * 关联商机页面
 * @author Pan
 *
 */
public class TaskOpportActivity extends BaseActivity implements MyXListViewListener{
	private MyXListView mXListView;
	private List<OpportList> mList;
	private OpportAdapter adapter;
	private int pageIndex = 1;//页码
	private boolean isLoadMore = false;
	private webServiceHelp<OpportList> mServiceHelp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_opport);
		initBackTitle("选择商机");
		initView();
		setResult(RESULT_CANCELED);//设置返回值，默认为没有选择商机
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mServiceHelp.removeOnServiceCallBack();
		super.onDestroy();
	}
	
	/**
	 * 实例化各组件
	 */
	private void initView() {
		mServiceHelp = new webServiceHelp<OpportList>(Methods.SUPPLY_LIST, 
				new TypeToken<ZJResponse<OpportList>>() {}.getType());
		mServiceHelp.setOnServiceCallBack(onServiceCallBack);
		mXListView = (MyXListView) this.findViewById(R.id.task_opport_listview);
		mList = new ArrayList<OpportList>();
		adapter = new OpportAdapter(TaskOpportActivity.this, mList);
		mXListView.setPullLoadEnable(true);//设置能下拉（这两个属性是上拉加载下拉刷新的）
		mXListView.setMyXListViewListener(this);
		mXListView.setOnItemClickListener(mClickListener);
		mXListView.setAdapter(adapter);
		//new Thread(new getOpportList(getJson(pageIndex))).start();
		mServiceHelp.start(getJson(pageIndex));
		showDialog("正在加载...");
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		pageIndex = 1;
		isLoadMore = false;
		//new Thread(new getOpportList(getJson(pageIndex))).start();
		mServiceHelp.start(getJson(pageIndex));
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		isLoadMore = true;
		//new Thread(new getOpportList(getJson(pageIndex))).start();
		mServiceHelp.start(getJson(pageIndex));
	}
	
	/**
	 * 点击事件监听
	 */
	private OnItemClickListener mClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			if (arg2 == 0) {
				return;
			}
			Intent mIntent = new Intent();
			mIntent.putExtra("ID", mList.get(arg2 - 1).getID());
			mIntent.putExtra("Name", mList.get(arg2 - 1).getTitle());
			setResult(RESULT_OK, mIntent);
			finish();
		}
	};
	
	/**
	 * 生成查询语句
	 * @param pageIndex
	 * @return
	 */
	private String getJson(int pageIndex) {
		ZJRequest zjRequest = new ZJRequest();
		// 公司id
		zjRequest.setCompany_ID(preferences.getInt(Content.COMPS_ID, 1016));
	
		// 设置部门号，0时为相当没有部门编号查询
		zjRequest.setDepartment_ID(preferences.getInt(Constant.HUISHANG_DEPARTMENT_ID, 0));
		// 设置搜索关键字
		zjRequest.setKeywords("");
		// 设置页码，默认是1
		zjRequest.setPageIndex(pageIndex);
		// 设置当页显示条数
		zjRequest.setPageSize(10);
		return JsonUtil.toJson(zjRequest);
		
	}
	
	/**
	 * 获取商机列表线程
	 * @author Pan
	 *
	 */
	/*private class getOpportList implements Runnable {
		private String json;
		
		public getOpportList(String json) {
			L.e("json = " + json);
			this.json = json;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			String result = DataUtil.callWebService(Methods.SUPPLY_LIST, json);
			L.e("result = " + result);
			if (result != null) {
				Type type = new TypeToken<ZJResponse<OpportList>>() {}.getType();
				ZJResponse<OpportList> zjResponse = JsonUtil.fromJson(result,type);
				if (zjResponse.getCode() == 0) {
					List<OpportList> results;
					if (!isLoadMore) {//判断是否为上拉加载
						results = zjResponse.getResults();
						mList.clear();
						for (OpportList opportList : results) {
							mList.add(opportList);
						}
					} else {//是上拉加载
						results = zjResponse.getResults();
						for (OpportList opportList : results) {
							mList.add(opportList);
						}
					}
					mHandler.sendEmptyMessage(HanderUtil.case1);
					pageIndex++;
					Message msg = new Message();
					msg.obj = zjResponse.getResults();
					msg.what = HanderUtil.case1;
					mHandler.sendMessage(msg);
				} else {//返回错误信息
					Message msg = new Message();
					msg.obj = zjResponse.getDesc();
					msg.what = HanderUtil.case2;
					mHandler.sendMessage(msg);
				}
			} else {//无法访问服务器
				Message msg = new Message();
				msg.obj = "无法访问服务器";
				msg.what = HanderUtil.case2;
				mHandler.sendMessage(msg);
			}
		}
		
	}*/
	
	private webServiceHelp.OnServiceCallBack<OpportList> onServiceCallBack = new webServiceHelp.OnServiceCallBack<OpportList>() {

		@Override
		public void onServiceCallBack(boolean haveCallBack,
				ZJResponse<OpportList> zjResponse) {
			// TODO Auto-generated method stub
			if (haveCallBack && zjResponse != null) {
				if (zjResponse.getCode() == 0) {
					List<OpportList> results;
					if (!isLoadMore) {//判断是否为上拉加载
						results = zjResponse.getResults();
						mList.clear();
						for (OpportList opportList : results) {
							mList.add(opportList);
						}
					} else {//是上拉加载
						results = zjResponse.getResults();
						for (OpportList opportList : results) {
							mList.add(opportList);
						}
					}
					//mHandler.sendEmptyMessage(HanderUtil.case1);
					pageIndex++;
					Message msg = new Message();
					msg.obj = zjResponse.getResults();
					msg.what = HanderUtil.case1;
					mHandler.sendMessage(msg);
				} else {//返回错误信息
					Message msg = new Message();
					msg.obj = zjResponse.getDesc();
					msg.what = HanderUtil.case2;
					mHandler.sendMessage(msg);
				}
			} else {
				Message msg = new Message();
				msg.obj = "无法访问服务器";
				msg.what = HanderUtil.case2;
				mHandler.sendMessage(msg);
			}
		}
	};
	
	
	private Handler mHandler  = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HanderUtil.case1:
				List<OpportList> results;
				results = (List<OpportList>) msg.obj;
				if (!isLoadMore) {
					mList.clear();
				} 
				for (OpportList opportList : results) {
					mList.add(opportList);
				}
				pageIndex++;
				adapter.notifyDataSetChanged();
				break;
				
			case HanderUtil.case2:
				showCustomToast((String) msg.obj, false);
				break;

			default:
				break;
			}
			dismissDialog();
			mXListView.setRefreshTime();
			mXListView.stopLoadMore();
			mXListView.stopRefresh();
		};
	};
	
	/**
	 * 自定义--全部商机--listview适配器
	 */
	private class OpportAdapter extends BaseAdapter {

		private LayoutInflater mInflater;// 动态布局映射
		private List<OpportList> mList;

		public OpportAdapter(Context context, List<OpportList> mList) {
			this.mInflater = LayoutInflater.from(context);
			this.mList = mList;
		}

		// 决定listview显示条数
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
//			return ItemLists.size();
			return mList.size();
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
				holder.iv = (ImageView) convertView.findViewById(R.id.iv);// 公司头像
				holder.price_img = (ImageView) convertView.findViewById(R.id.price_img);// 价格图标
				holder.date_img = (ImageView) convertView
						.findViewById(R.id.date_img);// 日期图标
				
				
				
				convertView.setTag(holder);

			} else {
				
				holder = (ViewHolder) convertView.getTag();
				
			}
			
			holder.theme_name.setText(mList.get(position).getTitle());
			holder.state.setText(backStage(mList.get(position).getStatus()));
			holder.ps_name.setText(mList.get(position).getMember_Name());
			holder.price.setText( backPrice(mList.get(position).getForecastMoney()));
			holder.date.setText(backDate(mList.get(position).getForecastDate()));
			
			
			/*holder.iv.setBackgroundResource(R.drawable.contact_ps);
			holder.price_img.setBackgroundResource(R.drawable.contact_ps);
			holder.date_img.setBackgroundResource(R.drawable.datetime);*/
			
			return convertView;
		}

	}
	
	/**
	 * ViewHolder 模式, 效率提高 50%
	 * 
	 * @author XSL
	 * @version 不用这个容器会导致进入viewpager特别卡
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
	 * 传入阶段数值，返回对应中文意思。
	 * 
	 * @param data
	 *            传入数值
	 * @return
	 */
	private String backStage(Integer data) {
		String stage = null;
		switch (data) {
		case 1:
			stage = "初期沟通";
			break;
		case 2:
			stage = "立项跟踪";
			break;
		case 3:
			stage = "呈报方案";
			break;
		case 4:
			stage = "商务谈判";
			break;
		default:
			break;
		}
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
