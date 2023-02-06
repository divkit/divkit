@file:JvmName("FileExtensions")

package com.yandex.div.gradle

import groovy.lang.Closure
import java.io.File

fun ifExists(filename: String, action: (File) -> Unit) {
    val file = File(filename)
    if (file.exists()) {
        action(file)
    }
}

fun ifExists(filename: String, action: Closure<*>) {
    val file = File(filename)
    if (file.exists()) {
        action(file)
    }
}

private operator fun <T> Closure<T>.invoke(x: Any?): T = call(x)
