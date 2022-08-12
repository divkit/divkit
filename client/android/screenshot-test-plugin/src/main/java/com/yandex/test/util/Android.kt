package com.yandex.test.util

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.DomainObjectSet
import org.gradle.api.GradleException
import org.gradle.api.Project

internal typealias AndroidExtension = BaseExtension

internal val Project.android: AndroidExtension
    get() {
        val androidExtension = extensions.findByType(AndroidExtension::class.java)
        return androidExtension ?: throw GradleException(ERROR_ANDROID_PLUGIN_NOT_FOUND)
    }

internal val Project.androidComponents: AndroidComponentsExtension<*, *, *>
    get() {
        val androidComponentsExtension = extensions.findByType(AndroidComponentsExtension::class.java)
        return androidComponentsExtension ?: throw GradleException(ERROR_ANDROID_PLUGIN_NOT_FOUND)
    }

internal val AndroidExtension.variants: DomainObjectSet<out BaseVariant>
    get() = when (this) {
        is AppExtension -> applicationVariants
        is LibraryExtension -> libraryVariants
        else -> throw GradleException(ERROR_UNKNOWN_EXTENSION_TYPE)
}

private const val ERROR_ANDROID_PLUGIN_NOT_FOUND = "'com.android.application' or 'com.android.library' plugin is required."
private const val ERROR_UNKNOWN_EXTENSION_TYPE = "Unknown Android plugin extension type."
