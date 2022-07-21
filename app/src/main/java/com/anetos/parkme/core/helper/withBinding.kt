package com.anetos.parkme.core.helper

import android.content.Context
import android.view.View
import androidx.viewbinding.ViewBinding
import com.anetos.parkme.R
import com.anetos.parkme.data.model.Note

val LabelDefaultStrokeWidth = 2.dp
val LabelDefaultCornerRadius = 1000.dp.toFloat()

val Note.wordsCount
    get() = if (noteBody.isBlank()) 0 else noteBody.split("\\s+".toRegex()).size

inline fun <T : ViewBinding> T.withBinding(crossinline block: T.() -> Unit): View {
    block()
    return root
}

@Suppress("DEPRECATION")
fun Note.getTitle(context: Context) = context.stringResource(R.string.reading_mode)

fun Note.format(): String = """
    $noteTitle
    
    $noteBody
""".trimIndent()
    .trim()