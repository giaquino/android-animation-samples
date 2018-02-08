package com.giaquino.animations.common.drawable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class SnapshotDrawable extends Drawable {

  private Paint paint;
  private ViewGroup snapshot;
  private List<RectF> bounds = new ArrayList<>();
  private Matrix linearGradientMatrix;
  private LinearGradient linearGradient;
  private float gradientX = 0;


  public SnapshotDrawable(ViewGroup snapshot, ViewGroup container) {
    this.snapshot = snapshot;
    paint = new Paint();

    paint.setAntiAlias(true);
    paint.setStyle(Paint.Style.FILL);
    paint.setColor(Color.parseColor("#99323232"));

    int width = View.MeasureSpec.makeMeasureSpec(
        container.getWidth() - container.getPaddingLeft() + container.getPaddingRight(),
        View.MeasureSpec.AT_MOST);
    int height = View.MeasureSpec.makeMeasureSpec(
        container.getHeight() - container.getPaddingTop() + container.getPaddingBottom(),
        View.MeasureSpec.AT_MOST);

    snapshot.measure(width, height);
    snapshot.layout(container.getLeft(), container.getTop(), container.getRight(),
        container.getBottom());

    getBounds(snapshot, bounds);
    setBounds(container.getLeft(), container.getTop(), container.getRight(), container.getBottom());

    linearGradientMatrix = new Matrix();
    resetLinearGradient();
  }

  private void getBounds(ViewGroup group, List<RectF> bounds) {
    for (int i = group.getChildCount() - 1; i >= 0; i--) {
      View child = snapshot.getChildAt(i);
      if (child instanceof ViewGroup) {
        getBounds((ViewGroup) child, bounds);
      } else if (canGetBounds(child)) {
        bounds.add(new RectF(child.getLeft(), child.getTop(), child.getRight(), child.getBottom()));
      }
    }
  }

  private boolean canGetBounds(View view) {
    return view instanceof TextView || view instanceof ImageView;
  }

  @Override public void draw(@NonNull Canvas canvas) {
    linearGradientMatrix.setTranslate(gradientX, 0);
    linearGradient.setLocalMatrix(linearGradientMatrix);
    Log.d("WAPAK", "draw");
    paint.setShader(null);
    for (RectF rect : bounds) {
      Log.d("WAPAK", "draw bounds " + rect);
      canvas.drawRoundRect(rect, 16,16, paint);
    }
    paint.setShader(linearGradient);
    for (RectF rect : bounds) {
      Log.d("WAPAK", "draw bounds " + rect);
      canvas.drawRoundRect(rect,16, 16, paint);
    }
  }

  @Override public void setAlpha(int alpha) {
    paint.setAlpha(alpha);
    invalidateSelf();
  }

  @Override public void setColorFilter(@Nullable ColorFilter colorFilter) {
    paint.setColorFilter(colorFilter);
    invalidateSelf();
  }

  @Override public int getOpacity() {
    return paint.getAlpha() == 255 ? PixelFormat.OPAQUE : PixelFormat.TRANSLUCENT;
  }

  public void setGradientX(float gradientX) {
    this.gradientX = gradientX;
    invalidateSelf();
  }

  public float getGradientX() {
    return gradientX;
  }

  private void resetLinearGradient() {
    linearGradient = new LinearGradient(0, 0, getBounds().width(), 0, new int[] {
        Color.parseColor("#99323232"), Color.parseColor("#66FFFFFF"), Color.parseColor("#99323232"),
    }, new float[] {
        0, 0.5F, 1
    }, Shader.TileMode.CLAMP);
    paint.setShader(linearGradient);
  }
}
