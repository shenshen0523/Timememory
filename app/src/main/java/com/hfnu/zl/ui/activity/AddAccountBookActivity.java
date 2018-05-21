package com.hfnu.zl.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hfnu.zl.R;
import com.hfnu.zl.database.AccountBook;
import com.hfnu.zl.database.AccountBookDao;
import com.hfnu.zl.database.DaoMaster;
import com.hfnu.zl.tool.Tool;
import com.hfnu.zl.ui.widget.MySelectView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 添加账本界面 继承于AddBaseActivity拥有AddBaseActivity属性和方法 并实现点击监听接口和自定义选择器选择监听
 */
public class AddAccountBookActivity extends AddBaseActivity implements View.OnClickListener, MySelectView.OnItemSelectListener {
    //private Intent intent;
    private Bundle bundle;
    private MySelectView tv_type_select, tv_use_select, tv_turnover_select;
    private EditText et_money, et_remark;
    private Button btn_save;
    private List<List<String>> allSelect;
    private AccountBookDao accountBookDao;
    private AccountBook accountBook;
    private boolean isEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContentView(R.layout.activity_add_account_book);
    }

    /**
     * 以下三个方法作用不在赘述
     */
    @Override
     void bindView() {
        toolbar.setTitle(bundle.getInt("title", R.string.app_name));
        tv_type_select = Tool.findViewById(this, R.id.tv_type_select);
        tv_type_select.setData(Arrays.asList(getResources().getStringArray(R.array.type_array)));
        tv_type_select.setOnItemSelectListener(this);
        tv_use_select = Tool.findViewById(this, R.id.tv_use_select);
        tv_use_select.setData(allSelect.get(0));
        tv_turnover_select = Tool.findViewById(this, R.id.tv_turnover_select);
        tv_turnover_select.setLastAloneHandle(false);
        tv_turnover_select.setData(Arrays.asList(new String[]{getString(R.string.str_expenditure), getString(R.string.str_income)}));
        et_money = Tool.findViewById(this, R.id.et_money);
        et_remark = Tool.findViewById(this, R.id.et_remark);
        btn_save = Tool.findViewById(this, R.id.btn_save);
        btn_save.setOnClickListener(this);
    }
@Override
    void initViewBefore() {
        bundle = getIntent().getExtras();
        accountBookDao = DaoMaster.getDaoSession(this).getAccountBookDao();//得到操作账本的数据库操作对象
        allSelect = new ArrayList<>();
        allSelect.add(Arrays.asList(getResources().getStringArray(R.array.family_array)));
        allSelect.add(Arrays.asList(getResources().getStringArray(R.array.travel_array)));
        allSelect.add(Arrays.asList(getResources().getStringArray(R.array.company_array)));
        allSelect.add(Arrays.asList(getResources().getStringArray(R.array.other_array)));
        allSelect.add(Arrays.asList(getResources().getStringArray(R.array.other_array)));
    }
    @Override
    void initViewLater() {
       // Toast.makeText(this,"有"+bundle.getInt("title", R.string.app_name),Toast.LENGTH_SHORT).show();
        accountBook = (AccountBook) bundle.getSerializable("data");
        if (accountBook == null)
            accountBook = new AccountBook();
        else {//将编辑模式设置为true
            isEdit = true;
            tv_type_select.setText(accountBook.getType());
            tv_use_select.setText(accountBook.getUse());
            et_money.setText(accountBook.getMoney() + "");
            tv_turnover_select.setText(accountBook.getTurnover() ? R.string.str_expenditure : R.string.str_income);
            et_remark.setText(accountBook.getRemark().equals("无") ? "" : accountBook.getRemark());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                /*
                保存账本
                 */
                accountBook.setType(tv_type_select.getText().toString().trim());
                accountBook.setUse(tv_use_select.getText().toString().trim());
                accountBook.setMoney(Float.parseFloat(et_money.getText().toString().trim()));
                accountBook.setTurnoverText(tv_turnover_select.getText().toString().trim());
                accountBook.setWriteTime(new Date());
                String remark = et_remark.getText().toString().trim();
                if (TextUtils.isEmpty(remark)) {
                    remark = "无";
                }
                accountBook.setRemark(remark);
                if (!isEdit)
                    accountBookDao.insert(accountBook);
                else
                    accountBookDao.update(accountBook);
                setResult(MainActivity.HAVE_A_VALUE);
                onBackPressed();
                break;
        }
    }

    @Override
    public void onItemSelect(View view, int position) {
        switch (view.getId()) {
            case R.id.tv_type_select:
                tv_use_select.setData(allSelect.get(position));
        }
    }
}
