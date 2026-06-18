package com.yandex.div.compose.actions

import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.video.VideoPlayerStorage
import com.yandex.div.internal.actions.DivUntypedAction
import com.yandex.div2.DivActionVideo
import javax.inject.Inject

internal class VideoActionHandler @Inject constructor(
    private val videoPlayerStorage: VideoPlayerStorage,
    private val reporter: DivReporter,
) {

    fun handle(context: DivActionHandlingContext, action: DivActionVideo) {
        val resolver = context.expressionResolver
        applyCommand(
            id = action.id.evaluate(resolver),
            command = action.action.evaluate(resolver)
        )
    }

    fun handle(action: DivUntypedAction.Video) {
        val command = DivActionVideo.Action.fromString(action.action)
        if (command == null) {
            reporter.reportError("No such video action: ${action.action}")
            return
        }

        applyCommand(id = action.id, command = command)
    }

    private fun applyCommand(id: String, command: DivActionVideo.Action) {
        val player = videoPlayerStorage.get(id)
        if (player == null) {
            reporter.reportError("No video with id '$id' found for video action '$command'")
            return
        }

        when (command) {
            DivActionVideo.Action.START -> player.play()
            DivActionVideo.Action.PAUSE -> player.pause()
        }
    }
}
