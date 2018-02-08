package com.giaquino.animations.common.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.transition.TransitionValues;
import android.transition.Visibility;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;

@TargetApi(Build.VERSION_CODES.KITKAT) public class RevealTransition extends Visibility {

  public RevealTransition() {
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public RevealTransition(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override public Animator onAppear(ViewGroup sceneRoot, View view, TransitionValues startValues,
      TransitionValues endValues) {
    return createAppearAnimator(view);
  }

  @Override
  public Animator onDisappear(ViewGroup sceneRoot, View view, TransitionValues startValues,
      TransitionValues endValues) {
    return createDisappearAnimator(view);
  }

  private Animator createAppearAnimator(final View view) {
    /* hide view before the start of the animation */
    final float alpha = view.getAlpha();
    view.setAlpha(0f);

    final float radius = calculateRadius(view);
    Animator animator = createAnimator(view, 0, radius);
    animator.addListener(new AnimatorListenerAdapter() {
      @Override public void onAnimationStart(Animator animation) {
        view.setAlpha(alpha);
      }
    });
    return animator;
  }

  private Animator createDisappearAnimator(final View view) {
    return createAnimator(view, calculateRadius(view), 0);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  private Animator createAnimator(View view, float startRadius, float endRadius) {
    int centerX = view.getWidth() / 2;
    int centerY = view.getHeight() / 2;
    return ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius, endRadius);
  }

  private float calculateRadius(View view) {
    return (float) Math.hypot(view.getWidth() / 2, view.getHeight() / 2);
  }
}