// Generated code. Do not modify.

package com.yandex.div.reference

import android.graphics.Color
import android.net.Uri
import androidx.annotation.ColorInt
import com.yandex.div.data.*
import com.yandex.div.json.*
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionsList
import com.yandex.div.json.schema.*
import org.json.JSONArray
import org.json.JSONObject

class EntityWithComplexProperty(
    @JvmField val property: Property,
) : JSONSerializable, Hashable {

    private var _hash: Int? = null 

    override fun hash(): Int {
        _hash?.let {
            return it
        }
        val hash = 
            this::class.hashCode() +
            property.hash()
        _hash = hash
        return hash
    }

    fun equals(other: EntityWithComplexProperty?, resolver: ExpressionResolver, otherResolver: ExpressionResolver): Boolean {
        other ?: return false
        return property.equals(other.property, resolver, otherResolver)
    }

    fun copy(
        property: Property = this.property,
    ) = EntityWithComplexProperty(
        property = property,
    )

    override fun writeToJSON(): JSONObject {
        return builtInParserComponent.entityWithComplexPropertyJsonEntityParser
            .value
            .serialize(context = builtInParsingContext, value = this)
    }

    companion object {
        const val TYPE = "entity_with_complex_property"

        @JvmStatic
        @JvmName("fromJson")
        operator fun invoke(env: ParsingEnvironment, json: JSONObject): EntityWithComplexProperty {
            return builtInParserComponent.entityWithComplexPropertyJsonEntityParser
                .value
                .deserialize(context = env, data = json)
        }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithComplexProperty(env, json = it) }
    }

    class Property(
        @JvmField val value: Expression<Uri>,
    ) : JSONSerializable, Hashable {

        private var _hash: Int? = null 

        override fun hash(): Int {
            _hash?.let {
                return it
            }
            val hash = 
                this::class.hashCode() +
                value.hashCode()
            _hash = hash
            return hash
        }

        fun equals(other: Property?, resolver: ExpressionResolver, otherResolver: ExpressionResolver): Boolean {
            other ?: return false
            return value.evaluate(resolver) == other.value.evaluate(otherResolver)
        }

        fun copy(
            value: Expression<Uri> = this.value,
        ) = Property(
            value = value,
        )

        override fun writeToJSON(): JSONObject {
            return builtInParserComponent.entityWithComplexPropertyPropertyJsonEntityParser
                .value
                .serialize(context = builtInParsingContext, value = this)
        }

        companion object {
            @JvmStatic
            @JvmName("fromJson")
            operator fun invoke(env: ParsingEnvironment, json: JSONObject): Property {
                return builtInParserComponent.entityWithComplexPropertyPropertyJsonEntityParser
                    .value
                    .deserialize(context = env, data = json)
            }

            val CREATOR = { env: ParsingEnvironment, it: JSONObject -> Property(env, json = it) }
        }
    }
}
