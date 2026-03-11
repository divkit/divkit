import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN
import com.yandex.div.gradle.aar.UnpackedStubAarTask
import com.yandex.div.gradle.aar.ZipStubAarTask
import com.yandex.div.gradle.multiplatform.PlatformIdentifier
import com.yandex.div.gradle.multiplatform.configureDefaultKmpDependencies
import java.lang.Boolean.parseBoolean

plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.kotlin.multiplatform)
    id("maven-publish")
}

apply(from = "../kmp-library.gradle")
apply(from = "../publish-kmp.gradle")

kotlin {
    androidLibrary {
        namespace = "com.yandex.div.assertion"
    }
}

buildkonfig {
    packageName = "com.yandex.div.internal"

    defaultConfigs {
        buildConfigField(BOOLEAN, "DISABLE_ASSERTS", "${providers.gradleProperty("disableAssertsInBuild").map(::parseBoolean).get()}")
    }
}

afterEvaluate {
    publishing {
        publications {
            withType<MavenPublication>().configureEach {
                if (name == "kotlinMultiplatform") {
                    val buildDir = project.layout.buildDirectory
                    val minSdk = rootProject.ext["minSdkVersion"] as Int

                    val groupNamespace = project.group.toString().replace(':', '.')
                    val moduleNamespace = project.name.replace('-', '.')
                    val stubNamespace = "$groupNamespace.$moduleNamespace.anchor"

                    val unpackedStubAarTask = tasks.register<UnpackedStubAarTask>("unpackedStubAar") {
                        aarPackage.set(stubNamespace)
                        minSdkVersion.set(minSdk)
                        outputDir.set(buildDir.dir("intermediates/stub-aar"))
                    }
                    val stubAarTask = tasks.register<ZipStubAarTask>("stubAar") {
                        from(unpackedStubAarTask.flatMap { it.outputDir })
                        destinationDirectory.set(buildDir.dir("outputs"))
                        archiveExtension.set("aar")
                    }
                    artifact(stubAarTask)

                    pom {
                        packaging = "aar"
                        withXml {
                            configureDefaultKmpDependencies(this@withXml, PlatformIdentifier.ANDROID)
                        }
                    }
                }
            }
        }
    }
}
