// Generated code. Do not modify.

package com.yandex.div.reference

import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.core.json.JsonArray
import com.yandex.div.core.json.JsonObject
import com.yandex.div.core.type.Color
import com.yandex.div.core.type.Url
import com.yandex.div.model.entity.*
import com.yandex.div.model.expression.*
import com.yandex.div.model.entity.EntityTemplate as Template

public sealed class EntityTemplate : Template<Entity> {
    public class WithArray(public val value: EntityWithArrayTemplate) : EntityTemplate()
    public class WithArrayOfEnums(public val value: EntityWithArrayOfEnumsTemplate) : EntityTemplate()
    public class WithArrayOfExpressions(public val value: EntityWithArrayOfExpressionsTemplate) : EntityTemplate()
    public class WithArrayOfNestedItems(public val value: EntityWithArrayOfNestedItemsTemplate) : EntityTemplate()
    public class WithArrayWithTransform(public val value: EntityWithArrayWithTransformTemplate) : EntityTemplate()
    public class WithComplexProperty(public val value: EntityWithComplexPropertyTemplate) : EntityTemplate()
    public class WithComplexPropertyWithDefaultValue(public val value: EntityWithComplexPropertyWithDefaultValueTemplate) : EntityTemplate()
    public class WithEntityProperty(public val value: EntityWithEntityPropertyTemplate) : EntityTemplate()
    public class WithOptionalComplexProperty(public val value: EntityWithOptionalComplexPropertyTemplate) : EntityTemplate()
    public class WithOptionalProperty(public val value: EntityWithOptionalPropertyTemplate) : EntityTemplate()
    public class WithOptionalStringEnumProperty(public val value: EntityWithOptionalStringEnumPropertyTemplate) : EntityTemplate()
    public class WithPropertyWithDefaultValue(public val value: EntityWithPropertyWithDefaultValueTemplate) : EntityTemplate()
    public class WithRawArray(public val value: EntityWithRawArrayTemplate) : EntityTemplate()
    public class WithRequiredProperty(public val value: EntityWithRequiredPropertyTemplate) : EntityTemplate()
    public class WithSimpleProperties(public val value: EntityWithSimplePropertiesTemplate) : EntityTemplate()
    public class WithStringArrayProperty(public val value: EntityWithStringArrayPropertyTemplate) : EntityTemplate()
    public class WithStringEnumProperty(public val value: EntityWithStringEnumPropertyTemplate) : EntityTemplate()
    public class WithStringEnumPropertyWithDefaultValue(public val value: EntityWithStringEnumPropertyWithDefaultValueTemplate) : EntityTemplate()
    public class WithoutProperties(public val value: EntityWithoutPropertiesTemplate) : EntityTemplate()

    public fun value(): Any {
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

    public val type: String
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
}
