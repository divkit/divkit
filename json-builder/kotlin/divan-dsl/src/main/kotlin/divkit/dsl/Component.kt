package divkit.dsl

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonIgnore

@ExposedCopyVisibility
data class Component<T : Div> internal constructor(
    @JsonIgnore
    val template: Template<T>,
    @JsonIgnore
    val properties: Map<String, Any>,
) : Div {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> {
        val result = HashMap<String, Any>(properties.size + 1)
        result["type"] = template.name
        result.putAll(properties)
        return result
    }
}
