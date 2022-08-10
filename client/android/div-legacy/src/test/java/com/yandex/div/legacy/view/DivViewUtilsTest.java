package com.yandex.div.legacy.view;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.Gravity;
import com.yandex.alicekit.core.widget.YandexSansTypefaceProvider;
import com.yandex.div.DivAlignment;
import com.yandex.div.DivAlignmentVertical;
import com.yandex.div.DivBackground;
import com.yandex.div.DivDataTag;
import com.yandex.div.DivGradientBackground;
import com.yandex.div.DivImageBackground;
import com.yandex.div.DivPosition;
import com.yandex.div.DivSolidBackground;
import com.yandex.div.DivTextStyle;
import com.yandex.div.core.images.DivImageLoader;
import com.yandex.div.legacy.Alignment;
import com.yandex.div.legacy.BackgroundImageDrawable;
import com.yandex.div.legacy.DivTextStyleProvider;
import com.yandex.div.util.Position;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.ReflectionHelpers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class DivViewUtilsTest {

    private static final String TEST_URI = "test://TEST_URI/test";

    @Mock
    private DivImageLoader mDivImageLoader;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testDivBackgroundToBackground() {
        final DivView divView = mock(DivView.class);
        when(divView.getDivTag()).thenReturn(DivDataTag.INVALID);

        final DivBackground solidBackground = mock(DivBackground.class);
        final DivBackground gradientBackground = mock(DivBackground.class);
        final DivBackground imageBackground = mock(DivBackground.class);

        final DivBackground invalidBackground = mock(DivBackground.class);

        { // set up solidBackground
            DivSolidBackground background = mock(DivSolidBackground.class);
            when(solidBackground.asDivSolidBackground()).thenReturn(background);
            ReflectionHelpers.setField(background, "color", Color.parseColor("blue"));
        }
        { // set up gradientBackground
            DivGradientBackground background = mock(DivGradientBackground.class);
            when(gradientBackground.asDivGradientBackground()).thenReturn(background);
            ReflectionHelpers.setField(background, "startColor", Color.parseColor("red"));
            ReflectionHelpers.setField(background, "endColor", Color.parseColor("black"));
        }
        { // set up imageBackground
            DivImageBackground background = mock(DivImageBackground.class);
            when(imageBackground.asDivImageBackground()).thenReturn(background);
            ReflectionHelpers.setField(background, "imageUrl", Uri.parse("IMAGE URL"));
        }

        { // check solid
            Drawable drawable = DivViewUtils.divBackgroundToDrawable(solidBackground, mDivImageLoader, divView);
            Assert.assertTrue(drawable instanceof ColorDrawable);
        }
        { // check gradient
            Drawable drawable = DivViewUtils.divBackgroundToDrawable(gradientBackground, mDivImageLoader, divView);
            Assert.assertTrue(drawable instanceof GradientDrawable);
        }
        { // check image
            Drawable drawable = DivViewUtils.divBackgroundToDrawable(imageBackground, mDivImageLoader, divView);
            Assert.assertTrue(drawable instanceof BackgroundImageDrawable);
        }

        { // check invalid
            Assert.assertNull(DivViewUtils.divBackgroundToDrawable(invalidBackground, mDivImageLoader, divView));
        }
    }

    @Test
    public void testGetUri() {
        Assert.assertNull(DivViewUtils.getUri(""));
        Assert.assertNull(DivViewUtils.getUri(null));
        Assert.assertEquals(Uri.parse(TEST_URI), DivViewUtils.getUri(TEST_URI));
    }

    @Test
    public void testDivPositionToPosition() {
        Assert.assertEquals(Position.LEFT, DivViewUtils.divPositionToPosition(null));
        Assert.assertEquals(Position.LEFT, DivViewUtils.divPositionToPosition(DivPosition.LEFT));
        Assert.assertEquals(Position.RIGHT, DivViewUtils.divPositionToPosition(DivPosition.RIGHT));
    }

    @Test
    public void testDivAlignmentToAlignment() {
        Assert.assertEquals(Alignment.LEFT, DivViewUtils.divAlignmentToAlignment(null));
        Assert.assertEquals(Alignment.LEFT, DivViewUtils.divAlignmentToAlignment(DivAlignment.LEFT));
        Assert.assertEquals(Alignment.RIGHT, DivViewUtils.divAlignmentToAlignment(DivAlignment.RIGHT));
        Assert.assertEquals(Alignment.CENTER, DivViewUtils.divAlignmentToAlignment(DivAlignment.CENTER));
    }

    @Test
    public void testHorizontalAlignmentToGravity() {
        Assert.assertEquals(Gravity.LEFT, DivViewUtils.horizontalAlignmentToGravity(null));
        Assert.assertEquals(Gravity.LEFT, DivViewUtils.horizontalAlignmentToGravity(DivAlignment.LEFT));
        Assert.assertEquals(Gravity.RIGHT, DivViewUtils.horizontalAlignmentToGravity(DivAlignment.RIGHT));
        Assert.assertEquals(Gravity.CENTER_HORIZONTAL, DivViewUtils.horizontalAlignmentToGravity(DivAlignment.CENTER));
    }

    @Test
    public void testVerticalAlignmentToGravity() {
        Assert.assertEquals(Gravity.CENTER_VERTICAL, DivViewUtils.verticalAlignmentToGravity(null));
        Assert.assertEquals(Gravity.CENTER_VERTICAL, DivViewUtils.verticalAlignmentToGravity(DivAlignmentVertical.CENTER));
        Assert.assertEquals(Gravity.TOP, DivViewUtils.verticalAlignmentToGravity(DivAlignmentVertical.TOP));
        Assert.assertEquals(Gravity.BOTTOM, DivViewUtils.verticalAlignmentToGravity(DivAlignmentVertical.BOTTOM));
    }

    @Test
    public void testDivTextStyleToTextStyle() {
        DivTextStyleProvider styleFactory = new DivTextStyleProvider(
                new YandexSansTypefaceProvider(Robolectric.buildActivity(Activity.class).get()));
        Assert.assertEquals(styleFactory.getTextS(), styleFactory.getTextStyle(DivTextStyle.TEXT_S));
        Assert.assertEquals(styleFactory.getTextM(), styleFactory.getTextStyle(DivTextStyle.TEXT_M));
        Assert.assertEquals(styleFactory.getTextL(), styleFactory.getTextStyle(DivTextStyle.TEXT_L));
        Assert.assertEquals(styleFactory.getTextMMedium(), styleFactory.getTextStyle(DivTextStyle.TEXT_M_MEDIUM));

        Assert.assertEquals(styleFactory.getTitleS(), styleFactory.getTextStyle(DivTextStyle.TITLE_S));
        Assert.assertEquals(styleFactory.getTitleM(), styleFactory.getTextStyle(DivTextStyle.TITLE_M));
        Assert.assertEquals(styleFactory.getTitleL(), styleFactory.getTextStyle(DivTextStyle.TITLE_L));

        Assert.assertEquals(styleFactory.getNumbersS(), styleFactory.getTextStyle(DivTextStyle.NUMBERS_S));
        Assert.assertEquals(styleFactory.getNumbersM(), styleFactory.getTextStyle(DivTextStyle.NUMBERS_M));
        Assert.assertEquals(styleFactory.getNumbersL(), styleFactory.getTextStyle(DivTextStyle.NUMBERS_L));

        Assert.assertEquals(styleFactory.getCardHeader(), styleFactory.getTextStyle(DivTextStyle.CARD_HEADER));

        Assert.assertEquals(styleFactory.getButton(), styleFactory.getTextStyle(DivTextStyle.BUTTON));

        Assert.assertNotEquals(styleFactory.getTextS(), styleFactory.getTextStyle(DivTextStyle.TEXT_S, 5));
        Assert.assertNotEquals(styleFactory.getTextM(), styleFactory.getTextStyle(DivTextStyle.TEXT_M, 3));
    }
}
