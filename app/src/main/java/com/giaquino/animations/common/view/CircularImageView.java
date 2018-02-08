package com.giaquino.animations.common.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import com.giaquino.animations.common.util.Utilities;

public class CircularImageView extends AppCompatImageView {

  private boolean initialized;
  private boolean drawn;
  private Bitmap bitmap;
  private Bitmap modified;
  private BitmapShader shader;
  private Paint paint = new Paint();
  private Matrix matrix = new Matrix();
  private RectF sourceBounds = new RectF();
  private RectF targetBounds = new RectF();
  private float centerX;
  private float centerY;
  private float radius;
  private Canvas softCanvas = new Canvas();

  public CircularImageView(Context context) {
    super(context);
    init();
  }

  public CircularImageView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public CircularImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  @Override public void setImageDrawable(@Nullable Drawable drawable) {
    if (drawable instanceof BitmapDrawable) {
      Bitmap temp = ((BitmapDrawable) drawable).getBitmap();
      if (temp.sameAs(bitmap)) return;
      bitmap = temp;
      drawn = false;
      update();
    }

  }

  private void init() {
    shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
    sourceBounds.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
    initialized = true;
  }

  private void update() {
    if (initialized) {
      shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
      sourceBounds.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
    }
  }

  @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    centerX = getWidth() / 2;
    centerY = getHeight() / 2;
    radius = Math.min(centerX, centerY);
    radius = radius - (radius * .15F);
  }

  @Override protected void onDraw(Canvas canvas) {
    if (!drawn) {
      modified = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
      softCanvas.setBitmap(modified);
      drawShadow(softCanvas);
      drawBitmap(softCanvas);
      drawBorder(softCanvas);
      drawGloss(softCanvas);
      drawn = true;
      paint.reset();
      paint.setAntiAlias(true);
    }
   canvas.drawBitmap(modified, 0, 0, paint);
  }

  private void drawShadow(Canvas canvas) {
    paint.reset();
    paint.setAntiAlias(true);
    paint.setStrokeWidth(3);
    paint.setColor(Color.WHITE);
    paint.setStyle(Paint.Style.FILL);
    paint.setShadowLayer(radius * .15F, 2, 2, Color.LTGRAY);
    canvas.drawCircle(centerX, centerY, radius, paint);
  }

  private void drawBitmap(Canvas canvas) {
    targetBounds.set(0, 0, getWidth(), getHeight());
    matrix.set(null);
    matrix.setRectToRect(sourceBounds, targetBounds, Matrix.ScaleToFit.CENTER);
    shader.setLocalMatrix(matrix);
    paint.reset();
    paint.setShader(shader);
    paint.setAntiAlias(true);
    canvas.drawCircle(centerX, centerY, radius, paint);
  }

  private void drawBorder(Canvas canvas) {
    paint.reset();
    paint.setAntiAlias(true);
    paint.setStrokeWidth(3);
    paint.setColor(0xFFAEAEAE);
    paint.setStyle(Paint.Style.STROKE);
    canvas.drawCircle(centerX, centerY, radius, paint);
  }

  private void drawGloss(Canvas canvas) {
    LinearGradient gradient = new LinearGradient(0, getHeight(), getWidth(), 0,
        new int[] { 0x20FFFFFF, 0x20FFFFFF }, new float[] { 0, getHeight() / 2 },
        Shader.TileMode.CLAMP);
    paint.reset();
    paint.setAntiAlias(true);
    paint.setShader(gradient);
    paint.setStyle(Paint.Style.FILL);

    Path path = new Path();
    float[] start = Utilities.getCircleCoordinateForDegrees(200, radius, centerX, centerY);
    float[] end = Utilities.getCircleCoordinateForDegrees(35, radius, centerX, centerY);

    path.moveTo(start[0], start[1]);
    final float x2 = start[0] + Math.abs(start[0] - end[0]) / 5;
    final float y2 = end[1] + Math.abs(start[1] - end[1]) / 5;
    path.quadTo(x2, y2, end[0], end[1]);
    path.arcTo(new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius), 200, 195);
    path.setFillType(Path.FillType.WINDING);
    canvas.drawPath(path, paint);
  }
}