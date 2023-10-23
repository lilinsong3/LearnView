package com.github.lilinsong3.learnview.ext

import com.google.android.material.slider.Slider
import com.google.android.material.slider.Slider.OnChangeListener

public inline fun Slider.doOnValueDebounceChange (
    debounceTime: Long = 100L,
    crossinline action: (
        slider: Slider,
        value: Float,
        fromUser: Boolean
    ) -> Unit
) {
    val onChangeListener = object : OnChangeListener {
        private var lastTime = 0L
        override fun onValueChange(slider: Slider, value: Float, fromUser: Boolean) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastTime > debounceTime) {
                lastTime = currentTime
                action.invoke(slider, value, fromUser)
            }
        }
    }
    addOnChangeListener(onChangeListener)
}