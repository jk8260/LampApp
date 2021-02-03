package com.elasalle.lamp.recyclerview;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;

import com.elasalle.lamp.R;
import com.elasalle.lamp.util.DisplayHelper;
import com.elasalle.lamp.util.ResourcesUtil;

public class FooterDecoration extends RecyclerView.ItemDecoration {

    private final TextPaint textPaint;
    private StaticLayout staticLayout;

    public FooterDecoration() {
        textPaint = new TextPaint();
        textPaint.setColor(ResourcesUtil.getColor(R.color.grey5, null));
        textPaint.setTextSize(DisplayHelper.getPixelsDp(14f));
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView recyclerView, RecyclerView.State state) {
        super.onDrawOver(canvas, recyclerView, state);

        View view = recyclerView.getChildAt(recyclerView.getChildCount() - 1);

        if (view == null) {
            return;
        }

        staticLayout = new StaticLayout(
                ResourcesUtil.getString(R.string.scan_new_scan_set_list_footer),
                textPaint,
                canvas.getWidth() - DisplayHelper.getPixelsDp(80f),
                Layout.Alignment.ALIGN_NORMAL,
                1.0f,
                0.0f,
                false);
        canvas.save();
        canvas.translate(
                DisplayHelper.getPixelsDp(72f),
                view.getY() + view.getHeight() + DisplayHelper.getPixelsDp(16f));
        staticLayout.draw(canvas);
        canvas.restore();

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (staticLayout != null && parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1) {
            outRect.bottom = staticLayout.getHeight() + DisplayHelper.getPixelsDp(16f);
        } else {
            outRect.top = 0;
            outRect.bottom = 0;
            outRect.left = 0;
            outRect.right = 0;
        }
    }
}
