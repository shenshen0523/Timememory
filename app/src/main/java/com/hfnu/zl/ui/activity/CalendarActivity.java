package com.hfnu.zl.ui.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;

import com.hfnu.zl.R;
import com.hfnu.zl.tool.DateFormatter;
import com.hfnu.zl.tool.LunarCalendar;
import com.hfnu.zl.tool.Tool;
import com.hfnu.zl.ui.adapter.CalendarPagerAdapter;

import java.util.Calendar;
import java.util.Date;

/**
 * 日历activity
 */
public class CalendarActivity extends AppCompatActivity implements
        View.OnFocusChangeListener,DatePickerDialog.OnDateSetListener,View.OnClickListener {
    private ViewPager viewPager;
    private DateFormatter formatter;//农历时间格式化器
    private PagerAdapter mPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        initData();
        bindView();
    }
    private void initData(){
        formatter = new DateFormatter(this.getResources());
    }
    private void bindView() {
        Toolbar toolbar = Tool.findViewById(this,R.id.toolbar);
        CharSequence[] info =formatter.getFullDateInfo(new LunarCalendar());//获取全部日期信息 下标0为节气或节日 下标1为农历的名称如二零一七年四月初七 丁酉年 甲辰日 乙丑日
        toolbar.setTitle(Tool.formattingConciseDate(new Date()));
        toolbar.setSubtitle(info[1]);
        toolbar.setNavigationIcon(R.drawable.home_as_up_indicator);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        viewPager = Tool.findViewById(this,R.id.calendar_content);
        mPagerAdapter = new CalendarPagerAdapter(getFragmentManager());
        viewPager.setAdapter(mPagerAdapter);
        viewPager.addOnPageChangeListener(new SimplePageChangeListener());
        viewPager.setCurrentItem(getTodayMonthIndex());
    }

    private int getTodayMonthIndex() {//得到最开始年份到现在有多少个月
        Calendar today = Calendar.getInstance();
        int offset = (today.get(Calendar.YEAR) - LunarCalendar.getMinYear())
                * 12 + today.get(Calendar.MONTH);
        return offset;
    }
    // 日期单元格点击事件
    public void onCellClick(View v) {
       /* Toast.makeText(this, v.getTag().toString(), Toast.LENGTH_SHORT).show();
        Log.i("cellClick", v.getTag().toString());*/
    }
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus)
            return;
        LunarCalendar lc = (LunarCalendar) v.getTag();
        getSupportActionBar().setTitle(Tool.formattingConciseDate((Date) v.getTag(R.id.tag_date)));
        getSupportActionBar().setSubtitle(formatter.getFullDateInfo(lc)[1]);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        int offset = (year - LunarCalendar.getMinYear()) * 12 + month;
        viewPager.setCurrentItem(offset);
    }

    @Override
    public void onClick(View v) {

    }

    // 月份显示切换事件
    private class SimplePageChangeListener extends ViewPager.SimpleOnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
            StringBuilder title = new StringBuilder();
            title.append(LunarCalendar.getMinYear() + (position / 12));
            title.append('年');
            int month = (position % 12) + 1;
            if (month < 10) {
                title.append('0');
            }
            title.append(month);
            title.append("月");
            getSupportActionBar().setTitle(title.toString());
           // txtTitleGregorian.setText(title);
            // set related button's state
           /* if (position < mPagerAdapter.getCount() - 1
                    && !imgNextMonth.isEnabled()) {
                imgNextMonth.setEnabled(true);
            }
            if (position > 0 && !imgPreviousMonth.isEnabled()) {
                imgPreviousMonth.setEnabled(true);
            }*/
        }
    }
}
