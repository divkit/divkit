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
