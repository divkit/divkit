package com.yandex.div.legacy.dagger;

import android.view.ContextThemeWrapper;
import androidx.annotation.NonNull;
import com.yandex.div.core.images.DivImageLoader;
import com.yandex.div.legacy.DivAutoLogger;
import com.yandex.div.legacy.DivLegacyConfiguration;
import com.yandex.div.legacy.DivLogger;
import com.yandex.div.legacy.DivTextStyleProvider;
import com.yandex.div.legacy.DivViewBuilder;
import com.yandex.div.legacy.LegacyDivActionHandler;
import com.yandex.div.legacy.state.LegacyDivStateManager;
import dagger.BindsInstance;
import dagger.Component;

@DivLegacyScope
@Component(modules = {DivModule.class, DivLegacyConfiguration.class})
public interface DivComponent {

    @NonNull
    DivImageLoader getImageLoader();

    @NonNull
    DivTextStyleProvider getTextStyleProvider();

    @NonNull
    LegacyDivActionHandler getActionHandler();

    @NonNull
    DivLogger getLogger();

    @NonNull
    DivAutoLogger getAutoLogger();

    @NonNull
    LegacyDivStateManager getStateManager();

    @NonNull
    DivViewBuilder getViewBuilder();

    @Component.Builder
    interface Builder {

        @BindsInstance
        @NonNull
        Builder baseContext(@NonNull ContextThemeWrapper baseContext);

        @NonNull
        Builder configuration(@NonNull DivLegacyConfiguration configuration);

        @NonNull
        DivComponent build();
    }
}
