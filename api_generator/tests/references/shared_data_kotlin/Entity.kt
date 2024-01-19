// Generated code. Do not modify.

package com.yandex.div2

import org.json.JSONObject

sealed class Entity : Hashable {
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

    private var _hash: Int? = null 

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

}
