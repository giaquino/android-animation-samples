package com.giaquino.animations.common.view;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class DynamicTypeTextView extends AppCompatTextView {

  public DynamicTypeTextView(Context context) {
    super(context);
  }

  public DynamicTypeTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public DynamicTypeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    final float size = Math.max(
        getTextSize(),
        getSingleLineTextSize(
            getText().toString(),
            getPaint(),
            MeasureSpec.getSize(widthMeasureSpec),
            getTextSize(),
            getTextSize() * 3,
            0.5F,
            getResources().getDisplayMetrics()));
    setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  }

  public float getSingleLineTextSize(String text, TextPaint paint, float targetWidth,
      float low, float high, float precision, DisplayMetrics metrics) {
    final float mid = (low + high) / 2.0f;

    paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, mid, metrics));
    final float maxLineWidth = paint.measureText(text);

    if ((high - low) < precision) {
      return low;
    }
    else if (maxLineWidth > targetWidth) {
      return getSingleLineTextSize(text, paint, targetWidth, low, mid, precision, metrics);
    }
    else if (maxLineWidth < targetWidth) {
      return getSingleLineTextSize(text, paint, targetWidth, mid, high, precision, metrics);
    }
    else {
      return mid;
    }
  }
}
