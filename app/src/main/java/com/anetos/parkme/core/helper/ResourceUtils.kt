package com.anetos.parkme.core.helper

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import com.anetos.parkme.R

fun Context.colorResource(@ColorRes id: Int) = ResourcesCompat.getColor(resources, id, null)
fun Context.stringResource(@StringRes id: Int, vararg formatArgs: Any? = emptyArray()) =
    getString(id, *formatArgs)

fun Context.dimenResource(@DimenRes id: Int) = resources.getDimension(id)
fun Context.colorAttributeResource(@AttrRes id: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(id, typedValue, true)
    return typedValue.data
}

fun NoteColor.toResource(): Int = when (this) {
    NoteColor.Blue -> R.color.colorAccentBlue
    NoteColor.Gray -> R.color.colorAccentGray
    NoteColor.Pink -> R.color.colorAccentPink
    NoteColor.Cyan -> R.color.colorAccentCyan
    NoteColor.Purple -> R.color.colorAccentPurple
    NoteColor.Red -> R.color.colorAccentRed
    NoteColor.Yellow -> R.color.colorAccentYellow
    NoteColor.Orange -> R.color.colorAccentOrange
    NoteColor.Green -> R.color.colorAccentGreen
    NoteColor.Brown -> R.color.colorAccentBrown
    NoteColor.BlueGray -> R.color.colorAccentBlueGray
    NoteColor.Teal -> R.color.colorAccentTeal
    NoteColor.Indigo -> R.color.colorAccentIndigo
    NoteColor.DeepPurple -> R.color.colorAccentDeepPurple
    NoteColor.DeepOrange -> R.color.colorAccentDeepOrange
    NoteColor.DeepGreen -> R.color.colorAccentDeepGreen
    NoteColor.LightBlue -> R.color.colorAccentLightBlue
    NoteColor.LightGreen -> R.color.colorAccentLightGreen
    NoteColor.LightRed -> R.color.colorAccentLightRed
    NoteColor.LightPink -> R.color.colorAccentLightPink
    NoteColor.Black -> R.color.colorAccentBlack
    NoteColor.Error -> R.color.error
    NoteColor.Success -> R.color.success
}

val Number.dp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()