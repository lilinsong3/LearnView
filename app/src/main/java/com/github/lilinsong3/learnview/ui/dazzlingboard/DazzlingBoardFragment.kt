package com.github.lilinsong3.learnview.ui.dazzlingboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.lilinsong3.learnview.databinding.FragmentDazzlingBoardBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * An example full-screen fragment that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
@AndroidEntryPoint
class DazzlingBoardFragment : Fragment() {

    private var _binding: FragmentDazzlingBoardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: DazzlingBoardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDazzlingBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dazzlingBoardModelFlow.collect {
                    // 预览牌
                    binding.dbTextBoardPreview.text = it.text
                    binding.dbTextBoardPreview.setBackgroundColor(it.backgroundColor)
                    binding.dbTextBoardPreview.setTextColor(it.textColor)
                    binding.dbTextBoardPreview.textSize = it.textSize
                    // TODO: 文本闪烁、滚动，一些大小值限制

                    // 背景颜色 slider
                    binding.dbSliderBg.value = it.backgroundColor.toFloat()
                    // 文本颜色 slider
                    binding.dbSliderSloganColor.value = it.textColor.toFloat()
                    // 文本尺寸 slider
                    binding.dbSliderSloganSize.value = it.textSize
                    // 闪烁 switch
                    binding.dbSwitchFlashing.isChecked = it.flashing
                    // 滚动 switch
                    binding.dbSwitchRolling.isChecked = it.rolling
                }
            }
        }

        // TODO: 事件绑定
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}