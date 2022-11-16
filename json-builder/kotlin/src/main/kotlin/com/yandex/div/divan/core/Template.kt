package com.yandex.div.divan.core

import com.yandex.div.divan.scope.DivScope
import com.yandex.div.divan.scope.TemplateScope
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
)


fun <T : Div> template(name: String, builder: TemplateScope.() -> T): Template<T> {
    val scope = TemplateScope()
    val template = builder.invoke(scope)
    return Template(
        name = name,
        div = template,
        dependencies = scope.templates.values.toList()
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
): Unit {
    registry[template.name] = template
    for (dependency in template.dependencies) {
        registerTemplates(registry, dependency)
    }
}
