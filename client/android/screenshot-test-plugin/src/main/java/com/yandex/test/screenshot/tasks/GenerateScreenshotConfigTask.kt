package com.yandex.test.screenshot.tasks

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.yandex.test.screenshot.ScreenshotTestPluginExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

@CacheableTask
open class GenerateScreenshotConfigTask : DefaultTask() {

    @get:OutputDirectory
    val outputDir = File(project.buildDir, "generated/source/screenshots")

    private val screenshots = project.extensions.getByType(ScreenshotTestPluginExtension::class.java)

    init {
        group = "other"
    }

    @TaskAction
    fun perform() {
        outputDir.apply {
            deleteRecursively()
        }
        generateConfig().writeTo(outputDir)
    }

    private fun generateConfig(): FileSpec {
        return FileSpec.builder(CONFIG_PACKAGE, CONFIG_CLASS_NAME)
            .addComment("Automatically generated file. DO NOT MODIFY")
            .addType(
                TypeSpec.classBuilder(CONFIG_CLASS_NAME)
                    .primaryConstructor(FunSpec.constructorBuilder().build())
                    .superclass(HashMap::class.parameterizedBy(String::class, String::class))
                    .addInitializerBlock(
                        CodeBlock.builder()
                            .addStatement("put(%S, %S)", "DEVICE_SCREENSHOT_DIR", screenshots.deviceDir)
                            .build()
                    )
                    .build()
            )
            .build()
    }

    companion object {

        const val NAME = "generateScreenshotConfig"
        private const val CONFIG_PACKAGE = "com.yandex.test.screenshot"
        private const val CONFIG_CLASS_NAME = "ScreenshotConfig"
    }
}
