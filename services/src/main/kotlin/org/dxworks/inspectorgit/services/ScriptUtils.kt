package org.dxworks.inspectorgit.services

import org.dxworks.inspectorgit.services.chronos.chart.BarChartDTO
import java.io.StringWriter

fun exportJson(obj: Any, filenameWithoutExtension: String) {}

fun exportCsv(list: List<Map<String, Any>>, filenameWithoutExtension: String) {}

fun exportBarChart(barChart: BarChartDTO, projectName: String, chronosBasePath: String? = null) {}

val system: LoadedSystem = LoadedSystem()

val log: StringWriter = StringWriter()

val messageClassifier: MessageClassifierService = MessageClassifierService()

fun mergeAccounts(name: String, vararg idsToMerge: String) {}

fun mergeDevelopers(name: String, vararg idsToMerge: String) {}
