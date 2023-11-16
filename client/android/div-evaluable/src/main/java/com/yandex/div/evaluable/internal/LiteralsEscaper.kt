package com.yandex.div.evaluable.internal

import com.yandex.div.evaluable.EvaluableException
import com.yandex.div.evaluable.TokenizingException

object LiteralsEscaper {

    private val ESCAPE_LITERALS = arrayOf("'", "@{")

    fun process(
        string: String,
        escapingLiterals: Array<String> = ESCAPE_LITERALS
    ): String {
        val literalBuilder = StringBuilder()
        var index = 0
        while (index < string.length) {
            if (string[index] == '\\') {
                val startIndexOfBackslash = index
                while (index < string.length && string[index] == '\\') {
                    index++
                }
                val countOfBackslashes = index - startIndexOfBackslash
                repeat(countOfBackslashes / 2) {
                    literalBuilder.append('\\')
                }
                val remainsEscapingBackslash = countOfBackslashes % 2 == 1
                if (remainsEscapingBackslash) {
                    if (index == string.length || string[index] == ' ') {
                        throw TokenizingException("Alone backslash at ${index - 1}")
                    }

                    val escapingLiteralsSet = escapingLiterals.toMutableSet()
                    var literalToReplace: String? = null
                    var literalIndex = 0
                    while (literalToReplace.isNullOrEmpty()
                            && escapingLiteralsSet.isNotEmpty()
                            && index < string.length
                    ) {
                        val escapingLiteralsIterator = escapingLiteralsSet.iterator()
                        while (escapingLiteralsIterator.hasNext() && index < string.length) {
                            val literal = escapingLiteralsIterator.next()
                            if (literal[literalIndex] != string[index]) {
                                escapingLiteralsIterator.remove()
                            } else {
                                if (literalIndex == literal.lastIndex) {
                                    literalToReplace = literal
                                    break
                                }
                            }
                        }
                        literalIndex++
                        index++
                    }
                    if (literalToReplace.isNullOrEmpty()) {
                        throw EvaluableException("Incorrect string escape")
                    }
                    literalBuilder.append(literalToReplace)
                }
            } else {
                literalBuilder.append(string[index++])
            }
        }
        return literalBuilder.toString()
    }
}
