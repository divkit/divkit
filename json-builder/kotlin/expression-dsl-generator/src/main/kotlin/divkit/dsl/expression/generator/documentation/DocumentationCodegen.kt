package divkit.dsl.expression.generator.documentation

import divkit.dsl.expression.generator.model.LocalizedText
import divkit.dsl.expression.generator.util.resolveKotlinArgumentType
import divkit.dsl.expression.generator.util.resolveKotlinType
import java.io.File
import java.io.FileWriter
import java.io.Writer

typealias Localization = LocalizedText.() -> String

class DocumentationCodegen(
    private val outputDir: File,
) {

    fun generateCode(
        signatures: List<DocumentationSignature>,
        outputFilename: String,
        localized: Localization
    ) {
        FileWriter(File(outputDir, outputFilename)).use { fileWriter ->
            generateCode(fileWriter, signatures, localized)
        }
    }

    private fun generateCode(
        writer: Writer,
        signatures: List<DocumentationSignature>,
        localized: Localization
    ) {
        writer.appendLine("# ${fileHeader.localized()}")
        writer.appendLine()
        val signatureGroups = signatures.groupBy { signature -> signature.name }
        signatureGroups.forEach { (name, signatures) ->
            writer.appendLine("## $name")
            signatures.forEach { signature ->
                writeDocumentationEntry(writer, signature, localized)
            }
        }
    }

    private fun writeDocumentationEntry(
        writer: Writer,
        signature: DocumentationSignature,
        localized: Localization
    ) {
        writeCompactFunctionEntry(writer, signature, localized)
        if (signature.supportsExtension()) {
            writeCompactExtensionFunctionEntry(writer, signature, localized)
        }
    }

    private fun writeCompactFunctionEntry(
        writer: Writer,
        signature: DocumentationSignature,
        localized: Localization
    ) {
        val ktArguments = signature.arguments.map { argument ->
            "Expression<${argument.type.resolveKotlinArgumentType()}>"
        }
        val ktReturnType = "Expression<${signature.returnType.resolveKotlinType()}>"

        writer.appendLine("```kotlin")
        if (signature.arguments.isEmpty()) {
            writer.appendLine("fun ${signature.name}(): $ktReturnType")
        } else {
            writer.appendLine("fun ${signature.name}(")
            signature.arguments.forEachIndexed { index, argument ->
                val modifiers = if (argument.isVararg) "vararg " else ""
                writer.appendLine("    $modifiers${ktArguments[index]},  // ${argument.description.localized()}")
            }
            writer.appendLine("): $ktReturnType")
        }
        writer.appendLine("```")
        writer.appendLine("***")
        writer.appendLine()
    }

    private fun writeDetailedFunctionEntry(
        writer: Writer,
        signature: DocumentationSignature,
        localized: Localization
    ) {
        val ktArguments = signature.arguments.map { argument ->
            "Expression<${argument.type.resolveKotlinArgumentType()}>"
        }
        val ktReturnType = "Expression<${signature.returnType.resolveKotlinType()}>"

        val ktSignature = "fun ${signature.name}(${ktArguments.joinToString()}): $ktReturnType"
        writer.appendLine("### ${ktSignature.markdownEscaped()}")
        writer.appendLine()

        if (signature.arguments.isNotEmpty()) {
            writer.appendLine(argumentsHeader.localized())
            writer.appendLine()
            writer.appendLine("| ${typeTitle.localized()} | ${descriptionTitle.localized()} |")
            writer.appendLine("| -------- | -------- |")
            signature.arguments.forEachIndexed { index, argument ->
                val description = "<p>${argument.description.localized()}</p>"
                val marker = if (argument.isVararg) "<p>**${variadicMarker.localized()}**</p>" else ""
                writer.appendLine("| `${ktArguments[index]}` | $description$marker |")
            }
            writer.appendLine()
        }

        writer.appendLine("${returnTypeHeader.localized()}: `$ktReturnType`")
        writer.appendLine("***")
        writer.appendLine()
    }

    private fun writeCompactExtensionFunctionEntry(
        writer: Writer,
        signature: DocumentationSignature,
        localized: Localization
    ) {
        require(signature.arguments.size == 1)

        val receiverArgument = signature.arguments.first()
        val methodArguments = signature.arguments.drop(1)

        val ktReceiver = "Expression<${receiverArgument.type.resolveKotlinArgumentType()}>"
        val ktArguments = methodArguments.map { argument ->
            "Expression<${argument.type.resolveKotlinArgumentType()}>"
        }
        val ktReturnType = "Expression<${signature.returnType.resolveKotlinType()}>"

        writer.appendLine("```kotlin")
        if (methodArguments.isEmpty()) {
            writer.appendLine("fun $ktReceiver.${signature.name}(): $ktReturnType")
        } else {
            writer.appendLine("fun $ktReceiver.${signature.name}(")
            methodArguments.forEachIndexed { index, argument ->
                val modifiers = if (argument.isVararg) "vararg " else ""
                writer.appendLine("    $modifiers${ktArguments[index]},  // ${argument.description.localized()}")
            }
            writer.appendLine("): $ktReturnType")
        }
        writer.appendLine("```")
        writer.appendLine("***")
        writer.appendLine()
    }

    private fun writeDetailedExtensionFunctionEntry(
        writer: Writer,
        signature: DocumentationSignature,
        localized: Localization
    ) {
        require(signature.arguments.size == 1)

        val receiverArgument = signature.arguments.first()
        val methodArguments = signature.arguments.drop(1)

        val ktReceiver = "Expression<${receiverArgument.type.resolveKotlinArgumentType()}>"
        val ktArguments = methodArguments.map { argument ->
            "Expression<${argument.type.resolveKotlinArgumentType()}>"
        }
        val ktReturnType = "Expression<${signature.returnType.resolveKotlinType()}>"

        val ktSignature = "fun $ktReceiver.${signature.name}(${ktArguments.joinToString()}): $ktReturnType"
        writer.appendLine("### ${ktSignature.markdownEscaped()}")
        writer.appendLine()

        writer.appendLine("${receiverTypeHeader.localized()}: `$ktReceiver`")
        writer.appendLine()

        if (methodArguments.isNotEmpty()) {
            writer.appendLine(argumentsHeader.localized())
            writer.appendLine()
            writer.appendLine("| ${typeTitle.localized()} | ${descriptionTitle.localized()} |")
            writer.appendLine("| -------- | -------- |")
            methodArguments.forEachIndexed { index, argument ->
                val description = "<p>${argument.description.localized()}</p>"
                val marker = if (argument.isVararg) "<p>**${variadicMarker.localized()}**</p>" else ""
                writer.appendLine("| `${ktArguments[index]}` | $description$marker |")
            }
            writer.appendLine()
        }

        writer.appendLine("${returnTypeHeader.localized()}: `$ktReturnType`")
        writer.appendLine("***")
        writer.appendLine()
    }

    private companion object {

        private val fileHeader = LocalizedText(
            english = "Functions",
            russian = "Функции"
        )

        private val receiverTypeHeader = LocalizedText(
            english = "Receiver type",
            russian = "Тип получателя"
        )

        private val argumentsHeader = LocalizedText(
            english = "Arguments",
            russian = "Аргументы"
        )

        private val returnTypeHeader = LocalizedText(
            english = "Return type",
            russian = "Возвращаемый тип"
        )

        private val typeTitle = LocalizedText(
            english = "Type",
            russian = "Тип"
        )

        private val descriptionTitle = LocalizedText(
            english = "Description",
            russian = "Описание"
        )

        private val variadicMarker = LocalizedText(
            english = "Variadic argument",
            russian = "Вариативный аргумент"
        )
    }
}

private fun String.markdownEscaped(): String {
    val specialChars = charArrayOf(
        '\\', '`', '*', '_',
        '{', '}', '[', ']',
        '<', '>', '(', ')',
        '#', '+', '-', '.',
        '!', '|'
    )

    val result = StringBuilder()
    for (char in this) {
        if (char in specialChars) {
            result.append('\\')
        }
        result.append(char)
    }

    return result.toString()
}
