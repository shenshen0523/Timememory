package com.hfnu.zl.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.hfnu.zl.R;
import com.hfnu.zl.tool.Tool;

/**
 * 基础fragment
 * 具有下面全局变量的fragment均将实现此fragment
 */

public abstract class BaseFragment extends Fragment implements AdapterView.OnItemClickListener{
    ListView listView;
    LinearLayout ll_not_content;
    View contentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.public_fragment_view, container, false);
        listView = Tool.findViewById(contentView, R.id.content_list);
        listView.setOnItemClickListener(this);
        ll_not_content = Tool.findViewById(contentView, R.id.ll_not_content);
        initData();
        return contentView;
    }

    /**
     * 如果数据为true则隐藏list View 显示提示view false则相反
     * @param isShow
     */
    void isShowNullView(boolean isShow) {
        ll_not_content.setVisibility(isShow ? View.VISIBLE : View.GONE);
        listView.setVisibility(isShow ? View.GONE : View.VISIBLE);
    }

    /**
     * 子类提供的加载数据方法
     */
    abstract void initData();

    /**
     * 为子类提供的更新adapter方法
     */
    public abstract void notifyDataSetChanged();
}
