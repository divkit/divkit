package com.yandex.div.legacy;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import androidx.annotation.NonNull;
import com.yandex.alicekit.core.experiments.ExperimentConfig;
import com.yandex.div.DivBackground;
import com.yandex.div.DivData;
import com.yandex.div.DivDataTag;
import com.yandex.div.DivPosition;
import com.yandex.div.DivSize;
import com.yandex.div.DivSizeTrait;
import com.yandex.div.DivTextStyle;
import com.yandex.div.DivTitleBlock;
import com.yandex.div.core.images.BitmapSource;
import com.yandex.div.core.images.CachedBitmap;
import com.yandex.div.core.images.DivImageDownloadCallback;
import com.yandex.div.core.images.DivImageLoader;
import com.yandex.div.core.images.LoadReference;
import com.yandex.div.legacy.state.LegacyDivStateCache;
import com.yandex.div.legacy.view.DivView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import kotlin.Unit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class DivViewTest {

    private static final String MENU_COLOR = "blue";
    private static final String BACKGROUND_IMAGE_URL = "http://ya.ru/cat.png";

    @Mock
    private DivLegacyConfiguration mDivConfiguration;
    @Mock
    private DivImageLoader mImageLoader;
    @Mock
    private DivAutoLogger mAutoLogger;
    @Mock
    private DivLogger mDivLogger;
    @Mock
    private LegacyDivActionHandler mActionHandler;
    @Mock
    private ExperimentConfig mExperimentConfig;
    @Mock
    private LoadReference mLoadReference;
    @Mock
    private LegacyDivStateCache mDivStateCache;

    private final Activity mActivity = Robolectric.setupActivity(Activity.class);
    private DivContext mDivContext;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        when(mImageLoader.loadImage(anyString(), any(DivImageDownloadCallback.class))).thenReturn(mLoadReference);
        when(mDivConfiguration.getImageLoader()).thenReturn(mImageLoader);
        when(mDivConfiguration.getAutoLogger()).thenReturn(mAutoLogger);
        when(mDivConfiguration.getDivLogger()).thenReturn(mDivLogger);
        when(mDivConfiguration.getActionHandler()).thenReturn(mActionHandler);
        when(mDivConfiguration.getExperimentConfig()).thenReturn(mExperimentConfig);
        when(mDivConfiguration.getDivStateCache()).thenReturn(mDivStateCache);

        mDivContext = new DivContext(mActivity, mDivConfiguration);
    }

    @Test
    public void testDivViewLifecycle() {
        DivView divView = createDivView(mDivContext);
        divView.switchToState(1337);

        Assert.assertEquals(1, divView.getChildCount());
        final View addedChild = divView.getChildAt(0);
        Assert.assertNotNull(divView.getBackground());
        {
            final ArgumentCaptor<DivImageDownloadCallback> captor = ArgumentCaptor.forClass(DivImageDownloadCallback.class);
            verify(mImageLoader).loadImage(same(BACKGROUND_IMAGE_URL), captor.capture());
            final Bitmap bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
            captor.getValue().onSuccess(new CachedBitmap(bitmap, Uri.parse("https://uri.ru"), BitmapSource.NETWORK));
            Assert.assertNotNull(divView.getBackground());
        }
        {
            divView.switchToState(239);
            Assert.assertEquals(1, divView.getChildCount());
            Assert.assertNotSame(addedChild, divView.getChildAt(0));
        }
    }

    @Test
    public void testAsyncCreateAndBind() throws Exception {
        DivTestUtils.runAsync(() -> {
            createDivView(mDivContext);
            return Unit.INSTANCE;
        });
    }

    @NonNull
    private DivView createDivView(DivContext context) {
        final DivView divView = new DivView(context);

        final List<DivBackground> backgrounds = new ArrayList<>(3);
        backgrounds.add(DivDataMockUtils.createSolidBackground(Color.RED));
        backgrounds.add(DivDataMockUtils.createImageBackground(BACKGROUND_IMAGE_URL));
        backgrounds.add(DivDataMockUtils.createGradientBackground(Color.RED, Color.WHITE));

        final DivTitleBlock titleBlock1 = DivDataMockUtils.createDivTitleBlock("Title 1", DivTextStyle.TEXT_M, MENU_COLOR,
                                                                               DivDataMockUtils.createPaddingModifier(DivSize.M, DivPosition.LEFT),
                                                                               null);

        final DivData.State.Block blocks1 = DivDataMockUtils.createStateBlock(
                DivData.State.Block.Type.DIV_TITLE_BLOCK, titleBlock1);
        final DivData.State state1 = DivDataMockUtils.createDivState(1337, null, Collections.singletonList(blocks1));

        final DivTitleBlock titleBlock2 = DivDataMockUtils.createDivTitleBlock("Title 2", DivTextStyle.TEXT_S, MENU_COLOR,
                                                                               DivDataMockUtils.createPaddingModifier(DivSize.M, DivPosition.LEFT),
                                                                               null);
        final DivData.State.Block blocks2 = DivDataMockUtils.createStateBlock(
                DivData.State.Block.Type.DIV_TITLE_BLOCK, titleBlock2);
        final DivData.State state2 = DivDataMockUtils.createDivState(239, null, Collections.singletonList(blocks2));

        final List<DivData.State> stateList = Arrays.asList(state1, state2);
        final DivSizeTrait predefinedSize = DivDataMockUtils.createDivPredefinedSize();
        final DivData divData = DivDataMockUtils.createDivData(backgrounds, stateList, predefinedSize);
        final DivDataTag divDataTag = new DivDataTag("0");

        Assert.assertEquals(0, divView.getChildCount());
        Assert.assertNull(divView.getBackground());
        LegacyDivViewConfig divConfig = mock(LegacyDivViewConfig.class);
        when(divConfig.isContextMenuEnabled()).thenReturn(true);
        divView.setConfig(divConfig);
        divView.setDivData(divData, divDataTag);
        return divView;
    }
}
