package com.yandex.test.util

import java.io.File

internal class Logger(
    private val tag: String,
    private vararg val outputs: LogOutput
) {

    fun a(message: String) = writeLine("A/$tag: $message")

    fun a(message: String, e: Throwable) = writeLine("A/$tag: ${messageWithError(message, e)}")

    fun e(message: String) = writeLine("E/$tag: $message")

    fun e(message: String, e: Throwable) = writeLine("E/$tag: ${messageWithError(message, e)}")

    fun w(message: String) = writeLine("W/$tag: $message")

    fun w(message: String, e: Throwable) = writeLine("W/$tag: ${messageWithError(message, e)}")

    fun i(message: String) = writeLine("I/$tag: $message")

    fun i(message: String, e: Throwable) = writeLine("I/$tag: ${messageWithError(message, e)}")

    fun d(message: String) = writeLine("D/$tag: $message")

    fun d(message: String, e: Throwable) = writeLine("D/$tag: ${messageWithError(message, e)}")

    fun v(message: String) = writeLine("V/$tag: $message")

    fun v(message: String, e: Throwable) = writeLine("V/$tag: ${messageWithError(message, e)}")

    private fun writeLine(message: String) {
        outputs.forEach { output ->
            output.writeLine(message)
        }
    }

    private fun messageWithError(message: String, e: Throwable) = "$message\n\t${e.stackTrace()}"
}

internal interface LogOutput {

    fun writeLine(message: String)
}

internal class StreamOutput : LogOutput {

    override fun writeLine(message: String) = println(message)
}

internal class FileOutput(private val file: File) : LogOutput {

    override fun writeLine(message: String) = file.appendText("$message\n")
}
