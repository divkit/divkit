// Generated code. Do not modify.

package com.yandex.div2

import org.json.JSONObject

class EntityWithPropertyWithDefaultValue(
    @JvmField final val int: Expression<Long> = INT_DEFAULT_VALUE, // constraint: number >= 0; default value: 0
    @JvmField final val nested: Nested? = null,
    @JvmField final val url: Expression<Uri> = URL_DEFAULT_VALUE, // valid schemes: [https]; default value: https://yandex.ru
) : Hashable {

    private var _hash: Int? = null 

    override fun hash(): Int {
        _hash?.let {
            return it
        }
        val hash = 
            this::class.hashCode() +
            int.hashCode() +
            (nested?.hash() ?: 0) +
            url.hashCode()
        _hash = hash
        return hash
    }

    fun equals(other: EntityWithPropertyWithDefaultValue?, resolver: ExpressionResolver, otherResolver: ExpressionResolver): Boolean {
        other ?: return false
        return int.evaluate(resolver) == other.int.evaluate(otherResolver) &&
            (nested?.equals(other.nested, resolver, otherResolver) ?: (other.nested == null)) &&
            url.evaluate(resolver) == other.url.evaluate(otherResolver)
    }

    fun copy(
        int: Expression<Long> = this.int,
        nested: Nested? = this.nested,
        url: Expression<Uri> = this.url,
    ) = EntityWithPropertyWithDefaultValue(
        int = int,
        nested = nested,
        url = url,
    )

    companion object {
        const val TYPE = "entity_with_property_with_default_value"

        private val INT_DEFAULT_VALUE = Expression.constant(0L)
        private val URL_DEFAULT_VALUE = Expression.constant(Uri.parse("https://yandex.ru"))

        private val INT_VALIDATOR = ValueValidator<Long> { it: Long -> it >= 0 }
        private val URL_VALIDATOR = ValueValidator<Uri> { it.hasScheme(listOf("https")) }
    }


    class Nested(
        @JvmField final val int: Expression<Long> = INT_DEFAULT_VALUE, // constraint: number >= 0; default value: 0
        @JvmField final val nonOptional: Expression<String>,
        @JvmField final val url: Expression<Uri> = URL_DEFAULT_VALUE, // valid schemes: [https]; default value: https://yandex.ru
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

            private val INT_VALIDATOR = ValueValidator<Long> { it: Long -> it >= 0 }
            private val URL_VALIDATOR = ValueValidator<Uri> { it.hasScheme(listOf("https")) }
        }

    }
}
