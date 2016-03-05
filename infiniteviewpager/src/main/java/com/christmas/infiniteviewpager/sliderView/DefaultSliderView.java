package com.christmas.infiniteviewpager.sliderView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.christmas.infiniteviewpager.R;

/**
 * 默认SliderView.
 */
public class DefaultSliderView extends BaseSliderView {
  public DefaultSliderView(Context context) {
    super(context);
  }

  @Override
  public View getView() {
    View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_slider_view_default, null);
    ImageView ivSliderView = (ImageView) view.findViewById(R.id.iv_slider_image);
    bindEventAndShow(view, ivSliderView);
    return view;
  }
}
