package com.huishangyun.Util;

import android.util.SparseArray;
import android.view.View;

/**
 * Created by Pan on 2015/8/11.
 * ViewHolder优化类
 */
public class ViewHolder {

    /**
     *
     * @param convertView
     * @param id
     * @param <T>
     * @return View v = ViewHolder.get(convertView, v.id);
     */
    public static <T extends View> T get(View convertView, int id) {
        SparseArray<View> viewHolder = (SparseArray<View>) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray<View>();
            convertView.setTag(viewHolder);
        }
        View childView = viewHolder.get(id);
        if (childView == null) {
            childView = convertView.findViewById(id);
            viewHolder.put(id, childView);
        }
        return (T) childView;
    }
}
