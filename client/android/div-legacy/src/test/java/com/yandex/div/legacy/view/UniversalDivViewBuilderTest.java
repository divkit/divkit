package com.yandex.div.legacy.view;

import android.app.Activity;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;
import com.yandex.alicekit.core.utils.Views;
import com.yandex.alicekit.core.widget.YandexSansTypefaceProvider;
import com.yandex.div.DivDataTag;
import com.yandex.div.DivImageElement;
import com.yandex.div.DivPosition;
import com.yandex.div.DivSize;
import com.yandex.div.DivTextStyle;
import com.yandex.div.DivUniversalBlock;
import com.yandex.div.core.images.DivImageDownloadCallback;
import com.yandex.div.core.images.DivImageLoader;
import com.yandex.div.legacy.DivDataMockUtils;
import com.yandex.div.legacy.DivTextStyleProvider;
import com.yandex.div.legacy.R;
import com.yandex.div.view.pooling.PseudoViewPool;
import com.yandex.div.view.pooling.ViewPool;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class UniversalDivViewBuilderTest {

    private static final Uri IMAGE_URL = Uri.parse("https://image_url.ru");
    private static final String TEXT = "text";
    private static final String TITLE = "title";

    @Mock
    private DivImageLoader mImageLoader;
    @Mock
    private DivView mDivView;

    private final TextViewFactory mTextViewFactory = new DivLineHeightTextViewFactory();
    private final ViewPool mViewPool = new PseudoViewPool();

    private UniversalDivViewBuilder mUniversalDivViewBuilder;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        when(mDivView.getDivTag()).thenReturn(DivDataTag.INVALID);

        Activity activity = Robolectric.setupActivity(Activity.class);
        DivTextStyleProvider textStyleProvider = new DivTextStyleProvider(new YandexSansTypefaceProvider(activity));
        mUniversalDivViewBuilder = new UniversalDivViewBuilder(activity, activity, mViewPool,
                                                               mImageLoader, textStyleProvider, mTextViewFactory);
    }

    @Test
    public void testBind_dateTextTitle() {
        final DivUniversalBlock universalDivBlockData = DivDataMockUtils.createDivUniversalBlock(
                TITLE,
                DivTextStyle.TITLE_M,
                null,
                TEXT,
                DivTextStyle.TEXT_S,
                5);

        DivDataMockUtils.addDateToDivUniversalBlock(universalDivBlockData, DivPosition.RIGHT, DivSize.M, "1", "2");

        final View build = mUniversalDivViewBuilder.build(mDivView, universalDivBlockData);
        Assert.assertNotNull(build);

        final TextView text = Views.findViewAndCast(build, R.id.div_universal_text);
        Assert.assertEquals(TEXT, text.getText().toString());
        final TextView title = Views.findViewAndCast(build, R.id.div_universal_title);
        Assert.assertEquals(TITLE, title.getText().toString());
        final TextView day = build.findViewById(R.id.div_universal_date_day);
        Assert.assertEquals("1", day.getText().toString());
        final TextView month = build.findViewById(R.id.div_universal_date_month);
        Assert.assertEquals("2", month.getText().toString());
    }

    @Test
    public void testBind_imageTextTitle() {
        final DivImageElement imageElement = DivDataMockUtils.createDivImageElement(IMAGE_URL, 1.f);
        final DivUniversalBlock universalDivBlockData = DivDataMockUtils.createDivUniversalBlock(
                TITLE,
                DivTextStyle.TITLE_M,
                null,
                TEXT,
                DivTextStyle.TEXT_S,
                5);

        DivDataMockUtils.addImageToDivUniversalBlock(universalDivBlockData, DivPosition.RIGHT, DivSize.M, imageElement);

        final View build = mUniversalDivViewBuilder.build(mDivView, universalDivBlockData);
        verify(mImageLoader).loadImage(eq(IMAGE_URL.toString()), any(DivImageDownloadCallback.class));
        final TextView text = Views.findViewAndCast(build, R.id.div_universal_text);
        Assert.assertEquals(TEXT, text.getText().toString());
        final TextView title = Views.findViewAndCast(build, R.id.div_universal_title);
        Assert.assertEquals(TITLE, title.getText().toString());
        Assert.assertNotNull(build.findViewById(R.id.div_universal_image));

        Assert.assertNull(build.findViewById(R.id.div_universal_date_day));
        Assert.assertNull(build.findViewById(R.id.div_universal_date_month));
    }

    @Test
    public void testBind_textTitle() {
        final DivUniversalBlock universalDivBlockData = DivDataMockUtils.createDivUniversalBlock(
                TITLE,
                DivTextStyle.TITLE_M,
                null,
                TEXT,
                DivTextStyle.TEXT_S,
                5);

        final View build = mUniversalDivViewBuilder.build(mDivView, universalDivBlockData);
        final TextView text = Views.findViewAndCast(build, R.id.div_universal_text);
        Assert.assertEquals(TEXT, text.getText().toString());
        Assert.assertEquals(View.VISIBLE, text.getVisibility());
        final TextView title = Views.findViewAndCast(build, R.id.div_universal_title);
        Assert.assertEquals(TITLE, title.getText().toString());
        Assert.assertEquals(View.VISIBLE, title.getVisibility());

        Assert.assertNull(build.findViewById(R.id.div_universal_image));
        Assert.assertNull(build.findViewById(R.id.div_universal_date_day));
        Assert.assertNull(build.findViewById(R.id.div_universal_date_month));
    }

    @Test
    public void testBind_title() {
        final DivUniversalBlock universalDivBlockData = DivDataMockUtils.createDivUniversalBlock(
                TITLE,
                DivTextStyle.TITLE_M,
                null,
                null,
                DivTextStyle.TEXT_S,
                5);

        final View build = mUniversalDivViewBuilder.build(mDivView, universalDivBlockData);
        final TextView title = Views.findViewAndCast(build, R.id.div_universal_title);
        Assert.assertEquals(TITLE, title.getText().toString());

        Assert.assertNull(build.findViewById(R.id.div_universal_text));
        Assert.assertNull(build.findViewById(R.id.div_universal_image));
        Assert.assertNull(build.findViewById(R.id.div_universal_date_day));
        Assert.assertNull(build.findViewById(R.id.div_universal_date_month));
    }

    @Test
    public void testBind_text() {
        final DivUniversalBlock universalDivBlockData = DivDataMockUtils.createDivUniversalBlock(
                null,
                DivTextStyle.TITLE_M,
                null,
                TEXT,
                DivTextStyle.TEXT_S,
                5);

        final View build = mUniversalDivViewBuilder.build(mDivView, universalDivBlockData);
        final TextView text = Views.findViewAndCast(build, R.id.div_universal_text);
        Assert.assertEquals(TEXT, text.getText().toString());

        Assert.assertNull(build.findViewById(R.id.div_universal_title));
        Assert.assertNull(build.findViewById(R.id.div_universal_image));
        Assert.assertNull(build.findViewById(R.id.div_universal_date_day));
        Assert.assertNull(build.findViewById(R.id.div_universal_date_month));
    }

    @Test
    public void testTitleMaxLines() {
        int titleMaxLines = 10;
        final DivUniversalBlock universalDivBlockData = DivDataMockUtils.createDivUniversalBlock(
                TITLE,
                DivTextStyle.TITLE_M,
                titleMaxLines,
                TEXT,
                DivTextStyle.TEXT_S,
                null);

        final View build = mUniversalDivViewBuilder.build(mDivView, universalDivBlockData);
        final TextView title = Views.findViewAndCast(build, R.id.div_universal_title);
        Assert.assertEquals(titleMaxLines, title.getMaxLines());
    }

    @Test
    public void testTextMaxLines() {
        int textMaxLines = 10;
        final DivUniversalBlock universalDivBlockData = DivDataMockUtils.createDivUniversalBlock(
                TITLE,
                DivTextStyle.TITLE_M,
                null,
                TEXT,
                DivTextStyle.TEXT_S,
                textMaxLines);

        final View build = mUniversalDivViewBuilder.build(mDivView, universalDivBlockData);
        final TextView text = Views.findViewAndCast(build, R.id.div_universal_text);
        Assert.assertEquals(textMaxLines, text.getMaxLines());
    }
}
