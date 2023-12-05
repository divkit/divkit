package com.yandex.div.evaluable.internal

internal sealed interface Token {
    sealed interface Operand : Token {
        sealed interface Literal : Operand {
            @JvmInline
            value class Num(val value: Number) : Literal
            @JvmInline
            value class Bool(val value: Boolean) : Literal
            @JvmInline
            value class Str(val value: String) : Literal
        }
        @JvmInline
        value class Variable(val name: String) : Operand
    }

    sealed interface Operator : Token {

        sealed interface Unary : Operator {
            object Plus : Unary {
                override fun toString() = "+"
            }

            object Minus : Unary {
                override fun toString() = "-"
            }

            object Not : Unary {
                override fun toString() = "!"
            }
        }

        sealed interface Binary : Operator {
            sealed interface Sum : Binary {
                object Plus : Sum {
                    override fun toString() = "+"
                }

                object Minus : Sum {
                    override fun toString() = "-"
                }
            }

            sealed interface Factor : Binary {
                object Multiplication : Factor {
                    override fun toString() = "*"
                }

                object Division : Factor {
                    override fun toString() = "/"
                }

                object Modulo : Factor {
                    override fun toString() = "%"
                }
            }

            // Exponent
            object Power : Binary {
                override fun toString() = "^"
            }

            sealed interface Logical : Binary {
                object And : Logical {
                    override fun toString() = "&&"
                }

                object Or : Logical {
                    override fun toString() = "||"
                }
            }

            sealed interface Comparison : Binary {
                object Greater : Comparison {
                    override fun toString() = ">"
                }

                object GreaterOrEqual : Comparison {
                    override fun toString() = ">="
                }

                object Less : Comparison {
                    override fun toString() = "<"
                }

                object LessOrEqual : Comparison {
                    override fun toString() = "<="
                }
            }

            sealed interface Equality : Binary {
                object Equal : Equality {
                    override fun toString() = "=="
                }

                object NotEqual : Equality {
                    override fun toString() = "!="
                }
            }
        }

        object Try : Operator {
            override fun toString() = "!:"
        }

        // Ternary
        object TernaryIf : Operator {
            override fun toString() = "?"
        }
        object TernaryElse : Operator {
            override fun toString() = ":"
        }
        object TernaryIfElse : Operator
    }

    data class Function(val name: String) : Token {
        object ArgumentDelimiter : Token {
            override fun toString() = ","
        }
    }

    object StringTemplate : Operand {
        object Start : Token
        object End : Token
        object StartOfExpression : Token
        object EndOfExpression : Token
    }

    sealed interface Bracket : Token {
        object LeftRound : Bracket {
            override fun toString() = "("
        }
        object RightRound : Bracket {
            override fun toString() = ")"
        }
    }
}