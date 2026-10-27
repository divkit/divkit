package divkit.dsl

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import divkit.dsl.serialization.ComponentSerializer

@JsonSerialize(using = ComponentSerializer::class)
@ExposedCopyVisibility
data class Component<T : Div> internal constructor(
    val template: Template<T>,
    val properties: Map<String, Any>,
) : Div
