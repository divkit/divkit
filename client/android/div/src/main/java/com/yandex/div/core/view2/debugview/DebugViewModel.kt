package com.yandex.div.core.view2.debugview

import androidx.annotation.DrawableRes

internal sealed interface DebugViewModel {
    object Hidden : DebugViewModel

    data class InfoButton(
        val label: String,
        val notificationText: String? = null,
        @DrawableRes val background: Int,
    ) : DebugViewModel

    data class Details(
        val errorsAndWarningsOverview: String,
        val hotReload: HotReloadViewModel? = null,
    ) : DebugViewModel {

        class HotReloadViewModel(
            val title: String,
            val description: String?,
            val switchChecked: Boolean,
            val onSwitchClick: (isChecked: Boolean) -> Unit,
            val docLink: String?,
            val onDocLinkClick: () -> Unit,
            val address: String,
            val onAddressChanged: (address: String) -> Unit
        )
    }
}
