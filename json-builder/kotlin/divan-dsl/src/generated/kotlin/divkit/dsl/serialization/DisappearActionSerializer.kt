package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.DisappearAction

@Generated
internal class DisappearActionSerializer : EntitySerializer<DisappearAction>(DisappearAction::class.java) {
    override fun writeFields(value: DisappearAction, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(DISAPPEAR_DURATION, properties.disappearDuration, provider)
        gen.writeProperty(DOWNLOAD_CALLBACKS, properties.downloadCallbacks, provider)
        gen.writeProperty(IS_ENABLED, properties.isEnabled, provider)
        gen.writeProperty(LOG_ID, properties.logId, provider)
        gen.writeProperty(LOG_LIMIT, properties.logLimit, provider)
        gen.writeProperty(PAYLOAD, properties.payload, provider)
        gen.writeProperty(REFERER, properties.referer, provider)
        gen.writeProperty(SCOPE_ID, properties.scopeId, provider)
        gen.writeProperty(TYPED, properties.typed, provider)
        gen.writeProperty(URL, properties.url, provider)
        gen.writeProperty(VISIBILITY_PERCENTAGE, properties.visibilityPercentage, provider)
    }

    private companion object {
        val DISAPPEAR_DURATION = PropertyName("disappear_duration")
        val DOWNLOAD_CALLBACKS = PropertyName("download_callbacks")
        val IS_ENABLED = PropertyName("is_enabled")
        val LOG_ID = PropertyName("log_id")
        val LOG_LIMIT = PropertyName("log_limit")
        val PAYLOAD = PropertyName("payload")
        val REFERER = PropertyName("referer")
        val SCOPE_ID = PropertyName("scope_id")
        val TYPED = PropertyName("typed")
        val URL = PropertyName("url")
        val VISIBILITY_PERCENTAGE = PropertyName("visibility_percentage")
    }
}
