package com.swengineering.team1.traffic_accident.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
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
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.swengineering.team1.traffic_accident.MainActivity
import com.swengineering.team1.traffic_accident.R
import com.swengineering.team1.traffic_accident.config.ConfigRepository
import com.swengineering.team1.traffic_accident.model.AppLocalDatabase
import com.swengineering.team1.traffic_accident.model.notification.NotificationLogDao
import com.swengineering.team1.traffic_accident.model.notification.NotificationLogItem
import com.swengineering.team1.traffic_accident.model.remote_config.NotificationConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class LocationNotificationService : Service() {

    private lateinit var configRepository: ConfigRepository
    private lateinit var configs: NotificationConfig
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationManager: LocationManager
    private var fireDBCollection: CollectionReference? = null
    private var isQuerying = false

    private lateinit var localDao: NotificationLogDao

    private var lastQueryLoc: GeoLocation? = null
    private var shouldReQuery: Boolean = false

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

        val fireDB = FirebaseFirestore.getInstance("traffic-data")
        fireDBCollection = fireDB.collection("accident")

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

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        val localDB = AppLocalDatabase.getInstance(applicationContext)
        localDao = localDB.notificationLogDao()

        // 서비스가 강제 종료되어도 다시 재시작을 원하면 START_STICKY
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
        fireDBCollection = null
        _isRunning.value = false
    }

    override fun onBind(intent: Intent?): IBinder? {
        // 바인딩이 필요 없으면 null 반환
        return null
    }

    // 위치 업데이트 요청
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            configs.notificationMaxCooldownMills.toLong()
        )
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
    private fun sendLocationNotification(doc: DocumentSnapshot) {
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

        val location = doc.getGeoPoint("position.start_pos")
        val locationStr = "${doc.getString("info.street")}, ${doc.getString("info.city")}, " +
                "${doc.getString("info.state")}, ${doc.getString("info.country")}"
        val severity = doc.getDouble("severity")?.toInt()

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("근처 위험한 알림")
            .setContentText(locationStr)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(
                        "$locationStr\n\n" +
                                "심각도: $severity\n" +
                                "좌표: ${location?.latitude}, ${location?.longitude}"
                    )
            )
            .setAutoCancel(false)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()

        nm.notify(notifyId, notification)
    }

    private fun storeNotification(doc: DocumentSnapshot) {
        val log = NotificationLogItem(
            timestamp = Date(),
            location = doc.getGeoPoint("position.start_pos"),
            severity = doc.getDouble("severity")?.toInt() ?: 0,
            country = doc.getString("info.country") ?: "",
            state = doc.getString("info.state") ?: "",
            city = doc.getString("info.city") ?: "",
            street = doc.getString("info.street") ?: "",
        )

        Log.d("position", "start save log")
        CoroutineScope(Dispatchers.IO).launch {
            localDao.insert(log)
            Log.d("position", "finish save log")
        }
    }

    private fun checkLocationNotification(location: Location) {
        if (isQuerying) return

        if (fireDBCollection == null) {
            isQuerying = false
            return
        }

        val minDistForQuery = configs.queryDistanceIntervalMeters
        val currentLoc = GeoLocation(location.latitude, location.longitude)

        if (!shouldReQuery) { // 데이터 얻는데 실패할 경우 거리 상관 없이 다시 쿼리해야 함
            lastQueryLoc?.let {
                val dist = GeoFireUtils.getDistanceBetween(it, currentLoc)
                if (dist < minDistForQuery) {
                    isQuerying = false
                    return
                }
            }
        }

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.w("position", "gps is disabled")
            Toast.makeText(this, "Pleas turn on gps", Toast.LENGTH_SHORT).show()
            return
        }

        shouldReQuery = false
        isQuerying = true
        lastQueryLoc = currentLoc

        val radiusInM = configs.alertRadiusMeters.toDouble()
        val bounds = GeoFireUtils.getGeoHashQueryBounds(currentLoc, radiusInM)
        Log.d("position", "bounds size: ${bounds.size}")
        val tasks: MutableList<Task<QuerySnapshot>> = ArrayList()
        bounds.forEach {
            val q = fireDBCollection!!
                .orderBy("position.start_hash")
                .startAt(it.startHash)
                .endAt(it.endHash)
            tasks.add(q.get())
        }
        Log.d("position", "tasks size: ${tasks.size}")

        Tasks.whenAllComplete(tasks).addOnCompleteListener {
            Log.d("position", "complete to get pos")
            isQuerying = false
            val matchingDoc: MutableList<DocumentSnapshot> = ArrayList()
            var minDistDoc: DocumentSnapshot? = null
            var minDist: Double = radiusInM + 10
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
                        matchingDoc.add(doc)
                        if (distanceInM < minDist) {
                            minDistDoc = doc
                            minDist = distanceInM
                        }
                    }
                }
            }

            minDistDoc?.let { doc ->
                Log.d("position", "send notification")
                sendLocationNotification(doc)
                storeNotification(doc)
            }
        }.addOnFailureListener {
            Log.w("position", "fail to get pos")
            Toast.makeText(this, "fail to get data", Toast.LENGTH_SHORT).show()
            shouldReQuery = true
        }.addOnSuccessListener {
            Log.d("position", "success to get pos")
            shouldReQuery = false
        }
        Log.d("position", "end function")
    }
}
