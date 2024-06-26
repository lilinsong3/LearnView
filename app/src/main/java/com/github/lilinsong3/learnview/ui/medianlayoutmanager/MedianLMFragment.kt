package com.github.lilinsong3.learnview.ui.medianlayoutmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import com.github.lilinsong3.learnview.common.defaultLaunch
import com.github.lilinsong3.learnview.databinding.FragmentMedianLmBinding

class MedianLMFragment : Fragment() {

    private var _binding: FragmentMedianLmBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MedianLMViewModel by viewModels()
    private lateinit var textAdapter: TextAdapter
    private var currPos = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMedianLmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textAdapter = TextAdapter()
        binding.medianRecycler.adapter = textAdapter
//        binding.medianRecycler.addItemDecoration(MaterialDividerItemDecoration(requireContext(), VERTICAL))
//        binding.medianRecycler.addOnScrollListener()
        binding.medianImgAndroid.setOnClickListener {
            var nextPosition = currPos + 4
            if (nextPosition >= textAdapter.itemCount) nextPosition = 0
            binding.medianRecycler.smoothScrollToPosition(nextPosition)
            currPos = nextPosition
        }
        defaultLaunch {
            viewModel.uiState.collect {
                textAdapter.submitList(it.items) {
                    if (it.highlightPosition != NO_POSITION) {
                        binding.medianRecycler.scrollToPosition(it.highlightPosition)
                    }
                }
            }
        }
        viewModel.roll()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}