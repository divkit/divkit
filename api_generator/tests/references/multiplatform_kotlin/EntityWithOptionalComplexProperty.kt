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

public class EntityWithOptionalComplexProperty @InternalApi constructor (
    @JvmField public val property: ComplexProperty? = null,
) {

    @InternalApi
    public fun copy(
        property: ComplexProperty? = this.property,
    ): EntityWithOptionalComplexProperty = EntityWithOptionalComplexProperty(
        property = property,
    )

    public companion object {
        public const val TYPE: String = "entity_with_optional_complex_property"
    }

    public class ComplexProperty @InternalApi constructor (
        @JvmField public val value: Expression<Url>,
    ) {

        @InternalApi
        public fun copy(
            value: Expression<Url> = this.value,
        ): ComplexProperty = ComplexProperty(
            value = value,
        )
    }
}
