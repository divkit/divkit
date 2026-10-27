package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.ActionSubmit

@Generated
internal class ActionSubmitSerializer : EntitySerializer<ActionSubmit>(ActionSubmit::class.java, "submit") {
    override fun writeFields(value: ActionSubmit, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(CONTAINER_ID, properties.containerId, provider)
        gen.writeProperty(ON_FAIL_ACTIONS, properties.onFailActions, provider)
        gen.writeProperty(ON_SUCCESS_ACTIONS, properties.onSuccessActions, provider)
        gen.writeProperty(REQUEST, properties.request, provider)
    }

    private companion object {
        val CONTAINER_ID = PropertyName("container_id")
        val ON_FAIL_ACTIONS = PropertyName("on_fail_actions")
        val ON_SUCCESS_ACTIONS = PropertyName("on_success_actions")
        val REQUEST = PropertyName("request")
    }
}

@Generated
internal class ActionSubmitRequestSerializer : EntitySerializer<ActionSubmit.Request>(ActionSubmit.Request::class.java) {
    override fun writeFields(value: ActionSubmit.Request, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(HEADERS, properties.headers, provider)
        gen.writeProperty(METHOD, properties.method, provider)
        gen.writeProperty(URL, properties.url, provider)
    }

    private companion object {
        val HEADERS = PropertyName("headers")
        val METHOD = PropertyName("method")
        val URL = PropertyName("url")
    }
}

@Generated
internal class ActionSubmitRequestHeaderSerializer : EntitySerializer<ActionSubmit.Request.Header>(ActionSubmit.Request.Header::class.java) {
    override fun writeFields(value: ActionSubmit.Request.Header, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(NAME, properties.name, provider)
        gen.writeProperty(VALUE, properties.value, provider)
    }

    private companion object {
        val NAME = PropertyName("name")
        val VALUE = PropertyName("value")
    }
}
