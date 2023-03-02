package divkit.dsl.scope

import divkit.dsl.Div
import divkit.dsl.Template
import kotlin.String
import kotlin.collections.MutableMap

open class DivScope internal constructor() {
    internal val templates: MutableMap<String, Template<out Div>> = mutableMapOf()
}
