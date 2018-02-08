package com.giaquino.animations.common.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

public class RainbowTextView extends AppCompatTextView {

  public static final int[] RAINBOW_COLORS = new int[] {
      Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE, Color.MAGENTA
  };

  public RainbowTextView(Context context) {
    super(context);
  }

  public RainbowTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public RainbowTextView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    LinearGradient shader =
        new LinearGradient(0, 0, w, 0, RAINBOW_COLORS, null, Shader.TileMode.MIRROR);
    getPaint().setShader(shader);
  }
}
