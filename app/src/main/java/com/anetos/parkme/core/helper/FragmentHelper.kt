package com.anetos.parkme.core.helper

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.inputmethod.InputMethodManager
import android.app.Activity
import android.content.Intent
import android.view.View
import com.anetos.parkme.R
import com.anetos.parkme.domain.model.Note


inline fun FragmentManager.transaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commit()
}

fun AppCompatActivity.add(fragment: Fragment, container: Int) {
    supportFragmentManager.transaction { add(container, fragment) }
}

fun FragmentManager.add(fragment: Fragment, container: Int) {
    this.transaction { add(container, fragment) }
}

fun AppCompatActivity.replace(fragment: Fragment, container: Int) {
    supportFragmentManager.transaction { replace(container, fragment) }
}

/**
 * Replace a fragment into [containerId] for given [tag]
 */
fun AppCompatActivity.replaceFragmentWithTag(fragment: Fragment, containerId: Int, tag: String?) {
    supportFragmentManager.beginTransaction()
        .replace(containerId, fragment, tag)
        .apply {
            tag?.let {
                addToBackStack(it)
            }
        }
        .commit()
}

fun Fragment.launchShareNoteIntent(note: Note) {
    val intent = note.createShareIntent()
    val chooser = Intent.createChooser(intent, context?.stringResource(R.string.share_app))
    startActivity(chooser)
}

private fun Note.createShareIntent() = Intent(Intent.ACTION_SEND).apply {
    type = "text/plain"
    putExtra(Intent.EXTRA_TEXT, format())
}

fun hideKeyboard(activity: Activity, view: View) {
    val imm = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}