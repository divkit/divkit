package com.yandex.div.core;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yandex.div.core.annotations.InternalApi;
import com.yandex.div.core.annotations.PublicApi;
import com.yandex.div.core.dagger.ExperimentFlag;
import com.yandex.div.core.dagger.Names;
import com.yandex.div.core.downloader.DivDownloader;
import com.yandex.div.core.experiments.Experiment;
import com.yandex.div.core.expression.variables.DivVariableController;
import com.yandex.div.core.expression.variables.GlobalVariableController;
import com.yandex.div.core.extension.DivExtensionHandler;
import com.yandex.div.core.font.DivTypefaceProvider;
import com.yandex.div.core.images.DivImageLoader;
import com.yandex.div.core.player.DivPlayerFactory;
import com.yandex.div.core.player.DivPlayerPreloader;
import com.yandex.div.core.state.DivStateChangeListener;
import com.yandex.div.core.view2.divs.widgets.DivRecyclerView;
import com.yandex.div.internal.viewpool.ViewPoolProfiler;
import com.yandex.div.internal.viewpool.ViewPreCreationProfile;
import com.yandex.div.state.DivStateCache;
import com.yandex.div.state.InMemoryDivStateCache;
import com.yandex.yatagan.Module;
import com.yandex.yatagan.Provides;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;

/**
 * Holds {@link com.yandex.div.core.view2.Div2View} configuration.
 * Create instance using {@link Builder} class.
 */
@PublicApi
@Module
public class DivConfiguration {

    @NonNull
    private final DivImageLoader mImageLoader;
    @NonNull
    private final DivActionHandler mActionHandler;
    @NonNull
    private final Div2Logger mDiv2Logger;
    @NonNull
    private final DivDataChangeListener mDivDataChangeListener;
    @NonNull
    private final DivStateChangeListener mDivStateChangeListener;
    @NonNull
    private final DivStateCache mDivStateCache;
    @NonNull
    private final Div2ImageStubProvider mDiv2ImageStubProvider;
    @NonNull
    private final List<DivVisibilityChangeListener> mDivVisibilityChangeListeners;
    @NonNull
    private final DivCustomContainerViewAdapter mDivCustomContainerViewAdapter;
    @NonNull
    private final DivPlayerFactory mDivPlayerFactory;
    @NonNull
    private final DivPlayerPreloader mDivPlayerPreloader;
    @NonNull
    private final DivTooltipRestrictor mTooltipRestrictor;
    @NonNull
    private final List<DivExtensionHandler> mExtensionHandlers;
    @NonNull
    private final DivDownloader mDivDownloader;
    @NonNull
    private final DivTypefaceProvider mTypefaceProvider;
    @NonNull
    private final Map<String, DivTypefaceProvider> mTypefaceProviders;
    @NonNull
    private final ViewPreCreationProfile mViewPreCreationProfile;
    @NonNull
    private final ViewPoolProfiler.Reporter mViewPoolReporter;
    @NonNull
    private final DivVariableController mDivVariableController;

    private final boolean mTapBeaconsEnabled;
    private final boolean mVisibilityBeaconsEnabled;
    private final boolean mSwipeOutBeaconsEnabled;
    private final boolean mLongtapActionsPassToChild;
    private final boolean mShouldIgnoreMenuItemsInActions;
    private final boolean mVisualErrors;
    private final boolean mSupportHyphenation;
    private final boolean mAccessibilityEnabled;
    private boolean mViewPoolEnabled;
    private boolean mViewPoolProfilingEnabled;
    private boolean mViewPoolOptimizationDebug;
    private boolean mResourceCacheEnabled;
    private boolean mMultipleStateChangeEnabled;
    private boolean mBindOnAttachEnabled;
    private boolean mComplexRebindEnabled;
    private boolean mPagerPageClipEnabled;
    private boolean mPermanentDebugPanelEnabled;
    private boolean mRenderEffectEnabled;

    private float mRecyclerScrollInterceptionAngle;

    private DivConfiguration(
            @NonNull DivImageLoader imageLoader,
            @NonNull DivActionHandler actionHandler,
            @NonNull Div2Logger div2Logger,
            @NonNull DivDataChangeListener divDataChangeListener,
            @NonNull DivStateChangeListener divStateChangeListener,
            @NonNull DivStateCache divStateCache,
            @NonNull Div2ImageStubProvider div2ImageStubProvider,
            @NonNull List<DivVisibilityChangeListener> divVisibilityChangeListeners,
            @NonNull DivCustomContainerViewAdapter divCustomContainerViewAdapter,
            @NonNull DivPlayerFactory divPlayerFactory,
            @NonNull DivPlayerPreloader divPlayerPreloader,
            @NonNull DivTooltipRestrictor tooltipRestrictor,
            @NonNull List<DivExtensionHandler> extensionHandlers,
            @NonNull DivDownloader divDownloader,
            @NonNull DivTypefaceProvider typefaceProvider,
            @NonNull Map<String, DivTypefaceProvider> typefaceProviders,
            @NonNull ViewPreCreationProfile viewPreCreationProfile,
            @NonNull ViewPoolProfiler.Reporter reporter,
            @NonNull DivVariableController divVariableController,
            boolean tapBeaconsEnabled,
            boolean visibilityBeaconsEnabled,
            boolean swipeOutBeaconsEnabled,
            boolean longtapActionsPassToChild,
            boolean shouldIgnoreMenuItemsInActions,
            boolean visualErrors,
            boolean supportHyphenation,
            boolean accessibilityEnabled,
            boolean viewPoolEnabled,
            boolean viewPoolProfilingEnabled,
            boolean viewPoolOptimizationDebug,
            boolean resourceCacheEnabled,
            boolean multipleStateChangeEnabled,
            boolean bindOnAttachEnabled,
            boolean complexRebindEnabled,
            boolean pagerChildrenClipEnabled,
            boolean permanentDebugPanelEnabled,
            float recyclerScrollInterceptionAngle,
            boolean renderEffectEnabled
    ) {
        mImageLoader = imageLoader;
        mActionHandler = actionHandler;
        mDiv2Logger = div2Logger;
        mDivDataChangeListener = divDataChangeListener;
        mDivStateChangeListener = divStateChangeListener;
        mDivStateCache = divStateCache;
        mDiv2ImageStubProvider = div2ImageStubProvider;
        mDivVisibilityChangeListeners = divVisibilityChangeListeners;
        mDivCustomContainerViewAdapter = divCustomContainerViewAdapter;
        mDivPlayerFactory = divPlayerFactory;
        mDivPlayerPreloader = divPlayerPreloader;
        mTooltipRestrictor = tooltipRestrictor;
        mExtensionHandlers = extensionHandlers;
        mDivDownloader = divDownloader;
        mTypefaceProvider = typefaceProvider;
        mTypefaceProviders = typefaceProviders;
        mViewPoolReporter = reporter;
        mTapBeaconsEnabled = tapBeaconsEnabled;
        mVisibilityBeaconsEnabled = visibilityBeaconsEnabled;
        mSwipeOutBeaconsEnabled = swipeOutBeaconsEnabled;
        mLongtapActionsPassToChild = longtapActionsPassToChild;
        mShouldIgnoreMenuItemsInActions = shouldIgnoreMenuItemsInActions;
        mVisualErrors = visualErrors;
        mSupportHyphenation = supportHyphenation;
        mAccessibilityEnabled = accessibilityEnabled;
        mViewPoolEnabled = viewPoolEnabled;
        mViewPreCreationProfile = viewPreCreationProfile;
        mViewPoolProfilingEnabled = viewPoolProfilingEnabled;
        mViewPoolOptimizationDebug = viewPoolOptimizationDebug;
        mResourceCacheEnabled = resourceCacheEnabled;
        mMultipleStateChangeEnabled = multipleStateChangeEnabled;
        mBindOnAttachEnabled = bindOnAttachEnabled;
        mComplexRebindEnabled = complexRebindEnabled;
        mPermanentDebugPanelEnabled = permanentDebugPanelEnabled;
        mDivVariableController = divVariableController;
        mRecyclerScrollInterceptionAngle = recyclerScrollInterceptionAngle;
        mPagerPageClipEnabled = pagerChildrenClipEnabled;
        mRenderEffectEnabled = renderEffectEnabled;
    }

    @Provides
    @NonNull
    public DivActionHandler getActionHandler() {
        return mActionHandler;
    }

    @Provides
    @NonNull
    @Named(Names.UNWRAPPED_IMAGE_LOADER)
    public DivImageLoader getImageLoader() {
        return mImageLoader;
    }

    @Provides
    @NonNull
    public Div2Logger getDiv2Logger() {
        return mDiv2Logger;
    }

    @Provides
    @NonNull
    public DivDataChangeListener getDivDataChangeListener() {
        return mDivDataChangeListener;
    }

    @Provides
    @NonNull
    public DivStateChangeListener getDivStateChangeListener() {
        return mDivStateChangeListener;
    }

    @Provides
    @NonNull
    public DivStateCache getDivStateCache() {
        return mDivStateCache;
    }

    @Provides
    @NonNull
    public Div2ImageStubProvider getDiv2ImageStubProvider() {
        return mDiv2ImageStubProvider;
    }

    @Provides
    @NonNull
    public List<? extends DivVisibilityChangeListener> getDivVisibilityChangeListeners() {
        return mDivVisibilityChangeListeners;
    }

    @Provides
    @NonNull
    public DivCustomContainerViewAdapter getDivCustomContainerViewAdapter() {
        return mDivCustomContainerViewAdapter;
    }

    @Provides
    @NonNull
    public DivPlayerFactory getDivPlayerFactory() {
        return mDivPlayerFactory;
    }

    @Provides
    @NonNull
    public DivPlayerPreloader getDivPlayerPreloader() {
        return mDivPlayerPreloader;
    }

    @Provides
    @NonNull
    public DivTooltipRestrictor getTooltipRestrictor() {
        return mTooltipRestrictor;
    }

    @Provides
    @NonNull
    public List<? extends DivExtensionHandler> getExtensionHandlers() {
        return mExtensionHandlers;
    }

    @Provides
    @ExperimentFlag(experiment = Experiment.TAP_BEACONS_ENABLED)
    public boolean isTapBeaconsEnabled() {
        return mTapBeaconsEnabled;
    }

    @Provides
    @ExperimentFlag(experiment = Experiment.VISIBILITY_BEACONS_ENABLED)
    public boolean isVisibilityBeaconsEnabled() {
        return mVisibilityBeaconsEnabled;
    }

    @Provides
    @ExperimentFlag(experiment = Experiment.SWIPE_OUT_BEACONS_ENABLED)
    public boolean isSwipeOutBeaconsEnabled() {
        return mSwipeOutBeaconsEnabled;
    }

    @Provides
    @ExperimentFlag(experiment = Experiment.LONGTAP_ACTIONS_PASS_TO_CHILD_ENABLED)
    public boolean isLongtapActionsPassToChild() {
        return mLongtapActionsPassToChild;
    }

    /**
     * {@see DivConfiguration.Builder#isContextMenuHandlerOverridden()}.
     */
    @Provides
    @ExperimentFlag(experiment = Experiment.IGNORE_ACTION_MENU_ITEMS_ENABLED)
    public boolean isContextMenuHandlerOverridden() {
        return mShouldIgnoreMenuItemsInActions;
    }

    @Provides
    @ExperimentFlag(experiment = Experiment.HYPHENATION_SUPPORT_ENABLED)
    public boolean isHyphenationSupported() {
        return mSupportHyphenation;
    }

    @Provides
    @ExperimentFlag(experiment = Experiment.VIEW_POOL_ENABLED)
    public boolean isViewPoolEnabled() {
        return mViewPoolEnabled;
    }

    @Provides
    @ExperimentFlag(experiment = Experiment.PAGER_PAGE_CLIP_ENABLED)
    public boolean isPagerPageClipEnabled() {
        return mPagerPageClipEnabled;
    }

    @Provides
    @ExperimentFlag(experiment = Experiment.VIEW_POOL_PROFILING_ENABLED)
    public boolean isViewPoolProfilingEnabled() {
        return mViewPoolProfilingEnabled;
    }

    @Provides
    @ExperimentFlag(experiment = Experiment.VIEW_POOL_OPTIMIZATION_DEBUG)
    public boolean isDebuggingViewPoolOptimization() {
        return mViewPoolOptimizationDebug;
    }

    @Provides
    @ExperimentFlag(experiment = Experiment.RESOURCE_CACHE_ENABLED)
    public boolean isResourceCacheEnabled() {
        return mResourceCacheEnabled;
    }

    @Provides
    @ExperimentFlag(experiment = Experiment.MULTIPLE_STATE_CHANGE_ENABLED)
    public boolean isMultipleStateChangeEnabled() {
        return mMultipleStateChangeEnabled;
    }

    @Provides
    @ExperimentFlag(experiment = Experiment.RENDER_EFFECT_ENABLED)
    public boolean isRenderEffectEnabled() {
        return mRenderEffectEnabled;
    }

    @Provides
    @NonNull
    public DivDownloader getDivDownloader() {
        return mDivDownloader;
    }

    @Provides
    @NonNull
    public DivTypefaceProvider getTypefaceProvider() {
        return mTypefaceProvider;
    }

    @Provides
    @NonNull
    public Map<String, ? extends DivTypefaceProvider> getAdditionalTypefaceProviders() {
        return mTypefaceProviders;
    }

    @Provides
    @NonNull
    public ViewPreCreationProfile getViewPreCreationProfile() {
        return mViewPreCreationProfile;
    }

    @Provides
    @NonNull
    public ViewPoolProfiler.Reporter getViewPoolReporter() {
        return mViewPoolReporter;
    }

    @Provides
    @ExperimentFlag(experiment = Experiment.VISUAL_ERRORS_ENABLED)
    public boolean getAreVisualErrorsEnabled() {
        return mVisualErrors;
    }

    @Provides
    @kotlin.Deprecated(message = "Accessibility is always enabled")
    @ExperimentFlag(experiment = Experiment.ACCESSIBILITY_ENABLED)
    public boolean isAccessibilityEnabled() {
        return mAccessibilityEnabled;
    }

    @Provides
    @ExperimentFlag(experiment = Experiment.BIND_ON_ATTACH_ENABLED)
    public boolean isBindOnAttachEnabled() {
        return mBindOnAttachEnabled;
    }

    @Provides
    @ExperimentFlag(experiment = Experiment.COMPLEX_REBIND_ENABLED)
    public boolean isComplexRebindEnabled() {
        return mComplexRebindEnabled;
    }

    @Provides
    @ExperimentFlag(experiment = Experiment.PERMANENT_DEBUG_PANEL_ENABLED)
    public boolean isPermanentDebugPanelEnabled() {
        return mPermanentDebugPanelEnabled;
    }

    @Provides
    public float getRecyclerScrollInterceptionAngle() {
        return mRecyclerScrollInterceptionAngle;
    }

    @NonNull
    public DivVariableController getDivVariableController() {
        return mDivVariableController;
    }

    @Deprecated
    @InternalApi
    public GlobalVariableController getGlobalVariableController() {
        return new GlobalVariableController(mDivVariableController);
    }

    public static class Builder {

        @NonNull
        private final DivImageLoader mImageLoader;
        @Nullable
        private DivActionHandler mActionHandler;
        @Nullable
        private Div2Logger mDiv2Logger;
        @Nullable
        private DivDataChangeListener mDivDataChangeListener;
        @Nullable
        private DivStateChangeListener mDivStateChangeListener;
        @Nullable
        private DivStateCache mDivStateCache;
        @Nullable
        private Div2ImageStubProvider mDiv2ImageStubProvider;
        @NonNull
        private final List<DivVisibilityChangeListener> mDivVisibilityChangeListeners = new ArrayList<>();
        @Nullable
        private DivPlayerFactory mDivPlayerFactory;
        @Nullable
        private DivPlayerPreloader mDivPlayerPreloader;
        @Nullable
        private DivCustomContainerViewAdapter mDivCustomContainerViewAdapter;
        @Nullable
        private DivTooltipRestrictor mTooltipRestrictor;
        @NonNull
        private final List<DivExtensionHandler> mExtensionHandlers = new ArrayList<>();
        @Nullable
        private DivDownloader mDivDownloader;
        @Nullable
        private DivTypefaceProvider mTypefaceProvider;
        @Nullable
        private Map<String, DivTypefaceProvider> mAdditionalTypefaceProviders;
        @Nullable
        private ViewPreCreationProfile mViewPreCreationProfile;
        @Nullable
        private ViewPoolProfiler.Reporter mViewPoolReporter;
        @Nullable
        private DivVariableController mDivVariableController;
        private boolean mTapBeaconsEnabled = Experiment.TAP_BEACONS_ENABLED.getDefaultValue();
        private boolean mVisibilityBeaconsEnabled = Experiment.VISIBILITY_BEACONS_ENABLED.getDefaultValue();
        private boolean mSwipeOutBeaconsEnabled = Experiment.SWIPE_OUT_BEACONS_ENABLED.getDefaultValue();
        private boolean mLongtapActionsPassToChild = Experiment.LONGTAP_ACTIONS_PASS_TO_CHILD_ENABLED.getDefaultValue();
        private boolean mShouldIgnoreMenuItemsInActions = Experiment.IGNORE_ACTION_MENU_ITEMS_ENABLED.getDefaultValue();
        private boolean mSupportHyphenation = Experiment.HYPHENATION_SUPPORT_ENABLED.getDefaultValue();
        private boolean mVisualErrors = Experiment.VISUAL_ERRORS_ENABLED.getDefaultValue();
        private boolean mAccessibilityEnabled = Experiment.ACCESSIBILITY_ENABLED.getDefaultValue();
        private boolean mViewPoolEnabled = Experiment.VIEW_POOL_ENABLED.getDefaultValue();
        private boolean mViewPoolProfilingEnabled = Experiment.VIEW_POOL_PROFILING_ENABLED.getDefaultValue();
        private boolean mViewPoolOptimizationDebug = Experiment.VIEW_POOL_OPTIMIZATION_DEBUG.getDefaultValue();
        private boolean mResourceCacheEnabled = Experiment.RESOURCE_CACHE_ENABLED.getDefaultValue();
        private boolean mMultipleStateChangeEnabled = Experiment.MULTIPLE_STATE_CHANGE_ENABLED.getDefaultValue();
        private boolean mBindOnAttachEnabled = false;
        private boolean mComplexRebindEnabled = Experiment.COMPLEX_REBIND_ENABLED.getDefaultValue();
        private boolean mPagerPageClipEnabled = Experiment.PAGER_PAGE_CLIP_ENABLED.getDefaultValue();
        private boolean mPermanentDebugPanelEnabled = Experiment.PERMANENT_DEBUG_PANEL_ENABLED.getDefaultValue();
        private float mRecyclerScrollInterceptionAngle = DivRecyclerView.NOT_INTERCEPT;
        private boolean mRenderEffectEnabled = Experiment.RENDER_EFFECT_ENABLED.getDefaultValue();

        public Builder(@NonNull DivImageLoader imageLoader) {
            mImageLoader = imageLoader;
        }

        @NonNull
        public Builder actionHandler(@NonNull DivActionHandler actionHandler) {
            mActionHandler = actionHandler;
            return this;
        }

        @NonNull
        public Builder div2Logger(@NonNull Div2Logger logger) {
            mDiv2Logger = logger;
            return this;
        }

        @NonNull
        public Builder divDataChangeListener(@NonNull DivDataChangeListener listener) {
            mDivDataChangeListener = listener;
            return this;
        }

        @NonNull
        public Builder divStateChangeListener(@NonNull DivStateChangeListener divStateChangeListener) {
            mDivStateChangeListener = divStateChangeListener;
            return this;
        }

        @NonNull
        public Builder divStateCache(@NonNull DivStateCache divStateCache) {
            mDivStateCache = divStateCache;
            return this;
        }

        @NonNull
        public Builder div2ImageStubProvider(@NonNull Div2ImageStubProvider div2ImageStubProvider) {
            mDiv2ImageStubProvider = div2ImageStubProvider;
            return this;
        }

        @NonNull
        public Builder divVisibilityChangeListener(@NonNull DivVisibilityChangeListener listener) {
            mDivVisibilityChangeListeners.add(listener);
            return this;
        }

        @NonNull
        public Builder divCustomContainerViewAdapter(
                @NonNull DivCustomContainerViewAdapter divCustomContainerViewAdapter) {
            mDivCustomContainerViewAdapter = divCustomContainerViewAdapter;
            return this;
        }

        @NonNull
        public Builder divPlayerFactory(@NonNull DivPlayerFactory divPlayerFactory) {
            mDivPlayerFactory = divPlayerFactory;
            return this;
        }

        @NonNull
        @Deprecated // Use DivPlayerFactory to setup preloader
        public Builder divPlayerPreloader(@NonNull DivPlayerPreloader divPlayerPreloader) {
            mDivPlayerPreloader = divPlayerPreloader;
            return this;
        }

        @NonNull
        public Builder tooltipRestrictor(@NonNull DivTooltipRestrictor tooltipRestrictor) {
            mTooltipRestrictor = tooltipRestrictor;
            return this;
        }

        @NonNull
        public Builder enableTapBeacons() {
            mTapBeaconsEnabled = true;
            return this;
        }

        @NonNull
        public Builder enableVisibilityBeacons() {
            mVisibilityBeaconsEnabled = true;
            return this;
        }

        @NonNull
        public Builder swipeOutBeacons(Boolean enabled) {
            mSwipeOutBeaconsEnabled = enabled;
            return this;
        }

        /**
         * Used for enable longtap actions in container child.
         */
        @NonNull
        public Builder enableLongtapActionsPassingToChild() {
            mLongtapActionsPassToChild = true;
            return this;
        }

        @NonNull
        public Builder extension(@NonNull DivExtensionHandler extensionHandler) {
            mExtensionHandlers.add(extensionHandler);
            return this;
        }

        @NonNull
        public Builder divDownloader(@NonNull DivDownloader divDownloader) {
            mDivDownloader = divDownloader;
            return this;
        }

        @NonNull
        public Builder additionalTypefaceProviders(
            @NonNull Map<String, DivTypefaceProvider> typefaceProviders
        ) {
            mAdditionalTypefaceProviders = typefaceProviders;
            return this;
        }

        @NonNull
        public Builder typefaceProvider(@NonNull DivTypefaceProvider typefaceProvider) {
            mTypefaceProvider = typefaceProvider;
            return this;
        }

        @NonNull
        public Builder viewPreCreationProfile(@NonNull ViewPreCreationProfile viewPreCreationProfile) {
            mViewPreCreationProfile = viewPreCreationProfile;
            return this;
        }

        @NonNull
        public Builder viewPoolReporter(@NonNull ViewPoolProfiler.Reporter viewPoolReporter) {
            mViewPoolReporter = viewPoolReporter;
            return this;
        }

        /**
         * Despite method's name, the flag just shutdown support for {@link com.yandex.div2.DivAction#menuItems}.
         */
        @NonNull
        public Builder overrideContextMenuHandler(boolean shouldIgnore) {
            mShouldIgnoreMenuItemsInActions = shouldIgnore;
            return this;
        }

        /**
         * Shows tiny error counter at each div. Click on counter open detailed error view.
         */
        @NonNull
        public Builder visualErrorsEnabled(boolean enabled) {
            mVisualErrors = enabled;
            return this;
        }

        @NonNull
        public Builder supportHyphenation(boolean supportHyphenation) {
            mSupportHyphenation = supportHyphenation;
            return this;
        }

        @NonNull
        public Builder enableAccessibility(boolean enable) {
            mAccessibilityEnabled = enable;
            return this;
        }

        @NonNull
        public Builder enableViewPool(boolean enable) {
            mViewPoolEnabled = enable;
            return this;
        }

        @NonNull
        public Builder enableViewPoolProfiling(boolean enable) {
            mViewPoolProfilingEnabled = enable;
            return this;
        }

        @NonNull
        public Builder debugViewPoolOptimization(boolean enable) {
            mViewPoolOptimizationDebug = enable;
            return this;
        }

        @NonNull
        public Builder enableResourceCache(boolean enable) {
            mResourceCacheEnabled = enable;
            return this;
        }

        @NonNull
        public Builder enableMultipleStateChange(boolean enable) {
            mMultipleStateChangeEnabled = enable;
            return this;
        }

        @NonNull
        public Builder enableBindOnAttach(boolean enable) {
            mBindOnAttachEnabled = enable;
            return this;
        }

        @NonNull
        public Builder enableComplexRebind(boolean enable) {
            mComplexRebindEnabled = enable;
            return this;
        }

        @NonNull
        public Builder enablePagerPageClipping(boolean enable) {
            mPagerPageClipEnabled = enable;
            return this;
        }

        @NonNull
        public Builder enablePermanentDebugPanel(boolean enable) {
            mPermanentDebugPanelEnabled = enable;
            return this;
        }

        @NonNull
        public Builder divVariableController(DivVariableController variableController) {
            mDivVariableController = variableController;
            return this;
        }

        @Deprecated
        @InternalApi
        @NonNull
        public Builder globalVariableController(GlobalVariableController variableController) {
            return this;
        }

        public Builder recyclerScrollInterceptionAngle(float angle) {
            mRecyclerScrollInterceptionAngle = angle;
            return this;
        }

        @NonNull
        public Builder setRenderEffectEnabled(boolean enable) {
            mRenderEffectEnabled = enable;
            return this;
        }

        @NonNull
        public DivConfiguration build() {
            DivTypefaceProvider nonNullTypefaceProvider =
                    mTypefaceProvider == null ? DivTypefaceProvider.DEFAULT : mTypefaceProvider;
            DivPlayerFactory divPlayerFactory =
                    mDivPlayerFactory == null ? DivPlayerFactory.STUB : mDivPlayerFactory;
            DivPlayerPreloader divPlayerPreloader =
                    mDivPlayerPreloader == null ? divPlayerFactory.makePreloader() : mDivPlayerPreloader;
            return new DivConfiguration(
                    mImageLoader,
                    mActionHandler == null ? new DivActionHandler() : mActionHandler,
                    mDiv2Logger == null ? Div2Logger.STUB : mDiv2Logger,
                    mDivDataChangeListener == null ? DivDataChangeListener.STUB : mDivDataChangeListener,
                    mDivStateChangeListener == null ? DivStateChangeListener.STUB : mDivStateChangeListener,
                    mDivStateCache == null ? new InMemoryDivStateCache() : mDivStateCache,
                    mDiv2ImageStubProvider == null ? Div2ImageStubProvider.STUB : mDiv2ImageStubProvider,
                    mDivVisibilityChangeListeners,
                    mDivCustomContainerViewAdapter == null ? DivCustomContainerViewAdapter.STUB
                            : mDivCustomContainerViewAdapter,
                    divPlayerFactory,
                    divPlayerPreloader,
                    mTooltipRestrictor == null ? DivTooltipRestrictor.STUB : mTooltipRestrictor,
                    mExtensionHandlers,
                    mDivDownloader == null ? DivDownloader.STUB : mDivDownloader,
                    nonNullTypefaceProvider,
                    mAdditionalTypefaceProviders == null ? new HashMap<>() : mAdditionalTypefaceProviders,
                    mViewPreCreationProfile == null ? new ViewPreCreationProfile() : mViewPreCreationProfile,
                    mViewPoolReporter == null ? ViewPoolProfiler.Reporter.NO_OP : mViewPoolReporter,
                    mDivVariableController == null ? new DivVariableController() : mDivVariableController,
                    mTapBeaconsEnabled,
                    mVisibilityBeaconsEnabled,
                    mSwipeOutBeaconsEnabled,
                    mLongtapActionsPassToChild,
                    mShouldIgnoreMenuItemsInActions,
                    mVisualErrors,
                    mSupportHyphenation,
                    mAccessibilityEnabled,
                    mViewPoolEnabled,
                    mViewPoolProfilingEnabled,
                    mViewPoolOptimizationDebug,
                    mResourceCacheEnabled,
                    mMultipleStateChangeEnabled,
                    mBindOnAttachEnabled,
                    mComplexRebindEnabled,
                    mPagerPageClipEnabled,
                    mPermanentDebugPanelEnabled,
                    mRecyclerScrollInterceptionAngle,
                    mRenderEffectEnabled);
        }
    }
}
