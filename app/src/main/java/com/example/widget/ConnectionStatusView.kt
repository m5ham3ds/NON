package com.example.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.R
import com.google.android.material.color.MaterialColors

class ConnectionStatusView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val tvStatus: TextView
    private val ivIcon: ImageView

    init {
        LayoutInflater.from(context).inflate(R.layout.view_connection_status, this, true)
        tvStatus = findViewById(R.id.tv_connection_status)
        ivIcon = findViewById(R.id.iv_connection_icon)
    }

    fun setConnected(connected: Boolean) {
        val colorRes = if (connected) {
            com.google.android.material.R.attr.colorPrimary
        } else {
            com.google.android.material.R.attr.colorError
        }
        val color = MaterialColors.getColor(context, colorRes, Color.GRAY)
        tvStatus.setTextColor(color)
        tvStatus.text = context.getString(
            if (connected) R.string.status_connected else R.string.status_disconnected
        )
        ivIcon.setColorFilter(color)
        ivIcon.setImageResource(
            if (connected) R.drawable.ic_wifi_connected else R.drawable.ic_wifi_disconnected
        )
    }
}
