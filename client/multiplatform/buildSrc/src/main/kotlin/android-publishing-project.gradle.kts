package divkit.convention

import com.yandex.divkit.gradle.applyProperties
import com.yandex.divkit.gradle.optProperty
import java.net.URI

plugins {
    id("io.github.gradle-nexus.publish-plugin")
    id("divkit.convention.version")
}

applyProperties(from = "$rootDir/bucket.properties")

val sonatypeUsername: String? by extra(optProperty<String>("sonatypeUsername") ?: System.getenv("SONATYPE_USERNAME"))
val sonatypePassword: String? by extra(optProperty<String>("sonatypePassword") ?: System.getenv("SONATYPE_PASSWORD"))
val publishToMavenCentral: Boolean by extra(sonatypeUsername != null && sonatypePassword != null)
val bucketReleasesUrl: URI by extra(URI.create(optProperty<String>("bucketReleasesUrl").orEmpty()))
val bucketSnapshotsUrl: URI by extra(URI.create(optProperty<String>("bucketSnapshotsUrl").orEmpty()))
val bucketUsername: String? by extra(optProperty<String>("bucketUsername") ?: System.getenv("BUCKET_USERNAME"))
val bucketPassword: String? by extra(optProperty<String>("bucketPassword") ?: System.getenv("BUCKET_PASSWORD"))
val publishToBucket: Boolean by extra(bucketUsername != null && bucketPassword != null)

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
