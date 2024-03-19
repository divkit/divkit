package com.yandex.test.util

import org.gradle.api.GradleException
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Executes shell command with timeout
 * @property workDir - working directory
 * @property execTimeoutSec - timeout in seconds, 60 seconds by default
 * @property outputBufferSize - size of output buffer, 16 by default
 * @property errorBufferSize - size of error buffer, 16 by default
 */
internal class CmdExecutor(
    private val execTimeoutSec: Long = DEFAULT_EXEC_TIMEOUT_SEC,
    private val outputBufferSize: Int = DEFAULT_OUTPUT_BUFFER_SIZE,
    private val errorBufferSize: Int = DEFAULT_ERROR_BUFFER_SIZE
) {

    val output: Buffer<String>
        get() = outputBuffer

    val errors: Buffer<String>
        get() = errorBuffer

    private val outputBuffer = CircularBuffer<String>(outputBufferSize)
    private val errorBuffer = CircularBuffer<String>(errorBufferSize)

    private fun executeCommand(command: String, timeoutSec: Long): Int {
        try {
            val args = command.split("\\s".toRegex())
            val process = ProcessBuilder(args)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()

            val outputReader = process.inputStream.reader()
            val errorReader = process.errorStream.reader()
            val output = StringBuilder()
            val error = StringBuilder()
            val completed = try {
                process.waitFor(timeoutSec, TimeUnit.SECONDS) {
                    if (process.inputStream.available() > 0) output.append(outputReader.readText())
                    if (process.errorStream.available() > 0) error.append(errorReader.readText())
                }
            } finally {
                outputReader.close()
                errorReader.close()
            }

            return if (completed) {
                val exitCode = process.exitValue()
                if (exitCode == EXIT_CODE_OK) {
                    outputBuffer.add(output.toString().trim())
                } else {
                    errorBuffer.add(error.toString().trim())
                }
                exitCode
            } else {
                process.destroyForcibly()
                errorBuffer.add(ERROR_TIMEOUT)
                EXIT_CODE_TIMEOUT
            }
        } catch (e: IOException) {
            e.printStackTrace()
            errorBuffer.add(e.stackTrace())
            return EXIT_CODE_MISUSE
        }
    }

    fun runCommand(command: String, timeoutSec: Long = execTimeoutSec): String {
        val exitCode = executeCommand(command, timeoutSec)
        if (exitCode == EXIT_CODE_OK) {
            return output.last()
        } else {
            throw GradleException(errors.last())
        }
    }

    companion object {

        const val EXIT_CODE_OK = 0
        const val EXIT_CODE_MISUSE = 64
        const val EXIT_CODE_TIMEOUT = 80

        private const val DEFAULT_EXEC_TIMEOUT_SEC = 60L
        private const val DEFAULT_OUTPUT_BUFFER_SIZE = 16
        private const val DEFAULT_ERROR_BUFFER_SIZE = 16

        private const val ERROR_TIMEOUT = "Execution timeout"
    }
}
