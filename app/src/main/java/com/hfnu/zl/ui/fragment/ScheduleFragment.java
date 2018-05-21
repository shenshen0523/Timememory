package com.hfnu.zl.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hfnu.zl.R;
import com.hfnu.zl.database.DaoMaster;
import com.hfnu.zl.database.Schedule;
import com.hfnu.zl.database.ScheduleDao;
import com.hfnu.zl.tool.Tool;
import com.hfnu.zl.ui.activity.AddScheduleActivity;
import com.hfnu.zl.ui.activity.MainActivity;
import com.hfnu.zl.ui.adapter.ListViewHolder;
import com.hfnu.zl.ui.adapter.OmnipotentBaseAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 日程fragment  实现BaseFragment接口
 * 具体方法作用查看AccountBookFragment
 */

public class ScheduleFragment extends BaseFragment {
    private ScheduleDao scheduleDao;
    private List<Schedule> scheduleList;
    private OmnipotentBaseAdapter<Schedule> adapter;

    private OmnipotentBaseAdapter<String[]> detailsAdapter;
    private ListView detailsListView;
    private List<String[]> detailsItemName;

    @Override
    void initData() {
        scheduleDao = DaoMaster.getDaoSession(getActivity()).getScheduleDao();
        scheduleList = new ArrayList<>();
        scheduleList.addAll(scheduleDao.queryBuilder().orderDesc(ScheduleDao.Properties.WriteTime).build().list());
        adapter = new OmnipotentBaseAdapter<Schedule>(getActivity(), scheduleList, R.layout.schedule_item_view) {
            int[] moodBack = new int[]{R.drawable.back_gaoxing, R.drawable.back_haipa, R.drawable.back_han,
                    R.drawable.back_yun, R.drawable.back_nu, R.drawable.back_ku, R.drawable.back_daku, R.drawable.back_buxie};
            List<String> moodType = Arrays.asList(getResources().getStringArray(R.array.mood_type_array));

            @Override
            public void convert(ListViewHolder viewHolder, Schedule schedule) {
                viewHolder.setText(R.id.tv_schedule_name, schedule.getScheduleName());
                viewHolder.setText(R.id.tv_timer, Tool.formattingDetailDate(schedule.getWriteTime()));
                viewHolder.setText(R.id.tv_mood, schedule.getMood());
                viewHolder.setBackgroundRes(R.id.tv_mood, moodBack[moodType.indexOf(schedule.getMood())]);
            }
        };
        listView.setAdapter(adapter);
        // listView.setOnItemLongClickListener(this);
        if (scheduleList.isEmpty()) {
            isShowNullView(true);
        }
    }

    @Override
    public void notifyDataSetChanged() {
        getListData();
    }

    private void getListData() {
        scheduleList.clear();
        scheduleList.addAll(scheduleDao.queryBuilder().orderDesc(ScheduleDao.Properties.WriteTime).build().list());
        adapter.notifyDataSetChanged();
        if (scheduleList.isEmpty()) {
            isShowNullView(true);
        }else{
            isShowNullView(false);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        showDetailsDialog(scheduleList.get(position));
    }

    private void showDetailsDialog(final Schedule schedule) {
        if (detailsAdapter == null) {
            detailsItemName = new ArrayList<>();
            detailsAdapter = new OmnipotentBaseAdapter<String[]>(getActivity(), detailsItemName, R.layout.account_book_details_item) {
                @Override
                public void convert(ListViewHolder viewHolder, String[] array) {
                    viewHolder.setText(R.id.item_name, array[0]).setText(R.id.item_content, array[1]);
                }
            };
        }
        detailsItemName.clear();
        String[] schedule_details_item_name_array = getResources().getStringArray(R.array.schedule_details_item_name_array);
        for (int i = 0; i < schedule_details_item_name_array.length; i++) {
            String[] itemData = new String[2];
            itemData[0] = schedule_details_item_name_array[i];
            detailsItemName.add(itemData);
        }
        detailsItemName.get(0)[1] = schedule.getScheduleName();
        detailsItemName.get(1)[1] = Tool.formattingDetailDate(schedule.getWriteTime());
        detailsItemName.get(2)[1] = schedule.getMood();
        detailsItemName.get(3)[1] = schedule.getDetailContent();
        detailsAdapter.notifyDataSetChanged();
        detailsListView = new ListView(getActivity());
        detailsListView.setDivider(null);
        detailsListView.setPadding(0, Tool.dip2px(getActivity(), 8), 0, 0);
        detailsListView.setAdapter(detailsAdapter);
        AlertDialog.Builder detailsDialog = new AlertDialog.Builder(getActivity());
        detailsDialog.setTitle(R.string.str_schedule_details);
        detailsDialog.setView(detailsListView);
        detailsDialog.setNegativeButton(R.string.str_cancel, new ScheduleFragment.MtyDialogOnClickListener());
        detailsDialog.setPositiveButton(R.string.str_edit, new ScheduleFragment.MtyDialogOnClickListener(schedule));
        detailsDialog.setNeutralButton(R.string.str_delete, new ScheduleFragment.MtyDialogOnClickListener(schedule));
        detailsDialog.create().show();
    }

    /**
     * 账单详情对话框按钮的点击事件
     */
    private class MtyDialogOnClickListener implements DialogInterface.OnClickListener {
        private Schedule schedule;

        public MtyDialogOnClickListener() {}

        public MtyDialogOnClickListener(Schedule schedule) {
            this.schedule = schedule;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_NEUTRAL:
                    scheduleDao.delete(schedule);
                    notifyDataSetChanged();
                    break;
                case DialogInterface.BUTTON_POSITIVE:
                    Intent intent = new Intent(getActivity(), AddScheduleActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("title", R.string.str_edit_schedule);
                    bundle.putSerializable("data", schedule);
                    intent.putExtras(bundle);
                    getActivity().startActivityForResult(intent, MainActivity.RB_SCHEDULE);
                    break;
            }
            dialog.cancel();
            dialog.dismiss();
        }
    }
}
