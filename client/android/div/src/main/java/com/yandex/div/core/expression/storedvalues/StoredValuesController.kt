package com.yandex.div.core.expression.storedvalues

import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.data.StoredValue
import javax.inject.Inject

@Mockable
@DivScope
internal class StoredValuesController @Inject constructor() {

    fun getStoredValue(name: String): StoredValue? {
        // TODO(timatifey): DIVKIT-1939
        return null
    }

    fun setStoredValue(
        name: String,
        value: String,
        lifetime: String,
        type: String
    ): Boolean {
        // TODO(timatifey): DIVKIT-1939
        return false
    }
}
