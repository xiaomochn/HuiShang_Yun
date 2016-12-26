package com.huishangyun.Office.Summary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.gotye.api.GotyeUser;
import com.huishangyun.Activity.Chat;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Channel.Clues.ClueCustomToast;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Methods;
import com.huishangyun.swipelistview.MyXListView;
import com.huishangyun.yun.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SummaryDetailFourthDateFragment extends Fragment implements MyXListView.MyXListViewListener {

    protected static final String TAG = null;
    View FourthView;
    private int Company_ID;//公司id
    private int Manager_ID;//登录人id
    private int Department_ID;//部门id
    private int index;
    private int ID;
    private String belongdate;//所属时间
    private MyXListView slistview;//listview
    private SummaryAdapter adapter;
    private List<SummaryDateList> itemLists = new ArrayList<SummaryDateList>();
    private List<SummaryDateList> list = new ArrayList<SummaryDateList>();
    private int PagerIndex = 0;
    private ImageView no_information;
    private String OFUserName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        if (FourthView == null) {
            FourthView = inflater.inflate(R.layout.activity_office_allbusinesstripfragment, container, false);
            Company_ID = getActivity().getIntent().getIntExtra("Company_ID", 0);
            Manager_ID = getActivity().getIntent().getIntExtra("Manager_ID", 0);
            Department_ID = getActivity().getIntent().getIntExtra("Department_ID", 0);
            belongdate = getActivity().getIntent().getStringExtra("belongdate");
            ID = getActivity().getIntent().getIntExtra("ID", 0);
            index = getActivity().getIntent().getIntExtra("index", -1);
            belongdate = getDate(index - 3);
            getNetData(ID, belongdate, Manager_ID, Department_ID, 1, 10, 1);
            init();
        }
        return FourthView;
    }


    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView();
        if (null != FourthView) {
            ((ViewGroup) FourthView.getParent()).removeView(FourthView);
        }
    }

    /**
     * 初始化界面
     */
    private void init() {
        no_information = (ImageView) FourthView.findViewById(R.id.no_information);
        slistview = (MyXListView) FourthView.findViewById(R.id.businesstriplistview);
        adapter = new SummaryAdapter(getActivity(), list);
        slistview.setAdapter(adapter);
        slistview.setPullLoadEnable(true);
        slistview.setMyXListViewListener(this);


    }


    /**
     * 时间加减计算
     *
     * @param dayAddNum
     * @return
     */
    private String getDate(int dayAddNum) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date nowDate = null;
        try {
            nowDate = df.parse(belongdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date newDate2 = new Date(nowDate.getTime() - dayAddNum * 24 * 60 * 60 * 1000);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateOk = simpleDateFormat.format(newDate2);

        return dateOk;
    }


    private void getNetData(final int ID, final String BelongDate, final int Manager_ID,
                            final int Department_ID, final int pageIndex, final int pageSize, final int index) {
        new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                String result = DataUtil.callWebService(
                        Methods.GET_SUMMARY_LIST,
                        getJson(ID, BelongDate, Manager_ID, Department_ID,
                                pageIndex, pageSize, index));
                Log.e("TAGS", "result4:=" + result);
//                Log.e("TAGS", "result:=getjson" + getJson(ID, BelongDate, Manager_ID, Department_ID,
//                        pageIndex, pageSize, index));
                //先判断网络数据是否获取成功，防止网络不好导致程序崩溃
                if (result != null) {
                    // 获取对象的Type
                    Type type = new TypeToken<ZJResponse<SummaryDateList>>() {
                    }.getType();
                    ZJResponse<SummaryDateList> zjResponse = JsonUtil.fromJson(result,
                            type);
                    // 获取对象列表
                    itemLists.clear();
                    itemLists = zjResponse.getResults();
                    Log.e(TAG, "-----------111");
//					Log.e(TAG, "belongdate================>" + belongdate);
                    if (index == 1) {//刷新
                        mHandler.sendEmptyMessage(0);
                    } else if (index == 2) {//加载更多
                        mHandler.sendEmptyMessage(1);

                    } else if (index == 3) {
                        mHandler.sendEmptyMessage(1);
                    }

                } else {
                    mHandler.sendEmptyMessage(2);
                }
            }
        }.start();
    }


    private String getJson(int ID, String BelongDate, int Manager_ID,
                           int Department_ID, int pageIndex, int pageSize, int index) {

        ZJRequest zjRequest = new ZJRequest();
        if (index == 3) {
            zjRequest.setID(ID);
            zjRequest.setType(1);
            // 设置部门号，0时为相当没有部门编号查询
            zjRequest.setDepartment_ID(Department_ID);
            zjRequest.setBelongDate(BelongDate);
            zjRequest.setManager_ID(Manager_ID);
            // 设置页码，默认是1
            zjRequest.setPageIndex(pageIndex);
            // 设置当页显示条数
            zjRequest.setPageSize(pageSize);
        } else {
            // 公司id
//			zjRequest.setID(ID);//不要传id
            // 用户编号
            zjRequest.setManager_ID(Manager_ID);
            zjRequest.setBelongDate(BelongDate);
        }


        return JsonUtil.toJson(zjRequest);

    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0://刷新
                    list.clear();
                    for (int i = 0; i < itemLists.size(); i++) {
                        list.add(itemLists.get(i));
                    }
                    if (list.size() == 0) {
                        no_information.setVisibility(View.VISIBLE);
                    } else {
                        no_information.setVisibility(View.GONE);
                    }
                    adapter.notifyDataSetChanged();
                    break;
                case 1://加载更多

                    if (itemLists.size() <= 0) {
                        showDialog("没有更多数据！");

                    } else {

                        for (int i = 0; i < itemLists.size(); i++) {
                            list.add(itemLists.get(i));
                        }
                        adapter.notifyDataSetChanged();
                    }

                    break;
                case 2:
                    showDialog("未获得任何网络数据，请检查网络连接！");
                    break;

                default:
                    break;
            }
        }

        ;
    };

    /**
     * toast
     * 提醒方法
     */
    public void showDialog(String TXT) {
        new ClueCustomToast().showToast(getActivity(),
                R.drawable.toast_warn, TXT);

    }

    /**
     * 自定义适配器
     *
     * @author xsl
     */
    private class SummaryAdapter extends BaseAdapter {
        private LayoutInflater mInflater;//动态映射
        private Context context;
        private List<SummaryDateList> list;

        private SummaryAdapter(Context context, List<SummaryDateList> list) {
            this.context = context;
            this.mInflater = LayoutInflater.from(context);
            this.list = list;
        }

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
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder;
            String[] wroks_temp = list.get(position).getWorks().split("#");
            String[] plans_temp = list.get(position).getPlans().split("#");
            if (convertView == null) {

                convertView = mInflater.inflate(R.layout.activity_office_summary_detail_item, null);
                holder = new ViewHolder();
                holder.person_img = (ImageView) convertView.findViewById(R.id.person_img);
                holder.familyname = (TextView) convertView.findViewById(R.id.familyname);
                holder.todaylayout = (LinearLayout) convertView.findViewById(R.id.todaylayout);
                holder.belongdate = (TextView) convertView.findViewById(R.id.belongdate);
                holder.exprenice = (TextView) convertView.findViewById(R.id.exprenice);
                holder.tomorrowlayout = (LinearLayout) convertView.findViewById(R.id.tomorrowlayout);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.todaylayout.removeAllViews();
            for (int i = 0; i < wroks_temp.length; i++) {
                LinearLayout mLayout = (LinearLayout) mInflater.inflate(R.layout.activity_office_summary_my_note_item, null);
                TextView note = (TextView) mLayout.findViewById(R.id.note);
                String reallyValue = backString(wroks_temp[i].replace((i + 1) + "、", "-"));
                note.setText((i + 1) + "、" + reallyValue);
                holder.todaylayout.addView(mLayout);
            }

            holder.tomorrowlayout.removeAllViews();
            for (int i = 0; i < plans_temp.length; i++) {
                LinearLayout mLayout = (LinearLayout) mInflater.inflate(R.layout.activity_office_summary_my_note_item, null);
                TextView note = (TextView) mLayout.findViewById(R.id.note);
                String reallyValue = backString(plans_temp[i].replace((i + 1) + "、", "-"));
                note.setText((i + 1) + "、" + reallyValue);
                holder.tomorrowlayout.addView(mLayout);
            }
            holder.belongdate.setText("时间：" + backDate(list.get(position).getBelongDate()));
            holder.familyname.setText(list.get(position).getManager_Name());
            holder.exprenice.setText(list.get(position).getTips());
            final String person_url = Constant.pathurl +
                    Company_ID + "/Photo/" + list.get(position).getManager_Photo();
//			ImageLoad.displayImage(person_url, holder.person_img, R.drawable.person_img,10);
            //new ImageLoad().displayImage(getActivity(), person_url, holder.person_img, R.drawable.person_img, 10, false);
            ImageLoader.getInstance().displayImage(person_url, holder.person_img, MyApplication.getInstance().getOptions());
            //点击头像跳转到聊天界面
            holder.person_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createUserChat(list.get(position).getOFUserName(), 2, list.get(position).getManager_Name(), 0, person_url);
                    Log.e("TAGS","getOFUserName4="+list.get(position).getOFUserName());
                    Log.e("TAGS","getManager_Name4="+list.get(position).getManager_Name());
                }
            });
            return convertView;
        }

    }

    /**
     * 启动聊天
     *
     * @param JID
     * @param type
     * @param name
     * @param messtype
     * @param sSign    个性签名
     */
    private void createUserChat(String JID, int type, String name, int messtype, String sSign) {
        Intent intent = new Intent(getActivity(), Chat.class);
        intent.putExtra("JID", JID);
        intent.putExtra("type", type);
        intent.putExtra("name", name);
        intent.putExtra("messtype", messtype);
        intent.putExtra("chat_name", name);
        intent.putExtra("Sign", sSign);
        GotyeUser gotyeUser = new GotyeUser(JID);
        intent.putExtra("user", gotyeUser);
        startActivity(intent);
    }

    /**
     * 字符串分割
     *
     * @param str
     * @return
     */
    private String backString(String str) {
        String aString = "";
        String[] temper = null;
        temper = str.split("-");
        for (int i = 0; i < temper.length; i++) {
            aString = aString + temper[i];
        }
        return aString;

    }

    /**
     * listview容器
     *
     * @author xsl
     */
    static class ViewHolder {

        private ImageView person_img;//头像
        private TextView familyname; //名字
        private LinearLayout todaylayout;
        private TextView exprenice;//心得体会
        private LinearLayout tomorrowlayout;
        private TextView belongdate;//所属日期
    }

    /**
     * 传入后台接到的时间，返回我们需要的格式
     *
     * @param date 传入时间参数
     * @return
     */
    private String backDate(String date) {
        //这里包含0位但不包含5即（0，5】
        String a = date.substring(0, 4);
        String b = date.substring(5, 7);
        String c = date.substring(8, 10);
        String datesString = a + "-" + b + "-" + c;

        return datesString;
    }


    /**
     * 上拉刷新
     */
    public void onRefresh() {
        // TODO Auto-generated method stub
        mHandler.postDelayed(new Runnable() {
            public void run() {
                slistview.stopRefresh();
                slistview.stopLoadMore();
                slistview.setRefreshTime();
            }
        }, 2000);

//		getNetData(ID, belongdate, Manager_ID, Department_ID, 1, 10, 1);
    }

    /**
     * 下拉加载
     */
    public void onLoadMore() {
        // TODO Auto-generated method stub
        mHandler.postDelayed(new Runnable() {
            public void run() {
                slistview.stopRefresh();
                slistview.stopLoadMore();
                slistview.setRefreshTime();
            }
        }, 2000);

        if (!getActivity().getIntent().getStringExtra("mTpye").equals("1")) {
            PagerIndex += 1;
            getNetData(ID, belongdate, Manager_ID, Department_ID, PagerIndex, 10, 3);
        } else {
            showDialog("没有更多数据！");
        }

    }

}
