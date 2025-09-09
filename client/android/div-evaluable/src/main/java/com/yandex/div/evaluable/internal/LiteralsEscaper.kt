package com.yandex.div.evaluable.internal

import com.yandex.div.evaluable.EvaluableException
import com.yandex.div.evaluable.TokenizingException

object LiteralsEscaper {

    private const val BACKSLASH = '\\'

    private val ESCAPE_LITERALS = arrayOf("'", "@{")

    fun process(
        string: String,
        escapingLiterals: Array<String> = ESCAPE_LITERALS
    ): String {
        if (!string.contains(BACKSLASH)) return string
        val literalBuilder = StringBuilder(string.length)
        var index = 0
        while (index < string.length) {
            if (string[index] != BACKSLASH) {
                literalBuilder.append(string[index++])
                continue
            }
            val countOfBackslashes = countConsecutiveBackslashes(string, index)
            index += countOfBackslashes
            repeat(countOfBackslashes / 2) {
                literalBuilder.append(BACKSLASH)
            }
            val remainsEscapingBackslash = countOfBackslashes % 2 == 1
            if (remainsEscapingBackslash) {
                val literalToReplace = escapeLiteral(string, index, escapingLiterals)
                literalBuilder.append(literalToReplace)
                index += literalToReplace.length
            }
        }
        return literalBuilder.toString()
    }

    private fun countConsecutiveBackslashes(string: String, index: Int): Int {
        var currentIndex = index
        while (currentIndex < string.length && string[currentIndex] == BACKSLASH) {
            currentIndex++
        }
        return currentIndex - index
    }

    private fun escapeLiteral(string: String, index: Int, possibleLiterals: Array<String>): String {
        if (index == string.length || string[index] == ' ') {
            throw EvaluableException("Error tokenizing '$string'.", TokenizingException("Alone backslash at ${index - 1}"))
        }
        for (literal in possibleLiterals) {
            if (isPossibleEscapeLiteral(literal, string, index)) {
                return literal
            }
        }
        throw EvaluableException("Incorrect string escape")
    }

    private fun isPossibleEscapeLiteral(literal: String, string: String, from: Int): Boolean {
        for (literalIndex in literal.indices) {
            val stringIndex = from + literalIndex
            if (stringIndex >= string.length || string[stringIndex] != literal[literalIndex]) {
                return false
            }
        }
        return true
    }
}
