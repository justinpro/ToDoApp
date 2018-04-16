package com.example.justin.todoapp.tasks;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class ScrollChildSwipeRefreshLayout extends SwipeRefreshLayout {

    private View mScrollUpChild;
    private static final String TAG = "test";

    public ScrollChildSwipeRefreshLayout(@NonNull Context context) {
        super(context);
    }

    public ScrollChildSwipeRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean canChildScrollUp() {
        if (mScrollUpChild != null) {
            /**
             * 源码是这样的：
             * direction 为 < 0 的时候，而且这个参数的view有位移的时候
             * 才返回true
             */
            boolean flag = ViewCompat.canScrollVertically(mScrollUpChild, -1);
            Log.i(TAG, "ScrollChildSwipeRefresh canChildScrollUp: flag -> " + flag);
            return flag;
        }
        return super.canChildScrollUp();
    }

    public void setScrollUpChild(View view) {
        mScrollUpChild = view;
    }
}
