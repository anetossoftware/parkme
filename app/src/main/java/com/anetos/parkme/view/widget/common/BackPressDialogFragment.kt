package com.anetos.parkme.view.widget.common

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anetos.parkme.core.helper.BaseDialogFragment
import com.anetos.parkme.core.helper.withBinding
import com.anetos.parkme.databinding.BackpressDialogFragmentBinding
import com.anetos.parkme.databinding.BaseDialogFragmentBinding

class BackPressDialogFragment(
    var ctx: Context? = null,
    var dialogTitle: String? = null,
    var confirmation: String? = null,
    var description: String? = null,
    var positiveButtonText: String? = null,
    var negativeButtonText: String? = null,
) : BaseDialogFragment() {

    var onClick: onBackPressClickListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        BackpressDialogFragmentBinding.inflate(inflater, container, false).withBinding {
            setupBaseDialogFragment()
            setupState()
            setupListeners()
        }

    private fun BackpressDialogFragmentBinding.setupBaseDialogFragment() =
        BaseDialogFragmentBinding.bind(root).apply {
            tvDialogTitle.text = dialogTitle
        }

    private fun BackpressDialogFragmentBinding.setupState() {
        tvConfirmation.text = confirmation
        tvDescription.text = description
        btnConfirm.text = positiveButtonText
        btnDismiss.text = negativeButtonText
    }

    private fun BackpressDialogFragmentBinding.setupListeners() {
        btnConfirm.setOnClickListener {
            onClick?.onClick(this@BackPressDialogFragment)
            dismiss()
        }
        btnDismiss.setOnClickListener {
            onClick?.onNegativeClick(this@BackPressDialogFragment)
            dismiss()
        }
    }

    interface onBackPressClickListener {
        fun onClick(backPressDialogFragment: BackPressDialogFragment)
        fun onNegativeClick(backPressDialogFragment: BackPressDialogFragment) {}
    }

    fun onClickListener(onClick: onBackPressClickListener): BackPressDialogFragment {
        this.onClick = onClick
        return this
    }

}