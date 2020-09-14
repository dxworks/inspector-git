package org.dxworks.inspectorgit.services

fun exportJson(obj: Any, filenameWithoutExtension: String) {}

fun exportCsv(list: List<Map<String, Any>>, filenameWithoutExtension: String) {}

val system: LoadedSystem = LoadedSystem()

fun mergeAccounts(name: String, vararg idsToMerge: String) {}

fun mergeDevelopers(name: String, vararg idsToMerge: String) {}
