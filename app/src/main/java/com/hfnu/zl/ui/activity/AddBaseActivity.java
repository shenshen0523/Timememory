package com.hfnu.zl.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ScrollView;

import com.hfnu.zl.R;
import com.hfnu.zl.tool.Tool;

/**
 * 添加界面的基础activity
 *
 * Created by ontheway on 2017/4/25.
 */

public abstract class AddBaseActivity extends AppCompatActivity {
    Toolbar toolbar; //工具栏
    private View rooView;//根view 界面的最底层布局
    ScrollView content_parent;//滑动的View
    private void initRootView(){
        rooView = getLayoutInflater().inflate(R.layout.base_activity,null);
        toolbar = Tool.findViewById(rooView,R.id.toolbar);
        content_parent = Tool.findViewById(rooView,R.id.content_parent);
    }

    /**
     * 初始化内容区域的View
     * @param layout 内容区域view布局文件
     */
    void initContentView(int layout){
        initContentView(getLayoutInflater().inflate(layout,null));
    }
    /**
     * 初始化内容区域的View
     * @param view 内容区域view布局
     */
    void initContentView(View view){
        initRootView();//先初始化根view
        initViewBefore();//初始化view之前加载数据方法
        content_parent.addView(view);//将内容view添加在滑动的View中 这样如果内容比较多的话可以通过滑动得到被遮挡的View
        setContentView(rooView);//调用弗父类设置view方法将跟view设置进去
        bindView();//绑定view
        setSupportActionBar(toolbar);//将工具栏设置为标题栏
        toolbar.setNavigationIcon(R.drawable.home_as_up_indicator);//设置返回按钮图标
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });//设置点击返回按钮时的监听事件
        initViewLater();
    }

    /**
     * 获取根view 为子类提供的方法
     * @return
     */
    View getRootView(){
        return rooView;
    }
    /**
     * 加载View之前需要做的事情 为子类提供的方法
     */
    void initViewBefore(){}
    /**
     * 绑定View 为子类提供的方法
     */
    void bindView(){}
    /**
     * 加载View之后需要做的事情 为子类提供的方法
     */
    void initViewLater(){}
}
