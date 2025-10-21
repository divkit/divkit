@file:Suppress("unused", "PackageDirectoryMismatch")

package ru.yandex.home.mapi.div.toolkit

import divkit.dsl.Color
import divkit.dsl.EnumValue
import divkit.dsl.Variable
import divkit.dsl.core.ExpressionProperty
import divkit.dsl.expression.Expression
import divkit.dsl.expression.Var
import divkit.dsl.expression.compile
import divkit.dsl.expression.string
import divkit.dsl.scope.DivScope
import divkit.dsl.expression.divanExpression as divanExpressionImpl
import divkit.dsl.expression.divkitColorVariable as divkitColorVariableImpl
import divkit.dsl.expression.divkitVariable as divkitVariableImpl

typealias ActionEnumValue = divkit.dsl.expression.ActionEnumValue

typealias OverflowActionEnum = divkit.dsl.expression.OverflowActionEnum

typealias ClampOverflowActionEnumValue = divkit.dsl.expression.ClampOverflowActionEnumValue

typealias RingOverflowActionEnumValue = divkit.dsl.expression.RingOverflowActionEnumValue

val DivScope.clamp: ClampOverflowActionEnumValue
    get() = ClampOverflowActionEnumValue

val DivScope.ring: RingOverflowActionEnumValue
    get() = RingOverflowActionEnumValue

typealias VideoActionEnum = divkit.dsl.expression.VideoActionEnum

typealias PauseVideoActionEnum = divkit.dsl.expression.PauseVideoActionEnum

typealias StartVideoActionEnum = divkit.dsl.expression.StartVideoActionEnum

val DivScope.pause: PauseVideoActionEnum
    get() = PauseVideoActionEnum

val DivScope.start: StartVideoActionEnum
    get() = StartVideoActionEnum

typealias OpenModeActionEnum = divkit.dsl.expression.OpenModeActionEnum

typealias PopupOpenModeActionEnum = divkit.dsl.expression.PopupOpenModeActionEnum

typealias FullscreenOpenModeActionEnum = divkit.dsl.expression.FullscreenOpenModeActionEnum

val DivScope.popup: PopupOpenModeActionEnum
    get() = PopupOpenModeActionEnum

val DivScope.fullscreen: FullscreenOpenModeActionEnum
    get() = FullscreenOpenModeActionEnum

fun EnumValue.string(): Expression<String> = this.serialized.string()

fun ActionEnumValue.string(): Expression<String> = this.value.string()

fun DivScope.divkitColorVariable(colorVariable: Var<String>, value: String): Variable =
    this.divkitColorVariableImpl(colorVariable, value)

fun DivScope.divkitVariable(variable: Var<Long>, value: Int): Variable = divkitVariableImpl(variable, value)

fun DivScope.divkitVariable(variable: Var<Double>, value: Float): Variable = divkitVariableImpl(variable, value)

inline fun <reified T> DivScope.divkitVariable(variable: Var<T>, value: T): Variable = divkitVariableImpl(variable, value)

fun <T> Expression<T>.expressionArrayElement() = divkit.dsl.core.expressionArrayElement<T>(compile())

fun Expression<String>.colorExpressionArrayElement() = divkit.dsl.core.expressionArrayElement<Color>(compile())

fun <T : Any> Expression<*>.divanExpression(): ExpressionProperty<T> = this.divanExpressionImpl()

@JvmName("divanExpressionWrapper")
fun <T : Any> divanExpression(expression: Expression<*>): ExpressionProperty<T> = divkit.dsl.expression.divanExpression(expression)
