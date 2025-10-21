@file:Suppress("ktlint", "unused")

package ru.yandex.home.mapi.div.toolkit

import divkit.dsl.Url
import divkit.dsl.expression.and as andImpl
import divkit.dsl.expression.arrayVariable as arrayVariableImpl
import divkit.dsl.expression.boolean as booleanImpl
import divkit.dsl.expression.booleanVariable as booleanVariableImpl
import divkit.dsl.expression.colorVariable as colorVariableImpl
import divkit.dsl.expression.compile as compileImpl
import divkit.dsl.expression.datetimeVariable as datetimeVariableImpl
import divkit.dsl.expression.dictVariable as dictVariableImpl
import divkit.dsl.expression.div as divImpl
import divkit.dsl.expression.equalTo as equalImpl
import divkit.dsl.expression.ifElse as ifElseImpl
import divkit.dsl.expression.integer as integerImpl
import divkit.dsl.expression.integerVariable as integerVariableImpl
import divkit.dsl.expression.lessThan as lessThanImpl
import divkit.dsl.expression.lessThanOrEqualTo as lessOrEqualThanImpl
import divkit.dsl.expression.literalExpression as literalExpressionImpl
import divkit.dsl.expression.minus as minusImpl
import divkit.dsl.expression.moreThan as moreThanImpl
import divkit.dsl.expression.moreThanOrEqualTo as moreOrEqualThanImpl
import divkit.dsl.expression.not as notImpl
import divkit.dsl.expression.notEqualTo as notEqualImpl
import divkit.dsl.expression.number as numberImpl
import divkit.dsl.expression.numberVariable as numberVariableImpl
import divkit.dsl.expression.or as orImpl
import divkit.dsl.expression.plus as plusImpl
import divkit.dsl.expression.rem as remImpl
import divkit.dsl.expression.string as stringImpl
import divkit.dsl.expression.stringVariable as stringVariableImpl
import divkit.dsl.expression.times as timesImpl
import divkit.dsl.expression.tryExpression as tryExpressionImpl
import divkit.dsl.expression.unaryMinus as unaryMinusImpl
import divkit.dsl.expression.urlVariable as urlVariableImpl

typealias Expression<T> = divkit.dsl.expression.Expression<T>

typealias Var<T> = divkit.dsl.expression.Var<T>

//region Common
fun String.booleanVariable(): Var<Boolean> = this.booleanVariableImpl()

fun String.stringVariable(): Var<String> = this.stringVariableImpl()

fun String.numberVariable(): Var<Double> = this.numberVariableImpl()

fun String.integerVariable(): Var<Long> = this.integerVariableImpl()

fun String.urlVariable(): Var<Url> = this.urlVariableImpl()

fun <T> String.dictVariable(): Var<Map<String, T>> = this.dictVariableImpl()

fun String.datetimeVariable(): Var<Long> = this.datetimeVariableImpl()

fun <T> String.arrayVariable(): Var<List<T>> = this.arrayVariableImpl()

fun String.colorVariable(): Var<String> = this.colorVariableImpl()

fun Int.integer(): Expression<Long> = this.integerImpl()

fun Long.integer(): Expression<Long> = this.integerImpl()

fun Int.number(): Expression<Double> = this.numberImpl()

fun Long.number(): Expression<Double> = this.numberImpl()

fun Double.number(): Expression<Double> = this.numberImpl()

fun Float.number(): Expression<Double> = this.numberImpl()

fun String.string(): Expression<String> = this.stringImpl()

fun Boolean.boolean(): Expression<Boolean> = this.booleanImpl()

fun <T> function(functionName: String, vararg expressionArguments: Expression<*>): Expression<T> = divkit.dsl.expression.function(functionName, *expressionArguments)

fun <T> String.literalExpression(): Expression<T> = this.literalExpressionImpl()

fun Expression<*>.compile() = this.compileImpl()
//endregion

//region Arithmetic expressions
@JvmName("unaryMinusInt")
operator fun Expression<Long>.unaryMinus(): Expression<Long> = this.unaryMinusImpl()

@JvmName("unaryMinusDouble")
operator fun Expression<Double>.unaryMinus(): Expression<Double> = this.unaryMinusImpl()

@JvmName("notBoolean")
operator fun Expression<Boolean>.not(): Expression<Boolean> = this.notImpl()

@JvmName("plusExpStrExpStr")
operator fun Expression<String>.plus(other: Expression<String>): Expression<String> = this.plusImpl(other)

@JvmName("plusExpIntExpInt")
operator fun Expression<Long>.plus(other: Expression<Long>): Expression<Long> = this.plusImpl(other)

@JvmName("plusExpIntInt")
operator fun Expression<Long>.plus(other: Int): Expression<Long> = this.plusImpl(other)

@JvmName("plusExpIntLong")
operator fun Expression<Long>.plus(other: Long): Expression<Long> = this.plusImpl(other)

@JvmName("plusExpDoubleExpDouble")
operator fun Expression<Double>.plus(other: Expression<Double>): Expression<Double> = this.plusImpl(other)

@JvmName("plusExpDoubleDouble")
operator fun Expression<Double>.plus(other: Double): Expression<Double> = this.plusImpl(other)

@JvmName("plusExpDoubleFloat")
operator fun Expression<Double>.plus(other: Float): Expression<Double> = this.plusImpl(other)

@JvmName("plusExpDoubleInt")
operator fun Expression<Double>.plus(other: Int): Expression<Double> = this.plusImpl(other)

@JvmName("plusExpDoubleLong")
operator fun Expression<Double>.plus(other: Long): Expression<Double> = this.plusImpl(other)

@JvmName("plusIntExpInt")
operator fun Int.plus(other: Expression<Long>): Expression<Long> = this.plusImpl(other)

@JvmName("plusLongExpInt")
operator fun Long.plus(other: Expression<Long>): Expression<Long> = this.plusImpl(other)

@JvmName("plusDoubleExpDouble")
operator fun Double.plus(other: Expression<Double>): Expression<Double> = this.plusImpl(other)

@JvmName("plusFloatExpDouble")
operator fun Float.plus(other: Expression<Double>): Expression<Double> = this.plusImpl(other)

@JvmName("minusExpIntExpInt")
operator fun Expression<Long>.minus(other: Expression<Long>): Expression<Long> = this.minusImpl(other)

@JvmName("minusExpIntInt")
operator fun Expression<Long>.minus(other: Int): Expression<Long> = this.minusImpl(other)

@JvmName("minusExpIntLong")
operator fun Expression<Long>.minus(other: Long): Expression<Long> = this.minusImpl(other)

@JvmName("minusExpDoubleExpDouble")
operator fun Expression<Double>.minus(other: Expression<Double>): Expression<Double> = this.minusImpl(other)

@JvmName("minusExpDoubleDouble")
operator fun Expression<Double>.minus(other: Double): Expression<Double> = this.minusImpl(other)

@JvmName("minusExpDoubleFloat")
operator fun Expression<Double>.minus(other: Float): Expression<Double> = this.minusImpl(other)

@JvmName("minusExpDoubleLong")
operator fun Expression<Double>.minus(other: Long): Expression<Double> = this.minusImpl(other)

@JvmName("minusExpDoubleInt")
operator fun Expression<Double>.minus(other: Int): Expression<Double> = this.minusImpl(other)

@JvmName("minusIntExpInt")
operator fun Int.minus(other: Expression<Long>): Expression<Long> = this.minusImpl(other)

@JvmName("minusLongExpInt")
operator fun Long.minus(other: Expression<Long>): Expression<Long> = this.minusImpl(other)

@JvmName("minusDoubleExpDouble")
operator fun Double.minus(other: Expression<Double>): Expression<Double> = this.minusImpl(other)

@JvmName("minusFloatExpDouble")
operator fun Float.minus(other: Expression<Double>): Expression<Double> = this.minusImpl(other)

@JvmName("timesExpIntExpInt")
operator fun Expression<Long>.times(other: Expression<Long>): Expression<Long> = this.timesImpl(other)

@JvmName("timesExpIntInt")
operator fun Expression<Long>.times(other: Int): Expression<Long> = this.timesImpl(other)

@JvmName("timesExpIntLong")
operator fun Expression<Long>.times(other: Long): Expression<Long> = this.timesImpl(other)

@JvmName("timesExpDoubleExpDouble")
operator fun Expression<Double>.times(other: Expression<Double>): Expression<Double> = this.timesImpl(other)

@JvmName("timesExpDoubleDouble")
operator fun Expression<Double>.times(other: Double): Expression<Double> = this.timesImpl(other)

@JvmName("timesExpDoubleFloat")
operator fun Expression<Double>.times(other: Float): Expression<Double> = this.timesImpl(other)

@JvmName("timesExpDoubleLong")
operator fun Expression<Double>.times(other: Long): Expression<Double> = this.timesImpl(other)

@JvmName("timesExpDoubleInt")
operator fun Expression<Double>.times(other: Int): Expression<Double> = this.timesImpl(other)

@JvmName("timesIntExpInt")
operator fun Int.times(other: Expression<Long>): Expression<Long> = this.timesImpl(other)

@JvmName("timesLongExpInt")
operator fun Long.times(other: Expression<Long>): Expression<Long> = this.timesImpl(other)

@JvmName("timesDoubleExpDouble")
operator fun Double.times(other: Expression<Double>): Expression<Double> = this.timesImpl(other)

@JvmName("timesFloatExpDouble")
operator fun Float.times(other: Expression<Double>): Expression<Double> = this.timesImpl(other)

@JvmName("divExpIntExpInt")
operator fun Expression<Long>.div(other: Expression<Long>): Expression<Long> = this.divImpl(other)

@JvmName("divExpIntInt")
operator fun Expression<Long>.div(other: Int): Expression<Long> = this.divImpl(other)

@JvmName("divExpIntLong")
operator fun Expression<Long>.div(other: Long): Expression<Long> = this.divImpl(other)

@JvmName("divExpDoubleExpDouble")
operator fun Expression<Double>.div(other: Expression<Double>): Expression<Double> = this.divImpl(other)

@JvmName("divExpDoubleDouble")
operator fun Expression<Double>.div(other: Double): Expression<Double> = this.divImpl(other)

@JvmName("divExpDoubleFloat")
operator fun Expression<Double>.div(other: Float): Expression<Double> = this.divImpl(other)

@JvmName("divExpDoubleLong")
operator fun Expression<Double>.div(other: Long): Expression<Double> = this.divImpl(other)

@JvmName("divExpDoubleInt")
operator fun Expression<Double>.div(other: Int): Expression<Double> = this.divImpl(other)

@JvmName("divIntExpInt")
operator fun Int.div(other: Expression<Long>): Expression<Long> = this.divImpl(other)

@JvmName("divLongExpInt")
operator fun Long.div(other: Expression<Long>): Expression<Long> = this.divImpl(other)

@JvmName("divDoubleExpDouble")
operator fun Double.div(other: Expression<Double>): Expression<Double> = this.divImpl(other)

@JvmName("divFloatExpDouble")
operator fun Float.div(other: Expression<Double>): Expression<Double> = this.divImpl(other)

@JvmName("remExpIntExpInt")
operator fun Expression<Long>.rem(other: Expression<Long>): Expression<Long> = this.remImpl(other)

@JvmName("remExpIntInt")
operator fun Expression<Long>.rem(other: Int): Expression<Long> = this.remImpl(other)

@JvmName("remExpIntLong")
operator fun Expression<Long>.rem(other: Long): Expression<Long> = this.remImpl(other)

@JvmName("remExpDoubleExpDouble")
operator fun Expression<Double>.rem(other: Expression<Double>): Expression<Double> = this.remImpl(other)

@JvmName("remExpDoubleDouble")
operator fun Expression<Double>.rem(other: Double): Expression<Double> = this.remImpl(other)

@JvmName("remExpDoubleFloat")
operator fun Expression<Double>.rem(other: Float): Expression<Double> = this.remImpl(other)

@JvmName("remExpDoubleLong")
operator fun Expression<Double>.rem(other: Long): Expression<Double> = this.remImpl(other)

@JvmName("remExpDoubleInt")
operator fun Expression<Double>.rem(other: Int): Expression<Double> = this.remImpl(other)

@JvmName("remIntExpInt")
operator fun Int.rem(other: Expression<Long>): Expression<Long> = this.remImpl(other)

@JvmName("remLongExpInt")
operator fun Long.rem(other: Expression<Long>): Expression<Long> = this.remImpl(other)

@JvmName("remDoubleExpDouble")
operator fun Double.rem(other: Expression<Double>): Expression<Double> = this.remImpl(other)

@JvmName("remFloatExpDouble")
operator fun Float.rem(other: Expression<Double>): Expression<Double> = this.remImpl(other)
//endregion

//region Boolean expressions
infix fun Expression<Boolean>.and(other: Expression<Boolean>): Expression<Boolean> = andImpl(other)

infix fun Boolean.and(other: Expression<Boolean>): Expression<Boolean> = andImpl(other)

infix fun Expression<Boolean>.and(other: Boolean): Expression<Boolean> = andImpl(other)

infix fun Expression<Boolean>.or(other: Expression<Boolean>): Expression<Boolean> = orImpl(other)

infix fun Boolean.or(other: Expression<Boolean>): Expression<Boolean> = orImpl(other)

infix fun Expression<Boolean>.or(other: Boolean): Expression<Boolean> = orImpl(other)
//endregion

//region Compare expressions
infix fun <T> Expression<T>.equal(other: Expression<T>): Expression<Boolean> = this.equalImpl(other)

infix fun <T> Expression<T>.notEqual(other: Expression<T>): Expression<Boolean> = this.notEqualImpl(other)

infix fun <T> Expression<T>.moreThan(other: Expression<T>): Expression<Boolean> = this.moreThanImpl(other)

infix fun <T> Expression<T>.moreOrEqualThan(other: Expression<T>): Expression<Boolean> = this.moreOrEqualThanImpl(other)

infix fun <T> Expression<T>.lessThan(other: Expression<T>): Expression<Boolean> = this.lessThanImpl(other)

infix fun <T> Expression<T>.lessOrEqualThan(other: Expression<T>): Expression<Boolean> = this.lessOrEqualThanImpl(other)

@JvmName("equalToIntExpLong")
infix fun Int.equal(other: Expression<Long>): Expression<Boolean> = this.equalImpl(other)

@JvmName("equalToLongExpLong")
infix fun Long.equal(other: Expression<Long>): Expression<Boolean> = this.equalImpl(other)

@JvmName("equalToIntExpDouble")
infix fun Int.equal(other: Expression<Double>): Expression<Boolean> = this.equalImpl(other)

@JvmName("equalToLongExpDouble")
infix fun Long.equal(other: Expression<Double>): Expression<Boolean> = this.equalImpl(other)

@JvmName("equalToFloatExpDouble")
infix fun Float.equal(other: Expression<Double>): Expression<Boolean> = this.equalImpl(other)

@JvmName("equalToDoubleExpDouble")
infix fun Double.equal(other: Expression<Double>): Expression<Boolean> = this.equalImpl(other)

@JvmName("equalToStringExpString")
infix fun String.equal(other: Expression<String>): Expression<Boolean> = this.equalImpl(other)

@JvmName("equalToBooleanExpBoolean")
infix fun Boolean.equal(other: Expression<Boolean>): Expression<Boolean> = this.equalImpl(other)

@JvmName("equalToExpLongInt")
infix fun Expression<Long>.equal(other: Int): Expression<Boolean> = this.equalImpl(other)

@JvmName("equalToExpLongLong")
infix fun Expression<Long>.equal(other: Long): Expression<Boolean> = this.equalImpl(other)

@JvmName("equalToExpDoubleDouble")
infix fun Expression<Double>.equal(other: Double): Expression<Boolean> = this.equalImpl(other)

@JvmName("equalToExpDoubleFloat")
infix fun Expression<Double>.equal(other: Float): Expression<Boolean> = this.equalImpl(other)

@JvmName("equalToExpDoubleLong")
infix fun Expression<Double>.equal(other: Long): Expression<Boolean> = this.equalImpl(other)

@JvmName("equalToExpDoubleInt")
infix fun Expression<Double>.equal(other: Int): Expression<Boolean> = this.equalImpl(other)

@JvmName("equalToExpStringString")
infix fun Expression<String>.equal(other: String): Expression<Boolean> = this.equalImpl(other)

@JvmName("equalToExpBooleanBoolean")
infix fun Expression<Boolean>.equal(other: Boolean): Expression<Boolean> = this.equalImpl(other)

@JvmName("notEqualToIntExpLong")
infix fun Int.notEqual(other: Expression<Long>): Expression<Boolean> = this.notEqualImpl(other)

@JvmName("notEqualToLongExpLong")
infix fun Long.notEqual(other: Expression<Long>): Expression<Boolean> = this.notEqualImpl(other)

@JvmName("notEqualToIntExpDouble")
infix fun Int.notEqual(other: Expression<Double>): Expression<Boolean> = this.notEqualImpl(other)

@JvmName("notEqualToLongExpDouble")
infix fun Long.notEqual(other: Expression<Double>): Expression<Boolean> = this.notEqualImpl(other)

@JvmName("notEqualToFloatExpDouble")
infix fun Float.notEqual(other: Expression<Double>): Expression<Boolean> = this.notEqualImpl(other)

@JvmName("notEqualToDoubleExpDouble")
infix fun Double.notEqual(other: Expression<Double>): Expression<Boolean> = this.notEqualImpl(other)

@JvmName("notEqualToStringExpString")
infix fun String.notEqual(other: Expression<String>): Expression<Boolean> = this.notEqualImpl(other)

@JvmName("notEqualToBooleanExpBoolean")
infix fun Boolean.notEqual(other: Expression<Boolean>): Expression<Boolean> = this.notEqualImpl(other)

@JvmName("notEqualToExpLongInt")
infix fun Expression<Long>.notEqual(other: Int): Expression<Boolean> = this.notEqualImpl(other)

@JvmName("notEqualToExpLongLong")
infix fun Expression<Long>.notEqual(other: Long): Expression<Boolean> = this.notEqualImpl(other)

@JvmName("notEqualToExpDoubleDouble")
infix fun Expression<Double>.notEqual(other: Double): Expression<Boolean> = this.notEqualImpl(other)

@JvmName("notEqualToExpDoubleFloat")
infix fun Expression<Double>.notEqual(other: Float): Expression<Boolean> = this.notEqualImpl(other)

@JvmName("notEqualToExpDoubleLong")
infix fun Expression<Double>.notEqual(other: Long): Expression<Boolean> = this.notEqualImpl(other)

@JvmName("notEqualToExpDoubleInt")
infix fun Expression<Double>.notEqual(other: Int): Expression<Boolean> = this.notEqualImpl(other)

@JvmName("notEqualToExpStringString")
infix fun Expression<String>.notEqual(other: String): Expression<Boolean> = this.notEqualImpl(other)

@JvmName("notEqualToExpBooleanBoolean")
infix fun Expression<Boolean>.notEqual(other: Boolean): Expression<Boolean> = this.notEqualImpl(other)

@JvmName("moreThanIntExpLong")
infix fun Int.moreThan(other: Expression<Long>): Expression<Boolean> = this.moreThanImpl(other)

@JvmName("moreThanLongExpLong")
infix fun Long.moreThan(other: Expression<Long>): Expression<Boolean> = this.moreThanImpl(other)

@JvmName("moreThanIntExpDouble")
infix fun Int.moreThan(other: Expression<Double>): Expression<Boolean> = this.moreThanImpl(other)

@JvmName("moreThanLongExpDouble")
infix fun Long.moreThan(other: Expression<Double>): Expression<Boolean> = this.moreThanImpl(other)

@JvmName("moreThanFloatExpDouble")
infix fun Float.moreThan(other: Expression<Double>): Expression<Boolean> = this.moreThanImpl(other)

@JvmName("moreThanDoubleExpDouble")
infix fun Double.moreThan(other: Expression<Double>): Expression<Boolean> = this.moreThanImpl(other)

@JvmName("moreThanExpLongInt")
infix fun Expression<Long>.moreThan(other: Int): Expression<Boolean> = this.moreThanImpl(other)

@JvmName("moreThanExpLongLong")
infix fun Expression<Long>.moreThan(other: Long): Expression<Boolean> = this.moreThanImpl(other)

@JvmName("moreThanExpDoubleDouble")
infix fun Expression<Double>.moreThan(other: Double): Expression<Boolean> = this.moreThanImpl(other)

@JvmName("moreThanExpDoubleFloat")
infix fun Expression<Double>.moreThan(other: Float): Expression<Boolean> = this.moreThanImpl(other)

@JvmName("moreThanExpDoubleLong")
infix fun Expression<Double>.moreThan(other: Long): Expression<Boolean> = this.moreThanImpl(other)

@JvmName("moreThanExpDoubleInt")
infix fun Expression<Double>.moreThan(other: Int): Expression<Boolean> = this.moreThanImpl(other)

@JvmName("moreThanOrEqualToIntExpLong")
infix fun Int.moreOrEqualThan(other: Expression<Long>): Expression<Boolean> = this.moreOrEqualThanImpl(other)

@JvmName("moreThanOrEqualToLongExpLong")
infix fun Long.moreOrEqualThan(other: Expression<Long>): Expression<Boolean> = this.moreOrEqualThanImpl(other)

@JvmName("moreThanOrEqualToIntExpDouble")
infix fun Int.moreOrEqualThan(other: Expression<Double>): Expression<Boolean> = this.moreOrEqualThanImpl(other)

@JvmName("moreThanOrEqualToLongExpDouble")
infix fun Long.moreOrEqualThan(other: Expression<Double>): Expression<Boolean> = this.moreOrEqualThanImpl(other)

@JvmName("moreThanOrEqualToFloatExpDouble")
infix fun Float.moreOrEqualThan(other: Expression<Double>): Expression<Boolean> = this.moreOrEqualThanImpl(other)

@JvmName("moreThanOrEqualToDoubleExpDouble")
infix fun Double.moreOrEqualThan(other: Expression<Double>): Expression<Boolean> = this.moreOrEqualThanImpl(other)

@JvmName("moreThanOrEqualToExpLongInt")
infix fun Expression<Long>.moreOrEqualThan(other: Int): Expression<Boolean> = this.moreOrEqualThanImpl(other)

@JvmName("moreThanOrEqualToExpLongLong")
infix fun Expression<Long>.moreOrEqualThan(other: Long): Expression<Boolean> = this.moreOrEqualThanImpl(other)

@JvmName("moreThanOrEqualToExpDoubleDouble")
infix fun Expression<Double>.moreOrEqualThan(other: Double): Expression<Boolean> = this.moreOrEqualThanImpl(other)

@JvmName("moreThanOrEqualToExpDoubleFloat")
infix fun Expression<Double>.moreOrEqualThan(other: Float): Expression<Boolean> = this.moreOrEqualThanImpl(other)

@JvmName("moreThanOrEqualToExpDoubleInt")
infix fun Expression<Double>.moreOrEqualThan(other: Int): Expression<Boolean> = this.moreOrEqualThanImpl(other)

@JvmName("moreThanOrEqualToExpDoubleLong")
infix fun Expression<Double>.moreOrEqualThan(other: Long): Expression<Boolean> = this.moreOrEqualThanImpl(other)

@JvmName("lessThanIntExpLong")
infix fun Int.lessThan(other: Expression<Long>): Expression<Boolean> = this.lessThanImpl(other)

@JvmName("lessThanLongExpLong")
infix fun Long.lessThan(other: Expression<Long>): Expression<Boolean> = this.lessThanImpl(other)

@JvmName("lessThanIntExpDouble")
infix fun Int.lessThan(other: Expression<Double>): Expression<Boolean> = this.lessThanImpl(other)

@JvmName("lessThanLongExpDouble")
infix fun Long.lessThan(other: Expression<Double>): Expression<Boolean> = this.lessThanImpl(other)

@JvmName("lessThanFloatExpDouble")
infix fun Float.lessThan(other: Expression<Double>): Expression<Boolean> = this.lessThanImpl(other)

@JvmName("lessThanDoubleExpDouble")
infix fun Double.lessThan(other: Expression<Double>): Expression<Boolean> = this.lessThanImpl(other)

@JvmName("lessThanExpLongInt")
infix fun Expression<Long>.lessThan(other: Int): Expression<Boolean> = this.lessThanImpl(other)

@JvmName("lessThanExpLongLong")
infix fun Expression<Long>.lessThan(other: Long): Expression<Boolean> = this.lessThanImpl(other)

@JvmName("lessThanExpDoubleDouble")
infix fun Expression<Double>.lessThan(other: Double): Expression<Boolean> = this.lessThanImpl(other)

@JvmName("lessThanExpDoubleFloat")
infix fun Expression<Double>.lessThan(other: Float): Expression<Boolean> = this.lessThanImpl(other)

@JvmName("lessThanExpDoubleLong")
infix fun Expression<Double>.lessThan(other: Long): Expression<Boolean> = this.lessThanImpl(other)

@JvmName("lessThanExpDoubleInt")
infix fun Expression<Double>.lessThan(other: Int): Expression<Boolean> = this.lessThanImpl(other)

@JvmName("lessThanOrEqualIntExpLong")
infix fun Int.lessOrEqualThan(other: Expression<Long>): Expression<Boolean> = this.lessOrEqualThanImpl(other)

@JvmName("lessThanOrEqualLongExpLong")
infix fun Long.lessOrEqualThan(other: Expression<Long>): Expression<Boolean> = this.lessOrEqualThanImpl(other)

@JvmName("lessThanOrEqualIntExpDouble")
infix fun Int.lessOrEqualThan(other: Expression<Double>): Expression<Boolean> = this.lessOrEqualThanImpl(other)

@JvmName("lessThanOrEqualLongExpDouble")
infix fun Long.lessOrEqualThan(other: Expression<Double>): Expression<Boolean> = this.lessOrEqualThanImpl(other)

@JvmName("lessThanOrEqualFloatExpDouble")
infix fun Float.lessOrEqualThan(other: Expression<Double>): Expression<Boolean> = this.lessOrEqualThanImpl(other)

@JvmName("lessThanOrEqualDoubleExpDouble")
infix fun Double.lessOrEqualThan(other: Expression<Double>): Expression<Boolean> = this.lessOrEqualThanImpl(other)

@JvmName("lessThanOrEqualExpLongInt")
infix fun Expression<Long>.lessOrEqualThan(other: Int): Expression<Boolean> = this.lessOrEqualThanImpl(other)

@JvmName("lessThanOrEqualExpLongLong")
infix fun Expression<Long>.lessOrEqualThan(other: Long): Expression<Boolean> = this.lessOrEqualThanImpl(other)

@JvmName("lessThanOrEqualExpDoubleDouble")
infix fun Expression<Double>.lessOrEqualThan(other: Double): Expression<Boolean> = this.lessOrEqualThanImpl(other)

@JvmName("lessThanOrEqualExpDoubleFloat")
infix fun Expression<Double>.lessOrEqualThan(other: Float): Expression<Boolean> = this.lessOrEqualThanImpl(other)

@JvmName("lessThanOrEqualExpDoubleLong")
infix fun Expression<Double>.lessOrEqualThan(other: Long): Expression<Boolean> = this.lessOrEqualThanImpl(other)

@JvmName("lessThanOrEqualExpDoubleInt")
infix fun Expression<Double>.lessOrEqualThan(other: Int): Expression<Boolean> = this.lessOrEqualThanImpl(other)
//endregion

//region Try expressions
@JvmName("tryExpressionIntExpLong")
infix fun Int.tryExpression(onFail: Expression<Long>): Expression<Long> = this.tryExpressionImpl(onFail)

@JvmName("tryExpressionIntInt")
infix fun Int.tryExpression(onFail: Int): Expression<Long> = this.tryExpressionImpl(onFail)

@JvmName("tryExpressionExpLongExpLong")
infix fun Expression<Long>.tryExpression(onFail: Expression<Long>): Expression<Long> = this.tryExpressionImpl(onFail)

@JvmName("tryExpressionExpLongInt")
infix fun Expression<Long>.tryExpression(onFail: Int): Expression<Long> = this.tryExpressionImpl(onFail)

@JvmName("tryExpressionLongExpLong")
infix fun Long.tryExpression(onFail: Expression<Long>): Expression<Long> = this.tryExpressionImpl(onFail)

@JvmName("tryExpressionLongLong")
infix fun Long.tryExpression(onFail: Long): Expression<Long> = this.tryExpressionImpl(onFail)

@JvmName("tryExpressionFloatExpDouble")
infix fun Float.tryExpression(onFail: Expression<Double>): Expression<Double> = this.tryExpressionImpl(onFail)

@JvmName("tryExpressionFloatFloat")
infix fun Float.tryExpression(onFail: Float): Expression<Double> = this.tryExpressionImpl(onFail)

@JvmName("tryExpressionExpDoubleExpDouble")
infix fun Expression<Double>.tryExpression(onFail: Expression<Double>): Expression<Double> = this.tryExpressionImpl(onFail)

@JvmName("tryExpressionExpDoubleDouble")
infix fun Expression<Double>.tryExpression(onFail: Double): Expression<Double> = this.tryExpressionImpl(onFail)

@JvmName("tryExpressionDoubleExpDouble")
infix fun Double.tryExpression(onFail: Expression<Double>): Expression<Double> = this.tryExpressionImpl(onFail)

@JvmName("tryExpressionDoubleDouble")
infix fun Double.tryExpression(onFail: Double): Expression<Double> = this.tryExpressionImpl(onFail)

@JvmName("tryExpressionExpStringExpString")
infix fun Expression<String>.tryExpression(onFail: Expression<String>): Expression<String> = this.tryExpressionImpl(onFail)

@JvmName("tryExpressionExpStringString")
infix fun Expression<String>.tryExpression(onFail: String): Expression<String> = this.tryExpressionImpl(onFail)

@JvmName("tryExpressionStringExpString")
infix fun String.tryExpression(onFail: Expression<String>): Expression<String> = this.tryExpressionImpl(onFail)

@JvmName("tryExpressionStringString")
infix fun String.tryExpression(onFail: String): Expression<String> = this.tryExpressionImpl(onFail)

@JvmName("tryExpressionExpBooleanExpBoolean")
infix fun Expression<Boolean>.tryExpression(onFail: Expression<Boolean>): Expression<Boolean> = this.tryExpressionImpl(onFail)

@JvmName("tryExpressionExpBooleanBoolean")
infix fun Expression<Boolean>.tryExpression(onFail: Boolean): Expression<Boolean> = this.tryExpressionImpl(onFail)

@JvmName("tryExpressionBooleanExpBoolean")
infix fun Boolean.tryExpression(onFail: Expression<Boolean>): Expression<Boolean> = this.tryExpressionImpl(onFail)

@JvmName("tryExpressionBooleanBoolean")
infix fun Boolean.tryExpression(onFail: Boolean): Expression<Boolean> = this.tryExpressionImpl(onFail)
//endregion

//region Ternary expressions
fun <T> Expression<Boolean>.ifElse(onMatch: Expression<T>, onMismatch: Expression<T>): Expression<T> = ifElseImpl(onMatch, onMismatch)

fun Expression<Boolean>.ifElse(onMatch: Expression<Long>, onMismatch: Int): Expression<Long> = ifElseImpl(onMatch, onMismatch)

fun Expression<Boolean>.ifElse(onMatch: Int, onMismatch: Expression<Long>): Expression<Long> = ifElseImpl(onMatch, onMismatch)

fun Expression<Boolean>.ifElse(onMatch: Int, onMismatch: Int): Expression<Long> = ifElseImpl(onMatch, onMismatch)

fun Expression<Boolean>.ifElse(onMatch: Expression<Long>, onMismatch: Long): Expression<Long> = ifElseImpl(onMatch, onMismatch)

fun Expression<Boolean>.ifElse(onMatch: Long, onMismatch: Expression<Long>): Expression<Long> = ifElseImpl(onMatch, onMismatch)

fun Expression<Boolean>.ifElse(onMatch: Long, onMismatch: Long): Expression<Long> = ifElseImpl(onMatch, onMismatch)

fun Expression<Boolean>.ifElse(onMatch: Expression<Double>, onMismatch: Float): Expression<Double> = ifElseImpl(onMatch, onMismatch)

fun Expression<Boolean>.ifElse(onMatch: Float, onMismatch: Expression<Double>): Expression<Double> = ifElseImpl(onMatch, onMismatch)

fun Expression<Boolean>.ifElse(onMatch: Float, onMismatch: Float): Expression<Double> = ifElseImpl(onMatch, onMismatch)

fun Expression<Boolean>.ifElse(onMatch: Expression<Double>, onMismatch: Double): Expression<Double> = ifElseImpl(onMatch, onMismatch)

fun Expression<Boolean>.ifElse(onMatch: Double, onMismatch: Expression<Double>): Expression<Double> = ifElseImpl(onMatch, onMismatch)

fun Expression<Boolean>.ifElse(onMatch: Double, onMismatch: Double): Expression<Double> = ifElseImpl(onMatch, onMismatch)

fun Expression<Boolean>.ifElse(onMatch: Expression<String>, onMismatch: String): Expression<String> = ifElseImpl(onMatch, onMismatch)

fun Expression<Boolean>.ifElse(onMatch: String, onMismatch: Expression<String>): Expression<String> = ifElseImpl(onMatch, onMismatch)

fun Expression<Boolean>.ifElse(onMatch: String, onMismatch: String): Expression<String> = ifElseImpl(onMatch, onMismatch)

fun Expression<Boolean>.ifElse(onMatch: Expression<Boolean>, onMismatch: Boolean): Expression<Boolean> = ifElseImpl(onMatch, onMismatch)

fun Expression<Boolean>.ifElse(onMatch: Boolean, onMismatch: Expression<Boolean>): Expression<Boolean> = ifElseImpl(onMatch, onMismatch)

fun Expression<Boolean>.ifElse(onMatch: Boolean, onMismatch: Boolean): Expression<Boolean> = ifElseImpl(onMatch, onMismatch)

fun Expression<Boolean>.ifElse(onMatch: Int, onMismatch: Long): Expression<Long> = ifElseImpl(onMatch, onMismatch)

fun Expression<Boolean>.ifElse(onMatch: Long, onMismatch: Int): Expression<Long> = ifElseImpl(onMatch, onMismatch)

fun Expression<Boolean>.ifElse(onMatch: Float, onMismatch: Int): Expression<Double> = ifElseImpl(onMatch, onMismatch)

fun Expression<Boolean>.ifElse(onMatch: Float, onMismatch: Long): Expression<Double> = ifElseImpl(onMatch, onMismatch)

fun Expression<Boolean>.ifElse(onMatch: Float, onMismatch: Double): Expression<Double> = ifElseImpl(onMatch, onMismatch)

fun Expression<Boolean>.ifElse(onMatch: Double, onMismatch: Int): Expression<Double> = ifElseImpl(onMatch, onMismatch)

fun Expression<Boolean>.ifElse(onMatch: Double, onMismatch: Long): Expression<Double> = ifElseImpl(onMatch, onMismatch)

fun Expression<Boolean>.ifElse(onMatch: Double, onMismatch: Float): Expression<Double> = ifElseImpl(onMatch, onMismatch)
//endregion
