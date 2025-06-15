package com.swengineering.team1.traffic_accident.admin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.swengineering.team1.traffic_accident.R
import com.swengineering.team1.traffic_accident.models.ConfigChangeHistory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

class ConfigHistoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ConfigHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_config_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.historyRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        
        fetchHistory()
    }

    private fun fetchHistory() {
        Firebase.functions
            .getHttpsCallable("gethistory") // El nombre de la función debe ser en minúsculas
            .call()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result?.data as? List<Map<String, Any>>
                    if (result != null) {
                        val historyList = parseHistory(result)
                        adapter = ConfigHistoryAdapter(historyList)
                        recyclerView.adapter = adapter
                    }
                } else {
                    Toast.makeText(requireContext(), "Error fetching history: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    Log.w("ConfigHistory", "Error fetching history", task.exception)
                }
            }
    }

    private fun parseHistory(data: List<Map<String, Any>>): List<ConfigChangeHistory> {
        val historyList = mutableListOf<ConfigChangeHistory>()
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        sdf.timeZone = TimeZone.getTimeZone("UTC")

        for (item in data) {
            try {
                val timestamp = sdf.parse(item["timestamp"] as String) ?: Date()
                val adminUser = item["adminUser"] as String
                
                val historyItem = ConfigChangeHistory(
                    timestamp,
                    adminUser,
                    item["changes"] as? Map<String, Any>
                )
                historyList.add(historyItem)
            } catch (e: Exception) {
                Log.e("ConfigHistory", "Error parsing history item", e)
            }
        }
        return historyList
    }
} 