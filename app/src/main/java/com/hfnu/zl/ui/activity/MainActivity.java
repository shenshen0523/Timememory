package com.hfnu.zl.ui.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.hfnu.zl.R;
import com.hfnu.zl.database.DaoMaster;
import com.hfnu.zl.tool.Tool;
import com.hfnu.zl.ui.fragment.AccountBookFragment;
import com.hfnu.zl.ui.fragment.BaseFragment;
import com.hfnu.zl.ui.fragment.DiaryFragment;
import com.hfnu.zl.ui.fragment.ScheduleFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 主界面
 */
public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {
    private RadioButton[] radioButtons;
    public final static int RB_DIARY = 0, RB_ACCOUNT_BOOK = 1, RB_SCHEDULE = 2, HAVE_A_VALUE = 1;
    private RadioGroup tab_group;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private List<BaseFragment> fragmentList;
    private SharedPreferences sp;
    private MenuItem calendarItem,statisticsItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        bindView();
    }

    private void bindView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);//获取工具栏
        toolbar.setTitle(R.string.str_diary);
        setSupportActionBar(toolbar);//将工具栏设置为导航栏
        viewPager = Tool.findViewById(this, R.id.viewPager);
        viewPager.setAdapter(new MyFragmentPagerAdapter(getFragmentManager()));
        viewPager.addOnPageChangeListener(new MyOnPageChangeListener());
        tab_group = Tool.findViewById(this, R.id.tab_group);
        tab_group.setOnCheckedChangeListener(this);
        //底部的三个radio button
        radioButtons = new RadioButton[3];
        radioButtons[RB_DIARY] = Tool.findViewById(this, R.id.rb_diary);
        radioButtons[RB_ACCOUNT_BOOK] = Tool.findViewById(this, R.id.rb_account_book);
        radioButtons[RB_SCHEDULE] = Tool.findViewById(this, R.id.rb_schedule);
    }

    private void initData() {
        /**
         * 初始化首页的三个fragment
         */
        fragmentList = new ArrayList<>();
        fragmentList.add(new DiaryFragment());
        fragmentList.add(new AccountBookFragment());
        fragmentList.add(new ScheduleFragment());
    }

    /**
     * RadioGroup下面的radioButton切换监听
     * @param group
     * @param checkedId 当前选中的radiobutton的id
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        calendarItem.setVisible(false);//切换时将工具栏上的日历和统计按钮隐藏
        statisticsItem.setVisible(false);
        /*
        选中状态改变时 改变工具栏上的title为对应的标题 并切换viewpager选中页
         */
        switch (checkedId) {
            case R.id.rb_diary:
                toolbar.setTitle(R.string.str_diary);
                viewPager.setCurrentItem(RB_DIARY);
                break;
            case R.id.rb_account_book:
                statisticsItem.setVisible(true);
                toolbar.setTitle(R.string.str_account_book);
                viewPager.setCurrentItem(RB_ACCOUNT_BOOK);
                break;
            case R.id.rb_schedule:
                calendarItem.setVisible(true);
                toolbar.setTitle(R.string.str_schedule);
                viewPager.setCurrentItem(RB_SCHEDULE);
                break;
        }
    }

    /**
     * 创建菜单
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        calendarItem = menu.findItem(R.id.calendar);//得到日历item
        statisticsItem = menu.findItem(R.id.statistics);//得到统计item
        return true;
    }

    /**
     * 菜单选择事件
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                /*
                点击添加按钮时通过radio Ground的选中状态进入不同的添加界面
                 */
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                int requestCode = RB_DIARY;
                switch (tab_group.getCheckedRadioButtonId()) {
                    case R.id.rb_diary:
                        requestCode = RB_DIARY;//设置请求码
                        intent.setComponent(new ComponentName(MainActivity.this, AddDiaryActivity.class));//设置要跳转的界面
                        bundle.putInt("title", R.string.str_add_diary);//设置该界面的标题
                        break;
                    case R.id.rb_account_book:
                        requestCode = RB_ACCOUNT_BOOK;
                        intent.setComponent(new ComponentName(MainActivity.this, AddAccountBookActivity.class));
                        bundle.putInt("title", R.string.str_add_account_book);
                        break;
                    case R.id.rb_schedule:
                        requestCode = RB_SCHEDULE;
                        intent.setComponent(new ComponentName(MainActivity.this, AddScheduleActivity.class));
                        bundle.putInt("title", R.string.str_add_schedule);
                        break;
                }
                intent.putExtras(bundle);
                MainActivity.this.startActivityForResult(intent, requestCode);//使用startActivityForResult方式启动activity 这样在添加界面中保存数据后此界面可以接收到保存的消息  从而更新对应视图中的数据
                break;
            case R.id.setting:
                if (sp == null) {
                    sp = getSharedPreferences("baseData", Context.MODE_PRIVATE);
                }
                switch (sp.getInt("savePassWord", LoginActivity.DO_NOT_SAVE_THE_PASSWORD)) {
                    case LoginActivity.NO_PASSWORD:
                        showManageDialog();
                        break;
                    case LoginActivity.DO_NOT_SAVE_THE_PASSWORD:

                    case LoginActivity.SAVE_PASSWORD:
                        showEditPassWordDialog();
                        break;
                }
                break;
            case R.id.exit:
                //退出系统前关闭数据库的Session和database连接对象
                DaoMaster.clossSession();
                DaoMaster.colssDB();
                android.os.Process.killProcess(android.os.Process.myPid());    //获取PID
                System.exit(0);
                break;
            case R.id.calendar:
                startActivity(new Intent(this,CalendarActivity.class));//启动日历界面
                break;
            case R.id.statistics:
                startActivity(new Intent(this,StatisticsActivity.class));//启动统计界面
                break;
        }
        return true;
    }

    /**
     * 显示设置密码对话框
     */
    private void showManageDialog() {
        AlertDialog.Builder manageDialog = new AlertDialog.Builder(this);
        manageDialog.setTitle(R.string.str_set_password);
        View view = getLayoutInflater().inflate(R.layout.manage_password_view, null);
        view.setPadding(Tool.dip2px(this, 16), Tool.dip2px(this, 16), Tool.dip2px(this, 16), 0);
        final EditText passWordEd = Tool.findViewById(view, R.id.et_user_password);
        manageDialog.setCancelable(false);
        manageDialog.setView(view);
        final SharedPreferences.Editor editor = sp.edit();
        manageDialog.setPositiveButton(R.string.str_ok, null);//设置确认按钮，点击事件设置为空这样对话框就不会关闭
        manageDialog.setNegativeButton(R.string.str_no_set_pass_word, new DialogInterface.OnClickListener() {//设置不设置密码按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                dialog.dismiss();
            }
        });
        final AlertDialog alertDialog = manageDialog.create();//得到对话框实例
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {//设置确认按钮点击事件
            @Override
            public void onClick(View v) {
                String inputPassWord = passWordEd.getText().toString().trim();
                if (TextUtils.isEmpty(inputPassWord)) {
                    Toast.makeText(MainActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    editor.putString("passWord", inputPassWord);
                    editor.putInt("savePassWord", LoginActivity.DO_NOT_SAVE_THE_PASSWORD);
                    editor.commit();
                    alertDialog.cancel();
                    alertDialog.dismiss();
                }
            }
        });
    }

    /**
     * 显示更改密码对话框
     */
    private void showEditPassWordDialog() {
        AlertDialog.Builder editPassWordDialog = new AlertDialog.Builder(this);
        editPassWordDialog.setTitle(R.string.str_edit_password);
        View contentView = getLayoutInflater().inflate(R.layout.setting_pass_word_view, null);
        final EditText oldPass = Tool.findViewById(contentView, R.id.et_old_password);
        final EditText newPass = Tool.findViewById(contentView, R.id.et_new_password);
        editPassWordDialog.setView(contentView);
        editPassWordDialog.setNegativeButton(R.string.str_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                dialog.dismiss();
            }
        });
        editPassWordDialog.setPositiveButton(R.string.str_ok, null);
        final AlertDialog alertDialog = editPassWordDialog.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {//设置确认按钮点击事件
            @Override
            public void onClick(View v) {
                String oldPassWord = oldPass.getText().toString().trim();
                String newPassWord = newPass.getText().toString().trim();
                if (TextUtils.isEmpty(oldPassWord) || TextUtils.isEmpty(newPassWord)) {
                    Toast.makeText(MainActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                } else if (oldPassWord.equals(sp.getString("passWord", ""))) {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("passWord", newPassWord);
                    editor.putInt("savePassWord", LoginActivity.DO_NOT_SAVE_THE_PASSWORD);
                    editor.commit();
                    alertDialog.cancel();
                    alertDialog.dismiss();
                    Toast.makeText(MainActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(MainActivity.this, "原密码错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * View Pager中的适配器
     */
    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }

    /**
     * pagerView的页面改变监听
     */
    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            //切换fragment后改变Radio Ground的选中按钮
            radioButtons[position].setChecked(true);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    /**
     * 从其他activity返回该界面时的监听
     * @param requestCode 请求码 启动另一个activity设定的
     * @param resultCode 返回码 另一个activity返回回来的
     * @param data intent数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == HAVE_A_VALUE) {//如果从其他界面返回时所带的返回值为有数据的话 通知fragment跟新界面 其中请求码是更fragmentlist中的序列一一对应的
            fragmentList.get(requestCode).notifyDataSetChanged();
        }
/*        switch (requestCode) {
            case RB_DIARY:
                break;
            case RB_ACCOUNT_BOOK:
                fragmentList.get(requestCode).notifyDataSetChanged();
                break;
            case RB_SCHEDULE:
                break;
        }*/
    }

    private long lastTimer = 0;//此变量用于再次点击回到界面时记录第一次点击的时间戳

    @Override
    public void onBackPressed() {
        long currentTimer = System.currentTimeMillis();//点击返回按钮后得到当前的时间戳
        if (currentTimer - lastTimer > 2000) {
            lastTimer = currentTimer;
            Toast.makeText(this, "再次点击回到桌面", Toast.LENGTH_SHORT).show();
        } else {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
        }
    }
}
