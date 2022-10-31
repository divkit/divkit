@file:Suppress(
    "unused",
    "UNUSED_PARAMETER",
)

package divan

import com.fasterxml.jackson.annotation.JsonAnyGetter
import divan.annotation.Generated
import divan.scope.DivScope
import kotlin.Any
import kotlin.String
import kotlin.Suppress
import kotlin.collections.Map

/**
 *
 * Можно создать при помощи метода [entity_without_properties].
 *
 * Обязательные поля: type
 */
@Generated
class Entity_without_properties internal constructor() : Entity {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = mapOf("type" to
            "entity_without_properties")
}

@Generated
fun DivScope.entity_without_properties(): Entity_without_properties =
        Entity_without_properties()
