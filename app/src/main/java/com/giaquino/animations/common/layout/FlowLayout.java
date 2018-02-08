package com.giaquino.animations.common.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import com.giaquino.animations.R;

import static android.view.View.MeasureSpec.AT_MOST;
import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.getMode;
import static android.view.View.MeasureSpec.getSize;
import static android.view.View.MeasureSpec.makeMeasureSpec;
import static com.giaquino.animations.common.layout.FlowLayout.LayoutParams.MATCH_PARENT;
import static com.giaquino.animations.common.layout.FlowLayout.LayoutParams.WRAP_CONTENT;

public class FlowLayout extends ViewGroup {

  private int[] lineVerticalSpacing = new int[10];
  private int horizontalPadding = 0;
  private int verticalPadding = 0;

  public FlowLayout(Context context) {
    super(context);
  }

  public FlowLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout, defStyleAttr, 0);
    for (int i = 0, size = a.getIndexCount(); i < size; i++) {
      switch (a.getIndex(i)) {
        case R.styleable.FlowLayout_horizontalSpacing:
          horizontalPadding = a.getDimensionPixelSize(i, 0);
          break;
        case R.styleable.FlowLayout_verticalSpacing:
          verticalPadding = a.getDimensionPixelSize(i, 0);
          break;
      }
    }
    a.recycle();
  }

  @Override protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
    return new LayoutParams(lp);
  }

  @Override protected LayoutParams generateDefaultLayoutParams() {
    return new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
  }

  @Override public LayoutParams generateLayoutParams(AttributeSet attrs) {
    return new LayoutParams(getContext(), attrs);
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

    final int parentWidth = getSize(widthMeasureSpec);
    final int parentHeight = getSize(heightMeasureSpec);
    final int parentWidthMode = getMode(widthMeasureSpec);
    final int parentHeightMode = getMode(heightMeasureSpec);
    final int parentAvailableWidth = parentWidth - getPaddingLeft() - getPaddingRight();
    final int parentAvailableHeight = parentHeight - getPaddingTop() - getPaddingBottom();

    final boolean rtl = isRightToLeft(this);

    int lines = 0;
    int lineHeight = 0;

    int positionY = getPaddingTop();
    int positionX = rtl ? getPaddingRight() : getPaddingLeft();

    lineVerticalSpacing[0] = getPaddingTop();

    for (int i = 0, size = getChildCount(); i < size; i++) {

      View child = getChildAt(i);
      if (!isVisible(child)) continue;

      /* measure children */
      measureChildWithMargin(child, parentAvailableWidth, parentAvailableHeight);

      /* compute total child dimension */
      LayoutParams lp = (LayoutParams) child.getLayoutParams();
      int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
      int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

      /* next line */
      if (positionX + childWidth > parentAvailableWidth) {

        /* record positionY (top) for current line */
        lines += 1;
        positionY += lineHeight + verticalPadding;
        lineVerticalSpacing = ensureArrayCapacity(lineVerticalSpacing, lines);
        lineVerticalSpacing[lines] = positionY;

        /* reset values */
        lineHeight = 0;
        positionX = rtl ? getPaddingRight() : getPaddingLeft();
      }

      /* get max line height */
      lineHeight = Math.max(lineHeight, childHeight);

      /* compute horizontal position */
      lp.line = lines;
      computeChildLeftAndRightPosition(child, positionX, parentWidth);

      positionX += childWidth + horizontalPadding;
    }

    /* add last line height */
    lines += 1;
    positionY += lineHeight + verticalPadding;
    lineVerticalSpacing = ensureArrayCapacity(lineVerticalSpacing, lines);
    lineVerticalSpacing[lines] = positionY;

    /* compute top and bottom values for children */
    for (int i = 0, size = getChildCount(); i < size; i++) {

      View child = getChildAt(i);
      if (!isVisible(child)) continue;

      computeChildTopAndBottomPosition(child);
    }

    /* measure layout size base on its children's consumed size */
    int measuredWidth, measuredHeight;
    switch (parentWidthMode) {
      case EXACTLY:
        measuredWidth = parentWidth;
        break;
      default:
        measuredWidth =
            lines > 1 ? parentWidth : Math.min(parentWidth, positionX + getPaddingRight());
        break;
    }
    switch (parentHeightMode) {
      case EXACTLY:
        measuredHeight = parentHeight;
        break;
      default:
        /* ignore last line vertical padding */
        measuredHeight = Math.min(parentHeight, positionY + getPaddingBottom() - verticalPadding);
        break;
    }
    setMeasuredDimension(measuredWidth, measuredHeight);
  }

  @Override protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    if (changed) layoutChildren();
  }

  private void layoutChildren() {
    for (int i = 0, size = getChildCount(); i < size; i++) {
      View child = getChildAt(i);
      if (!isVisible(child)) {
        continue;
      }
      LayoutParams lp = (LayoutParams) child.getLayoutParams();
      child.layout(lp.left, lp.top, lp.right, lp.bottom);
    }
  }

  private void computeChildLeftAndRightPosition(View child, int left, int parentWidth) {
    LayoutParams lp = (LayoutParams) child.getLayoutParams();
    if (isRightToLeft(this)) {
      lp.left = parentWidth - left - child.getMeasuredWidth() - lp.rightMargin;
    } else {
      lp.left = left + lp.leftMargin;
    }
    lp.right = lp.left + child.getMeasuredWidth();
  }

  private void computeChildTopAndBottomPosition(View child) {
    LayoutParams lp = (LayoutParams) child.getLayoutParams();
    switch (lp.gravity & Gravity.VERTICAL_GRAVITY_MASK) {
      case Gravity.TOP:
        lp.top = lineVerticalSpacing[lp.line] + lp.topMargin;
        break;
      case Gravity.CENTER_VERTICAL:
        lp.top = ((lineVerticalSpacing[lp.line] + lineVerticalSpacing[lp.line + 1]) / 2)
            - child.getMeasuredHeight() / 2;
        break;
      case Gravity.BOTTOM:
        lp.top = lineVerticalSpacing[lp.line + 1]
            - verticalPadding
            - child.getMeasuredHeight()
            - lp.bottomMargin;
        break;
    }
    lp.bottom = lp.top + child.getMeasuredHeight();
  }

  private void measureChildWithMargin(View child, int width, int height) {
    LayoutParams lp = (LayoutParams) child.getLayoutParams();

    width -= (lp.leftMargin + lp.rightMargin);
    height -= (lp.topMargin + lp.bottomMargin);

    int childWidthMeasureSpec, childHeightMeasureSpec;
    switch (lp.width) {
      case MATCH_PARENT:
        childWidthMeasureSpec = makeMeasureSpec(width, EXACTLY);
        break;
      case WRAP_CONTENT:
        childWidthMeasureSpec = makeMeasureSpec(width, AT_MOST);
        break;
      default:
        childWidthMeasureSpec = makeMeasureSpec(Math.min(lp.width, width), EXACTLY);
        break;
    }
    switch (lp.height) {
      case MATCH_PARENT:
        childHeightMeasureSpec = makeMeasureSpec(height, EXACTLY);
        break;
      case WRAP_CONTENT:
        childHeightMeasureSpec = makeMeasureSpec(height, AT_MOST);
        break;
      default:
        childHeightMeasureSpec = makeMeasureSpec(Math.min(lp.height, height), EXACTLY);
        break;
    }
    child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
  }

  private int[] ensureArrayCapacity(int[] source, int size) {
    if (size > source.length - 1) {
      int[] array = new int[source.length * 2];
      System.arraycopy(source, 0, array, 0, source.length);
      return array;
    }
    return source;
  }

  private boolean isVisible(View view) {
    return view.getVisibility() == View.VISIBLE;
  }

  private boolean isRightToLeft(View view) {
    return ViewCompat.LAYOUT_DIRECTION_RTL == ViewCompat.getLayoutDirection(view);
  }

  @SuppressWarnings("WeakerAccess") public static class LayoutParams
      extends ViewGroup.MarginLayoutParams {

    /* cached line for getting the top indent */
    private int line;

    /* cached position of the view */
    private int left;
    private int top;
    private int right;
    private int bottom;

    public int gravity = Gravity.BOTTOM;

    public LayoutParams(Context c, AttributeSet attrs) {
      super(c, attrs);
      TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.FlowLayout_Layout);
      gravity = a.getInt(R.styleable.FlowLayout_Layout_android_layout_gravity, Gravity.BOTTOM);
      a.recycle();
    }

    public LayoutParams(int width, int height) {
      super(width, height);
    }

    public LayoutParams(ViewGroup.LayoutParams source) {
      super(source);
    }
  }
}
