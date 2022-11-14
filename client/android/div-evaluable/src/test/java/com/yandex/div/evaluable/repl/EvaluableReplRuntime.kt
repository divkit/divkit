package com.yandex.div.evaluable.repl

import com.yandex.div.evaluable.Evaluable
import com.yandex.div.evaluable.EvaluableType
import com.yandex.div.evaluable.Evaluator
import com.yandex.div.evaluable.VariableProvider
import com.yandex.div.evaluable.function.BuiltinFunctionProvider
import com.yandex.div.evaluable.internal.Parser
import com.yandex.div.evaluable.internal.Token
import com.yandex.div.evaluable.internal.Tokenizer
import com.yandex.div.evaluable.types.DateTime


internal object EvaluableReplRuntime {
    private val variableProvider = VariableProvider { variableName -> variableList[variableName] }
    private val functionProvider = BuiltinFunctionProvider
    private val evaluator = Evaluator(variableProvider, functionProvider)

    private val variableList = mutableMapOf<String, Any?>()

    val expressionRegex = Regex("@\\{(?<expression>.+)}")

    val variableAssigningRegex = Regex(
        "(?<variableName>\\w+)\\s?:\\s?(?<type>\\w+)\\s?=\\s?(?<value>.+)"
    )

    private val helpMessage = Command.values().joinToString(
        prefix = "Available commands:\n",
        separator = "\n"
    ) { "- ${it.commandName} [${it.commandDescription}]" }

    fun readNewCommand(): String {
        print(">")
        return readLine()!!.trim().minifySpaces()
    }

    fun clearVariables() {
        variableList.clear()
        println("Variables cleared.")
    }

    fun printVariables() {
        if (variableList.isEmpty()) {
            println("There are no variables be assigned.")
            return
        }
        println("Variables:")
        variableList.forEach {
            if (it.value != null) {
                println("${it.key}=${it.value}")
            }
        }
    }

    fun printHelpMessage() {
        println(helpMessage)
    }

    fun evaluateExpression(expression: String) {
        val evaluable = parseEvaluable(expression)?: return
        try {
            evaluator.eval<Any?>(evaluable).also { println(it) }
        } catch (t: Throwable) {
            println("Error evaluating '$expression'.")
            t.printStackTrace(System.out)
        }
    }

    fun parseAssignment(command: String) {
        try {
            val (name, value) = command.getNewVariableValue()
            variableList[name] = value
            println("Variable '$name' is set to value [$value].")
        } catch (t: Throwable) {
            println("Error assigning variable.")
            t.printStackTrace(System.out)
        }
    }

    private fun Token.asString() = when (this) {
        is Token.Operator.Binary -> {
            "Binary.${this::class.simpleName}"
        }
        is Token.Operator.Unary -> {
            "Unary.${this::class.simpleName}"
        }
        is Token.Operand, is Token.Function -> {
            toString()
        }
        else -> {
            this::class.simpleName
        }
    }

    private fun String.minifySpaces() = replace(Regex("\\s+"), " ")

    private fun String.getNewVariableValue(): Pair<String, Any> {
        val (_, name, type, value) = variableAssigningRegex.matchEntire(this.minifySpaces())!!.groupValues
        val evaluableType = EvaluableType.values().find { it.typeName.lowercase() == type.lowercase() }
            ?: throw RuntimeException("Unknown type $type.")
        return try {
            val convertedValue = when (evaluableType) {
                EvaluableType.INTEGER -> value.toInt()
                EvaluableType.NUMBER -> value.toDouble()
                EvaluableType.BOOLEAN -> value.toBoolean()
                EvaluableType.STRING -> value
                EvaluableType.COLOR -> value
                EvaluableType.DATETIME -> DateTime.parseAsUTC(value)
            }
            name to convertedValue
        } catch (t: Throwable) {
            throw RuntimeException(
                "Can't cast value '$value' to ${evaluableType.typeName} type.",
                t
            )
        }
    }

    private fun parseEvaluable(command: String): Evaluable? {
        val tokens = try {
            Tokenizer.tokenize(command)
        } catch (t: Throwable) {
            println("Error tokenizing '$command'.")
            t.printStackTrace(System.out)
            return null
        }
        return try {
            Parser.parse(tokens, command)
        } catch (t: Throwable) {
            val tokensString =
                tokens.joinToString(prefix = "[", postfix = "]") { "'${it.asString()}'" }
            println("Error evaluating '$command'. Parsed tokens: $tokensString.")
            t.printStackTrace(System.out)
            null
        }
    }
}
