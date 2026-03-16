package com.yandex.div.internal.actions

import android.net.Uri
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.data.StoredValue

@InternalApi
sealed class DivUntypedAction {

    data class HideTooltip(
        val id: String
    ) : DivUntypedAction()

    data class SetState(
        val id: String,
        val isTemporary: Boolean
    ) : DivUntypedAction()

    data class SetStoredValue(
        val name: String,
        val value: String,
        val type: StoredValue.Type,
        val lifetime: Long
    ) : DivUntypedAction()

    data class SetVariable(
        val name: String,
        val value: String
    ) : DivUntypedAction()

    data class ShowTooltip(
        val id: String,
        val isMultiple: Boolean
    ) : DivUntypedAction()

    data class Timer(
        val id: String,
        val action: String
    ) : DivUntypedAction()

    data class Video(
        val id: String,
        val action: String
    ) : DivUntypedAction()

    companion object {

        @JvmStatic
        fun parse(uri: Uri): DivUntypedAction? {
            if (uri.scheme != "div-action") {
                return null
            }

            when (uri.authority) {
                "hide_tooltip" -> {
                    val id = uri.getQueryParameter("id") ?: return null
                    return HideTooltip(id = id)
                }

                "set_state" -> {
                    val id = uri.getQueryParameter("state_id") ?: return null
                    return SetState(
                        id = id,
                        isTemporary = uri.getBooleanQueryParameter("temporary", true)
                    )
                }

                "set_stored_value" -> {
                    val name = uri.getQueryParameter("name") ?: return null
                    val value = uri.getQueryParameter("value") ?: return null
                    val type = uri.getQueryParameter("type")
                        ?.let { StoredValue.Type.fromString(it) }
                        ?: return null
                    val lifetime = uri.getQueryParameter("lifetime")?.toLongOrNull() ?: return null
                    return SetStoredValue(
                        name = name,
                        value = value,
                        type = type,
                        lifetime = lifetime
                    )
                }

                "set_variable" -> {
                    val name = uri.getQueryParameter("name") ?: return null
                    val value = uri.getQueryParameter("value") ?: return null
                    return SetVariable(name = name, value = value)
                }

                "show_tooltip" -> {
                    val id = uri.getQueryParameter("id") ?: return null
                    return ShowTooltip(
                        id = id,
                        isMultiple = uri.getBooleanQueryParameter("multiple", false)
                    )
                }

                "timer" -> {
                    val id = uri.getQueryParameter("id") ?: return null
                    val action = uri.getQueryParameter("action") ?: return null
                    return Timer(id = id, action = action)
                }

                "video" -> {
                    val id = uri.getQueryParameter("id") ?: return null
                    val action = uri.getQueryParameter("action") ?: return null
                    return Video(id = id, action = action)
                }
            }

            return null
        }
    }
}
