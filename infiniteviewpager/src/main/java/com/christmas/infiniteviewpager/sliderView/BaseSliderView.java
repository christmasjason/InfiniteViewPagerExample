package com.christmas.infiniteviewpager.sliderView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

/**
 * SliderView基类.
 * 如果要在图片加载过程中显示进度条, 需要定义一个ProgressBar(@+id/pb_loading).
 */
public abstract class BaseSliderView {

  private Context context;
  private Bundle bundle;
  private OnSliderClickListener onSliderClickListener;

  private int res;

  protected BaseSliderView(Context context) {
    this.context = context;
    this.bundle = new Bundle();
  }

  /**
   * 可以添加额外的信息.
   *
   * @return
   */
  public Bundle getBundle() {
    return this.bundle;
  }

  public Context getContext() {
    return this.context;
  }

  public BaseSliderView setImage(int res) {
    this.res = res;
    return this;
  }

  /**
   * 设置SliderView点击回调监听器.
   *
   * @param onSliderClickListener
   */
  public BaseSliderView setOnSliderClickListener(OnSliderClickListener onSliderClickListener) {
    this.onSliderClickListener = onSliderClickListener;
    return this;
  }

  /**
   * 重写自己的SliderView, 在getView()中调用这个方法.
   *
   * @param view
   * @param targetImageView
   */
  protected void bindEventAndShow(final View view, ImageView targetImageView) {
    view.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (onSliderClickListener != null) {
          onSliderClickListener.onSliderClick(BaseSliderView.this);
        }
      }
    });

    if (targetImageView == null) {
      return;
    }

    if (res != 0) {
      targetImageView.setImageResource(res);
    }
  }

  /**
   * 所有扩展类要重写这个方法，在adapter中调用.
   *
   * @return
   */
  public abstract View getView();

  /**
   * SliderView点击事件回调接口.
   */
  public interface OnSliderClickListener {
    void onSliderClick(BaseSliderView baseSliderView);
  }
}
