package com.christmas.infiniteviewpagerexample;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.christmas.infiniteviewpager.InfiniteViewPagerWithIndicatorLayout;
import com.christmas.infiniteviewpager.sliderView.BaseSliderView;
import com.christmas.infiniteviewpager.sliderView.DefaultSliderView;

public class MainActivity extends AppCompatActivity {
  InfiniteViewPagerWithIndicatorLayout infiniteViewPagerWithIndicatorLayout;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.layout_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    infiniteViewPagerWithIndicatorLayout = (InfiniteViewPagerWithIndicatorLayout) findViewById(R.id.vp_infinite_view_pager);
    for (int index = 0; index < 4; index++) {
      final int tempIndex = index;
      DefaultSliderView sliderView = new DefaultSliderView(this);
      sliderView
          .setImage(getResources().getIdentifier(String.format("bg_vp_%d", index + 1), "drawable", getPackageName()))
          .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
            @Override
            public void onSliderClick(BaseSliderView baseSliderView) {
              Toast.makeText(MainActivity.this, String.format("点击第%d个", tempIndex + 1), Toast.LENGTH_LONG).show();
            }
          });
      infiniteViewPagerWithIndicatorLayout.addSliderView(sliderView);
    }
    infiniteViewPagerWithIndicatorLayout.setIndicatorPosition();

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            .setAction("Action", null).show();
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (infiniteViewPagerWithIndicatorLayout != null) {
      infiniteViewPagerWithIndicatorLayout.startAutoScroll();
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (infiniteViewPagerWithIndicatorLayout != null) {
      infiniteViewPagerWithIndicatorLayout.stopAutoScroll();
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }
}
