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

public class EntityWithComplexPropertyWithDefaultValueTemplate @InternalApi constructor (
    @JvmField public val property: Field<ComplexPropertyTemplate>,
) : Template<EntityWithComplexPropertyWithDefaultValue> {

    public companion object {
        public const val TYPE: String = "entity_with_complex_property_with_default_value"
    }

    public class ComplexPropertyTemplate @InternalApi constructor (
        @JvmField public val value: Field<Expression<String>>,
    ) : Template<EntityWithComplexPropertyWithDefaultValue.ComplexProperty> {
    }
}
