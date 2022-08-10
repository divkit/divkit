package com.yandex.div.legacy.view;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yandex.alicekit.core.widget.YandexSansTypefaceProvider;
import com.yandex.div.DivAction;
import com.yandex.div.DivPaddingModifier;
import com.yandex.div.DivPosition;
import com.yandex.div.DivSize;
import com.yandex.div.DivTextStyle;
import com.yandex.div.DivTitleBlock;
import com.yandex.div.legacy.DivAutoLogger;
import com.yandex.div.legacy.DivDataMockUtils;
import com.yandex.div.legacy.DivTextStyleProvider;
import com.yandex.div.legacy.LegacyDivViewConfig;
import com.yandex.div.legacy.R;
import com.yandex.div.view.pooling.PseudoViewPool;
import java.util.Arrays;
import java.util.List;
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
public class TitleDivBlockViewBuilderTest {

    private static final String TEXT = "SOME TEXT";
    private static final String MENU_COLOR = "blue";
    private static final String ACTION_URL = "ACTION URL";
    private static final String ACTION_LOGID = "ACTION LOGID";
    @DivTextStyle
    private static final String TEXT_STYLE = DivTextStyle.CARD_HEADER;
    @DivSize
    private static final String PAD_TOP = DivSize.L;

    @Mock
    private DivAutoLogger mDivAutoLogger;
    @Mock
    private LegacyDivViewConfig mDivConfig;
    @Mock
    private DivView mDivView;

    private final TextViewFactory mTextViewFactory = new DivLineHeightTextViewFactory();

    private TitleDivBlockViewBuilder mBuilder;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);

        Activity activity = Robolectric.setupActivity(Activity.class);
        DivTextStyleProvider textStyleProvider = new DivTextStyleProvider(new YandexSansTypefaceProvider(activity));
        when(mDivView.getContext()).thenReturn(activity);
        when(mDivView.getConfig()).thenReturn(mDivConfig);
        when(mDivConfig.isContextMenuEnabled()).thenReturn(true);

        mBuilder = new TitleDivBlockViewBuilder(activity, new PseudoViewPool(), textStyleProvider,
                                                mDivAutoLogger, mTextViewFactory);
    }

    @Test
    public void testEmptyData() {
        final DivTitleBlock titleBlock = mock(DivTitleBlock.class);
        final DivAction divAction = DivDataMockUtils.createDivAction(ACTION_LOGID, ACTION_URL);
        final DivPaddingModifier paddingModifier = DivDataMockUtils.createPaddingModifier(PAD_TOP, DivPosition.LEFT);
        setPrimaryData(titleBlock, null, TEXT_STYLE, MENU_COLOR, paddingModifier, divAction);

        Assert.assertNull(titleBlock.text);
        View build = mBuilder.build(mDivView, titleBlock);
        Assert.assertTrue(build instanceof TextView);
        Assert.assertEquals("", ((TextView) build).getText());
    }

    @Test
    public void testBindTextOnly() {
        final DivTitleBlock titleBlock = mock(DivTitleBlock.class);
        final DivAction divAction = DivDataMockUtils.createDivAction(ACTION_LOGID, ACTION_URL);
        final DivPaddingModifier paddingModifier = DivDataMockUtils.createPaddingModifier(PAD_TOP, DivPosition.LEFT);
        setPrimaryData(titleBlock, TEXT, TEXT_STYLE, MENU_COLOR, paddingModifier, divAction);

        View build = mBuilder.build(mDivView, titleBlock);

        Assert.assertTrue(build instanceof TextView);
        Assert.assertEquals(TEXT, ((TextView) build).getText().toString());
    }

    @Test
    public void testBindTextAndMenu() {
        final DivTitleBlock titleBlock = mock(DivTitleBlock.class);
        final DivAction divAction = DivDataMockUtils.createDivAction(ACTION_LOGID, ACTION_URL);
        final DivPaddingModifier paddingModifier = DivDataMockUtils.createPaddingModifier(PAD_TOP, DivPosition.LEFT);
        setPrimaryData(titleBlock, TEXT, TEXT_STYLE, MENU_COLOR, paddingModifier, divAction);

        final DivTitleBlock.MenuItem item1 = createMenuItem("TEXT1", "URL1");
        final DivTitleBlock.MenuItem item2 = createMenuItem("TEXT2", "");
        final DivTitleBlock.MenuItem item3 = createMenuItem("TEXT3", "URL3");
        final List<DivTitleBlock.MenuItem> items = Arrays.asList(item1, item2, item3);
        ReflectionHelpers.setField(titleBlock, "menuItems", items);

        View build = mBuilder.build(mDivView, titleBlock);

        Assert.assertFalse(build instanceof TextView);
        Assert.assertNotNull(build.findViewById(R.id.overflow_menu));
        Assert.assertEquals(TEXT, ((TextView) build.findViewById(R.id.div_title_text)).getText().toString());
    }

    private static void setPrimaryData(@NonNull final DivTitleBlock titleBlock, @Nullable final String text,
                                       @NonNull @DivTextStyle final String textStyle, @NonNull final String menuColor,
                                       @Nullable final DivPaddingModifier paddingModifier, @Nullable DivAction action) {
        ReflectionHelpers.setField(titleBlock, "text", text);
        ReflectionHelpers.setField(titleBlock, "textStyle", textStyle);
        ReflectionHelpers.setField(titleBlock, "menuColor", Color.parseColor(menuColor));
        ReflectionHelpers.setField(titleBlock, "paddingModifier", paddingModifier);
        ReflectionHelpers.setField(titleBlock, "action", action);
    }

    private static DivTitleBlock.MenuItem createMenuItem(@NonNull final String text, @NonNull final String url) {
        final DivTitleBlock.MenuItem menuItem = mock(DivTitleBlock.MenuItem.class);
        ReflectionHelpers.setField(menuItem, "text", text);
        ReflectionHelpers.setField(menuItem, "url", TextUtils.isEmpty(url) ? null : Uri.parse(url));
        return menuItem;
    }
}
