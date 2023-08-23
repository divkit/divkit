// Generated code. Do not modify.

package com.yandex.div2

import org.json.JSONObject

sealed class Entity {
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
    class WithStrictArray(val value: EntityWithStrictArray) : Entity()
    class WithStringArrayProperty(val value: EntityWithStringArrayProperty) : Entity()
    class WithStringEnumProperty(val value: EntityWithStringEnumProperty) : Entity()
    class WithStringEnumPropertyWithDefaultValue(val value: EntityWithStringEnumPropertyWithDefaultValue) : Entity()
    class WithoutProperties(val value: EntityWithoutProperties) : Entity()

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
            is WithStrictArray -> value
            is WithStringArrayProperty -> value
            is WithStringEnumProperty -> value
            is WithStringEnumPropertyWithDefaultValue -> value
            is WithoutProperties -> value
        }
    }

}
