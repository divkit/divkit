package com.yandex.android

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import java.util.concurrent.Executor

internal class TestExecutor : Executor {

    val commands = mutableListOf<Runnable>()

    override fun execute(r: Runnable) {
        commands.add(r)
    }

    fun runAll() {
        val commandsCopy = ArrayList(commands)
        commands.clear()
        commandsCopy.forEach { command ->
            command.run()
        }
    }

    fun runAllOnce() {
        assertFalse(commands.isEmpty())
        runAll()
        assertTrue(commands.isEmpty())
    }
}
