package com.giaquino.animations.common.util

import android.content.res.Resources

object Utilities {

  /**
   * returns the (x,y) position for the given degrees of a circle.
   */
  @JvmStatic
  fun getCircleCoordinateForDegrees(degrees: Float, radius: Float, centerX: Float,
      centerY: Float): FloatArray {
    val radians = Math.toRadians(degrees.toDouble());
    val coordinates = FloatArray(2);
    coordinates[0] = Math.cos(radians).toFloat() * radius + centerX;
    coordinates[1] = Math.sin(radians).toFloat() * radius + centerY;
    return coordinates;
  }

  @JvmStatic
  fun lerp(a: Float, b: Float, t: Float): Float {
    return a + (b - a) * t
  }

  @JvmStatic fun dpToPx(dp: Int): Int {
    val metrics = Resources.getSystem().displayMetrics
    val px = dp * (metrics.densityDpi / 160f)
    return Math.round(px)
  }
}
