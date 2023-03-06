package divkit.dsl

import divkit.dsl.core.*
import divkit.dsl.scope.DivScope
import divkit.dsl.scope.TemplateScope
import kotlin.Any
import kotlin.String
import kotlin.Unit
import kotlin.collections.Iterator
import kotlin.collections.List
import kotlin.collections.MutableMap


class Template<T : Div> internal constructor(
    val name: String,
    val div: T,
    val dependencies: List<Template<out Div>>,
    val supplements: Map<SupplementKey<*>, Supplement>,
)


fun <T : Div> template(name: String, builder: TemplateScope.() -> T): Template<T> {
    val scope = TemplateScope()
    val template = builder.invoke(scope)
    return Template(
        name = name,
        div = template,
        dependencies = scope.templates.values.toList(),
        supplements = scope.supplements,
    )
}


fun <T : Div> DivScope.render(
    template: Template<T>, vararg
    resolutions: Resolution<out Any>
): Component<T> = internalRender(
    template,
    resolutions.iterator()
)


fun <T : Div> DivScope.render(template: Template<T>, resolutions: List<Resolution<out Any>>):
        Component<T> = internalRender(template, resolutions.iterator())


private fun <T : Div> DivScope.internalRender(
    template: Template<T>,
    resolutions: Iterator<Resolution<out Any>>
): Component<T> {
    registerTemplates(this.templates, template)
    registerSupplements(this.supplements, template)
    val properties = mutableMapOf<String, Any>()
    for (resolution in resolutions) {
        when (resolution) {
            is FinalResolution -> {
                properties[resolution.reference.name] = resolution.value
            }

            is ProxyResolution -> {
                properties["$" + resolution.reference.name] = resolution.proxy.name
            }
        }
    }
    return Component(
        template = template,
        properties = properties
    )
}


private fun registerTemplates(
    registry: MutableMap<String, Template<out Div>>,
    template: Template<out Div>
) {
    registry[template.name] = template
    for (dependency in template.dependencies) {
        registerTemplates(registry, dependency)
    }
}

private fun registerSupplements(
    registry: MutableMap<SupplementKey<*>, Supplement>,
    template: Template<out Div>
) {
    for ((key, value) in template.supplements) {
        val existing = registry[key]
        if (existing != null) {
            registry[key] = existing.extend(value)
        } else {
            registry[key] = value
        }
    }
    for (dependency in template.dependencies) {
        registerSupplements(registry, dependency)
    }
}
