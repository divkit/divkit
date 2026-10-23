package com.yandex.div.detekt

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
import org.jetbrains.kotlin.psi.KtFile

class FileLength(config: Config) : Rule(config, "Production Kotlin files must not exceed 800 physical lines.") {
    private val allowedLines: Int by config(800)

    override fun visitKtFile(file: KtFile) {
        if (generatedHeader.containsMatchIn(file.text.take(1500))) {
            return
        }
        val lines = file.text.lineSequence().count() - when {
            file.text.isEmpty() || file.text.endsWith('\n') || file.text.endsWith('\r') -> 1
            else -> 0
        }
        if (lines > allowedLines) {
            report(Finding(
                Entity.from(file),
                "${file.virtualFilePath} has $lines lines; maximum $allowedLines. " +
                    "Reduce the file or move code into another file.",
            ))
        }
    }

    private companion object {
        val generatedHeader = Regex(
            "^\\s*(?://|\\*)\\s*(?:generated (?:by|code)|auto[- ]?generated|" +
                "this (?:file|code) (?:is|was) (?:auto[- ]?)?generated)",
            setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE),
        )
    }
}
