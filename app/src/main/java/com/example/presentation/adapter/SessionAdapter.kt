package com.example.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.R
import com.example.data.local.entity.TestSessionEntity
import com.example.util.DateUtils
import com.google.android.material.chip.Chip

class SessionAdapter(
    private val onSessionClick: (TestSessionEntity) -> Unit
) : ListAdapter<TestSessionEntity, SessionAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_session, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvId = view.findViewById<TextView>(R.id.tv_session_id)
        private val tvDate = view.findViewById<TextView>(R.id.tv_session_date)
        private val tvRouter = view.findViewById<TextView>(R.id.tv_session_router)
        private val tvStats = view.findViewById<TextView>(R.id.tv_session_stats)
        private val chipRunning = view.findViewById<Chip>(R.id.chip_running)

        fun bind(item: TestSessionEntity) {
            tvId.text = "Session #${item.id}"
            tvDate.text = DateUtils.formatDateTime(item.startedAt)
            tvRouter.text = item.routerName
            tvStats.text = "Success: ${item.successCount} | Failure: ${item.failureCount}"

            chipRunning.visibility = if (item.isRunning) View.VISIBLE else View.GONE

            itemView.setOnClickListener { onSessionClick(item) }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<TestSessionEntity>() {
        override fun areItemsTheSame(oldItem: TestSessionEntity, newItem: TestSessionEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TestSessionEntity, newItem: TestSessionEntity): Boolean {
            return oldItem == newItem
        }
    }
}
