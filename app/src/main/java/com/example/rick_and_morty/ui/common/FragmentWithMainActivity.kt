package com.example.rick_and_morty.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.rick_and_morty.ui.MainActivity

abstract class FragmentWithMainActivity(layout: Int) : Fragment(layout) {
    protected lateinit var mainActivity: MainActivity
    protected lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = requireActivity() as MainActivity
        viewModel = mainActivity.mainViewModel
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}