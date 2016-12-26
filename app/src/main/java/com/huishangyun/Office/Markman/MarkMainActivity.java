package com.huishangyun.Office.Markman;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Adapter.MarkMainAdapter;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.TimeUtil;
import com.huishangyun.Util.Utility;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.Util.webServiceHelp;
import com.huishangyun.model.CallCalendarList;
import com.huishangyun.model.CallList;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Methods;
import com.huishangyun.swipelistview.MyXListView;
import com.huishangyun.widget.CalendarTag;
import com.huishangyun.widget.CalendarView;
import com.huishangyun.yun.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pan on 2015/7/31.
 */
public class MarkMainActivity extends BaseActivity {
    //private ViewPager mViewPager;
    private TextView mTimeTextView;//年月日
    private LinearLayout mLeftMonth;//上一个月
    private LinearLayout mRightMonth;//下一个月
    private CalendarView mCalendarView;//日历控件
    private int year = 0;
    private int month = 0;
    private LinearLayout mYLMark;//预约拜访
    private LinearLayout mLFRecord;//来访记录
    private LinearLayout mYYRecord;//预约记录
    private MyXListView mListView;//当日来访记录
    private TextView mLFRecordNum;
    private TextView mYYRecordNum;
    private MarkMainAdapter mainAdapter;
    private List<MarkManBean> mList = new ArrayList<MarkManBean>();
    private webServiceHelp<CallCalendarList> serviceHelp;
    private webServiceHelp<MarkManBean> callListwebServiceHelp;
    //页面数量
    private int pageIndex = 1;
    private final int pageSize = 10;
    private boolean isFrist = true;
    private boolean isLoadMore = false;
    private String BelongDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_markman);
        initBackTitle("预约拜访");
        initView();
    }

    /**
     * 实例化组件
     */
    private void initView() {
        //实例化控件
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        mTimeTextView = (TextView) findViewById(R.id.markman_mou);
        mLeftMonth = (LinearLayout) findViewById(R.id.markman_left_mou);
        mRightMonth = (LinearLayout) findViewById(R.id.markman_right_mou);
        mCalendarView = (CalendarView) findViewById(R.id.markman_calendarview);
        mYLMark = (LinearLayout) findViewById(R.id.markman_reserve);
        mLFRecord = (LinearLayout) findViewById(R.id.markman_annal);
        mYYRecord = (LinearLayout) findViewById(R.id.markman_record);
        mListView = (MyXListView) findViewById(R.id.markman_listview);
        mLFRecordNum = (TextView) findViewById(R.id.markman_annal_num);
        mYYRecordNum = (TextView) findViewById(R.id.markman_record_num);
        mainAdapter = new MarkMainAdapter(this, mList);
        mListView.setAdapter(mainAdapter);
        mListView.setMyXListViewListener(myXListViewListener);
        mListView.setPullLoadEnable(true);//设置能下拉（这两个属性是上拉加载下拉刷新的）
        //点击事件监听
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0)
                    return;
                MarkManBean markManBean = mList.get(position - 1);
                Intent intent = new Intent(MarkMainActivity.this, MarkManListActivity.class);
                if (markManBean.getGuest_ID() == MyApplication.getInstance().getManagerID())
                    intent.putExtra(MarkManListActivity.STATUS_NAME, MarkManListActivity.STATUS_YY);
                else
                    intent.putExtra(MarkManListActivity.STATUS_NAME, MarkManListActivity.STATUS_LF);
                startActivity(intent);
            }
        });
        //设置点击监听
        mLeftMonth.setOnClickListener(listener);
        mRightMonth.setOnClickListener(listener);
        mYLMark.setOnClickListener(listener);
        mLFRecord.setOnClickListener(listener);
        mYYRecord.setOnClickListener(listener);
        refreshCalemderCard(year, month);
        //日期点击监听
        mCalendarView.setOnDayClickListener(new CalendarView.OnDayClickListener() {
            @Override
            public void onDayClick(CalendarView calendarView, String calendarDay) {
                L.e("calendarDay = " + calendarDay);
                BelongDate = calendarDay;
                //重置当前页数
                pageIndex = 1;
                callListwebServiceHelp.start(getCallListJson(BelongDate));
                showNotDialog("");
                isLoadMore = false;
            }
        });

        BelongDate = year + "-" + mCalendarView.addZero(month + "") + "-" + mCalendarView.addZero(calendar.get(Calendar.DAY_OF_MONTH) + "");
        //设置当前的日期
        mCalendarView.setToDay(BelongDate);

        //设置网络参数
        serviceHelp = new webServiceHelp<CallCalendarList>(Methods.HUISHANG_GET_CALLCALENDARLIST,
                new TypeToken<ZJResponse<CallCalendarList>>() {
                }.getType());

        callListwebServiceHelp = new webServiceHelp<MarkManBean>(Methods.HUISHANG_GET_CALLLIST,
                new TypeToken<ZJResponse<MarkManBean>>() {
                }.getType());
        //设置网络访问接口
        serviceHelp.setOnServiceCallBack(onServiceCallBack);
        callListwebServiceHelp.setOnServiceCallBack(callListOnServiceCallBack);

        //获取当月数据
        serviceHelp.start(getCalendarListJson(year, month));
        showNotDialog("");
    }

    /**
     * 访问服务器json
     *
     * @param year  年
     * @param month 月
     * @return
     */
    private String getCalendarListJson(int year, int month) {
        ZJRequest zjRequest = new ZJRequest();
        zjRequest.setManager_ID(Integer.parseInt(MyApplication.getInstance().getSharedPreferences().getString(Constant.HUISHANGYUN_UID, "0")));
        zjRequest.setYear(year);
        zjRequest.setMonth(month);
        return JsonUtil.toJson(zjRequest);
    }

    /**
     * @param BelongDate 年月日
     * @return
     */
    private String getCallListJson(String BelongDate) {
        ZJRequest zjRequest = new ZJRequest();
        zjRequest.setBelongDate(BelongDate);
        zjRequest.setManager_ID(Integer.parseInt(MyApplication.getInstance().getSharedPreferences().getString(Constant.HUISHANGYUN_UID, "0")));

        zjRequest.setPageIndex(pageIndex);
        zjRequest.setPageSize(pageSize);
        L.e("json = " + JsonUtil.toJson(zjRequest));
        return JsonUtil.toJson(zjRequest);
    }


    /**
     * 刷新时间
     *
     * @param year
     * @param month
     */
    private void refreshCalemderCard(int year, int month) {
        mCalendarView.setTime(year, month);
        mTimeTextView.setText(year + "年" + mCalendarView.addZero(month + "") + "月");
        //设置日历控件高度
        WindowManager wm = this.getWindowManager();

        int width = wm.getDefaultDisplay().getWidth();
        //int mHeight = mCalendarView.getHeight();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, width + 20);
        L.e("mCalendarView.mOutHeight = " + width);
        mCalendarView.setLayoutParams(params);
    }

    /**
     * 点击事件监听
     */
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent startIntent = null;
            switch (v.getId()) {
                case R.id.markman_left_mou:
                    //判断是否为1月
                    if (month == 1) {
                        year--;
                        month = 12;
                    } else {
                        month--;
                    }
                    refreshCalemderCard(year, month);
                    serviceHelp.start(getCalendarListJson(year, month));
                    showNotDialog("");
                    break;
                case R.id.markman_right_mou:
                    //判断当前是否为12月
                    if (month == 12) {
                        year++;
                        month = 1;
                    } else {
                        month++;
                    }
                    refreshCalemderCard(year, month);
                    serviceHelp.start(getCalendarListJson(year, month));
                    showNotDialog("");
                    break;
                case R.id.markman_reserve:
                    //预约拜访
                    startIntent = new Intent(MarkMainActivity.this, MarkAddActivity.class);
                    break;
                case R.id.markman_annal:
                    //来访记录
                    startIntent = new Intent(MarkMainActivity.this, MarkManListActivity.class);
                    startIntent.putExtra(MarkManListActivity.STATUS_NAME, MarkManListActivity.STATUS_LF);
                    break;
                case R.id.markman_record:
                    //预约记录
                    startIntent = new Intent(MarkMainActivity.this, MarkManListActivity.class);
                    startIntent.putExtra(MarkManListActivity.STATUS_NAME, MarkManListActivity.STATUS_YY);
                    break;
                default:
                    break;
            }
            //启动相应界面
            if (startIntent != null) {
                startActivity(startIntent);
            }
        }
    };

    /**
     * 列表刷新监听
     */
    private MyXListView.MyXListViewListener myXListViewListener = new MyXListView.MyXListViewListener() {
        @Override
        public void onRefresh() {
            isLoadMore = false;
            pageIndex = 1;
            callListwebServiceHelp.start(getCallListJson(BelongDate));
        }

        @Override
        public void onLoadMore() {
            isLoadMore = true;
            callListwebServiceHelp.start(getCallListJson(BelongDate));
        }
    };

    /**
     * 获取服务器参数返回监听
     */
    private webServiceHelp.OnServiceCallBack<CallCalendarList> onServiceCallBack = new webServiceHelp.OnServiceCallBack<CallCalendarList>() {
        @Override
        public void onServiceCallBack(boolean haveCallBack, ZJResponse<CallCalendarList> zjResponse) {
            dismissDialog();
            if (haveCallBack) {
                switch (zjResponse.getCode()) {
                    case 0:
                        Map<String, CalendarTag> map = new HashMap<String, CalendarTag>();
                        List<CallCalendarList> lists = zjResponse.getResults();
                        //设置有数据日期颜色
                        for (CallCalendarList callCalendarList : lists) {
                            if (callCalendarList.getInCount() > 0 || callCalendarList.getOutCount() > 0) {
                                //设置有数据的日期
                                CalendarTag calendarTag = new CalendarTag(TimeUtil.getTimeNoT(callCalendarList.getBelongDate()),
                                        CalendarTag.TAG_DONE, CalendarTag.TAG_DONE);
                                map.put(calendarTag.getDate(), calendarTag);
                            }
                        }
                        mCalendarView.setDay(map);
                        //设置当月来访次数
                        String[] size = zjResponse.getDesc().split(",");
                        if (size != null && size.length > 1) {
                            mLFRecordNum.setText(size[0]);
                            mYYRecordNum.setText(size[1]);
                        } else {
                            mLFRecordNum.setText("0");
                            mYYRecordNum.setText("0");
                        }
                        //第一次加载当天的数据
                        if (isFrist) {
                            isFrist = false;
                            callListwebServiceHelp.start(getCallListJson(BelongDate));
                            showNotDialog("");
                        }
                        break;
                    default:
                        showCustomToast("" + zjResponse.getDesc(), false);
                        break;
                }
            } else {
                showCustomToast("服务器连接失败", haveCallBack);
            }
        }
    };

    /**
     * 获取数据返回
     */
    private webServiceHelp.OnServiceCallBack<MarkManBean> callListOnServiceCallBack = new webServiceHelp.OnServiceCallBack<MarkManBean>() {
        @Override
        public void onServiceCallBack(boolean haveCallBack, ZJResponse<MarkManBean> zjResponse) {
            dismissDialog();
            mListView.stopLoadMore();
            mListView.stopRefresh();
            if (haveCallBack) {
                switch (zjResponse.getCode()) {
                    case 0:
                        if (!isLoadMore)
                            mList.clear();
                        List<MarkManBean> lists = zjResponse.getResults();
                        for (MarkManBean callList : lists) {
                            mList.add(callList);
                        }
                        mainAdapter.notifyDataSetChanged();
                        Utility.setListViewHeightBasedOnChildren(mListView);
                        pageIndex++;
                        break;
                    default:
                        showCustomToast("" + zjResponse.getDesc(), false);
                        break;
                }
            } else {
                showCustomToast("服务器连接失败", haveCallBack);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        serviceHelp.removeOnServiceCallBack();
        callListwebServiceHelp.removeOnServiceCallBack();
    }
}
