package com.yandex.divkit.demo.div

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yandex.divkit.demo.databinding.Div2MetadataBottomsheetBinding
import com.yandex.divkit.demo.div.editor.DEMO_ACTIVITY_COMPONENT_NAME
import com.yandex.divkit.demo.div.editor.DIV_PARSING_DATA
import com.yandex.divkit.demo.div.editor.DIV_PARSING_TEMPLATES
import com.yandex.divkit.demo.div.editor.DIV_RENDER_TOTAL
import com.yandex.divkit.demo.div.histogram.LoggingHistogramBridge
import java.lang.StringBuilder

class Div2MetadataBottomSheet : BottomSheetDialogFragment() {

    private var _binding: Div2MetadataBottomsheetBinding? = null
    private val binding: Div2MetadataBottomsheetBinding get() = _binding!!

    private val metadataHost: MetadataHost get() = requireActivity() as MetadataHost

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = Div2MetadataBottomsheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindRenderingTimeMetadata(metadataHost.renderingTimeMessages)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun bindRenderingTimeMetadata(renderingTimeMessages: Map<String, LoggingHistogramBridge.TimeHistogram>) {
        val sb = StringBuilder()
        sb.append("Rendering time:\n\n")

        sb.append("• ", renderingTimeMessages[DIV_RENDER_TOTAL].toString().removePrefix("$DEMO_ACTIVITY_COMPONENT_NAME."), '\n')
        sb.append("• ", renderingTimeMessages[DIV_PARSING_DATA].toString().removePrefix("$DEMO_ACTIVITY_COMPONENT_NAME."), '\n')
        if (renderingTimeMessages.containsKey(DIV_PARSING_TEMPLATES)) {
            sb.append("• ", renderingTimeMessages[DIV_PARSING_TEMPLATES].toString().removePrefix("$DEMO_ACTIVITY_COMPONENT_NAME."))
        }
        binding.renderTimeView.text = sb.toString()
    }


    interface MetadataHost {
        val renderingTimeMessages: HashMap<String, LoggingHistogramBridge.TimeHistogram>
    }

    companion object {
        const val TAG = "MetadataBottomSheet"
    }
}
