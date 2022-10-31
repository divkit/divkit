package divan.core

import divan.Data
import divan.annotation.Generated
import divan.scope.DivScope
import kotlin.String
import kotlin.collections.Map

@Generated
class Divan(
    val card: Data,
    val templates: Map<String, Div>,
)

@Generated
fun divan(init: DivScope.() -> Data): Divan {
    val scope = DivScope()
    val card = init.invoke(scope)
    return Divan(
    	card = card,
    	templates = scope.templates.mapValues { (_, template) -> template.div }
    )
}
