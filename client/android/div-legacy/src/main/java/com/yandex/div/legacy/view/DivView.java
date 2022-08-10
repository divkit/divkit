package com.yandex.div.legacy.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yandex.alicekit.core.annotations.PublicApi;
import com.yandex.alicekit.core.utils.AnimationUtils;
import com.yandex.alicekit.core.utils.Assert;
import com.yandex.div.DivAction;
import com.yandex.div.DivBackground;
import com.yandex.div.DivData;
import com.yandex.div.DivDataTag;
import com.yandex.div.DivPredefinedSize;
import com.yandex.div.core.images.LoadReference;
import com.yandex.div.legacy.DivBlockWithId;
import com.yandex.div.legacy.DivContext;
import com.yandex.div.legacy.DivViewBuilder;
import com.yandex.div.legacy.LegacyDivDataUtils;
import com.yandex.div.legacy.LegacyDivViewConfig;
import com.yandex.div.legacy.dagger.DivComponent;
import com.yandex.div.legacy.state.LegacyDivViewState;
import com.yandex.div.util.DivViewScrollHelper;
import com.yandex.div.view.menu.OverflowMenuSubscriber;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * View that builds its own layout using the given {@link DivData}.
 */
@SuppressLint("ViewConstructor")
@PublicApi
public class DivView extends LinearLayout implements OverflowMenuSubscriber {

    private final List<Listener> mOverflowMenuListeners = new ArrayList<>(1);
    private final List<WeakReference<LoadReference>> mImageLoadReferences = new ArrayList<>();

    @NonNull
    private final DivComponent mComponent;

    @Nullable
    private DivData mData;

    @IntRange(from = LegacyDivDataUtils.INVALID_STATE_ID)
    private int mCurrentStateId = LegacyDivDataUtils.INVALID_STATE_ID;
    @NonNull
    private LegacyDivViewConfig mConfig = LegacyDivViewConfig.DEFAULT;
    @NonNull
    private DivDataTag mTag = DivDataTag.INVALID;

    public DivView(@NonNull Context context) {
        this(context, null);
    }

    public DivView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DivView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (context instanceof DivContext) {
            DivContext divContext = (DivContext) context;
            setOrientation(VERTICAL);
            mComponent = divContext.getComponent();
        } else {
            throw new IllegalStateException("Use DivContext for creating this v");
        }
    }

    public void addLoadReference(@NonNull LoadReference loadReference, @NonNull View targetView) {
        BaseDivViewExtensionsKt.saveLoadReference(targetView, loadReference);
        mImageLoadReferences.add(new WeakReference<>(loadReference));
    }

    public void setConfig(@NonNull LegacyDivViewConfig config) {
        mConfig = config;
    }

    @Nullable
    public DivData getDivData() {
        return mData;
    }

    public boolean setDivData(@NonNull DivData data, @NonNull DivDataTag tag) {
        if (mData == data) {
            return false;
        }

        cleanup();

        mData = data;
        mTag = tag;

        setBackgroundData(data);
        setWidth();

        LegacyDivViewState state = getCurrentState();
        int stateId = state == null ? LegacyDivDataUtils.getInitialStateId(data) : state.getCurrentDivStateId();
        switchToState(stateId);

        return true;
    }

    public void setActionHandlerForView(@NonNull View view, @Nullable DivAction action) {
        if (action == null) {
            AnimationUtils.detachTouchAnimation(this);
            setOnClickListener(null);
            return;
        }

        AnimationUtils.attachTouchAnimation(view);
        view.setOnClickListener(v -> {
            mComponent.getLogger().logClick(this, view, action);
            handleUri(action.url);
        });

        String id = action.logId;
        if (!TextUtils.isEmpty(id)) {
            mComponent.getAutoLogger().setId(view, id);
        }
    }

    @NonNull
    public LegacyDivViewConfig getConfig() {
        return mConfig;
    }

    @NonNull
    public DivDataTag getDivTag() {
        return mTag;
    }

    public void handleUri(@NonNull Uri uri) {
        mComponent.getActionHandler().handleUri(uri, this);
    }

    public void cleanup() {
        dismissPendingOverflowMenus();
        mOverflowMenuListeners.clear();

        mCurrentStateId = LegacyDivDataUtils.INVALID_STATE_ID;
        mData = null;
        setBackground(null);

        cancelImageLoads();
        clearSubscriptions();
    }

    private void cancelImageLoads() {
        for (WeakReference<LoadReference> weakReference : mImageLoadReferences) {
            LoadReference reference = weakReference.get();
            if (reference != null) {
                reference.cancel();
            }
        }
        mImageLoadReferences.clear();
    }

    private void setBackgroundData(@NonNull DivData data) {
        final List<DivBackground> backgroundList = data.background;
        if (backgroundList == null || backgroundList.isEmpty()) {
            return;
        }

        final List<Drawable> drawableList = new ArrayList<>(backgroundList.size());

        for (final DivBackground background : backgroundList) {
            Drawable drawable = LegacyDivDataUtils.divBackgroundToDrawable(
                    background,
                    mComponent.getImageLoader(),
                    this
            );
            if (drawable != null) {
                drawableList.add(drawable);
            }
        }

        if (drawableList.isEmpty()) {
            return;
        }

        final LayerDrawable layerDrawable = new LayerDrawable(drawableList.toArray(new Drawable[drawableList.size()]));
        setBackground(layerDrawable);
    }

    private void setWidth() {
        DivPredefinedSize width = mData != null ? mData.width.asDivPredefinedSize() : null;
        if (width != null && DivPredefinedSize.Value.WRAP_CONTENT.equals(width.value)) {
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            layoutParams.width = LayoutParams.WRAP_CONTENT;
            setLayoutParams(layoutParams);
        }
    }

    public void switchToState(@IntRange(from = 0) int id) {
        if (mCurrentStateId == id) {
            return;
        }
        setState(id);
    }

    public void switchToInitialState() {
        if (mData == null) {
            return;
        }
        setState(LegacyDivDataUtils.getInitialStateId(mData));
    }

    @Override
    public void subscribe(@NonNull Listener listener) {
        mOverflowMenuListeners.add(listener);
    }

    public void clearSubscriptions() {
        mOverflowMenuListeners.clear();
    }

    public void onConfigurationChangedOutside(@NonNull Configuration newConfig) {
        dismissPendingOverflowMenus();
    }

    public void dismissPendingOverflowMenus() {
        //noinspection Convert2streamapi
        for (final Listener overflowMenuListener : mOverflowMenuListeners) {
            overflowMenuListener.dismiss();
        }
    }

    public boolean hasScrollableViewUnder(@NonNull MotionEvent event) {
        return DivViewScrollHelper.hasScrollableChildUnder(this, event);
    }

    public void resetToInitialState() {
        LegacyDivViewState viewState = getCurrentState();
        if (viewState != null) {
            viewState.reset();
        }

        final int initialState;
        if (mData == null) {
            Assert.fail("This shouldn't happen. Fix in MOBSEARCHANDROID-16428");
            initialState = LegacyDivDataUtils.INVALID_STATE_ID;
        } else {
            initialState = LegacyDivDataUtils.getInitialStateId(mData);
        }
        setState(initialState);
    }

    public int getCurrentStateId() {
        return mCurrentStateId;
    }

    @Nullable
    public LegacyDivViewState getCurrentState() {
        LegacyDivViewState currentState = mComponent.getStateManager().getState(mTag);
        if (mData == null || currentState == null) {
            return null;
        }

        for (DivData.State state : mData.states) {
            if (state.stateId == currentState.getCurrentDivStateId()) {
                return currentState;
            }
        }
        return null;
    }

    @NonNull
    public View getView() {
        return this;
    }

    private void setState(@IntRange(from = 0) int id) {
        mCurrentStateId = id;
        removeAllViews();

        DivData.State currentState = LegacyDivDataUtils.getStateByIdSafely(mData, mCurrentStateId);
        if (currentState == null) {
            // todo(ntcheban) invalid state processing
            mCurrentStateId = LegacyDivDataUtils.INVALID_STATE_ID;
            return;
        }

        mComponent.getStateManager().updateState(mTag, mCurrentStateId);

        setActionHandlerForView(this, currentState.action);

        final DivViewBuilder viewBuilder = mComponent.getViewBuilder();
        String randomId = UUID.randomUUID().toString();
        viewBuilder.build(this, this, currentState, DivBlockWithId.appendId(randomId + "/state", String.valueOf(mCurrentStateId)));
    }
}
