// Generated code. Do not modify.

package com.yandex.div2

import org.json.JSONObject

class EntityWithSimpleProperties(
    @JvmField final val boolean: Expression<Boolean>? = null,
    @JvmField final val booleanInt: Expression<Boolean>? = null,
    @JvmField final val color: Expression<Int>? = null,
    @JvmField final val double: Expression<Double>? = null,
    @JvmField final val id: Long = ID_DEFAULT_VALUE, // default value: 0
    @JvmField final val integer: Expression<Long> = INTEGER_DEFAULT_VALUE, // default value: 0
    @JvmField final val positiveInteger: Expression<Long>? = null, // constraint: number > 0
    @JvmField final val string: Expression<String>? = null,
    @JvmField final val url: Expression<Uri>? = null,
) : Hashable {

    private var _hash: Int? = null 

    override fun hash(): Int {
        _hash?.let {
            return it
        }
        val hash = 
            this::class.hashCode() +
            (boolean?.hashCode() ?: 0) +
            (booleanInt?.hashCode() ?: 0) +
            (color?.hashCode() ?: 0) +
            (double?.hashCode() ?: 0) +
            id.hashCode() +
            integer.hashCode() +
            (positiveInteger?.hashCode() ?: 0) +
            (string?.hashCode() ?: 0) +
            (url?.hashCode() ?: 0)
        _hash = hash
        return hash
    }

    fun equals(other: EntityWithSimpleProperties?, resolver: ExpressionResolver, otherResolver: ExpressionResolver): Boolean {
        other ?: return false
        return boolean?.evaluate(resolver) == other.boolean?.evaluate(otherResolver) &&
            booleanInt?.evaluate(resolver) == other.booleanInt?.evaluate(otherResolver) &&
            color?.evaluate(resolver) == other.color?.evaluate(otherResolver) &&
            double?.evaluate(resolver) == other.double?.evaluate(otherResolver) &&
            id == other.id &&
            integer.evaluate(resolver) == other.integer.evaluate(otherResolver) &&
            positiveInteger?.evaluate(resolver) == other.positiveInteger?.evaluate(otherResolver) &&
            string?.evaluate(resolver) == other.string?.evaluate(otherResolver) &&
            url?.evaluate(resolver) == other.url?.evaluate(otherResolver)
    }

    fun copy(
        boolean: Expression<Boolean>? = this.boolean,
        booleanInt: Expression<Boolean>? = this.booleanInt,
        color: Expression<Int>? = this.color,
        double: Expression<Double>? = this.double,
        id: Long = this.id,
        integer: Expression<Long> = this.integer,
        positiveInteger: Expression<Long>? = this.positiveInteger,
        string: Expression<String>? = this.string,
        url: Expression<Uri>? = this.url,
    ) = EntityWithSimpleProperties(
        boolean = boolean,
        booleanInt = booleanInt,
        color = color,
        double = double,
        id = id,
        integer = integer,
        positiveInteger = positiveInteger,
        string = string,
        url = url,
    )

    companion object {
        const val TYPE = "entity_with_simple_properties"

        private val ID_DEFAULT_VALUE = 0L
        private val INTEGER_DEFAULT_VALUE = Expression.constant(0L)

        private val POSITIVE_INTEGER_VALIDATOR = ValueValidator<Long> { it: Long -> it > 0 }
    }
}
