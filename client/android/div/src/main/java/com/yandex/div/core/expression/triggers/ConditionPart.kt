package com.yandex.div.core.expression.triggers

import com.yandex.div.core.expression.triggers.State.Input.EndOfLine
import com.yandex.div.core.expression.triggers.State.Input.EscapeCharacter
import com.yandex.div.core.expression.triggers.State.Input.Letter
import com.yandex.div.core.expression.triggers.State.Input.OpeningBracket
import com.yandex.div.core.expression.triggers.State.Input.Other
import com.yandex.div.core.expression.triggers.State.Input.SingleQuote
import com.yandex.div.core.expression.triggers.State.Input.VarSpecial
import com.yandex.div.json.invalidCondition

// TODO(gulevsky): possibly unused
internal sealed interface ConditionPart {
    data class RawString(val value: String) : ConditionPart
    data class Variable(val name: String) : ConditionPart

    companion object {
        fun parse(condition: String) = Result(condition).parse()
    }
}

private class Result(val input: String) {
    private var start = 0
    private var end = 0
    private val result = mutableListOf<ConditionPart>()

    fun emitRaw() {
        emit { ConditionPart.RawString(it) }
    }

    fun emitVariable() {
        emit { ConditionPart.Variable(it) }
    }

    fun throwError(message: String): Nothing {
        throw invalidCondition(message, input)
    }

    private inline fun emit(part: (String) -> ConditionPart) {
        result.add(part(input.substring(start, end)))
        start = end
    }

    fun parse(): List<ConditionPart> {
        var state: State = State.Start
        input.forEach {
            state = state.input(State.Input.fromChar(it), this)
            end++
        }
        state.input(EndOfLine, this)
        return result
    }
}

private sealed interface State {
    object Start : State {
        override fun input(input: Input, result: Result) =
            initialInput(input)
    }

    object Raw : State {
        override fun input(input: Input, result: Result) = when (input) {
            Other, VarSpecial, OpeningBracket, EscapeCharacter -> Raw
            Letter -> {
                result.emitRaw()
                Variable
            }
            SingleQuote -> {
                result.emitRaw()
                QuotedString
            }
            EndOfLine -> {
                result.emitRaw()
                End
            }
        }
    }

    object Variable : State {
        override fun input(input: Input, result: Result) = when (input) {
            Letter, VarSpecial -> Variable
            OpeningBracket -> {
                Function
            }
            Other, EscapeCharacter -> {
                result.emitVariable()
                Raw
            }
            SingleQuote -> {
                result.emitVariable()
                QuotedString
            }
            EndOfLine -> {
                result.emitVariable()
                End
            }
        }
    }

    object Function : State {
        override fun input(input: Input, result: Result): State {
            result.emitRaw()
            return initialInput(input)
        }
    }

    object QuotedString : State {
        override fun input(input: Input, result: Result) = when (input) {
            Letter,
            VarSpecial,
            OpeningBracket,
            Other -> QuotedString
            SingleQuote -> {
                EndOfString
            }
            EscapeCharacter -> QuotedStringEscaped
            EndOfLine -> result.throwError("Invalid quoted string")
        }
    }

    object QuotedStringEscaped : State {
        override fun input(input: Input, result: Result) = when (input) {
            Letter,
            VarSpecial,
            OpeningBracket,
            Other,
            EscapeCharacter,
            SingleQuote -> QuotedString
            EndOfLine -> result.throwError("Invalid escape sequence")
        }
    }

    object EndOfString : State {
        override fun input(input: Input, result: Result): State {
            result.emitRaw()
            return initialInput(input)
        }
    }

    object End : State {
        override fun input(input: Input, result: Result) = throw IllegalStateException()
    }

    fun input(input: Input, result: Result): State

    enum class Input {
        Letter,
        VarSpecial,
        OpeningBracket,
        Other,
        SingleQuote,
        EscapeCharacter,
        EndOfLine;

        companion object {
            fun fromChar(c: Char) = when (c) {
                in 'A'..'Z', in 'a'..'z', '_' -> Letter
                '.', in '0'..'9' -> VarSpecial
                '(' -> OpeningBracket
                '\'' -> SingleQuote
                '\\' -> EscapeCharacter
                else -> Other
            }
        }
    }

    private companion object {
        private fun initialInput(input: Input): State =
            when (input) {
                Letter -> Variable
                VarSpecial, OpeningBracket, Other, EscapeCharacter -> Raw
                SingleQuote -> QuotedString
                EndOfLine -> End
            }
    }
}
