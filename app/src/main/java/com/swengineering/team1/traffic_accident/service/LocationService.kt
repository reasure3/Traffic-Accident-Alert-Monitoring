package com.swengineering.team1.traffic_accident.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.swengineering.team1.traffic_accident.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LocationService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    // 알림 ID, 채널 ID
    companion object {
        private const val NOTIF_ID = 1

        private val _isRunning = MutableStateFlow(false)
        val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()
    }

    override fun onCreate() {
        super.onCreate()

        // 1) NotificationChannel 생성 (최초 한 번만)
        BackgroundLocationNotification.createNotificationChannel(this)

        // 2) FusedLocationProviderClient 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // 3) 위치 콜백 정의
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val location = locationResult.lastLocation ?: return

                // TODO: 위치를 받아 조건 검사 (예: 특정 영역 진입, 이동 거리 등)
                // 예시: 위도/경도가 특정 범위 내로 들어오면 알림 전송
                if (shouldSendNotification(location)) {
                    sendLocationNotification(location)
                }
            }
        }

        _isRunning.value = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 4) 포그라운드 서비스로 실행
        val notification = BackgroundLocationNotification.buildForegroundNotification(this)
        startForeground(NOTIF_ID, notification)

        // 5) 위치 업데이트 요청 시작
        startLocationUpdates()

        // 서비스가 강제 종료되어도 다시 재시작을 원하면 START_STICKY
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
        _isRunning.value = false
    }

    override fun onBind(intent: Intent?): IBinder? {
        // 바인딩이 필요 없으면 null 반환
        return null
    }

    // 위치 업데이트 요청
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10_000L)
            .setMinUpdateIntervalMillis(5_000L)
            .build()

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    // 알림 전송 로직
    private fun sendLocationNotification(location: Location) {
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // 고유 알림 ID (아래는 예시로 현재 시간 밀리초 사용)
        val notifyId = System.currentTimeMillis().toInt()

        val channelId = "location_alerts" // 알림 채널(ID) 별도 생성 가능
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "위치 기반 알림",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "특정 위치 도달 시 사용자에게 알림을 보냅니다."
            }
            nm.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_hotspot_filled) // 적절한 아이콘 지정
            .setContentTitle("위치 알림")
            .setContentText("현재 위치: (${location.latitude}, ${location.longitude})")
            .setAutoCancel(true)
            .build()

        nm.notify(notifyId, notification)
    }

    // TODO: 위치 기반 알림을 보낼지 결정하는 함수
    private fun shouldSendNotification(location: Location): Boolean {
        // 예시: 위도가 37.0~37.1 사이, 경도가 127.0~127.1 사이에 들어오면 true
        return (location.latitude in 37.0..37.1 && location.longitude in 127.0..127.1)
    }
}
