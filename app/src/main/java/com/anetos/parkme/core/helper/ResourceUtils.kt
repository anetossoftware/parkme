package com.anetos.parkme.core.helper

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.util.TypedValue
import androidx.annotation.*
import androidx.core.content.res.ResourcesCompat
import com.anetos.parkme.R

fun Context.colorStateListResource(@ColorRes id: Int) = ResourcesCompat.getColorStateList(resources, id, null)
fun Context.colorResource(@ColorRes id: Int) = ResourcesCompat.getColor(resources, id, null)
fun Context.stringResource(@StringRes id: Int, vararg formatArgs: Any? = emptyArray()) = getString(id, *formatArgs)
fun Context.drawableResource(@DrawableRes id: Int) = ResourcesCompat.getDrawable(resources, id, theme)
fun Context.dimenResource(@DimenRes id: Int) = resources.getDimension(id)
fun Context.fontResource(@FontRes id: Int) = ResourcesCompat.getFont(this, id)
fun Context.quantityStringResource(@PluralsRes id: Int, quantity: Int, vararg formatArgs: Any?) = resources.getQuantityString(id, quantity, *formatArgs)
fun Context.pluralsResource(@PluralsRes id: Int, quantity: Int, vararg formatArgs: Any?) = resources.getQuantityString(id, quantity, *formatArgs)
fun Context.colorAttributeResource(@AttrRes id: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(id, typedValue, true)
    return typedValue.data
}

fun @receiver:ColorInt Int.toColorStateList() = ColorStateList.valueOf(this)

fun Context.tryLoadingFontResource(@FontRes id: Int) = try {
    fontResource(id)
} catch (exception: Throwable) {
    null
}

fun AppColor.toResource(): Int = when (this) {
    AppColor.Blue -> R.color.colorAccentBlue
    AppColor.Gray -> R.color.colorAccentGray
    AppColor.Pink -> R.color.colorAccentPink
    AppColor.Cyan -> R.color.colorAccentCyan
    AppColor.Purple -> R.color.colorAccentPurple
    AppColor.Red -> R.color.colorAccentRed
    AppColor.Yellow -> R.color.colorAccentYellow
    AppColor.Orange -> R.color.colorAccentOrange
    AppColor.Green -> R.color.colorAccentGreen
    AppColor.Brown -> R.color.colorAccentBrown
    AppColor.BlueGray -> R.color.colorAccentBlueGray
    AppColor.Teal -> R.color.colorAccentTeal
    AppColor.Indigo -> R.color.colorAccentIndigo
    AppColor.DeepPurple -> R.color.colorAccentDeepPurple
    AppColor.DeepOrange -> R.color.colorAccentDeepOrange
    AppColor.DeepGreen -> R.color.colorAccentDeepGreen
    AppColor.LightBlue -> R.color.colorAccentLightBlue
    AppColor.LightGreen -> R.color.colorAccentLightGreen
    AppColor.LightRed -> R.color.colorAccentLightRed
    AppColor.LightPink -> R.color.colorAccentLightPink
    AppColor.Black -> R.color.colorAccentBlack
    AppColor.Error -> R.color.error
    AppColor.Success -> R.color.success
}

val Number.dp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()