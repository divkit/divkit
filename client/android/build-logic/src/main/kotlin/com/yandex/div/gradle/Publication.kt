package com.yandex.div.gradle

import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withGroovyBuilder
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.signing.SigningExtension

// TODO(gulevsky): remove withGroovyBuilder once com.yandex.div.gradle.Version class becomes available.
fun Project.releaseLibraryVersion(): String {
    val divkitVersion = requireNotNull(rootProject.extra["divkitVersion"])
    return divkitVersion.withGroovyBuilder {
        getProperty("releaseLibraryVersion") as String
    }
}

fun Project.mavenPublication() {
    group = "com.yandex.div"

    val publicationType = providers.gradleProperty("publicationType").orNull
    if (publicationType != "release") {
        tasks.withType<AbstractPublishToMaven>().configureEach {
            notCompatibleWithConfigurationCache(
                "Snapshot and nightly publication types use the build start timestamp"
            )
        }
    }

    val releaseVersion = releaseLibraryVersion()
    version = releaseVersion
    val publishToMavenCentral = rootProject.findProperty("publishToMavenCentral") == true &&
        !projectDir.path.contains("internal/android/libs")
    val publishToBucket = rootProject.findProperty("publishToBucket") == true
    val publishing = extensions.getByType<PublishingExtension>()

    publishing.publications.withType<MavenPublication>().configureEach {
        version = releaseVersion
        pom {
            name.set("DivKit")
            description.set("DivKit is an open source Server-Driven UI (SDUI) framework. SDUI is a an emerging technique that leverage the server to build the user interfaces of their mobile app.")
            url.set("http://divkit.tech/")

            licenses {
                license {
                    name.set("Apache License, version 2.0")
                    url.set("http://www.apache.org/licenses/LICENSE-2.0")
                    distribution.set("repo")
                }
            }

            scm {
                connection.set("scm:git://github.com/divkit/divkit.git")
                developerConnection.set("scm:git://github.com/divkit/divkit.git")
                url.set("https://github.com/divkit/divkit.git")
            }

            developers {
                developer {
                    name.set("Yandex")
                    url.set("http://divkit.tech/")
                }
            }
        }
        (this as ExtensionAware).extra["repo"] = "release"
    }

    if (publishToBucket) {
        publishing.repositories.maven {
            name = "bucket"
            credentials {
                username = rootProject.findProperty("bucketUsername") as String?
                password = rootProject.findProperty("bucketPassword") as String?
            }
            url = uri(if (publicationType == "release") {
                "https://bucket.yandex-team.ru/v1/maven/yandex_mobile_releases/"
            } else {
                "https://bucket.yandex-team.ru/v1/maven/yandex_mobile_snapshots/"
            })
        }
    }

    if (publishToMavenCentral) {
        extra["signing.keyId"] = providers.gradleProperty("signingKeyId").orNull
        extra["signing.password"] = providers.gradleProperty("signingPassword").orNull
        extra["signing.secretKeyRingFile"] = providers.gradleProperty("signingSecretKeyRingFile").orNull
        extensions.configure<SigningExtension> {
            sign(publishing.publications)
            sign(configurations.getByName("archives"))
        }
    }
}
