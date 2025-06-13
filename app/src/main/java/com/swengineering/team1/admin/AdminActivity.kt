package com.swengineering.team1.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.swengineering.team1.R

class AdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ConfigSettingsFragment())
                .commit()
        }
    }
} 