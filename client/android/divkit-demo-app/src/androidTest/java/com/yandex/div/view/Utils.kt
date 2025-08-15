package com.yandex.div.view

import android.view.View
import androidx.test.espresso.action.CoordinatesProvider
import androidx.test.espresso.action.GeneralLocation

class SavingCoordinatesProvider : CoordinatesProvider {
    var lastCoordinates: FloatArray? = null

    override fun calculateCoordinates(view: View?): FloatArray {
        return GeneralLocation.CENTER.calculateCoordinates(view).also {
            lastCoordinates = it
        }
    }
}
