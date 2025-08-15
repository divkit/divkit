package com.yandex.test.util

import org.junit.runner.Description

internal fun Description.formattedName(): String {
    return className + "#" + methodName
}
