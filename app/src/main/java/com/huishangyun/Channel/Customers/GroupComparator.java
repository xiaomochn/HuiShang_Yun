package com.huishangyun.Channel.Customers;

import java.util.Comparator;

public class GroupComparator implements Comparator<GroupModel>{

	@Override
	public int compare(GroupModel lhs, GroupModel rhs) {
		// TODO Auto-generated method stub
		if (lhs.getPinyin().equals("@")
				|| rhs.getPinyin().equals("#")) {
			return 1;
		} else if (lhs.getPinyin().equals("#")
				|| rhs.getPinyin().equals("@")) {
			return -1;
		} else {
			return lhs.getPinyin().compareTo(rhs.getPinyin());
		}
	}

}
