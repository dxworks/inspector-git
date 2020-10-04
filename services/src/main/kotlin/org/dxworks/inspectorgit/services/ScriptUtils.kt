package org.dxworks.inspectorgit.services

import java.io.StringWriter

fun exportJson(obj: Any, filenameWithoutExtension: String) {}

fun exportCsv(list: List<Map<String, Any>>, filenameWithoutExtension: String) {}

val system: LoadedSystem = LoadedSystem()

val log: StringWriter = StringWriter()

val messageClassifier: MessageClassifierService = MessageClassifierService()

fun mergeAccounts(name: String, vararg idsToMerge: String) {}

fun mergeDevelopers(name: String, vararg idsToMerge: String) {}
