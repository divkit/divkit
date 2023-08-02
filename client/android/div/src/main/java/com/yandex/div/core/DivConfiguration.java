package com.yandex.div.core;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yandex.div.core.annotations.PublicApi;
import com.yandex.div.core.dagger.ExperimentFlag;
import com.yandex.div.core.downloader.DivDownloader;
import com.yandex.div.core.experiments.Experiment;
import com.yandex.div.core.expression.variables.GlobalVariableController;
import com.yandex.div.core.extension.DivExtensionHandler;
import com.yandex.div.core.font.DivTypefaceProvider;
import com.yandex.div.core.images.DivImageLoader;
import com.yandex.div.core.player.DivPlayerFactory;
import com.yandex.div.core.state.DivStateChangeListener;
import com.yandex.div.core.view2.divs.widgets.DivRecyclerView;
import com.yandex.div.internal.viewpool.FixedPreCreationProfile;
import com.yandex.div.internal.viewpool.ViewPoolProfiler;
import com.yandex.div.internal.viewpool.ViewPreCreationProfile;
import com.yandex.div.state.DivStateCache;
import com.yandex.div.state.InMemoryDivStateCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dagger.Module;
import dagger.Provides;

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
    private final DivVisibilityChangeListener mDivVisibilityChangeListener;
    @NonNull
    private final DivCustomViewFactory mDivCustomViewFactory;
    @Nullable
    private final DivCustomViewAdapter mDivCustomViewAdapter;
    @Nullable
    private final DivCustomContainerViewAdapter mDivCustomContainerViewAdapter;
    @NonNull
    private final DivPlayerFactory mDivPlayerFactory;
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
    private final GlobalVariableController mGlobalVariableController;


    private final boolean mTapBeaconsEnabled;
    private final boolean mVisibilityBeaconsEnabled;
    private final boolean mLongtapActionsPassToChild;
    private final boolean mShouldIgnoreMenuItemsInActions;
    private final boolean mVisualErrors;
    private final boolean mSupportHyphenation;
    private boolean mViewPoolEnabled;
    private boolean mViewPoolProfilingEnabled;
    private boolean mResourceCacheEnabled;
    private boolean mMultipleStateChangeEnabled;
    private boolean mBindOnAttachEnabled;

    private float mRecyclerScrollInterceptionAngle;

    private DivConfiguration(
            @NonNull DivImageLoader imageLoader,
            @NonNull DivActionHandler actionHandler,
            @NonNull Div2Logger div2Logger,
            @NonNull DivDataChangeListener divDataChangeListener,
            @NonNull DivStateChangeListener divStateChangeListener,
            @NonNull DivStateCache divStateCache,
            @NonNull Div2ImageStubProvider div2ImageStubProvider,
            @NonNull DivVisibilityChangeListener divVisibilityChangeListener,
            @NonNull DivCustomViewFactory divCustomViewFactory,
            @Nullable DivCustomViewAdapter divCustomViewAdapter,
            @Nullable DivCustomContainerViewAdapter divCustomContainerViewAdapter,
            @NonNull DivPlayerFactory divPlayerFactory,
            @NonNull DivTooltipRestrictor tooltipRestrictor,
            @NonNull List<DivExtensionHandler> extensionHandlers,
            @NonNull DivDownloader divDownloader,
            @NonNull DivTypefaceProvider typefaceProvider,
            @NonNull Map<String, DivTypefaceProvider> typefaceProviders,
            @NonNull ViewPreCreationProfile viewPreCreationProfile,
            @NonNull ViewPoolProfiler.Reporter reporter,
            @Nullable GlobalVariableController globalVariableController,
            boolean tapBeaconsEnabled,
            boolean visibilityBeaconsEnabled,
            boolean longtapActionsPassToChild,
            boolean shouldIgnoreMenuItemsInActions,
            boolean visualErrors,
            boolean supportHyphenation,
            boolean viewPoolEnabled,
            boolean viewPoolProfilingEnabled,
            boolean resourceCacheEnabled,
            boolean multipleStateChangeEnabled,
            boolean bindOnAttachEnabled,
            float recyclerScrollInterceptionAngle
    ) {
        mImageLoader = imageLoader;
        mActionHandler = actionHandler;
        mDiv2Logger = div2Logger;
        mDivDataChangeListener = divDataChangeListener;
        mDivStateChangeListener = divStateChangeListener;
        mDivStateCache = divStateCache;
        mDiv2ImageStubProvider = div2ImageStubProvider;
        mDivVisibilityChangeListener = divVisibilityChangeListener;
        mDivCustomViewFactory = divCustomViewFactory;
        mDivCustomViewAdapter = divCustomViewAdapter;
        mDivCustomContainerViewAdapter = divCustomContainerViewAdapter;
        mDivPlayerFactory = divPlayerFactory;
        mTooltipRestrictor = tooltipRestrictor;
        mExtensionHandlers = extensionHandlers;
        mDivDownloader = divDownloader;
        mTypefaceProvider = typefaceProvider;
        mTypefaceProviders = typefaceProviders;
        mViewPoolReporter = reporter;
        mTapBeaconsEnabled = tapBeaconsEnabled;
        mVisibilityBeaconsEnabled = visibilityBeaconsEnabled;
        mLongtapActionsPassToChild = longtapActionsPassToChild;
        mShouldIgnoreMenuItemsInActions = shouldIgnoreMenuItemsInActions;
        mVisualErrors = visualErrors;
        mSupportHyphenation = supportHyphenation;
        mViewPoolEnabled = viewPoolEnabled;
        mViewPreCreationProfile = viewPreCreationProfile;
        mViewPoolProfilingEnabled = viewPoolProfilingEnabled;
        mResourceCacheEnabled = resourceCacheEnabled;
        mMultipleStateChangeEnabled = multipleStateChangeEnabled;
        mBindOnAttachEnabled = bindOnAttachEnabled;
        mGlobalVariableController = globalVariableController;
        mRecyclerScrollInterceptionAngle = recyclerScrollInterceptionAngle;
    }

    @Provides
    @NonNull
    public DivActionHandler getActionHandler() {
        return mActionHandler;
    }

    @Provides
    @NonNull
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
    public DivVisibilityChangeListener getDivVisibilityChangeListener() {
        return mDivVisibilityChangeListener;
    }

    @Provides
    @NonNull
    public DivCustomViewFactory getDivCustomViewFactory() {
        return mDivCustomViewFactory;
    }

    @Provides
    @Nullable
    public DivCustomViewAdapter getDivCustomViewAdapter() {
        return mDivCustomViewAdapter;
    }

    @Provides
    @Nullable
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
    @ExperimentFlag(experiment = Experiment.VIEW_POOL_PROFILING_ENABLED)
    public boolean isViewPoolProfilingEnabled() {
        return mViewPoolProfilingEnabled;
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
    public boolean isAccessibilityEnabled() {
        return true;
    }

    @Provides
    @ExperimentFlag(experiment = Experiment.BIND_ON_ATTACH_ENABLED)
    public boolean isBindOnAttachEnabled() {
        return mBindOnAttachEnabled;
    }

    @Provides
    public float getRecyclerScrollInterceptionAngle() {
        return mRecyclerScrollInterceptionAngle;
    }

    @NonNull
    public GlobalVariableController getGlobalVariableController() {
        return mGlobalVariableController;
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
        @Nullable
        private DivVisibilityChangeListener mDivVisibilityChangeListener;
        @Nullable
        private DivCustomViewFactory mDivCustomViewFactory;
        @Nullable
        private DivCustomViewAdapter mDivCustomViewAdapter;
        @Nullable
        private DivPlayerFactory mDivPlayerFactory;
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
        private GlobalVariableController mGlobalVariableController;

        private boolean mTapBeaconsEnabled = Experiment.TAP_BEACONS_ENABLED.getDefaultValue();
        private boolean mVisibilityBeaconsEnabled = Experiment.VISIBILITY_BEACONS_ENABLED.getDefaultValue();
        private boolean mLongtapActionsPassToChild = Experiment.LONGTAP_ACTIONS_PASS_TO_CHILD_ENABLED.getDefaultValue();
        private boolean mShouldIgnoreMenuItemsInActions = Experiment.IGNORE_ACTION_MENU_ITEMS_ENABLED.getDefaultValue();
        private boolean mSupportHyphenation = Experiment.HYPHENATION_SUPPORT_ENABLED.getDefaultValue();
        private boolean mVisualErrors = Experiment.VISUAL_ERRORS_ENABLED.getDefaultValue();
        private boolean mViewPoolEnabled = Experiment.VIEW_POOL_ENABLED.getDefaultValue();
        private boolean mViewPoolProfilingEnabled = Experiment.VIEW_POOL_PROFILING_ENABLED.getDefaultValue();
        private boolean mResourceCacheEnabled = Experiment.RESOURCE_CACHE_ENABLED.getDefaultValue();
        private boolean mMultipleStateChangeEnabled = Experiment.MULTIPLE_STATE_CHANGE_ENABLED.getDefaultValue();
        private boolean mBindOnAttachEnabled = false;
        private float mRecyclerScrollInterceptionAngle = DivRecyclerView.NOT_INTERCEPT;

        public Builder(@NonNull DivImageLoader imageLoader) {
            mImageLoader = imageLoader;
        }

        /**
         * @deprecated backward compat call. To be deleted VERY soon
         */
        @Deprecated
        @NonNull
        public Builder autoLogger(Object literallyAnything) {
            return this;
        }

        /**
         * @deprecated backward compat call. To be deleted VERY soon
         */
        @Deprecated
        @NonNull
        public Builder divLogger(Object literallyAnything) {
            return this;
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
            mDivVisibilityChangeListener = listener;
            return this;
        }

        /**
         * @deprecated use {@link #divCustomContainerViewAdapter}
         */
        @Deprecated
        @NonNull
        public Builder divCustomViewFactory(@NonNull DivCustomViewFactory divCustomViewFactory) {
            mDivCustomViewFactory = divCustomViewFactory;
            return this;
        }

        /**
         * @deprecated use {@link #divCustomContainerViewAdapter}
         */
        @Deprecated
        @NonNull
        public Builder divCustomViewAdapter(@NonNull DivCustomViewAdapter divCustomViewAdapter) {
            mDivCustomViewAdapter = divCustomViewAdapter;
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

        // Used for enable longtap actions in container child.
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
        @kotlin.Deprecated(message = "Accessibility is always enabled")
        public Builder enableAccessibility(boolean enable) {
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
        public Builder globalVariableController(GlobalVariableController globalVariableController) {
            mGlobalVariableController = globalVariableController;
            return this;
        }

        public Builder recyclerScrollInterceptionAngle(float angle) {
            mRecyclerScrollInterceptionAngle = angle;
            return this;
        }

        @NonNull
        public DivConfiguration build() {
            DivTypefaceProvider nonNullTypefaceProvider =
                    mTypefaceProvider == null ? DivTypefaceProvider.DEFAULT : mTypefaceProvider;
            return new DivConfiguration(
                    mImageLoader,
                    mActionHandler == null ? new DivActionHandler() : mActionHandler,
                    mDiv2Logger == null ? Div2Logger.STUB : mDiv2Logger,
                    mDivDataChangeListener == null ? DivDataChangeListener.STUB : mDivDataChangeListener,
                    mDivStateChangeListener == null ? DivStateChangeListener.STUB : mDivStateChangeListener,
                    mDivStateCache == null ? new InMemoryDivStateCache() : mDivStateCache,
                    mDiv2ImageStubProvider == null ? Div2ImageStubProvider.STUB : mDiv2ImageStubProvider,
                    mDivVisibilityChangeListener == null ?
                            DivVisibilityChangeListener.STUB : mDivVisibilityChangeListener,
                    mDivCustomViewFactory == null ? DivCustomViewFactory.STUB : mDivCustomViewFactory,
                    mDivCustomViewAdapter,
                    mDivCustomContainerViewAdapter,
                    mDivPlayerFactory == null ? DivPlayerFactory.STUB : mDivPlayerFactory,
                    mTooltipRestrictor == null ? DivTooltipRestrictor.STUB : mTooltipRestrictor,
                    mExtensionHandlers,
                    mDivDownloader == null ? DivDownloader.STUB : mDivDownloader,
                    nonNullTypefaceProvider,
                    mAdditionalTypefaceProviders == null ? new HashMap<>() : mAdditionalTypefaceProviders,
                    mViewPreCreationProfile == null ? new FixedPreCreationProfile() : mViewPreCreationProfile,
                    mViewPoolReporter == null ? ViewPoolProfiler.Reporter.NO_OP : mViewPoolReporter,
                    mGlobalVariableController == null ? new GlobalVariableController() : mGlobalVariableController,
                    mTapBeaconsEnabled,
                    mVisibilityBeaconsEnabled,
                    mLongtapActionsPassToChild,
                    mShouldIgnoreMenuItemsInActions,
                    mVisualErrors,
                    mSupportHyphenation,
                    mViewPoolEnabled,
                    mViewPoolProfilingEnabled,
                    mResourceCacheEnabled,
                    mMultipleStateChangeEnabled,
                    mBindOnAttachEnabled,
                    mRecyclerScrollInterceptionAngle);
        }
    }
}
