package com.yandex.div.legacy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yandex.alicekit.core.annotations.PublicApi;
import com.yandex.alicekit.core.experiments.ExperimentConfig;
import com.yandex.div.core.images.DivImageLoader;
import com.yandex.div.legacy.state.LegacyDivStateCache;
import com.yandex.div.legacy.state.LegacyInMemoryDivStateCache;
import dagger.Module;
import dagger.Provides;

/**
 * Holds DivView configuration.
 * Create instance using {@link Builder} class.
 */
@PublicApi
@Module
public class DivLegacyConfiguration {

    @NonNull
    private final DivAutoLogger mAutoLogger;
    @NonNull
    private final DivImageLoader mImageLoader;
    @NonNull
    private final LegacyDivActionHandler mActionHandler;
    @NonNull
    private final ExperimentConfig mExperimentConfig;
    @NonNull
    private final DivLogger mDivLogger;
    @NonNull
    private final LegacyDivStateCache mDivStateCache;

    private DivLegacyConfiguration(
            @NonNull DivAutoLogger autoLogger,
            @NonNull DivImageLoader imageLoader,
            @NonNull LegacyDivActionHandler actionHandler,
            @NonNull ExperimentConfig experimentConfig,
            @NonNull DivLogger divLogger,
            @NonNull LegacyDivStateCache divStateCache
    ) {
        mAutoLogger = autoLogger;
        mImageLoader = imageLoader;
        mActionHandler = actionHandler;
        mExperimentConfig = experimentConfig;
        mDivLogger = divLogger;
        mDivStateCache = divStateCache;
    }

    @Provides
    @NonNull
    public LegacyDivActionHandler getActionHandler() {
        return mActionHandler;
    }

    @Provides
    @NonNull
    public DivAutoLogger getAutoLogger() {
        return mAutoLogger;
    }

    @Provides
    @NonNull
    public DivImageLoader getImageLoader() {
        return mImageLoader;
    }

    @Provides
    @NonNull
    public ExperimentConfig getExperimentConfig() {
        return mExperimentConfig;
    }

    @Provides
    @NonNull
    public DivLogger getDivLogger() {
        return mDivLogger;
    }

    @Provides
    @NonNull
    public LegacyDivStateCache getDivStateCache() {
        return mDivStateCache;
    }

    public static class Builder {

        @NonNull
        private final DivImageLoader mImageLoader;
        @Nullable
        private DivAutoLogger mAutoLogger;
        @Nullable
        private LegacyDivActionHandler mActionHandler;
        @Nullable
        private ExperimentConfig mExperimentConfig;
        @Nullable
        private DivLogger mDivLogger;
        @Nullable
        private LegacyDivStateCache mDivStateCache;

        public Builder(@NonNull DivImageLoader imageLoader) {
            mImageLoader = imageLoader;
        }

        @NonNull
        public Builder autoLogger(@NonNull DivAutoLogger autoLogger) {
            mAutoLogger = autoLogger;
            return this;
        }

        @NonNull
        public Builder actionHandler(@NonNull LegacyDivActionHandler actionHandler) {
            mActionHandler = actionHandler;
            return this;
        }

        @NonNull
        public Builder experimentConfig(@NonNull ExperimentConfig experimentConfig) {
            mExperimentConfig = experimentConfig;
            return this;
        }

        @NonNull
        public Builder divLogger(@NonNull DivLogger logger) {
            mDivLogger = logger;
            return this;
        }

        @NonNull
        public Builder divStateCache(@NonNull LegacyDivStateCache divStateCache) {
            mDivStateCache = divStateCache;
            return this;
        }

        @NonNull
        public DivLegacyConfiguration build() {
            return new DivLegacyConfiguration(
                    mAutoLogger == null ? DivAutoLogger.DEFAULT : mAutoLogger,
                    mImageLoader,
                    mActionHandler == null ? new LegacyDivActionHandler() : mActionHandler,
                    mExperimentConfig == null ? new ExperimentConfig() : mExperimentConfig,
                    mDivLogger == null ? DivLogger.STUB : mDivLogger,
                    mDivStateCache == null ? new LegacyInMemoryDivStateCache() : mDivStateCache
            );
        }
    }
}
