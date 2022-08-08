package com.anetos.parkme.view.widget.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import com.anetos.parkme.R
import com.anetos.parkme.core.BaseFragment
import com.anetos.parkme.core.helper.colorAttributeResource
import com.anetos.parkme.core.helper.setOnSwipeGestureListener
import com.anetos.parkme.core.helper.setRoundedCorners
import com.anetos.parkme.core.helper.setupMixedTransitions
import com.anetos.parkme.databinding.FragmentHomeBinding
import com.anetos.parkme.view.widget.common.WorkInProgressBottomSheetDialog
import com.anetos.parkme.view.widget.profile.ProfileDialogFragment

class HomeFragment : BaseFragment() {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        setupMixedTransitions()
        setupState()
        setupListeners()
        return binding.root
    }

    private fun setupListeners() {
        binding.bottomAppBar.setOnSwipeGestureListener {

        }
        binding.bottomAppBar.setNavigationOnClickListener {
            WorkInProgressBottomSheetDialog().show(
                requireActivity().supportFragmentManager,
                null
            )
        }

        binding.bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.profile -> {
                    //setupFadeTransition()
                    showProfile()
                    true
                }
                R.id.more -> {
                    WorkInProgressBottomSheetDialog().show(
                        requireActivity().supportFragmentManager,
                        null
                    )
                    true
                }
                else -> false
            }
        }
    }

    private fun setupState() {
        binding.bottomAppBar.setRoundedCorners()
        activity?.let { context ->
            val backgroundColor = context.colorAttributeResource(R.attr.noteBackgroundColor)
            binding.bottomAppBar.navigationIcon?.mutate()?.setTint(backgroundColor)
            binding.bottomAppBar.menu.forEach { it.icon?.mutate()?.setTint(backgroundColor) }
        }
    }

    fun showProfile() {
        activity?.supportFragmentManager?.let {
            ProfileDialogFragment().show(it, null)
        }
    }

    companion object {
        fun newInstance(ctx: Context) = HomeFragment()
    }
}