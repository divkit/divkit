package com.yandex.div.compose.text

import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.yandex.div.compose.dagger.DivViewScope
import javax.inject.Inject

/**
 * The texts of the text elements of a view that have an id: a validator of an input names its label by
 * `label_id`, and the text of that label is the error of the input while the validator fails.
 *
 * Several composed elements can share an id (siblings, `item_builder` copies, a tooltip of the view); the text of
 * the first [Label] that set it is the text of the id, as the View renderer finds the first view with the id, and
 * the next label takes over when it is removed.
 */
@DivViewScope
internal class LabelTextStorage @Inject constructor() {
    private val texts: SnapshotStateMap<String, String> = mutableStateMapOf()
    private val labels = mutableMapOf<String, LinkedHashMap<Label, String>>()

    operator fun get(id: String): String? = texts[id]

    /**
     * A text element with [id]: it publishes its text with [Label.set] and withdraws it with [Label.remove]. A label
     * remembered in a composition withdraws its text when the composition forgets it.
     */
    fun label(id: String): Label = Label(id)

    inner class Label internal constructor(private val id: String) : RememberObserver {
        fun set(text: String) {
            val byLabel = labels.getOrPut(id) { LinkedHashMap() }
            byLabel[this] = text
            publish(id, byLabel)
        }

        fun remove() {
            val byLabel = labels[id] ?: return
            if (byLabel.remove(this) == null) return
            if (byLabel.isEmpty()) {
                labels.remove(id)
                texts.remove(id)
            } else {
                publish(id, byLabel)
            }
        }

        override fun onRemembered() = Unit

        override fun onForgotten() = remove()

        override fun onAbandoned() = Unit
    }

    private fun publish(id: String, byLabel: LinkedHashMap<Label, String>) {
        val text = byLabel.values.first()
        // A recomposed label sets the same text again; skip the snapshot write.
        if (texts[id] != text) texts[id] = text
    }
}
