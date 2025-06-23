package com.swengineering.team1.traffic_accident.model.hotspot

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object MapFilterModel {

    // 내부 상태: 변경 가능
    private val _filterState = MutableStateFlow(MapFilter())

    // 외부에 노출할 StateFlow: 읽기 전용
    val filterState: StateFlow<MapFilter> = _filterState.asStateFlow()

    // 복수 선택 가능한 사고 심각도 토글
    fun toggleSeverity(level: Int) {
        val current = _filterState.value
        val updated = current.copy(
            severityList = current.severityList.toggle(level)
        )
        _filterState.value = updated
    }

    // 복수 선택 가능한 날씨 토글
    fun toggleWeather(weather: String) {
        val current = _filterState.value
        val updated = current.copy(
            weatherList = current.weatherList.toggle(weather)
        )
        _filterState.value = updated
    }

    // 초기화
    fun reset() {
        _filterState.value = MapFilter()
    }
}

// 3. 확장 함수: Set에서 토글 동작을 쉽게 하기 위해
private fun <T> Set<T>.toggle(item: T): Set<T> =
    if (contains(item)) this - item else this + item