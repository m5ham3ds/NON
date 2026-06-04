package com.example.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.R
import com.example.data.local.entity.RouterProfileEntity
import com.example.util.DateUtils
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip

class RouterProfileAdapter(
    private val onSetDefault: (RouterProfileEntity) -> Unit,
    private val onEdit: (RouterProfileEntity) -> Unit,
    private val onDelete: (RouterProfileEntity) -> Unit
) : ListAdapter<RouterProfileEntity, RouterProfileAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_router_profile, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvName = view.findViewById<TextView>(R.id.tv_router_name)
        private val tvIp = view.findViewById<TextView>(R.id.tv_router_ip)
        private val tvProtocol = view.findViewById<TextView>(R.id.tv_router_protocol)
        private val tvAuth = view.findViewById<TextView>(R.id.tv_router_auth)
        private val tvDate = view.findViewById<TextView>(R.id.tv_router_date)
        private val chipDefault = view.findViewById<Chip>(R.id.chip_default)
        private val btnSetDefault = view.findViewById<MaterialButton>(R.id.btn_set_default)
        private val btnEdit = view.findViewById<MaterialButton>(R.id.btn_edit)
        private val btnDelete = view.findViewById<MaterialButton>(R.id.btn_delete)

        fun bind(item: RouterProfileEntity) {
            tvName.text = item.name
            tvIp.text = item.ip
            tvProtocol.text = item.protocol.uppercase()
            tvAuth.text = item.authType.name
            tvDate.text = DateUtils.formatDate(item.createdAt)

            chipDefault.visibility = if (item.isDefault) View.VISIBLE else View.GONE
            btnSetDefault.visibility = if (item.isDefault) View.GONE else View.VISIBLE

            btnSetDefault.setOnClickListener { onSetDefault(item) }
            btnEdit.setOnClickListener { onEdit(item) }
            btnDelete.setOnClickListener { onDelete(item) }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<RouterProfileEntity>() {
        override fun areItemsTheSame(oldItem: RouterProfileEntity, newItem: RouterProfileEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RouterProfileEntity, newItem: RouterProfileEntity): Boolean {
            return oldItem == newItem
        }
    }
}
