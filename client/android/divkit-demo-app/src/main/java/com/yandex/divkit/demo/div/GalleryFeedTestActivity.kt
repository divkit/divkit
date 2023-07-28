package com.yandex.divkit.demo.div

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.DivDataTag
import com.yandex.div.core.view2.Div2View
import com.yandex.div2.DivData
import com.yandex.divkit.demo.R
import com.yandex.divkit.demo.screenshot.DivAssetReader
import java.util.*

class GalleryFeedTestActivity : AppCompatActivity() {

    private val assetReader = DivAssetReader(this)

    private val divContext by lazy {
        divContext(activity = this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = FrameLayout(this)
        val rv = RecyclerView(this)
        rv.id = R.id.gallery_feed
        rv.layoutManager = LinearLayoutManager(this)
        val data = loadCard()
        rv.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): RecyclerView.ViewHolder {
                val divContainer = DivContainer(parent.context).apply {
                    div2View = Div2View(divContext).also { addView(it) }
                    layoutParams = RecyclerView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                }
                return object : RecyclerView.ViewHolder(divContainer) {}
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                (holder.itemView as DivContainer).div2View
                    ?.setData(data, DivDataTag(UUID.randomUUID().toString()))
            }

            override fun getItemCount(): Int = 10
        }
        rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val position =
                    (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                val vh = recyclerView.findViewHolderForAdapterPosition(position)
                (vh?.itemView as? DivContainer)?.div2View?.tryLogVisibility()
            }
        })
        container.addView(rv)
        setContentView(container)
    }

    private class DivContainer(context: Context) : FrameLayout(context) {
        var div2View: Div2View? = null
    }

    private fun loadCard(): DivData {
        return assetReader.read("div2-test/pager.json").asDiv2DataWithTemplates()
    }
}
