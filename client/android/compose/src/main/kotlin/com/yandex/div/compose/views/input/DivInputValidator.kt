package com.yandex.div.compose.views.input

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.withFrameNanos
import com.yandex.div.compose.context.DivViewContext
import com.yandex.div.compose.context.LocalDivViewContext
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.hasElement
import com.yandex.div.compose.variables.mutableStateFromBooleanVariable
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivInputValidator

/**
 * Writes the validity of [text] to the variables of the validators that have one and returns the validators
 * [text] fails.
 */
@Composable
internal fun List<DivInputValidator>.validate(text: String): List<DivInputValidator> = filter { validator ->
    val result = mutableStateFromBooleanVariable(validator.variableName)
    val isValid = when (validator) {
        is DivInputValidator.Regex -> validator.isValid(text)
        is DivInputValidator.Expression -> validator.isValid(text)
    }
    result?.value = isValid
    !isValid
}

/**
 * The texts of the labels of the [failed] validators, which accessibility services announce as the error of
 * the input: the View renderer labels the input with them through `labelFor`, the iOS one joins them into the
 * accessibility label of the input.
 */
@Composable
internal fun List<DivInputValidator>.observeErrorMessage(failed: List<DivInputValidator>): String? {
    val viewContext = LocalDivViewContext.current
    val labels = viewContext.component.labelTextStorage
    val labelIds = map { it.labelId.observedValue() }
    // Several validators can name one label: report it once.
    labelIds.distinct().forEach { labelId -> key(labelId) { ReportMissingLabel(labelId, viewContext) } }
    val messages = mapIndexedNotNull { index, validator ->
        if (validator in failed) labels[labelIds[index]] else null
    }
    return if (messages.isEmpty()) null else messages.joinToString(". ")
}

@Composable
private fun ReportMissingLabel(labelId: String, viewContext: DivViewContext) {
    val reporter = LocalComponent.current.reporter
    LaunchedEffect(labelId) {
        // A label registers in a side effect of its composition: after this effect starts under the test
        // dispatcher, which starts it undispatched, and in the measure pass of the frame for an item of a lazy
        // list. A frame later every label of the frame has registered.
        withFrameNanos {}
        // A label the markup has but the composition does not (`visibility: gone`, an inactive state) has no text
        // and gives no error, as the View renderer announces nothing for a gone view; only an id absent from the
        // markup is reported.
        if (viewContext.component.labelTextStorage[labelId] == null && !viewContext.hasElement(labelId)) {
            reporter.reportError("Can't find label with id '$labelId'")
        }
    }
}

private fun DivViewContext.hasElement(id: String): Boolean = states.any { it.div.hasElement(id) }

@Composable
private fun DivInputValidator.Regex.isValid(text: String): Boolean {
    val allowEmpty = value.allowEmpty.observedValue()
    if (allowEmpty && text.isEmpty()) {
        return true
    }

    val pattern = value.pattern.observedValue()
    val regex = rememberRegex(pattern) ?: return true
    return regex.matches(text)
}

@Composable
private fun DivInputValidator.Expression.isValid(text: String): Boolean {
    val allowEmpty = value.allowEmpty.observedValue()
    if (allowEmpty && text.isEmpty()) {
        return true
    }

    val condition = value.condition.observedValue()
    return condition
}

private val DivInputValidator.variableName: String
    get() = when (this) {
        is DivInputValidator.Regex -> value.variable
        is DivInputValidator.Expression -> value.variable
    }

private val DivInputValidator.labelId: Expression<String>
    get() = when (this) {
        is DivInputValidator.Regex -> value.labelId
        is DivInputValidator.Expression -> value.labelId
    }
