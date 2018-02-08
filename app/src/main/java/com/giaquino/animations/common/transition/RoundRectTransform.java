package com.giaquino.animations.common.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Outline;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.util.AttributeSet;
import android.util.IntProperty;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import com.giaquino.animations.R;

import static android.view.View.MeasureSpec;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) public class RoundRectTransform
    extends Transition {

  /**
   * animate width and height clipping with  same rate.
   */
  private static final int CLIP_SYMMETRIC = 1;

  /**
   * animate width and height clipping with different rate.
   */
  private static final int CLIP_ASYMMETRIC = 2;

  /**
   * default duration
   */
  private static final int DEFAULT_DURATION = 375;

  /**
   * delay when doing asymmetric animation
   */
  private static final float DELAY_PERCENTAGE = 0.134F;

  /**
   * Flag to determine the direction of animation
   */
  private static final int FLAG_ENTERED = R.id.flag_round_rect_entered;

  private static final String PROP_BOUNDS = "Animations:RoundRectTransform:bounds";

  /**
   * convert radius to int due to un-precise floating operations causing
   * outline provider to have an alpha blinking effect during animation.
   */
  private final int startCornerRadius;
  private final int endCornerRadius;
  private final int startHorizontalInset;
  private final int endHorizontalInset;
  private final int startVerticalInset;
  private final int endVerticalInset;
  private final int clipType;
  private final long duration;

  public RoundRectTransform(Context context, AttributeSet attrs) {
    super(context, attrs);
    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundRectTransform);
    try {
      startCornerRadius =
          a.getDimensionPixelSize(R.styleable.RoundRectTransform_startCornerRadius, 0);
      endCornerRadius =
          a.getDimensionPixelOffset(R.styleable.RoundRectTransform_endCornerRadius, 0);
      startHorizontalInset =
          a.getDimensionPixelSize(R.styleable.RoundRectTransform_startHorizontalInset, 0) * 2;
      endHorizontalInset =
          a.getDimensionPixelSize(R.styleable.RoundRectTransform_endHorizontalInset, 0) * 2;
      startVerticalInset =
          a.getDimensionPixelSize(R.styleable.RoundRectTransform_startVerticalInset, 0) * 2;
      endVerticalInset =
          a.getDimensionPixelSize(R.styleable.RoundRectTransform_endVerticalInset, 0) * 2;
      clipType = a.getInteger(R.styleable.RoundRectTransform_clipType, CLIP_SYMMETRIC);
    } finally {
      a.recycle();
    }

    if (getDuration() != -1) {
      /* override duration so it will follow our own duration */
      duration = getDuration();
      setDuration(-1);
    } else {
      duration = DEFAULT_DURATION;
    }
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

  @Override public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues,
      TransitionValues endValues) {
    final View view = endValues.view;
    final Rect startBounds = (Rect) startValues.values.get(PROP_BOUNDS);
    final Rect endBounds = (Rect) endValues.values.get(PROP_BOUNDS);
    final boolean entering = view.getTag(FLAG_ENTERED) == null;

    /* if exiting relayout view to its starting bounds */
    if (!entering) {
      view.measure(MeasureSpec.makeMeasureSpec(startBounds.width(), MeasureSpec.EXACTLY),
          MeasureSpec.makeMeasureSpec(startBounds.height(), MeasureSpec.EXACTLY));
      view.layout(startBounds.left, startBounds.top, startBounds.right, startBounds.bottom);
    }

    /* store this props so we can restore it at the end of animation */
    final boolean viewClipToOutline = view.getClipToOutline();
    final ViewOutlineProvider viewOutlineProvider = view.getOutlineProvider();

    /* clip to start bounds */
    final RevealViewOutlineProvider outlineProvider =
        createOutlineProvider(startBounds, entering ? startHorizontalInset : endHorizontalInset,
            entering ? startVerticalInset : endVerticalInset,
            entering ? startCornerRadius : endCornerRadius);
    view.setOutlineProvider(outlineProvider);
    view.setClipToOutline(true);

    /* animate clipping for a round rect reveal */
    Animator revealWidth = createRevealWidthAnimator(outlineProvider, endBounds,
        !entering ? startHorizontalInset : endHorizontalInset);

    Animator revealHeight = createRevealHeightAnimator(outlineProvider, endBounds,
        !entering ? startVerticalInset : endVerticalInset);

    Animator cornerRadius =
        ObjectAnimator.ofInt(outlineProvider, RevealViewOutlineProvider.CORNER_RADIUS,
            (int) (!entering ? startCornerRadius : endCornerRadius));

    AnimatorSet transform = new AnimatorSet();

    cornerRadius.setDuration(duration);
    transform.playTogether(cornerRadius);

    switch (clipType) {
      case CLIP_SYMMETRIC:
        revealWidth.setDuration(duration);
        revealHeight.setDuration(duration);
        transform.playTogether(revealWidth, revealHeight);
        break;
      case CLIP_ASYMMETRIC:
        final long delay = (long) (duration * DELAY_PERCENTAGE);
        revealWidth.setDuration(duration - delay);
        revealHeight.setDuration(duration - delay);
        if (entering) {
          /* delay height transform */
          revealHeight.setStartDelay(delay);
          transform.play(revealWidth).with(revealHeight);
        } else {
          /* delay width transform */
          revealWidth.setStartDelay(delay);
          transform.play(revealHeight).with(revealWidth);
        }
        break;
    }
    transform.addListener(new AnimatorListenerAdapter() {
      @Override public void onAnimationEnd(Animator animation) {
        if (entering) {
          view.setTag(FLAG_ENTERED, true);
          view.setClipToOutline(viewClipToOutline);
          view.setOutlineProvider(viewOutlineProvider);
        }
      }
    });
    return transform;
  }

  private Animator createRevealWidthAnimator(RevealViewOutlineProvider outline, Rect bounds,
      int inset) {
    int reveal = bounds.width() - inset;
    return ObjectAnimator.ofInt(outline, RevealViewOutlineProvider.REVEAL_WIDTH, reveal);
  }

  private Animator createRevealHeightAnimator(RevealViewOutlineProvider outline, Rect bounds,
      int inset) {
    int reveal = bounds.height() - inset;
    return ObjectAnimator.ofInt(outline, RevealViewOutlineProvider.REVEAL_HEIGHT, reveal);
  }

  private RevealViewOutlineProvider createOutlineProvider(Rect bounds, int horizontalInset,
      int verticalInset, int cornerRadius) {
    int width = bounds.width() - horizontalInset;
    int height = bounds.height() - verticalInset;
    return new RevealViewOutlineProvider(width, height, cornerRadius);
  }

  /**
   * OutlineProvider to animate bounds and corner radius
   */
  private static class RevealViewOutlineProvider extends ViewOutlineProvider {

    public static final Property<RevealViewOutlineProvider, Integer> REVEAL_WIDTH =
        new IntProperty<RevealViewOutlineProvider>("revealWidth") {
          @Override public void setValue(RevealViewOutlineProvider object, int value) {
            object.setRevealWidth(value);
          }

          @Override public Integer get(RevealViewOutlineProvider object) {
            return object.getRevealWidth();
          }
        };

    public static final Property<RevealViewOutlineProvider, Integer> REVEAL_HEIGHT =
        new IntProperty<RevealViewOutlineProvider>("revealHeight") {
          @Override public void setValue(RevealViewOutlineProvider object, int value) {
            object.setRevealHeight(value);
          }

          @Override public Integer get(RevealViewOutlineProvider object) {
            return object.getRevealHeight();
          }
        };

    public static final Property<RevealViewOutlineProvider, Integer> CORNER_RADIUS =
        new IntProperty<RevealViewOutlineProvider>("cornerRadius") {
          @Override public void setValue(RevealViewOutlineProvider object, int value) {
            object.setCornerRadius(value);
          }

          @Override public Integer get(RevealViewOutlineProvider object) {
            return object.getCornerRadius();
          }
        };

    private int revealWidth;
    private int revealHeight;
    private int cornerRadius;

    public RevealViewOutlineProvider(int revealWidth, int revealHeight, int cornerRadius) {
      this.revealWidth = revealWidth;
      this.revealHeight = revealHeight;
      this.cornerRadius = cornerRadius;
    }

    @Override public void getOutline(View view, Outline outline) {
      int horizontalInset = (view.getWidth() - revealWidth) / 2;
      int verticalInset = (view.getHeight() - revealHeight) / 2;
      outline.setRoundRect(horizontalInset, verticalInset, view.getWidth() - horizontalInset,
          view.getHeight() - verticalInset, cornerRadius);
    }

    public int getRevealWidth() {
      return revealWidth;
    }

    public void setRevealWidth(int revealWidth) {
      this.revealWidth = revealWidth;
    }

    public int getRevealHeight() {
      return revealHeight;
    }

    public void setRevealHeight(int revealHeight) {
      this.revealHeight = revealHeight;
    }

    public int getCornerRadius() {
      return cornerRadius;
    }

    public void setCornerRadius(int cornerRadius) {
      this.cornerRadius = cornerRadius;
    }
  }
}
