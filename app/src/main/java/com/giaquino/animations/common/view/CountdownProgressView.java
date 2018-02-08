package com.giaquino.animations.common.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import com.giaquino.animations.R;
import com.giaquino.animations.common.util.Utilities;
import java.util.concurrent.TimeUnit;

public class CountdownProgressView extends View {

  private ValueAnimator animator;
  private Path path = new Path();
  private Paint paint = new Paint();
  private Rect textBounds = new Rect();
  private RectF background = new RectF();

  private int min = 0;
  private int max = 10;
  private float time = 0;
  private int progress = max;

  private int spacing = 0;
  private int textSize = 0;
  private int strokeWidth = 0;

  private int fillColor = Color.BLACK;
  private int textColor = Color.parseColor("#F8BE39");
  private int progressColor = Color.parseColor("#F8BE39");
  private int progressBackgroundColor = Color.parseColor("#D5D5D5");

  public CountdownProgressView(Context context) {
    super(context);
  }

  public CountdownProgressView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  public CountdownProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
  }

  private void init(Context context, AttributeSet attributeSet) {
    TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.CountdownProgressView);
    min = a.getInt(R.styleable.CountdownProgressView_min, min);
    max = a.getInt(R.styleable.CountdownProgressView_max, max);
    spacing = a.getDimensionPixelSize(R.styleable.CountdownProgressView_spacing, spacing);
    textSize = a.getDimensionPixelSize(R.styleable.CountdownProgressView_android_textSize, textSize);
    strokeWidth = a.getDimensionPixelSize(R.styleable.CountdownProgressView_strokeWidth, strokeWidth);
    a.recycle();
    progress = max;
    paint.setTextSize(textSize);

    getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override public void onGlobalLayout() {
        startAnimation();
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
      }
    });
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    String text = max + "";
    resetPaint();
    paint.setColor(textColor);
    paint.setTextSize(textSize);
    paint.getTextBounds(text, 0, text.length(), textBounds);
    int size = (int) Math.max(textBounds.height(), paint.measureText(text)) + spacing + strokeWidth;
    setMeasuredDimension(size, size);
  }

  @Override protected void onDraw(Canvas canvas) {
    float center = Math.max(getWidth() >> 1, getHeight() >> 1);
    float margin = center * 0.10f; // 10% padding for the circle & border

    /* Draw background circle */
    resetPaint();
    paint.setColor(fillColor);
    paint.setStyle(Paint.Style.FILL);
    canvas.drawCircle(center, center, center - margin, paint);

    /* Draw progress background */
    resetPaint();
    paint.setColor(progressBackgroundColor);
    paint.setStrokeWidth(strokeWidth);
    paint.setStyle(Paint.Style.STROKE);
    canvas.drawCircle(center, center, center - margin, paint);

    /* Draw progress */
    paint.setColor(progressColor);
    background.set(margin, margin, getWidth() - margin, getHeight() - margin);
    path.reset();
    float t = 1 - time % 1;
    float sweep = Utilities.INSTANCE.lerp(0, 360, t);
    path.arcTo(background, 270, sweep);
    canvas.drawPath(path, paint);

    /* Draw countdown */
    String text = progress + "";
    resetPaint();
    paint.setColor(textColor);
    paint.setTextSize(textSize);
    paint.getTextBounds(text, 0, text.length(), textBounds);
    canvas.drawText(
        text,
        center - paint.measureText(text) / 2,
        center + textBounds.height() / 2,
        paint);
  }

  private void resetPaint() {
    paint.reset();
    paint.setAntiAlias(true);
    paint.setTypeface(Typeface.SANS_SERIF);
  }

  private void startAnimation() {
    if (animator != null) animator.cancel();
    animator = ValueAnimator.ofFloat(max, min);
    animator.setInterpolator(new LinearInterpolator());
    animator.setDuration(TimeUnit.SECONDS.toMillis(max));
    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override public void onAnimationUpdate(ValueAnimator animation) {
        time = (float) animation.getAnimatedValue();
        progress = (int) time;
        invalidate();
      }
    });
    animator.start();
  }
}
