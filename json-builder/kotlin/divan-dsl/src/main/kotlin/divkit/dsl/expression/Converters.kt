@file:Suppress("unused")

package divkit.dsl.expression

import divkit.dsl.Color
import divkit.dsl.Url
import divkit.dsl.Variable
import divkit.dsl.arrayVariable
import divkit.dsl.booleanVariable
import divkit.dsl.color
import divkit.dsl.colorVariable
import divkit.dsl.dictVariable
import divkit.dsl.integerVariable
import divkit.dsl.numberVariable
import divkit.dsl.scope.DivScope
import divkit.dsl.stringVariable
import divkit.dsl.urlVariable

fun DivScope.divkitColorVariable(colorVariable: Var<String>, value: String): Variable {
    return colorVariable(
        name = colorVariable.name,
        value = color(value)
    )
}

fun DivScope.divkitVariable(variable: Var<Long>, value: Int): Variable {
    return integerVariable(
        name = variable.name,
        value = value.toLong(),
    )
}

fun DivScope.divkitVariable(variable: Var<Double>, value: Float): Variable {
    return numberVariable(
        name = variable.name,
        value = value.toDouble(),
    )
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T> DivScope.divkitVariable(variable: Var<T>, value: T): Variable {
    return when (T::class) {
        Boolean::class -> booleanVariable(
            name = variable.name,
            value = value as Boolean,
        )

        String::class -> stringVariable(
            name = variable.name,
            value = value as String,
        )

        Int::class -> integerVariable(
            name = variable.name,
            value = (value as Int).toLong(),
        )

        Long::class -> integerVariable(
            name = variable.name,
            value = value as Long,
        )

        Double::class -> numberVariable(
            name = variable.name,
            value = value as Double,
        )

        Url::class -> urlVariable(
            name = variable.name,
            value = value as Url,
        )

        Map::class -> dictVariable(
            name = variable.name,
            value = value as Map<String, Any>,
        )

        List::class -> arrayVariable(
            name = variable.name,
            value = value as List<Any>,
        )

        else -> throw UnsupportedOperationException("Unable to create variable for type ${T::class}.")
    }
}

fun <T> Expression<T>.expressionArrayElement() = divkit.dsl.core.expressionArrayElement<T>(compile())
fun Expression<String>.colorExpressionArrayElement() = divkit.dsl.core.expressionArrayElement<Color>(compile())
