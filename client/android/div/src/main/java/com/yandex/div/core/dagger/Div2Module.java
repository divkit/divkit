package com.yandex.div.core.dagger;

import android.content.Context;
import android.os.Build;
import android.renderscript.RenderScript;
import android.view.ContextThemeWrapper;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;

import com.yandex.div.core.DivCustomContainerViewAdapter;
import com.yandex.div.core.DivCustomViewAdapter;
import com.yandex.div.core.DivPreloader;
import com.yandex.div.core.experiments.Experiment;
import com.yandex.div.core.extension.DivExtensionController;
import com.yandex.div.core.font.DivTypefaceProvider;
import com.yandex.div.core.resources.ContextThemeWrapperWithResourceCache;
import com.yandex.div.internal.viewpool.AdvanceViewPool;
import com.yandex.div.internal.widget.tabs.TabTextStyleProvider;
import com.yandex.div.core.view2.DivImagePreloader;
import com.yandex.div.internal.viewpool.PseudoViewPool;
import com.yandex.div.internal.viewpool.ViewCreator;
import com.yandex.div.internal.viewpool.ViewPool;
import com.yandex.div.internal.viewpool.ViewPoolProfiler;
import com.yandex.div.internal.viewpool.optimization.PerformanceDependentSessionProfiler;
import com.yandex.yatagan.Binds;
import com.yandex.yatagan.Module;
import com.yandex.yatagan.Provides;

import javax.inject.Named;

@Module
abstract public class Div2Module {

    @Binds
    @Named(Names.CONTEXT)
    @NonNull
    public abstract Context bindContext(@NonNull ContextThemeWrapper baseContext);

    @Provides
    @Named(Names.THEMED_CONTEXT)
    @DivScope
    @NonNull
    public static Context provideThemedContext(@NonNull ContextThemeWrapper baseContext,
                                        @Named(Names.THEME) @StyleRes int themeId,
                                        @ExperimentFlag(experiment = Experiment.RESOURCE_CACHE_ENABLED) boolean resourceCacheEnabled) {
        return resourceCacheEnabled
                ? new ContextThemeWrapperWithResourceCache(baseContext, themeId)
                : new ContextThemeWrapper(baseContext, themeId);
    }

    @Provides
    @DivScope
    @NonNull
    public static ViewPool provideViewPool(@ExperimentFlag(experiment = Experiment.VIEW_POOL_ENABLED) boolean viewPoolEnabled,
                                    @NonNull ExternalOptional<ViewPoolProfiler> profiler,
                                    @NonNull PerformanceDependentSessionProfiler sessionProfiler,
                                    @NonNull ViewCreator viewCreator
    ) {
        return viewPoolEnabled
                ? new AdvanceViewPool(profiler.getOptional().orNull(), sessionProfiler, viewCreator)
                : new PseudoViewPool();
    }

    @Provides
    @DivScope
    @NonNull
    public static TabTextStyleProvider provideTabTextStyleProvider(@NonNull DivTypefaceProvider typefaceProvider) {
        return new TabTextStyleProvider(typefaceProvider);
    }

    @Provides
    @DivScope
    @NonNull
    public static ExternalOptional<ViewPoolProfiler> provideViewPoolProfiler(
            @ExperimentFlag(experiment = Experiment.VIEW_POOL_PROFILING_ENABLED) boolean profilingEnabled,
            @NonNull ViewPoolProfiler.Reporter reporter
    ) {
        return profilingEnabled
                ? ExternalOptional.of(new ViewPoolProfiler(reporter))
                : ExternalOptional.empty();
    }

    @Provides
    @DivScope
    @NonNull
    public static RenderScript provideRenderScript(@NonNull @Named(Names.CONTEXT) Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return RenderScript.create(context);
        }
        return RenderScript.createMultiContext(context, RenderScript.ContextType.NORMAL,
                RenderScript.CREATE_FLAG_NONE, context.getApplicationInfo().targetSdkVersion);
    }

    @Provides
    @DivScope
    @NonNull
    public static DivPreloader provideDivPreloader(
            @NonNull DivImagePreloader imagePreloader,
            @NonNull DivCustomViewAdapter customViewAdapter,
            @NonNull DivCustomContainerViewAdapter customContainerViewAdapter,
            @NonNull DivExtensionController extensionController
    ) {
        return new DivPreloader(imagePreloader, customViewAdapter, customContainerViewAdapter, extensionController);
    }
}
