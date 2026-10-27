package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.ActionDownload

@Generated
internal class ActionDownloadSerializer : EntitySerializer<ActionDownload>(ActionDownload::class.java, "download") {
    override fun writeFields(value: ActionDownload, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ON_FAIL_ACTIONS, properties.onFailActions, provider)
        gen.writeProperty(ON_SUCCESS_ACTIONS, properties.onSuccessActions, provider)
        gen.writeProperty(URL, properties.url, provider)
    }

    private companion object {
        val ON_FAIL_ACTIONS = PropertyName("on_fail_actions")
        val ON_SUCCESS_ACTIONS = PropertyName("on_success_actions")
        val URL = PropertyName("url")
    }
}
