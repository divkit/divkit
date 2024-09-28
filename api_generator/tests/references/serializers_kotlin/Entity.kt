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
        val propertiesHash = this::class.hashCode() + when(this) {
            is WithArray -> this.value.propertiesHash()
            is WithArrayOfEnums -> this.value.propertiesHash()
            is WithArrayOfExpressions -> this.value.propertiesHash()
            is WithArrayOfNestedItems -> this.value.propertiesHash()
            is WithArrayWithTransform -> this.value.propertiesHash()
            is WithComplexProperty -> this.value.propertiesHash()
            is WithComplexPropertyWithDefaultValue -> this.value.propertiesHash()
            is WithEntityProperty -> this.value.propertiesHash()
            is WithOptionalComplexProperty -> this.value.propertiesHash()
            is WithOptionalProperty -> this.value.propertiesHash()
            is WithOptionalStringEnumProperty -> this.value.propertiesHash()
            is WithPropertyWithDefaultValue -> this.value.propertiesHash()
            is WithRawArray -> this.value.propertiesHash()
            is WithRequiredProperty -> this.value.propertiesHash()
            is WithSimpleProperties -> this.value.propertiesHash()
            is WithStringArrayProperty -> this.value.propertiesHash()
            is WithStringEnumProperty -> this.value.propertiesHash()
            is WithStringEnumPropertyWithDefaultValue -> this.value.propertiesHash()
            is WithoutProperties -> this.value.propertiesHash()
        }
       _propertiesHash = propertiesHash
       return propertiesHash
    }

    override fun hash(): Int {
        _hash?.let {
            return it
        }
        val hash = this::class.hashCode() + when(this) {
            is WithArray -> this.value.hash()
            is WithArrayOfEnums -> this.value.hash()
            is WithArrayOfExpressions -> this.value.hash()
            is WithArrayOfNestedItems -> this.value.hash()
            is WithArrayWithTransform -> this.value.hash()
            is WithComplexProperty -> this.value.hash()
            is WithComplexPropertyWithDefaultValue -> this.value.hash()
            is WithEntityProperty -> this.value.hash()
            is WithOptionalComplexProperty -> this.value.hash()
            is WithOptionalProperty -> this.value.hash()
            is WithOptionalStringEnumProperty -> this.value.hash()
            is WithPropertyWithDefaultValue -> this.value.hash()
            is WithRawArray -> this.value.hash()
            is WithRequiredProperty -> this.value.hash()
            is WithSimpleProperties -> this.value.hash()
            is WithStringArrayProperty -> this.value.hash()
            is WithStringEnumProperty -> this.value.hash()
            is WithStringEnumPropertyWithDefaultValue -> this.value.hash()
            is WithoutProperties -> this.value.hash()
        }
       _hash = hash
       return hash
    }

    fun equals(other: Entity?, resolver: ExpressionResolver, otherResolver: ExpressionResolver): Boolean {
        other ?: return false
        return when(this) {
            is WithArray -> this.value.equals(other.value() as? EntityWithArray, resolver, otherResolver)
            is WithArrayOfEnums -> this.value.equals(other.value() as? EntityWithArrayOfEnums, resolver, otherResolver)
            is WithArrayOfExpressions -> this.value.equals(other.value() as? EntityWithArrayOfExpressions, resolver, otherResolver)
            is WithArrayOfNestedItems -> this.value.equals(other.value() as? EntityWithArrayOfNestedItems, resolver, otherResolver)
            is WithArrayWithTransform -> this.value.equals(other.value() as? EntityWithArrayWithTransform, resolver, otherResolver)
            is WithComplexProperty -> this.value.equals(other.value() as? EntityWithComplexProperty, resolver, otherResolver)
            is WithComplexPropertyWithDefaultValue -> this.value.equals(other.value() as? EntityWithComplexPropertyWithDefaultValue, resolver, otherResolver)
            is WithEntityProperty -> this.value.equals(other.value() as? EntityWithEntityProperty, resolver, otherResolver)
            is WithOptionalComplexProperty -> this.value.equals(other.value() as? EntityWithOptionalComplexProperty, resolver, otherResolver)
            is WithOptionalProperty -> this.value.equals(other.value() as? EntityWithOptionalProperty, resolver, otherResolver)
            is WithOptionalStringEnumProperty -> this.value.equals(other.value() as? EntityWithOptionalStringEnumProperty, resolver, otherResolver)
            is WithPropertyWithDefaultValue -> this.value.equals(other.value() as? EntityWithPropertyWithDefaultValue, resolver, otherResolver)
            is WithRawArray -> this.value.equals(other.value() as? EntityWithRawArray, resolver, otherResolver)
            is WithRequiredProperty -> this.value.equals(other.value() as? EntityWithRequiredProperty, resolver, otherResolver)
            is WithSimpleProperties -> this.value.equals(other.value() as? EntityWithSimpleProperties, resolver, otherResolver)
            is WithStringArrayProperty -> this.value.equals(other.value() as? EntityWithStringArrayProperty, resolver, otherResolver)
            is WithStringEnumProperty -> this.value.equals(other.value() as? EntityWithStringEnumProperty, resolver, otherResolver)
            is WithStringEnumPropertyWithDefaultValue -> this.value.equals(other.value() as? EntityWithStringEnumPropertyWithDefaultValue, resolver, otherResolver)
            is WithoutProperties -> this.value.equals(other.value() as? EntityWithoutProperties, resolver, otherResolver)
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
        return builtInParserComponent.entityJsonEntityParser
            .value
            .serialize(context = builtInParsingContext, value = this)
    }

    companion object {

        @Throws(ParsingException::class)
        @JvmStatic
        @JvmName("fromJson")
        operator fun invoke(env: ParsingEnvironment, json: JSONObject): Entity {
            return builtInParserComponent.entityJsonEntityParser
                .value
                .deserialize(context = env, data = json)
        }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> Entity(env, json = it) }
    }
}
