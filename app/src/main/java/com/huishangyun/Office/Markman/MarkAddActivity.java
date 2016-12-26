package com.huishangyun.Office.Markman;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Channel.Plan.CustomersListActivity;
import com.huishangyun.Channel.Plan.SortModel;
import com.huishangyun.Fragment.TimeFragment;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.TimeUtil;
import com.huishangyun.Util.ViewHolder;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.Util.webServiceHelp;
import com.huishangyun.model.Methods;
import com.huishangyun.yun.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Pan on 2015/8/6.
 */
public class MarkAddActivity extends BaseActivity{
    private EditText mUnitView;
    private EditText mNameView;
    private EditText mNumberView;
    private EditText mDepartView;//部门
    private TextView mTimeView;
    private EditText mSubjectView;
    private Button mSubmitBtn;
    private TimeFragment timeFragment;
    private final int TIME_TYPE = 0X00110;
    private long timeStamp = 0;
    private String time = "";
    private PopupWindow popupWindow;
    private List<MarkmanUnits> mList = new ArrayList<MarkmanUnits>();
    private UnitAdapter mUnitAdapter;
    private View popView;
    private ListView mListView;
    private webServiceHelp<MarkmanUnits> mSearchHelp;
    private webServiceHelp<MarkmanAdd> mSubmitHelp;
    private int hostID = 0;
    private String searchKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mrakman);
        initBackTitle("预约");
        initView();
        initNetWork();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSearchHelp.removeOnServiceCallBack();
        mSubmitHelp.removeOnServiceCallBack();
    }

    /**
     * 实例化组件
     */
    private void initView() {
        mUnitView = (EditText) findViewById(R.id.markman_add_unit);
        mNameView = (EditText) findViewById(R.id.markman_add_name);
        mNumberView = (EditText) findViewById(R.id.markman_add_number);
        mTimeView = (TextView) findViewById(R.id.markman_add_time);
        mSubjectView = (EditText) findViewById(R.id.markman_add_subject);
        mSubmitBtn = (Button) findViewById(R.id.markman_add_submit);
        mDepartView = (EditText) findViewById(R.id.markman_add_depart);
        //设置时间选择监听
        timeFragment = new TimeFragment();
        timeFragment.setChooseTime(true, MarkAddActivity.this);
        timeFragment.setIndex(new TimeFragment.TimeFace() {
            @Override
            public void chooseTime(String time, int type, long timeStamp) {
               /* if (!time.equals(TimeUtil.getInfoTime(System.currentTimeMillis()))) {
                    showCustomToast("请选择有效时间", false);
                    return;
                }*/
                try {
                    SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String time1 = sf1.format(new Date(System.currentTimeMillis()));//当前时间
                    Date d1 = sf1.parse(time1);
                    Date d2 = sf1.parse(time);
                    long diff1 = d2.getTime() - d1.getTime();
                    long days1 = diff1 / (1000 * 60 * 60 * 24);
                    if (days1 < 0) {
                        showCustomToast("请选择今天或以后的日期", false);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MarkAddActivity.this.time = time;
                MarkAddActivity.this.timeStamp = timeStamp;
                mTimeView.setText(time);
            }
        }, TIME_TYPE);

        //添加监听
        mTimeView.setOnClickListener(listener);
        mSubmitBtn.setOnClickListener(listener);
        mUnitView.addTextChangedListener(mTextWatcher);
    }

    /**
     * 实例化网络访问组件
     */
    private void initNetWork() {
        mSearchHelp = new webServiceHelp<MarkmanUnits>(Methods.HUISHANG_GET_CALLCOMPANYLIST,
                new TypeToken<ZJResponse<MarkmanUnits>>(){}.getType());
        mSearchHelp.setOnServiceCallBack(new webServiceHelp.OnServiceCallBack<MarkmanUnits>() {
            @Override
            public void onServiceCallBack(boolean haveCallBack, ZJResponse<MarkmanUnits> zjResponse) {
                if (haveCallBack) {
                    switch (zjResponse.getCode()) {
                        case 0:
                            List<MarkmanUnits> list = zjResponse.getResults();
                            mList.clear();
                            for (MarkmanUnits markmanUnits : list) {
                                mList.add(markmanUnits);
                            }
                            //显示结果
                            showResult();
                            break;
                        default:
                            break;
                    }
                }
            }
        });

        mSubmitHelp = new webServiceHelp<MarkmanAdd>(Methods.HUISHNAG_SET_CALL, new TypeToken<ZJResponse<MarkmanAdd>>(){}.getType());
        mSubmitHelp.setOnServiceCallBack(new webServiceHelp.OnServiceCallBack<MarkmanAdd>() {
            @Override
            public void onServiceCallBack(boolean haveCallBack, ZJResponse<MarkmanAdd> zjResponse) {
                dismissDialog();
                if (haveCallBack) {
                    switch (zjResponse.getCode()) {
                        case 0:
                            showCustomToast("提交成功!", true);
                            finish();
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
    }

    /**
     * 点击事件监听
     */
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.markman_add_time:
                    timeFragment.show(getSupportFragmentManager(), "dialog");
                    break;
                case R.id.markman_add_submit:
                    if (chekMessage()) {
                        showNotDialog("");
                        String json = getJson();
                        L.e("访问的json = " + json);
                        mSubmitHelp.start(json);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private String getJson() {
        ZJRequest<MarkmanAdd> zjRequest = new ZJRequest<MarkmanAdd>();
        MarkmanAdd markmanAdd = new MarkmanAdd();
        markmanAdd.setAction("Insert");
        markmanAdd.setHost_CompanyID(hostID);
        markmanAdd.setHost_Company(mUnitView.getText().toString().trim());
        markmanAdd.setHost_Department(mDepartView.getText().toString().trim());
        markmanAdd.setHost_Name(mNameView.getText().toString().trim());
        markmanAdd.setReserveTime(mTimeView.getText().toString().trim());
        markmanAdd.setNote(mSubjectView.getText().toString().trim());
        markmanAdd.setCompany_ID(MyApplication.getInstance().getCompanyID());
        markmanAdd.setHost_Mobile(mNumberView.getText().toString().trim());
        markmanAdd.setGuest_ID(MyApplication.getInstance().getManagerID());
        zjRequest.setData(markmanAdd);
        return JsonUtil.toJson(zjRequest);
    }

    /**
     * 检查是否填写完成
     * @return
     */
    private boolean chekMessage() {
        if (mUnitView.getText().toString().trim().equals("") || hostID == 0) {
            showCustomToast("请选择拜访单位", false);
            return false;
        } else if (mDepartView.getText().toString().trim().equals("")) {
            showCustomToast("请填写拜访部门", false);
            return false;
        } else if (mNameView.getText().toString().trim().equals("")) {
            showCustomToast("请填写拜访人姓名", false);
            return false;
        } else if (!isMobileNO(mNumberView.getText().toString().trim())) {
            showCustomToast("请填写正确的手机号码", false);
            return false;
        } else if (mTimeView.getText().toString().trim().equals("")) {
            showCustomToast("请选择拜访时间", false);
            return false;
        } else if (mSubjectView.getText().toString().trim().equals("")) {
            showCustomToast("请填写拜访事由", false);
            return false;
        }
        return true;
    }

    /**
     * 文字输入监听
     */
    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (popupWindow != null) {
                popupWindow.dismiss();
            }
            if (!TextUtils.isEmpty(s) && !searchKey.equals(s.toString())) {
                //开始搜索
                searchKey = s.toString();
                ZJRequest zjRequest = new ZJRequest();
                zjRequest.setKeywords(s.toString());
                mSearchHelp.start(JsonUtil.toJson(zjRequest));

            }
        }
    };

    /**
     * 显示弹出框
     */
    private void showResult() {
        if (popupWindow == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            popView = layoutInflater.inflate(R.layout.markman_pop, null);
            mListView = (ListView) popView.findViewById(R.id.markman_pop_list);
            popupWindow = new PopupWindow(popView);
        }
        mUnitAdapter = new UnitAdapter();
        mListView.setAdapter(mUnitAdapter);
        // 使其聚集
        popupWindow.setFocusable(false);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setWidth(mUnitView.getWidth());
        popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);

        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAsDropDown(mUnitView);
        //点击事件监听
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchKey = mList.get(position).getName().trim();
                mUnitView.setText(mList.get(position).getName().trim());
                hostID = mList.get(position).getID();
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
            }
        });
    }

    /**
     * 控件适配器
     */
    private class UnitAdapter extends BaseAdapter{

        @Override
        public int getCount() {
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
            if (convertView == null) {
                convertView = LayoutInflater.from(MarkAddActivity.this).inflate(R.layout.markman_pop_item, null);
            }
            TextView nameView = ViewHolder.get(convertView, R.id.markman_add_pop_txt);
            nameView.setText(mList.get(position).getName().trim());
            return convertView;
        }
    }

    /**
     * 判断输入手机号码是否有效
     * @param number
     * @return
     */
    private boolean isMobileNO(String number) {
        String telRegex = "[1][3458]\\d{9}";//"[1]"代表第1位为数字1，"[3458]"代表第二位可以为3、4、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(number)) return false;
        else return number.matches(telRegex);
    }

}
