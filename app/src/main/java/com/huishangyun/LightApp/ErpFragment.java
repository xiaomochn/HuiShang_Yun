package com.huishangyun.LightApp;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Erp.AdjustActivity;
import com.huishangyun.Erp.CustomerfileActivity;
import com.huishangyun.Erp.GatheringActivity;
import com.huishangyun.Erp.OrderGoodsActivity;
import com.huishangyun.Erp.PaymentsActivity;
import com.huishangyun.Erp.SelectActivity;
import com.huishangyun.Office.Attendance.OfficeMainDataList;
import com.huishangyun.Office.Summary.ImageLoad;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.WebHelp;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.model.Methods;
import com.huishangyun.Activity.ChatWebview;
import com.huishangyun.Erp.DeliveryActivity;
import com.huishangyun.Erp.GoodfilesActivity;
import com.huishangyun.Erp.ReportsearchActivity;
import com.huishangyun.yun.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class ErpFragment extends Fragment {
    private View mView;
    private RelativeLayout erp_ordergoods;
    private RelativeLayout erp_select;
    private RelativeLayout erp_delivery;
    private RelativeLayout erp_gathering;
    private RelativeLayout erp_payments;
    private RelativeLayout erp_adjust;
    private RelativeLayout erp_goodfiles;
    private RelativeLayout erp_customerfiles;
    private RelativeLayout erp_reportsearch;
    private ScheduledExecutorService scheduledExecutorService;

    private ViewPager mPager;
    private int currentItem = 0;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        mView = inflater.inflate(R.layout.fragment_erp, container, false);
        initView();
        getNetData();
        return mView;
    }

    /**
     * 实例化组件
     */
    private void initView() {
        erp_ordergoods = (RelativeLayout) mView.findViewById(R.id.erp_ordergoods);
        erp_select = (RelativeLayout) mView.findViewById(R.id.erp_select);
        erp_delivery = (RelativeLayout) mView.findViewById(R.id.erp_delivery);
        erp_gathering = (RelativeLayout) mView.findViewById(R.id.erp_gathering);
        erp_payments = (RelativeLayout) mView.findViewById(R.id.erp_payments);
        erp_adjust = (RelativeLayout) mView.findViewById(R.id.erp_adjust);
        erp_goodfiles = (RelativeLayout) mView.findViewById(R.id.erp_goodfiles);
        erp_customerfiles = (RelativeLayout) mView.findViewById(R.id.erp_customerfiles);
        erp_reportsearch = (RelativeLayout) mView.findViewById(R.id.erp_reportsearch);
        //添加点击事件监听
        erp_ordergoods.setOnClickListener(mListener);
        erp_select.setOnClickListener(mListener);
        erp_delivery.setOnClickListener(mListener);
        erp_gathering.setOnClickListener(mListener);
        erp_payments.setOnClickListener(mListener);
        erp_adjust.setOnClickListener(mListener);
        erp_goodfiles.setOnClickListener(mListener);
        erp_customerfiles.setOnClickListener(mListener);
        erp_reportsearch.setOnClickListener(mListener);
    }


    /**
     * 点击事件监听器
     */
    private OnClickListener mListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Intent startIntent = null;
            switch (v.getId()) {
                case R.id.erp_ordergoods:
                    //订货支付
                    startIntent = new Intent(getActivity(), OrderGoodsActivity.class);
                    break;
                case R.id.erp_select:
                    //采购收货
                    startIntent = new Intent(getActivity(), SelectActivity.class);
                    break;
                case R.id.erp_delivery:
                    //销售出库
                    startIntent = new Intent(getActivity(), DeliveryActivity.class);
                    break;
                case R.id.erp_gathering:
                    //收款
                    startIntent = new Intent(getActivity(), GatheringActivity.class);
                    break;
                case R.id.erp_payments:
                    //付款
                    startIntent = new Intent(getActivity(), PaymentsActivity.class);
                    break;
                case R.id.erp_adjust:
                    //库存调整
                    startIntent = new Intent(getActivity(), AdjustActivity.class);
                    break;
                case R.id.erp_goodfiles:
                    //产品档案
                    startIntent = new Intent(getActivity(), GoodfilesActivity.class);
                    break;
                case R.id.erp_customerfiles:
                    //客户档案
                    startIntent = new Intent(getActivity(), CustomerfileActivity.class);
                    break;
                case R.id.erp_reportsearch:
                    //报表查询
                    startIntent = new Intent(getActivity(), ReportsearchActivity.class);
                    break;

                default:
                    break;
            }
            if (startIntent != null) {
                startActivity(startIntent);
            }
        }

    };


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
                    item = LayoutInflater.from(getActivity()).inflate(R.layout.activity_office_imagefragment, null);
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
    private List<OfficeMainDataList> list = new ArrayList<OfficeMainDataList>();
    private View item;
    private List<View> ViewList = new ArrayList<View>(); // 滑动的图片集合
    private int viewPagerSize;//图片总页数
    private ImageView image;

    /**
     * 初始化ViewPager
     */
    public void InitViewPager() {

        mPager = (ViewPager) mView.findViewById(R.id.viewpager);
        L.e("11111111:" + list.size());
        /*if (list.size()==0) {
			item = LayoutInflater.from(getActivity()).inflate(R.layout.activity_office_imagefragment,null);
			ViewList.add(item);
		}else {*/
        for (int i = 0; i < list.size(); i++) {
            item = LayoutInflater.from(getActivity()).inflate(R.layout.activity_office_imagefragment, null);
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
                        L.e("跳转地址：" + link_Url);
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
        zjRequest.setCompany_ID(MyApplication.getInstance().getCompanyID());

        zjRequest.setKeywords("外勤首页底端流动广告位");

        L.e("获取广告位json:" + JsonUtil.toJson(zjRequest));
        return JsonUtil.toJson(zjRequest);

    }

}
