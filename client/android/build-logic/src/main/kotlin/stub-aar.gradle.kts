package divkit.convention

import com.yandex.div.gradle.aar.UnpackedStubAarTask
import com.yandex.div.gradle.aar.ZipStubAarTask
import com.yandex.div.gradle.multiplatform.PlatformIdentifier
import com.yandex.div.gradle.multiplatform.configureDefaultKmpDependencies

plugins {
    `maven-publish`
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
val minSdk = libs.findVersion("android-minSdk").get().requiredVersion.toInt()

val unpackedStubAarTask = tasks.register<UnpackedStubAarTask>("unpackedStubAar") {
    description = ""
    aarPackage.set(provider {
        val groupNamespace = project.group.toString().replace(':', '.')
        val moduleNamespace = project.name.replace('-', '.')
        "$groupNamespace.$moduleNamespace.anchor"
    })
    minSdkVersion.set(minSdk)
    outputDir.set(layout.buildDirectory.dir("intermediates/stub-aar"))
}

val stubAarTask = tasks.register<ZipStubAarTask>("stubAar") {
    description = ""
    from(unpackedStubAarTask.flatMap { it.outputDir })
    destinationDirectory.set(layout.buildDirectory.dir("outputs"))
    archiveExtension.set("aar")
}

afterEvaluate {
    publishing {
        publications.withType<MavenPublication>().configureEach {
            if (name == "kotlinMultiplatform") {
                artifact(stubAarTask)
                pom {
                    packaging = "aar"
                    withXml {
                        configureDefaultKmpDependencies(this, PlatformIdentifier.ANDROID)
                    }
                }
            }
        }
    }
}
