package com.huishangyun.Office.Markman;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.TimeUtil;
import com.huishangyun.Util.ViewHolder;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.Util.webServiceHelp;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Methods;
import com.huishangyun.swipelistview.MyXListView;
import com.huishangyun.yun.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pan on 2015/8/10.
 */
public class MarkManListActivity extends BaseActivity{
    private MyXListView mListView;
    private ImageView mNoImage;
    private MyAdapter myAdapter;
    private webServiceHelp<MarkManBean> mServiceHelp;
    private List<MarkManBean> mList = new ArrayList<MarkManBean>();
    private int pagerIndex = 1;
    private final int pagerSize = 10;
    private boolean isLoadMore = false;
    private webServiceHelp mStatusHelp;
    public static final int STATUS_LF = 2;
    public static final int STATUS_YY = 1;
    public static final String STATUS_NAME = "status";
    private int status = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_markman_list);
        initView();
        status = getIntent().getIntExtra(STATUS_NAME, 0);
        if (status == 2)
            initBackTitle("来访记录");
        else
            initBackTitle("预约记录");
        initNetWord();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mServiceHelp.removeOnServiceCallBack();
        mStatusHelp.removeOnServiceCallBack();
    }

    /**
     * 实例化组件
     */
    private void initView() {
        mListView = (MyXListView) findViewById(R.id.markman_list);
        mListView.setPullLoadEnable(true);//设置能下拉（这两个属性是上拉加载下拉刷新的）
        mListView.setMyXListViewListener(myXListViewListener);
        mNoImage = (ImageView) findViewById(R.id.no_img);
        mNoImage.setVisibility(View.GONE);
        //mListView.setEmptyView(mNoImage);
        myAdapter = new MyAdapter();
        mListView.setAdapter(myAdapter);
        mListView.stopLoadMore();
        mListView.stopRefresh();
    }

    private MyXListView.MyXListViewListener myXListViewListener = new MyXListView.MyXListViewListener() {
        @Override
        public void onRefresh() {
            pagerIndex = 1;
            mServiceHelp.start(getJson());
            isLoadMore = false;
        }

        @Override
        public void onLoadMore() {
            mServiceHelp.start(getJson());
            isLoadMore = true;
        }
    };

    private void initNetWord() {
        mServiceHelp = new webServiceHelp<MarkManBean>(Methods.HUISHANG_GET_CALLLIST, new TypeToken<ZJResponse<MarkManBean>>(){}.getType());
        mServiceHelp.setOnServiceCallBack(new webServiceHelp.OnServiceCallBack<MarkManBean>() {

            @Override
            public void onServiceCallBack(boolean haveCallBack, ZJResponse<MarkManBean> zjResponse) {
                mListView.stopRefresh();
                mListView.stopLoadMore();
                mListView.setRefreshTime();
                if (haveCallBack) {
                    switch (zjResponse.getCode()) {
                        case 0:
                            if (!isLoadMore)
                                mList.clear();
                            List<MarkManBean> list = zjResponse.getResults();
                            for (MarkManBean markManBean : list) {
                                mList.add(markManBean);
                            }
                            myAdapter.notifyDataSetChanged();
                            pagerIndex++;
                            break;
                        default:
                            showCustomToast("" + zjResponse.getDesc(), false);
                            break;
                    }
                } else {
                    showCustomToast("无法连接到服务器", false);
                }
            }
        });
        mServiceHelp.start(getJson());


        mStatusHelp = new webServiceHelp(Methods.HUISHANG_SET_CALL_STATUS, new TypeToken<ZJResponse>(){}.getType());
        mStatusHelp.setOnServiceCallBack(new webServiceHelp.OnServiceCallBack() {
            @Override
            public void onServiceCallBack(boolean haveCallBack, ZJResponse zjResponse) {
                dismissDialog();
                if (haveCallBack) {
                    switch (zjResponse.getCode()) {
                        case 0:
                            showCustomToast("操作成功", false);

                            mListView.startRefresh2();
                            break;
                        default:
                            showCustomToast(zjResponse.getDesc(), false);
                            break;
                    }
                } else {
                    showCustomToast("无法连接到服务器", false);
                }
            }
        });
    }

    private String getJson() {
        ZJRequest zjRequest = new ZJRequest();
        zjRequest.setManager_ID(MyApplication.getInstance().getManagerID());
        zjRequest.setType(getIntent().getIntExtra(STATUS_NAME, 0));
        zjRequest.setPageIndex(pagerIndex);
        zjRequest.setPageSize(pagerSize);
        return JsonUtil.toJson(zjRequest);
    }

    /**
     * 控件适配器
     */
    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mList.size() == 0) {
                mNoImage.setVisibility(View.VISIBLE);
            } else {
                mNoImage.setVisibility(View.GONE);
            }
            return mList.size();
        }


        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = LayoutInflater.from(MarkManListActivity.this).inflate(R.layout.item_markmanlist, null);

            ImageView imageView = ViewHolder.get(convertView, R.id.markman_list_img);//左边圆圈
            TextView dateView = ViewHolder.get(convertView, R.id.markman_list_date);//日期
            TextView mintView = ViewHolder.get(convertView, R.id.markman_list_mint);//时间
            TextView agreeView = ViewHolder.get(convertView, R.id.markman_list_agree);//同意
            TextView refuseView = ViewHolder.get(convertView, R.id.markman_list_refuse);//拒绝
            TextView typeView = ViewHolder.get(convertView, R.id.markman_list_type);//状态
            TextView nameView = ViewHolder.get(convertView, R.id.markman_list_name);//姓名
            TextView unitView = ViewHolder.get(convertView, R.id.markman_list_unit);//单位
            TextView noteView = ViewHolder.get(convertView, R.id.markman_list_note);//事由
            TextView phoneView = ViewHolder.get(convertView, R.id.markman_list_phone);//电话
            LinearLayout linearLayout = ViewHolder.get(convertView, R.id.markman_list_line);
            LinearLayout callView = ViewHolder.get(convertView, R.id.markman_list_call);//拨打电话
            TextView nameLeftView = ViewHolder.get(convertView, R.id.markman_add_nameleft);//左边名称

            final MarkManBean markManBean = mList.get(position);

            String time = markManBean.getReserveTime();
            dateView.setText(TimeUtil.getTimeOnType(time, "MM-dd"));
            mintView.setText(TimeUtil.getTimeOnType(time, "HH:mm"));
            //判断显示的控件
            if (status == STATUS_LF) {
                nameView.setText(markManBean.getGuest_Name());
                unitView.setText(markManBean.getGuest_Company());
                phoneView.setText(markManBean.getGuest_Mobile());
                nameLeftView.setText("来  访  人：");
                if (markManBean.getStatus().equals("正常")) {
                    linearLayout.setVisibility(View.VISIBLE);
                    typeView.setVisibility(View.GONE);
                } else {
                    linearLayout.setVisibility(View.GONE);
                    typeView.setVisibility(View.VISIBLE);

                }
            } else {
                nameView.setText(markManBean.getHost_Name());
                unitView.setText(markManBean.getHost_Company());
                phoneView.setText(markManBean.getHost_Mobile());
                nameLeftView.setText("被  访  人：");
                linearLayout.setVisibility(View.GONE);
                typeView.setVisibility(View.VISIBLE);
            }

            noteView.setText(markManBean.getNote());
            imageView.setImageResource(getLeftImg(markManBean.getStatus()));
            typeView.setTextColor(getResources().getColor(getTypeColor(markManBean.getStatus())));
            typeView.setText(getTypeStr(markManBean.getStatus()));

            //拨打电话
            callView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + (status == STATUS_LF ?  markManBean.getGuest_Mobile() : markManBean.getHost_Mobile())));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //启动
                    startActivity(intent);
                }
            });
            //同意
            agreeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ZJRequest zjRequest = new ZJRequest();
                    zjRequest.setManager_ID(MyApplication.getInstance().getManagerID());
                    zjRequest.setNote("");
                    zjRequest.setAction("OK");
                    zjRequest.setID(markManBean.getID());
                    zjRequest.setOperationName(MyApplication.getInstance().getSharedPreferences().getString(Constant.XMPP_MY_REAlNAME, ""));
                    showNotDialog("");
                    mStatusHelp.start(JsonUtil.toJson(zjRequest));
                }
            });
            //拒绝
            refuseView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ZJRequest zjRequest = new ZJRequest();
                    zjRequest.setManager_ID(MyApplication.getInstance().getManagerID());
                    zjRequest.setNote("");
                    zjRequest.setAction("Cancel");
                    zjRequest.setID(markManBean.getID());
                    zjRequest.setOperationName(MyApplication.getInstance().getSharedPreferences().getString(Constant.XMPP_MY_REAlNAME, ""));
                    showNotDialog("");
                    mStatusHelp.start(JsonUtil.toJson(zjRequest));
                }
            });

            return convertView;
        }

        /**
         * 获取左边图标
         * @param status
         * @return
         */
        private int getLeftImg(int status) {
            if (status == 1)
                return R.drawable.appointment_application;
            if (status == 2)
                return R.drawable.appointment_agree;
            if (status == 3)
                return R.drawable.appointment_agree;
            if (status == 4)
                return R.drawable.appointment_refused;
            return R.drawable.appointment_application;
        }

        /**
         * 或者字体颜色
         * @param status
         * @return
         */
        private int getTypeColor(int status) {
            if (status == 1)
                return R.color.c_txt96;
            if (status == 2)
                return R.color.c_tx06cd77;
            if (status == 3)
                return R.color.c_tx06cd77;
            if (status == 4)
                return R.color.c_txt96;
            return R.color.c_txt96;
        }

        private String getTypeStr(int status) {
            if (status == 1)
                return "待验证";
            if (status == 2)
                return "已确认";
            if (status == 3)
                return "已验证";
            if (status == 4)
                return "已谢绝";
            return "未知状态";
        }


    }
}
