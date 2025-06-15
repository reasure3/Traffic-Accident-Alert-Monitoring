package com.swengineering.team1.traffic_accident.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.swengineering.team1.traffic_accident.R
import com.swengineering.team1.traffic_accident.config.ConfigRepository
import com.swengineering.team1.traffic_accident.models.NotificationConfig

class ConfigSettingsFragment : Fragment() {

    private lateinit var configRepository: ConfigRepository
    private lateinit var radiusEditText: EditText
    private lateinit var periodEditText: EditText
    private lateinit var countEditText: EditText
    private lateinit var cooldownEditText: EditText
    private lateinit var weatherSwitch: SwitchMaterial

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_config_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configRepository = ConfigRepository()
        
        radiusEditText = view.findViewById(R.id.radiusEditText)
        periodEditText = view.findViewById(R.id.periodEditText)
        countEditText = view.findViewById(R.id.countEditText)
        cooldownEditText = view.findViewById(R.id.cooldownEditText)
        weatherSwitch = view.findViewById(R.id.weatherSwitch)
        val saveButton = view.findViewById<Button>(R.id.saveButton)

        displayCurrentConfig()

        saveButton.setOnClickListener {
            saveNewConfig()
        }
    }

    private fun displayCurrentConfig() {
        val config = configRepository.getNotificationConfig()
        radiusEditText.setText(config.alertRadiusMeters.toString())
        periodEditText.setText(config.accidentPeriodDays.toString())
        countEditText.setText(config.minAccidentCount.toString())
        cooldownEditText.setText(config.notificationCooldownMinutes.toString())
        weatherSwitch.isChecked = config.weatherConditionEnabled
    }

    private fun saveNewConfig() {
        // Construye un objeto NotificationConfig con los nuevos valores
        val newConfig = NotificationConfig(
            alertRadiusMeters = radiusEditText.text.toString().toIntOrNull() ?: 0,
            accidentPeriodDays = periodEditText.text.toString().toIntOrNull() ?: 0,
            minAccidentCount = countEditText.text.toString().toIntOrNull() ?: 0,
            weatherConditionEnabled = weatherSwitch.isChecked,
            notificationCooldownMinutes = cooldownEditText.text.toString().toIntOrNull() ?: 0
        )

        // Convierte el objeto a un mapa para enviarlo a la función de Firebase
        val newValues = mapOf(
            "alert_radius_meters" to newConfig.alertRadiusMeters,
            "accident_period_days" to newConfig.accidentPeriodDays,
            "min_accident_count" to newConfig.minAccidentCount,
            "notification_cooldown_minutes" to newConfig.notificationCooldownMinutes,
            "weather_condition_enabled" to newConfig.weatherConditionEnabled
        )

        if (newValues.values.any { (it as? Int ?: 1) <= 0 } && newValues.size < 5) {
            Toast.makeText(requireContext(), "Please fill all fields correctly.", Toast.LENGTH_SHORT).show()
            return
        }

        val data = hashMapOf("newConfig" to newValues)

        Firebase.functions
            .getHttpsCallable("updateconfigandlog") // El nombre de la función debe ser en minúsculas
            .call(data)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Changes saved successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
} 