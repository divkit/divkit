package com.yandex.div.core.view2.divs

import com.yandex.android.beacon.SendBeaconManager
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.dagger.ExperimentFlag
import com.yandex.div.core.experiments.Experiment.TAP_BEACONS_ENABLED
import com.yandex.div.core.experiments.Experiment.VISIBILITY_BEACONS_ENABLED
import com.yandex.div.internal.KAssert
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAction
import com.yandex.div2.DivSightAction
import dagger.Lazy
import javax.inject.Inject

@DivScope
@Mockable
internal class DivActionBeaconSender @Inject constructor(
    private val sendBeaconManagerLazy: Lazy<SendBeaconManager?>,
    @ExperimentFlag(TAP_BEACONS_ENABLED) private val isTapBeaconsEnabled: Boolean,
    @ExperimentFlag(VISIBILITY_BEACONS_ENABLED) private val isVisibilityBeaconsEnabled: Boolean,
) {

    fun sendTapActionBeacon(action: DivAction, resolver: ExpressionResolver) {
        val url = action.logUrl?.evaluate(resolver)
        if (isTapBeaconsEnabled && url != null) {
            val sendBeaconManager = sendBeaconManagerLazy.get()
            if (sendBeaconManager == null) {
                KAssert.fail { "SendBeaconManager was not configured" }
                return
            }
            sendBeaconManager.addUrl(url, action.toHttpHeaders(resolver), action.payload)
        }
    }

    fun sendVisibilityActionBeacon(action: DivSightAction, resolver: ExpressionResolver) {
        val url = action.url?.evaluate(resolver) ?: return
        if (url.scheme.isNotHttpScheme()) return
        if (isVisibilityBeaconsEnabled) {
            val sendBeaconManager = sendBeaconManagerLazy.get()
            if (sendBeaconManager == null) {
                KAssert.fail { "SendBeaconManager was not configured" }
                return
            }
            sendBeaconManager.addUrl(url, action.toHttpHeaders(resolver), action.payload)
        }
    }

    fun sendSwipeOutActionBeacon(action: DivAction, resolver: ExpressionResolver) {
        val url = action.logUrl?.evaluate(resolver)
        if (url != null) {
            val sendBeaconManager = sendBeaconManagerLazy.get()
            if (sendBeaconManager == null) {
                KAssert.fail { "SendBeaconManager was not configured" }
                return
            }
            sendBeaconManager.addUrl(url, action.toHttpHeaders(resolver), action.payload)
        }
    }

    private fun DivAction.toHttpHeaders(resolver: ExpressionResolver): Map<String, String> {
        val headers = mutableMapOf<String, String>()
        referer?.let { referer ->
            headers[HTTP_HEADER_REFERER] = referer.evaluate(resolver).toString()
        }
        return headers
    }

    private fun DivSightAction.toHttpHeaders(resolver: ExpressionResolver): Map<String, String> {
        val headers = mutableMapOf<String, String>()
        referer?.let { referer ->
            headers[HTTP_HEADER_REFERER] = referer.evaluate(resolver).toString()
        }
        return headers
    }

    private fun String?.isNotHttpScheme() = this != HTTP_SCHEME && this != HTTPS_SCHEME

    private companion object {

        private const val HTTP_HEADER_REFERER = "Referer"
        private const val HTTP_SCHEME = "http"
        private const val HTTPS_SCHEME = "https"
    }
}
