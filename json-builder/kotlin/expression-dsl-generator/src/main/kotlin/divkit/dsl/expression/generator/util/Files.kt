package divkit.dsl.expression.generator.util

import java.io.File

private val environmentVariableRegex = Regex("\\$\\{([a-zA-Z_][a-zA-Z0-9_]*)}|\\$([a-zA-Z_][a-zA-Z0-9_]*)")

fun String.asFile(withVariableSubstitution: Boolean = true): File {
    val path = if (withVariableSubstitution) {
        substituteVariables()
    } else {
        this
    }
    return File(path)
}

private fun String.substituteVariables(): String {
    return environmentVariableRegex.replace(this) { matchResult ->
        val variableName = matchResult.groupValues[1].takeIf { it.isNotEmpty() }
            ?: matchResult.groupValues[2]
        System.getenv(variableName) ?: matchResult.value
    }
}

fun File.prepareDirectory(): File {
    mkdirs()
    if (!exists()) {
        throw RuntimeException("Failed to create directories in $this")
    }
    return this
}
