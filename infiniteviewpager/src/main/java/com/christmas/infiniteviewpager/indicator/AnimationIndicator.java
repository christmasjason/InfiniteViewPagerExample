package com.christmas.infiniteviewpager.indicator;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

import com.christmas.infiniteviewpager.R;
import com.christmas.infiniteviewpager.salvageAdapter.RecyclingPagerAdapter;

import static android.support.v4.view.ViewPager.OnPageChangeListener;

/**
 * 动画效果的指示器.
 */
public class AnimationIndicator extends LinearLayout implements BaseIndicator {
  private static final int DEFAULT_INDICATOR_WIDTH = 5;

  private int indicatorWidth = -1;
  private int indicatorHeight = -1;
  private int indicatorMargin = -1;

  private ViewPager viewPager;
  private OnPageChangeListener onPageChangeListener;

  private int currentPage = 0;

  private int animatorResId = R.animator.anim_scale_by_alpha;
  private int indicatorBackground = R.drawable.shape_round_oval_solid_white;

  private AnimatorSet animationIn;
  private AnimatorSet animationOut;

  public AnimationIndicator(Context context) {
    super(context);
    init(context, null);
  }

  public AnimationIndicator(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  private void init(Context context, AttributeSet attrs) {
    setOrientation(LinearLayout.HORIZONTAL);
    setGravity(Gravity.CENTER);
    handleTypedArray(context, attrs);

    animationOut = (AnimatorSet) AnimatorInflater.loadAnimator(context, animatorResId);
    animationOut.setInterpolator(new LinearInterpolator());
    animationIn = (AnimatorSet) AnimatorInflater.loadAnimator(context, animatorResId);
    animationIn.setInterpolator(new ReverseInterpolator());
  }

  private void handleTypedArray(Context context, AttributeSet attrs) {
    if (attrs != null) {
      TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AnimationIndicator);
      indicatorWidth = typedArray.getDimensionPixelSize(R.styleable.AnimationIndicator_indicatorWidth, -1);
      indicatorHeight = typedArray.getDimensionPixelSize(R.styleable.AnimationIndicator_indicatorHeight, -1);
      indicatorMargin = typedArray.getDimensionPixelSize(R.styleable.AnimationIndicator_indicatorMargin, -1);
      animatorResId = typedArray.getResourceId(R.styleable.AnimationIndicator_indicatorAnimator, R.animator.anim_scale_by_alpha);
      indicatorBackground = typedArray.getResourceId(R.styleable.AnimationIndicator_indicatorDrawable, R.drawable.shape_round_oval_solid_white);
      typedArray.recycle();
    }

    indicatorWidth =
        (indicatorWidth == -1) ? dip2px(DEFAULT_INDICATOR_WIDTH) : indicatorWidth;
    indicatorHeight =
        (indicatorHeight == -1) ? dip2px(DEFAULT_INDICATOR_WIDTH) : indicatorHeight;
    indicatorMargin =
        (indicatorMargin == -1) ? dip2px(DEFAULT_INDICATOR_WIDTH) : indicatorMargin;
  }

  @Override
  public void setViewPager(ViewPager viewPager) {
    this.viewPager = viewPager;
    createIndicators();
    this.viewPager.clearOnPageChangeListeners();
    this.viewPager.addOnPageChangeListener(this);
  }

  @Override
  public void setViewPager(ViewPager viewPager, int initialPosition) {
    setViewPager(viewPager);
    setCurrentItem(initialPosition);
  }

  @Override
  public void setCurrentItem(int item) {
    if (this.viewPager == null) {
      throw new IllegalStateException("ViewPager has not been found.");
    }

    this.viewPager.setCurrentItem(item);
    currentPage = item;
    invalidate();
  }

  @Override
  public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
    if (this.viewPager == null) {
      throw new IllegalStateException("ViewPager has not been found, set ViewPager first.");
    }
    this.onPageChangeListener = onPageChangeListener;
  }

  @Override
  public void notifyDataSetChanged() {
    createIndicators();
  }

  @Override
  public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    if (this.onPageChangeListener != null) {
      this.onPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
    }
  }

  @Override
  public void onPageSelected(int position) {
    if (this.onPageChangeListener != null) {
      this.onPageChangeListener.onPageSelected(position);
    }

    if (getRealChildAt(currentPage) == null) {
      return;
    }

    animationIn.setTarget(getRealChildAt(currentPage));
    animationIn.start();
    animationOut.setTarget(getRealChildAt(position));
    animationOut.start();

    currentPage = position;
  }

  @Override
  public void onPageScrollStateChanged(int state) {
    if (this.onPageChangeListener != null) {
      this.onPageChangeListener.onPageScrollStateChanged(state);
    }
  }

  private View getRealChildAt(int position) {
    return getChildAt(((RecyclingPagerAdapter) viewPager.getAdapter()).getRealPosition(position));
  }

  private void createIndicators() {
    removeAllViews();

    if (viewPager.getAdapter() == null) {
      return;
    }

    int count = ((RecyclingPagerAdapter) viewPager.getAdapter()).getRealCount();
    if (count <= 1) {
      return;
    }

    for (int i = 0; i < count; i++) {
      View indicator = new View(getContext());
      indicator.setBackgroundResource(indicatorBackground);
      addView(indicator, indicatorWidth, indicatorHeight);
      LayoutParams layoutParams = (LayoutParams) indicator.getLayoutParams();
      layoutParams.leftMargin = indicatorMargin;
      layoutParams.rightMargin = indicatorMargin;
      indicator.setLayoutParams(layoutParams);
    }
  }

  public class ReverseInterpolator implements Interpolator {

    @Override
    public float getInterpolation(float value) {
      return Math.abs(1.0f - value);
    }
  }

  public int dip2px(float dpValue) {
    final float scale = getResources().getDisplayMetrics().density;
    return (int) (dpValue * scale + 0.5f);
  }
}
