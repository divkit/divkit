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
sealed class EntityTemplate : JSONSerializable, JsonTemplate<Entity> {
    class WithArray(val value: EntityWithArrayTemplate) : EntityTemplate()
    class WithArrayOfEnums(val value: EntityWithArrayOfEnumsTemplate) : EntityTemplate()
    class WithArrayOfExpressions(val value: EntityWithArrayOfExpressionsTemplate) : EntityTemplate()
    class WithArrayOfNestedItems(val value: EntityWithArrayOfNestedItemsTemplate) : EntityTemplate()
    class WithArrayWithTransform(val value: EntityWithArrayWithTransformTemplate) : EntityTemplate()
    class WithComplexProperty(val value: EntityWithComplexPropertyTemplate) : EntityTemplate()
    class WithComplexPropertyWithDefaultValue(val value: EntityWithComplexPropertyWithDefaultValueTemplate) : EntityTemplate()
    class WithEntityProperty(val value: EntityWithEntityPropertyTemplate) : EntityTemplate()
    class WithOptionalComplexProperty(val value: EntityWithOptionalComplexPropertyTemplate) : EntityTemplate()
    class WithOptionalProperty(val value: EntityWithOptionalPropertyTemplate) : EntityTemplate()
    class WithOptionalStringEnumProperty(val value: EntityWithOptionalStringEnumPropertyTemplate) : EntityTemplate()
    class WithPropertyWithDefaultValue(val value: EntityWithPropertyWithDefaultValueTemplate) : EntityTemplate()
    class WithRequiredProperty(val value: EntityWithRequiredPropertyTemplate) : EntityTemplate()
    class WithSimpleProperties(val value: EntityWithSimplePropertiesTemplate) : EntityTemplate()
    class WithStrictArray(val value: EntityWithStrictArrayTemplate) : EntityTemplate()
    class WithStringArrayProperty(val value: EntityWithStringArrayPropertyTemplate) : EntityTemplate()
    class WithStringEnumProperty(val value: EntityWithStringEnumPropertyTemplate) : EntityTemplate()
    class WithStringEnumPropertyWithDefaultValue(val value: EntityWithStringEnumPropertyWithDefaultValueTemplate) : EntityTemplate()
    class WithoutProperties(val value: EntityWithoutPropertiesTemplate) : EntityTemplate()

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
            is WithRequiredProperty -> value
            is WithSimpleProperties -> value
            is WithStrictArray -> value
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
            is WithRequiredProperty -> value.writeToJSON()
            is WithSimpleProperties -> value.writeToJSON()
            is WithStrictArray -> value.writeToJSON()
            is WithStringArrayProperty -> value.writeToJSON()
            is WithStringEnumProperty -> value.writeToJSON()
            is WithStringEnumPropertyWithDefaultValue -> value.writeToJSON()
            is WithoutProperties -> value.writeToJSON()
        }
    }

    override fun resolve(env: ParsingEnvironment, data: JSONObject): Entity {
        return when (this) {
            is WithArray -> Entity.WithArray(value.resolve(env, data))
            is WithArrayOfEnums -> Entity.WithArrayOfEnums(value.resolve(env, data))
            is WithArrayOfExpressions -> Entity.WithArrayOfExpressions(value.resolve(env, data))
            is WithArrayOfNestedItems -> Entity.WithArrayOfNestedItems(value.resolve(env, data))
            is WithArrayWithTransform -> Entity.WithArrayWithTransform(value.resolve(env, data))
            is WithComplexProperty -> Entity.WithComplexProperty(value.resolve(env, data))
            is WithComplexPropertyWithDefaultValue -> Entity.WithComplexPropertyWithDefaultValue(value.resolve(env, data))
            is WithEntityProperty -> Entity.WithEntityProperty(value.resolve(env, data))
            is WithOptionalComplexProperty -> Entity.WithOptionalComplexProperty(value.resolve(env, data))
            is WithOptionalProperty -> Entity.WithOptionalProperty(value.resolve(env, data))
            is WithOptionalStringEnumProperty -> Entity.WithOptionalStringEnumProperty(value.resolve(env, data))
            is WithPropertyWithDefaultValue -> Entity.WithPropertyWithDefaultValue(value.resolve(env, data))
            is WithRequiredProperty -> Entity.WithRequiredProperty(value.resolve(env, data))
            is WithSimpleProperties -> Entity.WithSimpleProperties(value.resolve(env, data))
            is WithStrictArray -> Entity.WithStrictArray(value.resolve(env, data))
            is WithStringArrayProperty -> Entity.WithStringArrayProperty(value.resolve(env, data))
            is WithStringEnumProperty -> Entity.WithStringEnumProperty(value.resolve(env, data))
            is WithStringEnumPropertyWithDefaultValue -> Entity.WithStringEnumPropertyWithDefaultValue(value.resolve(env, data))
            is WithoutProperties -> Entity.WithoutProperties(value.resolve(env, data))
        }
    }

    val type: String
        get() {
            return when (this) {
                is WithArray -> EntityWithArrayTemplate.TYPE
                is WithArrayOfEnums -> EntityWithArrayOfEnumsTemplate.TYPE
                is WithArrayOfExpressions -> EntityWithArrayOfExpressionsTemplate.TYPE
                is WithArrayOfNestedItems -> EntityWithArrayOfNestedItemsTemplate.TYPE
                is WithArrayWithTransform -> EntityWithArrayWithTransformTemplate.TYPE
                is WithComplexProperty -> EntityWithComplexPropertyTemplate.TYPE
                is WithComplexPropertyWithDefaultValue -> EntityWithComplexPropertyWithDefaultValueTemplate.TYPE
                is WithEntityProperty -> EntityWithEntityPropertyTemplate.TYPE
                is WithOptionalComplexProperty -> EntityWithOptionalComplexPropertyTemplate.TYPE
                is WithOptionalProperty -> EntityWithOptionalPropertyTemplate.TYPE
                is WithOptionalStringEnumProperty -> EntityWithOptionalStringEnumPropertyTemplate.TYPE
                is WithPropertyWithDefaultValue -> EntityWithPropertyWithDefaultValueTemplate.TYPE
                is WithRequiredProperty -> EntityWithRequiredPropertyTemplate.TYPE
                is WithSimpleProperties -> EntityWithSimplePropertiesTemplate.TYPE
                is WithStrictArray -> EntityWithStrictArrayTemplate.TYPE
                is WithStringArrayProperty -> EntityWithStringArrayPropertyTemplate.TYPE
                is WithStringEnumProperty -> EntityWithStringEnumPropertyTemplate.TYPE
                is WithStringEnumPropertyWithDefaultValue -> EntityWithStringEnumPropertyWithDefaultValueTemplate.TYPE
                is WithoutProperties -> EntityWithoutPropertiesTemplate.TYPE
            }
        }

    companion object {
        @Throws(ParsingException::class)
        operator fun invoke(
            env: ParsingEnvironment,
            topLevel: Boolean = false,
            json: JSONObject
        ): EntityTemplate {
            val logger = env.logger
            val receivedType: String = json.read("type", logger = logger, env = env)
            val parent = env.templates[receivedType] as? EntityTemplate
            val type = parent?.type ?: receivedType
            when (type) {
                EntityWithArrayTemplate.TYPE -> return WithArray(EntityWithArrayTemplate(env, parent?.value() as EntityWithArrayTemplate?, topLevel, json))
                EntityWithArrayOfEnumsTemplate.TYPE -> return WithArrayOfEnums(EntityWithArrayOfEnumsTemplate(env, parent?.value() as EntityWithArrayOfEnumsTemplate?, topLevel, json))
                EntityWithArrayOfExpressionsTemplate.TYPE -> return WithArrayOfExpressions(EntityWithArrayOfExpressionsTemplate(env, parent?.value() as EntityWithArrayOfExpressionsTemplate?, topLevel, json))
                EntityWithArrayOfNestedItemsTemplate.TYPE -> return WithArrayOfNestedItems(EntityWithArrayOfNestedItemsTemplate(env, parent?.value() as EntityWithArrayOfNestedItemsTemplate?, topLevel, json))
                EntityWithArrayWithTransformTemplate.TYPE -> return WithArrayWithTransform(EntityWithArrayWithTransformTemplate(env, parent?.value() as EntityWithArrayWithTransformTemplate?, topLevel, json))
                EntityWithComplexPropertyTemplate.TYPE -> return WithComplexProperty(EntityWithComplexPropertyTemplate(env, parent?.value() as EntityWithComplexPropertyTemplate?, topLevel, json))
                EntityWithComplexPropertyWithDefaultValueTemplate.TYPE -> return WithComplexPropertyWithDefaultValue(EntityWithComplexPropertyWithDefaultValueTemplate(env, parent?.value() as EntityWithComplexPropertyWithDefaultValueTemplate?, topLevel, json))
                EntityWithEntityPropertyTemplate.TYPE -> return WithEntityProperty(EntityWithEntityPropertyTemplate(env, parent?.value() as EntityWithEntityPropertyTemplate?, topLevel, json))
                EntityWithOptionalComplexPropertyTemplate.TYPE -> return WithOptionalComplexProperty(EntityWithOptionalComplexPropertyTemplate(env, parent?.value() as EntityWithOptionalComplexPropertyTemplate?, topLevel, json))
                EntityWithOptionalPropertyTemplate.TYPE -> return WithOptionalProperty(EntityWithOptionalPropertyTemplate(env, parent?.value() as EntityWithOptionalPropertyTemplate?, topLevel, json))
                EntityWithOptionalStringEnumPropertyTemplate.TYPE -> return WithOptionalStringEnumProperty(EntityWithOptionalStringEnumPropertyTemplate(env, parent?.value() as EntityWithOptionalStringEnumPropertyTemplate?, topLevel, json))
                EntityWithPropertyWithDefaultValueTemplate.TYPE -> return WithPropertyWithDefaultValue(EntityWithPropertyWithDefaultValueTemplate(env, parent?.value() as EntityWithPropertyWithDefaultValueTemplate?, topLevel, json))
                EntityWithRequiredPropertyTemplate.TYPE -> return WithRequiredProperty(EntityWithRequiredPropertyTemplate(env, parent?.value() as EntityWithRequiredPropertyTemplate?, topLevel, json))
                EntityWithSimplePropertiesTemplate.TYPE -> return WithSimpleProperties(EntityWithSimplePropertiesTemplate(env, parent?.value() as EntityWithSimplePropertiesTemplate?, topLevel, json))
                EntityWithStrictArrayTemplate.TYPE -> return WithStrictArray(EntityWithStrictArrayTemplate(env, parent?.value() as EntityWithStrictArrayTemplate?, topLevel, json))
                EntityWithStringArrayPropertyTemplate.TYPE -> return WithStringArrayProperty(EntityWithStringArrayPropertyTemplate(env, parent?.value() as EntityWithStringArrayPropertyTemplate?, topLevel, json))
                EntityWithStringEnumPropertyTemplate.TYPE -> return WithStringEnumProperty(EntityWithStringEnumPropertyTemplate(env, parent?.value() as EntityWithStringEnumPropertyTemplate?, topLevel, json))
                EntityWithStringEnumPropertyWithDefaultValueTemplate.TYPE -> return WithStringEnumPropertyWithDefaultValue(EntityWithStringEnumPropertyWithDefaultValueTemplate(env, parent?.value() as EntityWithStringEnumPropertyWithDefaultValueTemplate?, topLevel, json))
                EntityWithoutPropertiesTemplate.TYPE -> return WithoutProperties(EntityWithoutPropertiesTemplate(env, parent?.value() as EntityWithoutPropertiesTemplate?, topLevel, json))
                else -> throw typeMismatch(json = json, key = "type", value = type)
            }
        }
        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityTemplate(env, json = it) }
    }
}
