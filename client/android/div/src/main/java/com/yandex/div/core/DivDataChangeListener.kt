package com.yandex.div.core

import com.yandex.div.core.annotations.PublicApi
import com.yandex.div.core.view2.Div2View
import com.yandex.div2.DivData

@PublicApi
interface DivDataChangeListener {

    fun beforeAnimatedDataChange(divView: Div2View, data: DivData)

    fun afterAnimatedDataChange(divView: Div2View, data: DivData)

    companion object {

        @JvmField
        val STUB = object : DivDataChangeListener {

            override fun beforeAnimatedDataChange(divView: Div2View, data: DivData) = Unit

            override fun afterAnimatedDataChange(divView: Div2View, data: DivData) = Unit
        }
    }
}
