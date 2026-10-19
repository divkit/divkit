package com.yandex.divkit.generator

import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.domainObjectContainer
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class ApiGeneratorPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val schemasContainer = project.objects.domainObjectContainer(ApiGeneratorSchema::class)
        project.extensions.add("schemas", schemasContainer)

        val umbrellaTask = project.tasks.register("generateModel")
        schemasContainer.configureEach {
            val schemaTask = project.tasks.register<ApiGeneratorTask>("scheme_${name}") {
                workingDirectory.set(project.rootProject.file("../../api_generator"))
                configPath.set(config)
                schemasDirectory.set(schemas)
                outputDirectory.convention(project.layout.buildDirectory.dir("generated/kotlin/scheme_${name}"))
            }
            addGeneratedSources(project, schemaTask)
            umbrellaTask.configure {
                dependsOn(schemaTask)
            }
        }
    }

    private fun addGeneratedSources(project: Project, schemaTask: TaskProvider<ApiGeneratorTask>) {
        if (addGeneratedSourcesToKmpProject(project, schemaTask)) {
            return
        }

        addGeneratedSourcesToAndroidProject(project, schemaTask)
    }

    private fun addGeneratedSourcesToKmpProject(
        project: Project,
        schemaTask: TaskProvider<ApiGeneratorTask>
    ): Boolean {
        val kotlin = project.extensions.findByType<KotlinMultiplatformExtension>() ?: return false
        kotlin.sourceSets.named("androidMain") {
            this.kotlin.srcDir(schemaTask.flatMap { it.outputDirectory })
        }
        return true
    }

    private fun addGeneratedSourcesToAndroidProject(
        project: Project,
        schemaTask: TaskProvider<ApiGeneratorTask>
    ): Boolean {
        val androidComponents = project.extensions.getByName<AndroidComponentsExtension<*, *, *>>("androidComponents")
        androidComponents.onVariants { variant ->
            val javaSources = checkNotNull(variant.sources.java) {
                "Java sources are missing in variant ${variant.name}"
            }
            javaSources.addGeneratedSourceDirectory(
                schemaTask,
                ApiGeneratorTask::outputDirectory,
            )
        }
        return true
    }
}
