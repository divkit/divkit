 // Copyright (c) 2018 Yandex LLC. All rights reserved.
 // Author: Vasiliy Polikarpov <polikarpov@yandex-team.ru>
package com.yandex.div.core.view2.backbutton

import com.yandex.div.core.view2.backbutton.BackKeyPressedHelper.OnBackClickListener

/**
 * An interface that represents some View that handle BACK key
 * apparently using [BackKeyPressedHelper].
 */
internal interface BackHandlingView {
    /**
     * @see BackKeyPressedHelper.setOnBackClickListener
     */
    fun setOnBackClickListener(listener: OnBackClickListener?)
}
