package divkit.convention

import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

val checkTaskName = "checkJvmApi"

val checkRequested = gradle.startParameter.taskNames.any {
    it.substringAfterLast(':') == checkTaskName
}

pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
    if (checkRequested) {
        val checkTarget = extensions.getByType<KotlinMultiplatformExtension>().linuxX64()
        tasks.named { taskName ->
            taskName.contains(checkTarget.name, ignoreCase = true) || taskName == "downloadKotlinNativeDistribution"
        }.configureEach { enabled = false }

        // Kotlin adds Native libraries while finalizing the DSL. This disabled target needs no
        // classpath; leaving it resolvable makes Metalava require the disabled Native download.
        gradle.projectsEvaluated {
            checkTarget.compilations.configureEach {
                compileDependencyFiles = files()
            }
        }
    }

    tasks.register(checkTaskName) {
        group = "verification"
        description = "Fails if common code uses JVM-only APIs."
        if (checkRequested) {
            dependsOn("compileCommonMainKotlinMetadata")
        } else {
            doLast { error("Request $checkTaskName by its full name, so that the check target is configured.") }
        }
    }
}
