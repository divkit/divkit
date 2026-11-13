package com.yandex.div.compose.views.video

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.FixedScale
import androidx.compose.ui.platform.LocalDensity
import coil3.compose.rememberAsyncImagePainter
import com.yandex.div.compose.LocalDivContext
import com.yandex.div.compose.di.LocalComponent
import com.yandex.div.compose.expressions.observedFloatValue
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.images.ImageRequestParams
import com.yandex.div.compose.images.rememberImageRequest
import com.yandex.div.compose.utils.reportError
import com.yandex.div.compose.variables.mutableStateFromIntegerVariable
import com.yandex.div.compose.video.DivVideoPlayer
import com.yandex.div.compose.video.DivVideoPlayerConfig
import com.yandex.div.compose.video.DivVideoResolution
import com.yandex.div.compose.video.DivVideoSource
import com.yandex.div.compose.video.registerPlayer
import com.yandex.div2.DivVideo
import com.yandex.div2.DivVideoScale
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import com.yandex.div2.DivVideoSource as Div2VideoSource

@Composable
internal fun DivVideoView(modifier: Modifier, data: DivVideo) {
    val component = LocalComponent.current
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
        component.videoPlayerStorage.registerPlayer(it, player)
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

/**
 * Two-way binding of `elapsed_time_variable`: the variable seeks the player and the player's progress
 * is written to the variable. A position the player reported is not seeked back.
 */
@Composable
private fun ObserveElapsedTimeVariable(player: DivVideoPlayer, variableName: String) {
    val variableState = mutableStateFromIntegerVariable(variableName) ?: return
    LaunchedEffect(player, variableState) {
        var reportedTimeMs: Long? = null
        launch {
            snapshotFlow { variableState.value }.collect { timeMs ->
                if (timeMs != reportedTimeMs) player.seek(timeMs)
            }
        }
        // Subscribed before the launched seek runs, so a synchronous seek acknowledgement is seen.
        player.currentTimeMs.drop(1).collect { timeMs ->
            reportedTimeMs = timeMs
            variableState.value = timeMs
        }
    }
}

@Composable
private fun VideoPreviewImage(preview: ByteArray, scale: DivVideoScale) {
    val component = LocalDivContext.current.component
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
