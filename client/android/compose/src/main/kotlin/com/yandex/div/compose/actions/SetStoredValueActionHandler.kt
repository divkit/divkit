package com.yandex.div.compose.actions

import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.storedvalues.LazyStoredValuesStorage
import com.yandex.div.internal.actions.DivUntypedAction
import com.yandex.div.internal.storedvalues.StoredValueParser
import com.yandex.div.internal.storedvalues.toStoredValue
import com.yandex.div.internal.storedvalues.toStoredValueScope
import com.yandex.div2.DivActionSetStoredValue
import javax.inject.Inject

internal class SetStoredValueActionHandler @Inject constructor(
    private val reporter: DivReporter,
    private val storedValuesStorage: LazyStoredValuesStorage
) {
    private val parser = StoredValueParser(
        reportError = { message -> reporter.reportError(message) }
    )

    fun handle(
        context: DivActionHandlingContext,
        action: DivActionSetStoredValue
    ) {
        val expressionResolver = context.expressionResolver
        storedValuesStorage.setValue(
            value = action.value.toStoredValue(
                name = action.name.evaluate(expressionResolver),
                expressionResolver = expressionResolver
            ),
            scope = action.scope?.evaluate(expressionResolver).toStoredValueScope(),
            lifetime = action.lifetime.evaluate(expressionResolver)
        )
    }

    fun handle(action: DivUntypedAction.SetStoredValue) {
        val value = parser.parse(
            type = action.type,
            name = action.name,
            value = action.value
        ) ?: return

        storedValuesStorage.setValue(
            value = value,
            scope = action.scope,
            lifetime = action.lifetime
        )
    }
}
