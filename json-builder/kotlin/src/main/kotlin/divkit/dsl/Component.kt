package divkit.dsl

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.HashMap
import kotlin.Any
import kotlin.String
import kotlin.collections.Map

class Component<T : Div> internal constructor(
    @JsonIgnore
    val template: Template<T>,
    @JsonIgnore
    val properties: Map<String, Any>,
) : Div {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> {
        val result = HashMap<String, Any>(properties.size + 1)
        result.put("type", template.name)
        result.putAll(properties)
        return result
    }
}
