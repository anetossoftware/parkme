package com.anetos.parkme.core

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.anetos.parkme.R
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment : Fragment() {

    fun showSnackBar(container: View, message: String, buttonText: String) {
        val snackbar = Snackbar.make(container, message, BaseTransientBottomBar.LENGTH_INDEFINITE)
        snackbar.setAction(buttonText) { view -> snackbar.dismiss() }
        snackbar.setActionTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
        snackbar.show()
    }


    private val mHideKeyBoard = {
        val view = activity?.currentFocus
        if (view != null) {
            val inputManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val view = activity?.currentFocus
        if (view != null) {
            val inputManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }
}