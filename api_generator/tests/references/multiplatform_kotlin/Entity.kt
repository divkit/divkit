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

public sealed class Entity {
    public class WithArray(public val value: EntityWithArray) : Entity()
    public class WithArrayOfEnums(public val value: EntityWithArrayOfEnums) : Entity()
    public class WithArrayOfExpressions(public val value: EntityWithArrayOfExpressions) : Entity()
    public class WithArrayOfNestedItems(public val value: EntityWithArrayOfNestedItems) : Entity()
    public class WithArrayWithTransform(public val value: EntityWithArrayWithTransform) : Entity()
    public class WithComplexProperty(public val value: EntityWithComplexProperty) : Entity()
    public class WithComplexPropertyWithDefaultValue(public val value: EntityWithComplexPropertyWithDefaultValue) : Entity()
    public class WithEntityProperty(public val value: EntityWithEntityProperty) : Entity()
    public class WithOptionalComplexProperty(public val value: EntityWithOptionalComplexProperty) : Entity()
    public class WithOptionalProperty(public val value: EntityWithOptionalProperty) : Entity()
    public class WithOptionalStringEnumProperty(public val value: EntityWithOptionalStringEnumProperty) : Entity()
    public class WithPropertyWithDefaultValue(public val value: EntityWithPropertyWithDefaultValue) : Entity()
    public class WithRawArray(public val value: EntityWithRawArray) : Entity()
    public class WithRequiredProperty(public val value: EntityWithRequiredProperty) : Entity()
    public class WithSimpleProperties(public val value: EntityWithSimpleProperties) : Entity()
    public class WithStringArrayProperty(public val value: EntityWithStringArrayProperty) : Entity()
    public class WithStringEnumProperty(public val value: EntityWithStringEnumProperty) : Entity()
    public class WithStringEnumPropertyWithDefaultValue(public val value: EntityWithStringEnumPropertyWithDefaultValue) : Entity()
    public class WithoutProperties(public val value: EntityWithoutProperties) : Entity()

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
}
