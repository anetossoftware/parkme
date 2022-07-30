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
import com.anetos.parkme.core.helper.setRoundedCorners
import com.anetos.parkme.databinding.FragmentHomeBinding

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
        //startShimmering()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //binding.bottomAppBar.setRoundedCorners()
        context?.let { context ->
            val backgroundColor = context.colorAttributeResource(R.attr.noteBackgroundColor)
            binding.bottomAppBar.navigationIcon?.mutate()?.setTint(backgroundColor)
            binding.bottomAppBar.menu.forEach { it.icon?.mutate()?.setTint(backgroundColor) }
        }
    }

    companion object {
        fun newInstance(ctx: Context) = HomeFragment()
    }
}