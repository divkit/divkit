package com.yandex.divkit.demo.screenshot

import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

abstract class DivDataScreenshotActivity : AppCompatActivity() {
    abstract fun setDivData(json: JSONObject)
}
