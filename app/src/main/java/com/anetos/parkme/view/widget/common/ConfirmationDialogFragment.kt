package com.anetos.parkme.view.widget.common

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anetos.parkme.core.helper.BaseDialogFragment
import com.anetos.parkme.core.helper.withBinding
import com.anetos.parkme.databinding.BaseDialogFragmentBinding
import com.anetos.parkme.databinding.ConfirmationDialogFragmentBinding

class ConfirmationDialogFragment(
    var dialogTitle: String? = null,
    var confirmation: String? = null,
    var description: String? = null,
    var buttonText: String? = null,
) : BaseDialogFragment() {

    var onClick: onConfirmationClickListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        ConfirmationDialogFragmentBinding.inflate(inflater, container, false).withBinding {
            setupBaseDialogFragment()
            setupState()
            setupListeners()
        }

    private fun ConfirmationDialogFragmentBinding.setupBaseDialogFragment() =
        BaseDialogFragmentBinding.bind(root).apply {
            tvDialogTitle.text = dialogTitle
        }

    private fun ConfirmationDialogFragmentBinding.setupState() {
        tvConfirmation.text = confirmation
        tvDescription.text = description
        btnConfirm.text = buttonText
    }

    private fun ConfirmationDialogFragmentBinding.setupListeners() {
        btnConfirm.setOnClickListener {
            onClick?.onClick(this@ConfirmationDialogFragment)
            dismiss()
        }
    }

    interface onConfirmationClickListener {
        fun onClick(confirmationDialogFragment: ConfirmationDialogFragment)
    }

    fun onClickListener(onClick: onConfirmationClickListener): ConfirmationDialogFragment {
        this.onClick = onClick
        return this
    }

}