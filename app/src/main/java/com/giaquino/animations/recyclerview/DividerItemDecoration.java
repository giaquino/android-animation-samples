package com.giaquino.animations.recyclerview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.Dimension;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class DividerItemDecoration extends RecyclerView.ItemDecoration {

  private final Paint paint;
  private final int inset;
  private final int height;

  public DividerItemDecoration(@Dimension int dividerHeight, @Dimension int leftInset, @ColorInt int dividerColor) {
    inset = leftInset;
    height = dividerHeight;
    paint = new Paint();
    paint.setColor(dividerColor);
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeWidth(dividerHeight);
  }

  @Override public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {

    int childCount = parent.getChildCount();
    if (childCount < 2) return;

    RecyclerView.LayoutManager lm = parent.getLayoutManager();
    float[] lines = new float[childCount * 4];
    boolean hasDividers = false;

    for (int i = 0; i < childCount; i++) {
      View child = parent.getChildAt(i);
      RecyclerView.ViewHolder viewHolder = parent.getChildViewHolder(child);

      if (viewHolder instanceof Divided) {
        // skip if this *or next* view is activated
        if (child.isActivated() || (i + 1 < childCount && parent.getChildAt(i + 1).isActivated())) {
          continue;
        }
        int n = i * 4;
        int y = lm.getDecoratedBottom(child) + (int) child.getTranslationY() - height;
        lines[n] = inset + lm.getDecoratedLeft(child);
        lines[n + 1] = y;
        lines[n + 2] = lm.getDecoratedRight(child);
        lines[n + 3] = y;
        hasDividers = true;
      }
    }
    if (hasDividers) canvas.drawLines(lines, paint);
  }
}
