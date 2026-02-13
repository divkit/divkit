package com.yandex.div.internal.storage

import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset

internal interface DataReader<T> {
    fun read(input: InputStream): T
}

internal interface DataWriter<T> {
    fun write(value: T, output: OutputStream)
}

internal interface DataEditor<T> : DataReader<T>, DataWriter<T>

internal abstract class TextDataEditor<T>(
    private val charset: Charset = Charsets.UTF_8
) : DataEditor<T> {

    override fun read(input: InputStream): T {
        val fileData = input.reader(charset).use { it.readText() }
        return read(fileData)
    }

    abstract fun read(input: String): T

    override fun write(value: T, output: OutputStream) {
        val fileData = write(value)
        output.writer(charset).use { it.write(fileData) }
    }

    abstract fun write(value: T): String
}
