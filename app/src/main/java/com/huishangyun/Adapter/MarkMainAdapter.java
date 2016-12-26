package com.huishangyun.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huishangyun.App.MyApplication;
import com.huishangyun.Office.Markman.MarkManBean;
import com.huishangyun.Util.TimeUtil;
import com.huishangyun.model.CallList;
import com.huishangyun.yun.R;

import java.util.List;

/**
 * Created by pan on 2015/8/4.
 */
public class MarkMainAdapter extends BaseAdapter{

    private List<MarkManBean> mList;
    private Context mContext;
    private LayoutInflater mInflater;

    public MarkMainAdapter(Context mContext, List<MarkManBean> mList) {
        this.mContext = mContext;
        this.mList = mList;
        mInflater = LayoutInflater.from(mContext);
    }

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
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_markmain, null);
            holder.leftView = (View) convertView.findViewById(R.id.item_markmain_left_view);
            holder.time = (TextView) convertView.findViewById(R.id.item_markmain_time);
            holder.title = (TextView) convertView.findViewById(R.id.item_markmain_title);
            holder.bottomView = (View) convertView.findViewById(R.id.item_markmain_bottom_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        MarkManBean callList = mList.get(position);
        //判断是否隐藏底部分割线
        if (position == mList.size() - 1) {
            holder.bottomView.setVisibility(View.GONE);
        } else {
            holder.bottomView.setVisibility(View.VISIBLE);
        }
        if (callList.getGuest_ID() == MyApplication.getInstance().getManagerID())
            holder.leftView.setBackgroundColor(0xfffb7250);
        else
            holder.leftView.setBackgroundColor(0xff78a9f1);
        holder.title.setText(callList.getGuest_Name() + "来访");
        holder.time.setText(TimeUtil.getTimeOfDay(callList.getReserveTime()));
        return convertView;
    }

    static class ViewHolder {
        public View leftView;
        public TextView time;
        public TextView title;
        public View bottomView;
    }
}
