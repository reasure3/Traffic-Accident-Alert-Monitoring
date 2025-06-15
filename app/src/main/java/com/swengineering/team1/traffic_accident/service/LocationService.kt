package com.swengineering.team1.traffic_accident.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.QuerySnapshot
import com.swengineering.team1.traffic_accident.MainActivity
import com.swengineering.team1.traffic_accident.R
import com.swengineering.team1.traffic_accident.config.ConfigRepository
import com.swengineering.team1.traffic_accident.models.NotificationConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LocationService : Service() {

    private lateinit var configRepository: ConfigRepository
    private lateinit var configs: NotificationConfig
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var dbCollection: CollectionReference? = null
    private var isQuerying = false

    private var lastQueryLoc: GeoLocation? = null

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

                // 예시: 위도/경도가 특정 범위 내로 들어오면 알림 전송
                checkLocationNotification(location)
//                sendLocationNotification(GeoPoint(location.latitude, location.longitude))
            }
        }

        val db = FirebaseFirestore.getInstance("traffic-data")
        dbCollection = db.collection("accident")
        _isRunning.value = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 4) 포그라운드 서비스로 실행
        val notification = BackgroundLocationNotification.buildForegroundNotification(this)
        startForeground(NOTIF_ID, notification)


        configRepository = ConfigRepository.INSTANCE
        configRepository.fetchRemoteConfig {
            configs = configRepository.getNotificationConfig()
            // 5) 위치 업데이트 요청 시작
            startLocationUpdates()
        }

        // 서비스가 강제 종료되어도 다시 재시작을 원하면 START_STICKY
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
        dbCollection = null
        _isRunning.value = false
    }

    override fun onBind(intent: Intent?): IBinder? {
        // 바인딩이 필요 없으면 null 반환
        return null
    }

    // 위치 업데이트 요청
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, configs.notificationMaxCooldownMills.toLong())
            .setMinUpdateIntervalMillis(configs.notificationMinCooldownMills.toLong())
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
    private fun sendLocationNotification(location: GeoPoint) {
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

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

        val activityIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            activityIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_hotspot_filled)
            .setContentTitle("위치 알림")
            .setContentText("위험한 위치: (${location.latitude}, ${location.longitude})")
            .setAutoCancel(false)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()

        nm.notify(notifyId, notification)
    }

    private fun checkLocationNotification(location: Location) {
        if (isQuerying) return

        if (dbCollection == null) {
            isQuerying = false
            return
        }

        val minDistForQuery = configs.queryDistanceIntervalMeters
        val currentLoc = GeoLocation(location.latitude, location.longitude)
        lastQueryLoc?.let {
            val dist = GeoFireUtils.getDistanceBetween(it, currentLoc)
            if (dist < minDistForQuery) {
                isQuerying = false
                return
            }
        }

        isQuerying = true
        lastQueryLoc = currentLoc

        val radiusInM = configs.alertRadiusMeters.toDouble()
        val bounds = GeoFireUtils.getGeoHashQueryBounds(currentLoc, radiusInM)
        Log.d("position", "bounds size: ${bounds.size}")
        val tasks: MutableList<Task<QuerySnapshot>> = ArrayList()
        bounds.forEach {
            val q = dbCollection!!
                .orderBy("position.start_hash")
                .startAt(it.startHash)
                .endAt(it.endHash)
            tasks.add(q.get())
        }
        Log.d("position", "tasks size: ${tasks.size}")

        Tasks.whenAllComplete(tasks).addOnCompleteListener {
            Log.d("position", "complete to get pos")
            isQuerying = false
            val matchingPos: MutableList<GeoPoint> = ArrayList()
            for (task in tasks) {
                val snap = task.result
                for (doc in snap.documents) {
                    Log.d("position", "document: ${doc.id} => ${doc.data}")
                    val minSeverity = configs.minSeverity
                    val severity = doc.getDouble("severity")?.toInt() ?: 4
                    val pos = doc.getGeoPoint("position.start_pos") ?: continue
                    val docLocation = GeoLocation(pos.latitude, pos.longitude)
                    val distanceInM = GeoFireUtils.getDistanceBetween(docLocation, currentLoc)
                    Log.d("position", "distance: ${distanceInM}m, position: $pos")
                    if (severity >= minSeverity && distanceInM <= radiusInM) {
                        matchingPos.add(pos)
                    }
                }
            }
            if (matchingPos.isNotEmpty()) {
                sendLocationNotification(matchingPos[0])
            }
        }.addOnFailureListener {
            Log.w("position", "fail to get pos")
        }.addOnSuccessListener {
            Log.d("position", "success to get pos")
        }
        Log.d("position", "end function")
    }
}
