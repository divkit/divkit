package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.ActionClearFocus

@Generated
internal class ActionClearFocusSerializer : EntitySerializer<ActionClearFocus>(ActionClearFocus::class.java, "clear_focus")
