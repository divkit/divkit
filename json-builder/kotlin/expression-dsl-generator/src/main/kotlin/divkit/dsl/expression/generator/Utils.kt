package divkit.dsl.expression.generator

import divkit.dsl.expression.generator.model.Type

fun Type.resolveKotlinType(): String {
    return when (this) {
        Type.INTEGER -> "Long"
        Type.BOOLEAN -> "Boolean"
        Type.STRING -> "String"
        Type.NUMBER -> "Double"
        Type.COLOR -> "String"
        Type.DATETIME -> "Long"
        Type.ARRAY -> "List<*>"
        Type.URL -> "String"
        Type.DICT -> "Map<*, *>"
    }
}

fun Type.resolveKotlinArgumentType(): String {
    return when (this) {
        Type.ARRAY -> "out List<*>"
        Type.DICT -> "out Map<*, *>"
        else -> resolveKotlinType()
    }
}

fun Type.resolveKotlinBasicType(): String {
    return when (this) {
        Type.INTEGER -> "Int"
        Type.BOOLEAN -> "Boolean"
        Type.STRING -> "String"
        Type.NUMBER -> "Double"
        Type.COLOR -> "String"
        Type.DATETIME -> "Int"
        Type.ARRAY -> "List"
        Type.URL -> "String"
        Type.DICT -> "Map"
    }
}
