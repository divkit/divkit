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
sealed class Entity : JSONSerializable, Hashable {
    class WithArray(val value: EntityWithArray) : Entity()
    class WithArrayOfEnums(val value: EntityWithArrayOfEnums) : Entity()
    class WithArrayOfExpressions(val value: EntityWithArrayOfExpressions) : Entity()
    class WithArrayOfNestedItems(val value: EntityWithArrayOfNestedItems) : Entity()
    class WithArrayWithTransform(val value: EntityWithArrayWithTransform) : Entity()
    class WithComplexProperty(val value: EntityWithComplexProperty) : Entity()
    class WithComplexPropertyWithDefaultValue(val value: EntityWithComplexPropertyWithDefaultValue) : Entity()
    class WithEntityProperty(val value: EntityWithEntityProperty) : Entity()
    class WithOptionalComplexProperty(val value: EntityWithOptionalComplexProperty) : Entity()
    class WithOptionalProperty(val value: EntityWithOptionalProperty) : Entity()
    class WithOptionalStringEnumProperty(val value: EntityWithOptionalStringEnumProperty) : Entity()
    class WithPropertyWithDefaultValue(val value: EntityWithPropertyWithDefaultValue) : Entity()
    class WithRawArray(val value: EntityWithRawArray) : Entity()
    class WithRequiredProperty(val value: EntityWithRequiredProperty) : Entity()
    class WithSimpleProperties(val value: EntityWithSimpleProperties) : Entity()
    class WithStringArrayProperty(val value: EntityWithStringArrayProperty) : Entity()
    class WithStringEnumProperty(val value: EntityWithStringEnumProperty) : Entity()
    class WithStringEnumPropertyWithDefaultValue(val value: EntityWithStringEnumPropertyWithDefaultValue) : Entity()
    class WithoutProperties(val value: EntityWithoutProperties) : Entity()

    private var _propertiesHash: Int? = null 
    private var _hash: Int? = null 

    override fun propertiesHash(): Int {
        _propertiesHash?.let {
            return it
        }
        return when(this) {
            is WithArray -> 31 + this.value.propertiesHash()
            is WithArrayOfEnums -> 62 + this.value.propertiesHash()
            is WithArrayOfExpressions -> 93 + this.value.propertiesHash()
            is WithArrayOfNestedItems -> 124 + this.value.propertiesHash()
            is WithArrayWithTransform -> 155 + this.value.propertiesHash()
            is WithComplexProperty -> 186 + this.value.propertiesHash()
            is WithComplexPropertyWithDefaultValue -> 217 + this.value.propertiesHash()
            is WithEntityProperty -> 248 + this.value.propertiesHash()
            is WithOptionalComplexProperty -> 279 + this.value.propertiesHash()
            is WithOptionalProperty -> 310 + this.value.propertiesHash()
            is WithOptionalStringEnumProperty -> 341 + this.value.propertiesHash()
            is WithPropertyWithDefaultValue -> 372 + this.value.propertiesHash()
            is WithRawArray -> 403 + this.value.propertiesHash()
            is WithRequiredProperty -> 434 + this.value.propertiesHash()
            is WithSimpleProperties -> 465 + this.value.propertiesHash()
            is WithStringArrayProperty -> 496 + this.value.propertiesHash()
            is WithStringEnumProperty -> 527 + this.value.propertiesHash()
            is WithStringEnumPropertyWithDefaultValue -> 558 + this.value.propertiesHash()
            is WithoutProperties -> 589 + this.value.propertiesHash()
        }.also {
            _propertiesHash = it
        }
    }

    override fun hash(): Int {
        _hash?.let {
            return it
        }
        return when(this) {
            is WithArray -> 31 + this.value.hash()
            is WithArrayOfEnums -> 62 + this.value.hash()
            is WithArrayOfExpressions -> 93 + this.value.hash()
            is WithArrayOfNestedItems -> 124 + this.value.hash()
            is WithArrayWithTransform -> 155 + this.value.hash()
            is WithComplexProperty -> 186 + this.value.hash()
            is WithComplexPropertyWithDefaultValue -> 217 + this.value.hash()
            is WithEntityProperty -> 248 + this.value.hash()
            is WithOptionalComplexProperty -> 279 + this.value.hash()
            is WithOptionalProperty -> 310 + this.value.hash()
            is WithOptionalStringEnumProperty -> 341 + this.value.hash()
            is WithPropertyWithDefaultValue -> 372 + this.value.hash()
            is WithRawArray -> 403 + this.value.hash()
            is WithRequiredProperty -> 434 + this.value.hash()
            is WithSimpleProperties -> 465 + this.value.hash()
            is WithStringArrayProperty -> 496 + this.value.hash()
            is WithStringEnumProperty -> 527 + this.value.hash()
            is WithStringEnumPropertyWithDefaultValue -> 558 + this.value.hash()
            is WithoutProperties -> 589 + this.value.hash()
        }.also {
            _hash = it
        }
    }

    fun value(): Any {
        return when (this) {
            is WithArray -> value
            is WithArrayOfEnums -> value
            is WithArrayOfExpressions -> value
            is WithArrayOfNestedItems -> value
            is WithArrayWithTransform -> value
            is WithComplexProperty -> value
            is WithComplexPropertyWithDefaultValue -> value
            is WithEntityProperty -> value
            is WithOptionalComplexProperty -> value
            is WithOptionalProperty -> value
            is WithOptionalStringEnumProperty -> value
            is WithPropertyWithDefaultValue -> value
            is WithRawArray -> value
            is WithRequiredProperty -> value
            is WithSimpleProperties -> value
            is WithStringArrayProperty -> value
            is WithStringEnumProperty -> value
            is WithStringEnumPropertyWithDefaultValue -> value
            is WithoutProperties -> value
        }
    }

    override fun writeToJSON(): JSONObject {
        return when (this) {
            is WithArray -> value.writeToJSON()
            is WithArrayOfEnums -> value.writeToJSON()
            is WithArrayOfExpressions -> value.writeToJSON()
            is WithArrayOfNestedItems -> value.writeToJSON()
            is WithArrayWithTransform -> value.writeToJSON()
            is WithComplexProperty -> value.writeToJSON()
            is WithComplexPropertyWithDefaultValue -> value.writeToJSON()
            is WithEntityProperty -> value.writeToJSON()
            is WithOptionalComplexProperty -> value.writeToJSON()
            is WithOptionalProperty -> value.writeToJSON()
            is WithOptionalStringEnumProperty -> value.writeToJSON()
            is WithPropertyWithDefaultValue -> value.writeToJSON()
            is WithRawArray -> value.writeToJSON()
            is WithRequiredProperty -> value.writeToJSON()
            is WithSimpleProperties -> value.writeToJSON()
            is WithStringArrayProperty -> value.writeToJSON()
            is WithStringEnumProperty -> value.writeToJSON()
            is WithStringEnumPropertyWithDefaultValue -> value.writeToJSON()
            is WithoutProperties -> value.writeToJSON()
        }
    }

    companion object {
        @Throws(ParsingException::class)
        @JvmStatic
        @JvmName("fromJson")
        operator fun invoke(env: ParsingEnvironment, json: JSONObject): Entity {
            val logger = env.logger
            val type: String = json.read("type", logger = logger, env = env)
            when (type) {
                EntityWithArray.TYPE -> return WithArray(EntityWithArray(env, json))
                EntityWithArrayOfEnums.TYPE -> return WithArrayOfEnums(EntityWithArrayOfEnums(env, json))
                EntityWithArrayOfExpressions.TYPE -> return WithArrayOfExpressions(EntityWithArrayOfExpressions(env, json))
                EntityWithArrayOfNestedItems.TYPE -> return WithArrayOfNestedItems(EntityWithArrayOfNestedItems(env, json))
                EntityWithArrayWithTransform.TYPE -> return WithArrayWithTransform(EntityWithArrayWithTransform(env, json))
                EntityWithComplexProperty.TYPE -> return WithComplexProperty(EntityWithComplexProperty(env, json))
                EntityWithComplexPropertyWithDefaultValue.TYPE -> return WithComplexPropertyWithDefaultValue(EntityWithComplexPropertyWithDefaultValue(env, json))
                EntityWithEntityProperty.TYPE -> return WithEntityProperty(EntityWithEntityProperty(env, json))
                EntityWithOptionalComplexProperty.TYPE -> return WithOptionalComplexProperty(EntityWithOptionalComplexProperty(env, json))
                EntityWithOptionalProperty.TYPE -> return WithOptionalProperty(EntityWithOptionalProperty(env, json))
                EntityWithOptionalStringEnumProperty.TYPE -> return WithOptionalStringEnumProperty(EntityWithOptionalStringEnumProperty(env, json))
                EntityWithPropertyWithDefaultValue.TYPE -> return WithPropertyWithDefaultValue(EntityWithPropertyWithDefaultValue(env, json))
                EntityWithRawArray.TYPE -> return WithRawArray(EntityWithRawArray(env, json))
                EntityWithRequiredProperty.TYPE -> return WithRequiredProperty(EntityWithRequiredProperty(env, json))
                EntityWithSimpleProperties.TYPE -> return WithSimpleProperties(EntityWithSimpleProperties(env, json))
                EntityWithStringArrayProperty.TYPE -> return WithStringArrayProperty(EntityWithStringArrayProperty(env, json))
                EntityWithStringEnumProperty.TYPE -> return WithStringEnumProperty(EntityWithStringEnumProperty(env, json))
                EntityWithStringEnumPropertyWithDefaultValue.TYPE -> return WithStringEnumPropertyWithDefaultValue(EntityWithStringEnumPropertyWithDefaultValue(env, json))
                EntityWithoutProperties.TYPE -> return WithoutProperties(EntityWithoutProperties(env, json))
            }
            val template = env.templates.getOrThrow(type, json) as? EntityTemplate
            if (template != null) {
                return template.resolve(env, json)
            } else {
                throw typeMismatch(json = json, key = "type", value = type)
            }
        }
        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> Entity(env, json = it) }
    }
}
