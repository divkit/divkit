package com.yandex.div.core

import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context.Companion.RESET_ALL
import com.yandex.div.core.Div2Context.Companion.RESET_ERROR_COLLECTORS
import com.yandex.div.core.Div2Context.Companion.RESET_EXPRESSION_RUNTIMES
import com.yandex.div.core.Div2Context.Companion.RESET_SELECTED_STATES
import com.yandex.div.core.Div2Context.Companion.RESET_VISIBILITY_COUNTERS
import com.yandex.div.core.Div2Context.Companion.RESET_VIEW_STATES
import com.yandex.div.core.Div2Context.ResetFlag
import com.yandex.div.core.dagger.Div2Component
import com.yandex.div.core.dagger.DivDataComponent
import com.yandex.div.core.dagger.DivScope
import javax.inject.Inject

@DivScope
internal class DivDataComponentStore @Inject constructor() {

    private val components = mutableMapOf<String, DivDataComponent>()

    fun put(tag: String, component: DivDataComponent) {
        components[tag] = component
    }

    fun getOrPut(tag: String, div2Component: Div2Component) =
        components.getOrPut(tag) { div2Component.dataComponent.dataTag(tag).build() }

    fun reset(@ResetFlag flags: Int = RESET_ALL, tags: List<DivDataTag> = emptyList()) {
        if (flags == RESET_ALL) {
            remove(tags)
            return
        }

        if (tags.isEmpty()) {
            components.values.forEach { it.reset(flags) }
            return
        }

        tags.forEach {
            components[it.id]?.reset(flags)
        }
    }

    private fun remove(tags: List<DivDataTag>) {
        if (tags.isEmpty()) {
            components.clear()
            return
        }

        tags.forEach {
            components.remove(it.id)
        }
    }

    private fun DivDataComponent.reset(@ResetFlag flags: Int) {
        if (flags and RESET_EXPRESSION_RUNTIMES != 0) {
            runtimeStoreProvider.reset()
        }
        if (flags and RESET_VIEW_STATES != 0) {
            viewStateStore.reset()
        }
        if (flags and RESET_ERROR_COLLECTORS != 0) {
            errorCollectors.reset()
        }
        if (flags and RESET_SELECTED_STATES != 0) {
            stateManager.reset()
        }
        if (flags and RESET_VISIBILITY_COUNTERS != 0) {
            visibilityActionDispatcher.reset()
        }
    }
}
