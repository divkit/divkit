package divkit.dsl

import com.fasterxml.jackson.annotation.JsonIgnore
import divkit.dsl.core.Supplement
import divkit.dsl.core.SupplementKey
import divkit.dsl.scope.DivScope
import kotlin.String
import kotlin.collections.Map

class DivanPatch internal constructor(
    val patch: Patch,
    val templates: Map<String, Div>,
    @JsonIgnore
    val supplements: Map<SupplementKey<*>, Supplement>,
)

fun divanPatch(init: DivScope.() -> Patch): DivanPatch {
    val scope = DivScope()
    val patch = init.invoke(scope)
    return DivanPatch(
        patch = patch,
        templates = scope.templates.mapValues { (_, template) -> template.div },
        supplements = scope.supplements
    )
}
