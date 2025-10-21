package divkit.dsl.scope

import divkit.dsl.Div
import divkit.dsl.Template
import divkit.dsl.core.Supplement
import divkit.dsl.core.SupplementKey
import kotlin.String
import kotlin.collections.MutableMap

open class DivScope internal constructor() {
    internal val templates: MutableMap<String, Template<out Div>> = mutableMapOf()
    internal val supplements: MutableMap<SupplementKey<*>, Supplement> = mutableMapOf()
}
