package com.github.lilinsong3.learnview.ui.gridtext

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.lilinsong3.learnview.databinding.FragmentGridTextBinding

class GridTextFragment : Fragment() {

    private var _dataBinding: FragmentGridTextBinding? = null
    private val dataBinding: FragmentGridTextBinding get() = _dataBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _dataBinding = FragmentGridTextBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _dataBinding = null
    }
}