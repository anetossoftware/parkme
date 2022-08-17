package com.anetos.parkme.core.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.SpannableString
import android.text.TextPaint
import android.text.TextWatcher
import android.text.style.URLSpan
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.anetos.parkme.R
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.RelativeCornerSize
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.android.material.snackbar.Snackbar
import kotlin.math.absoluteValue

fun NavController.navigateSafely(
    directions: NavDirections,
    builder: (NavOptionsBuilder.() -> Unit)? = null
) {
    if (currentDestination?.getAction(directions.actionId) != null)
        if (builder == null)
            navigate(directions)
        else
            navigate(directions, navOptions(builder))
}

val Fragment.navController: NavController?
    get() = if (isAdded) findNavController() else null

fun Activity.showKeyboard(view: View) = WindowInsetsControllerCompat(window, view).show(
    WindowInsetsCompat.Type.ime()
)

fun Activity.hideKeyboard(view: View) = WindowInsetsControllerCompat(window, view).hide(
    WindowInsetsCompat.Type.ime()
)

fun View.showKeyboardUsingImm() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

internal fun View?.invisible() {
    this?.visibility = View.INVISIBLE
}

fun View.disableLook() {
    this.alpha = .5f
    this.isEnabled = false
}

fun View.enableLook() {
    this.alpha = 1f
    this.isEnabled = true
}

internal fun View.visible() {
    animate()
        .setDuration(DefaultAnimationDuration)
        //.translationY(this.height.toFloat())
        .alpha(1.0f)
        .withEndAction {
            this.visibility = View.VISIBLE
        }
}

internal fun View.gone() {
    animate()
        .setDuration(DefaultAnimationDuration)
        .translationY(0f)
        .alpha(0.0f)
        .withStartAction {
            visibility = View.GONE
        }
}

fun View.snackbar(
    @StringRes stringId: Int,
    @DrawableRes drawableId: Int? = null,
    @IdRes anchorViewId: Int? = null,
    color: NoteColor? = null,
    vararg formatArgs: Any? = emptyArray(),
    vibrate: Boolean = false,
) = Snackbar.make(this, context.stringResource(stringId, *formatArgs), Snackbar.LENGTH_SHORT)
    .apply {
        animationMode = Snackbar.ANIMATION_MODE_SLIDE
        val textView = view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        if (anchorViewId != null) setAnchorView(anchorViewId)
        if (drawableId != null) {
            textView?.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableId, 0, 0, 0)
            textView?.compoundDrawablePadding =
                context.dimenResource(R.dimen.spacing_normal).toInt()
            textView?.gravity = Gravity.CENTER
        }
        if (color != null) {
            val backgroundColor = context.colorResource(color.toResource())
            val contentColor = context.colorAttributeResource(R.attr.noteBackgroundColor)
            setBackgroundTint(backgroundColor)
            setTextColor(contentColor)
            textView?.compoundDrawablesRelative?.get(0)?.mutate()?.setTint(contentColor)
        } else {
            val backgroundColor = context.colorAttributeResource(R.attr.notePrimaryColor)
            val contentColor = context.colorAttributeResource(R.attr.noteBackgroundColor)
            setBackgroundTint(backgroundColor)
            setTextColor(contentColor)
            textView?.compoundDrawablesRelative?.get(0)?.mutate()?.setTint(contentColor)
        }
        if (vibrate) performClickHapticFeedback()
        show()
    }

fun View.snackbar(
    string: String,
    @DrawableRes drawableId: Int? = null,
    @IdRes anchorViewId: Int? = null,
    color: NoteColor? = null,
    vararg formatArgs: Any? = emptyArray(),
    vibrate: Boolean = false,
) = Snackbar.make(this, string, Snackbar.LENGTH_SHORT).apply {
    animationMode = Snackbar.ANIMATION_MODE_SLIDE
    val textView = view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
    if (anchorViewId != null) setAnchorView(anchorViewId)
    if (drawableId != null) {
        textView?.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableId, 0, 0, 0)
        textView?.compoundDrawablePadding = context.dimenResource(R.dimen.spacing_normal).toInt()
        textView?.gravity = Gravity.CENTER
    }
    if (color != null) {
        val backgroundColor = context.colorResource(color.toResource())
        val contentColor = context.colorAttributeResource(R.attr.noteBackgroundColor)
        setBackgroundTint(backgroundColor)
        setTextColor(contentColor)
        textView?.compoundDrawablesRelative?.get(0)?.mutate()?.setTint(contentColor)
    } else {
        val backgroundColor = context.colorAttributeResource(R.attr.notePrimaryColor)
        val contentColor = context.colorAttributeResource(R.attr.noteBackgroundColor)
        setBackgroundTint(backgroundColor)
        setTextColor(contentColor)
        textView?.compoundDrawablesRelative?.get(0)?.mutate()?.setTint(contentColor)
    }
    if (vibrate) performClickHapticFeedback()
    show()
}

fun View.performClickHapticFeedback() =
    performHapticFeedback(
        HapticFeedbackConstants.VIRTUAL_KEY,
        HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
    )

fun View.performLongClickHapticFeedback() =
    performHapticFeedback(
        HapticFeedbackConstants.LONG_PRESS,
        HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
    )

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
    })
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
        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
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

fun vectorToBitmap(
    context: Context,
    @DrawableRes id: Int,
    @ColorInt color: Int? = null
): BitmapDescriptor {
    val vectorDrawable = ResourcesCompat.getDrawable(context.resources, id, null)!!
    val iconSize = context.resources.getInteger(R.integer.map_view_icon_size)
    val bitmap = Bitmap.createBitmap(
        iconSize,
        iconSize, Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
    color?.let { DrawableCompat.setTint(vectorDrawable, it) }
    vectorDrawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

fun <R> (() -> R).withDelay(delay: Long = 250L) {
    Looper.myLooper()?.let {
        Handler(it).postDelayed({ this.invoke() }, delay)
    }
}

fun withDelay(delay: Long = 500, block: () -> Unit) {
    Looper.myLooper()?.let {
        Handler(it).postDelayed(Runnable(block), delay)
    }
}