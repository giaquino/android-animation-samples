package com.giaquino.animations.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import com.giaquino.animations.R;
import com.giaquino.animations.common.drawable.SnapshotDrawable;

public class SnapshotActivity extends AppCompatActivity {

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_snapshot);

    final ViewGroup container = (ViewGroup) findViewById(R.id.container);

    container.getViewTreeObserver()
        .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
          @Override public void onGlobalLayout() {
            final ViewGroup snapshot =
                (ViewGroup) getLayoutInflater().inflate(R.layout.layout_snapshot, container, false);
            final SnapshotDrawable drawable = new SnapshotDrawable(snapshot, container);

            final ObjectAnimator animator =
                ObjectAnimator.ofFloat(drawable, "gradientX", -drawable.getBounds().width() * 1.2f,
                    drawable.getBounds().width() * 1.2f).setDuration(1500);
            animator.setRepeatCount(2);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
              @Override public void onAnimationUpdate(ValueAnimator animation) {
                drawable.invalidateSelf();
              }
            });
            animator.setInterpolator(new AccelerateDecelerateInterpolator());

            ObjectAnimator alpha = ObjectAnimator.ofInt(drawable, "alpha", 255, 0);
            alpha.setDuration(300);
            alpha.setInterpolator(new LinearInterpolator());
            alpha.addListener(new AnimatorListenerAdapter() {
              @Override public void onAnimationStart(Animator animation) {
              }
              @Override public void onAnimationEnd(Animator animation) {
                container.getOverlay().clear();
              }
            });

            ObjectAnimator alpha2 = ObjectAnimator.ofFloat(snapshot, View.ALPHA, 0, 1F);
            alpha2.setDuration(300);
            alpha.setInterpolator(new LinearInterpolator());
            alpha.addListener(new AnimatorListenerAdapter() {
              @Override public void onAnimationStart(Animator animation) {
                snapshot.setAlpha(0);
                container.addView(snapshot);
              }
            });

            container.getOverlay().add(drawable);
            container.getViewTreeObserver().removeOnGlobalLayoutListener(this);

            AnimatorSet set = new AnimatorSet();
            set.playSequentially(animator, alpha, alpha2);
            set.start();
          }
        });
  }
}
