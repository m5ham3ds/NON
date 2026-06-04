package com.example.widget

import android.content.Context
import android.graphics.Color
import android.text.Html
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import com.example.R
import com.example.domain.model.LogEntry
import com.example.domain.model.LogLevel
import com.example.util.DateUtils
import com.google.android.material.card.MaterialCardView
import com.google.android.material.color.MaterialColors

class LogTerminalView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.materialCardViewStyle
) : MaterialCardView(context, attrs, defStyleAttr) {

    private val textView: TextView
    private val scrollView: ScrollView
    private val maxLines = 200
    private val logLines = ArrayDeque<String>(maxLines)

    init {
        LayoutInflater.from(context).inflate(R.layout.view_log_terminal, this, true)
        textView = findViewById(R.id.tv_log_content)
        scrollView = findViewById(R.id.scroll_log)
        setCardBackgroundColor(
            MaterialColors.getColor(
                context,
                com.google.android.material.R.attr.colorSurface,
                Color.BLACK
            )
        )
    }

    fun appendLog(entry: LogEntry) {
        if (logLines.size >= maxLines) logLines.removeFirst()
        val color = when (entry.level) {
            LogLevel.SUCCESS -> "#4CAF50"
            LogLevel.ERROR -> "#F44336"
            LogLevel.WARNING -> "#FF9800"
            else -> "#FFFFFF"
        }
        val formattedTime = DateUtils.formatTime(entry.timestamp)
        logLines.addLast("<font color='$color'>[$formattedTime] ${entry.message}</font>")
        textView.text = Html.fromHtml(
            logLines.joinToString("<br>"),
            Html.FROM_HTML_MODE_COMPACT
        )
        scrollView.post { scrollView.fullScroll(View.FOCUS_DOWN) }
    }

    fun clearLogs() {
        logLines.clear()
        textView.text = ""
    }
}
