// Generated code. Do not modify.

package com.yandex.div2

import android.graphics.Color
import android.net.Uri
import androidx.annotation.ColorInt
import com.yandex.div.json.*
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionsList
import com.yandex.div.json.schema.*
import com.yandex.div.core.annotations.Mockable
import java.io.IOException
import java.util.BitSet
import org.json.JSONObject
import com.yandex.div.data.*
import org.json.JSONArray

@Mockable
class EntityWithComplexProperty(
    @JvmField final val property: Property,
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
        val json = JSONObject()
        json.write(key = "property", value = property)
        json.write(key = "type", value = TYPE)
        return json
    }

    companion object {
        const val TYPE = "entity_with_complex_property"

        @JvmStatic
        @JvmName("fromJson")
        operator fun invoke(env: ParsingEnvironment, json: JSONObject): EntityWithComplexProperty {
            val logger = env.logger
            return EntityWithComplexProperty(
                property = JsonParser.read(json, "property", Property.CREATOR, logger, env)
            )
        }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithComplexProperty(env, json = it) }
    }

    @Mockable
    class Property(
        @JvmField final val value: Expression<Uri>,
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
            val json = JSONObject()
            json.writeExpression(key = "value", value = value, converter = URI_TO_STRING)
            return json
        }

        companion object {
            @JvmStatic
            @JvmName("fromJson")
            operator fun invoke(env: ParsingEnvironment, json: JSONObject): Property {
                val logger = env.logger
                return Property(
                    value = JsonParser.readExpression(json, "value", STRING_TO_URI, logger, env, TYPE_HELPER_URI)
                )
            }

            val CREATOR = { env: ParsingEnvironment, it: JSONObject -> Property(env, json = it) }
        }
    }
}
