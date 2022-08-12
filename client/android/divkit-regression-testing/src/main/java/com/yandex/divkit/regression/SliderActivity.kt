package com.yandex.divkit.regression

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.yandex.div.util.dpToPx
import com.yandex.div.core.widget.slider.SliderView
import com.yandex.div.core.widget.slider.shapes.RoundedRectDrawable
import com.yandex.div.core.widget.slider.shapes.RoundedRectParams


class SliderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.slider_activity_layout)

        val slider = findViewById<SliderView>(R.id.slider)
        slider.inactiveTrackDrawable =
            RoundedRectDrawable(
                RoundedRectParams(
                    0f,
                    dpToPx(5f),
                    Color.GRAY,
                    dpToPx(4f)
                )
            )
        slider.activeTrackDrawable =
            RoundedRectDrawable(
                RoundedRectParams(
                    0f,
                    dpToPx(5f),
                    Color.RED,
                    dpToPx(4f)
                )
            )
        slider.maxValue = 10f
        val thumbDrawable = RoundedRectDrawable(
            RoundedRectParams(
                dpToPx(28f),
                dpToPx(28f),
                Color.RED,
                dpToPx(14f),
                Color.WHITE,
                dpToPx(2f),
            )
        )
        slider.setThumbValue(7f, false)
        slider.thumbDrawable = thumbDrawable
        findViewById<SwitchCompat>(R.id.secondary_thumb_switch).apply {
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    slider.setThumbSecondaryValue(3f)
                    slider.thumbSecondaryDrawable = thumbDrawable
                } else {
                    slider.setThumbSecondaryValue(null)
                }
            }
        }

        findViewById<SwitchCompat>(R.id.tick_marks_switch).apply {
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    slider.activeTickMarkDrawable =
                        RoundedRectDrawable(
                            RoundedRectParams(
                                dpToPx(7f),
                                dpToPx(7f),
                                Color.RED,
                                dpToPx(3.5f),
                                Color.WHITE,
                                dpToPx(2f),
                            )
                        )
                    slider.inactiveTickMarkDrawable =
                        RoundedRectDrawable(
                            RoundedRectParams(
                                dpToPx(7f),
                                dpToPx(7f),
                                Color.GRAY,
                                dpToPx(3.5f),
                                Color.WHITE,
                                dpToPx(2f),
                            )
                        )
                } else {
                    slider.activeTickMarkDrawable = null
                    slider.inactiveTickMarkDrawable = null
                }
            }
        }

        findViewById<SwitchCompat>(R.id.interactive_switch).apply {
            setOnCheckedChangeListener { _, isChecked -> slider.interactive = isChecked }
        }

        findViewById<EditText>(R.id.max_value).apply {
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) = Unit
                override fun beforeTextChanged(
                    s: CharSequence, start: Int,
                    count: Int, after: Int
                ) = Unit

                override fun onTextChanged(
                    s: CharSequence, start: Int,
                    before: Int, count: Int
                ) {
                    if (s.isNotEmpty()) {
                        slider.maxValue = s.toString().toIntSafe(slider.maxValue.toInt()).toFloat()
                    }
                }
            })
        }

        findViewById<SwitchCompat>(R.id.anim_switch).apply {
            isChecked = slider.animationEnabled
            setOnCheckedChangeListener { _, isChecked ->
                slider.animationEnabled = isChecked
            }
        }

        findViewById<EditText>(R.id.anim_duration).apply {
            setText(slider.animationDuration.toString())
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) = Unit
                override fun beforeTextChanged(
                    s: CharSequence, start: Int,
                    count: Int, after: Int
                ) = Unit

                override fun onTextChanged(
                    s: CharSequence, start: Int,
                    before: Int, count: Int
                ) {
                    if (s.isNotEmpty()) {
                        slider.animationDuration = s.toString().toIntSafe(0).toLong()
                    }
                }
            })
        }
    }
}

private fun String.toIntSafe(default: Int): Int =
    try {
        toInt()
    } catch (e: NumberFormatException) {
        default
    }
