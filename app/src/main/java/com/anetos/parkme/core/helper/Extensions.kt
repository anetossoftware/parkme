package com.anetos.parkme.core.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.os.Handler
import android.os.Looper
import android.text.*
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.anetos.parkme.R
import com.anetos.parkme.data.model.Note
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
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
import kotlin.OptIn
import kotlin.math.absoluteValue

fun NavController.navigateSafely(directions: NavDirections, builder: (NavOptionsBuilder.() -> Unit)? = null) {
    if (currentDestination?.getAction(directions.actionId) != null)
        if (builder == null)
            navigate(directions)
        else
            navigate(directions, navOptions(builder))
}

val Fragment.navController: NavController?
    get() = if (isAdded) findNavController() else null

fun NavController.destinationAsFlow() = callbackFlow {
    val listener = NavController.OnDestinationChangedListener { controller, destination, arguments ->
        trySend(destination)
    }
    addOnDestinationChangedListener(listener)
    awaitClose { removeOnDestinationChangedListener(listener) }
}

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

internal fun String.textStyle(context: Context, startLength: String, endLength: String,
                              typeface: Int = Typeface.NORMAL, colorInt: Int = R.color.black): SpannableStringBuilder {
    val str = SpannableStringBuilder(String.format(this).replace(this, this))
    this.let {
        str.setSpan(
            StyleSpan(typeface),
            str.indexOf(startLength),
            str.indexOf(endLength) + endLength.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        str.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, colorInt)),
            str.indexOf(startLength),
            str.indexOf(endLength) + endLength.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    return str
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

fun View.disable(isAnimate: Boolean = true) {
    if (isAnimate) {
        animate()
            .setDuration(DefaultAnimationDuration)
            .alpha(0.5F)
            .withEndAction { isEnabled = false }
    } else {
        alpha = .5f
        isEnabled = false
    }
}

fun View.enable(isAnimate: Boolean = true) {
    if (isAnimate) {
        animate()
            .setDuration(DefaultAnimationDuration)
            .alpha(1F)
            .withEndAction { isEnabled = true }
    } else {
        this.alpha = 1f
        this.isEnabled = true
    }
}

fun View.snackbar(
    @StringRes stringId: Int,
    @DrawableRes drawableId: Int? = null,
    @IdRes anchorViewId: Int? = null,
    color: NoteColor? = null,
    vararg formatArgs: Any? = emptyArray(),
    vibrate: Boolean = false,
) = Snackbar.make(this, context.stringResource(stringId, *formatArgs), Snackbar.LENGTH_SHORT).apply {
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

fun View.performClickHapticFeedback() =
    performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING)

fun View.performLongClickHapticFeedback() =
    performHapticFeedback(HapticFeedbackConstants.LONG_PRESS, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING)


private var toast: Toast? = null
fun Context.showToast(msg: String) {
    toast?.cancel()
    toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
    toast?.show()
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

private val beforeTextChangedStub: (CharSequence, Int, Int, Int) -> Unit = { _, _, _, _ -> }
private val onTextChangedStub: (CharSequence, Int, Int, Int) -> Unit = { _, _, _, _ -> }
private val afterTextChangedStub: (Editable) -> Unit = {}

fun EditText.addChangedListener(
    beforeTextChanged: (CharSequence, Int, Int, Int) -> Unit = beforeTextChangedStub,
    onTextChanged: (CharSequence, Int, Int, Int) -> Unit = onTextChangedStub,
    afterTextChanged: (Editable) -> Unit = afterTextChangedStub
) = addTextChangedListener(object : TextWatcher {
    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
        beforeTextChanged(charSequence, i, i1, i2)
    }

    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
        onTextChanged(charSequence, i, i1, i2)
    }

    override fun afterTextChanged(editable: Editable) {
        afterTextChanged(editable)
    }
})

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

fun vectorToBitmap(
    context: Context,
    @DrawableRes id: Int,
    @ColorInt color: Int? = null,
    @Size size: Int
): BitmapDescriptor {
    val vectorDrawable = ResourcesCompat.getDrawable(context.resources, id, null)!!
    val iconSize = size
    val bitmap = Bitmap.createBitmap(
        iconSize,
        iconSize, Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
    if (!(color == null))
        DrawableCompat.setTint(vectorDrawable, color)
    vectorDrawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

fun String.formatAccountNumber(interval: Int = 4, separator: Char = ' '): String {
    return if (this == null) {
        ""
    } else {
        val stringBuilder = StringBuilder(this)
        for (i in 0 until this.length / interval) {
            stringBuilder.insert((i + 1) * interval + i, separator)
        }
        if (stringBuilder[stringBuilder.length - 1] == separator) {
            stringBuilder.deleteCharAt(stringBuilder.length - 1)
        }
        stringBuilder.toString()
    }
}

fun <R> (() -> R).withDelay(delay: Long = 250L) {
    Looper.myLooper()?.let {
        Handler(it).postDelayed({ this.invoke() }, delay)
    }
}

fun withDelay(delay : Long = 500, block : () -> Unit) {
    Looper.myLooper()?.let {
        Handler(it).postDelayed(Runnable(block), delay)
    }
}

/*private fun (() -> Any).withDelay(delay: Long? = 1000L) {
    Looper.myLooper()?.let {
        Handler(it).postDelayed({ this.invoke() }, delay)
    }
}*/