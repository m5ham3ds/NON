package com.example.widget

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.widget.ProgressBar
import androidx.core.view.ViewCompat

class CustomProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.progressBarStyleHorizontal
) : ProgressBar(context, attrs, defStyleAttr) {

    fun setProgressWithAnimation(targetProgress: Int, durationMs: Long = 300L) {
        if (!ViewCompat.isAttachedToWindow(this)) {
            progress = targetProgress
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setProgress(targetProgress, true)
        } else {
            ObjectAnimator.ofInt(this, "progress", progress, targetProgress)
                .apply { duration = durationMs }
                .start()
        }
    }
}
