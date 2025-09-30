package com.yandex.test.screenshot

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import javax.inject.Inject

open class ScreenshotTestPluginExtension @Inject constructor(
    objects: ObjectFactory,
) {

    val enabled: Property<Boolean> = objects.property(Boolean::class.java).convention(true)
    var enableComparison: Property<Boolean> = objects.property(Boolean::class.java).convention(true)
    val strictComparison: Property<Boolean> = objects.property(Boolean::class.java).convention(false)
    val testAnnotations: ListProperty<String> = objects.listProperty(String::class.java)
        .convention(emptyList())
    val hostDir: Property<String> = objects.property(String::class.java)
        .convention("screenshots")
    val referencesDir: Property<String> = objects.property(String::class.java)
        .convention("src/androidTest/resources/screenshots")
    val screenshotDir: Property<String> = objects.property(String::class.java)
        .convention("outputs/connected_android_test_additional_output/debugAndroidTest/connected")
    val collectedDir: Provider<String> = hostDir.map { "$it/collected" }
    val comparisonDir: Provider<String> = hostDir.map { "$it/comparison" }

    val comparableCategories: ListProperty<String> = objects.listProperty(String::class.java)
        .convention(listOf("viewRender", "viewPixelCopy"))

    companion object {
        const val NAME = "screenshotTests"
    }
}
