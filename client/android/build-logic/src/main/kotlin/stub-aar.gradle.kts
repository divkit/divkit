package divkit.convention

import com.yandex.div.gradle.aar.UnpackedStubAarTask
import com.yandex.div.gradle.aar.ZipStubAarTask
import com.yandex.div.gradle.multiplatform.PlatformIdentifier
import com.yandex.div.gradle.multiplatform.configureDefaultKmpDependencies
import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    `maven-publish`
}

val libs = the<LibrariesForLibs>()
val minSdk = libs.versions.android.minSdk.map(String::toInt)

val unpackedStubAarTask = tasks.register<UnpackedStubAarTask>("unpackedStubAar") {
    description = "Generates a stub AAR manifest with the module package name and minimum Android SDK version."
    aarPackage.set(provider {
        val groupNamespace = project.group.toString().replace(':', '.')
        val moduleNamespace = project.name.replace('-', '.')
        "$groupNamespace.$moduleNamespace.anchor"
    })
    minSdkVersion.set(minSdk)
    outputDir.set(layout.buildDirectory.dir("intermediates/stub-aar"))
}

val stubAarTask = tasks.register<ZipStubAarTask>("stubAar") {
    description = "Packages the stub AAR for the Kotlin Multiplatform publication."
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
