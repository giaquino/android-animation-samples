package com.giaquino.animations.common.layout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

public class DragDismissFrameLayout extends FrameLayout {

  public DragDismissFrameLayout(@NonNull Context context) {
    super(context);
  }

  public DragDismissFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public DragDismissFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs,
      int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed,
      int dyUnconsumed) {
    super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
  }

  @Override public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
    super.onNestedPreScroll(target, dx, dy, consumed);
  }

  @Override public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
    return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
  }

  @Override public void onStopNestedScroll(View child) {
    super.onStopNestedScroll(child);
  }

  @Override public void onNestedScrollAccepted(View child, View target, int axes) {
    super.onNestedScrollAccepted(child, target, axes);
  }
}
