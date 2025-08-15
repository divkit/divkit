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

internal class EntityJsonParser(
    private val component: JsonParserComponent
) {

    class EntityParserImpl(
        private val component: JsonParserComponent
    ) : Parser<JSONObject, Entity> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, data: JSONObject): Entity {
            val type: String = JsonPropertyParser.readString(context, data, "type")
            when (type) {
                EntityWithArray.TYPE -> return Entity.WithArray(component.entityWithArrayJsonEntityParser.value.deserialize(context, data))
                EntityWithArrayOfEnums.TYPE -> return Entity.WithArrayOfEnums(component.entityWithArrayOfEnumsJsonEntityParser.value.deserialize(context, data))
                EntityWithArrayOfExpressions.TYPE -> return Entity.WithArrayOfExpressions(component.entityWithArrayOfExpressionsJsonEntityParser.value.deserialize(context, data))
                EntityWithArrayOfNestedItems.TYPE -> return Entity.WithArrayOfNestedItems(component.entityWithArrayOfNestedItemsJsonEntityParser.value.deserialize(context, data))
                EntityWithArrayWithTransform.TYPE -> return Entity.WithArrayWithTransform(component.entityWithArrayWithTransformJsonEntityParser.value.deserialize(context, data))
                EntityWithComplexProperty.TYPE -> return Entity.WithComplexProperty(component.entityWithComplexPropertyJsonEntityParser.value.deserialize(context, data))
                EntityWithComplexPropertyWithDefaultValue.TYPE -> return Entity.WithComplexPropertyWithDefaultValue(component.entityWithComplexPropertyWithDefaultValueJsonEntityParser.value.deserialize(context, data))
                EntityWithEntityProperty.TYPE -> return Entity.WithEntityProperty(component.entityWithEntityPropertyJsonEntityParser.value.deserialize(context, data))
                EntityWithOptionalComplexProperty.TYPE -> return Entity.WithOptionalComplexProperty(component.entityWithOptionalComplexPropertyJsonEntityParser.value.deserialize(context, data))
                EntityWithOptionalProperty.TYPE -> return Entity.WithOptionalProperty(component.entityWithOptionalPropertyJsonEntityParser.value.deserialize(context, data))
                EntityWithOptionalStringEnumProperty.TYPE -> return Entity.WithOptionalStringEnumProperty(component.entityWithOptionalStringEnumPropertyJsonEntityParser.value.deserialize(context, data))
                EntityWithPropertyWithDefaultValue.TYPE -> return Entity.WithPropertyWithDefaultValue(component.entityWithPropertyWithDefaultValueJsonEntityParser.value.deserialize(context, data))
                EntityWithRawArray.TYPE -> return Entity.WithRawArray(component.entityWithRawArrayJsonEntityParser.value.deserialize(context, data))
                EntityWithRequiredProperty.TYPE -> return Entity.WithRequiredProperty(component.entityWithRequiredPropertyJsonEntityParser.value.deserialize(context, data))
                EntityWithSimpleProperties.TYPE -> return Entity.WithSimpleProperties(component.entityWithSimplePropertiesJsonEntityParser.value.deserialize(context, data))
                EntityWithStringArrayProperty.TYPE -> return Entity.WithStringArrayProperty(component.entityWithStringArrayPropertyJsonEntityParser.value.deserialize(context, data))
                EntityWithStringEnumProperty.TYPE -> return Entity.WithStringEnumProperty(component.entityWithStringEnumPropertyJsonEntityParser.value.deserialize(context, data))
                EntityWithStringEnumPropertyWithDefaultValue.TYPE -> return Entity.WithStringEnumPropertyWithDefaultValue(component.entityWithStringEnumPropertyWithDefaultValueJsonEntityParser.value.deserialize(context, data))
                EntityWithoutProperties.TYPE -> return Entity.WithoutProperties(component.entityWithoutPropertiesJsonEntityParser.value.deserialize(context, data))
            }

            val template = context.templates.getOrThrow(type, data) as? EntityTemplate
            if (template != null) {
                return component.entityJsonTemplateResolver
                    .value
                    .resolve(context, template, data)
            } else {
                throw typeMismatch(json = data, key = "type", value = type)
            }
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: Entity): JSONObject {
            return when (value) {
                is Entity.WithArray -> component.entityWithArrayJsonEntityParser.value.serialize(context, value.value)
                is Entity.WithArrayOfEnums -> component.entityWithArrayOfEnumsJsonEntityParser.value.serialize(context, value.value)
                is Entity.WithArrayOfExpressions -> component.entityWithArrayOfExpressionsJsonEntityParser.value.serialize(context, value.value)
                is Entity.WithArrayOfNestedItems -> component.entityWithArrayOfNestedItemsJsonEntityParser.value.serialize(context, value.value)
                is Entity.WithArrayWithTransform -> component.entityWithArrayWithTransformJsonEntityParser.value.serialize(context, value.value)
                is Entity.WithComplexProperty -> component.entityWithComplexPropertyJsonEntityParser.value.serialize(context, value.value)
                is Entity.WithComplexPropertyWithDefaultValue -> component.entityWithComplexPropertyWithDefaultValueJsonEntityParser.value.serialize(context, value.value)
                is Entity.WithEntityProperty -> component.entityWithEntityPropertyJsonEntityParser.value.serialize(context, value.value)
                is Entity.WithOptionalComplexProperty -> component.entityWithOptionalComplexPropertyJsonEntityParser.value.serialize(context, value.value)
                is Entity.WithOptionalProperty -> component.entityWithOptionalPropertyJsonEntityParser.value.serialize(context, value.value)
                is Entity.WithOptionalStringEnumProperty -> component.entityWithOptionalStringEnumPropertyJsonEntityParser.value.serialize(context, value.value)
                is Entity.WithPropertyWithDefaultValue -> component.entityWithPropertyWithDefaultValueJsonEntityParser.value.serialize(context, value.value)
                is Entity.WithRawArray -> component.entityWithRawArrayJsonEntityParser.value.serialize(context, value.value)
                is Entity.WithRequiredProperty -> component.entityWithRequiredPropertyJsonEntityParser.value.serialize(context, value.value)
                is Entity.WithSimpleProperties -> component.entityWithSimplePropertiesJsonEntityParser.value.serialize(context, value.value)
                is Entity.WithStringArrayProperty -> component.entityWithStringArrayPropertyJsonEntityParser.value.serialize(context, value.value)
                is Entity.WithStringEnumProperty -> component.entityWithStringEnumPropertyJsonEntityParser.value.serialize(context, value.value)
                is Entity.WithStringEnumPropertyWithDefaultValue -> component.entityWithStringEnumPropertyWithDefaultValueJsonEntityParser.value.serialize(context, value.value)
                is Entity.WithoutProperties -> component.entityWithoutPropertiesJsonEntityParser.value.serialize(context, value.value)
            }
        }
    }

    class TemplateParserImpl(
        private val component: JsonParserComponent
    ) : Parser<JSONObject, EntityTemplate> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, data: JSONObject): EntityTemplate {
            val extendedType = JsonPropertyParser.readString(context, data, "type")
            val parent = context.templates[extendedType] as? EntityTemplate
            val type = parent?.type ?: extendedType
            when (type) {
                EntityWithArrayTemplate.TYPE -> return EntityTemplate.WithArray(component.entityWithArrayJsonTemplateParser.value.deserialize(context, parent?.value() as EntityWithArrayTemplate?, data))
                EntityWithArrayOfEnumsTemplate.TYPE -> return EntityTemplate.WithArrayOfEnums(component.entityWithArrayOfEnumsJsonTemplateParser.value.deserialize(context, parent?.value() as EntityWithArrayOfEnumsTemplate?, data))
                EntityWithArrayOfExpressionsTemplate.TYPE -> return EntityTemplate.WithArrayOfExpressions(component.entityWithArrayOfExpressionsJsonTemplateParser.value.deserialize(context, parent?.value() as EntityWithArrayOfExpressionsTemplate?, data))
                EntityWithArrayOfNestedItemsTemplate.TYPE -> return EntityTemplate.WithArrayOfNestedItems(component.entityWithArrayOfNestedItemsJsonTemplateParser.value.deserialize(context, parent?.value() as EntityWithArrayOfNestedItemsTemplate?, data))
                EntityWithArrayWithTransformTemplate.TYPE -> return EntityTemplate.WithArrayWithTransform(component.entityWithArrayWithTransformJsonTemplateParser.value.deserialize(context, parent?.value() as EntityWithArrayWithTransformTemplate?, data))
                EntityWithComplexPropertyTemplate.TYPE -> return EntityTemplate.WithComplexProperty(component.entityWithComplexPropertyJsonTemplateParser.value.deserialize(context, parent?.value() as EntityWithComplexPropertyTemplate?, data))
                EntityWithComplexPropertyWithDefaultValueTemplate.TYPE -> return EntityTemplate.WithComplexPropertyWithDefaultValue(component.entityWithComplexPropertyWithDefaultValueJsonTemplateParser.value.deserialize(context, parent?.value() as EntityWithComplexPropertyWithDefaultValueTemplate?, data))
                EntityWithEntityPropertyTemplate.TYPE -> return EntityTemplate.WithEntityProperty(component.entityWithEntityPropertyJsonTemplateParser.value.deserialize(context, parent?.value() as EntityWithEntityPropertyTemplate?, data))
                EntityWithOptionalComplexPropertyTemplate.TYPE -> return EntityTemplate.WithOptionalComplexProperty(component.entityWithOptionalComplexPropertyJsonTemplateParser.value.deserialize(context, parent?.value() as EntityWithOptionalComplexPropertyTemplate?, data))
                EntityWithOptionalPropertyTemplate.TYPE -> return EntityTemplate.WithOptionalProperty(component.entityWithOptionalPropertyJsonTemplateParser.value.deserialize(context, parent?.value() as EntityWithOptionalPropertyTemplate?, data))
                EntityWithOptionalStringEnumPropertyTemplate.TYPE -> return EntityTemplate.WithOptionalStringEnumProperty(component.entityWithOptionalStringEnumPropertyJsonTemplateParser.value.deserialize(context, parent?.value() as EntityWithOptionalStringEnumPropertyTemplate?, data))
                EntityWithPropertyWithDefaultValueTemplate.TYPE -> return EntityTemplate.WithPropertyWithDefaultValue(component.entityWithPropertyWithDefaultValueJsonTemplateParser.value.deserialize(context, parent?.value() as EntityWithPropertyWithDefaultValueTemplate?, data))
                EntityWithRawArrayTemplate.TYPE -> return EntityTemplate.WithRawArray(component.entityWithRawArrayJsonTemplateParser.value.deserialize(context, parent?.value() as EntityWithRawArrayTemplate?, data))
                EntityWithRequiredPropertyTemplate.TYPE -> return EntityTemplate.WithRequiredProperty(component.entityWithRequiredPropertyJsonTemplateParser.value.deserialize(context, parent?.value() as EntityWithRequiredPropertyTemplate?, data))
                EntityWithSimplePropertiesTemplate.TYPE -> return EntityTemplate.WithSimpleProperties(component.entityWithSimplePropertiesJsonTemplateParser.value.deserialize(context, parent?.value() as EntityWithSimplePropertiesTemplate?, data))
                EntityWithStringArrayPropertyTemplate.TYPE -> return EntityTemplate.WithStringArrayProperty(component.entityWithStringArrayPropertyJsonTemplateParser.value.deserialize(context, parent?.value() as EntityWithStringArrayPropertyTemplate?, data))
                EntityWithStringEnumPropertyTemplate.TYPE -> return EntityTemplate.WithStringEnumProperty(component.entityWithStringEnumPropertyJsonTemplateParser.value.deserialize(context, parent?.value() as EntityWithStringEnumPropertyTemplate?, data))
                EntityWithStringEnumPropertyWithDefaultValueTemplate.TYPE -> return EntityTemplate.WithStringEnumPropertyWithDefaultValue(component.entityWithStringEnumPropertyWithDefaultValueJsonTemplateParser.value.deserialize(context, parent?.value() as EntityWithStringEnumPropertyWithDefaultValueTemplate?, data))
                EntityWithoutPropertiesTemplate.TYPE -> return EntityTemplate.WithoutProperties(component.entityWithoutPropertiesJsonTemplateParser.value.deserialize(context, parent?.value() as EntityWithoutPropertiesTemplate?, data))
                else -> throw typeMismatch(json = data, key = "type", value = type)
            }
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityTemplate): JSONObject {
            return when (value) {
                is EntityTemplate.WithArray -> component.entityWithArrayJsonTemplateParser.value.serialize(context, value.value)
                is EntityTemplate.WithArrayOfEnums -> component.entityWithArrayOfEnumsJsonTemplateParser.value.serialize(context, value.value)
                is EntityTemplate.WithArrayOfExpressions -> component.entityWithArrayOfExpressionsJsonTemplateParser.value.serialize(context, value.value)
                is EntityTemplate.WithArrayOfNestedItems -> component.entityWithArrayOfNestedItemsJsonTemplateParser.value.serialize(context, value.value)
                is EntityTemplate.WithArrayWithTransform -> component.entityWithArrayWithTransformJsonTemplateParser.value.serialize(context, value.value)
                is EntityTemplate.WithComplexProperty -> component.entityWithComplexPropertyJsonTemplateParser.value.serialize(context, value.value)
                is EntityTemplate.WithComplexPropertyWithDefaultValue -> component.entityWithComplexPropertyWithDefaultValueJsonTemplateParser.value.serialize(context, value.value)
                is EntityTemplate.WithEntityProperty -> component.entityWithEntityPropertyJsonTemplateParser.value.serialize(context, value.value)
                is EntityTemplate.WithOptionalComplexProperty -> component.entityWithOptionalComplexPropertyJsonTemplateParser.value.serialize(context, value.value)
                is EntityTemplate.WithOptionalProperty -> component.entityWithOptionalPropertyJsonTemplateParser.value.serialize(context, value.value)
                is EntityTemplate.WithOptionalStringEnumProperty -> component.entityWithOptionalStringEnumPropertyJsonTemplateParser.value.serialize(context, value.value)
                is EntityTemplate.WithPropertyWithDefaultValue -> component.entityWithPropertyWithDefaultValueJsonTemplateParser.value.serialize(context, value.value)
                is EntityTemplate.WithRawArray -> component.entityWithRawArrayJsonTemplateParser.value.serialize(context, value.value)
                is EntityTemplate.WithRequiredProperty -> component.entityWithRequiredPropertyJsonTemplateParser.value.serialize(context, value.value)
                is EntityTemplate.WithSimpleProperties -> component.entityWithSimplePropertiesJsonTemplateParser.value.serialize(context, value.value)
                is EntityTemplate.WithStringArrayProperty -> component.entityWithStringArrayPropertyJsonTemplateParser.value.serialize(context, value.value)
                is EntityTemplate.WithStringEnumProperty -> component.entityWithStringEnumPropertyJsonTemplateParser.value.serialize(context, value.value)
                is EntityTemplate.WithStringEnumPropertyWithDefaultValue -> component.entityWithStringEnumPropertyWithDefaultValueJsonTemplateParser.value.serialize(context, value.value)
                is EntityTemplate.WithoutProperties -> component.entityWithoutPropertiesJsonTemplateParser.value.serialize(context, value.value)
            }
        }
    }

    class TemplateResolverImpl(
        private val component: JsonParserComponent
    ) : TemplateResolver<JSONObject, EntityTemplate, Entity> {

        @Throws(ParsingException::class)
        override fun resolve(context: ParsingContext, template: EntityTemplate, data: JSONObject): Entity {
            return when (template) {
                is EntityTemplate.WithArray -> Entity.WithArray(component.entityWithArrayJsonTemplateResolver.value.resolve(context, template.value, data))
                is EntityTemplate.WithArrayOfEnums -> Entity.WithArrayOfEnums(component.entityWithArrayOfEnumsJsonTemplateResolver.value.resolve(context, template.value, data))
                is EntityTemplate.WithArrayOfExpressions -> Entity.WithArrayOfExpressions(component.entityWithArrayOfExpressionsJsonTemplateResolver.value.resolve(context, template.value, data))
                is EntityTemplate.WithArrayOfNestedItems -> Entity.WithArrayOfNestedItems(component.entityWithArrayOfNestedItemsJsonTemplateResolver.value.resolve(context, template.value, data))
                is EntityTemplate.WithArrayWithTransform -> Entity.WithArrayWithTransform(component.entityWithArrayWithTransformJsonTemplateResolver.value.resolve(context, template.value, data))
                is EntityTemplate.WithComplexProperty -> Entity.WithComplexProperty(component.entityWithComplexPropertyJsonTemplateResolver.value.resolve(context, template.value, data))
                is EntityTemplate.WithComplexPropertyWithDefaultValue -> Entity.WithComplexPropertyWithDefaultValue(component.entityWithComplexPropertyWithDefaultValueJsonTemplateResolver.value.resolve(context, template.value, data))
                is EntityTemplate.WithEntityProperty -> Entity.WithEntityProperty(component.entityWithEntityPropertyJsonTemplateResolver.value.resolve(context, template.value, data))
                is EntityTemplate.WithOptionalComplexProperty -> Entity.WithOptionalComplexProperty(component.entityWithOptionalComplexPropertyJsonTemplateResolver.value.resolve(context, template.value, data))
                is EntityTemplate.WithOptionalProperty -> Entity.WithOptionalProperty(component.entityWithOptionalPropertyJsonTemplateResolver.value.resolve(context, template.value, data))
                is EntityTemplate.WithOptionalStringEnumProperty -> Entity.WithOptionalStringEnumProperty(component.entityWithOptionalStringEnumPropertyJsonTemplateResolver.value.resolve(context, template.value, data))
                is EntityTemplate.WithPropertyWithDefaultValue -> Entity.WithPropertyWithDefaultValue(component.entityWithPropertyWithDefaultValueJsonTemplateResolver.value.resolve(context, template.value, data))
                is EntityTemplate.WithRawArray -> Entity.WithRawArray(component.entityWithRawArrayJsonTemplateResolver.value.resolve(context, template.value, data))
                is EntityTemplate.WithRequiredProperty -> Entity.WithRequiredProperty(component.entityWithRequiredPropertyJsonTemplateResolver.value.resolve(context, template.value, data))
                is EntityTemplate.WithSimpleProperties -> Entity.WithSimpleProperties(component.entityWithSimplePropertiesJsonTemplateResolver.value.resolve(context, template.value, data))
                is EntityTemplate.WithStringArrayProperty -> Entity.WithStringArrayProperty(component.entityWithStringArrayPropertyJsonTemplateResolver.value.resolve(context, template.value, data))
                is EntityTemplate.WithStringEnumProperty -> Entity.WithStringEnumProperty(component.entityWithStringEnumPropertyJsonTemplateResolver.value.resolve(context, template.value, data))
                is EntityTemplate.WithStringEnumPropertyWithDefaultValue -> Entity.WithStringEnumPropertyWithDefaultValue(component.entityWithStringEnumPropertyWithDefaultValueJsonTemplateResolver.value.resolve(context, template.value, data))
                is EntityTemplate.WithoutProperties -> Entity.WithoutProperties(component.entityWithoutPropertiesJsonTemplateResolver.value.resolve(context, template.value, data))
            }
        }
    }
}
