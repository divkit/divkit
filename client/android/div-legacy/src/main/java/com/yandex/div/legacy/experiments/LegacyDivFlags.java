package com.yandex.div.legacy.experiments;

import androidx.annotation.NonNull;
import com.yandex.alicekit.core.annotations.PublicApi;
import com.yandex.alicekit.core.experiments.BooleanFlag;
import com.yandex.alicekit.core.experiments.ExperimentFlag;
import java.util.Arrays;
import java.util.Collection;

/**
 * A set of experiment flags that used by divs.
 * To create a new flag you should instantiate a constant in this class and add to the {@link #EXPERIMENT_FLAGS} list.
 */
@PublicApi
public class LegacyDivFlags {

    private LegacyDivFlags() { /* prevent initialization */ }

    /**
     * Enables div view pool.
     * Default value is {@code false}.
     */
    public static final BooleanFlag VIEW_POOL_ENABLED = new BooleanFlag("Div.ViewPool.enabled", true);
    public static final BooleanFlag VIEW_POOL_PROFILING_ENABLED =
            new BooleanFlag("Div.ViewPool.profilingEnabled", false);

    private static final ExperimentFlag<?>[] EXPERIMENT_FLAGS = {
            VIEW_POOL_ENABLED,
            VIEW_POOL_PROFILING_ENABLED
    };

    /**
     * @return All experiment flags related to divs
     */
    @NonNull
    public static Collection<ExperimentFlag<?>> getAllFlags() {
        return Arrays.asList(EXPERIMENT_FLAGS);
    }
}
