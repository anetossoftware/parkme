package com.anetos.parkme.core.helper.dialog

interface IDialogActions {
    fun onYesClicked()
    fun onNoClicked()
    fun onSubmitClicked(txt: String)
}