package com.yandex.divkit.demo.settings.dsl

import com.yandex.divkit.demo.R
import com.yandex.divkit.demo.settings.FlagPreferenceProvider
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.frameLayout
import org.jetbrains.anko.internals.AnkoInternals

/**
 * Bridge for Anko.
 */
fun AnkoContext<Any>.preferenceFragment(
    flagPreferenceProvider: FlagPreferenceProvider,
    id: Int = R.id.settings_root_layout,
    init: DslPreferenceFragment.() -> Unit
) = frameLayout {
    this.id = id
    fragment = DslPreferenceFragment(flagPreferenceProvider, init)
}

var android.view.ViewGroup.fragment: androidx.fragment.app.Fragment
    get() = AnkoInternals.noGetter()
    set(fragment) {
        val activity = context as androidx.fragment.app.FragmentActivity
        activity.supportFragmentManager.beginTransaction().replace(id, fragment).commit()
    }
