package com.anetos.parkme.core.helper

import android.view.animation.AccelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.transition.Visibility
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFade
import com.google.android.material.transition.MaterialSharedAxis

const val DefaultAnimationDuration = 250L
typealias DefaultInterpolator = AccelerateInterpolator


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