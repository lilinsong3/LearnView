package com.github.lilinsong3.learnview.ui.animatetext

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.lilinsong3.learnview.Event
import com.github.lilinsong3.learnview.SharedMainViewModel
import com.github.lilinsong3.learnview.databinding.FragmentAnimateTextBinding
import com.google.android.material.animation.ArgbEvaluatorCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * An example full-screen fragment that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
@AndroidEntryPoint
class AnimateTextFragment : Fragment() {

    private var _binding: FragmentAnimateTextBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: AnimateTextViewModel by viewModels()
    private val sharedMainViewModel: SharedMainViewModel by activityViewModels()

    private val mainLooperHandler: Handler = Handler(Looper.getMainLooper())
    private var dismissPlayControlBtn: Runnable? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnimateTextBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val screenOrientation = resources.configuration.orientation

        val animatorBoardBackgroundColor = ObjectAnimator.ofArgb(
            binding.atLayoutBoardPreview,
            "backgroundColor",
            Color.BLACK,
            Color.WHITE
        ).apply {
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
        }

        val animatorSloganColor = ObjectAnimator.ofArgb(
            binding.atTextSloganPreview,
            "textColor",
            Color.WHITE,
            Color.BLACK
        ).apply {
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
        }

        val animatorFlashing = AnimatorSet().apply {
            playTogether(animatorBoardBackgroundColor, animatorSloganColor)
            duration = 3000L
        }
        val animatorRolling = ObjectAnimator.ofFloat(
            binding.atTextSloganPreview,
            "x",
            0f,
            0f
        ).apply {
            duration = 2000L
            interpolator = LinearInterpolator()
            repeatCount = ValueAnimator.INFINITE
        }
        // 数据绑定
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.animateTextModelFlow.collect {
                    // 预览牌
                    if (binding.atTextSloganPreview.text != it.text) {
                        binding.atTextSloganPreview.text = it.text
                    }
                    if (binding.atInputSlogan.editText?.text.toString() != it.text) {
                        binding.atInputSlogan.editText?.setText(it.text)
                    }
                    binding.atLayoutBoardPreview.setBackgroundColor(it.backgroundColor)
                    binding.atTextSloganPreview.setTextColor(it.textColor)
                    binding.atTextSloganPreview.textSize = it.textSize

                    // 竖屏则设置数据显示
                    if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {
                        // 背景颜色 slider
                        binding.atSliderBg.value = it.backgroundColor.toFloat()
                        // 文本颜色 slider
                        binding.atSliderSloganColor.value = it.textColor.toFloat()
                        // 文本尺寸 slider
                        binding.atSliderSloganSize.value = it.textSize
                        // 闪烁 switch
                        binding.atSwitchFlashing.isChecked = it.flashing
                        // 滚动 switch
                        binding.atSwitchRolling.isChecked = it.rolling
                    }

                    // 动画
                    if (it.flashing) {
                        // 背景色动画和文字颜色动画
                        animatorBoardBackgroundColor.apply {
                            // 设置 Int 类型值
                            setIntValues(it.backgroundColor, it.textColor)
                            // 使用颜色估值器
                            setEvaluator(ArgbEvaluatorCompat.getInstance())
                        }
                        animatorSloganColor.apply {
                            setIntValues(it.textColor, it.backgroundColor)
                            setEvaluator(ArgbEvaluatorCompat.getInstance())
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
                        // 使用post，等待width测量确定后，再开始动画
                        binding.atTextSloganPreview.post {
                            animatorRolling.apply {
                                setFloatValues(
                                    binding.atLayoutBoardPreview.width.toFloat(),
                                    -binding.atTextSloganPreview.width.toFloat()
                                )
                                if (isStarted) {
                                    resume()
                                } else {
                                    start()
                                }
                            }
                        }
                    } else {
                        animatorRolling.pause()
                    }
                }
            }
        }

        if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {
            sharedMainViewModel.send(Event.AppBarVisibilityEvent(true))
            bindControlEvent()
        }

        // 横屏
        if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 隐藏AppBar
            sharedMainViewModel.send(Event.AppBarVisibilityEvent(false))

            requireActivity().apply {
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    hide(WindowInsetsCompat.Type.statusBars())
                    hide(WindowInsetsCompat.Type.navigationBars())
                }
            }

            // 控制播放按钮显示
            binding.root.setOnClickListener {
                if (binding.atBtnPlayControl.visibility == View.GONE) {
                    binding.atBtnPlayControl.visibility = View.VISIBLE
                    // 延时隐藏按钮
                    dismissPlayControlBtn = Runnable { binding.atBtnPlayControl.visibility = View.GONE }
                    mainLooperHandler.postDelayed(dismissPlayControlBtn!!, 2000L)
                    return@setOnClickListener
                }
                // 如果是显示的，就立即隐藏
                if (binding.atBtnPlayControl.visibility == View.VISIBLE) {
                    cancelDismissPlayControlBtn()
                    binding.atBtnPlayControl.visibility = View.GONE
                }
            }
        }

        binding.atBtnPlayControl.setOnClickListener {
            if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {
                requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                return@setOnClickListener
            }
            if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }

    }

    private fun cancelDismissPlayControlBtn() {
        if (dismissPlayControlBtn != null) {
            mainLooperHandler.removeCallbacks(dismissPlayControlBtn!!)
        }
    }

    private fun bindControlEvent() {
        binding.atSliderBg.addOnChangeListener { _, value, fromUser ->
            if (fromUser) viewModel.slideBackgroundColor(
                value.toInt()
            )
        }
        // 事件绑定
        binding.atInputSlogan.editText?.doOnTextChanged { text, _, _, _ ->
            viewModel.inputText(
                (text ?: "").toString()
            )
        }
        binding.atSliderSloganColor.addOnChangeListener { _, value, fromUser ->
            if (fromUser) viewModel.slideTextColor(
                value.toInt()
            )
        }
        binding.atSliderSloganSize.addOnChangeListener { _, value, fromUser ->
            if (fromUser) viewModel.slideTextSize(
                value
            )
        }
        binding.atSwitchFlashing.setOnCheckedChangeListener { _, isChecked ->
            viewModel.switchFlashing(
                isChecked
            )
        }
        binding.atSwitchRolling.setOnCheckedChangeListener { _, isChecked ->
            viewModel.switchRolling(
                isChecked
            )
        }

        // 重置
        binding.atBtnReset.setOnClickListener { viewModel.reset() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 恢复AppBar显示
        cancelDismissPlayControlBtn()
        _binding = null
    }


}