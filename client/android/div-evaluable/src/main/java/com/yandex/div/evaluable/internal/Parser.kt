package com.yandex.div.evaluable.internal

import com.yandex.div.evaluable.Evaluable
import com.yandex.div.evaluable.EvaluableException

/**
 *  Converts tokens to expression using this Mantras:
 *
 *  The Mantra of Expression
 *  expression -> try ( "?" expression ":" expression )?
 *  try -> or ( "!:" expression )?
 *  or -> and ( "||" and )*
 *  and -> equal ( "&&" equal )*
 *  equal -> comparison ( ( "!=" | "==" ) comparison )*
 *  comparison -> sum ( ( ">" | ">=" | "<" | "<=" ) sum )*
 *  sum -> factor ( ( "-" | "+" ) factor )*
 *  factor -> unary ( ( "/" | "*" | "%") unary )*
 *  unary -> ( "!" | "-" | "+" ) unary | exponent
 *  exponent -> method ( "^" unary )*
 *  method -> calls call.method(args)
 *  call -> function | variable | number | boolean
 *
 *  The Mantra of Function
 *  function -> name "(" ( arguments )* ")"
 *  arguments -> expression ( ',' expression )*
 *
 *  The Mantra of Variables
 *  variable -> name
 *  name -> CHAR ( CHAR | DIGIT | _ | . )*
 *
 *  The Mantra of Literals
 *  int -> DIGIT ( DIGIT )*
 *  decimal -> DIGIT ( DIGIT | . )*
 *  string -> ' (SYMBOL)* '
 * */
internal object Parser {

    fun parse(tokens: List<Token>, rawExpression: String): Evaluable {
        if (tokens.isEmpty()) {
            throw EvaluableException("Expression expected")
        }

        val state = ParsingState(tokens, rawExpression)
        val expression = expression(state)
        if (state.isNotAtEnd()) {
            throw EvaluableException("Expression expected")
        }

        return expression
    }

    private fun expression(state: ParsingState): Evaluable {
        val first = `try`(state)
        if (state.isNotAtEnd() && state.currentToken() is Token.Operator.TernaryIf) {
            state.forward()
            val second = expression(state)
            if (state.isAtEnd() || state.currentToken() !is Token.Operator.TernaryElse) {
                throw EvaluableException("':' expected in ternary-if-else expression")
            }
            state.forward()
            val third = expression(state)
            return Evaluable.Ternary(Token.Operator.TernaryIfElse, first, second, third, state.rawExpr)
        }
        return first
    }

    private fun `try`(state: ParsingState): Evaluable {
        val first = or(state)
        if (state.isNotAtEnd() && state.currentToken() is Token.Operator.Try) {
            val token = state.next()
            val second = expression(state)
            return Evaluable.Try(token as Token.Operator.Try, first, second, state.rawExpr)
        }
        return first
    }

    private fun or(state: ParsingState): Evaluable {
        var left = and(state)
        while (state.isNotAtEnd() && state.currentToken() is Token.Operator.Binary.Logical.Or) {
            state.forward()
            val right = and(state)
            left = Evaluable.Binary(Token.Operator.Binary.Logical.Or, left, right, state.rawExpr)
        }
        return left
    }

    private fun and(state: ParsingState): Evaluable {
        var left = equal(state)
        while (state.isNotAtEnd() && state.currentToken() is Token.Operator.Binary.Logical.And) {
            state.forward()
            val right = equal(state)
            left = Evaluable.Binary(Token.Operator.Binary.Logical.And, left, right, state.rawExpr)
        }
        return left
    }

    private fun equal(state: ParsingState): Evaluable {
        var left = comparison(state)
        while (state.isNotAtEnd() && state.currentToken() is Token.Operator.Binary.Equality) {
            val operator = state.next()
            val right = comparison(state)
            left = Evaluable.Binary(operator as Token.Operator.Binary, left, right, state.rawExpr)
        }
        return left
    }

    private fun comparison(state: ParsingState): Evaluable {
        var left = sum(state)
        while (state.isNotAtEnd() && state.currentToken() is Token.Operator.Binary.Comparison) {
            val operator = state.next()
            val right = sum(state)
            left = Evaluable.Binary(operator as Token.Operator.Binary, left, right, state.rawExpr)
        }
        return left
    }

    private fun sum(state: ParsingState): Evaluable {
        var left = factor(state)
        while(state.isNotAtEnd() && state.currentToken() is Token.Operator.Binary.Sum) {
            val operator = state.next()
            left = Evaluable.Binary(operator as Token.Operator.Binary, left, factor(state), state.rawExpr)
        }
        return left
    }

    private fun factor(state: ParsingState): Evaluable {
        var left = unary(state)
        while(state.isNotAtEnd() && state.currentToken() is Token.Operator.Binary.Factor) {
            val operator = state.next()
            left = Evaluable.Binary(operator as Token.Operator.Binary, left, unary(state), state.rawExpr)
        }
        return left
    }

    private fun unary(state: ParsingState): Evaluable {
        return if (state.isNotAtEnd() && state.currentToken() is Token.Operator.Unary) {
            val token = state.next()
            Evaluable.Unary(token as Token.Operator, unary(state), state.rawExpr)
        } else {
            exponent(state)
        }
    }

    private fun exponent(state: ParsingState): Evaluable {
        var call = method(state)
        if (state.isNotAtEnd() && state.currentToken() is Token.Operator.Binary.Power) {
            state.forward()
            call = Evaluable.Binary(Token.Operator.Binary.Power, call, unary(state), state.rawExpr)
        }
        return call
    }

    private fun method(state: ParsingState): Evaluable {
        var call = call(state)
        while (state.isNotAtEnd() && state.currentToken() is Token.Operator.Dot) {
            state.forward()
            call = call(state, call)
        }
        return call
    }

    private fun call(state: ParsingState, prefix: Evaluable? = null): Evaluable {
        if (state.isAtEnd()) {
            throw EvaluableException("Expression expected")
        }
        val token = state.next()
        if (prefix != null && token !is Token.Function) {
            throw EvaluableException("Method expected after .")
        }
        return when (token) {
            is Token.Operand.Literal -> Evaluable.Value(token, state.rawExpr)
            is Token.Operand.Variable -> Evaluable.Variable(token, state.rawExpr)
            is Token.Function -> parseFunction(token, state, prefix)
            is Token.Bracket.LeftRound -> {
                val result = expression(state)
                if (state.next() !is Token.Bracket.RightRound) {
                    throw EvaluableException("')' expected after expression")
                }
                result
            }
            is Token.StringTemplate.Start -> {
                val arguments = mutableListOf<Evaluable>()
                while (state.isNotAtEnd() && state.currentToken() !is Token.StringTemplate.End) {
                    if (state.currentToken() is Token.StringTemplate.StartOfExpression
                        || state.currentToken() is Token.StringTemplate.EndOfExpression) {
                        state.forward()
                        continue
                    }
                    arguments += expression(state)
                }
                if (state.next() !is Token.StringTemplate.End) {
                    throw EvaluableException("expected ''' at end of a string template")
                }
                Evaluable.StringTemplate(arguments, state.rawExpr)
            }
            else -> throw EvaluableException("Expression expected")
        }
    }

    private fun parseFunction(
        token: Token.Function, state: ParsingState, prefix: Evaluable?,
    ): Evaluable {
        if (state.next() !is Token.Bracket.LeftRound) {
            throw EvaluableException("'(' expected after function call")
        }
        val arguments = mutableListOf<Evaluable>()
        if (prefix != null) {
            arguments += prefix
        }
        while (state.currentToken() !is Token.Bracket.RightRound) {
            arguments += expression(state)
            if (state.currentToken() is Token.Function.ArgumentDelimiter) state.forward()
        }
        if (state.next() !is Token.Bracket.RightRound) {
            throw EvaluableException("expected ')' after a function call")
        }
        return if (prefix == null) {
            Evaluable.FunctionCall(token, arguments, state.rawExpr)
        } else {
            Evaluable.MethodCall(token, arguments, state.rawExpr)
        }
    }

    private data class ParsingState(private val tokens: List<Token>, val rawExpr: String) {
        var index: Int = 0
        fun currentToken() = tokens[index]
        fun next() = tokens[forward()]
        fun forward() = index++
        fun isNotAtEnd() = !isAtEnd()
        fun isAtEnd() = index >= tokens.size
    }
}