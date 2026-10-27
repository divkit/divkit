package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.Action

@Generated
internal class ActionSerializer : EntitySerializer<Action>(Action::class.java) {
    override fun writeFields(value: Action, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(DOWNLOAD_CALLBACKS, properties.downloadCallbacks, provider)
        gen.writeProperty(IS_ENABLED, properties.isEnabled, provider)
        gen.writeProperty(LOG_ID, properties.logId, provider)
        gen.writeProperty(LOG_URL, properties.logUrl, provider)
        gen.writeProperty(MENU_ITEMS, properties.menuItems, provider)
        gen.writeProperty(PAYLOAD, properties.payload, provider)
        gen.writeProperty(REFERER, properties.referer, provider)
        gen.writeProperty(SCOPE_ID, properties.scopeId, provider)
        gen.writeProperty(TARGET, properties.target, provider)
        gen.writeProperty(TYPED, properties.typed, provider)
        gen.writeProperty(URL, properties.url, provider)
    }

    private companion object {
        val DOWNLOAD_CALLBACKS = PropertyName("download_callbacks")
        val IS_ENABLED = PropertyName("is_enabled")
        val LOG_ID = PropertyName("log_id")
        val LOG_URL = PropertyName("log_url")
        val MENU_ITEMS = PropertyName("menu_items")
        val PAYLOAD = PropertyName("payload")
        val REFERER = PropertyName("referer")
        val SCOPE_ID = PropertyName("scope_id")
        val TARGET = PropertyName("target")
        val TYPED = PropertyName("typed")
        val URL = PropertyName("url")
    }
}

@Generated
internal class ActionMenuItemSerializer : EntitySerializer<Action.MenuItem>(Action.MenuItem::class.java) {
    override fun writeFields(value: Action.MenuItem, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ACTION, properties.action, provider)
        gen.writeProperty(ACTIONS, properties.actions, provider)
        gen.writeProperty(TEXT, properties.text, provider)
    }

    private companion object {
        val ACTION = PropertyName("action")
        val ACTIONS = PropertyName("actions")
        val TEXT = PropertyName("text")
    }
}
