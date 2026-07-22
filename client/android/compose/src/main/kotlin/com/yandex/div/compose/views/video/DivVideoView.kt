package com.yandex.div.compose.views.video

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.FixedScale
import androidx.compose.ui.platform.LocalDensity
import coil3.ImageLoader
import coil3.compose.rememberAsyncImagePainter
import com.yandex.div.compose.context.LocalDivViewContext
import com.yandex.div.compose.context.divContext
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.images.ImageRequestParams
import com.yandex.div.compose.images.decodePreview
import com.yandex.div.compose.images.rememberImageRequest
import com.yandex.div.compose.utils.reportError
import com.yandex.div.compose.utils.variables.mutableStateFromIntegerVariable
import com.yandex.div.compose.video.DivVideoPlayer
import com.yandex.div.compose.video.DivVideoPlayerConfig
import com.yandex.div.compose.video.DivVideoResolution
import com.yandex.div.compose.video.DivVideoSource
import com.yandex.div.compose.video.registerPlayer
import com.yandex.div2.DivVideo
import com.yandex.div2.DivVideoScale
import com.yandex.div2.DivVideoSource as Div2VideoSource

@Composable
internal fun DivVideoView(modifier: Modifier, data: DivVideo) {
    val component = divContext.component
    val config = data.observedConfig()

    if (config.sources.isEmpty() && data.playerSettingsPayload == null) {
        reportError(
            "Neither 'video_source' nor 'player_settings_payload' are specified for video with id '${data.id}'"
        )
    }

    val player = remember { component.playerFactory.makePlayer() }

    DisposableEffect(player) {
        onDispose { player.release() }
    }

    data.id?.let {
        LocalDivViewContext.current.videoPlayerStorage.registerPlayer(it, player)
    }

    ObserveVideoActions(player, data)

    data.elapsedTimeVariable?.let { variableName ->
        ObserveElapsedTimeVariable(player = player, variableName = variableName)
    }

    Box(modifier = modifier) {
        player.Content(config = config, modifier = Modifier.fillMaxSize())

        if (!player.isReady.collectAsState().value) {
            data.preview?.let {
                VideoPreviewImage(
                    imageLoader = component.imageLoader,
                    scale = config.scale,
                    preview = it.observedValue(transform = ::decodePreview)
                )
            }
        }
    }
}

@Composable
private fun ObserveElapsedTimeVariable(player: DivVideoPlayer, variableName: String) {
    val variableState = mutableStateFromIntegerVariable(variableName) ?: return

    LaunchedEffect(player) {
        player.currentTimeMs.collect { timeMs ->
            variableState.value = timeMs
        }
    }

    LaunchedEffect(variableState.value) {
        if (variableState.value != player.currentTimeMs.value) {
            player.seek(variableState.value)
        }
    }
}

@Composable
private fun VideoPreviewImage(imageLoader: ImageLoader, scale: DivVideoScale, preview: ByteArray) {
    val density = LocalDensity.current.density
    val contentScale = scale.toContentScale(density)
    val imageRequest = rememberImageRequest(
        ImageRequestParams(data = preview, transformations = emptyList())
    )
    Image(
        modifier = Modifier.fillMaxSize(),
        painter = rememberAsyncImagePainter(model = imageRequest, imageLoader = imageLoader),
        contentDescription = null,
        contentScale = contentScale,
    )
}

@Composable
private fun DivVideo.observedConfig(): DivVideoPlayerConfig {
    return DivVideoPlayerConfig(
        sources = videoSources.orEmpty().map { it.observedPlayerSource() },
        autoplay = autostart.observedValue(),
        repeatable = repeatable.observedValue(),
        payload = playerSettingsPayload?.observedValue(),
        muted = muted.observedValue(),
        playbackSpeed = playbackSpeed.observedValue().toFloat(),
        scale = scale.observedValue(),
    )
}

@Composable
private fun Div2VideoSource.observedPlayerSource(): DivVideoSource {
    return DivVideoSource(
        url = url.observedValue(),
        mimeType = mimeType.observedValue(),
        resolution = resolution?.let {
            DivVideoResolution(
                width = it.width.observedIntValue(),
                height = it.height.observedIntValue(),
            )
        },
        bitrate = bitrate?.observedValue(),
    )
}

private fun DivVideoScale.toContentScale(density: Float): ContentScale = when (this) {
    DivVideoScale.FILL -> ContentScale.Crop
    DivVideoScale.FIT -> ContentScale.Fit
    DivVideoScale.NO_SCALE -> FixedScale(density)
}
