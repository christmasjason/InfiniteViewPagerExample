package com.christmas.infiniteviewpager.salvageAdapter;

import android.util.SparseArray;
import android.view.View;

/**
 * 重用的View的存储类.
 */
public class RecycleBin {
  private SparseArray<View>[] scrapViews;
  private int viewTypeCount;
  private SparseArray<View> currentScrapViews;

  @SuppressWarnings(value = "unchecked")
  public void setViewTypeCount(int viewTypeCount) {
    if (viewTypeCount < 1) {
      throw new IllegalArgumentException("Can't have a viewTypeCount < 1.");
    }

    this.scrapViews = new SparseArray[viewTypeCount];
    for (int i = 0; i < viewTypeCount; i++) {
      this.scrapViews[i] = new SparseArray<>();
    }

    this.viewTypeCount = viewTypeCount;
    currentScrapViews = this.scrapViews[0];
  }

  /**
   * 把adapter remove的View添加到散列表.
   *
   * @param view     待存储的view.
   * @param position 存储的位置.
   * @param viewType 存储view的类型.
   */
  public void addScrapView(View view, int position, int viewType) {
    if (viewTypeCount == 1) {
      currentScrapViews.put(position, view);
    } else {
      scrapViews[viewType].put(position, view);
    }
  }

  /**
   * 从散列表中取出之前存储的对应位置View.
   *
   * @param position 在散列表中的位置.
   * @param viewType 取散列表中的view的类型.
   * @return 取到的view.
   */
  public View getScrapView(int position, int viewType) {
    if (viewTypeCount == 1) {
      return retrieveFromScrap(currentScrapViews, position);

    } else if (viewType >= 0 && viewType < viewTypeCount) {
      return retrieveFromScrap(this.scrapViews[viewType], position);

    }

    return null;
  }

  private View retrieveFromScrap(SparseArray<View> currentScrapViews, int position) {
    int size = currentScrapViews.size();
    if (size > 0) {
      for (int i = 0; i < size; i++) {
        int fromPosition = currentScrapViews.keyAt(i);
        if (fromPosition == position) {
          View view = currentScrapViews.get(fromPosition);
          currentScrapViews.remove(fromPosition);
          return view;
        }
      }
    }
    return null;
  }

  /**
   * 数据变动清空散列表.
   */
  public void clear() {
    for (SparseArray<View> scrapView : scrapViews) {
      scrapView.clear();
    }
    currentScrapViews.clear();
  }
}
