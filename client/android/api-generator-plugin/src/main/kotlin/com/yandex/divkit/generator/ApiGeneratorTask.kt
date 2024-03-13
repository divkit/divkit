package com.yandex.divkit.generator

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

abstract class ApiGeneratorTask : DefaultTask() {
    @get:Inject
    abstract val exec: ExecOperations

    @get:InputDirectory
    abstract val workingDirectory: DirectoryProperty

    @get:InputFile
    abstract val configPath: RegularFileProperty

    @get:InputDirectory
    abstract val schemasDirectory: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun generate() {
        logger.lifecycle("Process schemas: ${schemasDirectory.get().asFile.absolutePath}")

        exec.exec {
            workingDir = workingDirectory.get().asFile
            commandLine(exec.resolvePythonExecutableName())
            args(
                "-m", "api_generator",
                "-c", configPath.get().asFile.absolutePath,
                "-s", schemasDirectory.get().asFile.absolutePath,
                "-o", outputDirectory.get().asFile.absolutePath,
            )
        }.assertNormalExitValue()
    }
}

private fun ExecOperations.resolvePythonExecutableName(): String {
    return PYTHON_EXECUTABLE_VARIANTS.firstOrNull {
        exec { commandLine(it, "--version") }.exitValue == 0
    } ?: throw RuntimeException("Couldn't find Python binary")
}

private val PYTHON_EXECUTABLE_VARIANTS = arrayOf(
    "python3",
    "python3.exe",
    "python",
    "python.exe",
    "py"
)
