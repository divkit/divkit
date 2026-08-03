package com.yandex.test.util

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.GradleException
import org.gradle.api.Project

internal val Project.android: ApplicationExtension
    get() {
        return extensions.findByType(ApplicationExtension::class.java)
            ?: throw GradleException(ERROR_ANDROID_PLUGIN_NOT_FOUND)
    }

internal val Project.androidComponents: AndroidComponentsExtension<*, *, *>
    get() {
        return extensions.findByType(AndroidComponentsExtension::class.java)
            ?: throw GradleException(ERROR_ANDROID_PLUGIN_NOT_FOUND)
    }

private const val ERROR_ANDROID_PLUGIN_NOT_FOUND = "'com.android.application' or 'com.android.library' plugin is required."
