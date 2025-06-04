package com.swengineering.team1.traffic_accident.model

object AccidentModel {

    val allAccidents: List<AccidentItem> = listOf(
        AccidentItem("1", 3, "맑음", "서울 강남", "37.4979", "127.0276"),
        AccidentItem("2", 1, "비", "서울 종로", "37.5714", "126.9910"),
        AccidentItem("3", 4, "맑음", "서울 서초", "37.4836", "127.0324"),
        AccidentItem("4", 2, "눈", "서울 용산", "37.5323", "126.9901"),
    )

    // 필터 조건에 따라 사고 데이터를 필터링하여 반환
    fun getFilteredAccidents(
        severityList: Set<Int>,
        weatherList: Set<String>
    ): List<AccidentItem> {
        return allAccidents.filter { accident ->
            (severityList.isEmpty() || accident.severity in severityList) &&
                    (weatherList.isEmpty() || accident.weather in weatherList)
        }
    }
}