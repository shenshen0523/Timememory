package com.hfnu.zl.ui.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.hfnu.zl.R;
import com.hfnu.zl.database.DaoMaster;
import com.hfnu.zl.database.Schedule;
import com.hfnu.zl.database.ScheduleDao;
import com.hfnu.zl.tool.Tool;
import com.hfnu.zl.ui.widget.MySelectView;

import java.util.Arrays;

/**
 * 添加日程界面 继承于AddBaseActivity拥有AddBaseActivity属性和方法 并实现点击监听接口
 */
public class AddScheduleActivity extends AddBaseActivity implements View.OnClickListener {
    private Bundle bundle;
    private EditText schedule_name, et_remark;
    private MySelectView tv_mood_select, tv_time_select;
    private Button btn_save;
    private ScheduleDao scheduleDao;
    private Schedule schedule;
    private LinearLayout.LayoutParams dateLayoutParams;
    private String timer;
    private boolean isEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContentView(R.layout.activity_add_schedule);
        //setContentView(R.layout.activity_add_schedule);
    }

    /**
     * 以下三个方法作用不在赘述 参考AddDiaryActivity类
     */
    @Override
    void bindView() {
        toolbar.setTitle(bundle.getInt("title", R.string.app_name));
        schedule_name = Tool.findViewById(this, R.id.schedule_name);
        et_remark = Tool.findViewById(this, R.id.et_remark);
        tv_mood_select = Tool.findViewById(this, R.id.tv_mood_select);
        tv_mood_select.setData(Arrays.asList(getResources().getStringArray(R.array.mood_type_array)));
        tv_mood_select.setLastAloneHandle(false);
        tv_time_select = Tool.findViewById(this, R.id.tv_time_select);
        tv_time_select.setOnClickListener(this);
        btn_save = Tool.findViewById(this, R.id.btn_save);
        btn_save.setOnClickListener(this);
    }

    @Override
    void initViewBefore() {
        dateLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dateLayoutParams.setMargins(0, Tool.dip2px(this, 8), 0, 0);
        bundle = getIntent().getExtras();
        scheduleDao = DaoMaster.getDaoSession(this).getScheduleDao();
    }

    @Override
    void initViewLater() {
        schedule = (Schedule) bundle.getSerializable("data");
        if (schedule == null)
            schedule = new Schedule();
        else {
            isEdit = true;
            schedule_name.setText(schedule.getScheduleName());
            tv_mood_select.setText(schedule.getMood());
            et_remark.setText(schedule.getDetailContent().equals("无") ? "" : schedule.getDetailContent());
            tv_time_select.setText(Tool.formattingDetailDate(schedule.getWriteTime()));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_time_select:
                //显示时间选择对话框
                AlertDialog.Builder dateDialog = new AlertDialog.Builder(this);
                final DatePicker datePicker = new DatePicker(this);//此日期选择器为系统自带日期选择器 在不同的安卓版本上显示的效果不同，安卓版本越高效果越好
                dateDialog.setView(datePicker);
                dateDialog.setNegativeButton(R.string.str_cancel, new MyNegativeOnClick());
                dateDialog.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        timer = datePicker.getYear() + "年" + (datePicker.getMonth() + 1) + "月" + datePicker.getDayOfMonth() + "日";
                        dialog.cancel();
                        dialog.dismiss();
                        /*
                        日期选择好以后显示时间选择对话框
                         */
                        AlertDialog.Builder timerDialog = new AlertDialog.Builder(AddScheduleActivity.this);
                        final TimePicker timePicker = new TimePicker(AddScheduleActivity.this);//此时间选择器为系统自带时间选择器 在不同的安卓版本上显示的效果不同，安卓版本越高效果越好
                        timerDialog.setView(timePicker);
                        timerDialog.setNegativeButton(R.string.str_cancel, new MyNegativeOnClick());
                        timerDialog.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int h = timePicker.getCurrentHour();
                                int M = timePicker.getCurrentMinute();
                                timer += " " + (h < 10 ? "0" + h : h) + ":" + (M < 10 ? "0" + M : M);
                                tv_time_select.setText(timer);
                                dialog.cancel();
                                dialog.dismiss();
                            }
                        });
                        timerDialog.create().show();
                    }
                });
                dateDialog.create().show();

                break;
            case R.id.btn_save:
                //保存日程对象
                String scheduleName = schedule_name.getText().toString().trim();
                if (TextUtils.isEmpty(scheduleName)) {
                    Toast.makeText(this, R.string.str_schedule_name_is_the_null, Toast.LENGTH_SHORT).show();
                } else {
                    schedule.setScheduleName(scheduleName);
                    schedule.setWriteTime(Tool.formattingDateString(tv_time_select.getText().toString().trim()));
                    schedule.setMood(tv_mood_select.getText().toString().trim());
                    String remark = et_remark.getText().toString().trim();
                    if (TextUtils.isEmpty(remark)) {
                        remark = "无";
                    }
                    schedule.setDetailContent(remark);
                    if (isEdit) {
                        scheduleDao.update(schedule);
                    } else
                        scheduleDao.insert(schedule);
                    setResult(MainActivity.HAVE_A_VALUE);
                    onBackPressed();
                }
                break;
        }

    }

    /**
     * 时间和日期选择对话框公用Negative按钮响应时间
     */
    private class MyNegativeOnClick implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            dialog.dismiss();
        }
    }
}
