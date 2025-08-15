package com.yandex.divkit.regression

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yandex.div.core.util.SafeAlertDialogBuilder
import com.yandex.divkit.regression.data.Scenario
import com.yandex.divkit.regression.databinding.MetadataBottomsheetBinding

class MetadataBottomSheet : BottomSheetDialogFragment() {

    private var _binding: MetadataBottomsheetBinding? = null
    private val binding: MetadataBottomsheetBinding get() = _binding!!

    private val scenarioHost: ScenarioHost get() = requireActivity() as ScenarioHost

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MetadataBottomsheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.closeButton.setOnClickListener { dismiss() }
        bindScenarioMetadata(scenarioHost.scenario)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun bindScenarioMetadata(scenario: Scenario) {
        binding.scenarioSteps.text = scenario.steps?.formatList()
        binding.scenarioExpectations.text = scenario.expected_results?.formatList()
        bindLogging()
        binding.clearLoggingButton.setOnClickListener { clearLogging() }
        binding.toggleScenarioButton.setOnClickListener { toggleScenario() }
        binding.toggleLoggingButton.setOnClickListener { toggleLogging() }
    }

    private fun clearLogging() {
        SafeAlertDialogBuilder(requireContext())
            .setTitle(R.string.clear)
            .setPositiveButton(R.string.clear) { _, _ ->
                binding.logging.text = null
                scenarioHost.initLogging()
                bindLogging()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun bindLogging() {
        scenarioHost.logMessages.forEach { binding.logging.append("$it\n") }
        binding.logging.doOnPreDraw {
            val tv = binding.logging
            val layout = tv.layout
            val dy = layout.getLineBottom(tv.lineCount - 1) - tv.scrollY - tv.height
            if (dy > 0) {
                tv.scrollBy(0, dy)
            }
        }
    }

    private fun toggleScenario() {
        binding.scenarioGroup.isVisible = !binding.scenarioGroup.isVisible
        binding.toggleScenarioButton.text = binding.scenarioGroup.isVisible.toggleText
    }

    private fun toggleLogging() {
        binding.logging.isVisible = !binding.logging.isVisible
        binding.toggleLoggingButton.text = binding.logging.isVisible.toggleText
        binding.clearLoggingButton.isVisible = binding.logging.isVisible
    }

    private fun List<String>.formatList() = joinToString("\n") { "• $it" }

    private val Boolean.toggleText: String
        get() = getString(if (this) R.string.hide_toggle else R.string.show_toggle)

    interface ScenarioHost {
        val scenario: Scenario
        val logMessages: List<String>

        fun initLogging()
    }

    companion object {
        const val TAG = "MetadataBottomSheet"
    }
}
