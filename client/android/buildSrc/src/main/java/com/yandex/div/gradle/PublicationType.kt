package com.yandex.div.gradle

import org.gradle.api.Project
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val VERSION_DATE_FORMAT = SimpleDateFormat("yyyyMMdd.HHmmss", Locale.US)

@Suppress("EnumEntryName")
enum class PublicationType {

    snapshot {
        override fun getVersionSuffix() = "-${VERSION_DATE_FORMAT.format(Date())}-SNAPSHOT"
        override fun configureForProject(project: Project) {
            project.tasks.withType(AbstractPublishToMaven::class.java).configureEach { task ->
                task.notCompatibleWithConfigurationCache("Snapshot publication type is not compatible " +
                        "with configuration cache as it uses build start timestamp")
            }
        }
    },

    nightly {
        override fun getVersionSuffix() = "-${VERSION_DATE_FORMAT.format(Date())}-NIGHTLY"
        override fun configureForProject(project: Project) {
            project.tasks.withType(AbstractPublishToMaven::class.java).configureEach { task ->
                task.notCompatibleWithConfigurationCache("Snapshot publication type is not compatible " +
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
                return snapshot
            } catch (ignored: NullPointerException) {
                return snapshot
            }
        }
    }
}
