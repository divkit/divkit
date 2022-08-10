package com.yandex.test.idling

import android.util.Log
import android.view.View
import androidx.annotation.IdRes
import androidx.test.espresso.Espresso.onIdle
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import com.yandex.div.core.Disposable
import java.io.Closeable

private const val TAG = "IdlingResources"

fun waitForCondition(name: String? = null, condition: (SimpleIdlingResource) -> Boolean) {
    waitForIdlingResource(object : SimpleIdlingResource(description = name) {
        override fun checkIdle(): Boolean {
            return condition(this)
        }
    })
}

fun waitForView(@IdRes id: Int): View {
    val idlingResource = ViewIdlingResource(id)
    waitForIdlingResource(idlingResource)
    return idlingResource.view ?: throw IllegalStateException("view with id=%id missed unexpectedly")
}

fun waitForIdlingResource(res: IdlingResource) = res.register().use { onIdle() }

fun IdlingResource.await() = waitForIdlingResource(this)

fun IdlingResource.register(): Disposable = IdlingResourceRegistration(this)

private class IdlingResourceRegistration(
    private val resource: IdlingResource
) : Disposable {

    init {
        IdlingRegistry.getInstance().register(resource)
    }

    override fun close() {
        val registry = IdlingRegistry.getInstance()
        registry.unregister(resource)
        Log.i(
            TAG,
            "idling resource ${resource.name} unregistered, now ${registry.resources.size} resources registered"
        )
        (resource as? Closeable)?.close()
    }
}
