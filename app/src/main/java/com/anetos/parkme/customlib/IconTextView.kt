package com.anetos.parkme.customlib

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView

class IconTextView : AppCompatTextView {

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    companion object {
        private const val RADIX_VALUE = 16
    }

    init {
        createView(context)
    }

    private fun createView(context: Context) {
        gravity = Gravity.CENTER
        typeface = Typeface.createFromAsset(context.assets, "fonts/fontawesome.otf")
    }

    fun setIconText(iconText: String) {
        if (iconText.isNotEmpty() && iconText.isNotBlank()) {
            val hexValue = Integer.parseInt(iconText, RADIX_VALUE)
            this.text = String(Character.toChars(hexValue))
        }
    }
}