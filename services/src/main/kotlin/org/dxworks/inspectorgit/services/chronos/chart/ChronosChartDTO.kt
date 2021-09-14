package org.dxworks.inspectorgit.services.chronos.chart

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonUnwrapped

class ChronosChartDTO(
        var graphName: String,
        var data: List<ChartDataDTO>,
        var graphTypeOy1: String,
        var oxLabel: String,
        var oy1Label: String,
        var graphTypeOy2: String? = null,
        var oy2Label: String? = null,
        var target: Long? = null,
        var groups: List<ChartGroupDTO>? = null
)

class ChartDataDTO(
        val ox: String,
        val oy1: OyDTO,
        val oy2: OyDTO? = null
)

class OyDTO(
        val result: Number,
        private val groups: MutableMap<String, Number>? = null
) {
    @JsonAnyGetter
    @JsonUnwrapped
    fun getGroups(): Map<String, Number>? {
        return groups
    }
}

class ChartGroupDTO(
        var id: String,
        var label: String,
        var color: String? = null,
        var textColor: String? = null)

