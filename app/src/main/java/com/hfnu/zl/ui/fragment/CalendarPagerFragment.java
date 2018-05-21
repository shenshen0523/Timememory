package com.hfnu.zl.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.hfnu.zl.R;
import com.hfnu.zl.tool.CalendarTableCellProvider;

/**
 * 日历fragment
 */
public class CalendarPagerFragment extends Fragment {

    public static final String ARG_PAGE = "page";

    private int mMonthIndex;//当前月份所在的pager页
    /*
    使用静态方式创建fragment
     */
    public static CalendarPagerFragment create(int monthIndex) {
        CalendarPagerFragment fragment = new CalendarPagerFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, monthIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMonthIndex = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TableRow tableRow;
        View cellView;
        TableLayout tableView = (TableLayout) inflater.inflate(R.layout.view_calendar_table, container, false);//单行列表View
        //得到日历的某个是日期的View
        CalendarTableCellProvider adpt = new CalendarTableCellProvider(getResources(), mMonthIndex);
        for (int row = 0; row < 6; row++) {//创建6行
            tableRow = new TableRow(tableView.getContext());//得到一行
            for (int column = 0; column < 8; column++) {//创建8列
                cellView = adpt.getView(row * 8 + column, inflater, tableRow);//得到某一天的View
                cellView.setOnFocusChangeListener((View.OnFocusChangeListener) container.getContext());
                tableRow.addView(cellView);
            }
            tableView.addView(tableRow);
        }
        return tableView;
    }
}

