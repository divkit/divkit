package com.yandex.div.legacy.view;

import android.app.Activity;
import android.graphics.Color;
import androidx.annotation.ColorInt;
import com.yandex.div.DivSeparatorBlock;
import com.yandex.div.DivSize;
import com.yandex.div.legacy.DivDataMockUtils;
import com.yandex.div.view.SeparatorView;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class SeparatorDivViewBuilderTest {

    @Mock
    private DivView mDivView;

    private SeparatorDivViewBuilder mBuilder;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);

        Activity activity = Robolectric.setupActivity(Activity.class);
        when(mDivView.getContext()).thenReturn(activity);

        mBuilder = new SeparatorDivViewBuilder();
    }

    @Test
    public void testSeparatorWithDivider() {
        @ColorInt int delimiterColor = Color.BLACK;
        @DivSize String size = DivSize.L;

        DivSeparatorBlock data = DivDataMockUtils.createDivSeparatorBlock(delimiterColor, true, size);

        SeparatorView view = (SeparatorView) mBuilder.build(mDivView, data);
        Assert.assertTrue(view.getDividerThickness() > 0);
    }

    @Test
    public void testSeparatorWithoutDivider() {
        @ColorInt int delimiterColor = Color.BLACK;
        @DivSize String size = DivSize.L;

        DivSeparatorBlock data = DivDataMockUtils.createDivSeparatorBlock(delimiterColor, false, size);

        SeparatorView view = (SeparatorView) mBuilder.build(mDivView, data);
        Assert.assertEquals(0, view.getDividerThickness());
    }
}
