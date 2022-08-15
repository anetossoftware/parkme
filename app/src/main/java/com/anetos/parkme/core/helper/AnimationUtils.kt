package com.anetos.parkme.core.helper

import android.animation.ArgbEvaluator
import android.animation.IntEvaluator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.annotation.AnimRes
import androidx.fragment.app.Fragment
import androidx.transition.Visibility
import com.anetos.parkme.R
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFade
import com.google.android.material.transition.MaterialSharedAxis
import jp.wasabeef.recyclerview.animators.ScaleInAnimator
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator

const val DefaultAnimationDuration = 250L
typealias DefaultInterpolator = AccelerateInterpolator

@Suppress("FunctionName")
fun VerticalListItemAnimator() = SlideInUpAnimator(DefaultInterpolator()).apply {
    addDuration = DefaultAnimationDuration
    changeDuration = DefaultAnimationDuration
    moveDuration = DefaultAnimationDuration
    removeDuration = DefaultAnimationDuration
}

@Suppress("FunctionName")
fun HorizontalListItemAnimator() = ScaleInAnimator(DefaultInterpolator()).apply {
    addDuration = DefaultAnimationDuration
    changeDuration = DefaultAnimationDuration
    moveDuration = DefaultAnimationDuration
    removeDuration = DefaultAnimationDuration
}

fun View.animateBackgroundColor(fromColor: Int, toColor: Int): ValueAnimator? {
    return ValueAnimator.ofArgb(fromColor, toColor)
        .apply {
            duration = DefaultAnimationDuration
            addUpdateListener { animator ->
                val value = animator.animatedValue as Int
                background?.mutate()
                    ?.also { it.setRippleColor(fromColor.toColorStateList()) }
                    ?.setTint(value)
            }
            start()
        }
}

fun View.animateLabelColors(fromColor: Int, toColor: Int) {
    ValueAnimator.ofPropertyValuesHolder(
        PropertyValuesHolder.ofObject("background", ArgbEvaluator(), fromColor, toColor),
        PropertyValuesHolder.ofObject("stroke", IntEvaluator(), 0.dp, LabelDefaultStrokeWidth),
    ).apply {
        duration = DefaultAnimationDuration
        addUpdateListener { animator ->
            val backgroundColor = animator.getAnimatedValue("background") as Int
            val strokeWidth = animator.getAnimatedValue("stroke") as Int
            background = context.drawableResource(R.drawable.label_item_shape)
                ?.mutate()
                ?.let { it as RippleDrawable }
                ?.let { it.getDrawable(0) as GradientDrawable }
                ?.apply {
                    setStroke(strokeWidth, fromColor)
                    cornerRadius = LabelDefaultCornerRadius
                    setColor(backgroundColor)
                }
                ?.toRippleDrawable(context)
                ?.also { it.setRippleColor(fromColor.toColorStateList()) }
        }
        start()
    }
}

fun TextView.animateTextColor(fromColor: Int, toColor: Int): ValueAnimator? {
    return ValueAnimator.ofArgb(fromColor, toColor)
        .apply {
            duration = DefaultAnimationDuration
            addUpdateListener { animator ->
                val value = animator.animatedValue as Int
                setTextColor(value)
            }
            start()
        }
}

private fun Visibility.applyDefaultConfig() = apply {
    duration = DefaultAnimationDuration
    interpolator = DefaultInterpolator()
}

fun Fragment.setupMixedTransitions() {
    exitTransition = MaterialElevationScale(false).applyDefaultConfig()
    reenterTransition = MaterialElevationScale(true).applyDefaultConfig()
    enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true).applyDefaultConfig()
    returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false).applyDefaultConfig()
}

fun Fragment.setupFadeTransition() {
    exitTransition = MaterialFade().applyDefaultConfig()
    enterTransition = MaterialFade().applyDefaultConfig()
    reenterTransition = MaterialFade().applyDefaultConfig()
    returnTransition = MaterialFade().applyDefaultConfig()
}

fun animate(viewToAnimate: View, duration: Long, @AnimRes animResource: Int, listener: Animation.AnimationListener? = null) {
    val animation = AnimationUtils.loadAnimation(viewToAnimate.context, animResource)
    animation.duration = duration
    animation.setAnimationListener(listener)
    viewToAnimate.startAnimation(animation)
}

fun getValueAnimator(fromWidth: Int, toWidth: Int, updateListener: ValueAnimator.AnimatorUpdateListener
): ValueAnimator {
    val widthAnimation = ValueAnimator.ofInt(fromWidth, toWidth)
    widthAnimation.interpolator = DecelerateInterpolator()
    widthAnimation.addUpdateListener(updateListener)
    return widthAnimation
}

fun colorAnimator(
    startColor: Int, endColor: Int, updateListener: ValueAnimator.AnimatorUpdateListener
): ValueAnimator {
    val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), startColor, endColor)
    colorAnimation.addUpdateListener(updateListener)
    return colorAnimation
}