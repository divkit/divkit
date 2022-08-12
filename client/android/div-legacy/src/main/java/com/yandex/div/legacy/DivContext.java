package com.yandex.div.legacy;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yandex.alicekit.core.annotations.PublicApi;
import com.yandex.alicekit.core.utils.Assert;
import com.yandex.div.util.CustomInflaterContext;
import com.yandex.div.legacy.dagger.DaggerDivComponent;
import com.yandex.div.legacy.dagger.DivComponent;
import com.yandex.div.legacy.view.DivView;

/**
 * Context to be used to create instance of {@link DivView}
 * Note: if you want to inflate a DivView from XML layout file you should get an inflater as following
 * <pre>
 *     {@code
 *     DivContext divContext = new DivContext(activity, divConfiguration);
 *     LayoutInflater inflater = (LayoutInflater) divContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 *     }
 * </pre>
 * or
 * <pre>
 *     {@code
 *     DivContext divContext = new DivContext(activity, divConfiguration);
 *     LayoutInflater inflater = LayoutInflater.from(divContext);
 *     }
 * </pre>
 */
@PublicApi
public class DivContext extends CustomInflaterContext {

    @NonNull
    private final DivComponent mComponent;

    @MainThread
    public DivContext(ContextThemeWrapper baseContext, @NonNull DivLegacyConfiguration configuration) {
        super(baseContext);

        Assert.assertMainThread();
        mComponent = DaggerDivComponent.builder()
                .baseContext(baseContext)
                .configuration(configuration)
                .build();
    }

    @MainThread
    public DivContext(Activity activity, @NonNull DivLegacyConfiguration configuration) {
        this((ContextThemeWrapper) activity, configuration);
    }

    @NonNull
    @Override
    public LayoutInflater.Factory2 createInflaterFactory() {
        return new DivInflaterFactory(this);
    }

    @NonNull
    public DivComponent getComponent() {
        return mComponent;
    }

    public void warmUp() {
        mComponent.getViewBuilder();
    }

    private static class DivInflaterFactory implements LayoutInflater.Factory2 {

        private static final String DIV_VIEW_CLASS_NAME = "com.yandex.div.core.DivView";
        private static final String DIV_VIEW_SIMPLE_CLASS_NAME = "DivView";

        @NonNull
        private final DivContext mDivContext;

        DivInflaterFactory(@NonNull DivContext context) {
            mDivContext = context;
        }

        @Nullable
        @Override
        public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
            return onCreateView(name, context, attrs);
        }

        @Nullable
        @Override
        public View onCreateView(String name, Context context, AttributeSet attrs) {
            if (isDivView(name)) {
                return new DivView(mDivContext, attrs);
            }
            return null;
        }

        private boolean isDivView(@NonNull String viewClassName) {
            return DIV_VIEW_CLASS_NAME.equals(viewClassName) || DIV_VIEW_SIMPLE_CLASS_NAME.equals(viewClassName);
        }
    }
}
