package com.example.widget

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.appbar.MaterialToolbar

class CustomToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.toolbarStyle
) : MaterialToolbar(context, attrs, defStyleAttr) {

    fun setTitle(title: String, subtitle: String? = null) {
        this.title = title
        this.subtitle = subtitle
    }
}
