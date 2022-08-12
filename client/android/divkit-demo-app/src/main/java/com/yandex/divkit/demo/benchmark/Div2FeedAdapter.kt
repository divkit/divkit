package com.yandex.divkit.demo.benchmark

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.view2.Div2View
import com.yandex.div2.DivData
import java.util.UUID

class Div2FeedAdapter(
    private val divContext: Div2Context
) : RecyclerView.Adapter<Div2FeedAdapter.DivViewHolder>() {

    var metricsObserver: BindMetricsObserver? = null

    private var cards = emptyList<DivData>()

    fun setFeed(cards: List<DivData>) {
        this.cards = cards.toList()
        notifyDataSetChanged()
    }

    override fun getItemCount() = cards.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DivViewHolder {
        val divView = Div2View(divContext)
        val profilingLayout = ProfilingLayout(divContext.baseContext).apply {
            addView(divView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        return DivViewHolder(profilingLayout, divView, metricsObserver)
    }

    override fun onBindViewHolder(holder: DivViewHolder, position: Int) {
        holder.bind(position, cards[position])
    }

    class DivViewHolder(
        private val profilingLayout: ProfilingLayout,
        private val divView: Div2View,
        private val metricsObserver: BindMetricsObserver?
    ) : RecyclerView.ViewHolder(profilingLayout) {

        fun bind(position: Int, divData: DivData) {
            val bindingMetrics = profile {
                divView.setData(divData, DIV_DATA_TAG)
            }

            profilingLayout.addListener(object : ProfilingLayout.FrameMetricsListener {
                override fun onMetricsMeasured(layout: ProfilingLayout, metrics: ProfilingLayout.FrameMetrics) {
                    metricsObserver?.onMetricsMeasured(
                        position = position,
                        bindingTime = bindingMetrics.wallTime,
                        measureTime = metrics.measure,
                        layoutTime = metrics.layout,
                        drawTime = metrics.draw
                    )
                    layout.removeListener(this)
                }
            })
        }

        private companion object {
            private val DIV_DATA_TAG = DivDataTag(UUID.randomUUID().toString())
        }
    }

    fun interface BindMetricsObserver {

        fun onMetricsMeasured(position: Int, bindingTime: Long, measureTime: Long, layoutTime: Long, drawTime: Long)
    }
}
