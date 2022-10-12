package com.yandex.div.core.dagger;

import android.view.ContextThemeWrapper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import com.yandex.div.core.Div2Logger;
import com.yandex.div.core.DivActionHandler;
import com.yandex.div.core.DivConfiguration;
import com.yandex.div.core.DivCreationTracker;
import com.yandex.div.core.DivCustomContainerChildFactory;
import com.yandex.div.core.DivCustomViewAdapter;
import com.yandex.div.core.DivCustomViewFactory;
import com.yandex.div.core.DivDataChangeListener;
import com.yandex.div.core.DivStateChangeListener;
import com.yandex.div.core.downloader.DivDownloader;
import com.yandex.div.core.downloader.DivPatchManager;
import com.yandex.div.core.expression.ExpressionsRuntimeProvider;
import com.yandex.div.core.expression.variables.GlobalVariableController;
import com.yandex.div.core.state.DivStateManager;
import com.yandex.div.core.state.TemporaryDivStateCache;
import com.yandex.div.core.tooltip.DivTooltipController;
import com.yandex.div.core.view2.Div2Builder;
import com.yandex.div.core.view2.DivBinder;
import com.yandex.div.core.view2.DivImagePreloader;
import com.yandex.div.core.view2.DivPreloader;
import com.yandex.div.core.view2.DivVisibilityActionDispatcher;
import com.yandex.div.core.view2.DivVisibilityActionTracker;
import com.yandex.div.core.view2.ViewVisibilityCalculator;
import com.yandex.div.core.view2.divs.DivActionBinder;
import com.yandex.div.histogram.reporter.HistogramReporter;
import dagger.BindsInstance;
import dagger.Subcomponent;
import javax.inject.Named;

/**
 * Context scoped component for div2 {@link com.yandex.div.core.Div2Context}
 */
@DivScope
@Subcomponent(modules = {Div2Module.class, DivConfiguration.class, DivHistogramsModule.class})
public interface Div2Component {

    @NonNull
    Div2Builder getDiv2Builder();

    @NonNull
    DivBinder getDivBinder();

    @NonNull
    DivImagePreloader getImagePreLoader();

    @NonNull
    DivPreloader getPreLoader();

    @NonNull
    Div2Logger getDiv2Logger();

    @NonNull
    DivVisibilityActionTracker getVisibilityActionTracker();

    @NonNull
    DivVisibilityActionDispatcher getVisibilityActionDispatcher();

    @NonNull
    DivActionBinder getActionBinder();

    @NonNull
    TemporaryDivStateCache getTemporaryDivStateCache();

    @NonNull
    ViewVisibilityCalculator getViewVisibilityCalculator();

    @NonNull
    DivCustomContainerChildFactory getDivCustomContainerChildFactory();

    /**
     * @deprecated use {@link #getDivCustomViewAdapter}
     */
    @NonNull
    @Deprecated
    DivCustomViewFactory getDivCustomViewFactory();

    @Nullable
    DivCustomViewAdapter getDivCustomViewAdapter();

    @NonNull
    DivDataChangeListener getDivDataChangeListener();

    @NonNull
    ExpressionsRuntimeProvider getExpressionsRuntimeProvider();

    @NonNull
    DivStateManager getStateManager();

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
    GlobalVariableController getGlobalVariableController();

    @NonNull
    HistogramReporter getHistogramReporter();

    @NonNull
    DivCreationTracker getDivCreationTracker();

    /**
     * Builder for Div2Component
     */
    @Subcomponent.Builder
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
        Div2Component build();
    }
}
