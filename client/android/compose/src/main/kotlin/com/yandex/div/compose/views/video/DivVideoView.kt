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
import coil3.compose.rememberAsyncImagePainter
import com.yandex.div.compose.context.LocalDivViewContext
import com.yandex.div.compose.context.divContext
import com.yandex.div.compose.expressions.observedFloatValue
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.images.ImageRequestParams
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
            data.preview
                ?.observedValue(transform = component.imagePreviewDecoder::decodePreview)
                ?.let { preview ->
                    VideoPreviewImage(
                        preview = preview,
                        scale = config.scale
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
private fun VideoPreviewImage(preview: ByteArray, scale: DivVideoScale) {
    val component = divContext.component
    Image(
        modifier = Modifier.fillMaxSize(),
        painter = rememberAsyncImagePainter(
            model = rememberImageRequest(
                ImageRequestParams(data = preview)
            ),
            imageLoader = component.imageLoader,
            onState = component.debugConfiguration.imagePainterStateListener
        ),
        contentDescription = null,
        contentScale = scale.toContentScale(LocalDensity.current.density)
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
        playbackSpeed = playbackSpeed.observedFloatValue(),
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
