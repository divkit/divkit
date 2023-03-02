package divkit.dsl

import divkit.dsl.scope.DivScope
import kotlin.String
import kotlin.collections.Map

class Divan(
    val card: Data,
    val templates: Map<String, Div>,
)

fun divan(init: DivScope.() -> Data): Divan {
    val scope = DivScope()
    val card = init.invoke(scope)
    return Divan(
        card = card,
        templates = scope.templates.mapValues { (_, template) -> template.div }
    )
}
