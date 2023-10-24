package com.github.lilinsong3.learnview.ui.dazzlingboard

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.core.widget.doOnTextChanged
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
        val animatorBoardBackgroundColor = ObjectAnimator.ofArgb(
            binding.dbLayoutBoardPreview,
            "backgroundColor",
            Color.BLACK,
            Color.WHITE
        ).apply {
            repeatCount = ValueAnimator.INFINITE
            duration = 2000L
        }

        val animatorSloganColor = ObjectAnimator.ofArgb(
            binding.dbTextSloganPreview,
            "textColor",
            Color.WHITE,
            Color.BLACK
        ).apply {
            repeatCount = ValueAnimator.INFINITE
            duration = 2000L
        }

        val animatorFlashing = AnimatorSet().apply {
            playTogether(animatorBoardBackgroundColor, animatorSloganColor)
        }
        val animatorRolling = ObjectAnimator.ofFloat(
            binding.dbTextSloganPreview,
            "x",
            binding.dbLayoutBoardPreview.width.toFloat(),
            -binding.dbTextSloganPreview.width.toFloat()
        ).apply {
            duration = 2000L
            interpolator = LinearInterpolator()
            repeatCount = ValueAnimator.INFINITE
        }
        // 数据绑定
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dazzlingBoardModelFlow.collect {
                    // 预览牌
                    if (binding.dbTextSloganPreview.text != it.text) {
                        binding.dbTextSloganPreview.text = it.text
                    }
                    binding.dbLayoutBoardPreview.setBackgroundColor(it.backgroundColor)
                    binding.dbTextSloganPreview.setTextColor(it.textColor)
                    binding.dbTextSloganPreview.textSize = it.textSize
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

                    // 动画
                    if (it.flashing) {
                        // 背景色动画和文字颜色动画
                        animatorBoardBackgroundColor.apply {
                            setIntValues(it.backgroundColor, it.textColor)
                        }
                        animatorSloganColor.apply {
                            setIntValues(it.textColor, it.backgroundColor)
                        }
                        if (animatorFlashing.isStarted) {
                            animatorFlashing.resume()
                        } else {
                            animatorFlashing.start()
                        }
                    } else {
                        animatorFlashing.pause()
                    }

                    if (it.rolling) {
                        // 平移滚动动画
                        animatorRolling.apply {
                            setFloatValues(
                                binding.dbLayoutBoardPreview.width.toFloat(),
                                -binding.dbTextSloganPreview.width.toFloat()
                            )
                            if (isStarted) {
                                resume()
                            } else {
                                start()
                            }
                        }
                    } else {
                        animatorRolling.pause()
                    }
                }
            }
        }

        // 事件绑定
        binding.dbInputSlogan.editText?.doOnTextChanged { text, _, _, _ ->
            viewModel.inputText(
                (text ?: "") as String
            )
        }
        binding.dbSliderBg.addOnChangeListener { _, value, fromUser ->
            if (fromUser) viewModel.slideBackgroundColor(
                value.toInt()
            )
        }
        binding.dbSliderSloganColor.addOnChangeListener { _, value, fromUser ->
            if (fromUser) viewModel.slideTextColor(
                value.toInt()
            )
        }
        binding.dbSliderSloganSize.addOnChangeListener { _, value, fromUser ->
            if (fromUser) viewModel.slideTextSize(
                value
            )
        }
        binding.dbSwitchFlashing.setOnCheckedChangeListener { _, isChecked ->
            viewModel.switchFlashing(
                isChecked
            )
        }
        binding.dbSwitchRolling.setOnCheckedChangeListener { _, isChecked ->
            viewModel.switchRolling(
                isChecked
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}