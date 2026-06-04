package com.example.util

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object DialogHelper {

    enum class DialogType {
        SUCCESS, WARNING, ERROR, INFO
    }

    fun showCustomDialog(
        context: Context,
        title: CharSequence,
        message: CharSequence,
        dialogType: DialogType = DialogType.INFO,
        iconRes: Int? = null,
        positiveButtonText: CharSequence? = null,
        positiveAction: (() -> Unit)? = null,
        negativeButtonText: CharSequence? = null,
        negativeAction: (() -> Unit)? = null,
        neutralButtonText: CharSequence? = null,
        neutralAction: (() -> Unit)? = null,
        isCancelable: Boolean = true
    ): AlertDialog {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_custom_alert, null)
        val dialog = MaterialAlertDialogBuilder(context)
            .setView(view)
            .setCancelable(isCancelable)
            .create()

        // Set transparent window background so our rounded card works
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvTitle = view.findViewById<TextView>(R.id.dialog_title)
        val tvMessage = view.findViewById<TextView>(R.id.dialog_message)
        val imgIcon = view.findViewById<ImageView>(R.id.dialog_icon)
        val iconContainer = view.findViewById<FrameLayout>(R.id.dialog_icon_container)

        val btnPositive = view.findViewById<MaterialButton>(R.id.btn_dialog_positive)
        val btnNegative = view.findViewById<MaterialButton>(R.id.btn_dialog_negative)
        val btnNeutral = view.findViewById<MaterialButton>(R.id.btn_dialog_neutral)

        // Set Title & Message
        tvTitle.text = title
        tvMessage.text = message

        // Set custom or default icon depending on dialogType
        if (iconRes != null) {
            imgIcon.setImageResource(iconRes)
        } else {
            when (dialogType) {
                DialogType.SUCCESS -> imgIcon.setImageResource(R.drawable.ic_wifi_connected)
                DialogType.WARNING -> imgIcon.setImageResource(R.drawable.ic_info)
                DialogType.ERROR -> imgIcon.setImageResource(R.drawable.ic_wifi_disconnected)
                DialogType.INFO -> imgIcon.setImageResource(R.drawable.ic_launcher)
            }
        }

        // Set Circle Background for Icon
        val bgDrawable = when (dialogType) {
            DialogType.SUCCESS -> R.drawable.bg_circle_green
            DialogType.WARNING -> R.drawable.bg_circle_orange
            DialogType.ERROR -> R.drawable.bg_circle_red
            DialogType.INFO -> R.drawable.bg_circle_blue
        }
        iconContainer.setBackgroundResource(bgDrawable)

        // Set Positive Button
        if (positiveButtonText != null) {
            btnPositive.text = positiveButtonText
        } else {
            btnPositive.text = "موافق"
        }
        btnPositive.setOnClickListener {
            dialog.dismiss()
            positiveAction?.invoke()
        }

        // Set Negative Button
        if (negativeButtonText != null) {
            btnNegative.visibility = View.VISIBLE
            btnNegative.text = negativeButtonText
            btnNegative.setOnClickListener {
                dialog.dismiss()
                negativeAction?.invoke()
            }
        } else {
            btnNegative.visibility = View.GONE
        }

        // Set Neutral Button
        if (neutralButtonText != null) {
            btnNeutral.visibility = View.VISIBLE
            btnNeutral.text = neutralButtonText
            btnNeutral.setOnClickListener {
                dialog.dismiss()
                neutralAction?.invoke()
            }
        } else {
            btnNeutral.visibility = View.GONE
        }

        dialog.show()
        return dialog
    }
}
