package com.anetos.parkme.view.widget.about

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anetos.parkme.R
import com.anetos.parkme.core.helper.BaseDialogFragment
import com.anetos.parkme.core.helper.removeLinksUnderline
import com.anetos.parkme.core.helper.stringResource
import com.anetos.parkme.core.helper.withBinding
import com.anetos.parkme.databinding.DialogAboutFragmentBinding

class AboutDialogFragment : BaseDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = DialogAboutFragmentBinding.inflate(inflater, container, false).withBinding {
        setupState()
        setupListeners()
    }

    private fun DialogAboutFragmentBinding.setupState() {
        tb.tvDialogTitle.text = context?.stringResource(R.string.about)
        tvAbout.removeLinksUnderline()
        tvAbout.movementMethod = LinkMovementMethod.getInstance()
        btnOkay.text = BUTTON_TEXT
    }

    private fun DialogAboutFragmentBinding.setupListeners() {
        btnOkay.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        const val BUTTON_TEXT = "Okay"
    }
}