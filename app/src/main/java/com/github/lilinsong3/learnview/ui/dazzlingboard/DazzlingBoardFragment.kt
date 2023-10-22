package com.github.lilinsong3.learnview.ui.dazzlingboard

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.collection.ArraySet
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
        // 数据绑定
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dazzlingBoardModelFlow.collect {
                    // 预览牌
                    binding.dbTextSloganPreview.text = it.text
                    binding.dbLayoutBoardPreview.setBackgroundColor(it.backgroundColor)
                    binding.dbTextSloganPreview.setTextColor(it.textColor)
                    binding.dbTextSloganPreview.textSize = it.textSize
                    // TODO: 一些大小值限制

                    val playSet = ArraySet<Animator>(3)

                    if (it.flashing) {
                        // 背景色动画和文字颜色动画
                        playSet.apply {
                            add(ObjectAnimator.ofArgb(
                                binding.dbLayoutBoardPreview,
                                "backgroundColor",
                                it.backgroundColor,
                                it.textColor
                            ).apply {
                                repeatCount = ValueAnimator.INFINITE
                            })
                            add(ObjectAnimator.ofArgb(
                                binding.dbTextSloganPreview,
                                "textColor",
                                it.textColor,
                                it.backgroundColor
                            ).apply {
                                repeatCount = ValueAnimator.INFINITE
                            })
                        }
                    }

                    if (it.rolling) {
                        // 平移滚动动画
                        playSet.add(ObjectAnimator.ofFloat(
                            binding.dbTextSloganPreview,
                            "translationX",
                            binding.dbTextSloganPreview.left.toFloat(),
                            - binding.dbTextSloganPreview.width.toFloat(),
                            binding.dbLayoutBoardPreview.width.toFloat(),
                            binding.dbTextSloganPreview.left.toFloat()
                        ).apply {
                            repeatCount = ValueAnimator.INFINITE
                        })
                    }

                    AnimatorSet().apply {
                        playTogether(playSet)
                        start()
                    }

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

        // TODO: 颜色值转换
        // 事件绑定
        binding.dbInputSlogan.editText?.doOnTextChanged { text, _, _, _ -> viewModel.inputText((text ?: "") as String) }
        binding.dbSliderBg.addOnChangeListener { _, value, _ -> viewModel.slideBackgroundColor(value.toInt()) }
        binding.dbSliderSloganColor.addOnChangeListener { _, value, _ -> viewModel.slideTextColor(value.toInt()) }
        binding.dbSliderSloganSize.addOnChangeListener { _, value, _ -> viewModel.slideTextSize(value) }
        binding.dbSwitchFlashing.setOnCheckedChangeListener { _, isChecked -> viewModel.switchFlashing(isChecked) }
        binding.dbSwitchRolling.setOnCheckedChangeListener { _, isChecked -> viewModel.switchRolling(isChecked) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}