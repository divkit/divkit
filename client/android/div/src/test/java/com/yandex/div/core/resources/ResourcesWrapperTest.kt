package com.yandex.div.core.resources

import android.content.res.Resources
import com.yandex.div.core.util.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.lang.reflect.Method
import java.lang.reflect.Modifier

// These methods are either not part of Android public API (but still observable through reflection)
// or cannot be overridden.
val ANDROID_INTERNAL_OR_FINAL_RESOURCES_PUBLIC_METHODS = listOf(
    "calcConfigChanges(Configuration)",
    "finishPreloading()",
    "flushLayoutCache()",
    "getAnimatorCache()",
    "getAssets()",
    "getClassLoader()",
    "getCompatibilityInfo()",
    "getDisplayAdjustments()",
    "getDrawableInflater()",
    "getImpl()",
    "getPreloadedDrawables()",
    "getSizeConfigurations()",
    "getStateListAnimatorCache()",
    "loadComplexColor(TypedValue, int, Theme)",
    "newTheme()",
    "preloadFonts(int)",
    "setCompatibilityInfo(CompatibilityInfo)",
    "setImpl(ResourcesImpl)",
    "startPreloading()",
    "updateConfiguration(Configuration, DisplayMetrics, CompatibilityInfo)",
)

@RunWith(RobolectricTestRunner::class)
class ResourcesWrapperTest {
    @Test
    fun `all resources public methods are overridden in wrapper`() {
        val wrapperPublicMethods =
            ResourcesWrapper::class.java.getOverridablePublicMethodSignatures()
        val basePublicMethods = Resources::class.java.getOverridablePublicMethodSignatures() -
            ANDROID_INTERNAL_OR_FINAL_RESOURCES_PUBLIC_METHODS

        val diff = basePublicMethods - wrapperPublicMethods
        Assert.assertTrue("Following Resources methods are not overridden in ResourcesWrapper: " +
            diff.joinToString(), diff.isEmpty())
    }
}

fun <T> Class<T>.getOverridablePublicMethodSignatures() = declaredMethods
    .filter {
        Modifier.isPublic(it.modifiers) && !Modifier.isStatic(it.modifiers) && !it.isSynthetic
    }.map { it.signature() }

fun Method.signature() = "$name(${parameterTypes.joinToString { it.simpleName }})"
