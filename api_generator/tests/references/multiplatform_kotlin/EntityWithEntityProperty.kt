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

public class EntityWithEntityProperty @InternalApi constructor (
    @JvmField public val entity: Entity = ENTITY_DEFAULT_VALUE, // default value: Entity.WithStringEnumProperty(EntityWithStringEnumProperty(property = Expression.constant(EntityWithStringEnumProperty.Property.SECOND)))
) {

    @InternalApi
    public fun copy(
        entity: Entity = this.entity,
    ): EntityWithEntityProperty = EntityWithEntityProperty(
        entity = entity,
    )

    public companion object {
        public const val TYPE: String = "entity_with_entity_property"

        private val ENTITY_DEFAULT_VALUE: Entity = Entity.WithStringEnumProperty(EntityWithStringEnumProperty(property = Expression.constant(EntityWithStringEnumProperty.Property.SECOND)))
    }
}
