package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.VisibilityAction

@Generated
internal class VisibilityActionSerializer : EntitySerializer<VisibilityAction>(VisibilityAction::class.java) {
    override fun writeFields(value: VisibilityAction, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(DOWNLOAD_CALLBACKS, properties.downloadCallbacks, provider)
        gen.writeProperty(IS_ENABLED, properties.isEnabled, provider)
        gen.writeProperty(LOG_ID, properties.logId, provider)
        gen.writeProperty(LOG_LIMIT, properties.logLimit, provider)
        gen.writeProperty(PAYLOAD, properties.payload, provider)
        gen.writeProperty(REFERER, properties.referer, provider)
        gen.writeProperty(SCOPE_ID, properties.scopeId, provider)
        gen.writeProperty(TYPED, properties.typed, provider)
        gen.writeProperty(URL, properties.url, provider)
        gen.writeProperty(VISIBILITY_DURATION, properties.visibilityDuration, provider)
        gen.writeProperty(VISIBILITY_PERCENTAGE, properties.visibilityPercentage, provider)
    }

    private companion object {
        val DOWNLOAD_CALLBACKS = PropertyName("download_callbacks")
        val IS_ENABLED = PropertyName("is_enabled")
        val LOG_ID = PropertyName("log_id")
        val LOG_LIMIT = PropertyName("log_limit")
        val PAYLOAD = PropertyName("payload")
        val REFERER = PropertyName("referer")
        val SCOPE_ID = PropertyName("scope_id")
        val TYPED = PropertyName("typed")
        val URL = PropertyName("url")
        val VISIBILITY_DURATION = PropertyName("visibility_duration")
        val VISIBILITY_PERCENTAGE = PropertyName("visibility_percentage")
    }
}
