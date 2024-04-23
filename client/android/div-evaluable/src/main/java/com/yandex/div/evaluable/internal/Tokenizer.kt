package com.yandex.div.evaluable.internal

import com.yandex.div.evaluable.EvaluableException
import com.yandex.div.evaluable.TokenizingException

internal object Tokenizer {

    private const val EMPTY_CHAR = '\u0000'

    fun tokenize(input: String): List<Token> = tokenize(input.toCharArray())

    private fun tokenize(input: CharArray): List<Token> {
        val state = TokenizationState(input)
        try {
            processStringTemplate(state, state.tokens, false)
        } catch (exception: EvaluableException) {
            when (exception) {
                is TokenizingException ->
                    throw EvaluableException("Error tokenizing '${input.concatToString()}'.", exception)
                else -> throw exception
            }
        }
        return state.tokens
    }

    private fun processStringTemplate(state: TokenizationState,
                                      tokens: MutableList<Token>,
                                      isPartOfExpression: Boolean = true) {
        if (isPartOfExpression) {
            state.forward()
        }

        val stringTemplateTokens = mutableListOf<Token>()
        val stringLiteral = processString(state, isPartOfExpression)

        if (state.currentChar().isAtEnd()) {
            if (isPartOfExpression) {
                throw TokenizingException("'\'' expected at end of string literal at ${state.index}")
            }
            stringLiteral?.let {  tokens.add(it) }
            return
        }

        if (state.currentChar().isAtEndOfStringLiteral(state)) {
            tokens.add(stringLiteral ?: Token.Operand.Literal.Str(""))
            state.forward()
            return
        }

        if (stringLiteral != null && state.currentChar().isStartOfExpression(state)) {
            stringTemplateTokens.add(Token.StringTemplate.Start)
            stringTemplateTokens.add(stringLiteral)
        }

        while (state.currentChar().isStartOfExpression(state)) {
            val expressionTokens = mutableListOf<Token>()
            processExpression(state, expressionTokens)

            val stringAfterExpression = processString(state)

            val isSpecialCaseWithExpressionInStringTemplate = !isPartOfExpression
                    && stringTemplateTokens.isEmpty()
                    && stringAfterExpression == null
                    && !state.currentChar().isStartOfExpression(state)

            if (isSpecialCaseWithExpressionInStringTemplate) {
                tokens.addAll(expressionTokens)
                return
            }

            if (stringTemplateTokens.isEmpty()) {
                stringTemplateTokens.add(Token.StringTemplate.Start)
            }
            stringTemplateTokens.add(Token.StringTemplate.StartOfExpression)
            stringTemplateTokens.addAll(expressionTokens)
            stringTemplateTokens.add(Token.StringTemplate.EndOfExpression)
            stringAfterExpression?.let { stringTemplateTokens.add(it) }
        }

        if (isPartOfExpression && !state.currentChar().isAtEndOfStringLiteral(state)) {
            throw TokenizingException("'\'' expected at end of string literal at ${state.index}")
        }

        if (stringTemplateTokens.isNotEmpty()) {
            tokens.addAll(stringTemplateTokens)
            tokens.add(Token.StringTemplate.End)
        }

        if (isPartOfExpression) {
            state.forward()
        }
    }

    private fun processString(state: TokenizationState, isLiteral: Boolean = true): Token.Operand.Literal.Str? {
        val start = state.index

        while (!isAtEndOfString(state, isLiteral)) {
            state.forward()
        }

        val string = LiteralsEscaper.process(state.part(start, state.index))

        return if (string.isNotEmpty()) {
            Token.Operand.Literal.Str(string)
        } else {
            null
        }
    }

    private fun isAtEndOfString(state: TokenizationState, isLiteral: Boolean): Boolean {
        return state.currentChar().isAtEnd()
                || state.currentChar().isStartOfExpression(state)
                || (isLiteral && state.currentChar().isAtEndOfStringLiteral(state))
    }

    private fun processExpression(state: TokenizationState, tokens: MutableList<Token> = state.tokens): Boolean {
        if (!state.currentChar().isStartOfExpression(state)) {
            return false
        }
        state.forward(2)
        while(!state.currentChar().isAtEnd() && state.currentChar() != '}') {
            when(state.currentChar()) {
                '?' -> {
                    tokens.add(Token.Operator.TernaryIf)
                    state.forward()
                }
                ':' -> {
                    tokens.add(Token.Operator.TernaryElse)
                    state.forward()
                }
                '+' -> {
                    val token = when {
                        isUnaryOperator(tokens) -> Token.Operator.Unary.Plus
                        isOperator(tokens) -> Token.Operator.Binary.Sum.Plus
                        else -> throw invalidToken(state)
                    }
                    tokens.add(token)
                    state.forward()
                }
                '-' -> {
                    val token = when {
                        isUnaryOperator(tokens) -> Token.Operator.Unary.Minus
                        isOperator(tokens) -> Token.Operator.Binary.Sum.Minus
                        else -> throw invalidToken(state)
                    }
                    tokens.add(token)
                    state.forward()
                }
                '*' -> {
                    tokens.add(Token.Operator.Binary.Factor.Multiplication)
                    state.forward()
                }
                '/' -> {
                    tokens.add(Token.Operator.Binary.Factor.Division)
                    state.forward()
                }
                '%' -> {
                    tokens.add(Token.Operator.Binary.Factor.Modulo)
                    state.forward()
                }
                '!' -> {
                    when {
                        state.nextChar() == '=' -> {
                            tokens.add(Token.Operator.Binary.Equality.NotEqual)
                            state.forward(2)
                        }
                        state.nextChar() == ':' -> {
                            tokens.add(Token.Operator.Try)
                            state.forward(2)
                        }
                        isUnaryOperator(tokens) -> {
                            tokens.add(Token.Operator.Unary.Not)
                            state.forward()
                        }
                        else -> throw invalidToken(state)
                    }
                }
                '&' -> {
                    when {
                        state.nextChar() == '&' -> {
                            tokens.add(Token.Operator.Binary.Logical.And)
                            state.forward(2)
                        }
                        else -> throw invalidToken(state)
                    }
                }
                '|' -> {
                    when {
                        state.nextChar() == '|' -> {
                            tokens.add(Token.Operator.Binary.Logical.Or)
                            state.forward(2)
                        }
                        else -> throw invalidToken(state)
                    }
                }
                '<' -> {
                    when {
                        state.nextChar() == '=' -> {
                            tokens.add(Token.Operator.Binary.Comparison.LessOrEqual)
                            state.forward(2)
                        }
                        else -> {
                            tokens.add(Token.Operator.Binary.Comparison.Less)
                            state.forward()
                        }
                    }
                }
                '>' -> {
                    when {
                        state.nextChar() == '=' -> {
                            tokens.add(Token.Operator.Binary.Comparison.GreaterOrEqual)
                            state.forward(2)
                        }
                        else -> {
                            tokens.add(Token.Operator.Binary.Comparison.Greater)
                            state.forward()
                        }
                    }
                }
                '=' -> {
                    when {
                        state.nextChar() == '=' -> {
                            tokens.add(Token.Operator.Binary.Equality.Equal)
                            state.forward(2)
                        }
                        else -> throw invalidToken(state)
                    }
                }
                '(' -> {
                    tokens.add(Token.Bracket.LeftRound)
                    state.forward()
                }
                ')' -> {
                    tokens.add(Token.Bracket.RightRound)
                    state.forward()
                }
                ',' -> {
                    tokens.add(Token.Function.ArgumentDelimiter)
                    state.forward()
                }
                '\'' -> processStringTemplate(state, tokens)
                else -> {
                    when {
                        state.currentChar().isWhiteSpace() -> { state.forward() }
                        state.currentChar().isDecimal(state.prevChar(), state.nextChar()) -> processNumber(state, tokens)
                        state.currentChar().isAlphabetic() -> processIdentifier(state, tokens)
                        state.currentChar().isDot() -> {
                            state.forward()
                            tokens.add(Token.Operator.Dot)
                        }
                        else -> throw invalidToken(state)
                    }
                }
            }
        }

        if (!state.currentChar().isAtEndOfExpression()) {
            throw TokenizingException("'}' expected at end of expression at ${state.index}")
        }
        state.forward()

        return true
    }

    private fun processNumber(state: TokenizationState, tokens: MutableList<Token>) {
        val start = state.index
        val isNegative = tokens.lastOrNull() is Token.Operator.Unary.Minus
        if (isNegative) {
            tokens.removeLastOrNull()
        }

        do {
            state.forward()
        } while (state.currentChar().isDigit())

        if (state.charAt(start) == '.' || state.currentChar().isDecimal(state.prevChar(), state.nextChar())) {
            while (state.currentChar().isDecimal(state.prevChar(), state.nextChar())) {
                state.forward()
            }
            val valueStr = if (isNegative) {
                "-${state.part(start, state.index)}"
            } else {
                state.part(start, state.index)
            }
            val value = try {
                valueStr.toDouble()
            } catch (e: Exception) {
                throw EvaluableException("Value $valueStr can't be converted to Number type.")
            }
            tokens.add(Token.Operand.Literal.Num(value))
        } else {
            val valueStr = if (isNegative) {
                "-${state.part(start, state.index)}"
            } else {
                state.part(start, state.index)
            }
            val value = try {
                valueStr.toLong()
            } catch (e: Exception) {
                throw EvaluableException("Value $valueStr can't be converted to Integer type.")
            }
            tokens.add(Token.Operand.Literal.Num(value))
        }
    }

    private fun processIdentifier(state: TokenizationState, tokens: MutableList<Token>) {
        val parts = mutableListOf<String>()
        var endsWithDot: Boolean
        do {
            val start = state.index
            endsWithDot = false
            while (state.currentChar().isValidIdentifier()) state.forward()
            parts.add(state.part(start, state.index))
            if (state.currentChar().isDot()) {
                state.forward()
                endsWithDot = true
            }
        } while (state.currentChar().isValidIdentifier() || state.currentChar().isDot())

        var funcToken: Token.Function? = null
        while(state.currentChar().isWhiteSpace()) state.forward()

        while (parts.size > 0) {
            when {
                funcToken == null && state.currentChar() == '(' -> {
                    funcToken = Token.Function(parts.last())
                    parts.removeLast()
                }

                parts.size == 1 && processKeyword(parts.first(), tokens) -> {
                    funcToken?.let {
                        tokens.add(Token.Operator.Dot)
                        tokens.add(it)
                    }
                    return
                }

                else -> {
                    tokens.apply {
                        val name = parts.joinToString(
                            separator = ".",
                            postfix = if (endsWithDot) "." else ""
                        )
                        add(Token.Operand.Variable(name))
                        funcToken?.let {
                            add(Token.Operator.Dot)
                            add(it)
                        }
                    }
                    return
                }
            }
        }
        funcToken?.let { tokens.add(it) }
    }

    private fun processKeyword(identifier: String, tokens: MutableList<Token>): Boolean {
        val token = when (identifier) {
            "true" -> Token.Operand.Literal.Bool(true)
            "false" -> Token.Operand.Literal.Bool(false)
            else -> null
        }

        return if (token == null) {
            false
        } else {
            tokens.add(token)
            true
        }
    }

    private fun invalidToken(state: TokenizationState) =
        EvaluableException("Invalid token '${state.currentChar()}' at position ${state.index}")

    private fun isOperator(tokens: List<Token>): Boolean {
        if (tokens.isEmpty() || tokens.last() is Token.Operator.Unary) {
            return false
        }
        return tokens.last() is Token.Operand || tokens.last() is Token.Bracket.RightRound
    }

    private fun isUnaryOperator(tokens: List<Token>): Boolean {
        return !isOperator(tokens) && tokens.lastOrNull() !is Token.Operator.Unary
    }

    private data class TokenizationState(private val source: CharArray) {
        var index: Int = 0
        val tokens = mutableListOf<Token>()

        fun prevChar(step: Int = 1) = if (index - step >= 0) {
            source[index - step]
        } else {
            EMPTY_CHAR
        }

        fun currentCharIsEscaped() = if (index >= source.size) {
            false
        } else {
            var currentIndex = index - 1
            var backslashesCounter = 0
            while (currentIndex > 0 && source[currentIndex] == '\\') {
                backslashesCounter++
                currentIndex--
            }
            val isEscaped = backslashesCounter % 2 == 1
            isEscaped
        }

        fun currentChar() = if (index >= source.size) {
            EMPTY_CHAR
        } else {
            source[index]
        }

        fun charAt(position: Int) = if (position in source.indices) {
            source[position]
        } else {
            EMPTY_CHAR
        }

        fun part(from: Int, to: Int) = source.concatToString(from, to)

        fun nextChar(step: Int = 1) = if (index + step >= source.size) {
            EMPTY_CHAR
        } else {
            source[index + step]
        }

        fun forward(count: Int = 1): Int {
            val value = index
            index += count
            return value
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as TokenizationState

            return source.contentEquals(other.source)
        }

        override fun hashCode(): Int {
            return source.contentHashCode()
        }
    }

    private fun Char.isAlphabetic() = this in 'a'..'z' || this in 'A'..'Z' || this == '_'
    private fun Char.isNumber() = this in '0'..'9'
    private fun Char.isDecimal(
        previousChar: Char = EMPTY_CHAR,
        nextChar: Char = EMPTY_CHAR
    ) = this.isDigit() || when (this) {
        '.' -> nextChar.isDigit()
        'e', 'E' -> previousChar.isDigit() && (nextChar.isDigit() || nextChar == '+' || nextChar == '-')
        '+', '-' -> (previousChar == 'e' || previousChar == 'E') && nextChar.isDigit()
        else -> false
    }
    private fun Char.isWhiteSpace() = this == ' ' || this == '\t' || this == '\r' || this == '\n'
    private fun Char.isValidIdentifier() = this.isAlphabetic() || this.isNumber()
    private fun Char.isDot() = this == '.'
    private fun Char.isAtEndOfStringLiteral(state: TokenizationState) =
        this == '\'' && !state.currentCharIsEscaped()
    private fun Char.isStartOfExpression(state: TokenizationState) =
        this == '@' && state.prevChar() != '\\' && state.nextChar() == '{'
    private fun Char.isAtEndOfExpression() = this == '}'
    private fun Char.isAtEnd() = this == EMPTY_CHAR
}
