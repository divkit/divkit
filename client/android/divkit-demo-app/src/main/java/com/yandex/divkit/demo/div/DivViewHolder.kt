package com.yandex.divkit.demo.div

import android.view.MotionEvent
import androidx.recyclerview.widget.FixScrollTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.DivDataTag
import com.yandex.div.core.DivViewFacade
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.Div2View

class DivViewHolder(val view: Div2View) : RecyclerView.ViewHolder(view), FixScrollTouchHelper.ScrollableHolder {
    private val paths = listOf(
        DivStatePath.parse("0/footer_card_m/plain/like_icon/filled"),
        DivStatePath.parse("0/card_subscribe/subscribe"),
        DivStatePath.parse("0/footer_card_m/plain/likes_count/incremented")
    )

    fun setData(divItem: DivItem) {
        if (adapterPosition % 2 == 0) {
            view.setDataWithStates(divItem.data, DivDataTag(divItem.id), paths, false)
        } else {
            view.setData(divItem.data, DivDataTag(divItem.id))
        }
    }

    override fun hasScrollableViewUnder(event: MotionEvent) = (view as? DivViewFacade)?.hasScrollableViewUnder(event) ?: false
}
