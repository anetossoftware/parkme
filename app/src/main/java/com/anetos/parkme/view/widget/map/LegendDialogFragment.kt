package com.anetos.parkme.view.widget.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anetos.parkme.core.helper.BaseDialogFragment
import com.anetos.parkme.core.helper.withBinding
import com.anetos.parkme.databinding.BaseDialogFragmentBinding
import com.anetos.parkme.databinding.DialogLegendFragmentBinding

class LegendDialogFragment : BaseDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        DialogLegendFragmentBinding.inflate(inflater, container, false).withBinding {
            setupBaseDialogFragment()
            setupState()
            setupListeners()
        }

    private fun DialogLegendFragmentBinding.setupBaseDialogFragment() =
        BaseDialogFragmentBinding.bind(root).apply {
            tvDialogTitle.text = DIALOG_TITLE
        }

    private fun DialogLegendFragmentBinding.setupState() {
        btnConfirm.text = BUTTON_TITLE
        tvParkingAvailable.text = PARKING_AVAILABLE_TEXT
        tvParkingOccupied.text = PARKING_OCCUPIED_TEXT
    }

    private fun DialogLegendFragmentBinding.setupListeners() {
        btnConfirm.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        val TAG = LegendDialogFragment::class.java.simpleName
        const val DIALOG_TITLE = "Legend Info"
        const val BUTTON_TITLE = "Okay!"
        const val PARKING_AVAILABLE_TEXT = "Shows parking spot is available for booking."
        const val PARKING_OCCUPIED_TEXT = "Shows parking spot is occupied."
    }
}