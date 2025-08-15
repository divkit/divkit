package com.yandex.test.util

import java.io.PrintWriter
import java.io.StringWriter

fun Throwable.stackTrace() = StringWriter().apply { printStackTrace(PrintWriter(this)) }.toString()
