package com.yandex.divkit.demo

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

/**
 * Dummy activity that has empty content view for testing.
 */
class DummyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contentView = FrameLayout(this)
        setContentView(contentView)
        ViewCompat.setOnApplyWindowInsetsListener(contentView) { view, insets ->
            val top = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.updatePadding(top = top)
            insets
        }
    }
}
