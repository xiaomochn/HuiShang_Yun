package com.huishangyun.Util;

import java.util.Comparator;

import com.huishangyun.model.ChartHisBean;

public class ComparatorNoticeUtil implements Comparator{

	@Override
	public int compare(Object lhs, Object rhs) {
		// TODO Auto-generated method stub
		ChartHisBean notice1 = (ChartHisBean) lhs;
		ChartHisBean notice2 = (ChartHisBean) rhs;
		return notice2.getNoticeTime().compareTo(notice1.getNoticeTime());
	}

}
