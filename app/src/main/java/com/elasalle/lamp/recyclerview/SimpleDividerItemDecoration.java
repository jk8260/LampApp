package com.elasalle.lamp.recyclerview;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.R;
import com.elasalle.lamp.util.DisplayHelper;

public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {

    private final Drawable divider;
    private int inset = 0;
    private boolean isTabletOverride = false;

    public SimpleDividerItemDecoration() {
        divider = ContextCompat.getDrawable(LampApp.getInstance(), R.drawable.divider);
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView recyclerView, RecyclerView.State state) {
        if (!DisplayHelper.isTablet() || isTabletOverride) {
            int left = recyclerView.getPaddingLeft();
            int right = recyclerView.getWidth() - recyclerView.getPaddingRight();
            for (int i = 0; i < recyclerView.getChildCount(); i++) {
                View child = recyclerView.getChildAt(i);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + divider.getIntrinsicHeight();
                divider.setBounds(left + inset, top, right, bottom);
                divider.draw(canvas);
            }
        } else {
            super.onDrawOver(canvas, recyclerView, state);
        }
    }

    public void setInset(int inset) {
        this.inset = inset;
    }

    public void setTabletOverride(boolean isTabletOverride) {
        this.isTabletOverride = isTabletOverride;
    }
}