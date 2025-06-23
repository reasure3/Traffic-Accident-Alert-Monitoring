package com.swengineering.team1.traffic_accident.admin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.swengineering.team1.traffic_accident.R
import com.swengineering.team1.traffic_accident.model.remote_config.ConfigChangeHistory
import java.text.SimpleDateFormat
import java.util.Locale

class ConfigHistoryAdapter(private val context: Context, private val history: List<ConfigChangeHistory>) :
    RecyclerView.Adapter<ConfigHistoryAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_config_history, parent, false)
        return HistoryViewHolder(context, view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = history[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = history.size

    class HistoryViewHolder(val context: Context, itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val timestampTextView: TextView = itemView.findViewById(R.id.timestampTextView)
        private val adminUserTextView: TextView = itemView.findViewById(R.id.adminUserTextView)
        private val changesTextView: TextView = itemView.findViewById(R.id.changesTextView)

        fun bind(historyItem: ConfigChangeHistory) {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            timestampTextView.text = sdf.format(historyItem.timestamp)
            adminUserTextView.text = context.getString(R.string.changed_by, historyItem.adminUser)
            
            val changesString = historyItem.changes?.map { (key, value) ->
                "$key -> $value"
            }?.joinToString("\n") ?: context.getString(R.string.no_changes)

            changesTextView.text = changesString
        }
    }
} 