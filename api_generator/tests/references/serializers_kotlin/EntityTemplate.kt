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
    class WithRawArray(val value: EntityWithRawArrayTemplate) : EntityTemplate()
    class WithRequiredProperty(val value: EntityWithRequiredPropertyTemplate) : EntityTemplate()
    class WithSimpleProperties(val value: EntityWithSimplePropertiesTemplate) : EntityTemplate()
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
        return builtInParserComponent.entityJsonTemplateParser
            .value
            .serialize(context = builtInParsingContext, value = this)
    }

    override fun resolve(env: ParsingEnvironment, data: JSONObject): Entity {
        return builtInParserComponent.entityJsonTemplateResolver
            .value
            .resolve(context = env, template = this, data = data)
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
                is WithRawArray -> EntityWithRawArrayTemplate.TYPE
                is WithRequiredProperty -> EntityWithRequiredPropertyTemplate.TYPE
                is WithSimpleProperties -> EntityWithSimplePropertiesTemplate.TYPE
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
            return builtInParserComponent.entityJsonTemplateParser
                .value
                .deserialize(context = env, data = json)
        }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityTemplate(env, json = it) }
    }
}
