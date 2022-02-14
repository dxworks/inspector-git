package org.dxworks.inspectorgit.services.chronos.chart

data class BarChartDTO(
        val name: String,
        val data: Map<String, BarValue>,
        val labels: ChartLabels,
        val target: Long? = null,
        var groups: List<ChartGroupDTO>? = null
)

data class ChartLabels(
        val ox: String = "Ox",
        val oY1: String = "Oy1",
        val oY2: String? = null
)

data class BarValue(
        val oy1: Any,
        val oy2: Any? = null,
        val highlight: Boolean? = null
)
