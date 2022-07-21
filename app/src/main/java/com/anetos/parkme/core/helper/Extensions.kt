package com.anetos.parkme.core.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import android.view.GestureDetector
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.anetos.parkme.R
import com.anetos.parkme.data.model.Note
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.RelativeCornerSize
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import kotlin.math.absoluteValue

internal fun String.textStyle(context: Context, typeface: Int = Typeface.NORMAL, colorInt: Int = R.color.black): SpannableStringBuilder {
    val str = SpannableStringBuilder(String.format(this).replace(this, this))
    this.let {
        str.setSpan(
            StyleSpan(typeface),
            str.indexOf(this),
            str.indexOf(this) + this.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        str.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, colorInt)),
            str.indexOf(this),
            str.indexOf(this) + this.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    return str;
}

fun View.setFullSpan() {
    if (layoutParams != null && layoutParams is StaggeredGridLayoutManager.LayoutParams)
        (layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = true
}

fun GradientDrawable.toRippleDrawable(context: Context): RippleDrawable {
    val colorStateList = context.colorAttributeResource(R.attr.noteSecondaryColor).toColorStateList()
    return RippleDrawable(colorStateList, this, this)
}

fun Drawable.setRippleColor(colorStateList: ColorStateList) {
    val rippleDrawable = mutate() as RippleDrawable
    rippleDrawable.setColor(colorStateList.withAlpha(32))
}

fun Activity.showKeyboard(view: View) = WindowInsetsControllerCompat(window, view).show(
    WindowInsetsCompat.Type.ime())
fun Activity.hideKeyboard(view: View) = WindowInsetsControllerCompat(window, view).hide(
    WindowInsetsCompat.Type.ime())
fun View.showKeyboardUsingImm() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun @receiver:ColorInt Int.withDefaultAlpha(alpha: Int = 32): Int = ColorUtils.setAlphaComponent(this, alpha)

fun TextView.setBoldFont(font: Font) {
    when (font) {
        Font.Nunito -> context.tryLoadingFontResource(R.font.nunito_bold)?.let { typeface = it }
        Font.Monospace -> setTypeface(Typeface.MONOSPACE, Typeface.BOLD)
    }
}

fun TextView.setSemiboldFont(font: Font) {
    when (font) {
        Font.Nunito -> context.tryLoadingFontResource(R.font.nunito_semibold)?.let { typeface = it }
        Font.Monospace -> typeface = Typeface.MONOSPACE
    }
}

fun RecyclerView.resetAdapter() {
    adapter = adapter
}

internal fun View?.show() {
    this?.visibility = View.VISIBLE
}

internal fun View?.hide() {
    this?.visibility = View.GONE
}

internal fun View?.invisible() {
    this?.visibility = View.INVISIBLE
}

internal fun View.visibleOrGone(setVisible: Boolean) {
    if (setVisible) show() else hide()
}

fun View.snackbar(
    message: String,
    note: Note? = null,
) = Snackbar.make(this, message, Snackbar.LENGTH_SHORT).apply {
    if (note == null) {
        setBackgroundTint(context.colorAttributeResource(R.attr.notePrimaryColor))
        setTextColor(context.colorAttributeResource(R.attr.noteBackgroundColor))
    } else {
        setBackgroundTint(context.colorResource(note.noteColor.toResource()))
        setTextColor(context.colorAttributeResource(R.attr.noteBackgroundColor))
    }
    val params = view.layoutParams as? CoordinatorLayout.LayoutParams
    params?.let {
        it.gravity = Gravity.TOP
        view.layoutParams = it
    }
    show()
}

@OptIn(ExperimentalCoroutinesApi::class)
fun EditText.textAsFlow(emitNewTextOnly: Boolean = false): Flow<CharSequence?> {
    return callbackFlow {
        val listener = doOnTextChanged { text, start, before, count ->
//            if (emitNewTextOnly) {
//                if (before <= count)
//                    trySend(text)
//            } else {
            trySend(text)
//            }
        }
        addTextChangedListener(listener)
        awaitClose { removeTextChangedListener(listener) }
    }.onStart { emit(text) }
}

fun TextView.removeLinksUnderline() {
    val spannable = SpannableString(text)
    for (urlSpan in spannable.getSpans(0, spannable.length, URLSpan::class.java)) {
        spannable.setSpan(
            object : URLSpan(urlSpan.url) {
                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                }
            },
            spannable.getSpanStart(urlSpan),
            spannable.getSpanEnd(urlSpan),
            0,
        )
    }
    text = spannable
}

fun BottomAppBar.setRoundedCorners() {
    val babBackgroundDrawable = background as MaterialShapeDrawable
    babBackgroundDrawable.shapeAppearanceModel = babBackgroundDrawable.shapeAppearanceModel
        .toBuilder()
        .setAllCorners(RoundedCornerTreatment())
        .setAllCornerSizes(RelativeCornerSize(0.5F))
        .build()
}

const val SwipeGestureThreshold = 100F
@SuppressLint("ClickableViewAccessibility")
inline fun BottomAppBar.setOnSwipeGestureListener(crossinline callback: () -> Unit) {
    val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            val diffY = (e2?.y ?: 0F) - (e1?.y ?: 0F)
            return if (diffY.absoluteValue > SwipeGestureThreshold) {
                callback()
                true
            } else {
                false
            }
        }
    }

    val gestureDetector = GestureDetector(context, gestureListener)

    setOnTouchListener { _, event ->
        gestureDetector.onTouchEvent(event)
        false
    }
}