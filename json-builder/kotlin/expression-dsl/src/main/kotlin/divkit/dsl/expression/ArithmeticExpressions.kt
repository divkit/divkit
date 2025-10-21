@file:Suppress("ktlint", "unused")

package divkit.dsl.expression

//region Unary operations
@JvmName("unaryMinusInt")
operator fun Expression<Long>.unaryMinus(): Expression<Long> {
    return PrefixExpression(this, PrefixExpression.PrefixOperation.UNARY_MINUS)
}

@JvmName("unaryMinusDouble")
operator fun Expression<Double>.unaryMinus(): Expression<Double> {
    return PrefixExpression(this, PrefixExpression.PrefixOperation.UNARY_MINUS)
}

@JvmName("notBoolean")
operator fun Expression<Boolean>.not(): Expression<Boolean> {
    return PrefixExpression(this, PrefixExpression.PrefixOperation.NOT)
}
//endregion

//region Plus operator
@JvmName("plusExpStrExpStr")
operator fun Expression<String>.plus(other: Expression<String>): Expression<String> {
    return ConcatenationExpression(this, other)
}

@JvmName("plusExpIntExpInt")
operator fun Expression<Long>.plus(other: Expression<Long>): Expression<Long> {
    return ArithmeticExpression(this, other, ArithmeticExpression.ArithmeticOperation.PLUS)
}

@JvmName("plusExpIntInt")
operator fun Expression<Long>.plus(other: Int): Expression<Long> {
    return ArithmeticExpression(this, other.integer(), ArithmeticExpression.ArithmeticOperation.PLUS)
}

@JvmName("plusExpIntLong")
operator fun Expression<Long>.plus(other: Long): Expression<Long> {
    return ArithmeticExpression(this, other.integer(), ArithmeticExpression.ArithmeticOperation.PLUS)
}

@JvmName("plusExpDoubleExpDouble")
operator fun Expression<Double>.plus(other: Expression<Double>): Expression<Double> {
    return ArithmeticExpression(this, other, ArithmeticExpression.ArithmeticOperation.PLUS)
}

@JvmName("plusExpDoubleDouble")
operator fun Expression<Double>.plus(other: Double): Expression<Double> {
    return ArithmeticExpression(this, other.number(), ArithmeticExpression.ArithmeticOperation.PLUS)
}

@JvmName("plusExpDoubleFloat")
operator fun Expression<Double>.plus(other: Float): Expression<Double> {
    return ArithmeticExpression(this, other.number(), ArithmeticExpression.ArithmeticOperation.PLUS)
}

@JvmName("plusExpDoubleInt")
operator fun Expression<Double>.plus(other: Int): Expression<Double> {
    return ArithmeticExpression(this, other.number(), ArithmeticExpression.ArithmeticOperation.PLUS)
}

@JvmName("plusExpDoubleLong")
operator fun Expression<Double>.plus(other: Long): Expression<Double> {
    return ArithmeticExpression(this, other.number(), ArithmeticExpression.ArithmeticOperation.PLUS)
}

@JvmName("plusIntExpInt")
operator fun Int.plus(other: Expression<Long>): Expression<Long> {
    return ArithmeticExpression(this.integer(), other, ArithmeticExpression.ArithmeticOperation.PLUS)
}

@JvmName("plusLongExpInt")
operator fun Long.plus(other: Expression<Long>): Expression<Long> {
    return ArithmeticExpression(this.integer(), other, ArithmeticExpression.ArithmeticOperation.PLUS)
}

@JvmName("plusDoubleExpDouble")
operator fun Double.plus(other: Expression<Double>): Expression<Double> {
    return ArithmeticExpression(this.number(), other, ArithmeticExpression.ArithmeticOperation.PLUS)
}

@JvmName("plusFloatExpDouble")
operator fun Float.plus(other: Expression<Double>): Expression<Double> {
    return ArithmeticExpression(this.number(), other, ArithmeticExpression.ArithmeticOperation.PLUS)
}
//endregion

//region Minus operator
@JvmName("minusExpIntExpInt")
operator fun Expression<Long>.minus(other: Expression<Long>): Expression<Long> {
    return ArithmeticExpression(this, other, ArithmeticExpression.ArithmeticOperation.MINUS)
}

@JvmName("minusExpIntInt")
operator fun Expression<Long>.minus(other: Int): Expression<Long> {
    return ArithmeticExpression(this, other.integer(), ArithmeticExpression.ArithmeticOperation.MINUS)
}

@JvmName("minusExpIntLong")
operator fun Expression<Long>.minus(other: Long): Expression<Long> {
    return ArithmeticExpression(this, other.integer(), ArithmeticExpression.ArithmeticOperation.MINUS)
}

@JvmName("minusExpDoubleExpDouble")
operator fun Expression<Double>.minus(other: Expression<Double>): Expression<Double> {
    return ArithmeticExpression(this, other, ArithmeticExpression.ArithmeticOperation.MINUS)
}

@JvmName("minusExpDoubleDouble")
operator fun Expression<Double>.minus(other: Double): Expression<Double> {
    return ArithmeticExpression(this, other.number(), ArithmeticExpression.ArithmeticOperation.MINUS)
}

@JvmName("minusExpDoubleFloat")
operator fun Expression<Double>.minus(other: Float): Expression<Double> {
    return ArithmeticExpression(this, other.number(), ArithmeticExpression.ArithmeticOperation.MINUS)
}

@JvmName("minusExpDoubleLong")
operator fun Expression<Double>.minus(other: Long): Expression<Double> {
    return ArithmeticExpression(this, other.number(), ArithmeticExpression.ArithmeticOperation.MINUS)
}

@JvmName("minusExpDoubleInt")
operator fun Expression<Double>.minus(other: Int): Expression<Double> {
    return ArithmeticExpression(this, other.number(), ArithmeticExpression.ArithmeticOperation.MINUS)
}

@JvmName("minusIntExpInt")
operator fun Int.minus(other: Expression<Long>): Expression<Long> {
    return ArithmeticExpression(this.integer(), other, ArithmeticExpression.ArithmeticOperation.MINUS)
}

@JvmName("minusLongExpInt")
operator fun Long.minus(other: Expression<Long>): Expression<Long> {
    return ArithmeticExpression(this.integer(), other, ArithmeticExpression.ArithmeticOperation.MINUS)
}

@JvmName("minusDoubleExpDouble")
operator fun Double.minus(other: Expression<Double>): Expression<Double> {
    return ArithmeticExpression(this.number(), other, ArithmeticExpression.ArithmeticOperation.MINUS)
}

@JvmName("minusFloatExpDouble")
operator fun Float.minus(other: Expression<Double>): Expression<Double> {
    return ArithmeticExpression(this.number(), other, ArithmeticExpression.ArithmeticOperation.MINUS)
}
//endregion

//region Times operator
@JvmName("timesExpIntExpInt")
operator fun Expression<Long>.times(other: Expression<Long>): Expression<Long> {
    return ArithmeticExpression(this, other, ArithmeticExpression.ArithmeticOperation.TIMES)
}

@JvmName("timesExpIntInt")
operator fun Expression<Long>.times(other: Int): Expression<Long> {
    return ArithmeticExpression(this, other.integer(), ArithmeticExpression.ArithmeticOperation.TIMES)
}

@JvmName("timesExpIntLong")
operator fun Expression<Long>.times(other: Long): Expression<Long> {
    return ArithmeticExpression(this, other.integer(), ArithmeticExpression.ArithmeticOperation.TIMES)
}

@JvmName("timesExpDoubleExpDouble")
operator fun Expression<Double>.times(other: Expression<Double>): Expression<Double> {
    return ArithmeticExpression(this, other, ArithmeticExpression.ArithmeticOperation.TIMES)
}

@JvmName("timesExpDoubleDouble")
operator fun Expression<Double>.times(other: Double): Expression<Double> {
    return ArithmeticExpression(this, other.number(), ArithmeticExpression.ArithmeticOperation.TIMES)
}

@JvmName("timesExpDoubleFloat")
operator fun Expression<Double>.times(other: Float): Expression<Double> {
    return ArithmeticExpression(this, other.number(), ArithmeticExpression.ArithmeticOperation.TIMES)
}

@JvmName("timesExpDoubleLong")
operator fun Expression<Double>.times(other: Long): Expression<Double> {
    return ArithmeticExpression(this, other.number(), ArithmeticExpression.ArithmeticOperation.TIMES)
}

@JvmName("timesExpDoubleInt")
operator fun Expression<Double>.times(other: Int): Expression<Double> {
    return ArithmeticExpression(this, other.number(), ArithmeticExpression.ArithmeticOperation.TIMES)
}

@JvmName("timesIntExpInt")
operator fun Int.times(other: Expression<Long>): Expression<Long> {
    return ArithmeticExpression(this.integer(), other, ArithmeticExpression.ArithmeticOperation.TIMES)
}

@JvmName("timesLongExpInt")
operator fun Long.times(other: Expression<Long>): Expression<Long> {
    return ArithmeticExpression(this.integer(), other, ArithmeticExpression.ArithmeticOperation.TIMES)
}

@JvmName("timesDoubleExpDouble")
operator fun Double.times(other: Expression<Double>): Expression<Double> {
    return ArithmeticExpression(this.number(), other, ArithmeticExpression.ArithmeticOperation.TIMES)
}

@JvmName("timesFloatExpDouble")
operator fun Float.times(other: Expression<Double>): Expression<Double> {
    return ArithmeticExpression(this.number(), other, ArithmeticExpression.ArithmeticOperation.TIMES)
}
//endregion

//region Div operator
@JvmName("divExpIntExpInt")
operator fun Expression<Long>.div(other: Expression<Long>): Expression<Long> {
    return ArithmeticExpression(this, other, ArithmeticExpression.ArithmeticOperation.DIV)
}

@JvmName("divExpIntInt")
operator fun Expression<Long>.div(other: Int): Expression<Long> {
    return ArithmeticExpression(this, other.integer(), ArithmeticExpression.ArithmeticOperation.DIV)
}

@JvmName("divExpIntLong")
operator fun Expression<Long>.div(other: Long): Expression<Long> {
    return ArithmeticExpression(this, other.integer(), ArithmeticExpression.ArithmeticOperation.DIV)
}

@JvmName("divExpDoubleExpDouble")
operator fun Expression<Double>.div(other: Expression<Double>): Expression<Double> {
    return ArithmeticExpression(this, other, ArithmeticExpression.ArithmeticOperation.DIV)
}

@JvmName("divExpDoubleDouble")
operator fun Expression<Double>.div(other: Double): Expression<Double> {
    return ArithmeticExpression(this, other.number(), ArithmeticExpression.ArithmeticOperation.DIV)
}

@JvmName("divExpDoubleFloat")
operator fun Expression<Double>.div(other: Float): Expression<Double> {
    return ArithmeticExpression(this, other.number(), ArithmeticExpression.ArithmeticOperation.DIV)
}

@JvmName("divExpDoubleLong")
operator fun Expression<Double>.div(other: Long): Expression<Double> {
    return ArithmeticExpression(this, other.number(), ArithmeticExpression.ArithmeticOperation.DIV)
}

@JvmName("divExpDoubleInt")
operator fun Expression<Double>.div(other: Int): Expression<Double> {
    return ArithmeticExpression(this, other.number(), ArithmeticExpression.ArithmeticOperation.DIV)
}

@JvmName("divIntExpInt")
operator fun Int.div(other: Expression<Long>): Expression<Long> {
    return ArithmeticExpression(this.integer(), other, ArithmeticExpression.ArithmeticOperation.DIV)
}

@JvmName("divLongExpInt")
operator fun Long.div(other: Expression<Long>): Expression<Long> {
    return ArithmeticExpression(this.integer(), other, ArithmeticExpression.ArithmeticOperation.DIV)
}

@JvmName("divDoubleExpDouble")
operator fun Double.div(other: Expression<Double>): Expression<Double> {
    return ArithmeticExpression(this.number(), other, ArithmeticExpression.ArithmeticOperation.DIV)
}

@JvmName("divFloatExpDouble")
operator fun Float.div(other: Expression<Double>): Expression<Double> {
    return ArithmeticExpression(this.number(), other, ArithmeticExpression.ArithmeticOperation.DIV)
}
//endregion

//region Rem operator
@JvmName("remExpIntExpInt")
operator fun Expression<Long>.rem(other: Expression<Long>): Expression<Long> {
    return ArithmeticExpression(this, other, ArithmeticExpression.ArithmeticOperation.REM)
}

@JvmName("remExpIntInt")
operator fun Expression<Long>.rem(other: Int): Expression<Long> {
    return ArithmeticExpression(this, other.integer(), ArithmeticExpression.ArithmeticOperation.REM)
}

@JvmName("remExpIntLong")
operator fun Expression<Long>.rem(other: Long): Expression<Long> {
    return ArithmeticExpression(this, other.integer(), ArithmeticExpression.ArithmeticOperation.REM)
}

@JvmName("remExpDoubleExpDouble")
operator fun Expression<Double>.rem(other: Expression<Double>): Expression<Double> {
    return ArithmeticExpression(this, other, ArithmeticExpression.ArithmeticOperation.REM)
}

@JvmName("remExpDoubleDouble")
operator fun Expression<Double>.rem(other: Double): Expression<Double> {
    return ArithmeticExpression(this, other.number(), ArithmeticExpression.ArithmeticOperation.REM)
}

@JvmName("remExpDoubleFloat")
operator fun Expression<Double>.rem(other: Float): Expression<Double> {
    return ArithmeticExpression(this, other.number(), ArithmeticExpression.ArithmeticOperation.REM)
}

@JvmName("remExpDoubleLong")
operator fun Expression<Double>.rem(other: Long): Expression<Double> {
    return ArithmeticExpression(this, other.number(), ArithmeticExpression.ArithmeticOperation.REM)
}

@JvmName("remExpDoubleInt")
operator fun Expression<Double>.rem(other: Int): Expression<Double> {
    return ArithmeticExpression(this, other.number(), ArithmeticExpression.ArithmeticOperation.REM)
}

@JvmName("remIntExpInt")
operator fun Int.rem(other: Expression<Long>): Expression<Long> {
    return ArithmeticExpression(this.integer(), other, ArithmeticExpression.ArithmeticOperation.REM)
}

@JvmName("remLongExpInt")
operator fun Long.rem(other: Expression<Long>): Expression<Long> {
    return ArithmeticExpression(this.integer(), other, ArithmeticExpression.ArithmeticOperation.REM)
}

@JvmName("remDoubleExpDouble")
operator fun Double.rem(other: Expression<Double>): Expression<Double> {
    return ArithmeticExpression(this.number(), other, ArithmeticExpression.ArithmeticOperation.REM)
}

@JvmName("remFloatExpDouble")
operator fun Float.rem(other: Expression<Double>): Expression<Double> {
    return ArithmeticExpression(this.number(), other, ArithmeticExpression.ArithmeticOperation.REM)
}
//endregion
