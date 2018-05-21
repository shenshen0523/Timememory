package com.hfnu.zl.ui.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * 暂未用上 是为了防止设置半透明状态主题时弹出软件盘导致标题栏向上挤出
 */
public class CustomInsetsLinearLayout extends LinearLayout {

    public CustomInsetsLinearLayout(Context context) {
        super(context);
    }

    public CustomInsetsLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomInsetsLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    @Override
    protected final boolean fitSystemWindows(Rect insets) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Intentionally do not modify the bottom inset. For some reason,
            // if the bottom inset is modified, window resizing stops working.
            // TODO: Figure out why.
            insets.left = 0;
            insets.top = 0;
            insets.right = 0;
        }
        return super.fitSystemWindows(insets);
    }
}