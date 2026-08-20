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
            data object Plus : Unary {
                override fun toString() = "+"
            }

            data object Minus : Unary {
                override fun toString() = "-"
            }

            data object Not : Unary {
                override fun toString() = "!"
            }
        }

        sealed interface Binary : Operator {
            sealed interface Sum : Binary {
                data object Plus : Sum {
                    override fun toString() = "+"
                }

                data object Minus : Sum {
                    override fun toString() = "-"
                }
            }

            sealed interface Factor : Binary {
                data object Multiplication : Factor {
                    override fun toString() = "*"
                }

                data object Division : Factor {
                    override fun toString() = "/"
                }

                data object Modulo : Factor {
                    override fun toString() = "%"
                }
            }

            // Exponent
            data object Power : Binary {
                override fun toString() = "^"
            }

            sealed interface Logical : Binary {
                data object And : Logical {
                    override fun toString() = "&&"
                }

                data object Or : Logical {
                    override fun toString() = "||"
                }
            }

            sealed interface Comparison : Binary {
                data object Greater : Comparison {
                    override fun toString() = ">"
                }

                data object GreaterOrEqual : Comparison {
                    override fun toString() = ">="
                }

                data object Less : Comparison {
                    override fun toString() = "<"
                }

                data object LessOrEqual : Comparison {
                    override fun toString() = "<="
                }
            }

            sealed interface Equality : Binary {
                data object Equal : Equality {
                    override fun toString() = "=="
                }

                data object NotEqual : Equality {
                    override fun toString() = "!="
                }
            }
        }

        data object Try : Operator {
            override fun toString() = "!:"
        }

        data object Dot : Operator {
            override fun toString() = "."
        }

        // Ternary
        data object TernaryIf : Operator {
            override fun toString() = "?"
        }
        data object TernaryElse : Operator {
            override fun toString() = ":"
        }
        data object TernaryIfElse : Operator
    }

    data class Function(val name: String) : Token {
        data object ArgumentDelimiter : Token {
            override fun toString() = ","
        }
    }

    data object StringTemplate : Operand {
        data object Start : Token
        data object End : Token
        data object StartOfExpression : Token
        data object EndOfExpression : Token
    }

    sealed interface Bracket : Token {
        data object LeftRound : Bracket {
            override fun toString() = "("
        }
        data object RightRound : Bracket {
            override fun toString() = ")"
        }
    }
}
