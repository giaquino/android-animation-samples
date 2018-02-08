package com.giaquino.animations.common.transition;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import static android.view.View.TRANSLATION_X;
import static android.view.View.TRANSLATION_Y;

@TargetApi(Build.VERSION_CODES.LOLLIPOP) public class Move extends Transition {

  private static final String PROP_BOUNDS = "animations:translateTransition:bounds";

  public Move() {
    setPathMotion(new GravityArcMotion());
  }

  public Move(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override public void captureStartValues(TransitionValues transitionValues) {
    captureValues(transitionValues);
  }

  @Override public void captureEndValues(TransitionValues transitionValues) {
    captureValues(transitionValues);
  }

  private void captureValues(TransitionValues values) {
    final View view = values.view;
    if (view.isLaidOut() || view.getWidth() != 0 || view.getHeight() != 0) {
      values.values.put(PROP_BOUNDS,
          new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom()));
    }
  }

  @Override public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues,
      TransitionValues endValues) {
    final View view = endValues.view;
    final Rect startBounds = (Rect) startValues.values.get(PROP_BOUNDS);
    final Rect endBounds = (Rect) endValues.values.get(PROP_BOUNDS);
    if (view == null || startBounds == null || endBounds == null || startBounds.equals(endBounds)) {
      return null;
    }
    final float translateX = startBounds.centerX() - endBounds.centerX();
    final float translateY = startBounds.centerY() - endBounds.centerY();
    final Rect viewBounds =
        new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
    if (viewBounds.equals(endBounds)) {
      return ObjectAnimator.ofFloat(view, TRANSLATION_X, TRANSLATION_Y,
          getPathMotion().getPath(translateX, translateY, 0, 0));
    } else {
      return ObjectAnimator.ofFloat(view, TRANSLATION_X, TRANSLATION_Y,
          getPathMotion().getPath(0, 0, -translateX, -translateY));
    }
  }
}
