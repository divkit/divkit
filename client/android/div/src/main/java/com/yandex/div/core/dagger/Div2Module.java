package com.yandex.div.core.dagger;

import android.content.Context;
import android.view.ContextThemeWrapper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yandex.div.R;
import com.yandex.div.core.experiments.Experiment;
import com.yandex.div.core.resources.ContextThemeWrapperWithResourceCache;
import com.yandex.div.core.view.tabs.TabTextStyleProvider;
import com.yandex.div.font.DivTypefaceProvider;
import com.yandex.div.view.pooling.AdvanceViewPool;
import com.yandex.div.view.pooling.PseudoViewPool;
import com.yandex.div.view.pooling.ViewCreator;
import com.yandex.div.view.pooling.ViewPool;
import com.yandex.div.view.pooling.ViewPoolProfiler;
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
                                        @ExperimentFlag(experiment = Experiment.RESOURCE_CACHE_ENABLED) boolean resourceCacheEnabled) {
        return resourceCacheEnabled
                ? new ContextThemeWrapperWithResourceCache(baseContext, R.style.Div_Theme)
                : new ContextThemeWrapper(baseContext, R.style.Div_Theme);
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
}
