package divkit.dsl

import divkit.dsl.core.*
import divkit.dsl.scope.DivScope
import divkit.dsl.scope.TemplateScope
import kotlin.Any
import kotlin.String
import kotlin.collections.Iterator
import kotlin.collections.List
import kotlin.collections.MutableMap


class Template<T : Div> internal constructor(
    val name: String,
    val div: T,
    val dependencies: List<Template<out Div>>,
    val supplements: Map<SupplementKey<*>, Supplement>,
) {
    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other == null) return false
        if (other !is Template<*>) {
            return false
        }
        return name == other.name &&
            div == other.div &&
            dependencies == other.dependencies
    }

    override fun hashCode(): Int {
        var result = 1
        result = 31 * result + name.hashCode()
        result = 31 * result + div.hashCode()
        result = 31 * result + dependencies.hashCode()
        return result
    }
}


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

fun <T : Div> template(nameBuilder: (Div) -> String, builder: TemplateScope.() -> T): Template<T> {
    val scope = TemplateScope()
    val template = builder.invoke(scope)
    return Template(
        name = nameBuilder(template),
        div = template,
        dependencies = scope.templates.values.toList(),
        supplements = scope.supplements,
    )
}

// JvmName is set to prevent jvm signature clash
@JvmName("renderDiv")
fun <T : Div> DivScope.render(
    template: Template<T>, vararg
    resolutions: Resolution<out Any>
): Component<T> = internalRenderDiv(
    template,
    resolutions.iterator()
)


// JvmName is set to prevent jvm signature clash
@JvmName("renderComponent")
fun <T : Div> DivScope.render(
    template: Template<Component<T>>, vararg
    resolutions: Resolution<out Any>
): Component<T> = internalRenderComponent(
    template,
    resolutions.iterator()
)


fun <T : Div> DivScope.render(template: Template<T>, resolutions: List<Resolution<out Any>>):
        Component<T> = internalRenderDiv(template, resolutions.iterator())


private fun <T : Div> DivScope.internalRenderDiv(
    template: Template<T>,
    resolutions: Iterator<Resolution<out Any>>
): Component<T> {
    registerTemplates(this.templates, template)
    registerSupplements(this.supplements, template)
    val properties = mutableMapOf<String, Any>()
    for (resolution in resolutions) {
        when (resolution) {
            is FinalResolution -> {
                properties[resolution.reference.name] = resolveValue(resolution.value)
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

private fun <T : Div> DivScope.internalRenderComponent(
    template: Template<Component<T>>,
    resolutions: Iterator<Resolution<out Any>>
): Component<T> {
    registerTemplates(this.templates, template)
    registerSupplements(this.supplements, template)
    val properties = mutableMapOf<String, Any>()
    for (resolution in resolutions) {
        when (resolution) {
            is FinalResolution -> {
                properties[resolution.reference.name] = resolveValue(resolution.value)
            }

            is ProxyResolution -> {
                properties["$" + resolution.reference.name] = resolution.proxy.name
            }
        }
    }
    return Component(
        template = Template(
            name = template.name,
            div = template.div.template.div,
            dependencies = template.dependencies,
            supplements = template.supplements
        ),
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
