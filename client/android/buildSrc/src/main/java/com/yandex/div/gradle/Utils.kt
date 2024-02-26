package com.yandex.div.gradle

private val PYTHON_EXECUTABLE_VARIANTS = arrayOf(
    "python3",
    "python3.exe",
    "python",
    "python.exe",
    "py"
)

val pythonExecutableName: String
    get() = PYTHON_EXECUTABLE_VARIANTS.firstOrNull {
        runCatching {
            Runtime.getRuntime().exec("$it --version").waitFor() == 0
        }.getOrDefault(false)
    } ?: throw RuntimeException("Couldn't find Python binary")
