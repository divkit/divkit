package com.yandex.div.legacy.view

import android.content.Context
import android.util.AttributeSet
import com.yandex.div.view.SuperLineHeightTextView

internal class DivLineHeightTextViewFactory : TextViewFactory {

    override fun create(context: Context, attrs: AttributeSet?, defStyle: Int) =
        SuperLineHeightTextView(context, attrs, defStyle)

}
