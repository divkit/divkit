// Generated code. Do not modify.

package com.yandex.div2

import org.json.JSONObject

class EntityWithEntityProperty(
    @JvmField final val entity: Entity = ENTITY_DEFAULT_VALUE, // default value: Entity.WithStringEnumProperty(EntityWithStringEnumProperty(property = Expression.constant(EntityWithStringEnumProperty.Property.SECOND)))
) : Hashable {

    private var _hash: Int? = null 

    override fun hash(): Int {
        _hash?.let {
            return it
        }
        val hash = 
            entity.hash()
        _hash = hash
        return hash
    }

    fun copy(
        entity: Entity = this.entity,
    ) = EntityWithEntityProperty(
        entity = entity,
    )

    companion object {
        const val TYPE = "entity_with_entity_property"

        private val ENTITY_DEFAULT_VALUE = Entity.WithStringEnumProperty(EntityWithStringEnumProperty(property = Expression.constant(EntityWithStringEnumProperty.Property.SECOND)))
    }

}
