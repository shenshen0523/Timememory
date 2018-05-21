package com.hfnu.zl.ui.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.hfnu.zl.tool.LunarCalendar;
import com.hfnu.zl.ui.fragment.CalendarPagerFragment;

/**
 * 日历adapter适配器
 */
public class CalendarPagerAdapter extends FragmentPagerAdapter {

	public CalendarPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		return CalendarPagerFragment.create(position);
	}

	@Override
	public int getCount() {
		int years = LunarCalendar.getMaxYear() - LunarCalendar.getMinYear();//一共多少年
		return years * 12;//年数x12 一共有多少个月月的数量也就是pager页的数量
	}

}
