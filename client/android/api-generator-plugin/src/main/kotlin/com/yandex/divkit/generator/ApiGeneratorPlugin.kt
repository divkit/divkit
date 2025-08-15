package com.yandex.divkit.generator

import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.domainObjectContainer
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.register

class ApiGeneratorPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val androidComponents = project.extensions.getByName<AndroidComponentsExtension<*, *, *>>("androidComponents")
        val schemasContainer = project.objects.domainObjectContainer(ApiGeneratorSchema::class)
        project.extensions.add("schemas", schemasContainer)

        val umbrellaTask = project.tasks.register("generateModel")
        schemasContainer.configureEach {
            val schemaTask = project.tasks.register<ApiGeneratorTask>("scheme_${name}") {
                workingDirectory.set(project.rootProject.file("../../api_generator"))
                configPath.set(config)
                schemasDirectory.set(schemas)
            }
            androidComponents.onVariants { variant ->
                val javaSources = checkNotNull(variant.sources.java){
                    "Java sources are missing in variant ${variant.name}"
                }
                javaSources.addGeneratedSourceDirectory(
                    schemaTask,
                    ApiGeneratorTask::outputDirectory,
                )
            }
            umbrellaTask.configure {
                dependsOn(schemaTask)
            }
        }
    }
}
