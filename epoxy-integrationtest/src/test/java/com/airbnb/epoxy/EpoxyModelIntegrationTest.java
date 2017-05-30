package com.airbnb.epoxy;

import android.view.View;

import com.airbnb.epoxy.EpoxyModel.SpanSizeCallback;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class EpoxyModelIntegrationTest {

  static class ModelWithSpanCount extends EpoxyModel<View> {
    @Override
    protected int getDefaultLayout() {
      return 0;
    }

    @Override
    public int getSpanSize(int totalSpanCount, int position, int itemCount) {
      return 6;
    }
  }

  @Test
  public void modelReturnsSpanCount() {
    ModelWithSpanCount model = new ModelWithSpanCount();
    assertEquals(6, model.getSpanSizeInternal(0, 0, 0));
  }

  static class ModelWithSpanCountCallback extends EpoxyModel<View> {
    @Override
    protected int getDefaultLayout() {
      return 0;
    }
  }

  @Test
  public void modelReturnsSpanCountFromCallback() {
    ModelWithSpanCountCallback model = new ModelWithSpanCountCallback();
    model.spanSizeCallback(new SpanSizeCallback() {
      @Override
      public int getSpanSize(int totalSpanCount, int position, int itemCount) {
        return 7;
      }
    });

    assertEquals(7, model.getSpanSizeInternal(0, 0, 0));
  }
}
