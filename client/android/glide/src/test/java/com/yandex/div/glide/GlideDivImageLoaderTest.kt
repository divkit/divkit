package com.yandex.div.glide

import android.graphics.drawable.Drawable
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.os.Looper
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.yandex.div.core.images.DivCachedImage
import com.yandex.div.core.images.DivImageDownloadCallback
import com.yandex.div.core.images.LoadReference
import org.junit.After
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.LooperMode
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
class GlideDivImageLoaderTest {

    private val context = RuntimeEnvironment.application
    private val imageUri = Uri.fromFile(File(context.cacheDir, "host-before-loader.svg").apply {
        writeText(MINIMAL_SVG)
    })
    private val hostRequestFinished = CountDownLatch(1)
    private val hostTarget = object : CustomTarget<Drawable>() {
        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
            hostRequestFinished.countDown()
        }

        override fun onLoadFailed(errorDrawable: Drawable?) {
            hostRequestFinished.countDown()
        }

        override fun onLoadCleared(placeholder: Drawable?) = Unit
    }
    private val result = AtomicReference<DivCachedImage>()
    private val error = AtomicReference<Throwable>()
    private val divKitRequestFinished = CountDownLatch(1)
    private val callback = object : DivImageDownloadCallback() {
        override fun onSuccess(cachedImage: DivCachedImage) {
            result.set(cachedImage)
            divKitRequestFinished.countDown()
        }

        override fun onError(cause: Throwable?) {
            error.set(cause)
            divKitRequestFinished.countDown()
        }
    }
    private var loadReference: LoadReference? = null

    @After
    fun tearDown() {
        loadReference?.cancel()
        Glide.with(context).clear(hostTarget)
        Glide.tearDown()
    }

    @Test(timeout = 10_000)
    fun `svg loads after host request warms registry cache`() {
        Glide.with(context).asDrawable().load(imageUri).into(hostTarget)
        awaitMainLooper(hostRequestFinished)

        loadReference = GlideDivImageLoader(context).loadImage(imageUri.toString(), callback)
        awaitMainLooper(divKitRequestFinished)

        assertNull(error.get())
        assertTrue((result.get() as DivCachedImage.Drawable).drawable is PictureDrawable)
    }

    private fun awaitMainLooper(latch: CountDownLatch) {
        val deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5)
        while (latch.count != 0L && System.nanoTime() < deadline) {
            shadowOf(Looper.getMainLooper()).idle()
            Thread.sleep(10)
        }
        shadowOf(Looper.getMainLooper()).idle()
        assertTrue("Image request did not finish", latch.count == 0L)
    }

    private companion object {
        const val MINIMAL_SVG = """
            <svg xmlns="http://www.w3.org/2000/svg" width="1" height="1">
                <rect width="1" height="1" fill="red"/>
            </svg>
        """
    }
}
