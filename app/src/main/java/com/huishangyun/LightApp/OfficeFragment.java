package com.huishangyun.LightApp;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.WebHelp;
import com.huishangyun.Activity.ChatWebview;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.model.SubAppList;
import com.huishangyun.Office.Attendance.OfficeMainDataList;
import com.huishangyun.Office.Summary.ImageLoad;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.View.MyGridView;
import com.huishangyun.model.Methods;
import com.huishangyun.yun.R;

/**
 * 办公页面布局
 *
 * @author xsl
 */
public class OfficeFragment extends Fragment {

    protected static final String TAG = null;
    private View view;//保存布局对象
    //	private ImageView office_mian_img;//首页图片
//	private TextView daiban;//待办
    /*private RelativeLayout office_attendance;//考勤
	private RelativeLayout office_businesstrip;//出差
	private RelativeLayout office_approval;//审批
	private RelativeLayout office_information;//信息
	private RelativeLayout office_summary;//小结
	private RelativeLayout office_askforleave;//请假
	private RelativeLayout office_weiguan;//围观
	private RelativeLayout office_map;//地图
*/    private List<OfficeMainDataList> list = new ArrayList<OfficeMainDataList>();
    private int Company_ID;//公司id
    private ScheduledExecutorService scheduledExecutorService;

    private ViewPager mPager;
    private int currentItem = 0;
    private List<View> ViewList = new ArrayList<View>(); // 滑动的图片集合
    private LayoutInflater inflater;
    private View item;
    private int Manager_ID;
    private ImageView image;
    //private TextView index;
    private int viewPagerSize;//图片总页数
    private int W;//屏幕宽
    private int H;//屏幕高

    private List<SubAppList> subAppLists = new ArrayList<SubAppList>();
    private MyAdapter myAdapter;
    private MyGridView mGridView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        Company_ID = MyApplication.getInstance().getCompanyID();
        view = inflater.inflate(R.layout.fragment_office, container, false);
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        W = mDisplayMetrics.widthPixels;
        H = mDisplayMetrics.heightPixels;
        init();
        getNetData();
        return view;
    }

    /**
     * 初始化控件
     */
    private void init() {
        inflater = LayoutInflater.from(getActivity());
        mGridView = (MyGridView) view.findViewById(R.id.channel_gridview);
        getAppList();
    }

    /**
     * 获取子菜单列表
     */
    private void getAppList() {
        subAppLists = SubAppUtils.getInstance().getOAAppLists();
        myAdapter = new MyAdapter();
        mGridView.setAdapter(myAdapter);
        mGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                SubAppUtils.getInstance().startActivity(getActivity(), subAppLists.get(arg2).getCode());
            }
        });
    }


    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return subAppLists.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return subAppLists.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder mHolder;
            SubAppList subAppList = subAppLists.get(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.sub_app_item, null);
                mHolder = new ViewHolder();
                mHolder.icon = (ImageView) convertView.findViewById(R.id.subapp_img);
                mHolder.name = (TextView) convertView.findViewById(R.id.subapp_name);
                convertView.setTag(mHolder);
            } else {
                mHolder = (ViewHolder) convertView.getTag();
            }
            mHolder.icon.setImageResource(SubAppUtils.getInstance().getImageResource(subAppList.getCode()));
            mHolder.name.setText(subAppList.getName());
            return convertView;
        }

    }

    static class ViewHolder {
        public ImageView icon;
        public TextView name;
    }

    /**
     * 获取首页数据
     */
    private void getNetData() {
        new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                String result = DataUtil.callWebService(
                        Methods.HUISHANG_GETAD_LIST,
                        getJson());
                L.e("result:" + result);

                //先判断网络数据是否获取成功，防止网络不好导致程序崩溃
                if (result != null) {
                    // 获取对象的Type
                    Type type = new TypeToken<ZJResponse<OfficeMainDataList>>() {
                    }.getType();
                    ZJResponse<OfficeMainDataList> zjResponse = JsonUtil.fromJson(result,
                            type);
                    // 获取对象列表
                    list.clear();
                    list = zjResponse.getResults();

                    handler.sendEmptyMessage(0);

                } else {
                    handler.sendEmptyMessage(1);
                }
            }
        }.start();
    }


    /**
     * 设置json对象
     *
     * @return
     */
    private String getJson() {

        ZJRequest zjRequest = new ZJRequest();
        // 公司id
        zjRequest.setCompany_ID(Company_ID);

        zjRequest.setKeywords("外勤首页底端流动广告位");

        return JsonUtil.toJson(zjRequest);

    }


    /**
     * 更新UI
     */
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0://刷新
                    //index.setText("1/"+ list.size());
                    //promation_text.setText(list.get(0).getTitle());
                    InitViewPager();

                    break;
                case 1:
                    item = inflater.inflate(R.layout.activity_office_imagefragment, null);
                    ImageView img = (ImageView) item.findViewById(R.id.office_mian_img);
                    img.setImageResource(R.drawable.home_informfigure);
                    break;
                case 2://循环首页操作
                    int arg0 = mPager.getCurrentItem();
                    if (arg0 == (viewPagerSize - 1)) {
                        mPager.setCurrentItem(0);
                        //promation_text.setText(list.get(0).getTitle());
                        //index.setText(1 + "/"+ list.size());
                    } else {
                        mPager.setCurrentItem(arg0 + 1);
                        //promation_text.setText(list.get(arg0+1).getTitle());
                        //index.setText((arg0+2) + "/"+ list.size());
                    }
//				changeImage();
                    break;

                case HanderUtil.case3:
                    mPager.setCurrentItem(currentItem);// 切换当前显示的图片
                    break;
                default:
                    break;
            }
        }

        ;
    };


    /**
     * 初始化ViewPager
     */
    public void InitViewPager() {

        mPager = (ViewPager) view.findViewById(R.id.viewpager);
        Log.e(TAG, "11111111:" + list.size());
		/*if (list.size()==0) {
			item = inflater.inflate(R.layout.activity_office_imagefragment,null);
			ViewList.add(item);
		}else {*/
        for (int i = 0; i < list.size(); i++) {
            item = inflater.inflate(R.layout.activity_office_imagefragment, null);
            ViewList.add(item);
        }
        //}

        //给ViewPager设置适配器
        mPager.setAdapter(new MyFragmentPagerAdapter(ViewList));
        mPager.setCurrentItem(0);//设置当前显示标签页为第一页
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());//页面变化时的监听器
        viewPagerSize = ViewList.size();
        L.e("viewPagerSize:" + viewPagerSize);
        startPager();
//		changeImage();
    }

    private void startPager() {
        if (list.size() > 1) {
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 5, TimeUnit.SECONDS);
        }
    }

    /**
     * 换行切换任务
     *
     * @author Administrator
     */
    private class ScrollTask implements Runnable {

        public void run() {
            synchronized (mPager) {
                L.e("currentItem: " + currentItem);
                currentItem = (currentItem + 1) % list.size();
                handler.sendEmptyMessage(HanderUtil.case3); // 通过Handler切换图片
            }
        }

    }

    @Override
    public void onDestroy() {
        if (scheduledExecutorService != null)
            scheduledExecutorService.shutdown();
        super.onDestroy();
    }


    /**
     * viewpager滑动选项卡监听
     *
     * @author xsl
     */
    public class MyOnPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageSelected(int position) {
            // TODO Auto-generated method stub
            currentItem = position;

        }
    }


    public class MyFragmentPagerAdapter extends PagerAdapter {
        private List<View> mlist;

        public MyFragmentPagerAdapter(List<View> list) {
            // TODO Auto-generated constructor stub
            mlist = list;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mlist.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            View view = mlist.get(position);
            image = (ImageView) view.findViewById(R.id.office_mian_img);
            LinearLayout layout = (LinearLayout) view.findViewById(R.id.office_layout);
            //图片地址
            String url = "http://img.huishangyun.com/UploadFile/huishang/AD/" + list.get(position).getPicture();
//		    L.e("公司---Company_ID:" + Company_ID);
            //链接地址
            final String link_Url = list.get(position).getSourceUrl();
            new ImageLoad().displayImage(getActivity(), url, image, R.drawable.home_informfigure, 0, false);
            layout.setOnClickListener(new OnClickListener() {//图片单击跳转头条链接
                @Override
                public void onClick(View v) {
                    if (!list.get(position).getSourceUrl().equals("")) {
                        Intent intent = new Intent(getActivity(), ChatWebview.class);
                        intent.putExtra("httpurl", link_Url);
                        Log.e("跳转地址：" , link_Url);
                        startActivity(intent);
                    } else {
                        //不做链接
                    }
                }
            });

            container.removeView(mlist.get(position));
            container.addView(mlist.get(position));
            return mlist.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mlist.get(position));
        }


    }
}
