package com.yandex.div.legacy.dagger;

import android.content.Context;
import android.view.ContextThemeWrapper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yandex.alicekit.core.experiments.ExperimentConfig;
import com.yandex.alicekit.core.metrica.MetricaUtils;
import com.yandex.alicekit.core.widget.TypefaceProvider;
import com.yandex.alicekit.core.widget.YandexSansTypefaceProvider;
import com.yandex.div.legacy.DivTextStyleProvider;
import com.yandex.div.legacy.R;
import com.yandex.div.legacy.experiments.LegacyDivFlags;
import com.yandex.div.legacy.view.DivLineHeightTextViewFactory;
import com.yandex.div.legacy.view.TextViewFactory;
import com.yandex.div.view.pooling.AdvanceViewPool;
import com.yandex.div.view.pooling.PseudoViewPool;
import com.yandex.div.view.pooling.ViewPool;
import com.yandex.div.view.pooling.ViewPoolProfiler;
import com.yandex.metrica.IReporterInternal;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import java.util.Map;
import javax.inject.Named;

@Module
abstract class DivModule {

    @Binds
    @Named(LegacyNames.CONTEXT)
    @NonNull
    abstract Context bindContext(@NonNull ContextThemeWrapper baseContext);

    @Provides
    @DivLegacyScope
    @NonNull
    static TypefaceProvider provideTypefaceProvider(@Named(LegacyNames.CONTEXT) @NonNull Context context) {
        return new YandexSansTypefaceProvider(context);
    }

    @Provides
    @Named(LegacyNames.THEMED_CONTEXT)
    @DivLegacyScope
    @NonNull
    static Context provideThemedContext(@NonNull ContextThemeWrapper baseContext) {
        return new ContextThemeWrapper(baseContext, R.style.Legacy_Theme);
    }

    @Provides
    @DivLegacyScope
    @NonNull
    static IReporterInternal provideMetricaReporter(@NonNull @Named(LegacyNames.CONTEXT) Context context) {
        return MetricaUtils.getReporter(context);
    }

    @Provides
    @DivLegacyScope
    @NonNull
    static ViewPool provideViewPool(@NonNull ExperimentConfig experimentConfig,
                                    @Nullable ViewPoolProfiler profiler) {
        boolean viewPoolEnabled = experimentConfig.getBooleanValue(LegacyDivFlags.VIEW_POOL_ENABLED);
        return viewPoolEnabled ? new AdvanceViewPool(profiler, ViewCreatorHolder.VIEW_CREATOR) : new PseudoViewPool();
    }

    @Provides
    @DivLegacyScope
    @Nullable
    static ViewPoolProfiler provideViewPoolProfiler(@NonNull ExperimentConfig experimentConfig,
                                                    @NonNull IReporterInternal reporter) {
        boolean profilingEnabled = experimentConfig.getBooleanValue(LegacyDivFlags.VIEW_POOL_PROFILING_ENABLED);
        return profilingEnabled ? new ViewPoolProfiler(
                (message, result) -> reporter.reportEvent(message, (Map<String, Object>) result)
        ) : null;
    }

    @Provides
    @DivLegacyScope
    @NonNull
    static DivTextStyleProvider provideTextStyleProvider(@NonNull TypefaceProvider typefaceProvider) {
        return new DivTextStyleProvider(typefaceProvider);
    }

    @Provides
    @DivLegacyScope
    @NonNull
    static TextViewFactory provideDivTextFactory() {
        return new DivLineHeightTextViewFactory();
    }
}
