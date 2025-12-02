package com.yandex.div.internal.util

import android.net.Uri
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.data.VariableMutationException
import com.yandex.div.evaluable.types.Color
import com.yandex.div.evaluable.types.Url
import com.yandex.div.internal.parser.STRING_TO_COLOR_INT
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

@InternalApi
object ParsingValueUtils {

    fun String.parseAsLong(): Long {
        return try {
            this.toLong()
        } catch (e: NumberFormatException) {
            throw VariableMutationException(cause = e)
        }
    }

    fun String.parseAsInt(): Int {
        return try {
            this.toInt()
        } catch (e: NumberFormatException) {
            throw VariableMutationException(cause = e)
        }
    }

    fun String.parseAsBoolean(): Boolean {
        return toBooleanStrictOrNull() ?: parseAsInt().toBoolean()
            ?: throw VariableMutationException("Unable to convert $this to boolean")
    }

    fun String.parseAsDouble(): Double {
        return try {
            this.toDouble()
        } catch (e: NumberFormatException) {
            throw VariableMutationException(cause = e)
        }
    }

    fun String.parseAsColor(): Color {
        return try {
            Color(STRING_TO_COLOR_INT(this))
        } catch (e: ClassCastException) {
            throw VariableMutationException(cause = e)
        }
    }

    fun String.parseAsUri(): Uri {
        return try {
            Uri.parse(this)
        } catch (e: IllegalArgumentException) {
            throw VariableMutationException(cause = e)
        }
    }

    fun String.parseAsUrl(): Url {
        return try {
            Url.from(this)
        } catch (e: IllegalArgumentException) {
            throw VariableMutationException(cause = e)
        }
    }

    fun String.parseAsJsonObject(): JSONObject {
        return try {
            JSONObject(this)
        } catch (e: JSONException) {
            throw VariableMutationException(cause = e)
        }
    }

    fun String.parseAsJsonArray(): JSONArray {
        return try {
            JSONArray(this)
        } catch (e: JSONException) {
            throw VariableMutationException(cause = e)
        }
    }
}
