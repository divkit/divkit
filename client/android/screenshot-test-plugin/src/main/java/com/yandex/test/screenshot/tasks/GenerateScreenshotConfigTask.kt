package com.yandex.test.screenshot.tasks

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class GenerateScreenshotConfigTask : DefaultTask() {

    @get:Input
    abstract val deviceDir: Property<String>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    init {
        group = "other"
    }

    @TaskAction
    fun perform() {
        generateConfig().writeTo(outputDir.get().asFile)
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
                            .addStatement("put(%S, %S)", "DEVICE_SCREENSHOT_DIR", deviceDir.get())
                            .build()
                    )
                    .build()
            )
            .build()
    }

    companion object {
        private const val CONFIG_PACKAGE = "com.yandex.test.screenshot"
        private const val CONFIG_CLASS_NAME = "ScreenshotConfig"
    }
}
