package com.example.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import com.example.R
import com.google.android.material.card.MaterialCardView
import com.google.android.material.color.MaterialColors

class StatisticCard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.materialCardViewStyle
) : MaterialCardView(context, attrs, defStyleAttr) {

    private val tvLabel: TextView
    private val tvValue: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.view_statistic_card, this, true)
        tvLabel = findViewById(R.id.tv_stat_label)
        tvValue = findViewById(R.id.tv_stat_value)
    }

    fun bind(label: String, value: String, valueColor: Int? = null) {
        tvLabel.text = label
        tvValue.text = value
        tvValue.setTextColor(
            valueColor ?: MaterialColors.getColor(
                context,
                com.google.android.material.R.attr.colorOnSurface,
                Color.BLACK
            )
        )
    }
}
