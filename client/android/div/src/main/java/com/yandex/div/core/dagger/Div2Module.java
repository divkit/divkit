package com.yandex.div.core.dagger;

import android.content.Context;
import android.renderscript.RenderScript;
import android.view.ContextThemeWrapper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;

import com.yandex.div.core.DivCustomContainerViewAdapter;
import com.yandex.div.core.DivCustomViewAdapter;
import com.yandex.div.core.DivPreloader;
import com.yandex.div.core.experiments.Experiment;
import com.yandex.div.core.extension.DivExtensionController;
import com.yandex.div.core.font.DivTypefaceProvider;
import com.yandex.div.core.resources.ContextThemeWrapperWithResourceCache;
import com.yandex.div.internal.widget.tabs.TabTextStyleProvider;
import com.yandex.div.core.view2.DivImagePreloader;
import com.yandex.div.internal.viewpool.AdvanceViewPool;
import com.yandex.div.internal.viewpool.PseudoViewPool;
import com.yandex.div.internal.viewpool.ViewCreator;
import com.yandex.div.internal.viewpool.ViewPool;
import com.yandex.div.internal.viewpool.ViewPoolProfiler;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;

import javax.inject.Named;

@Module
abstract class Div2Module {

    @Binds
    @Named(Names.CONTEXT)
    @NonNull
    abstract Context bindContext(@NonNull ContextThemeWrapper baseContext);

    @Provides
    @Named(Names.THEMED_CONTEXT)
    @DivScope
    @NonNull
    static Context provideThemedContext(@NonNull ContextThemeWrapper baseContext,
                                        @Named(Names.THEME) @StyleRes int themeId,
                                        @ExperimentFlag(experiment = Experiment.RESOURCE_CACHE_ENABLED) boolean resourceCacheEnabled) {
        return resourceCacheEnabled
                ? new ContextThemeWrapperWithResourceCache(baseContext, themeId)
                : new ContextThemeWrapper(baseContext, themeId);
    }

    @Provides
    @DivScope
    @NonNull
    static ViewPool provideViewPool(@ExperimentFlag(experiment = Experiment.VIEW_POOL_ENABLED) boolean viewPoolEnabled,
                                    @Nullable ViewPoolProfiler profiler,
                                    @NonNull ViewCreator viewCreator) {
        return viewPoolEnabled ? new AdvanceViewPool(profiler, viewCreator) : new PseudoViewPool();
    }

    @Provides
    @DivScope
    @NonNull
    static TabTextStyleProvider provideTabTextStyleProvider(@NonNull DivTypefaceProvider typefaceProvider) {
        return new TabTextStyleProvider(typefaceProvider);
    }

    @Provides
    @DivScope
    @Nullable
    static ViewPoolProfiler provideViewPoolProfiler(
            @ExperimentFlag(experiment = Experiment.VIEW_POOL_PROFILING_ENABLED) boolean profilingEnabled,
            @NonNull ViewPoolProfiler.Reporter reporter
    ) {
        return profilingEnabled ? new ViewPoolProfiler(reporter) : null;
    }

    @Provides
    @DivScope
    @NonNull
    static RenderScript provideRenderScript(@NonNull @Named(Names.CONTEXT) Context context) {
        return RenderScript.create(context);
    }

    @Provides
    @DivScope
    @NonNull
    static DivPreloader provideDivPreloader(
            @NonNull DivImagePreloader imagePreloader,
            @Nullable DivCustomViewAdapter customViewAdapter,
            @Nullable DivCustomContainerViewAdapter customContainerViewAdapter,
            @NonNull DivExtensionController extensionController
    ) {
        return new DivPreloader(imagePreloader, customViewAdapter, customContainerViewAdapter, extensionController);
    }
}
