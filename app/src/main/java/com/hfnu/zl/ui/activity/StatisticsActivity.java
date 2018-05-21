package com.hfnu.zl.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.hfnu.zl.R;
import com.hfnu.zl.database.AccountBook;
import com.hfnu.zl.database.AccountBookDao;
import com.hfnu.zl.database.DaoMaster;
import com.hfnu.zl.tool.Tool;

import java.util.List;

public class StatisticsActivity extends AppCompatActivity {
    private TextView tc_historical_consume,tv_earnings_history;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        bindView();
        initViewLater();
    }
    private void bindView(){
        Toolbar toolbar = Tool.findViewById(this,R.id.toolbar);
        toolbar.setTitle(R.string.str_account_book_statistics);
        toolbar.setNavigationIcon(R.drawable.home_as_up_indicator);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tv_earnings_history = Tool.findViewById(this,R.id.tv_earnings_history);
        tc_historical_consume = Tool.findViewById(this,R.id.tc_historical_consume);
    }
    private void initViewLater(){
        AccountBookDao accountBookDao = DaoMaster.getDaoSession(this).getAccountBookDao();
        List<AccountBook> earningsHistoryList = accountBookDao.queryBuilder().where(AccountBookDao.Properties.Turnover.eq(false)).build().list();
        List<AccountBook> historicalConsumeList = accountBookDao.queryBuilder().where(AccountBookDao.Properties.Turnover.eq(true)).build().list();
        if(earningsHistoryList.isEmpty()){
            tv_earnings_history.setText(R.string.str_temporarily_no_income_record);
        }else{
           float earnings_history=0;
            for(AccountBook accountBook:earningsHistoryList){
                earnings_history+=accountBook.getMoney();
            }
            tv_earnings_history.setText(earnings_history+"元");
        }
        if(historicalConsumeList.isEmpty()){
            tc_historical_consume.setText(R.string.str_temporarily_no_records_of_consumption);
        }else{
            float historical_consume=0;
            for(AccountBook accountBook:historicalConsumeList){
                historical_consume+=accountBook.getMoney();
            }
            tc_historical_consume.setText(historical_consume+"元");
        }
    }
}
