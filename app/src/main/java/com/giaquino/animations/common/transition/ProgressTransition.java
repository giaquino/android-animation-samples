package com.giaquino.animations.common.transition;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.Transition;
import android.support.transition.TransitionValues;
import android.util.IntProperty;
import android.util.Property;
import android.view.ViewGroup;
import android.widget.ProgressBar;

public class ProgressTransition extends Transition {

  private static final String PROP_PROGRESS = "ProgressTransition:progress";

  private static final Property<ProgressBar, Integer> PROGRESS_PROPERTY =
      new IntProperty<ProgressBar>("Property:ProgressBar") {
        @Override public void setValue(ProgressBar progressBar, int value) {
          progressBar.setProgress(value);
        }

        @Override public Integer get(ProgressBar progressBar) {
          return progressBar.getProgress();
        }
      };

  @Override public void captureEndValues(@NonNull TransitionValues transitionValues) {
    captureValues(transitionValues);
  }

  @Override public void captureStartValues(@NonNull TransitionValues transitionValues) {
    captureValues(transitionValues);
  }

  @Nullable @Override public Animator createAnimator(@NonNull ViewGroup sceneRoot,
      @Nullable TransitionValues startValues, @Nullable TransitionValues endValues) {
    if (startValues == null || endValues == null || !(endValues.view instanceof ProgressBar)) {
      return null;
    }
    ProgressBar progressBar = (ProgressBar) endValues.view;
    int start = (int) startValues.values.get(PROP_PROGRESS);
    int end = (int) endValues.values.get(PROP_PROGRESS);
    if (start != end) {
      progressBar.setProgress(start);
      return ObjectAnimator.ofInt(progressBar, PROGRESS_PROPERTY, end);
    }
    return null;
  }

  private void captureValues(TransitionValues transitionValues) {
    if (transitionValues.view instanceof ProgressBar) {
      transitionValues.values.put(PROP_PROGRESS,
          ((ProgressBar) transitionValues.view).getProgress());
    }
  }
}
