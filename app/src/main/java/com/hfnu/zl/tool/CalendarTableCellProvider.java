package com.hfnu.zl.tool;

import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;

import com.hfnu.zl.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CalendarTableCellProvider {

    private long firstDayMillis = 0;
    private int solarTerm1 = 0;
    private int solarTerm2 = 0;
    private DateFormatter fomatter;//得到农历的格式化数据

    /**
     * @param resources  资源
     * @param monthIndex 月份所在的pager页
     */
    public CalendarTableCellProvider(Resources resources, int monthIndex) {
        int year = LunarCalendar.getMinYear() + (monthIndex / 12);//得到月份pager页所在的年份
        int month = monthIndex % 12;//得到月份pager页的真实月份
        Calendar date = new GregorianCalendar(year, month, 1);//得到所在月份的第一天的日历
        int offset = 1 - date.get(Calendar.DAY_OF_WEEK);//得到当天所在星期的偏移量
        date.add(Calendar.DAY_OF_MONTH, offset);
        firstDayMillis = date.getTimeInMillis();
        solarTerm1 = LunarCalendar.getSolarTerm(year, month * 2 + 1);//获得节气
        solarTerm2 = LunarCalendar.getSolarTerm(year, month * 2 + 2);
        fomatter = new DateFormatter(resources);
    }

    public View getView(int position, LayoutInflater inflater, ViewGroup container) {//获取日期(天)的View
        ViewGroup rootView;
        long milliSecondsGregorian = firstDayMillis +
                (position - (position / 8) - 1) * LunarCalendar.DAY_MILLIS;
        LunarCalendar date = new LunarCalendar(milliSecondsGregorian);
        // 周的年序号
        if (position % 8 == 0) {//返回周的年序号view
            rootView = (ViewGroup) inflater.inflate(R.layout.view_calendar_week_index, container, false);
            rootView.setLayoutParams(new LayoutParams(0, 0));//默认不显示周是当年的第几周
            /*TextView txtWeekIndex = (TextView)rootView.findViewById(R.id.txtWeekIndex);
			txtWeekIndex.setText(String.valueOf(date.getGregorianDate(Calendar.WEEK_OF_YEAR)));*/
            return rootView;
        }
        // 开始日期处理
        boolean isFestival = false, isSolarTerm = false;//节气
        rootView = (ViewGroup) inflater.inflate(R.layout.view_calendar_day_cell, container, false);
        rootView.setLayoutParams(new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT));
        TextView txtCellGregorian = (TextView) rootView.findViewById(R.id.txtCellGregorian);//阳历
        TextView txtCellLunar = (TextView) rootView.findViewById(R.id.txtCellLunar);//农历
        int gregorianDay = date.getGregorianDate(Calendar.DAY_OF_MONTH);
        // 判断是否为本月日期
        boolean isOutOfRange = ((position % 8 != 0) &&
                (position < 8 && gregorianDay > 7) || (position > 8 && gregorianDay < position - 7 - 6));
        txtCellGregorian.setText(String.valueOf(gregorianDay));
        //优先级 优先显示哪个标记
        // 农历节日 > 公历节日 > 农历月份 > 二十四节气 > 农历日
        int index = date.getLunarFestival();
        if (index >= 0) {
            // 农历节日
            txtCellLunar.setText(fomatter.getLunarFestivalName(index));//获得节日名称
            isFestival = true;
        } else {
            index = date.getGregorianFestival();
            if (index >= 0) {
                // 公历节日
                txtCellLunar.setText(fomatter.getGregorianFestivalName(index));
                isFestival = true;
            } else if (date.getLunar(LunarCalendar.LUNAR_DAY) == 1) {
                // 初一,显示月份
                txtCellLunar.setText(fomatter.getMonthName(date));
            } else if (!isOutOfRange && gregorianDay == solarTerm1) {
                // 节气1
                txtCellLunar.setText(fomatter.getSolarTermName(date.getGregorianDate(Calendar.MONTH) * 2));
                isSolarTerm = true;
            } else if (!isOutOfRange && gregorianDay == solarTerm2) {
                // 节气2
                txtCellLunar.setText(fomatter.getSolarTermName(date.getGregorianDate(Calendar.MONTH) * 2 + 1));
                isSolarTerm = true;
            } else {
                txtCellLunar.setText(fomatter.getDayName(date));
            }
        }

        // set style
        Resources resources = container.getResources();
        if (isOutOfRange) {//是否超出本月限制
            rootView.setFocusable(false);
            rootView.setFocusableInTouchMode(false);
            rootView.setBackgroundColor(Color.parseColor("#00000000"));
            txtCellGregorian.setTextColor(txtCellGregorian.getContext().getResources().getColor(R.color.textColorHint));
            txtCellLunar.setTextColor(txtCellGregorian.getTextColors());
        }/*else if(isFestival){//是否是节日
			txtCellLunar.setTextColor(resources.getColor(R.color.colorPrimary));
		}else if(isSolarTerm){//是否是节气
			txtCellLunar.setTextColor(resources.getColor(R.color.colorPrimary));
		}
		if (position % 8 == 1 || position % 8 == 7){//设置星期天和星期六德背景颜色
			rootView.setBackgroundResource(R.drawable.shape_calendar_cell_weekend);
		}*/
        if (date.isToday()&&!isOutOfRange) {//是否是今天
            rootView.setBackgroundResource(R.drawable.shape_calendar_cell_today);//设置今天德背景颜色
            txtCellGregorian.setTextColor(Color.WHITE);
            txtCellLunar.setTextColor(Color.WHITE);
        }
        rootView.setTag(date);
        rootView.setTag(R.id.tag_date,new Date(milliSecondsGregorian));
        return rootView;
    }

}

