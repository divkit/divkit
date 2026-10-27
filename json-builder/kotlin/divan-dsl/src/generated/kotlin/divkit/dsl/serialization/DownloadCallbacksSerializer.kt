package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.DownloadCallbacks

@Generated
internal class DownloadCallbacksSerializer : EntitySerializer<DownloadCallbacks>(DownloadCallbacks::class.java) {
    override fun writeFields(value: DownloadCallbacks, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ON_FAIL_ACTIONS, properties.onFailActions, provider)
        gen.writeProperty(ON_SUCCESS_ACTIONS, properties.onSuccessActions, provider)
    }

    private companion object {
        val ON_FAIL_ACTIONS = PropertyName("on_fail_actions")
        val ON_SUCCESS_ACTIONS = PropertyName("on_success_actions")
    }
}
