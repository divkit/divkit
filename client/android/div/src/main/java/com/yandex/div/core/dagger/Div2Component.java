package com.yandex.div.core.dagger;

import android.renderscript.RenderScript;
import android.view.ContextThemeWrapper;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;

import com.yandex.div.core.Div2Logger;
import com.yandex.div.core.DivActionHandler;
import com.yandex.div.core.DivActionPerformer;
import com.yandex.div.core.DivAnimationsEnabledProvider;
import com.yandex.div.core.DivConfiguration;
import com.yandex.div.core.DivCreationTracker;
import com.yandex.div.core.DivCustomContainerChildFactory;
import com.yandex.div.core.DivCustomContainerViewAdapter;
import com.yandex.div.core.DivDataChangeListener;
import com.yandex.div.core.DivDataComponentStore;
import com.yandex.div.core.DivErrorsReporter;
import com.yandex.div.core.DivPreloader;
import com.yandex.div.core.DivViewDataPreloader;
import com.yandex.div.core.actions.DivActionTypedHandlerCombiner;
import com.yandex.div.core.annotations.InternalApi;
import com.yandex.div.core.downloader.DivDownloader;
import com.yandex.div.core.downloader.DivPatchManager;
import com.yandex.div.core.experiments.Experiment;
import com.yandex.div.core.expression.storedvalues.StoredValuesController;
import com.yandex.div.core.expression.variables.DivVariableController;
import com.yandex.div.core.extension.DivExtensionController;
import com.yandex.div.core.player.DivPlayerFactory;
import com.yandex.div.core.player.DivPlayerPreloader;
import com.yandex.div.core.player.DivVideoActionHandler;
import com.yandex.div.core.state.DivStateChangeListener;
import com.yandex.div.core.tooltip.DivTooltipController;
import com.yandex.div.core.tooltip.DivTooltipManager;
import com.yandex.div.core.util.bitmap.BitmapEffectHelper;
import com.yandex.div.core.view2.Div2Builder;
import com.yandex.div.core.view2.DivBinder;
import com.yandex.div.core.view2.DivImagePreloader;
import com.yandex.div.core.view2.DivViewCreator;
import com.yandex.div.core.view2.DivVisibilityActionTracker;
import com.yandex.div.core.view2.ReleaseManager;
import com.yandex.div.core.view2.animations.DivAnimationsEnabledController;
import com.yandex.div.core.view2.divs.DivActionBinder;
import com.yandex.div.core.view2.divs.widgets.MediaLoadViewVisitor;
import com.yandex.div.histogram.reporter.HistogramReporter;
import com.yandex.div.internal.viewpool.optimization.PerformanceDependentSessionProfiler;
import com.yandex.div.internal.viewpool.optimization.ViewPreCreationProfileRepository;
import com.yandex.yatagan.BindsInstance;
import com.yandex.yatagan.Component;

import javax.inject.Named;

/**
 * Context scoped component for div2 {@link com.yandex.div.core.Div2Context}
 */
@InternalApi
@DivScope
@Component(isRoot = false,
    multiThreadAccess = true,
    modules = {
        Div2Module.class,
        DivConfiguration.class,
        DivHistogramsModule.class
    }
)
public interface Div2Component {

    @NonNull
    Div2Builder getDiv2Builder();

    @NonNull
    DivBinder getDivBinder();

    @NonNull
    DivViewCreator getDivViewCreator();

    @NonNull
    DivImagePreloader getImagePreloader();

    @NonNull
    DivPreloader getPreloader();

    @NonNull
    Div2Logger getDiv2Logger();

    @NonNull
    DivErrorsReporter getDivErrorsReporter();

    @NonNull
    DivVisibilityActionTracker getVisibilityActionTracker();

    @NonNull
    DivActionBinder getActionBinder();

    @NonNull
    DivActionPerformer getActionPerformer();

    @NonNull
    DivCustomContainerChildFactory getDivCustomContainerChildFactory();

    @NonNull
    DivCustomContainerViewAdapter getDivCustomContainerViewAdapter();

    @NonNull
    DivExtensionController getExtensionController();

    @NonNull
    DivDataChangeListener getDivDataChangeListener();

    @NonNull
    DivVideoActionHandler getDivVideoActionHandler();

    @NonNull
    DivStateChangeListener getDivStateChangeListener();

    @NonNull
    DivActionHandler getActionHandler();

    @NonNull
    DivTooltipController getTooltipController();

    @NonNull
    DivPatchManager getPatchManager();

    @NonNull
    Div2ViewComponent.Builder viewComponent();

    @NonNull
    DivDownloader getDivDownloader();

    @NonNull
    DivVariableController getDivVariableController();

    @NonNull
    DivAnimationsEnabledProvider getAnimationsEnabledProvider();

    @NonNull
    DivAnimationsEnabledController getAnimationsEnabledController();

    @NonNull
    PerformanceDependentSessionProfiler getPerformanceDependentSessionProfiler();

    @NonNull
    ViewPreCreationProfileRepository getViewPreCreationProfileRepository();

    @NonNull
    HistogramReporter getHistogramReporter();

    @NonNull
    @Deprecated
    DivPlayerFactory getDivVideoFactory();

    @NonNull
    DivPlayerPreloader getDivVideoPreloader();

    @NonNull
    DivCreationTracker getDivCreationTracker();

    @NonNull
    @Deprecated
    RenderScript getRenderScript();

    @NonNull
    ReleaseManager getReleaseManager();

    @NonNull
    StoredValuesController getStoredValuesController();

    @NonNull
    @ExperimentFlag(experiment = Experiment.BIND_ON_ATTACH_ENABLED)
    boolean isBindOnAttachEnabled();

    @NonNull
    @ExperimentFlag(experiment = Experiment.COMPLEX_REBIND_ENABLED)
    boolean isComplexRebindEnabled();

    @NonNull
    @ExperimentFlag(experiment = Experiment.STRICT_ITEM_BUILDER_VIEW_REUSE_ENABLED)
    boolean isStrictItemBuilderViewReuseEnabled();

    @NonNull
    @ExperimentFlag(experiment = Experiment.PAGER_PAGE_CLIP_ENABLED)
    boolean isPagerPageClipEnabled();

    @NonNull
    DivActionTypedHandlerCombiner getActionTypedHandlerCombiner();

    @NonNull
    BitmapEffectHelper getBitmapEffectHelper();

    @NonNull
    DivViewDataPreloader getDivViewDataPreloader();

    @NonNull
    DivTooltipManager getTooltipManager();

    @NonNull
    DivDataComponent.Builder getDataComponent();

    @NonNull
    DivDataComponentStore getDataComponentStore();

    /**
     * Builder for Div2Component
     */
    @Component.Builder
    interface Builder {

        @BindsInstance
        @NonNull
        Builder baseContext(@NonNull ContextThemeWrapper baseContext);

        @NonNull
        Builder configuration(@NonNull DivConfiguration configuration);

        @NonNull
        @BindsInstance
        Builder themeId(@Named(Names.THEME) @StyleRes int themeId);

        @NonNull
        @BindsInstance
        Builder divCreationTracker(@NonNull DivCreationTracker divCreationTracker);

        @NonNull
        @BindsInstance
        Builder divVariableController(@NonNull DivVariableController divVariableController);

        @NonNull
        Div2Component build();
    }
}
