package com.example.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.R
import com.example.domain.model.LogEntry
import com.example.domain.model.LogLevel
import com.example.util.DateUtils

class LogAdapter : ListAdapter<LogEntry, LogAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_log_entry, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvTime = view.findViewById<TextView>(R.id.tv_log_time)
        private val ivIcon = view.findViewById<ImageView>(R.id.iv_log_icon)
        private val tvMsg = view.findViewById<TextView>(R.id.tv_log_msg)

        fun bind(item: LogEntry) {
            tvTime.text = DateUtils.formatTime(item.timestamp)
            tvMsg.text = item.message

            val iconRes = when (item.level) {
                LogLevel.SUCCESS -> android.R.drawable.presence_online
                LogLevel.ERROR -> android.R.drawable.presence_busy
                LogLevel.WARNING -> android.R.drawable.presence_away
                LogLevel.INFO -> android.R.drawable.presence_invisible
            }
            ivIcon.setImageResource(iconRes)
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<LogEntry>() {
        override fun areItemsTheSame(oldItem: LogEntry, newItem: LogEntry): Boolean {
            return oldItem.timestamp == newItem.timestamp && oldItem.message == newItem.message
        }

        override fun areContentsTheSame(oldItem: LogEntry, newItem: LogEntry): Boolean {
            return oldItem == newItem
        }
    }
}
