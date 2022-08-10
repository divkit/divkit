package com.yandex.div.json

import android.net.Uri
import java.util.regex.Pattern

fun interface ValueValidator<T> {
    fun isValid(value: T): Boolean
}

fun interface ListValidator<T> {
    fun isValid(value: List<T>): Boolean
}

fun String.doesMatch(regex: String) = Pattern.matches(regex, this)

fun Uri.hasScheme(schemes: Collection<String>): Boolean = scheme?.let { it in schemes } ?: false
