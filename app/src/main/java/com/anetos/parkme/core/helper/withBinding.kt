package com.anetos.parkme.core.helper

import android.view.View
import androidx.viewbinding.ViewBinding

inline fun <T : ViewBinding> T.withBinding(crossinline block: T.() -> Unit): View {
    block()
    return root
}

