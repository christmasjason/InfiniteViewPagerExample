package com.christmas.infiniteviewpager.salvageAdapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.christmas.infiniteviewpager.sliderView.BaseSliderView;

import java.util.ArrayList;
import java.util.List;

/**
 * 回收机制的PagerAdapter.
 */
public class RecyclingPagerAdapter extends PagerAdapter {
  private final int IGNORE_ITEM_VIEW_TYPE = AdapterView.ITEM_VIEW_TYPE_IGNORE;
  private final RecycleBin recycleBin;
  private DataChangeListener dataChangeListener;
  private boolean isLoop = true;
  private List<BaseSliderView> sliderViews;

  public RecyclingPagerAdapter() {
    this(new RecycleBin());
  }

  private RecyclingPagerAdapter(RecycleBin recycleBin) {
    this.recycleBin = recycleBin;
    sliderViews = new ArrayList<>();
    recycleBin.setViewTypeCount(getViewTypeCount());
  }

  public <T extends BaseSliderView> void addSliderView(T sliderView) {
    this.sliderViews.add(sliderView);
    notifyDataSetChanged();
  }

  public <T extends BaseSliderView> void removeSliderView(T sliderView) {
    if (sliderViews.contains(sliderView)) {
      sliderViews.remove(sliderView);
      notifyDataSetChanged();
    }
  }

  public void removeSliderViewAt(int position) {
    if (position > 0 && position < sliderViews.size()) {
      sliderViews.remove(position);
      notifyDataSetChanged();
    }
  }

  public void removeAllSliderView() {
    sliderViews.clear();
    notifyDataSetChanged();
  }

  public int getRealPosition(int position) {
    return isLoop ? position % getRealCount() : position;
  }

  public void setLoop(boolean isLoop) {
    this.isLoop = isLoop;
    notifyDataSetChanged();
  }

  public boolean getLoop() {
    return isLoop;
  }

  public int getRealCount() {
    return sliderViews.size();
  }

  public View getView(int position, View convertView, ViewGroup container) {
    if (convertView != null) {
      return convertView;
    }
    return sliderViews.get(getRealPosition(position)).getView();
  }

  @Override
  public int getCount() {
    // 相当于创建一个 真实item数量 * 1000(任意数值) 的ViewPager,
    // 然后把 真实item数量 个View根据真实position存储到RecycleBin中,
    // 初始化item view的时候根据view真实位置从RecycleBin中取.
    return isLoop ? getRealCount() * 1000 : getRealCount();
  }

  public void setDataChangeListener(DataChangeListener dataChangeListener) {
    this.dataChangeListener = dataChangeListener;
  }

  @Override
  public void notifyDataSetChanged() {
    recycleBin.clear();
    if (this.dataChangeListener != null) {
      this.dataChangeListener.notifyDataChange();
    }
    super.notifyDataSetChanged();
  }

  @Override
  public final Object instantiateItem(ViewGroup container, int position) {
    int viewType = getItemViewType(position);
    View view = null;
    if (viewType != IGNORE_ITEM_VIEW_TYPE) {
      view = recycleBin.getScrapView(getRealPosition(position), viewType);
    }
    view = getView(position, view, container);
    container.addView(view);
    return view;
  }

  @Override
  public final void destroyItem(ViewGroup container, int position, Object object) {
    View view = (View) object;
    container.removeView(view);
    int viewType = getItemViewType(position);
    if (viewType != IGNORE_ITEM_VIEW_TYPE) {
      recycleBin.addScrapView(view, getRealPosition(position), viewType);
    }
  }

  public interface DataChangeListener {
    void notifyDataChange();
  }

  @Override
  public final boolean isViewFromObject(View view, Object object) {
    return view == object;
  }

  /**
   * 返回getView创建的View的类型.
   *
   * @return
   */
  public int getViewTypeCount() {
    return 1;
  }

  /**
   * 获取指定位置的View的类型, 现在只指定一种类型, 直接返回0.
   *
   * @param position
   * @return
   */
  public int getItemViewType(int position) {
    return 0;
  }
}
