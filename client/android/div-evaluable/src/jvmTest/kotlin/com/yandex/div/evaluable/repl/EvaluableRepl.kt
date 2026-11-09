@file:JvmName("EvaluableRepl")

package com.yandex.div.evaluable.repl

import com.yandex.div.evaluable.repl.EvaluableReplRuntime.readNewCommand

fun main() {
    println("Evaluable expressions REPL v0.0.1\n")
    println("Type 'help' to get list of available commands.\n")
    var command = readNewCommand()
    while (true) {
        val recognizedCommand = Command.values().find {
            it.canBeExecutedWith(command)
        }
        if (recognizedCommand != null) {
            recognizedCommand.execute(command)
        } else {
            println("Unknown command. Type 'help' to get list of available commands.")
        }
        command = readNewCommand()
    }
}
