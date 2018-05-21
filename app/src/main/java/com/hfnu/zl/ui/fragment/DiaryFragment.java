package com.hfnu.zl.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import com.hfnu.zl.R;
import com.hfnu.zl.database.DaoMaster;
import com.hfnu.zl.database.Diary;
import com.hfnu.zl.database.DiaryDao;
import com.hfnu.zl.tool.Tool;
import com.hfnu.zl.ui.activity.AddDiaryActivity;
import com.hfnu.zl.ui.activity.MainActivity;
import com.hfnu.zl.ui.adapter.ListViewHolder;
import com.hfnu.zl.ui.adapter.OmnipotentBaseAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * 日记展示列表fragment 实现BaseFragment接口
 */

public class DiaryFragment extends BaseFragment implements AdapterView.OnItemLongClickListener {
    private DiaryDao diaryDao;
    private List<Diary> diaryList;
    private OmnipotentBaseAdapter<Diary> adapter;
    private EditText passWordEd;//dialog的密码输入框
    private AlertDialog alertDialog;//密码输入框dialog实例

    @Override
    void initData() {
        diaryDao = DaoMaster.getDaoSession(getActivity()).getDiaryDao();
        diaryList = new ArrayList<>();
        diaryList.addAll(diaryDao.queryBuilder().orderDesc(DiaryDao.Properties.WriteTime).build().list());
        adapter = new OmnipotentBaseAdapter<Diary>(getActivity(), diaryList, R.layout.diary_item_view) {
            String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
            int[] moodBack = new int[]{R.drawable.back_gaoxing, R.drawable.back_haipa, R.drawable.back_han,
                    R.drawable.back_yun, R.drawable.back_nu, R.drawable.back_ku, R.drawable.back_daku, R.drawable.back_buxie};//心情背景集合
            List<String> moodType = Arrays.asList(getResources().getStringArray(R.array.mood_type_array));

            @Override
            public void convert(ListViewHolder viewHolder, Diary diary) {
                viewHolder.setText(R.id.tv_type, diary.getDiaryType());
                viewHolder.setText(R.id.tv_mood, diary.getMood());
                viewHolder.setBackgroundRes(R.id.tv_mood, moodBack[moodType.indexOf(diary.getMood())]);
                if (diary.getPassWord() == null || TextUtils.isEmpty(diary.getPassWord())) {
                    viewHolder.setText(R.id.tv_content, diary.getContent());
                    viewHolder.setViewVisibility(R.id.iv_pass_word, View.GONE);
                } else {
                    viewHolder.setText(R.id.tv_content, R.string.str_diary_is_set_pass_word);
                    viewHolder.setViewVisibility(R.id.iv_pass_word, View.VISIBLE);
                }
                viewHolder.setViewVisibility(R.id.iv_pic, diary.getPicture() == null || diary.getPicture().isEmpty() ? View.GONE : View.VISIBLE);
                viewHolder.setViewVisibility(R.id.iv_record, diary.getRecord() == null || diary.getRecord().isEmpty() ? View.GONE : View.VISIBLE);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(diary.getWriteTime());
                viewHolder.setText(R.id.tv_year_month, calendar.get(Calendar.YEAR) + "年" + (calendar.get(Calendar.MONTH) + 1) + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日");
                //viewHolder.setText(R.id.tv_day,calendar.get(Calendar.DAY_OF_MONTH)+""+);
                int minute = calendar.get(Calendar.MINUTE);
                viewHolder.setText(R.id.tv_time, weekDays[calendar.get(Calendar.DAY_OF_WEEK)] + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + (minute < 10 ? "0" + minute : minute));
            }
        };
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(this);//添加item点击监听事件
        if (diaryList.isEmpty()) {
            isShowNullView(true);
        }
    }

    @Override
    public void notifyDataSetChanged() {
        getListData();
    }

    /**
     * 获取list数据
     */
    private void getListData() {
        diaryList.clear();
        diaryList.addAll(diaryDao.queryBuilder().orderDesc(DiaryDao.Properties.WriteTime).build().list());
        adapter.notifyDataSetChanged();
        if (diaryList.isEmpty()) {
            isShowNullView(true);
        } else {
            isShowNullView(false);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Diary diary = diaryList.get(position);
        if (diary.getPassWord() == null || TextUtils.isEmpty(diary.getPassWord())) {
            showDiaryDetails(diary);
        } else {
            inputDiaryPassWordDialog(R.string.str_check_the_diary, R.string.str_ok, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String inputPassWord = passWordEd.getText().toString().trim();
                    if (TextUtils.isEmpty(inputPassWord)) {
                        Toast.makeText(getActivity(), "密码不能为空", Toast.LENGTH_SHORT).show();
                    } else if (inputPassWord.equals(diary.getPassWord())) {
                        showDiaryDetails(diary);
                        alertDialog.cancel();
                        alertDialog.dismiss();
                    } else {
                        Toast.makeText(getActivity(), R.string.str_pass_word_error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            /*AlertDialog.Builder putPassWordDialog = new AlertDialog.Builder(getActivity());
            putPassWordDialog.setTitle("查看日记");
            View passwordView = getActivity().getLayoutInflater().inflate(R.layout.manage_password_view, null);
            passwordView.setPadding(Tool.dip2px(getActivity(), 8), Tool.dip2px(getActivity(), 16), Tool.dip2px(getActivity(), 8), 0);
            passWordEd = Tool.findViewById(passwordView, R.id.et_user_password);
            putPassWordDialog.setView(passwordView);
            putPassWordDialog.setPositiveButton(R.string.str_ok, null);//设置确认按钮，点击事件设置为空这样对话框就不会关闭
            putPassWordDialog.setNegativeButton(R.string.str_cancel, new DialogInterface.OnClickListener() {//设置不设置密码按钮
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    dialog.dismiss();
                }
            });
            alertDialog = putPassWordDialog.create();
            alertDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {//设置确认按钮点击事件
                @Override
                public void onClick(View v) {
                    String inputPassWord = passWordEd.getText().toString().trim();
                    if (TextUtils.isEmpty(inputPassWord)) {
                        Toast.makeText(getActivity(), "密码不能为空", Toast.LENGTH_SHORT).show();
                    } else if(inputPassWord.equals(diary.getPassWord())){
                        showDiaryDetails(diary);
                        alertDialog.cancel();
                        alertDialog.dismiss();
                    }else{
                        Toast.makeText(getActivity(), R.string.str_pass_word_error, Toast.LENGTH_SHORT).show();
                    }
                }
            });*/
        }
    }

    /**
     * 输入日记密码对话框
     *
     * @param title
     * @param okString
     * @param onClickListener
     */
    private void inputDiaryPassWordDialog(int title, int okString, View.OnClickListener onClickListener) {
        AlertDialog.Builder putPassWordDialog = new AlertDialog.Builder(getActivity());
        putPassWordDialog.setTitle(title);
        View passwordView = getActivity().getLayoutInflater().inflate(R.layout.manage_password_view, null);
        passwordView.setPadding(Tool.dip2px(getActivity(), 8), Tool.dip2px(getActivity(), 16), Tool.dip2px(getActivity(), 8), 0);
        passWordEd = Tool.findViewById(passwordView, R.id.et_user_password);
        putPassWordDialog.setView(passwordView);
        putPassWordDialog.setPositiveButton(okString, null);//设置确认按钮，点击事件设置为空这样对话框就不会关闭 响应事件见177行
        putPassWordDialog.setNegativeButton(R.string.str_cancel, new DialogInterface.OnClickListener() {//设置不设置密码按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                dialog.dismiss();
            }
        });
        alertDialog = putPassWordDialog.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(onClickListener);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(getActivity());
        deleteDialog.setItems(new String[]{"删除"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        final Diary diary = diaryList.get(position);
                        if (diary.getPassWord() == null || TextUtils.isEmpty(diary.getPassWord())) {
                            diaryDao.delete(diary);
                            notifyDataSetChanged();
                        } else {
                            inputDiaryPassWordDialog(R.string.str_delete_diary, R.string.str_delete, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String inputPassWord = passWordEd.getText().toString().trim();
                                    if (TextUtils.isEmpty(inputPassWord)) {
                                        Toast.makeText(getActivity(), "密码不能为空", Toast.LENGTH_SHORT).show();
                                    } else if (inputPassWord.equals(diary.getPassWord())) {
                                        diaryDao.delete(diary);
                                        notifyDataSetChanged();
                                        alertDialog.cancel();
                                        alertDialog.dismiss();
                                    } else {
                                        Toast.makeText(getActivity(), R.string.str_pass_word_error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        break;
                }
            }
        });
        deleteDialog.create().show();
        return true;
    }

    /**
     * 显示日记细节
     */
    private void showDiaryDetails(Diary diary) {
        Intent intent = new Intent(getActivity(), AddDiaryActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("title", R.string.str_check_the_diary);
        bundle.putSerializable("data", diary);
        intent.putExtras(bundle);
        getActivity().startActivityForResult(intent, MainActivity.RB_DIARY);//跳转到添加日记界面进行查看
    }
}
