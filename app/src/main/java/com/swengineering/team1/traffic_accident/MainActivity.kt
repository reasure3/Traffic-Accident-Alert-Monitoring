package com.swengineering.team1.traffic_accident

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.swengineering.team1.traffic_accident.nav.NavBar
import com.swengineering.team1.traffic_accident.nav.NavigationGraph
import com.swengineering.team1.traffic_accident.ui.theme.TrafficAccidentAlertMonitoringTheme


/**
 * Main Activity 화면
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            TrafficAccidentAlertMonitoringTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding(),
                    // 밑에 네비게이션 바 부분
                    bottomBar = { NavBar(navController) }
                ) { innerPadding ->
                    // 실제 화면 부분
                    // [screen/***Screen.kt]를 보여줌
                    // 각각 맡은 파트에 대한 [screen/***Screen.kt]를 수정하면 됨
                    NavigationGraph(navController, innerPadding)
                }
            }
        }
    }
}