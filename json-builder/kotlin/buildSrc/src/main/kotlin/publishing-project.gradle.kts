package divkit.convention

import com.yandex.divkit.gradle.applyProperties
import com.yandex.divkit.gradle.optStringProperty
import java.net.URI

plugins {
    id("io.github.gradle-nexus.publish-plugin")
    id("divkit.convention.version")
}

applyProperties(from = "$rootDir/bucket.properties")

val divkitVersionSuffixed: String by extra
val sonatypeUsername: String? by extra(optStringProperty("sonatypeUsername") ?: System.getenv("SONATYPE_USERNAME"))
val sonatypePassword: String? by extra(optStringProperty("sonatypePassword") ?: System.getenv("SONATYPE_PASSWORD"))
val publishToMavenCentral: Boolean by extra(sonatypeUsername != null && sonatypePassword != null)
val bucketReleasesUrl: URI by extra(URI.create(optStringProperty("bucketReleasesUrl").orEmpty()))
val bucketSnapshotsUrl: URI by extra(URI.create(optStringProperty("bucketSnapshotsUrl").orEmpty()))
val bucketUsername: String? by extra(optStringProperty("bucketUsername") ?: System.getenv("BUCKET_USERNAME"))
val bucketPassword: String? by extra(optStringProperty("bucketPassword") ?: System.getenv("BUCKET_PASSWORD"))
val publishToBucket: Boolean by extra(bucketUsername != null && bucketPassword != null)

group = "com.yandex.divkit.kotlin-json-builder"
version = divkitVersionSuffixed

if (publishToMavenCentral) {
    nexusPublishing {
        repositories {
            sonatype {
                stagingProfileId.set("14c2d9e18a30b")
                nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
                snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
                username.set(sonatypeUsername)
                password.set(sonatypePassword)
            }
        }
    }
}

tasks.register("publishInternalRelease")

tasks.register("publishPublicRelease")

if (publishToMavenCentral) {
    tasks.named("publishPublicRelease") {
        dependsOn(tasks.named("closeAndReleaseSonatypeStagingRepository"))
    }
}
