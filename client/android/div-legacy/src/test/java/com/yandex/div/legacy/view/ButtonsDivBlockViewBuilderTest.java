package com.yandex.div.legacy.view;

import android.app.Activity;
import android.net.Uri;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.yandex.alicekit.core.widget.YandexSansTypefaceProvider;
import com.yandex.div.DivAction;
import com.yandex.div.DivAlignment;
import com.yandex.div.DivButtonsBlock;
import com.yandex.div.DivImageElement;
import com.yandex.div.core.images.DivImageLoader;
import com.yandex.div.legacy.DivDataMockUtils;
import com.yandex.div.legacy.DivTextStyleProvider;
import com.yandex.div.view.pooling.PseudoViewPool;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.ReflectionHelpers;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class ButtonsDivBlockViewBuilderTest {

    private static final String ACTION_URL = "ACTION URL";
    private static final String ACTION_LOGID = "ACTION LOGID";
    @DivAlignment
    private static final String ALIGNMENT = DivAlignment.RIGHT;

    private static final Uri IMAGE_URL = Uri.parse("http://url.ru");

    @Mock
    private DivImageLoader mDivImageLoader;
    @Mock
    private DivView mDivView;

    private ButtonsDivBlockViewBuilder mButtonsDivBlockViewBuilder;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);

        Activity activity = Robolectric.setupActivity(Activity.class);
        DivTextStyleProvider textStyleProvider = new DivTextStyleProvider(new YandexSansTypefaceProvider(activity));
        when(mDivView.getContext()).thenReturn(activity);

        mButtonsDivBlockViewBuilder = new ButtonsDivBlockViewBuilder(activity, new PseudoViewPool(), mDivImageLoader, textStyleProvider);
    }

    @Test
    public void testEmptyData() {
        final DivButtonsBlock buttonsBlock = mock(DivButtonsBlock.class);

        final DivAction divAction = DivDataMockUtils.createDivAction(ACTION_LOGID, ACTION_URL);
        DivDataMockUtils.setButtonData(buttonsBlock, divAction, ALIGNMENT, Collections.emptyList(), false);

        Assert.assertNull(mButtonsDivBlockViewBuilder.build(mDivView, buttonsBlock));
    }

    @Test
    public void testBindText() {
        final DivButtonsBlock buttonsBlock = mock(DivButtonsBlock.class);
        final DivAction divAction = DivDataMockUtils.createDivAction(ACTION_LOGID, ACTION_URL);

        final DivAction itemAction1 = DivDataMockUtils.createDivAction("ID1", "URL1");
        final DivButtonsBlock.Item item1 = DivDataMockUtils.createButtonItem(itemAction1, "black", null, "text1");

        DivDataMockUtils.setButtonData(buttonsBlock, divAction, ALIGNMENT, Arrays.asList(item1), true);

        final View build = mButtonsDivBlockViewBuilder.build(mDivView, buttonsBlock);
        final TextView textView = (TextView) ((FrameLayout) ((FrameLayout) build).getChildAt(0)).getChildAt(0);
        Assert.assertEquals("text1", textView.getText().toString());
    }

    @Test
    public void testBindImage() {
        final DivButtonsBlock buttonsBlock = mock(DivButtonsBlock.class);
        final DivAction divAction = DivDataMockUtils.createDivAction(ACTION_LOGID, ACTION_URL);

        final DivAction itemAction1 = DivDataMockUtils.createDivAction("ID1", "URL1");
        final DivImageElement image1 = mock(DivImageElement.class);
        ReflectionHelpers.setField(image1, "imageUrl", IMAGE_URL);
        final DivButtonsBlock.Item item1 = DivDataMockUtils.createButtonItem(itemAction1, "black", image1, null);

        DivDataMockUtils.setButtonData(buttonsBlock, divAction, ALIGNMENT, Arrays.asList(item1), true);

        final View build = ((FrameLayout) ((FrameLayout) mButtonsDivBlockViewBuilder.build(mDivView, buttonsBlock)).getChildAt(0)).getChildAt(0);
        Assert.assertTrue(build instanceof ImageView);
        verify(mDivImageLoader).loadImage(same(IMAGE_URL.toString()), any(ImageView.class));
    }

    @Test
    public void testBindSeveralElements() {
        final DivButtonsBlock buttonsBlock = mock(DivButtonsBlock.class);
        final DivAction divAction = DivDataMockUtils.createDivAction(ACTION_LOGID, ACTION_URL);

        final DivAction itemAction1 = DivDataMockUtils.createDivAction("ID1", "URL1");
        final DivAction itemAction2 = DivDataMockUtils.createDivAction("ID2", "URL2");
        final DivAction itemAction3 = DivDataMockUtils.createDivAction("ID2", "URL3");
        final DivImageElement image1 = mock(DivImageElement.class);
        ReflectionHelpers.setField(image1, "imageUrl", Uri.parse("IMAGE1 URL"));
        final DivButtonsBlock.Item item1 = DivDataMockUtils.createButtonItem(itemAction1, "black", image1, "text1");
        final DivButtonsBlock.Item item2 = DivDataMockUtils.createButtonItem(itemAction2, "green", null, "text2");
        final DivButtonsBlock.Item item3 = DivDataMockUtils.createButtonItem(itemAction3, "red", image1, "text3");

        DivDataMockUtils.setButtonData(buttonsBlock, divAction, ALIGNMENT, Arrays.asList(item1, item2, item3), true);

        final View build = mButtonsDivBlockViewBuilder.build(mDivView, buttonsBlock);
        Assert.assertTrue(build instanceof RecyclerView);
        final RecyclerView recyclerView = (RecyclerView) build;
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        Assert.assertEquals(3, adapter.getItemCount());
    }
}
