package com.hfnu.zl.ui.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 日历单个日期按钮父布局
 */
public class CalendarLinearlayout extends LinearLayout{
    private List<TextView> childTextViewList;//此布局下所有直系的Text View集合
    public CalendarLinearlayout(Context context) {
        super(context);
    }
    public CalendarLinearlayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public CalendarLinearlayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 重写焦点改变方法
     * @param gainFocus 焦点状态
     * @param direction
     * @param previouslyFocusedRect
     */
    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if(childTextViewList==null){
            childTextViewList = new ArrayList<>();
            for(int i=0;i<getChildCount();i++){
                if(getChildAt(i) instanceof TextView){//获取所有的Text View
                    TextView textView= (TextView) getChildAt(i);
                    textView.setTag(textView.getTextColors());//将text View默认的颜色设置为tag属性
                    childTextViewList.add(textView);//将text VIew添加到集合中
                }
            }
        }
        if(gainFocus){//如果当前的布局获取了焦点 将字体的颜色改成白色  至于背景色如何更改 可参考为本布局设置的drawable对象
            for(TextView textView:childTextViewList){
                textView.setTextColor(Color.WHITE);
            }
        }else{//如果当前的布局获取了焦点 将字体的颜色改成字体原本的颜色原本的颜色已设置为当前text View的tag属性  至于背景色如何更改 可参考为本布局设置的drawable对象
            for(TextView textView:childTextViewList){
                textView.setTextColor((ColorStateList) textView.getTag());
            }
        }
       // Log.i("gainFocus",gainFocus+"#############"+getChildCount());
    }
}
