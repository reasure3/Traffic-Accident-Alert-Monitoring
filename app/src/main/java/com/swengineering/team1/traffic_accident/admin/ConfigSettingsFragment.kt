package com.swengineering.team1.traffic_accident.admin

import android.os.Bundle
import android.util.Log
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
import com.swengineering.team1.traffic_accident.config.ConfigConstants
import com.swengineering.team1.traffic_accident.config.ConfigRepository
import com.swengineering.team1.traffic_accident.models.NotificationConfig

class ConfigSettingsFragment : Fragment() {

    private lateinit var configRepository: ConfigRepository
    private lateinit var radiusEditText: EditText
    private lateinit var periodEditText: EditText
    private lateinit var countEditText: EditText
    private lateinit var minCooldownEditText: EditText
    private lateinit var maxCooldownEditText: EditText
    private lateinit var minSeverityEditText: EditText
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

        configRepository = ConfigRepository.INSTANCE

        radiusEditText = view.findViewById(R.id.radiusEditText)
        periodEditText = view.findViewById(R.id.periodEditText)
        countEditText = view.findViewById(R.id.countEditText)
        minCooldownEditText = view.findViewById(R.id.minCooldownEditText)
        maxCooldownEditText = view.findViewById(R.id.maxCooldownEditText)
        minSeverityEditText = view.findViewById(R.id.minSeverityEditText)
        weatherSwitch = view.findViewById(R.id.weatherSwitch)
        val saveButton = view.findViewById<Button>(R.id.saveButton)

        displayCurrentConfig()

        saveButton.setOnClickListener {
            saveNewConfig()
        }
    }

    private fun displayCurrentConfig() {
        configRepository.fetchRemoteConfig(
            onFail = {
                Toast.makeText(context, "Fail to get remote config", Toast.LENGTH_SHORT).show()
            }
        ) {
            val config = configRepository.getNotificationConfig()
            radiusEditText.setText(config.alertRadiusMeters.toString())
            periodEditText.setText(config.accidentPeriodDays.toString())
            countEditText.setText(config.minAccidentCount.toString())
            minCooldownEditText.setText(config.notificationMinCooldownMills.toString())
            maxCooldownEditText.setText(config.notificationMaxCooldownMills.toString())
            minSeverityEditText.setText(config.minSeverity.toString())
            weatherSwitch.isChecked = config.weatherConditionEnabled
        }

    }

    private fun saveNewConfig() {
        // Construye un objeto NotificationConfig con los nuevos valores
        val newConfig = NotificationConfig(
            alertRadiusMeters = radiusEditText.text.toString().toIntOrNull() ?: 0,
            accidentPeriodDays = periodEditText.text.toString().toIntOrNull() ?: 0,
            minAccidentCount = countEditText.text.toString().toIntOrNull() ?: 0,
            weatherConditionEnabled = weatherSwitch.isChecked,
            notificationMinCooldownMills = minCooldownEditText.text.toString().toIntOrNull() ?: 0,
            notificationMaxCooldownMills = maxCooldownEditText.text.toString().toIntOrNull() ?: 0,
            minSeverity = minSeverityEditText.text.toString().toIntOrNull() ?: 0
        )

        // Convierte el objeto a un mapa para enviarlo a la función de Firebase
        val newValues = mapOf(
            ConfigConstants.ALERT_RADIUS_METERS to newConfig.alertRadiusMeters,
            ConfigConstants.ACCIDENT_PERIOD_DAYS to newConfig.accidentPeriodDays,
            ConfigConstants.MIN_ACCIDENT_COUNT to newConfig.minAccidentCount,
            ConfigConstants.NOTIFICATION_MIN_COOLDOWN_MILLS to newConfig.notificationMinCooldownMills,
            ConfigConstants.NOTIFICATION_MAX_COOLDOWN_MILLS to newConfig.notificationMaxCooldownMills,
            ConfigConstants.MIN_SEVERITY to newConfig.minSeverity,
            ConfigConstants.WEATHER_CONDITION_ENABLED to newConfig.weatherConditionEnabled
        )

        if (newValues.values.any { (it as? Int ?: 1) <= 0 } && newValues.size < 5) {
            Toast.makeText(
                requireContext(),
                "Please fill all fields correctly.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val data = hashMapOf("newConfig" to newValues)

        Firebase.functions
            .getHttpsCallable("updateConfigAndLog") // El nombre de la función debe ser en minúsculas
            .call(data)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        "Changes saved successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Error: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.w("updateConfigAndLog", "Error UpdateConfigAndLog", task.exception)
                }
            }
    }
} 