package com.giaquino.animations.common.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import com.giaquino.animations.common.util.Utilities;

public class PieProgressView extends View {

  private final Paint paint = new Paint();

  /* path for drawing the pie progress */
  private final Path path = new Path();

  private final RectF bounds = new RectF();
  private final RectF innerBounds = new RectF();
  private final RectF outerBounds = new RectF();

  private float maxProgress = 100;
  private float progress = 0;

  private float center;
  private float innerRadius;

  /* starting angle of the pie */
  private float startAngle = 270;

  /* resolve position of the starting angle */
  private float[] startAnglePosition;

  public PieProgressView(Context context) {
    super(context);
    initialize();
  }

  public PieProgressView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    initialize();
  }

  public PieProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initialize();
  }

  private void initialize() {
    paint.setAntiAlias(true);
    paint.setColor(Color.parseColor("#AA323232"));
    paint.setStyle(Paint.Style.FILL);
    startAnimation();
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int size = MeasureSpec.getSize(widthMeasureSpec);
    int mode = MeasureSpec.getMode(widthMeasureSpec);
    switch (mode) {
      case MeasureSpec.AT_MOST:
      case MeasureSpec.UNSPECIFIED:
        size = Math.min(Utilities.dpToPx(44), size);
        break;
      case MeasureSpec.EXACTLY:
        break;
    }
    int spec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
    super.onMeasure(spec, spec);
  }

  @Override protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);
    if (!changed) return;

    bounds.set(0, 0, getWidth(), getHeight());

    center = getWidth() / 2;
    float outerPadding = center * 0.25f;
    float innerPadding = center * 0.40f;
    innerRadius = center - innerPadding;

    startAnglePosition =
        Utilities.getCircleCoordinateForDegrees(startAngle, innerRadius, center, center);

    innerBounds.set(innerPadding, innerPadding, getWidth() - innerPadding,
        getHeight() - innerPadding);
    outerBounds.set(outerPadding, outerPadding, getWidth() - outerPadding,
        getHeight() - outerPadding);
  }

  @Override protected void onDraw(Canvas canvas) {
    if (progress == maxProgress) {
      return;
    }
    float progressAngle = Utilities.lerp(0, 360, progress / maxProgress);
    float[] sweepAnglePosition =
        Utilities.getCircleCoordinateForDegrees(startAngle + progressAngle, innerRadius, center,
            center);

    /* pie */
    if (progress == 0) {
      canvas.drawOval(innerBounds, paint);
    } else {
      path.rewind();
      path.setFillType(Path.FillType.EVEN_ODD);
      path.moveTo(startAnglePosition[0], startAnglePosition[1]);
      path.lineTo(center, center);
      path.lineTo(sweepAnglePosition[0], sweepAnglePosition[1]);
      path.arcTo(innerBounds, startAngle + progressAngle, 360 - progressAngle);
      canvas.drawPath(path, paint);
    }

    /* background fill */
    path.rewind();
    path.setFillType(Path.FillType.EVEN_ODD);
    path.addOval(outerBounds, Path.Direction.CW);
    path.addRoundRect(bounds, 20, 20, Path.Direction.CW);
    canvas.drawPath(path, paint);
  }

  private void startAnimation() {
    getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override public void onGlobalLayout() {
        if (ViewCompat.isLaidOut(PieProgressView.this)) {
          ValueAnimator progressAnimator =
              ObjectAnimator.ofFloat(PieProgressView.this, "progress", 0, maxProgress);
          progressAnimator.setInterpolator(new LinearInterpolator());
          progressAnimator.setDuration(1000 * 5);
          progressAnimator.setStartDelay(500);
          progressAnimator.start();
          getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
      }
    });
  }

  @Keep public float getProgress() {
    return progress;
  }

  @Keep public void setProgress(float progress) {
    this.progress = progress;
    this.invalidate();
  }

  @Keep public void setMaxProgress(float maxProgress) {
    this.maxProgress = maxProgress;
    this.invalidate();
  }

  @Keep public float getMaxProgress() {
    return maxProgress;
  }
}
