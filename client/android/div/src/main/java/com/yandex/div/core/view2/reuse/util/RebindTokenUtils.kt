package com.yandex.div.core.view2.reuse.util

import com.yandex.div.core.view2.reuse.ExistingToken
import com.yandex.div.core.view2.reuse.NewToken

internal fun combineTokens(
    existingToken: ExistingToken,
    newToken: NewToken,
) = ExistingToken(
    item = newToken.item,
    view = existingToken.view,
    parentToken = existingToken.parentToken,
    childIndex = newToken.childIndex,
)
