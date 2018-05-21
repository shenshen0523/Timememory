package com.hfnu.zl.ui.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.PopupWindowCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.hfnu.zl.R;
import com.hfnu.zl.tool.Tool;
import com.hfnu.zl.ui.adapter.ListViewHolder;
import com.hfnu.zl.ui.adapter.OmnipotentBaseAdapter;

import java.util.List;

/**
 * 自定义下拉选择器 继承于TextView
 */

public class MySelectView extends TextView implements View.OnClickListener {
    private PopupWindow popupWindow;
    private ListView listView;
    private List<String> dataList;
    private OnItemSelectListener onItemSelectListener;
    private boolean isLastAloneHandle = true;

    public MySelectView(Context context) {
        super(context);
    }

    public MySelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(this);
    }

    public MySelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 设置选择器数据
     * @param dataList
     */
    public void setData(List<String> dataList) {
        this.dataList = dataList;
        if (dataList != null && !dataList.isEmpty()) {
            setText(dataList.get(0));//将集合的第一个数据设置为选择器的默认text
        }
    }

    @Override
    public void onClick(View v) {
        if (dataList != null && !dataList.isEmpty()) {
            //点击时弹出数据的PopupWindow；
            showPopupWindow();
        }
    }

    /**
     * 设置最后一个数据是否单独处理
     * @param isLastAloneHandle
     */
    public void setLastAloneHandle(boolean isLastAloneHandle){
        this.isLastAloneHandle = isLastAloneHandle;
    }
    private void showPopupWindow() {
        listView = new ListView(getContext());
        popupWindow = new PopupWindow(listView, Tool.dip2px(getContext(), 128), ViewGroup.LayoutParams.WRAP_CONTENT,true);
        listView.setAdapter(new OmnipotentBaseAdapter<String>(getContext(), dataList, R.layout.add_popup_item_view) {
            @Override
            public void convert(ListViewHolder viewHolder, String s) {
                viewHolder.setText(R.id.item_text, s);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == dataList.size() - 1&&isLastAloneHandle) {
                    showCustomDialog();
                }else
                    setText(MySelectView.this.dataList.get(position));
                popupWindow.dismiss();
                if (onItemSelectListener != null) {
                    onItemSelectListener.onItemSelect(MySelectView.this, position);
                }
            }
        });
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup_window_back));
/*        popupWindow.showAsDropDown();*/
        PopupWindowCompat.showAsDropDown(popupWindow, (View) this.getParent(), 0, 0, Gravity.RIGHT);
    }

    /**
     * 选择器的选择的监听事件
     */
    public interface OnItemSelectListener {
        public void onItemSelect(View view, int position);
    }
    /**
     * 设置选择器的选择的监听事件
     */
    public void setOnItemSelectListener(OnItemSelectListener onItemSelectListener) {
        this.onItemSelectListener = onItemSelectListener;
    }

    /**
     * 显示自定义dialog 用于点击最后一个数据时弹出对话框 用户给对话框输入的内容将展示在text上面
     */
    private void showCustomDialog() {
        AlertDialog.Builder customDialog = new AlertDialog.Builder(getContext());
        customDialog.setTitle(R.string.str_administrative_custom_content);
        TextInputLayout view = (TextInputLayout) LayoutInflater.from(getContext()).inflate(R.layout.manage_password_view, null);
        view.setPadding(Tool.dip2px(getContext(), 8), Tool.dip2px(getContext(), 16), Tool.dip2px(getContext(), 8), 0);
        final EditText editText = Tool.findViewById(view, R.id.et_user_password);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setMaxEms(6);
        view.setHint(getContext().getText(R.string.str_max_6char));
        editText.setImeActionLabel(view.getHint(), 0);
        customDialog.setView(view);
        customDialog.setPositiveButton(R.string.str_ok, null);
        customDialog.setNegativeButton(R.string.str_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog alertDialog = customDialog.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {//设置确认按钮点击事件
            @Override
            public void onClick(View v) {
                String inputContent = editText.getText().toString().trim();
                if (TextUtils.isEmpty(inputContent)) {
                    Toast.makeText(getContext(), "内容不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    MySelectView.this.setText(inputContent);
                    alertDialog.dismiss();
                }
            }
        });
    }
}
