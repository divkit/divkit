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

@Mockable
class EntityWithOptionalComplexProperty(
    @JvmField final val property: Property? = null,
) : JSONSerializable {

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.write(key = "property", value = property)
        json.write(key = "type", value = TYPE)
        return json
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        other ?: return false
        if (other !is EntityWithOptionalComplexProperty) {
            return false
        }
        if (property != other.property) {
            return false
        }
        return true
    }

    companion object {
        const val TYPE = "entity_with_optional_complex_property"

        @JvmStatic
        @JvmName("fromJson")
        operator fun invoke(env: ParsingEnvironment, json: JSONObject): EntityWithOptionalComplexProperty {
            val logger = env.logger
            return EntityWithOptionalComplexProperty(
                property = JsonParser.readOptional(json, "property", Property.CREATOR, logger, env)
            )
        }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithOptionalComplexProperty(env, json = it) }
    }


    @Mockable
    class Property(
        @JvmField final val value: Expression<Uri>,
    ) : JSONSerializable {

        override fun writeToJSON(): JSONObject {
            val json = JSONObject()
            json.writeExpression(key = "value", value = value, converter = URI_TO_STRING)
            return json
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            }
            other ?: return false
            if (other !is Property) {
                return false
            }
            if (value != other.value) {
                return false
            }
            return true
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
