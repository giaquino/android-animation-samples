package com.giaquino.animations.common.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.Window;
import com.giaquino.animations.R;
import java.util.ArrayList;
import java.util.List;

import static android.view.View.MeasureSpec;
import static android.view.View.TRANSLATION_X;
import static android.view.View.TRANSLATION_Y;

/**
 * Transition that allows transformation from a circular view to other shape.
 */
@TargetApi(Build.VERSION_CODES.KITKAT) public class CircularTransform extends Transition {

  private static final String PROP_BOUNDS = "CircularTransform:BOUNDS";

  private static final String EXTRA_ICON = "CircularTransform:EXTRA_ICON";
  private static final String EXTRA_COLOR = "CircularTransform:EXTRA_COLOR";

  private static final long DEFAULT_DURATION = 240L;

  @ColorInt private final int color;
  @DrawableRes private final int icon;

  /**
   * Make an intent for starting an activity ready for performing circular transform.
   */
  public static void addExtras(@NonNull Intent intent, @ColorRes int color, @DrawableRes int icon) {
    intent.putExtra(EXTRA_COLOR, color);
    intent.putExtra(EXTRA_ICON, icon);
  }

  /**
   * Setup circular transform to this activity if possible.
   * This must be called before `setContentView()`
   */
  public static void setup(Activity activity, String target) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      return;
    }
    Intent intent = activity.getIntent();
    if (intent.hasExtra(EXTRA_ICON) && intent.hasExtra(EXTRA_COLOR)) {
      int color = intent.getIntExtra(EXTRA_COLOR, 0);
      int icon = intent.getIntExtra(EXTRA_ICON, 0);
      CircularTransform transition = new CircularTransform(color, icon);
      transition.addTarget(target);
      activity.getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
      activity.getWindow().setSharedElementEnterTransition(transition);
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) public CircularTransform(int color, int icon) {
    this.color = color;
    this.icon = icon;
    setDuration(DEFAULT_DURATION);
    setPathMotion(new GravityArcMotion());
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  public CircularTransform(Context context, AttributeSet attrs) {
    super(context, attrs);
    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircularTransform);
    color = a.getColor(R.styleable.CircularTransform_sourceBackgroundColor, Color.TRANSPARENT);
    icon = a.getResourceId(R.styleable.CircularTransform_sourceIcon, 0);
    a.recycle();
    setPathMotion(new GravityArcMotion());
  }

  @Override public void captureStartValues(TransitionValues transitionValues) {
    captureValues(transitionValues);
  }

  @Override public void captureEndValues(TransitionValues transitionValues) {
    captureValues(transitionValues);
  }

  private void captureValues(TransitionValues transitionValues) {
    final View view = transitionValues.view;
    if (view == null || view.getWidth() <= 0 || view.getHeight() <= 0) {
      return;
    }
    transitionValues.values.put(PROP_BOUNDS,
        new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom()));
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) @Override
  public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues,
      TransitionValues endValues) {
    if (startValues == null || endValues == null) {
      return null;
    }
    final View view = endValues.view;
    final Rect startBounds = (Rect) startValues.values.get(PROP_BOUNDS);
    final Rect endBounds = (Rect) endValues.values.get(PROP_BOUNDS);

    /* source view is expected to have less width */
    final boolean entering = endBounds.width() > startBounds.width();

    /*
      Scene transition automatically resize the target to obtain start and end values.

      on enter:
      1. view will get resize base on the source view
      2. transition will capture start values
      3. view will get resize to its original size
      4. transition will capture end values
      - upon enter, view is already on its end size

      on exit:
      1. because view is already on its end size it will capture start values
      2. view will get resize to the source view
      3. transition will capture end values
      - upon exit view is already on its end size so we need to do force layout to its start size.
     */
    if (!entering) {
      view.measure(MeasureSpec.makeMeasureSpec(startBounds.width(), MeasureSpec.EXACTLY),
          MeasureSpec.makeMeasureSpec(startBounds.height(), MeasureSpec.EXACTLY));
      view.layout(startBounds.left, startBounds.top, startBounds.right, startBounds.bottom);
    }

    /* add color overlay using the background of the source so they will look like the same */
    Drawable overlay = createColorDrawable(color, entering ? endBounds : startBounds);
    view.getOverlay().add(overlay);

    final long duration = getDuration();
    final long halfDuration = duration / 2;
    final long thirdDuration = duration / 3;

    /* animate child views */
    List<Animator> childViewAnimators = null;
    if (view instanceof ViewGroup) {
      ViewGroup group = (ViewGroup) view;
      childViewAnimators = new ArrayList<>(group.getChildCount());
      for (int i = group.getChildCount() - 1; i >= 0; i--) {
        View child = group.getChildAt(i);
        float startAlpha = entering ? 0f : child.getAlpha();
        float endAlpha = entering ? child.getAlpha() : 0f;
        childViewAnimators.add(ObjectAnimator.ofFloat(child, View.ALPHA, startAlpha, endAlpha)
            .setDuration(thirdDuration));
      }
    }

    /* animate overlay alpha */
    final Animator alpha = createAlphaAnimator(overlay, thirdDuration, entering);

    /* animate from and to translation */
    final Animator translate = createTranslateAnimator(view, startBounds, endBounds, entering);

    /* animate circular reveal so it will look like from the source view */
    final Animator reveal = createCircularRevealAnimator(view, startBounds, endBounds, entering);

    final AnimatorSet animators = new AnimatorSet();

    /* work around for shadow drawing twice when returning */
    if (!entering) {
      final Animator z = ObjectAnimator.ofFloat(view, View.TRANSLATION_Z, -view.getElevation());
      animators.play(z);
    }
    animators.playTogether(alpha, translate, reveal);
    if (childViewAnimators != null) animators.playTogether(childViewAnimators);
    animators.setDuration(duration);
    animators.addListener(new AnimatorListenerAdapter() {
      @Override public void onAnimationEnd(Animator animation) {
        if (entering) view.getOverlay().clear();
      }
    });
    return animators;
  }

  private Drawable createColorDrawable(int color, Rect bounds) {
    ColorDrawable drawable = new ColorDrawable(color);
    drawable.setBounds(0, 0, bounds.width(), bounds.height());
    return drawable;
  }

  /**
   * Creates an alpha animator that will animate from 255 - 0 or 0 - 255.
   */
  @SuppressLint("ObjectAnimatorBinding") private Animator createAlphaAnimator(Object object,
      long duration, boolean entering) {
    return ObjectAnimator.ofInt(object, "alpha", entering ? 255 : 0, entering ? 0 : 255)
        .setDuration(duration);
  }

  /**
   * Translates the view base on the start and end values.
   */
  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) private Animator createTranslateAnimator(
      View view, Rect start, Rect end, boolean entering) {
    final int translationX = start.centerX() - end.centerX();
    final int translationY = start.centerY() - end.centerY();
    if (entering) {
      view.setTranslationX(translationX);
      view.setTranslationY(translationY);
      return ObjectAnimator.ofFloat(view, TRANSLATION_X, TRANSLATION_Y,
          getPathMotion().getPath(translationX, translationY, 0, 0));
    } else {
      return ObjectAnimator.ofFloat(view, TRANSLATION_X, TRANSLATION_Y,
          getPathMotion().getPath(0, 0, -translationX, -translationY));
    }
  }

  /**
   * Creates an animator that will animate a circular reveal base on the start and end values.
   */
  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) private Animator createCircularRevealAnimator(
      final View view, final Rect start, final Rect end, boolean entering) {
    final int cx = view.getWidth() / 2;
    final int cy = view.getHeight() / 2;
    /* compute start and end radius */
    final float startRadius, endRadius;
    if (entering) {
      startRadius = start.width() / 2;
      endRadius = (float) Math.hypot(end.width() / 2, end.height() / 2);
    } else {
      startRadius = (float) Math.hypot(start.width() / 2, start.height() / 2);
      endRadius = end.width() / 2;
    }
    final Animator animator =
        ViewAnimationUtils.createCircularReveal(view, cx, cy, startRadius, endRadius);

    /* maintain circular mask at the end of animation */
    if (!entering) {
      animator.addListener(new AnimatorListenerAdapter() {
        @Override public void onAnimationEnd(Animator animation) {
          view.setOutlineProvider(createOvalOutlineProvider(end));
        }
      });
    }
    return animator;
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  private ViewOutlineProvider createOvalOutlineProvider(final Rect bounds) {
    return new ViewOutlineProvider() {
      @Override public void getOutline(View view, Outline outline) {
        final int left = (view.getWidth() - bounds.width()) / 2;
        final int top = (view.getHeight() - bounds.height()) / 2;
        outline.setOval(left, top, left + bounds.width(), top + bounds.height());
        view.setClipToOutline(true);
      }
    };
  }
}
