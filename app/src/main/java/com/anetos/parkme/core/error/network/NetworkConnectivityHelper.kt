package com.anetos.parkme.core.error.network

import android.content.Context
import com.anetos.parkme.core.error.AppError
import com.anetos.parkme.core.error.ErrorType
import java.lang.ref.WeakReference

class NetworkConnectivityHelper(val context: WeakReference<Context>) {

    fun getConsolidatedErrorObject(): AppError {
        context.get()?.let {
            val title = (GENERIC_SLIDE_DOWN_POPUP_ERROR_TEXT_NO_INTERNET)
            val message = (GENERIC_SLIDE_DOWN_POPUP_ERROR_TEXT_NO_INTERNET_RETRY)
            return AppError(ErrorType.NO_CONNECTIVITY, title, message)
        }
        return AppError(ErrorType.WARNING, "", "")
    }

    companion object {
        private const val GENERIC_SLIDE_DOWN_POPUP_ERROR_TEXT_NO_INTERNET = "generic_slide_down_popup_error_text_no_internet"
        private const val GENERIC_SLIDE_DOWN_POPUP_ERROR_TEXT_NO_INTERNET_RETRY = "generic_slide_down_popup_error_text_no_internet_retry"
    }
}