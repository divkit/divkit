package com.yandex.div.datetime

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnAttach
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.yandex.div.core.extension.DivExtensionHandler
import com.yandex.div.core.view2.Div2View
import com.yandex.div.datetime.data.PickerMode
import com.yandex.div.datetime.utils.parseMode
import com.yandex.div.datetime.utils.toPickerType
import com.yandex.div.internal.util.getStringOrEmpty
import com.yandex.div.internal.util.getStringOrNull
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivBase
import com.yandex.div2.DivInput
import com.yandex.div2.DivVariable
import com.yandex.div2.StrVariable
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * DivExtension that allows to show date/time picker from input.
 *
 * Example of usage:
 * ```json
 * "extensions": [
 *    {
 *        "id": "date_time_picker",
 *        "params": {
 *            "text_variable": "value",
 *            "mode": "date|time"
 *        }
 *    }
 * ],
 * ```
 */

public class DivDateTimeExtensionHandler : DivExtensionHandler {

    override fun matches(div: DivBase): Boolean {
        if (div !is DivInput) {
            return false
        }
        val hasExtensionSetUp =
            div.extensions?.any { extension -> extension.id == EXTENSION_ID } ?: false
        return hasExtensionSetUp
    }

    private var fragmentManagerRef: FragmentManager? = null
    private var viewRef: Div2View? = null
    private var inputVariableName: String? = null
    private var dateFormatter: SimpleDateFormat? = null

    override fun bindView(
        divView: Div2View,
        expressionResolver: ExpressionResolver,
        view: View,
        div: DivBase
    ) {
        // TODO extract methods
        val params = div.extensions?.first { it.id == EXTENSION_ID }?.params ?: return
        val modes = readMode(params)
        validateMode(modes)
        val pickerType = requireNotNull(modes.toPickerType())
        inputVariableName = readTextVariable(params)
        viewRef = divView
        dateFormatter = SimpleDateFormat(pickerType.toDateFormatter(), Locale.getDefault())
        divView.doOnAttach {
            val owner = divView.findViewTreeLifecycleOwner() as? AppCompatActivity
            if (owner != null) {
                fragmentManagerRef = owner.supportFragmentManager
                fragmentManagerRef?.setFragmentResultListener(
                    DivDateTimePickerDialogFragment.RESULT_KEY,
                    owner,
                ) { _, bundle ->
                    if (bundle.containsKey(DivDateTimePickerDialogFragment.RESULT_SELECTED_DATETIME)) {
                        val result =
                            bundle.getSerializable(
                                DivDateTimePickerDialogFragment.RESULT_SELECTED_DATETIME
                            ) as Date
                        onDateSet(result)
                    }
                }
            }
        }
        view.isFocusable = false
        view.setOnClickListener { _ ->
            fragmentManagerRef?.let { fragmentManager ->
                DivDateTimePickerDialogFragment.newInstance(pickerType).show(
                    fragmentManager,
                    null
                )
            }
        }
    }

    override fun unbindView(
        divView: Div2View,
        expressionResolver: ExpressionResolver,
        view: View,
        div: DivBase
    ) {
        view.setOnClickListener(null)
        fragmentManagerRef = null
        viewRef = null
        inputVariableName = null
        dateFormatter = null
    }

    private fun onDateSet(result: Date) {
        val view = viewRef
        val variable = inputVariableName
        val dateFormatter = dateFormatter
        if (view != null && variable != null && dateFormatter != null) {
            view.setVariable(variable, dateFormatter.format(result))
        }
    }

    private fun readTextVariable(json: JSONObject): String? {
        return json.getStringOrNull(PARAM_TEXT_VARIABLE)
    }

    private fun readMode(json: JSONObject): Set<PickerMode> {
        val modeString = json.getStringOrEmpty(PARAM_MODE)
        return parseMode(modeString)
    }

    private fun validateMode(modes: Set<PickerMode>) {
        if (modes.isEmpty()) {
            throw IllegalArgumentException(
                "\"mode\" should be set as \"date\", \"time\", or both split by | sign"
            )
        }
    }

    private fun PickerType.toDateFormatter() = when (this) {
        PickerType.DATE -> DATE_FORMATTER
        PickerType.TIME -> TIME_FORMATTER
        PickerType.DATE_TIME -> DATETIME_FORMATTER
    }

    private companion object {
        private const val EXTENSION_ID = "date_time_picker"
        private const val PARAM_TEXT_VARIABLE = "text_variable"
        private const val PARAM_MODE = "mode"
        private const val DATE_FORMATTER = "dd.MM.yyyy"
        private const val TIME_FORMATTER = "HH:mm"
        private const val DATETIME_FORMATTER = "dd.MM.yyyy HH:mm"
    }
}