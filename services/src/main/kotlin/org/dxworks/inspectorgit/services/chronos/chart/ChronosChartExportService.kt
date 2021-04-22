package org.dxworks.inspectorgit.services.chronos.chart

import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate

const val importEndpoint = "chronos/analysis/projects/imported/graphs/data/import"

fun exportChart(chartDTO: ChronosChartDTO, projectName: String, chronosBaseUrl: String = "http://localhost:8081"): Boolean {

    val requestEntity: HttpEntity<Any?> = HttpEntity(chartDTO)

    val restTemplate = RestTemplate()
    val response = restTemplate
            .exchange("$chronosBaseUrl/$importEndpoint/$projectName", HttpMethod.PUT, requestEntity, Map::class.java)
    return response.statusCode.is2xxSuccessful
}

fun convertBarChartToRequestBody(barChart: BarChartDTO): ChronosChartDTO {
    return ChronosChartDTO(
            graphName = barChart.name,
            data = convertData(barChart),
            graphTypeOy1 = BAR_CHART,
            graphTypeOy2 = getOy2ChartType(barChart),
            oxLabel = barChart.labels.ox,
            oy1Label = barChart.labels.oY1,
            oy2Label = barChart.labels.oY2,
            target = barChart.target,
            groups = barChart.groups
    )
}

fun getOy2ChartType(barChart: BarChartDTO): String? {
    return if (barChart.data.values.any { it is BarValue && it.oy2 != null }) BAR_CHART else null
}

fun convertData(barChart: BarChartDTO): List<ChartDataDTO> {
    return barChart.data.entries.map {
        ChartDataDTO(
                ox = it.key,
                oy1 = extractOy1(it.value),
                oy2 = extractOy2(it.value)
        )
    }
}

fun extractOy2(value: Any): OyDTO? {
    return when (value) {
        is Number -> OyDTO(result = value)
        is BarValue -> value.oy2
        else -> throw IllegalArgumentException(value.toString())
    }
}

fun extractOy1(value: Any): OyDTO {
    return when (value) {
        is Number -> OyDTO(result = value)
        is BarValue -> value.oy1
        else -> throw IllegalArgumentException(value.toString())
    }
}
