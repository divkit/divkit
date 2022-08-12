package com.yandex.div.evaluable.function

import com.yandex.div.evaluable.EvaluableType
import com.yandex.div.evaluable.Function
import com.yandex.div.evaluable.FunctionArgument
import com.yandex.div.evaluable.REASON_INDEXES_ORDER
import com.yandex.div.evaluable.REASON_OUT_OF_BOUNDS
import com.yandex.div.evaluable.throwExceptionOnFunctionEvaluationFailed
import java.net.URLDecoder
import java.net.URLEncoder

internal object StringLength : Function() {

    override val name = "len"

    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.STRING))

    override val resultType = EvaluableType.INTEGER

    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        return (args.first() as String).length
    }
}

internal object StringContains : Function() {

    override val name = "contains"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.STRING), // string
        FunctionArgument(type = EvaluableType.STRING), // substring
    )

    override val resultType = EvaluableType.BOOLEAN

    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        val str = args[0] as String
        val substring = args[1] as String
        return str.contains(substring, ignoreCase = false)
    }
}

internal object StringSubstring : Function() {

    override val name = "substring"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.STRING), // string
        FunctionArgument(type = EvaluableType.INTEGER), // from
        FunctionArgument(type = EvaluableType.INTEGER), // to
    )

    override val resultType = EvaluableType.STRING

    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        val str = args[0] as String
        val startIndex = args[1] as Int
        val endIndex = args[2] as Int
        if (startIndex < 0 || endIndex > str.length)
            throwExceptionOnFunctionEvaluationFailed(name, args, REASON_OUT_OF_BOUNDS)
        if (startIndex > endIndex)
            throwExceptionOnFunctionEvaluationFailed(name, args, REASON_INDEXES_ORDER)
        return str.substring(startIndex, endIndex)
    }
}

internal object StringReplaceAll : Function() {

    override val name = "replaceAll"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.STRING), // string
        FunctionArgument(type = EvaluableType.STRING), // old
        FunctionArgument(type = EvaluableType.STRING), // new
    )

    override val resultType = EvaluableType.STRING

    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        val str = args[0] as String
        val old = args[1] as String
        val new = args[2] as String
        if (old.isEmpty()) return str
        return str.replace(old, new, ignoreCase = false)
    }
}

internal object StringIndex : Function() {

    override val name = "index"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.STRING), // string
        FunctionArgument(type = EvaluableType.STRING), // substring
    )

    override val resultType = EvaluableType.INTEGER

    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        val str = args[0] as String
        val substring = args[1] as String
        return str.indexOf(substring, ignoreCase = false)
    }
}

internal object StringLastIndex : Function() {

    override val name = "lastIndex"

    override val declaredArgs = listOf(
        FunctionArgument(type = EvaluableType.STRING), // string
        FunctionArgument(type = EvaluableType.STRING), // substring
    )

    override val resultType = EvaluableType.INTEGER

    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        val str = args[0] as String
        val substring = args[1] as String
        return str.lastIndexOf(substring, ignoreCase = false)
    }
}

internal object StringEncodeUri : Function() {

    override val name = "encodeUri"

    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.STRING))

    override val resultType = EvaluableType.STRING

    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        val str = args[0] as String
        return URLEncoder.encode(str, Charsets.UTF_8.name())
            .replace("+", "%20")
            .replace("%21", "!")
            .replace("%7E", "~")
            .replace("%27", "'")
            .replace("%28", "(")
            .replace("%29", ")")
    }
}

internal object StringDecodeUri : Function() {

    override val name = "decodeUri"

    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.STRING))

    override val resultType = EvaluableType.STRING

    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        val str = args[0] as String
        return URLDecoder.decode(str, Charsets.UTF_8.name())
    }
}

internal object Trim : Function() {

    override val name = "trim"

    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.STRING))
    override val resultType = EvaluableType.STRING
    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        val str = args[0] as String
        return str.trim()
    }
}

internal object TrimLeft : Function() {

    override val name = "trimLeft"

    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.STRING))
    override val resultType = EvaluableType.STRING
    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        val str = args[0] as String
        return str.trimStart()
    }
}

internal object TrimRight : Function() {

    override val name = "trimRight"

    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.STRING))
    override val resultType = EvaluableType.STRING
    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        val str = args[0] as String
        return str.trimEnd()
    }
}

internal object ToUpperCase : Function() {

    override val name = "toUpperCase"

    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.STRING))
    override val resultType = EvaluableType.STRING
    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        val str = args[0] as String
        return str.toUpperCase()
    }
}

internal object ToLowerCase : Function() {

    override val name = "toLowerCase"

    override val declaredArgs = listOf(FunctionArgument(type = EvaluableType.STRING))
    override val resultType = EvaluableType.STRING
    override val isPure = true

    override fun evaluate(args: List<Any>): Any {
        val str = args[0] as String
        return str.toLowerCase()
    }
}