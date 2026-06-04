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
import com.example.data.local.entity.TestResultEntity
import com.example.util.DateUtils

class TestResultAdapter : ListAdapter<TestResultEntity, TestResultAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_test_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val ivIcon = view.findViewById<ImageView>(R.id.iv_result_icon)
        private val tvCode = view.findViewById<TextView>(R.id.tv_card_code)
        private val tvMessage = view.findViewById<TextView>(R.id.tv_result_message)
        private val tvTime = view.findViewById<TextView>(R.id.tv_result_time)
        private val tvDuration = view.findViewById<TextView>(R.id.tv_result_duration)

        fun bind(item: TestResultEntity) {
            tvCode.text = item.cardCode
            tvMessage.text = item.message
            tvTime.text = DateUtils.formatTime(item.testedAt)
            tvDuration.text = DateUtils.formatDuration(item.durationMs)

            val isSuccess = item.state == "Success"
            ivIcon.setImageResource(
                if (isSuccess) android.R.drawable.presence_online else android.R.drawable.presence_busy
            )
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<TestResultEntity>() {
        override fun areItemsTheSame(oldItem: TestResultEntity, newItem: TestResultEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TestResultEntity, newItem: TestResultEntity): Boolean {
            return oldItem == newItem
        }
    }
}
