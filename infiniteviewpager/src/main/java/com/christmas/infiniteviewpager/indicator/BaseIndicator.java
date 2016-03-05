package com.christmas.infiniteviewpager.indicator;

import android.support.v4.view.ViewPager;

/**
 * ViewPager指示器基类, 用于显示总共的View数量和指示出当前显示的View.
 */
public interface BaseIndicator extends ViewPager.OnPageChangeListener {

  enum IndicatorType {
    DEFAULT,
    ANIM_LINE,
    ANIM_CIRCLE
  }

  /**
   * 绑定ViewPager.
   *
   * @param viewPager
   */
  void setViewPager(ViewPager viewPager);

  /**
   * 绑定ViewPager, 设定初始位置.
   *
   * @param viewPager
   * @param initialPosition
   */
  void setViewPager(ViewPager viewPager, int initialPosition);

  /**
   * 设定ViewPager和Indicator当前位置.
   */
  void setCurrentItem(int item);

  void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener);

  /**
   * 当ViewPager元素改变时候, 通知指示器改变.
   */
  void notifyDataSetChanged();
}
