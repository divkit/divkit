package divkit.convention

import com.yandex.divkit.gradle.PublicationType
import com.yandex.divkit.gradle.optProperty
import com.yandex.divkit.gradle.sourceSets
import java.net.URI

plugins {
    signing
    id("maven-publish")
}

val publicationType: PublicationType by rootProject.extra
val divkitVersionSuffixed: String by rootProject.extra
val publishToMavenCentral: Boolean by rootProject.extra
val bucketReleasesUrl: URI by rootProject.extra
val bucketSnapshotsUrl: URI by rootProject.extra
val bucketUsername: String? by rootProject.extra
val bucketPassword: String? by rootProject.extra
val publishToBucket: Boolean by rootProject.extra

extra.apply {
    set("signing.keyId", rootProject.optProperty<String>("signing.keyId") ?: System.getenv("SIGNING_KEY_ID"))
    set("signing.password", rootProject.optProperty<String>("signing.password") ?: System.getenv("SIGNING_PASSWORD"))
    set("signing.secretKeyRingFile", rootProject.optProperty<String>("signing.secretKeyRingFile") ?: System.getenv("SIGNING_SECRET_KEY_RING_FILE"))
}

group = "com.yandex.divkit.json-builder"
version = divkitVersionSuffixed

if (publishToMavenCentral) {
    signing {
        afterEvaluate {
            sign(publishing.publications)
            sign(configurations.archives.get())
        }
    }
}

publishing {
    if (publishToBucket) {
        repositories {
            maven {
                name = "bucket"
                credentials {
                    username = bucketUsername
                    password = bucketPassword
                }
                url = if (publicationType.isRelease) {
                    bucketReleasesUrl
                } else {
                    bucketSnapshotsUrl
                }
            }
        }
    }

    publications {
        create<MavenPublication>("release") {
            version = divkitVersionSuffixed

            afterEvaluate {
                artifact(tasks.named<Jar>("sourcesJar"))
                artifact(tasks.named<Jar>("javadocJar"))
                from(components["java"])
            }

            pom {
                name = "DivKit"
                description = "DivKit is an open source Server-Driven UI (SDUI) framework. SDUI is a an emerging technique that leverage the server to build the user interfaces of their mobile app."
                url = "http://divkit.tech/"

                licenses {
                    license {
                        name = "Apache License, version 2.0"
                        url = "http://www.apache.org/licenses/LICENSE-2.0"
                        distribution = "repo"
                    }
                }

                scm {
                    connection = "scm:git://github.com/divkit/divkit.git"
                    developerConnection = "scm:git://github.com/divkit/divkit.git"
                    url = "https://github.com/divkit/divkit.git"
                }

                developers {
                    developer {
                        name = "Yandex"
                        url = "http://divkit.tech/"
                    }
                }
            }
        }
    }
}

tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

tasks.register<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")
    from(tasks.named("javadoc"))
}

tasks.register("reportVersion") {
    doLast {
        println("DivKit version $divkitVersionSuffixed")
    }
}

tasks.register("reportBuildNumber") {
    doLast {
        println("buildNumber $divkitVersionSuffixed")
    }
}

tasks.withType<PublishToMavenRepository> {
    finalizedBy(tasks.named("reportBuildNumber"))
}

tasks.withType<PublishToMavenLocal> {
    finalizedBy(tasks.named("reportVersion"))
}

rootProject.tasks.named("publishInternalRelease") {
    dependsOn(tasks.withType<PublishToMavenRepository>())
}

if (publishToMavenCentral) {
    rootProject.tasks.named("publishPublicRelease") {
        dependsOn(tasks.named("publishToSonatype"))
    }
}
