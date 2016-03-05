package com.christmas.infiniteviewpager;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;

import com.christmas.infiniteviewpager.indicator.BaseIndicator;
import com.christmas.infiniteviewpager.salvageAdapter.RecyclingPagerAdapter;
import com.christmas.infiniteviewpager.sliderView.BaseSliderView;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

/**
 * 无限循环、带有指示器的ViewPager.
 */
public class InfiniteViewPagerWithIndicatorLayout extends RelativeLayout implements
    RecyclingPagerAdapter.DataChangeListener {
  private static final int MSG_WHAT = 0;
  private static final int DEFAULT_INTERVAL = 2500;
  private static final int LEFT = 0;
  private static final int RIGHT = 1;

  private ScrollHandler scrollHandler;
  private BaseIndicator baseIndicator;
  private ViewPager viewPager;
  private RecyclingPagerAdapter recyclingPagerAdapter;
  private CustomDurationScroller customDurationScroller;

  private boolean isInfinite = true;
  private boolean isAutoScroll = false;
  private boolean isStopScrollWhenTouch = true;
  private boolean isStopByTouch = false;
  private int direction = RIGHT;
  private int interval = DEFAULT_INTERVAL;

  public InfiniteViewPagerWithIndicatorLayout(Context context) {
    this(context, null);
  }

  public InfiniteViewPagerWithIndicatorLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public InfiniteViewPagerWithIndicatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.InfiniteViewPagerWithIndicatorLayout);
    int indicatorType = typedArray.getInt(R.styleable.InfiniteViewPagerWithIndicatorLayout_indicatorType, BaseIndicator.IndicatorType.ANIM_CIRCLE.ordinal());
    typedArray.recycle();

    if (indicatorType == BaseIndicator.IndicatorType.ANIM_CIRCLE.ordinal()) {
      LayoutInflater.from(context).inflate(R.layout.layout_anim_circle_indicator, this, true);

    } else {
      LayoutInflater.from(context).inflate(R.layout.layout_anim_circle_indicator, this, true);

    }

    scrollHandler = new ScrollHandler(this);
    viewPager = (ViewPager) findViewById(R.id.vp_advertise);
    recyclingPagerAdapter = new RecyclingPagerAdapter();
    recyclingPagerAdapter.setDataChangeListener(this);
    viewPager.setAdapter(recyclingPagerAdapter);
    setViewPagerScroller();
  }

  public <T extends BaseSliderView> void addSliderView(T sliderView) {
    recyclingPagerAdapter.addSliderView(sliderView);
  }

  public void removeAllSliderViews() {
    recyclingPagerAdapter.removeAllSliderView();
  }

  @Override
  public void notifyDataChange() {
    if (this.baseIndicator != null) {
      this.baseIndicator.notifyDataSetChanged();
    }
  }

  private void setViewPagerScroller() {
    try {
      Field scrollerField = ViewPager.class.getDeclaredField("mScroller");
      scrollerField.setAccessible(true);
      Field interpolatorField = ViewPager.class.getDeclaredField("sInterpolator");
      interpolatorField.setAccessible(true);

      customDurationScroller = new CustomDurationScroller(getContext(), (Interpolator) interpolatorField.get(null));
      scrollerField.set(viewPager, customDurationScroller);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void setScrollDurationFactor(double scrollFactor) {
    customDurationScroller.setScrollDurationFactor(scrollFactor);
  }

  public void startAutoScroll() {
    if (recyclingPagerAdapter.getRealCount() > 1) {
      isAutoScroll = true;
      sendScrollMessage();
    }
  }

  public void startAutoScroll(long delayTimeMillis) {
    if (recyclingPagerAdapter.getRealCount() > 1) {
      isAutoScroll = true;
      sendScrollMessage(delayTimeMillis);
    }
  }

  public void stopAutoScroll() {
    isAutoScroll = false;
    scrollHandler.removeMessages(MSG_WHAT);
  }

  private void scrollOnce() {
    PagerAdapter pagerAdapter = viewPager.getAdapter();
    int currentItem = viewPager.getCurrentItem();
    int totalCount;
    if (pagerAdapter == null || (totalCount = pagerAdapter.getCount()) < 1) {
      return;
    }

    int nextItem = (direction == RIGHT) ? ++currentItem : --currentItem;
    if (nextItem < 0) {
      if (isInfinite) {
        viewPager.setCurrentItem(totalCount - 1);
      }
    } else if (nextItem == totalCount) {
      if (isInfinite) {
        viewPager.setCurrentItem(0);
      }
    } else {
      viewPager.setCurrentItem(nextItem, true);
    }
  }

  private void sendScrollMessage() {
    sendScrollMessage(DEFAULT_INTERVAL);
  }

  private void sendScrollMessage(long delayTimeInMills) {
    scrollHandler.removeMessages(MSG_WHAT);
    scrollHandler.sendEmptyMessageDelayed(MSG_WHAT, delayTimeInMills);
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    int action = MotionEventCompat.getActionMasked(ev);
    if (isStopScrollWhenTouch) {
      if (action == MotionEvent.ACTION_DOWN && isAutoScroll) {
        isStopByTouch = true;
        stopAutoScroll();
      } else if (action == MotionEvent.ACTION_UP && isStopByTouch) {
        isStopByTouch = false;
        startAutoScroll();
      }
    }
    return super.dispatchTouchEvent(ev);
  }

  public static class ScrollHandler extends Handler {
    private WeakReference<InfiniteViewPagerWithIndicatorLayout> weakRefInfiniteViewPagerWithIndicatorLayout;

    public ScrollHandler(InfiniteViewPagerWithIndicatorLayout infiniteViewPagerWithIndicatorLayout) {
      this.weakRefInfiniteViewPagerWithIndicatorLayout = new WeakReference<>(infiniteViewPagerWithIndicatorLayout);
    }

    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);

      InfiniteViewPagerWithIndicatorLayout infiniteViewPagerWithIndicatorLayout = weakRefInfiniteViewPagerWithIndicatorLayout.get();
      if (infiniteViewPagerWithIndicatorLayout != null) {
        switch (msg.what) {
          case MSG_WHAT:
            infiniteViewPagerWithIndicatorLayout.scrollOnce();
            infiniteViewPagerWithIndicatorLayout.sendScrollMessage();
            break;
          default:
            break;
        }
      }
    }
  }

  public void setInfinite(boolean isInfinite) {
    this.isInfinite = isInfinite;
    recyclingPagerAdapter.setLoop(isInfinite);
  }

  public boolean getInfinite() {
    return this.isInfinite;
  }

  public void setInterval(int interval) {
    this.interval = interval;
  }

  public int getInterval() {
    return this.interval;
  }

  public void setStopScrollWhenTouch(boolean stopScrollWhenTouch) {
    this.isStopScrollWhenTouch = stopScrollWhenTouch;
  }

  public boolean getStopScrollWhenTouch() {
    return this.isStopScrollWhenTouch;
  }

  public void setDirection(int direction) {
    this.direction = direction;
  }

  public int getDirection() {
    return (direction == LEFT) ? LEFT : RIGHT;
  }

  public enum IndicatorPosition {
    CENTER("CENTER", R.id.ai_center),
    CENTER_BOTTOM("CENTER_BOTTOM", R.id.ai_center_bottom),
    BOTTOM_LEFT("BOTTOM_LEFT", R.id.ai_bottom_left),
    BOTTOM_RIGHT("BOTTOM_RIGHT", R.id.ai_bottom_right),
    CENTER_TOP("CENTER_TOP", R.id.ai_center_top),
    TOP_RIGHT("TOP_RIGHT", R.id.ai_top_right),
    TOP_LEFT("TOP_LEFT", R.id.ai_top_left);

    private final String name;
    private final int resId;

    IndicatorPosition(String name, int resId) {
      this.name = name;
      this.resId = resId;
    }

    public String toString() {
      return name;
    }

    public int getResId() {
      return resId;
    }
  }

  public void setIndicatorPosition() {
    setIndicatorPosition(IndicatorPosition.CENTER_BOTTOM);
  }

  public void setIndicatorPosition(IndicatorPosition presentIndicator) {
    BaseIndicator indicator = (BaseIndicator) findViewById(presentIndicator.getResId());
    setCustomIndicator(indicator);
  }

  public void initFirstPage() {
    if (isInfinite && recyclingPagerAdapter.getRealCount() > 1) {
      viewPager.setCurrentItem(recyclingPagerAdapter.getRealCount() * 50);
    } else {
      setInfinite(false);
      viewPager.setCurrentItem(0);
    }
  }

  public void setCustomIndicator(BaseIndicator indicator) {
    initFirstPage();
    this.baseIndicator = indicator;
    this.baseIndicator.setViewPager(viewPager);
  }

  public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
    if (this.baseIndicator != null) {
      this.baseIndicator.setOnPageChangeListener(onPageChangeListener);
    }
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    scrollHandler.removeCallbacksAndMessages(null);
  }
}
