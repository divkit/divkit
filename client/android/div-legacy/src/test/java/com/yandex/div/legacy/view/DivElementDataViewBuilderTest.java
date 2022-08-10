package com.yandex.div.legacy.view;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.TextView;
import com.yandex.alicekit.core.utils.Assert;
import com.yandex.div.DivDataTag;
import com.yandex.div.DivImageElement;
import com.yandex.div.core.images.BitmapSource;
import com.yandex.div.core.images.CachedBitmap;
import com.yandex.div.core.images.DivImageDownloadCallback;
import com.yandex.div.core.images.DivImageLoader;
import com.yandex.div.legacy.DivDataMockUtils;
import com.yandex.div.legacy.R;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class DivElementDataViewBuilderTest {

    private static final Uri IMAGE_URL = Uri.parse("http://image_url.ru");
    private static final String TEXT = "text123";

    @Mock
    private DivImageLoader mDivImageLoader;
    @Mock
    private DivView mDivView;

    private TextView mTextView;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        Activity activity = Robolectric.setupActivity(Activity.class);
        when(mDivView.getContext()).thenReturn(activity);
        when(mDivView.getDivTag()).thenReturn(DivDataTag.INVALID);

        mTextView = spy(new TextView(activity));
    }

    @Test
    public void testBindTextAndImageDivElementData() {
        // set
        final Bitmap bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
        final DivImageElement imageElement = DivDataMockUtils.createDivImageElement(IMAGE_URL, 0.5f);

        // do
        DivElementDataViewBuilder.bind(mDivView, mDivImageLoader, mTextView, TEXT, imageElement,
                                       R.dimen.div_horizontal_padding, R.dimen.div_horizontal_padding,
                                       R.dimen.div_table_image_size_l, R.dimen.div_table_image_size_l);

        // check
        Assert.assertEquals(TEXT, mTextView.getText().toString());

        verify(mTextView).setCompoundDrawables(any(), eq(null), eq(null), eq(null));
        ArgumentCaptor<DivImageDownloadCallback> argumentCaptor = ArgumentCaptor.forClass(DivImageDownloadCallback.class);
        verify(mDivImageLoader).loadImage(same(IMAGE_URL.toString()), argumentCaptor.capture());
        argumentCaptor.getValue().onSuccess(new CachedBitmap(bitmap, Uri.parse("https://uri.ru"), BitmapSource.NETWORK));
        verify(mTextView, times(2)).setCompoundDrawables(any(), eq(null), eq(null), eq(null));
    }
}
