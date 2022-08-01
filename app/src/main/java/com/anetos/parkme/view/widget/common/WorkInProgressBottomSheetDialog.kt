package com.anetos.parkme.view.widget.common

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anetos.parkme.core.helper.BaseDialogFragment
import com.anetos.parkme.core.helper.withBinding
import com.anetos.parkme.databinding.BaseDialogFragmentBinding
import com.anetos.parkme.databinding.WorkinprogressBottomSheetDialogBinding

class WorkInProgressBottomSheetDialog(
    var ctx: Context? = null,
    var btnText: String? = null,
    var description: String? = null
) : BaseDialogFragment() {

    var onClick: onClickListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        WorkinprogressBottomSheetDialogBinding.inflate(inflater, container, false).withBinding {
            setupBaseDialogFragment()
            setupState()
            setupListeners()
        }

    private fun WorkinprogressBottomSheetDialogBinding.setupBaseDialogFragment() =
        BaseDialogFragmentBinding.bind(root).apply {
            tvDialogTitle.text = btnText ?: DESCRIPTION
        }

    private fun WorkinprogressBottomSheetDialogBinding.setupState() {
        //tvDescription.text = description ?: DESCRIPTION
        btnConfirm.text = btnText ?: BUTTON_TITLE
    }

    private fun WorkinprogressBottomSheetDialogBinding.setupListeners() {
        btnConfirm.setOnClickListener {
            onClick?.onClick(this@WorkInProgressBottomSheetDialog)
            dismiss()
        }
    }

    interface onClickListener {
        fun onClick(confirmationDialogFragment: WorkInProgressBottomSheetDialog)
    }

    fun onClickListener(onClick: onClickListener): WorkInProgressBottomSheetDialog {
        this.onClick = onClick
        return this
    }

    companion object {
        val TAG = WorkInProgressBottomSheetDialog::class.qualifiedName
        val BUTTON_TITLE = "Dismiss"
        val DESCRIPTION = "Work in progress"
    }
}