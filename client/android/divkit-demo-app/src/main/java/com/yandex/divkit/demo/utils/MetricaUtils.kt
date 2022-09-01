package com.yandex.divkit.demo.utils

import android.content.Context
import com.yandex.divkit.demo.BuildConfig
import com.yandex.metrica.IReporter
import com.yandex.metrica.Revenue
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.ecommerce.ECommerceEvent
import com.yandex.metrica.plugins.IPluginReporter
import com.yandex.metrica.plugins.PluginErrorDetails
import com.yandex.metrica.profile.UserProfile

object MetricaUtils {

    /**
     * Gets AppMetrica reporter for AliceKit components.
     */
    @JvmStatic
    fun getReporter(context: Context): IReporter {
        if (BuildConfig.METRICA_API_KEY.isEmpty()) {
            return NoOpReporter
        }
        return YandexMetrica.getReporter(context.applicationContext, BuildConfig.METRICA_API_KEY)
    }

    private object NoOpReporter : IReporter {

        override fun pauseSession() = Unit

        override fun resumeSession() = Unit

        override fun reportError(identifier: String, message: String?) = Unit

        override fun reportError(identifier: String, message: String?, error: Throwable?) = Unit

        override fun reportError(message: String, error: Throwable?) = Unit

        override fun reportUnhandledException(exception: Throwable) = Unit

        override fun reportEvent(eventName: String) = Unit

        override fun reportEvent(eventName: String, attributes: Map<String, Any>?) = Unit

        override fun reportEvent(eventName: String, jsonValue: String?) = Unit

        override fun reportRevenue(revenue: Revenue) = Unit

        override fun reportECommerce(event: ECommerceEvent) = Unit

        override fun reportUserProfile(profile: UserProfile) = Unit

        override fun sendEventsBuffer() = Unit

        override fun getPluginExtension(): IPluginReporter = NoOpPluginReporter

        override fun setStatisticsSending(enabled: Boolean) = Unit

        override fun setUserProfileID(profileID: String?) = Unit
    }

    private object NoOpPluginReporter : IPluginReporter {

        override fun reportError(errorDetails: PluginErrorDetails, message: String?) = Unit

        override fun reportError(identifier: String, message: String?, errorDetails: PluginErrorDetails?) = Unit

        override fun reportUnhandledException(errorDetails: PluginErrorDetails) = Unit
    }
}
