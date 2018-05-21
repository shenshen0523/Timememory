package com.hfnu.zl.tool;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ontheway on 2017/4/24.
 */

public class Tool {
    private static SimpleDateFormat sdfDetail,sdfConcise;

    /**
     * 通过泛型实现查找View后自动将查找的View转换为定义的类型
     * @param act activity实例
     * @param id 控件id
     * @param <T> 泛型
     * @return 返回转换后的view
     */
    @SuppressWarnings("unchecked")
    public static <T> T findViewById(Activity act, int id) {
        return (T) act.findViewById(id);
    }
    /**
     * 通过泛型实现查找View后自动将查找的View转换为定义的类型
     * @param parent 该控件的父view
     * @param id 控件id
     * @param <T> 泛型
     * @return 返回转换后的view
     */
    @SuppressWarnings("unchecked")
    public static <T> T findViewById(View parent, int id) {
        return (T) parent.findViewById(id);
    }
    /**
     * /**
     * 将sp值转换为px值，保证文字大小不变
     * （DisplayMetrics类中属性scaledDensity）
     *
     * @param context
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     * （DisplayMetrics类中属性scaledDensity）
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     * （DisplayMetrics类中属性density）
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 讲台方法 该类初始化以后就执行此方法 并且只执行一次
     */
    static {
        sdfDetail = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        sdfConcise = new SimpleDateFormat("yyyy年MM月dd日");
    }

    /**
     * 格式化得到详细的时间格式
     * @param date 时间
     * @return
     */
    public static String formattingDetailDate(Date date){
        return sdfDetail.format(date);
    }
    /**
     * 格式化得到简单的时间格式
     * @param date 时间
     * @return
     */
    public static String formattingConciseDate(Date date){
        return sdfConcise.format(date);
    }

    /**
     * 将字符串时间转换为时间对象
     * @param dateString
     * @return
     */
    public static Date formattingDateString(String dateString){
        try {
            return sdfDetail.parse(dateString);
        } catch (ParseException e) {
           // e.printStackTrace();
            return new Date();
        }
    }

}
