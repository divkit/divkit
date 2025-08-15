package com.yandex.div.steps

import android.view.InputDevice
import android.view.MotionEvent
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.GeneralClickAction
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Tap
import com.yandex.div.view.SavingCoordinatesProvider
import com.yandex.test.util.Report

interface CoordinateSteps {
    val lastSavedCoordinates: FloatArray?
    fun saveCoordinates(view: ViewInteraction)
}

class CoordinateStepsMixin: CoordinateSteps {
    private val savingCoordinatesProvider by lazy { SavingCoordinatesProvider() }

    override val lastSavedCoordinates get() = savingCoordinatesProvider.lastCoordinates

    override fun saveCoordinates(view: ViewInteraction) {
        Report.step("Save view current coordinates") {
            findCoordinates(view, savingCoordinatesProvider)
                ?: throw RuntimeException("Cannot find view coordinates")
        }
    }

    private fun findCoordinates(view: ViewInteraction, coordinatesProvider: SavingCoordinatesProvider): FloatArray? {
        val action = GeneralClickAction(
            Tap.SINGLE,
            coordinatesProvider,
            Press.FINGER,
            InputDevice.SOURCE_UNKNOWN,
            MotionEvent.BUTTON_PRIMARY
        )
        view.perform(action)
        return coordinatesProvider.lastCoordinates
    }
}
