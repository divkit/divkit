package com.yandex.divkit.regression.screenrecord

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Environment
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.getSystemService
import androidx.lifecycle.LifecycleOwner
import com.yandex.div.internal.KLog
import com.yandex.divkit.regression.R
import com.yandex.divkit.regression.di.ActivityScope
import com.yandex.divkit.regression.utils.shortMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.IOException
import javax.inject.Inject

private const val TAG = "ScenarioScreenRecorder"

private const val DISPLAY_WIDTH = 480
private const val DISPLAY_HEIGHT = 640

private const val MEDIA_PROJECTION_KEY = "media_projection_key"
private const val SCENARIO_CAPTURE_FILENAME = "scenario_capture.mp4"

@ActivityScope
internal class ScenarioScreenRecord @Inject constructor(
    private val context: Context,
    private val registry: ActivityResultRegistry,
) : ScreenRecord {

    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var projectionManager: MediaProjectionManager
    private lateinit var mediaProjection: MediaProjection
    private lateinit var virtualDisplay: VirtualDisplay
    private var screenDensity: Int = -1
    private val mediaProjectionCallback = MediaProjectionCallback()

    private lateinit var mediaProjectionLaunch: ActivityResultLauncher<Intent>

    private var isRecording = false

    private val _recordState = MutableStateFlow(ScreenRecord.RecordState.STOPPED)
    override val recordState: StateFlow<ScreenRecord.RecordState> = _recordState.asStateFlow()

    override fun onCreate(owner: LifecycleOwner) {
        screenDensity = context.resources.displayMetrics.densityDpi
        projectionManager = context.getSystemService()!!
        mediaProjectionLaunch = registry.register(
            MEDIA_PROJECTION_KEY,
            owner,
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                mediaProjection =
                    projectionManager.getMediaProjection(result.resultCode, result.data!!)
                mediaProjection.registerCallback(mediaProjectionCallback, null)
                virtualDisplay = createVirtualDisplay()
                startScreenRecording()
            } else {
                showFailMessage()
            }
        }
    }

    override fun onStart(owner: LifecycleOwner) {
        initMediaRecorder()
        prepareMediaRecorderSafe()
        mediaProjectionLaunch.launch(projectionManager.createScreenCaptureIntent())
    }

    override fun onStop(owner: LifecycleOwner) {
        stopMediaProjection()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        stopMediaProjection()
    }

    private fun stopMediaProjection() {
        if (isRecording) {
            mediaProjection.stop()
            isRecording = false
        }
    }

    private fun startScreenRecording() {
        mediaRecorder.start()
        isRecording = true
        _recordState.value = ScreenRecord.RecordState.STARTED
    }

    private fun stopScreenRecording() {
        mediaProjection.stop()
        mediaRecorder.stop()
        mediaRecorder.release()
        virtualDisplay.release()
        _recordState.value = ScreenRecord.RecordState.STOPPED
    }

    private fun showFailMessage(disabled: Boolean = true) {
        val message =
            R.string.screen_record_disabled.takeIf { disabled } ?: R.string.screen_record_failed
        shortMessage(context, message)
    }

    private fun initMediaRecorder() {
        mediaRecorder = MediaRecorder()
        with(mediaRecorder) {
            setVideoSource(MediaRecorder.VideoSource.SURFACE)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            setVideoEncodingBitRate(512 * 1000)
            setVideoFrameRate(30)
            setVideoSize(DISPLAY_WIDTH, DISPLAY_HEIGHT)
            setOutputFile(
                File(
                    context.getExternalFilesDir(Environment.DIRECTORY_MOVIES),
                    SCENARIO_CAPTURE_FILENAME
                ).absolutePath
            )
        }
    }

    private fun prepareMediaRecorderSafe() {
        try {
            mediaRecorder.prepare()
        } catch (e: IllegalStateException) {
            failPrepareMediaRecorder(e)
        } catch (e: IOException) {
            failPrepareMediaRecorder(e)
        }
    }

    private fun failPrepareMediaRecorder(t: Throwable) {
        KLog.e(TAG, t) { "Failed to prepare media recorder" }
        showFailMessage(disabled = false)
    }

    private fun createVirtualDisplay(): VirtualDisplay {
        return mediaProjection.createVirtualDisplay(
            "ScreenRecord",
            DISPLAY_WIDTH, DISPLAY_HEIGHT, screenDensity,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            mediaRecorder.surface, null, null
        )
    }

    private inner class MediaProjectionCallback : MediaProjection.Callback() {
        override fun onStop() {
            stopScreenRecording()
        }
    }
}
