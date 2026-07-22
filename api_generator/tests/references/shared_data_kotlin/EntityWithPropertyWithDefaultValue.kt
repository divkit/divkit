// Generated code. Do not modify.

package com.yandex.div.reference

import org.json.JSONArray
import org.json.JSONObject

class EntityWithPropertyWithDefaultValue(
    @JvmField val colorAarrggbb: Expression<Int> = COLOR_AARRGGBB_DEFAULT_VALUE, // default value: #80ff0000
    @JvmField val colorRrggbb: Expression<Int> = COLOR_RRGGBB_DEFAULT_VALUE, // default value: #ff0000
    @JvmField val int: Expression<Long> = INT_DEFAULT_VALUE, // constraint: number >= 0; default value: 0
    @JvmField val nested: Nested? = null,
    @JvmField val url: Expression<Uri> = URL_DEFAULT_VALUE, // valid schemes: [https]; default value: https://yandex.ru
) : Hashable {

    private var _hash: Int? = null 

    override fun hash(): Int {
        _hash?.let {
            return it
        }
        val hash = 
            this::class.hashCode() +
            colorAarrggbb.hashCode() +
            colorRrggbb.hashCode() +
            int.hashCode() +
            (nested?.hash() ?: 0) +
            url.hashCode()
        _hash = hash
        return hash
    }

    fun equals(other: EntityWithPropertyWithDefaultValue?, resolver: ExpressionResolver, otherResolver: ExpressionResolver): Boolean {
        other ?: return false
        return colorAarrggbb.evaluate(resolver) == other.colorAarrggbb.evaluate(otherResolver) &&
            colorRrggbb.evaluate(resolver) == other.colorRrggbb.evaluate(otherResolver) &&
            int.evaluate(resolver) == other.int.evaluate(otherResolver) &&
            (nested?.equals(other.nested, resolver, otherResolver) ?: (other.nested == null)) &&
            url.evaluate(resolver) == other.url.evaluate(otherResolver)
    }

    fun copy(
        colorAarrggbb: Expression<Int> = this.colorAarrggbb,
        colorRrggbb: Expression<Int> = this.colorRrggbb,
        int: Expression<Long> = this.int,
        nested: Nested? = this.nested,
        url: Expression<Uri> = this.url,
    ) = EntityWithPropertyWithDefaultValue(
        colorAarrggbb = colorAarrggbb,
        colorRrggbb = colorRrggbb,
        int = int,
        nested = nested,
        url = url,
    )

    companion object {
        const val TYPE = "entity_with_property_with_default_value"

        private val COLOR_AARRGGBB_DEFAULT_VALUE = Expression.constant(0x80FF0000.toInt())
        private val COLOR_RRGGBB_DEFAULT_VALUE = Expression.constant(0xFFFF0000.toInt())
        private val INT_DEFAULT_VALUE = Expression.constant(0L)
        private val URL_DEFAULT_VALUE = Expression.constant(Uri.parse("https://yandex.ru"))
    }

    class Nested(
        @JvmField val int: Expression<Long> = INT_DEFAULT_VALUE, // constraint: number >= 0; default value: 0
        @JvmField val nonOptional: Expression<String>,
        @JvmField val url: Expression<Uri> = URL_DEFAULT_VALUE, // valid schemes: [https]; default value: https://yandex.ru
    ) : Hashable {

        private var _hash: Int? = null 

        override fun hash(): Int {
            _hash?.let {
                return it
            }
            val hash = 
                this::class.hashCode() +
                int.hashCode() +
                nonOptional.hashCode() +
                url.hashCode()
            _hash = hash
            return hash
        }

        fun equals(other: Nested?, resolver: ExpressionResolver, otherResolver: ExpressionResolver): Boolean {
            other ?: return false
            return int.evaluate(resolver) == other.int.evaluate(otherResolver) &&
                nonOptional.evaluate(resolver) == other.nonOptional.evaluate(otherResolver) &&
                url.evaluate(resolver) == other.url.evaluate(otherResolver)
        }

        fun copy(
            int: Expression<Long> = this.int,
            nonOptional: Expression<String> = this.nonOptional,
            url: Expression<Uri> = this.url,
        ) = Nested(
            int = int,
            nonOptional = nonOptional,
            url = url,
        )

        companion object {
            private val INT_DEFAULT_VALUE = Expression.constant(0L)
            private val URL_DEFAULT_VALUE = Expression.constant(Uri.parse("https://yandex.ru"))
        }
    }
}
