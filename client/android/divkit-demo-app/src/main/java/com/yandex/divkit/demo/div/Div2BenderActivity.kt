package com.yandex.divkit.demo.div

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.appcompat.app.AppCompatActivity
import com.yandex.div.DivDataTag
import com.yandex.div.core.DivKit
import com.yandex.div.core.view2.Div2View
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div2.DivData
import com.yandex.divkit.demo.R
import org.jetbrains.anko.contentView
import org.json.JSONObject
import java.util.UUID

private const val ITEM_REFRESH = 874

/**
 * Demo activity to simulate div2 bender.
 */
class Div2BenderActivity : AppCompatActivity() {

    private val parsingEnvironment = DivParsingEnvironment(ParsingErrorLogger.LOG)

    private val container by lazy {
        LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.GREEN)
            layoutParams =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
        }
    }

    private val parsingHistogramReporter by lazy {
        DivKit.getInstance(this).parsingHistogramReporter
    }

    private val divContext by lazy {
        DivUtils.createDivContext(this@Div2BenderActivity, divStateChangeListener = null)
    }
    private val headerView by lazy { Div2View(divContext).apply(container::addView) }

    private val omniboxView by lazy { Div2View(divContext).apply(container::addView) }

    private val informersView by lazy { Div2View(divContext).apply(container::addView) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ScrollView(this).apply {
            this.id = id
            this@Div2BenderActivity.invalidate()
            addView(container)
            setBackgroundColor(Color.GRAY)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.addItem(ITEM_REFRESH, R.drawable.refresh, "Refresh")
        return true
    }

    private fun Menu.addItem(id: Int, @DrawableRes iconRes: Int, title: String) {
        add(Menu.NONE, id, Menu.NONE, title).run {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            setIcon(iconRes)
            if (Build.VERSION.SDK_INT >= 26) {
                iconTintList = ColorStateList.valueOf(Color.WHITE)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == ITEM_REFRESH) {
            /* !!! Crucial part to reproduce issue with jumping text */
            omniboxView.resetToInitialState()
            contentView?.postDelayed(::invalidate, 1000)
            /* !!! End of crucial part to reproduce issue with jumping text !!! */
            Toast.makeText(this, "Refreshed", Toast.LENGTH_SHORT).show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun invalidate() {
        val headerData = readDivData(R.raw.div_header_data, "Bender.Header")
        val omniboxData = readDivData(R.raw.div_omnibox_data, "Bender.Omnibox")
        val informersData = readDivData(R.raw.informers, "Informers")
        headerView.setData(headerData, DivDataTag(UUID.randomUUID().toString()))
        omniboxView.setData(omniboxData, DivDataTag(UUID.randomUUID().toString()))
        informersView.setData(informersData, DivDataTag("informers"))
    }

    private fun readDivData(@RawRes rawRes: Int, componentName: String): DivData {
        val jsonInput = resources.openRawResource(rawRes).bufferedReader().readText()
        val divJson = parsingHistogramReporter.measureJsonParsing(componentName) {
            JSONObject(jsonInput)
        }
        val cardData = if (divJson.has("templates")) {
            val templates = divJson.getJSONObject("templates")
            parsingHistogramReporter.measureTemplatesParsing(templates, componentName) {
                parsingEnvironment.parseTemplates(templates)
            }
            divJson.getJSONObject("card")
        } else {
            divJson
        }
        return parsingHistogramReporter.measureDataParsing(divJson, componentName) {
            DivData(parsingEnvironment, cardData)
        }
    }

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, Div2BenderActivity::class.java))
        }
    }
}
