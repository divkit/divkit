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

public class EntityWithComplexPropertyWithDefaultValue @InternalApi constructor (
    @JvmField public val property: ComplexProperty = PROPERTY_DEFAULT_VALUE, // default value: EntityWithComplexPropertyWithDefaultValue.ComplexProperty(value = Expression.constant("Default text"))
) {

    @InternalApi
    public fun copy(
        property: ComplexProperty = this.property,
    ): EntityWithComplexPropertyWithDefaultValue = EntityWithComplexPropertyWithDefaultValue(
        property = property,
    )

    public companion object {
        public const val TYPE: String = "entity_with_complex_property_with_default_value"

        private val PROPERTY_DEFAULT_VALUE: ComplexProperty = EntityWithComplexPropertyWithDefaultValue.ComplexProperty(value = Expression.constant("Default text"))
    }

    public class ComplexProperty @InternalApi constructor (
        @JvmField public val value: Expression<String>,
    ) {

        @InternalApi
        public fun copy(
            value: Expression<String> = this.value,
        ): ComplexProperty = ComplexProperty(
            value = value,
        )
    }
}
