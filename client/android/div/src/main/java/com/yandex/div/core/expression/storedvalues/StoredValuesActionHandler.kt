package com.yandex.div.core.expression.storedvalues

import android.net.Uri
import com.yandex.div.core.DivViewFacade
import com.yandex.div.core.view2.Div2View
import com.yandex.div.data.StoredValue
import com.yandex.div.evaluable.types.Color
import com.yandex.div.evaluable.types.Url
import com.yandex.div.internal.KAssert
import com.yandex.div.internal.parser.STRING_TO_COLOR_INT
import com.yandex.div.internal.util.toBoolean

private const val AUTHORITY_SET_STORED_VALUE = "set_stored_value"

private const val PARAM_NAME = "name"
private const val PARAM_VALUE = "value"
private const val PARAM_LIFETIME = "lifetime"
private const val PARAM_TYPE = "type"

internal object StoredValuesActionHandler {

    @JvmStatic
    fun canHandle(authority: String?): Boolean = authority == AUTHORITY_SET_STORED_VALUE

    @JvmStatic
    fun handleAction(uri: Uri, view: DivViewFacade): Boolean {
        val div2View = if (view is Div2View) view else null
        if (div2View == null) {
            KAssert.fail { "Handler view is not instance of Div2View" }
            return false
        }

        val name = uri.getParam(forName = PARAM_NAME) ?: return false
        val value = uri.getParam(forName = PARAM_VALUE) ?: return false
        val lifetime = uri.getParam(forName = PARAM_LIFETIME)?.toLongOrNull() ?: return false
        val type = uri.getParam(forName = PARAM_TYPE)
            ?.run { StoredValue.Type.fromString(this) }
            ?: return false

        return try {
            val storedValue = createStoredValue(type, name, value)
            val storedValuesController = div2View.div2Component.storedValuesController
            val errorCollector = div2View.viewComponent.errorCollectors.getOrCreate(
                div2View.divTag,
                div2View.divData
            )
            storedValuesController.setStoredValue(storedValue, lifetime, errorCollector)
        } catch (e: StoredValueDeclarationException) {
            KAssert.fail {"Stored value '$name' declaration failed: ${e.message}" }
            false
        }
    }

    private fun Uri.getParam(forName: String): String? {
        val param = getQueryParameter(forName)
        if (param == null) {
            KAssert.fail { "The required parameter $forName is missing" }
            return null
        }
        return param
    }

    @Throws(StoredValueDeclarationException::class)
    private fun createStoredValue(
        type: StoredValue.Type,
        name: String,
        value: String,
    ): StoredValue = when (type) {
        StoredValue.Type.STRING -> StoredValue.StringStoredValue(name, value)
        StoredValue.Type.INTEGER -> StoredValue.IntegerStoredValue(name, value.parseAsLong())
        StoredValue.Type.BOOLEAN -> StoredValue.BooleanStoredValue(name, value.parseAsBoolean())
        StoredValue.Type.NUMBER -> StoredValue.DoubleStoredValue(name, value.parseAsDouble())
        StoredValue.Type.COLOR -> StoredValue.ColorStoredValue(name, value.parseAsColor())
        StoredValue.Type.URL -> StoredValue.UrlStoredValue(name, value.parseAsUrl())
    }

    @Throws(StoredValueDeclarationException::class)
    private fun String.parseAsLong(): Long {
        return try {
            this.toLong()
        } catch (e: NumberFormatException) {
            throw StoredValueDeclarationException(cause = e)
        }
    }

    @Throws(StoredValueDeclarationException::class)
    private fun String.parseAsInt(): Int {
        return try {
            this.toInt()
        } catch (e: NumberFormatException) {
            throw StoredValueDeclarationException(cause = e)
        }
    }

    @Throws(StoredValueDeclarationException::class)
    private fun String.parseAsBoolean(): Boolean {
        try {
            return toBooleanStrictOrNull() ?: parseAsInt().toBoolean()
        } catch (e: IllegalArgumentException) {
            throw StoredValueDeclarationException(cause = e)
        }
    }

    @Throws(StoredValueDeclarationException::class)
    private fun String.parseAsDouble(): Double {
        return try {
            this.toDouble()
        } catch (e: NumberFormatException) {
            throw StoredValueDeclarationException(cause = e)
        }
    }

    @Throws(StoredValueDeclarationException::class)
    private fun String.parseAsUrl(): Url {
        return try {
            Url.from(this)
        } catch (e: IllegalArgumentException) {
            throw StoredValueDeclarationException(cause = e)
        }
    }

    @Throws(StoredValueDeclarationException::class)
    private fun String.parseAsColor(): Color {
        val intColor = STRING_TO_COLOR_INT(this) ?: throw StoredValueDeclarationException(
            "Wrong value format for color stored value: '$this'")
        return Color(intColor)
    }

}
