package com.giaquino.animations.common.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.FloatProperty;
import android.util.IntProperty;
import android.util.Property;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) public class MorphDrawable extends Drawable {

  public static final Property<MorphDrawable, Integer> COLOR =
      new IntProperty<MorphDrawable>("color") {
        @Override public void setValue(MorphDrawable object, int value) {
          object.setColor(value);
        }

        @Override public Integer get(MorphDrawable object) {
          return object.getColor();
        }
      };

  public static final Property<MorphDrawable, Float> CORNER_RADIUS =
      new FloatProperty<MorphDrawable>("cornerRadius") {
        @Override public void setValue(MorphDrawable object, float value) {
          object.setCornerRadius(value);
        }

        @Override public Float get(MorphDrawable object) {
          return object.getCornerRadius();
        }
      };

  private final Paint paint;
  private float cornerRadius;

  public MorphDrawable(int color, float cornerRadius) {
    this.cornerRadius = cornerRadius;
    paint = new Paint();
    paint.setColor(color);
    paint.setAntiAlias(true);
    paint.setStyle(Paint.Style.FILL);
  }

  @Override public void draw(@NonNull Canvas canvas) {
    final Rect rect = getBounds();
    canvas.drawRoundRect(rect.left, rect.top, rect.right, rect.bottom, cornerRadius, cornerRadius, paint);
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

  @Override public void getOutline(@NonNull Outline outline) {
    outline.setRoundRect(getBounds(), cornerRadius);
  }

  @Keep public int getColor() {
    return paint.getColor();
  }

  @Keep public void setColor(int color) {
    this.paint.setColor(color);
    invalidateSelf();
  }

  @Keep public float getCornerRadius() {
    return cornerRadius;
  }

  @Keep public void setCornerRadius(float cornerRadius) {
    this.cornerRadius = cornerRadius;
    invalidateSelf();
  }
}
