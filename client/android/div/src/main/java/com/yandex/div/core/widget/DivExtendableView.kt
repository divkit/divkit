package com.yandex.div.core.widget

import com.yandex.div.core.annotations.PublicApi

@PublicApi
interface DivExtendableView {
    var delegate: DivViewDelegate?
}