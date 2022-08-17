package com.anetos.parkme.core.error.view

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import com.anetos.parkme.R
import com.anetos.parkme.core.error.AppError
import com.anetos.parkme.core.error.ErrorType
import com.anetos.parkme.core.helper.CoroutineHelper
import com.anetos.parkme.core.helper.getStatusBarHeight
import com.anetos.parkme.core.helper.hide
import com.anetos.parkme.core.helper.show
import com.anetos.parkme.customlib.IconTextView
import kotlinx.coroutines.Job

class GenericSlideDownErrorPopup(
    private val context: Context,
    private val window: Window?,
    private val appError: AppError
) : PopupWindow() {

    private lateinit var warningIcon: IconTextView
    private lateinit var popupTextErrorTitle: TextView
    private lateinit var popupTextErrorMessage: TextView

    init {
        setupView()
        setDataToViewsDependOnType()
    }

    val coroutineHelper by lazy { CoroutineHelper() }
    var job: Job? = null

    private fun setupView() {
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = layoutInflater.inflate(R.layout.popup_view_generic_top_down_error, null)
        warningIcon = popupView.findViewById(R.id.warningIcon)
        popupTextErrorTitle = popupView.findViewById(R.id.popupTextErrorTitle)
        popupTextErrorMessage = popupView.findViewById(R.id.popupTextErrorMessage)
        width = RelativeLayout.LayoutParams.MATCH_PARENT
        height = RelativeLayout.LayoutParams.WRAP_CONTENT
        contentView = popupView
        animationStyle = R.style.GenericPopupErrorWindowAnimation
    }

    private fun setDataToViewsDependOnType() {
        when (appError.errorType) {
            ErrorType.NO_CONNECTIVITY -> {
                warningIcon.text = context.getString(R.string.fa_no_connection_sign)
            }
            ErrorType.WARNING -> {
                warningIcon.text = context.getString(R.string.fa_warning_sign)
            }
        }
        popupTextErrorTitle.text = appError.title
        if (appError.description.isNotBlank()) {
            popupTextErrorMessage.text = appError.description
            popupTextErrorMessage.show()
        } else {
            popupTextErrorMessage.hide()
        }
    }

    fun show() {
        val yPosition = getStatusBarHeight(context)
        val view = window?.decorView?.rootView

        view?.post {
            showAtLocation(view, Gravity.TOP, 0, yPosition)
        }
    }

    override fun showAtLocation(parent: View?, gravity: Int, x: Int, y: Int) {
        super.showAtLocation(parent, gravity, x, y)
        /*
        * We need to dismiss automatically the Popup after 2 seconds EXCEPT when
        * the we lost Network Connection!
        * */
        if (appError.errorType != ErrorType.NO_CONNECTIVITY) {
            job?.cancel()
            job = coroutineHelper.delayExecute(VISIBILITY_MILLISECONDS) {
                //EventHelper.publish(EventObserverList.DISMISS_GENERIC_ERROR_POPUP.observer(), null)
            }
        }
    }

    override fun dismiss() {
        super.dismiss()
        job?.cancel()
        job = null
    }

    companion object {
        const val VISIBILITY_MILLISECONDS = 5000L
    }

}