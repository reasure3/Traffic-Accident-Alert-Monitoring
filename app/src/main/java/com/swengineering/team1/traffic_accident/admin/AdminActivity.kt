package com.swengineering.team1.traffic_accident.admin

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.swengineering.team1.traffic_accident.R

class AdminActivity : AppCompatActivity() {
    private lateinit var btnSettings: Button
    private lateinit var btnHistory: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        btnSettings = findViewById(R.id.btn_settings)
        btnHistory = findViewById(R.id.btn_history)

        if (savedInstanceState == null) {
            showSettingsFragment()
        }

        btnSettings.setOnClickListener {
            showSettingsFragment()
        }

        btnHistory.setOnClickListener {
            showListFragment()
        }
    }

    private fun showSettingsFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, ConfigSettingsFragment())
            .commit()
    }

    private fun showListFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, ConfigHistoryFragment())
            .commit()
    }
} 