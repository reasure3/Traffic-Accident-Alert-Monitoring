package com.swengineering.team1.traffic_accident.model.hotspot

import android.util.Log
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.common.primitives.Doubles.min
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.QuerySnapshot
import com.swengineering.team1.traffic_accident.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.cos
import kotlin.math.pow

object AccidentModel {

    private val _accidentState = MutableStateFlow<List<AccidentItem>>(emptyList())
    val accidentState: StateFlow<List<AccidentItem>> = _accidentState.asStateFlow()
    private var isQuerying = false

    fun zoomToRadiusMeters(zoom: Float): Double {
        // 대략적 추정 (화면의 절반 기준): 40075000 / 2^zoom / 2
        return 156543.03392 * cos(Math.toRadians(0.0)) / 2.0.pow(zoom.toDouble()) * 500.0
    }


    // Firestore에서 데이터를 가져와 상태를 갱신
    fun loadAccidents(loc: GeoLocation, zoom: Float) {
        if (isQuerying) return

        isQuerying = true

        val firestore = FirebaseFirestore.getInstance("traffic-data")
        val dbCollection = firestore.collection("accident")

        Log.d("AccidentModel", "Firestore 데이터 로드 시작")

        val radius = zoomToRadiusMeters(zoom)
        val bounds = GeoFireUtils.getGeoHashQueryBounds(loc, min(radius, 50.0 * 1000))
        val tasks: MutableList<Task<QuerySnapshot>> = ArrayList()
        bounds.forEach {
            val q = dbCollection
                .orderBy("position.start_hash")
                .startAt(it.startHash)
                .endAt(it.endHash)
            tasks.add(q.get())
        }

        Tasks.whenAllComplete(tasks).addOnCompleteListener {
            try {
                val loadedItem: MutableList<AccidentItem> = arrayListOf()
                for (task in tasks) {
                    val snap = task.result
                    val docs = snap.documents
                    Log.d("AccidentModel", "${docs.size}건의 문서 분석 중")
                    for (doc in docs) {
                        val positionMap = doc.get("position") as? Map<*, *> ?: continue
                        val startPos = positionMap["start_pos"] as? GeoPoint ?: continue
                        Log.d("AccidentModel", "문서 start_pos: $startPos")
                        val lat = startPos.latitude
                        val lng = startPos.longitude

                        val severity = doc.getLong("severity")?.toInt() ?: continue
                        Log.d("AccidentModel", "문서 severity: $severity")
                        val weatherMap = doc.get("weather") as? Map<*, *>
                        val weatherRaw =
                            weatherMap?.get("weather_condition")?.toString()?.lowercase()

                        Log.d("AccidentModel", "문서 weather: $weatherRaw")
                        val weather = when {
                            weatherRaw == null -> "" to R.string.filter_weather_error
                            "rain" in weatherRaw -> "rain" to R.string.filter_weather_rain
                            "snow" in weatherRaw -> "snow" to R.string.filter_weather_snow
                            "cloudy" in weatherRaw -> "cloudy" to R.string.filter_weather_cloudy
                            "fair" in weatherRaw -> "fair" to R.string.filter_weather_fair
                            else -> "" to R.string.filter_weather_etc
                        }

                        loadedItem.add(
                            AccidentItem(
                                id = doc.id,
                                severity = severity,
                                weather = weather.first,
                                weatherId = weather.second,
                                latitude = lat.toString(),
                                longitude = lng.toString()
                            )
                        )
                    }
                }
                isQuerying = false

                _accidentState.value = loadedItem
                Log.d("AccidentModel", "데이터 ${loadedItem.size}건 가져옴")
            } catch (e: Exception) {
                Log.e("AccidentModel", "Firestore 로드 실패: ${e.message}")
                _accidentState.value = emptyList()
            }
        }
    }

    // 필터 조건에 따라 사고 데이터를 필터링하여 반환
    fun getFilteredAccidents(
        allAccidents: List<AccidentItem>,
        severityList: Set<Int>,
        weatherList: Set<String>
    ): List<AccidentItem> {
        return allAccidents.filter { accident ->
            (severityList.isEmpty() || accident.severity in severityList) &&
                    (weatherList.isEmpty() || accident.weather in weatherList)
        }
    }

    fun testFirestoreAccess() {
        FirebaseFirestore.getInstance()
            .collection("accident")
            .get()
            .addOnSuccessListener { snapshot ->
                Log.d("AccidentModel", "가져온 문서 수: ${snapshot.size()}")
                snapshot.documents.forEach { doc ->
                    Log.d("AccidentModel", "문서 ID: ${doc.id}")
                }
            }
            .addOnFailureListener { e ->
                Log.e("AccidentModel", "오류 발생: ${e.message}")
            }
    }

    fun uploadTestAccidentData() {
        Log.d("AccidentModel", "uploadTestAccident 호출됨")
        val firestore = FirebaseFirestore.getInstance()

        val testData = mapOf(
            "severity" to 2,
            "position" to mapOf(
                "start_pos" to GeoPoint(37.5665, 126.9780)  // 서울시청 좌표 예시
            ),
            "weather" to mapOf(
                "weather_condition" to "Fair"
            ),
            "time" to System.currentTimeMillis(),
            "distance" to 0.0
        )

        firestore.collection("test")
            .add(testData)
            .addOnSuccessListener { documentReference ->
                Log.d("AccidentModel", "테스트 문서 추가 성공: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.e("AccidentModel", "테스트 문서 추가 실패: ${e.message}", e)
            }
    }
}