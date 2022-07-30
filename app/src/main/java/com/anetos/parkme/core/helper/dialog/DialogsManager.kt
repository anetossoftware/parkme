package com.anetos.parkme.core.helper.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.WindowManager
import com.anetos.parkme.R
import com.anetos.parkme.databinding.DialogErrorBinding

object DialogsManager {
    private var loader: Dialog? = null

    fun showProgressDialog(context: Context) {
        dismissProgressDialog()
        try {
            loader = Dialog(context)
            loader?.setContentView(R.layout.dialog_loader)
            loader?.setCancelable(false)
            loader?.window?.setLayout(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            loader?.window?.setWindowAnimations(R.style.scaling_from_center)
            loader?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            loader?.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun dismissProgressDialog() {
        try {
            loader?.dismiss()
        } catch (err: Exception) {
            err.printStackTrace()
        }
    }

    fun showErrorDialog(
        context: Context,
        msg: String, btn: String, actions: IDialogActions?
    ) {
        val dialog = Dialog(context)
        val binding = DialogErrorBinding.inflate(
            LayoutInflater.from(context)
        )
        dialog.setContentView(binding.root)
        dialog.setCancelable(false)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setWindowAnimations(R.style.scaling_from_center)
        binding.message.text = msg
        binding.submit.text = btn
        binding.submit.setOnClickListener {
            actions?.onYesClicked()
            dialog.dismiss()
        }
        dialog.show()
    }
}