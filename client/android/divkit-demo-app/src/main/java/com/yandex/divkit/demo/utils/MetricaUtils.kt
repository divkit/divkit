package com.yandex.divkit.demo.utils

import android.content.Context
import com.yandex.divkit.demo.BuildConfig
import io.appmetrica.analytics.AdRevenue
import io.appmetrica.analytics.AppMetrica
import io.appmetrica.analytics.IReporter
import io.appmetrica.analytics.Revenue
import io.appmetrica.analytics.coreapi.event.AppMetricaEvent
import io.appmetrica.analytics.ecommerce.ECommerceEvent
import io.appmetrica.analytics.plugins.IPluginReporter
import io.appmetrica.analytics.plugins.PluginErrorDetails
import io.appmetrica.analytics.profile.UserProfile

object MetricaUtils {

    fun getReporter(context: Context): IReporter {
        if (BuildConfig.METRICA_API_KEY.isEmpty()) {
            return NoOpReporter
        }
        return AppMetrica.getReporter(context.applicationContext, BuildConfig.METRICA_API_KEY)
    }

    private object NoOpReporter : IReporter {

        override fun pauseSession() = Unit
        override fun resumeSession() = Unit

        override fun reportError(identifier: String, message: String?) = Unit
        override fun reportError(identifier: String, message: String?, error: Throwable?) = Unit
        override fun reportError(message: String, error: Throwable?) = Unit

        override fun reportUnhandledException(exception: Throwable) = Unit

        override fun reportAnr(p0: Map<Thread, Array<StackTraceElement>>) = Unit

        override fun reportEvent(eventName: String) = Unit
        override fun reportEvent(eventName: String, attributes: Map<String, Any>?) = Unit
        override fun reportEvent(eventName: String, jsonValue: String?) = Unit
        override fun reportEvent(event: AppMetricaEvent) = Unit

        override fun reportRevenue(revenue: Revenue) = Unit
        override fun reportAdRevenue(revenue: AdRevenue) = Unit
        override fun reportECommerce(event: ECommerceEvent) = Unit
        override fun reportUserProfile(profile: UserProfile) = Unit

        override fun setDataSendingEnabled(isEnabled: Boolean) = Unit
        override fun setUserProfileID(profileID: String?) = Unit

        override fun sendEventsBuffer() = Unit

        override fun getPluginExtension(): IPluginReporter = NoOpPluginReporter

        override fun putAppEnvironmentValue(p0: String, p1: String?) = Unit
        override fun putErrorEnvironmentValue(p0: String, p1: String?) = Unit

        override fun clearAppEnvironment() = Unit
        override fun clearErrorEnvironment() = Unit
    }

    private object NoOpPluginReporter : IPluginReporter {

        override fun reportError(errorDetails: PluginErrorDetails, message: String?) = Unit

        override fun reportError(identifier: String, message: String?, errorDetails: PluginErrorDetails?) = Unit

        override fun reportUnhandledException(errorDetails: PluginErrorDetails) = Unit
    }
}
