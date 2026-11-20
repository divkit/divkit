package com.yandex.divkit.generator

import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.domainObjectContainer
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.named
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
            if (project.extensions.findByType<KotlinMultiplatformExtension>() == null) {
                addGeneratedSourcesToAndroidProject(project, schemaTask)
            }
            umbrellaTask.configure {
                dependsOn(schemaTask)
            }
        }

        // Schema source sets are known only after the build script configures the schemas.
        project.afterEvaluate {
            addGeneratedSourcesToKmpProject(project, schemasContainer)
        }
    }

    private fun addGeneratedSourcesToKmpProject(project: Project, schemas: Iterable<ApiGeneratorSchema>) {
        val kotlin = project.extensions.findByType<KotlinMultiplatformExtension>() ?: return
        schemas.forEach { schema ->
            val schemaTask = project.tasks.named<ApiGeneratorTask>("scheme_${schema.name}")
            kotlin.sourceSets.named(schema.sourceSet.get()) {
                this.kotlin.srcDir(schemaTask.flatMap { it.outputDirectory })
            }
        }
    }

    private fun addGeneratedSourcesToAndroidProject(
        project: Project,
        schemaTask: TaskProvider<ApiGeneratorTask>
    ) {
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
    }
}
