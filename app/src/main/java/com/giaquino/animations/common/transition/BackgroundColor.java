package com.giaquino.animations.common.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.util.AttributeSet;
import android.util.IntProperty;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import com.giaquino.animations.R;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Transition a view's background using {@link ColorDrawable} by supplying
 * <code>startColor</code> and <code>endColor</code>.
 *
 * <p>
 * This will also do alpha animation on child views if the target view is a {@link ViewGroup}
 * and can be controlled by supplying animateChildren.
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP) @TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class BackgroundColor extends Transition {

  private static final Property<ColorDrawable, Integer> COLOR_DRAWABLE_PROPERTY =
      new IntProperty<ColorDrawable>("color") {
        @Override public void setValue(ColorDrawable object, int value) {
          object.setColor(value);
        }

        @Override public Integer get(ColorDrawable object) {
          return object.getColor();
        }
      };

  /**
   * Flag wes set as tag to determine if transition is entering or exiting.
   */
  private static final int FLAG_ENTERED = R.id.flag_background_color_transition_entered;

  private final int startColor;
  private final int endColor;
  private final boolean animateChildren;

  public BackgroundColor(int startColor, int endColor, boolean animateChildren) {
    this.startColor = startColor;
    this.endColor = endColor;
    this.animateChildren = animateChildren;
  }

  public BackgroundColor(Context context, AttributeSet attrs) {
    super(context, attrs);
    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BackgroundColor);
    startColor = a.getColor(R.styleable.BackgroundColor_startColor, Color.TRANSPARENT);
    endColor = a.getColor(R.styleable.BackgroundColor_endColor, Color.TRANSPARENT);
    animateChildren = a.getBoolean(R.styleable.BackgroundColor_animateChildren, false);
    a.recycle();
  }

  @Override public void captureStartValues(TransitionValues transitionValues) {
  }

  @Override public void captureEndValues(TransitionValues transitionValues) {
  }

  @Override public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues,
      TransitionValues endValues) {

    if (startValues == null || endValues == null || startColor == endColor) {
      return null;
    }
    final View view = endValues.view;
    final boolean entering = view.getTag(FLAG_ENTERED) == null;

    List<Animator> childAlphaAnimators = null;
    if (animateChildren && view instanceof ViewGroup) {
      ViewGroup parent = (ViewGroup) view;
      childAlphaAnimators = new ArrayList<>(parent.getChildCount());
      for (int i = parent.getChildCount() - 1; i >= 0; i--) {
        View child = parent.getChildAt(i);
        if (entering) child.setAlpha(0);
        childAlphaAnimators.add(ObjectAnimator.ofFloat(child, View.ALPHA, entering ? 1F : 0F));
      }
    }

    final Drawable originalBackground = view.getBackground();

    ColorDrawable colorBackground = new ColorDrawable(entering ? startColor : endColor);
    view.setBackground(colorBackground);

    ObjectAnimator colorAnimator = ObjectAnimator.ofArgb(colorBackground, COLOR_DRAWABLE_PROPERTY,
        !entering ? startColor : endColor);

    AnimatorSet transition = new AnimatorSet();
    transition.play(colorAnimator);
    if (childAlphaAnimators != null) transition.playTogether(childAlphaAnimators);
    transition.addListener(new AnimatorListenerAdapter() {
      @Override public void onAnimationEnd(Animator animation) {
        if (entering) {
          view.setTag(FLAG_ENTERED, true);
          view.setBackground(originalBackground);
        }
      }
    });
    return transition;
  }
}
