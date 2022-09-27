package com.yandex.divkit.demo.div

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yandex.divkit.demo.databinding.Div2MetadataBottomsheetBinding
import com.yandex.divkit.regression.data.Scenario

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
        bindMetadata(metadataHost.renderingTimeMessages)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun bindMetadata(renderingTimeMessages: List<String>) {
        binding.renderTimeView.text = renderingTimeMessages.formatList()
    }

    private fun List<String>.formatList() = joinToString("\n") { it }


    interface MetadataHost {
        val renderingTimeMessages: ArrayList<String>
    }

    companion object {
        const val TAG = "MetadataBottomSheet"
    }
}
