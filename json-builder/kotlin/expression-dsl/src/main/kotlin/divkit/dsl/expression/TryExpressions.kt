@file:Suppress("ktlint", "unused")

package divkit.dsl.expression

//region Integer
@JvmName("tryExpressionIntExpLong")
infix fun Int.tryExpression(onFail: Expression<Long>): Expression<Long> =
    TryExpression(this.integer(), onFail)

@JvmName("tryExpressionIntInt")
infix fun Int.tryExpression(onFail: Int): Expression<Long> =
    TryExpression(this.integer(), onFail.integer())

@JvmName("tryExpressionExpLongExpLong")
infix fun Expression<Long>.tryExpression(onFail: Expression<Long>): Expression<Long> =
    TryExpression(this, onFail)

@JvmName("tryExpressionExpLongInt")
infix fun Expression<Long>.tryExpression(onFail: Int): Expression<Long> =
    TryExpression(this, onFail.integer())

@JvmName("tryExpressionLongExpLong")
infix fun Long.tryExpression(onFail: Expression<Long>): Expression<Long> =
    TryExpression(this.integer(), onFail)

@JvmName("tryExpressionLongLong")
infix fun Long.tryExpression(onFail: Long): Expression<Long> =
    TryExpression(this.integer(), onFail.integer())
//endregion

//region Number
@JvmName("tryExpressionFloatExpDouble")
infix fun Float.tryExpression(onFail: Expression<Double>): Expression<Double> =
    TryExpression(this.number(), onFail)

@JvmName("tryExpressionFloatFloat")
infix fun Float.tryExpression(onFail: Float): Expression<Double> =
    TryExpression(this.number(), onFail.number())

@JvmName("tryExpressionExpDoubleExpDouble")
infix fun Expression<Double>.tryExpression(onFail: Expression<Double>): Expression<Double> =
    TryExpression(this, onFail)

@JvmName("tryExpressionExpDoubleDouble")
infix fun Expression<Double>.tryExpression(onFail: Double): Expression<Double> =
    TryExpression(this, onFail.number())

@JvmName("tryExpressionDoubleExpDouble")
infix fun Double.tryExpression(onFail: Expression<Double>): Expression<Double> =
    TryExpression(this.number(), onFail)

@JvmName("tryExpressionDoubleDouble")
infix fun Double.tryExpression(onFail: Double): Expression<Double> =
    TryExpression(this.number(), onFail.number())
//endregion

//region String
@JvmName("tryExpressionExpStringExpString")
infix fun Expression<String>.tryExpression(onFail: Expression<String>): Expression<String> =
    TryExpression(this, onFail)

@JvmName("tryExpressionExpStringString")
infix fun Expression<String>.tryExpression(onFail: String): Expression<String> =
    TryExpression(this, onFail.string())

@JvmName("tryExpressionStringExpString")
infix fun String.tryExpression(onFail: Expression<String>): Expression<String> =
    TryExpression(this.stringVariable(), onFail)

@JvmName("tryExpressionStringString")
infix fun String.tryExpression(onFail: String): Expression<String> =
    TryExpression(this.string(), onFail.string())
//endregion

//region Boolean
@JvmName("tryExpressionExpBooleanExpBoolean")
infix fun Expression<Boolean>.tryExpression(onFail: Expression<Boolean>): Expression<Boolean> =
    TryExpression(this, onFail)

@JvmName("tryExpressionExpBooleanBoolean")
infix fun Expression<Boolean>.tryExpression(onFail: Boolean): Expression<Boolean> =
    TryExpression(this, onFail.boolean())

@JvmName("tryExpressionBooleanExpBoolean")
infix fun Boolean.tryExpression(onFail: Expression<Boolean>): Expression<Boolean> =
    TryExpression(this.boolean(), onFail)

@JvmName("tryExpressionBooleanBoolean")
infix fun Boolean.tryExpression(onFail: Boolean): Expression<Boolean> =
    TryExpression(this.boolean(), onFail.boolean())
//endregion
