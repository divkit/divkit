package com.yandex.div.gradle

import org.gradle.api.Project
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import java.text.SimpleDateFormat
import java.util.Date

private val VERSION_DATE_FORMAT = SimpleDateFormat("yyyyMMdd.HHmmss")

enum class PublicationType {

    dev {
        override fun getVersionSuffix() = "-dev.${VERSION_DATE_FORMAT.format(Date())}"
        override fun configureForProject(project: Project) {
            project.tasks.withType(AbstractPublishToMaven::class.java).configureEach { task ->
                task.notCompatibleWithConfigurationCache("publicationType dev is not compatible " +
                        "with configuration cache as it uses build start timestamp")
            }
        }
    },

    release {
        override fun getVersionSuffix() = ""
        override fun configureForProject(project: Project) = Unit
    };

    abstract fun getVersionSuffix(): String

    abstract fun configureForProject(project: Project)

    companion object {

        @JvmStatic
        fun fromString(string: String?): PublicationType {
            return try {
                valueOf(string!!)
            } catch (ignored: IllegalArgumentException) {
                return PublicationType.dev
            } catch (ignored: NullPointerException) {
                return PublicationType.dev
            }
        }
    }
}
