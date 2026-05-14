@file:OptIn(UnstableApi::class, ExperimentalApi::class)
package com.yandex.div.compose.views.video

import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.FixedScale
import androidx.compose.ui.platform.LocalDensity
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.ExperimentalApi
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.SURFACE_TYPE_TEXTURE_VIEW
import androidx.media3.ui.compose.material3.Player
import coil3.ImageLoader
import coil3.compose.rememberAsyncImagePainter
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.context.divContext
import com.yandex.div.compose.context.expressionResolver
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.images.ImageRequestParams
import com.yandex.div.compose.images.decodePreview
import com.yandex.div.compose.images.rememberImageRequest
import com.yandex.div2.DivVideo
import com.yandex.div2.DivVideoScale
import com.yandex.div2.DivVideoSource

@Composable
internal fun DivVideoView(
    modifier: Modifier,
    data: DivVideo
) {
    val context = divContext
    val preview = data.preview?.observedValue(transform = ::decodePreview)
    val scale = data.scale.observedValue()

    val player = remember { ExoPlayer.Builder(context).build() }
    val videoState = rememberVideoState(data.elapsedTimeVariable)

    videoState.BindToPlayer(player)
    SetupPlayer(player, data)
    SetupMediaSource(player, data.videoSources)

    VideoView(
        player = player,
        context = context,
        modifier = modifier,
        scale = scale,
        videoState = videoState,
        preview = preview
    )
}

@Composable
private fun VideoView(
    player: Player,
    context: DivContext,
    modifier: Modifier,
    scale: DivVideoScale,
    videoState: DivVideoState,
    preview: ByteArray?
) {
    val contentScale = scale.toContentScale(LocalDensity.current.density)
    Box(
        modifier = modifier.clipToBounds(),
        contentAlignment = Alignment.Center,
    ) {
        Player(
            player,
            Modifier.fillMaxSize(),
            surfaceType = SURFACE_TYPE_TEXTURE_VIEW,
            contentScale = contentScale,
            showControls = false,
            shutter = {},
        )

        if (!videoState.isReady && preview != null) {
            VideoPreview(
                preview = preview,
                imageLoader = context.component.imageLoader,
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun VideoPreview(
    preview: ByteArray,
    imageLoader: ImageLoader,
    contentScale: ContentScale,
    modifier: Modifier,
) {
    val previewRequest = rememberImageRequest(
        ImageRequestParams(
            data = preview,
            transformations = emptyList()
        )
    )
    val previewPainter = rememberAsyncImagePainter(
        model = previewRequest,
        imageLoader = imageLoader
    )
    Image(
        modifier = modifier,
        painter = previewPainter,
        contentDescription = null,
        contentScale = contentScale,
        alignment = Alignment.Center
    )
}

@Composable
private fun SetupPlayer(player: ExoPlayer, data: DivVideo) {
    val resolver = expressionResolver

    val muted = data.muted.observedValue()
    val autostart = data.autostart.evaluate(resolver)
    val repeatable = data.repeatable.evaluate(resolver)

    LaunchedEffect(muted) {
        player.volume = if (muted) 0f else 1f
    }
    LaunchedEffect(repeatable) {
        player.repeatMode = if (repeatable) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
    }
    LaunchedEffect(autostart) {
        player.playWhenReady = autostart
    }
}

@Composable
private fun SetupMediaSource(player: ExoPlayer, sources: List<DivVideoSource>?) {
    val source = sources?.firstOrNull() ?: return
    val url = source.url.observedValue()
    val mimeType = source.mimeType.observedValue()

    LaunchedEffect(url, mimeType) {
        val mediaItem = MediaItem.Builder()
            .setUri(url)
            .setMimeType(mimeType)
            .build()
        player.setMediaItem(mediaItem)
        player.prepare()
    }
}

private fun DivVideoScale.toContentScale(density: Float): ContentScale {
    return when (this) {
        DivVideoScale.FILL -> ContentScale.Crop
        DivVideoScale.FIT -> ContentScale.Fit
        DivVideoScale.NO_SCALE -> FixedScale(density)
    }
}
