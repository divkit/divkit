package com.yandex.div.evaluable.repl

import kotlin.system.exitProcess

internal enum class Command(
    val commandName: String,
    val commandDescription: String,
    val actionOnCommand: (command: String) -> Unit,
    val regex: Regex = commandName.toRegex()
) {
    CLEAR(
        commandName = "clear",
        commandDescription = "clears info about all variables",
        actionOnCommand = { EvaluableReplRuntime.clearVariables() }
    ),
    EXIT(
        commandName = "exit",
        commandDescription = "exits program",
        actionOnCommand = { exitProcess(0) }
    ),
    HELP(
        commandName = "help",
        commandDescription = "prints this message",
        actionOnCommand = { EvaluableReplRuntime.printHelpMessage() }
    ),
    VARIABLES(
        commandName = "variables",
        commandDescription = "prints list of assigned variables",
        actionOnCommand = { EvaluableReplRuntime.printVariables() }
    ),
    ASSIGN_VARIABLE(
        commandName = "variableName: type = value",
        regex = EvaluableReplRuntime.variableAssigningRegex,
        commandDescription = "example: 'integerVar: integer = 14', 'stringVar: string = hello world'. Assigns variable <variableName> to value <value> of type <type>",
        actionOnCommand = { EvaluableReplRuntime.parseAssignment(it) }
    ),
    EXPRESSION(
        commandName = "@{expression}",
        regex = EvaluableReplRuntime.expressionRegex,
        commandDescription = "example: '@{sum(1, 2, 3)}', '@{toNumber(5) / 2.0}'. Evaluates expression <expression>",
        actionOnCommand = { EvaluableReplRuntime.evaluateExpression(it) }
    );

    fun canBeExecutedWith(command: String): Boolean = regex.matches(command)

    fun execute(command: String) = actionOnCommand(command)
}
