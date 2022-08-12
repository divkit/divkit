package com.yandex.div.test;

import com.google.auto.service.AutoService;
import java.util.Properties;
import javax.annotation.Priority;
import javax.inject.Inject;
import org.robolectric.annotation.Config;
import org.robolectric.pluginapi.config.GlobalConfigProvider;

/**
 * {@link GlobalConfigProvider} implementation. Provides default config for all Robolectric tests.
 * It's instantiated before any test runs.
 */
@AutoService(GlobalConfigProvider.class)
@Priority(Integer.MAX_VALUE)
@SuppressWarnings("unused")
public class DivKitGlobalConfigProvider implements GlobalConfigProvider {

    @Inject
    public DivKitGlobalConfigProvider(Properties systemProperties) {
        // Keep it synced with systemProperty in tests.gradle
        //
        // Android Studio 4.2 and earlier does not use Gradle to run tests so all the applied system properties by
        // android.testOptions.unitTests.all.systemProperty is ignored during test run. So we have to
        // declare property here to ensure that this property would be used for tests.
        // Can be removed in one of two cases:
        // 1. robolectric.looperMode=PAUSED is supported in all tests in project and it could be default.
        // 2. com.android.tools.build:gradle version is higher than 7.0. In this version all unit tests are run
        // via Gradle and this additional declaring is not needed anymore.
        systemProperties.setProperty("robolectric.looperMode", "LEGACY");
    }

    @Override
    public Config get() {
        return new Config.Builder().build();
    }
}
